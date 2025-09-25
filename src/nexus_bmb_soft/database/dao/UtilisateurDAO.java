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
            // D'abord, créer la table de base si elle n'existe pas
            String createTableSQL = "CREATE TABLE IF NOT EXISTS utilisateur (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "nom VARCHAR(100) NOT NULL," +
                "role ENUM('CONDUCTEUR', 'CONDUCTEUR_SENIOR', 'GESTIONNAIRE', 'ADMIN', 'SUPER_ADMIN') NOT NULL," +
                "mot_de_passe_hash VARCHAR(255) NOT NULL" +
                ")";
            
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(createTableSQL);
            
            // Ensuite, essayer d'ajouter les nouvelles colonnes si elles n'existent pas
            try {
                stmt.executeUpdate("ALTER TABLE utilisateur ADD COLUMN prenom VARCHAR(100) DEFAULT ''");
                System.out.println("Colonne 'prenom' ajoutée à la table utilisateur.");
            } catch (SQLException e) {
                // La colonne existe déjà ou erreur, continuer
            }
            
            try {
                stmt.executeUpdate("ALTER TABLE utilisateur ADD COLUMN matricule VARCHAR(20) DEFAULT ''");
                System.out.println("Colonne 'matricule' ajoutée à la table utilisateur.");
            } catch (SQLException e) {
                // La colonne existe déjà ou erreur, continuer
            }
            
            try {
                stmt.executeUpdate("ALTER TABLE utilisateur ADD COLUMN email VARCHAR(255) DEFAULT ''");
                System.out.println("Colonne 'email' ajoutée à la table utilisateur.");
            } catch (SQLException e) {
                // La colonne existe déjà ou erreur, continuer
            }
            
            try {
                stmt.executeUpdate("ALTER TABLE utilisateur ADD COLUMN statut ENUM('ACTIF', 'INACTIF', 'SUSPENDU') DEFAULT 'ACTIF'");
                System.out.println("Colonne 'statut' ajoutée à la table utilisateur.");
            } catch (SQLException e) {
                // La colonne existe déjà ou erreur, continuer
            }
            
            try {
                stmt.executeUpdate("ALTER TABLE utilisateur ADD COLUMN date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
                System.out.println("Colonne 'date_creation' ajoutée à la table utilisateur.");
            } catch (SQLException e) {
                // La colonne existe déjà ou erreur, continuer
            }
            
            // Vérifier quelles colonnes existent réellement
            checkAvailableColumns();
            
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
    
    // Variables pour suivre les colonnes disponibles
    private boolean hasPrenom = false;
    private boolean hasMatricule = false;
    private boolean hasEmail = false;
    private boolean hasStatut = false;
    private boolean hasDateCreation = false;
    
    /**
     * Vérifie quelles colonnes sont disponibles dans la table
     */
    private void checkAvailableColumns() {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "utilisateur", null);
            
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME").toLowerCase();
                switch (columnName) {
                    case "prenom":
                        hasPrenom = true;
                        break;
                    case "matricule":
                        hasMatricule = true;
                        break;
                    case "email":
                        hasEmail = true;
                        break;
                    case "statut":
                        hasStatut = true;
                        break;
                    case "date_creation":
                        hasDateCreation = true;
                        break;
                }
            }
            
            System.out.println("Colonnes disponibles - prenom: " + hasPrenom + ", matricule: " + hasMatricule + 
                             ", email: " + hasEmail + ", statut: " + hasStatut + ", date_creation: " + hasDateCreation);
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification des colonnes: " + e.getMessage());
        }
    }
    
    /**
     * Insère des données de test
     */
    private void insertTestData() {
        try {
            // Construire la requête selon les colonnes disponibles
            StringBuilder insertSQL = new StringBuilder("INSERT INTO utilisateur (nom, role, mot_de_passe_hash");
            StringBuilder valuesSQL = new StringBuilder("VALUES ");
            
            if (hasPrenom) insertSQL.append(", prenom");
            if (hasMatricule) insertSQL.append(", matricule");
            if (hasEmail) insertSQL.append(", email");
            if (hasStatut) insertSQL.append(", statut");
            if (hasDateCreation) insertSQL.append(", date_creation");
            
            insertSQL.append(") ");
            
            // Données de base pour tous les utilisateurs : nom, role, mot_de_passe, prenom, matricule, email, statut
            String[][] userData = {
                {"MUBADI", "SUPER_ADMIN", "admin123", "Blaise", "ADMIN001", "blaise.mubadi@charroi.cd", "ACTIF"},
                {"KABAMBA", "GESTIONNAIRE", "gest123", "Jean", "GEST001", "jean.kabamba@charroi.cd", "ACTIF"},
                {"MUKENDI", "GESTIONNAIRE", "gest123", "Marie", "GEST002", "marie.mukendi@charroi.cd", "ACTIF"},
                {"TSHILOMBO", "CONDUCTEUR_SENIOR", "cond123", "Pierre", "COND001", "pierre.tshilombo@charroi.cd", "ACTIF"},
                {"KASONGO", "CONDUCTEUR", "cond123", "Paul", "COND002", "paul.kasongo@charroi.cd", "ACTIF"},
                {"MULAMBA", "CONDUCTEUR", "cond123", "Céline", "COND003", "celine.mulamba@charroi.cd", "ACTIF"},
                {"ILUNGA", "CONDUCTEUR", "cond123", "Joseph", "COND004", "joseph.ilunga@charroi.cd", "INACTIF"},
                {"KAPEND", "CONDUCTEUR_SENIOR", "cond123", "Sylvie", "COND005", "sylvie.kapend@charroi.cd", "ACTIF"},
                {"MWANANGA", "ADMIN", "admin123", "Daniel", "ADMIN002", "daniel.mwananga@charroi.cd", "ACTIF"},
                {"BAKAJIKA", "CONDUCTEUR", "cond123", "Françoise", "COND006", "francoise.bakajika@charroi.cd", "SUSPENDU"}
            };
            
            for (int i = 0; i < userData.length; i++) {
                if (i > 0) valuesSQL.append(", ");
                
                valuesSQL.append("('").append(userData[i][0]).append("', '")
                         .append(userData[i][1]).append("', '")
                         .append(userData[i][2]).append("'");
                
                if (hasPrenom) valuesSQL.append(", '").append(userData[i][3]).append("'");
                if (hasMatricule) valuesSQL.append(", '").append(userData[i][4]).append("'");
                if (hasEmail) valuesSQL.append(", '").append(userData[i][5]).append("'");
                if (hasStatut) valuesSQL.append(", '").append(userData[i][6]).append("'");
                if (hasDateCreation) valuesSQL.append(", NOW()");
                
                valuesSQL.append(")");
            }
            
            String finalSQL = insertSQL.toString() + valuesSQL.toString();
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(finalSQL);
            System.out.println("Données de test insérées dans la table utilisateur.");
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion des données de test: " + e.getMessage());
        }
    }
    
    /**
     * Crée un nouvel utilisateur
     */
    public boolean creer(Utilisateur utilisateur) {
        // Construire la requête selon les colonnes disponibles
        StringBuilder sql = new StringBuilder("INSERT INTO utilisateur (nom, role, mot_de_passe_hash");
        StringBuilder values = new StringBuilder("VALUES (?, ?, ?");
        
        if (hasPrenom) {
            sql.append(", prenom");
            values.append(", ?");
        }
        
        if (hasMatricule) {
            sql.append(", matricule");
            values.append(", ?");
        }
        
        if (hasEmail) {
            sql.append(", email");
            values.append(", ?");
        }
        
        if (hasStatut) {
            sql.append(", statut");
            values.append(", ?");
        }
        
        if (hasDateCreation) {
            sql.append(", date_creation");
            values.append(", ?");
        }
        
        sql.append(") ").append(values).append(")");
        
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, utilisateur.getNom());
            stmt.setString(2, utilisateur.getRole().name());
            stmt.setString(3, utilisateur.getMotDePasseHash());
            
            int currentIndex = 4;
            
            if (hasPrenom) {
                stmt.setString(currentIndex++, utilisateur.getPrenom());
            }
            
            if (hasMatricule) {
                stmt.setString(currentIndex++, utilisateur.getMatricule());
            }
            
            if (hasEmail) {
                stmt.setString(currentIndex++, utilisateur.getEmail());
            }
            
            if (hasStatut) {
                stmt.setString(currentIndex++, utilisateur.getStatut());
            }
            
            if (hasDateCreation) {
                stmt.setTimestamp(currentIndex++, utilisateur.getDateCreation() != null ? 
                    Timestamp.valueOf(utilisateur.getDateCreation()) : 
                    Timestamp.valueOf(LocalDateTime.now()));
            }
            
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
        
        // Construire la requête selon les colonnes disponibles
        StringBuilder sql = new StringBuilder("SELECT id, nom, role, mot_de_passe_hash");
        if (hasPrenom) sql.append(", prenom");
        if (hasMatricule) sql.append(", matricule");
        if (hasEmail) sql.append(", email");  
        if (hasStatut) sql.append(", statut");
        if (hasDateCreation) sql.append(", date_creation");
        
        sql.append(" FROM utilisateur ORDER BY nom");
        if (hasPrenom) sql.append(", prenom");
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql.toString())) {
            
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
        // Construire la requête selon les colonnes disponibles
        StringBuilder sql = new StringBuilder("UPDATE utilisateur SET nom = ?, role = ?, mot_de_passe_hash = ?");
        
        if (hasPrenom) {
            sql.append(", prenom = ?");
        }
        
        if (hasMatricule) {
            sql.append(", matricule = ?");
        }
        
        if (hasEmail) {
            sql.append(", email = ?");
        }
        
        if (hasStatut) {
            sql.append(", statut = ?");
        }
        
        sql.append(" WHERE id = ?");
        
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            stmt.setString(1, utilisateur.getNom());
            stmt.setString(2, utilisateur.getRole().name());
            stmt.setString(3, utilisateur.getMotDePasseHash());
            
            int currentIndex = 4;
            
            if (hasPrenom) {
                stmt.setString(currentIndex++, utilisateur.getPrenom());
            }
            
            if (hasMatricule) {
                stmt.setString(currentIndex++, utilisateur.getMatricule());
            }
            
            if (hasEmail) {
                stmt.setString(currentIndex++, utilisateur.getEmail());
            }
            
            if (hasStatut) {
                stmt.setString(currentIndex++, utilisateur.getStatut());
            }
            
            stmt.setInt(currentIndex, utilisateur.getId());
            
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
        utilisateur.setRole(RoleUtilisateur.valueOf(rs.getString("role")));
        utilisateur.setMotDePasseHash(rs.getString("mot_de_passe_hash"));
        
        // Colonnes optionnelles
        if (hasPrenom) {
            try {
                utilisateur.setPrenom(rs.getString("prenom"));
            } catch (SQLException e) {
                utilisateur.setPrenom(""); // Valeur par défaut
            }
        } else {
            // Extraire le prénom du nom si possible (MUBADI Blaise -> Blaise)
            String nom = utilisateur.getNom();
            if (nom != null && nom.contains(" ")) {
                String[] parties = nom.split(" ");
                if (parties.length >= 2) {
                    utilisateur.setPrenom(parties[parties.length - 1]); // Dernier mot
                    utilisateur.setNom(parties[0]); // Premier mot
                }
            } else {
                utilisateur.setPrenom("");
            }
        }
        
        if (hasMatricule) {
            try {
                utilisateur.setMatricule(rs.getString("matricule"));
            } catch (SQLException e) {
                utilisateur.setMatricule("");
            }
        } else {
            utilisateur.setMatricule("");
        }
        
        if (hasEmail) {
            try {
                utilisateur.setEmail(rs.getString("email"));
            } catch (SQLException e) {
                utilisateur.setEmail("");
            }
        } else {
            utilisateur.setEmail("");
        }
        
        if (hasStatut) {
            try {
                utilisateur.setStatut(rs.getString("statut"));
            } catch (SQLException e) {
                utilisateur.setStatut("ACTIF");
            }
        } else {
            utilisateur.setStatut("ACTIF");
        }
        
        if (hasDateCreation) {
            try {
                Timestamp dateCreation = rs.getTimestamp("date_creation");
                if (dateCreation != null) {
                    utilisateur.setDateCreation(dateCreation.toLocalDateTime());
                }
            } catch (SQLException e) {
                utilisateur.setDateCreation(LocalDateTime.now());
            }
        } else {
            utilisateur.setDateCreation(LocalDateTime.now());
        }
        
        return utilisateur;
    }
}