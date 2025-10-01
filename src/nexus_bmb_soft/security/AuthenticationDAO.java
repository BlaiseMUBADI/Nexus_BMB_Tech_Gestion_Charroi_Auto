package nexus_bmb_soft.security;

import nexus_bmb_soft.database.DatabaseConnection;
import nexus_bmb_soft.models.Utilisateur;
import nexus_bmb_soft.models.RoleUtilisateur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO pour la gestion des utilisateurs et de l'authentification
 * Version simplifiée compatible avec les classes existantes
 * 
 * @author BlaiseMUBADI
 */
public class AuthenticationDAO {
    
    private static final Logger LOGGER = Logger.getLogger(AuthenticationDAO.class.getName());
    // private static final int MAX_FAILED_ATTEMPTS = 5; // Désactivé pour version simplifiée
    
    /**
     * Authentifie un utilisateur avec username/password
     */
    public AuthResult authenticate(String username, String password) {
        String sql = """
            SELECT id, matricule, email, mot_de_passe_hash, 
                   prenom, nom, role, actif, statut
            FROM utilisateur 
            WHERE (matricule = ? OR email = ?) AND actif = 1
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, username);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    logAuthEvent(null, username, "LOGIN_FAILED", "User not found");
                    return new AuthResult(false, "Nom d'utilisateur ou mot de passe incorrect");
                }
                
                Utilisateur user = mapUserFromResultSet(rs);
                
                // Vérifier si le compte est actif
                if (!user.isValidForLogin()) {
                    logAuthEvent(user.getId(), username, "LOGIN_FAILED", "Account inactive");
                    return new AuthResult(false, "Compte inactif. Contactez l'administrateur.");
                }
                
                // Vérifier le mot de passe (support ancien et nouveau système)
                String storedHash = rs.getString("mot_de_passe_hash");
                boolean passwordValid = false;
                
                if (storedHash != null && storedHash.length() > 20) {
                    // Nouveau système sécurisé
                    passwordValid = PasswordSecurity.verifyPassword(password, storedHash);
                } else {
                    // Ancien système - comparaison simple (à migrer)
                    passwordValid = password.equals(storedHash);
                }
                
                if (!passwordValid) {
                    handleFailedLogin(user.getId());
                    logAuthEvent(user.getId(), username, "LOGIN_FAILED", "Invalid password");
                    return new AuthResult(false, "Nom d'utilisateur ou mot de passe incorrect");
                }
                
                // Connexion réussie
                updateSuccessfulLogin(user.getId());
                logAuthEvent(user.getId(), username, "LOGIN_SUCCESS", "Successful login");
                
                return new AuthResult(true, "Connexion réussie", user);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'authentification", e);
            return new AuthResult(false, "Erreur interne du système");
        }
    }
    
    /**
     * Crée une nouvelle session pour un utilisateur (version simplifiée)
     */
    public String createSession(int userId, String ipAddress, String userAgent) {
        // Session simplifiée - retourne juste un token simple sans base de données
        // L'authentification est déjà gérée par la connexion principale
        String sessionToken = "session-" + userId + "-" + System.currentTimeMillis();
        LOGGER.info("Session créée avec succès pour utilisateur ID: " + userId);
        return sessionToken;
    }
    
    /**
     * Valide une session existante
     */
    public Utilisateur validateSession(String sessionToken) {
        String sql = """
            SELECT id, matricule, email, prenom, nom, role, actif, statut
            FROM utilisateur 
            WHERE token_session = ? AND actif = 1
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, sessionToken);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Utilisateur user = mapUserFromResultSet(rs);
                    
