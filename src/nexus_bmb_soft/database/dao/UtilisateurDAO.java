package nexus_bmb_soft.database.dao;

import nexus_bmb_soft.database.DatabaseConnection;
import nexus_bmb_soft.models.Utilisateur;
import nexus_bmb_soft.models.RoleUtilisateur;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des utilisateurs
 * 
 * @author BlaiseMUBADI
 */
public class UtilisateurDAO {
    
    private Connection connection;
    
    public UtilisateurDAO() {
        this.connection = DatabaseConnection.getConnection();
        initializeTable();
    }
    
    /**
     * Initialise la table des utilisateurs si elle n'existe pas
     */
    private void initializeTable() {
        try {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS utilisateur (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "nom VARCHAR(100) NOT NULL," +
                "prenom VARCHAR(100) NOT NULL," +
                "email VARCHAR(255) UNIQUE NOT NULL," +
                "role ENUM('CONDUCTEUR', 'CONDUCTEUR_SENIOR', 'GESTIONNAIRE', 'ADMIN', 'SUPER_ADMIN') NOT NULL," +
                "statut ENUM('ACTIF', 'INACTIF', 'SUSPENDU') DEFAULT 'ACTIF'," +
                "mot_de_passe_hash VARCHAR(255) NOT NULL," +
                "date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                ")";
            
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(createTableSQL);
            
            // Insérer des données de test si la table est vide
            String countSQL = "SELECT COUNT(*) FROM utilisateur";
            ResultSet rs = stmt.executeQuery(countSQL);
            rs.next();
            
            if (rs.getInt(1) == 0) {
                insertTestData();
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation de la table utilisateur: " + e.getMessage());
        }
    }
    
    /**
     * Insère des données de test
     */
    private void insertTestData() {
        try {
            String insertSQL = "INSERT INTO utilisateur (nom, prenom, email, role, statut, mot_de_passe_hash) VALUES " +
                "('MUBADI', 'Blaise', 'blaise.mubadi@charroi.cd', 'SUPER_ADMIN', 'ACTIF', 'admin123'), " +
                "('KABAMBA', 'Jean', 'jean.kabamba@charroi.cd', 'GESTIONNAIRE', 'ACTIF', 'gest123'), " +
                "('MUKENDI', 'Marie', 'marie.mukendi@charroi.cd', 'GESTIONNAIRE', 'ACTIF', 'gest123'), " +
                "('TSHILOMBO', 'Pierre', 'pierre.tshilombo@charroi.cd', 'CONDUCTEUR_SENIOR', 'ACTIF', 'cond123'), " +
                "('KASONGO', 'Paul', 'paul.kasongo@charroi.cd', 'CONDUCTEUR', 'ACTIF', 'cond123'), " +
                "('MULAMBA', 'Céline', 'celine.mulamba@charroi.cd', 'CONDUCTEUR', 'ACTIF', 'cond123'), " +
                "('ILUNGA', 'Joseph', 'joseph.ilunga@charroi.cd', 'CONDUCTEUR', 'INACTIF', 'cond123'), " +
                "('KAPEND', 'Sylvie', 'sylvie.kapend@charroi.cd', 'CONDUCTEUR_SENIOR', 'ACTIF', 'cond123'), " +
                "('MWANANGA', 'Daniel', 'daniel.mwananga@charroi.cd', 'ADMIN', 'ACTIF', 'admin123'), " +
                "('BAKAJIKA', 'Françoise', 'francoise.bakajika@charroi.cd', 'CONDUCTEUR', 'SUSPENDU', 'cond123')";
            
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(insertSQL);
            System.out.println("Données de test insérées dans la table utilisateur.");
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion des données de test: " + e.getMessage());
        }
    }
    
    /**
     * Crée un nouvel utilisateur
     */
    public boolean creer(Utilisateur utilisateur) {
        String sql = "INSERT INTO utilisateur (nom, prenom, email, role, statut, mot_de_passe_hash, date_creation) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, utilisateur.getNom());
            stmt.setString(2, utilisateur.getPrenom());
            stmt.setString(3, utilisateur.getEmail());
            stmt.setString(4, utilisateur.getRole().name());
            stmt.setString(5, utilisateur.getStatut());
            stmt.setString(6, utilisateur.getMotDePasseHash());
            stmt.setTimestamp(7, utilisateur.getDateCreation() != null ? 
                Timestamp.valueOf(utilisateur.getDateCreation()) : 
                Timestamp.valueOf(LocalDateTime.now()));
            
            int rows = stmt.executeUpdate();
            
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    utilisateur.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de l'utilisateur: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Lit un utilisateur par son ID
     */
    public Utilisateur lire(int id) {
        String sql = "SELECT * FROM utilisateur WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUtilisateur(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la lecture de l'utilisateur: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Lit tous les utilisateurs
     */
    public List<Utilisateur> lireTous() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur ORDER BY nom, prenom";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                utilisateurs.add(mapResultSetToUtilisateur(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la lecture de tous les utilisateurs: " + e.getMessage());
        }
        
        return utilisateurs;
    }
    
