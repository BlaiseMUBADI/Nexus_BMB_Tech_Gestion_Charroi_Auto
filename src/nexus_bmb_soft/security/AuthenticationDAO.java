package nexus_bmb_soft.security;

import nexus_bmb_soft.database.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO pour la gestion des utilisateurs et de l'authentification
 * Gère les opérations CRUD sur les utilisateurs et la sécurité
 * 
 * @author BlaiseMUBADI
 */
public class AuthenticationDAO {
    
    private static final Logger LOGGER = Logger.getLogger(AuthenticationDAO.class.getName());
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int SESSION_DURATION_HOURS = 8;
    
    /**
     * Authentifie un utilisateur avec username/password
     */
    public AuthResult authenticate(String username, String password) {
        String sql = """
            SELECT id, username, email, password_hash, first_name, last_name, role,
                   is_active, is_locked, failed_login_attempts, last_login, must_change_password,
                   department, employee_id, phone
            FROM user 
            WHERE username = ? OR email = ?
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
                
                User user = mapUserFromResultSet(rs);
                
                // Vérifier si le compte est verrouillé
                if (user.isLocked()) {
                    logAuthEvent(user.getId(), username, "LOGIN_FAILED", "Account locked");
                    return new AuthResult(false, "Compte verrouillé. Contactez l'administrateur.");
                }
                
                // Vérifier si le compte est actif
                if (!user.canLogin()) {
                    logAuthEvent(user.getId(), username, "LOGIN_FAILED", "Account inactive");
                    return new AuthResult(false, "Compte inactif. Contactez l'administrateur.");
                }
                
                // Vérifier le mot de passe
                if (!PasswordSecurity.verifyPassword(password, user.getPasswordHash())) {
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
     * Crée une nouvelle session pour un utilisateur
     */
    public UserSession createSession(int userId, String ipAddress, String userAgent) {
        String sessionToken = generateSessionToken();
        String sql = """
            INSERT INTO user_session (user_id, session_token, ip_address, user_agent, 
                                    is_active, expires_at, last_activity, created_at)
            VALUES (?, ?, ?, ?, 1, ?, NOW(), NOW())
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            LocalDateTime expiresAt = LocalDateTime.now().plusHours(SESSION_DURATION_HOURS);
            
            ps.setInt(1, userId);
            ps.setString(2, sessionToken);
            ps.setString(3, ipAddress);
            ps.setString(4, userAgent);
            ps.setTimestamp(5, Timestamp.valueOf(expiresAt));
            
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        UserSession session = new UserSession(userId, sessionToken, ipAddress, userAgent);
                        session.setId(keys.getInt(1));
                        session.setExpiresAt(expiresAt);
                        return session;
                    }
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création de session", e);
        }
        
        return null;
    }
    
    /**
     * Valide une session existante
     */
    public UserSession validateSession(String sessionToken) {
        String sql = """
            SELECT s.id, s.user_id, s.session_token, s.ip_address, s.user_agent,
                   s.is_active, s.expires_at, s.last_activity, s.created_at,
                   u.username, u.email, u.first_name, u.last_name, u.role,
                   u.is_active as user_active, u.is_locked, u.department, u.employee_id
            FROM user_session s
            JOIN user u ON s.user_id = u.id
            WHERE s.session_token = ? AND s.is_active = 1 AND s.expires_at > NOW()
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, sessionToken);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserSession session = mapSessionFromResultSet(rs);
                    User user = mapUserFromResultSet(rs);
                    session.setUser(user);
                    
                    // Vérifier que l'utilisateur peut toujours se connecter
                    if (user.canLogin()) {
                        // Mettre à jour l'activité de la session
                        updateSessionActivity(session.getId());
                        return session;
                    } else {
                        // Invalider la session si l'utilisateur n'est plus autorisé
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
                // Logger la déconnexion
                UserSession session = getSessionByToken(sessionToken);
                if (session != null) {
                    logAuthEvent(session.getUserId(), null, "LOGOUT", "User logout");
                }
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
    public boolean createUser(User user, String password) {
        if (!PasswordSecurity.isPasswordValid(password)) {
            return false;
        }
        
        String hashedPassword = PasswordSecurity.hashPassword(password);
        String sql = """
            INSERT INTO user (username, email, password_hash, first_name, last_name, role,
                            is_active, phone, department, employee_id, notes, created_by, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, hashedPassword);
            ps.setString(4, user.getFirstName());
            ps.setString(5, user.getLastName());
            ps.setString(6, user.getRole().name());
            ps.setBoolean(7, user.isActive());
            ps.setString(8, user.getPhone());
            ps.setString(9, user.getDepartment());
            ps.setString(10, user.getEmployeeId());
            ps.setString(11, user.getNotes());
            ps.setObject(12, user.getCreatedBy());
            
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
        String sql = """
            UPDATE user 
            SET password_hash = ?, must_change_password = 0, updated_at = NOW()
            WHERE id = ?
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, hashedPassword);
            ps.setInt(2, userId);
            
            int affected = ps.executeUpdate();
            if (affected > 0) {
                logAuthEvent(userId, null, "PASSWORD_CHANGED", "Password changed by user");
                return true;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du changement de mot de passe", e);
        }
        
        return false;
    }
    
    /**
     * Récupère un utilisateur par son ID
     */
    public User getUserById(int userId) {
        String sql = """
            SELECT id, username, email, first_name, last_name, role, is_active, is_locked,
                   failed_login_attempts, last_login, phone, department, employee_id, notes,
                   created_at, updated_at
            FROM user WHERE id = ?
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
     * Liste tous les utilisateurs actifs
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = """
            SELECT id, username, email, first_name, last_name, role, is_active, is_locked,
                   failed_login_attempts, last_login, phone, department, employee_id, notes,
                   created_at, updated_at
            FROM user 
            ORDER BY first_name, last_name
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
    
    // === MÉTHODES UTILITAIRES PRIVÉES ===
    
    private void handleFailedLogin(int userId) {
        String sql = """
            UPDATE user 
            SET failed_login_attempts = failed_login_attempts + 1,
                last_failed_login = NOW(),
                is_locked = CASE WHEN failed_login_attempts + 1 >= ? THEN 1 ELSE is_locked END
            WHERE id = ?
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, MAX_FAILED_ATTEMPTS);
            ps.setInt(2, userId);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la gestion d'échec de connexion", e);
        }
    }
    
    private void updateSuccessfulLogin(int userId) {
        String sql = """
            UPDATE user 
            SET failed_login_attempts = 0, last_login = NOW(), updated_at = NOW()
            WHERE id = ?
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour de connexion réussie", e);
        }
    }
    
    private void updateSessionActivity(int sessionId) {
        String sql = "UPDATE user_session SET last_activity = NOW() WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, sessionId);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Erreur lors de la mise à jour d'activité de session", e);
        }
    }
    
    private UserSession getSessionByToken(String sessionToken) {
        String sql = "SELECT id, user_id FROM user_session WHERE session_token = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, sessionToken);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserSession session = new UserSession();
                    session.setId(rs.getInt("id"));
                    session.setUserId(rs.getInt("user_id"));
                    session.setSessionToken(sessionToken);
                    return session;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Erreur lors de la récupération de session", e);
        }
        
        return null;
    }
    
    private void logAuthEvent(Integer userId, String username, String action, String details) {
        String sql = """
            INSERT INTO auth_log (user_id, username, action, ip_address, details, created_at)
            VALUES (?, ?, ?, ?, ?, NOW())
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setObject(1, userId);
            ps.setString(2, username);
            ps.setString(3, action);
            ps.setString(4, "localhost"); // TODO: Récupérer l'IP réelle
            ps.setString(5, details);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Erreur lors de l'enregistrement de log d'authentification", e);
        }
    }
    
    private String generateSessionToken() {
        return UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
    }
    
    private User mapUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setActive(rs.getBoolean("is_active"));
        user.setLocked(rs.getBoolean("is_locked"));
        user.setFailedLoginAttempts(rs.getInt("failed_login_attempts"));
        
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin.toLocalDateTime());
        }
        
        user.setPhone(rs.getString("phone"));
        user.setDepartment(rs.getString("department"));
        user.setEmployeeId(rs.getString("employee_id"));
        user.setNotes(rs.getString("notes"));
        
        // Autres champs si présents
        try {
            user.setPasswordHash(rs.getString("password_hash"));
            user.setMustChangePassword(rs.getBoolean("must_change_password"));
        } catch (SQLException ignored) {
            // Ces champs peuvent ne pas être présents dans tous les SELECT
        }
        
        return user;
    }
    
    private UserSession mapSessionFromResultSet(ResultSet rs) throws SQLException {
        UserSession session = new UserSession();
        session.setId(rs.getInt("id"));
        session.setUserId(rs.getInt("user_id"));
        session.setSessionToken(rs.getString("session_token"));
        session.setIpAddress(rs.getString("ip_address"));
        session.setUserAgent(rs.getString("user_agent"));
        session.setActive(rs.getBoolean("is_active"));
        
        Timestamp expiresAt = rs.getTimestamp("expires_at");
        if (expiresAt != null) {
            session.setExpiresAt(expiresAt.toLocalDateTime());
        }
        
        Timestamp lastActivity = rs.getTimestamp("last_activity");
        if (lastActivity != null) {
            session.setLastActivity(lastActivity.toLocalDateTime());
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            session.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return session;
    }
    
    /**
     * Classe pour encapsuler le résultat d'authentification
     */
    public static class AuthResult {
        private final boolean success;
        private final String message;
        private final User user;
        
        public AuthResult(boolean success, String message) {
            this(success, message, null);
        }
        
        public AuthResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public User getUser() { return user; }
    }
}