                    if (user.isValidForLogin()) {
                        return user;
                    } else {
                        invalidateSession(sessionToken);
                    }
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la validation de session", e);
        }
        
        return null;
    }
    
    /**
     * Invalide une session (logout)
     */
    public boolean invalidateSession(String sessionToken) {
        String sql = "UPDATE user_session SET is_active = 0 WHERE session_token = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, sessionToken);
            int affected = ps.executeUpdate();
            
            if (affected > 0) {
                logAuthEvent(null, null, "LOGOUT", "User logout");
                return true;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'invalidation de session", e);
        }
        
        return false;
    }
    
    /**
     * Crée un nouvel utilisateur
     */
    public boolean createUser(Utilisateur user, String password) {
        if (!PasswordSecurity.isPasswordValid(password)) {
            return false;
        }
        
        String hashedPassword = PasswordSecurity.hashPassword(password);
        String sql = """
            INSERT INTO utilisateur (matricule, email, mot_de_passe_hash, prenom, nom, role,
                            actif, statut, date_creation)
            VALUES (?, ?, ?, ?, ?, ?, ?, 'ACTIF', NOW())
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, user.getMatricule());
            ps.setString(2, user.getEmail());
            ps.setString(3, hashedPassword);
            ps.setString(4, user.getPrenom());
            ps.setString(5, user.getNom());
            ps.setString(6, user.getRole().name());
            ps.setBoolean(7, user.isActif());
            
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        user.setId(keys.getInt(1));
                        return true;
                    }
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création d'utilisateur", e);
        }
        
        return false;
    }
    
    /**
     * Met à jour le mot de passe d'un utilisateur
     */
    public boolean changePassword(int userId, String newPassword) {
        if (!PasswordSecurity.isPasswordValid(newPassword)) {
            return false;
        }
        
        String hashedPassword = PasswordSecurity.hashPassword(newPassword);
        String sql = "UPDATE utilisateur SET mot_de_passe_hash = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, hashedPassword);
            ps.setInt(2, userId);
            
            int affected = ps.executeUpdate();
            if (affected > 0) {
                logAuthEvent(userId, null, "PASSWORD_CHANGE", "Password changed successfully");
                return true;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du changement de mot de passe", e);
        }
        
        return false;
    }
    
    /**
     * Récupère un utilisateur par ID
     */
    public Utilisateur getUserById(int userId) {
        String sql = """
            SELECT id, matricule, email, prenom, nom, role, actif, statut
            FROM utilisateur WHERE id = ?
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUserFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération d'utilisateur", e);
        }
        
        return null;
    }
    
    /**
     * Récupère tous les utilisateurs
     */
    public List<Utilisateur> getAllUsers() {
        List<Utilisateur> users = new ArrayList<>();
        String sql = """
            SELECT id, matricule, email, prenom, nom, role, actif, statut
            FROM utilisateur ORDER BY nom, prenom
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapUserFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des utilisateurs", e);
        }
        
        return users;
    }
    
    private void handleFailedLogin(int userId) {
        // Version simplifiée - juste log l'échec pour le moment
        LOGGER.log(Level.WARNING, "Tentative de connexion échouée pour utilisateur ID: " + userId);
        
        // Dans la version simplifiée, on ne gère pas les tentatives d'échec
        // Si nécessaire, on pourra ajouter cette fonctionnalité plus tard
        logAuthEvent(userId, null, "LOGIN_FAILED", "Failed login attempt");
    }
    
    // Méthodes de sécurité avancées supprimées dans la version simplifiée
    // Ces fonctionnalités pourront être réactivées quand la base de données
    // sera mise à jour avec les colonnes nécessaires (failed_login_attempts, is_locked, etc.)
    
    private void updateSuccessfulLogin(int userId) {
        // Version simplifiée compatible avec la base actuelle
        String sql = "UPDATE utilisateur SET updated_at = NOW() WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.executeUpdate();
            
            LOGGER.log(Level.INFO, "Connexion réussie mise à jour pour utilisateur ID: {0}", userId);
            
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Erreur lors de la mise à jour de connexion réussie", e);
        }
    }
    
    private void logAuthEvent(Integer userId, String username, String action, String details) {
        // Créer la table de log si elle n'existe pas
        try (Connection conn = DatabaseConnection.getConnection()) {
            String createTable = """
                CREATE TABLE IF NOT EXISTS auth_log (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT,
                    username VARCHAR(100),
                    action VARCHAR(50) NOT NULL,
                    ip_address VARCHAR(45),
                    details TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;
            
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTable);
            }
            
            String sql = """
                INSERT INTO auth_log (user_id, username, action, ip_address, details)
                VALUES (?, ?, ?, 'localhost', ?)
            """;
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setObject(1, userId);
                ps.setString(2, username);
                ps.setString(3, action);
                ps.setString(4, details);
                ps.executeUpdate();
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Erreur lors de l'enregistrement de log d'authentification", e);
        }
    }
    

    
    private Utilisateur mapUserFromResultSet(ResultSet rs) throws SQLException {
        Utilisateur user = new Utilisateur();
        user.setId(rs.getInt("id"));
        user.setMatricule(rs.getString("matricule"));
        user.setEmail(rs.getString("email"));
        user.setPrenom(rs.getString("prenom"));
        user.setNom(rs.getString("nom"));
        user.setRole(RoleUtilisateur.fromString(rs.getString("role")));
        
        // Gestion sécurisée des colonnes tinyint(1) pour compatibilité MySQL
        user.setActif(rs.getInt("actif") == 1);
        user.setStatut(rs.getString("statut"));
        
        // Initialiser les nouvelles propriétés avec des valeurs par défaut
        user.setActive(rs.getInt("actif") == 1); // Même valeur que actif
        user.setLocked(false); // Par défaut non verrouillé
        user.setFailedLoginAttempts(0); // Par défaut aucune tentative échouée
        
        // Note: Les champs phone et department seront ajoutés lors de la migration
        // Pour l'instant, on utilise la structure existante
        
        return user;
    }
    
    /**
     * Classe pour encapsuler le résultat d'authentification
     */
    public static class AuthResult {
        private final boolean success;
        private final String message;
        private final Utilisateur user;
        
        public AuthResult(boolean success, String message) {
            this(success, message, null);
        }
        
        public AuthResult(boolean success, String message, Utilisateur user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Utilisateur getUser() { return user; }
    }
}