    /**
     * Met à jour un utilisateur
     */
    public boolean mettreAJour(Utilisateur utilisateur) {
        String sql = "UPDATE utilisateur SET nom = ?, prenom = ?, email = ?, role = ?, statut = ?, mot_de_passe_hash = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, utilisateur.getNom());
            stmt.setString(2, utilisateur.getPrenom());
            stmt.setString(3, utilisateur.getEmail());
            stmt.setString(4, utilisateur.getRole().name());
            stmt.setString(5, utilisateur.getStatut());
            stmt.setString(6, utilisateur.getMotDePasseHash());
            stmt.setInt(7, utilisateur.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Supprime un utilisateur
     */
    public boolean supprimer(int id) {
        String sql = "DELETE FROM utilisateur WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Recherche des utilisateurs par critères
     */
    public List<Utilisateur> rechercherParCriteres(String nomPrenom, String role, String statut) {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM utilisateur WHERE 1=1");
        
        if (nomPrenom != null && !nomPrenom.trim().isEmpty()) {
            sql.append(" AND (nom LIKE ? OR prenom LIKE ?)");
        }
        
        if (role != null && !role.equals("Tous")) {
            sql.append(" AND role = ?");
        }
        
        if (statut != null && !statut.equals("Tous")) {
            sql.append(" AND statut = ?");
        }
        
        sql.append(" ORDER BY nom, prenom");
        
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            
            if (nomPrenom != null && !nomPrenom.trim().isEmpty()) {
                String searchPattern = "%" + nomPrenom.trim() + "%";
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
            }
            
            if (role != null && !role.equals("Tous")) {
                stmt.setString(paramIndex++, role);
            }
            
            if (statut != null && !statut.equals("Tous")) {
                stmt.setString(paramIndex++, statut);
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                utilisateurs.add(mapResultSetToUtilisateur(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche d'utilisateurs: " + e.getMessage());
        }
        
        return utilisateurs;
    }
    
    /**
     * Trouve un utilisateur par email
     */
    public Utilisateur trouverParEmail(String email) {
        String sql = "SELECT * FROM utilisateur WHERE email = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUtilisateur(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par email: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Compte les utilisateurs par rôle
     */
    public int compterParRole(String role) {
        String sql = "SELECT COUNT(*) FROM utilisateur WHERE role = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, role);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage par rôle: " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * Compte les utilisateurs par statut
     */
    public int compterParStatut(String statut) {
        String sql = "SELECT COUNT(*) FROM utilisateur WHERE statut = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, statut);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage par statut: " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * Mappe un ResultSet vers un objet Utilisateur
     */
    private Utilisateur mapResultSetToUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(rs.getInt("id"));
        utilisateur.setNom(rs.getString("nom"));
        utilisateur.setPrenom(rs.getString("prenom"));
        utilisateur.setEmail(rs.getString("email"));
        utilisateur.setRole(RoleUtilisateur.valueOf(rs.getString("role")));
        utilisateur.setStatut(rs.getString("statut"));
        utilisateur.setMotDePasseHash(rs.getString("mot_de_passe_hash"));
        
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            utilisateur.setDateCreation(dateCreation.toLocalDateTime());
        }
        
        return utilisateur;
    }
}