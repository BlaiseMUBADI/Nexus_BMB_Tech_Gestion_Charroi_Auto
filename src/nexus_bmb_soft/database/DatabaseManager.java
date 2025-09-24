package nexus_bmb_soft.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestionnaire pour l'initialisation et la maintenance de la base de données
 * 
 * @author BlaiseMUBADI
 */
public class DatabaseManager {
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());
    
    /**
     * Initialise toute la base de données (tables + données de test)
     */
    public static boolean initializeDatabase() {
        LOGGER.info("🚀 Initialisation de la base de données...");
        
        try {
            // 1. Créer la base si elle n'existe pas
            if (!DatabaseConnection.createDatabaseIfNotExists()) {
                return false;
            }
            
            // 2. Créer les tables
            if (!createTables()) {
                return false;
            }
            
            // 3. Insérer les données de test
            if (!insertSampleData()) {
                return false;
            }
            
            LOGGER.info("✅ Base de données initialisée avec succès !");
            return true;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de l'initialisation", e);
            return false;
        }
    }
    
    /**
     * Crée toutes les tables nécessaires
     */
    private static boolean createTables() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            LOGGER.info("📋 Création des tables...");
            
            // Supprimer les tables existantes pour éviter les conflits de structure
            // ATTENTION: Ordre inverse pour respecter les contraintes de clés étrangères
            LOGGER.info("🧹 Nettoyage des tables existantes...");
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0"); // Désactiver temporairement les vérifications
            stmt.executeUpdate("DROP TABLE IF EXISTS entretien");
            stmt.executeUpdate("DROP TABLE IF EXISTS affectation");
            stmt.executeUpdate("DROP TABLE IF EXISTS utilisateur");
            stmt.executeUpdate("DROP TABLE IF EXISTS vehicule");
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1"); // Réactiver les vérifications
            
            // Table des véhicules (table de base, pas de FK)
            String createVehicule = "CREATE TABLE vehicule (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "matricule VARCHAR(20) UNIQUE NOT NULL," +
                    "marque VARCHAR(50) NOT NULL," +
                    "type VARCHAR(50)," +
                    "annee INT," +
                    "disponible BOOLEAN DEFAULT TRUE," +
                    "date_assurance DATE," +
                    "date_vidange DATE," +
                    "date_visite_technique DATE," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "INDEX idx_matricule (matricule)," +
                    "INDEX idx_disponible (disponible)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
            stmt.executeUpdate(createVehicule);
            LOGGER.info("✅ Table 'vehicule' créée");
            
            // Table des utilisateurs (table de base, pas de FK)
            String createUtilisateur = "CREATE TABLE utilisateur (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "nom VARCHAR(100) NOT NULL," +
                    "role ENUM('admin', 'gestionnaire', 'conducteur') NOT NULL," +
                    "mot_de_passe_hash VARCHAR(255) NOT NULL," +
                    "actif BOOLEAN DEFAULT TRUE," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "INDEX idx_role (role)," +
                    "INDEX idx_actif (actif)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
            stmt.executeUpdate(createUtilisateur);
            LOGGER.info("✅ Table 'utilisateur' créée");
            
            // Table des affectations (FK vers vehicule et utilisateur)
            String createAffectation = "CREATE TABLE affectation (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "vehicule_id INT NOT NULL," +
                    "conducteur_id INT NOT NULL," +
                    "date_debut DATE NOT NULL," +
                    "date_fin DATE," +
                    "motif TEXT," +
                    "statut ENUM('programmee', 'en_cours', 'terminee') DEFAULT 'programmee'," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "INDEX idx_vehicule_id (vehicule_id)," +
                    "INDEX idx_conducteur_id (conducteur_id)," +
                    "INDEX idx_statut (statut)," +
                    "INDEX idx_date_debut (date_debut)," +
                    "CONSTRAINT fk_affectation_vehicule FOREIGN KEY (vehicule_id) REFERENCES vehicule(id) ON DELETE CASCADE ON UPDATE CASCADE," +
                    "CONSTRAINT fk_affectation_conducteur FOREIGN KEY (conducteur_id) REFERENCES utilisateur(id) ON DELETE CASCADE ON UPDATE CASCADE" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
            stmt.executeUpdate(createAffectation);
            LOGGER.info("✅ Table 'affectation' créée");
            
            // Table des entretiens (FK vers vehicule uniquement)
            String createEntretien = "CREATE TABLE entretien (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "vehicule_id INT NOT NULL," +
                    "date_entretien DATE NOT NULL," +
                    "type_entretien VARCHAR(50)," +
                    "commentaire TEXT," +
                    "cout DECIMAL(10,2)," +
                    "statut ENUM('programme', 'en_cours', 'termine') DEFAULT 'programme'," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "INDEX idx_vehicule_id (vehicule_id)," +
                    "INDEX idx_date_entretien (date_entretien)," +
                    "INDEX idx_statut (statut)," +
                    "INDEX idx_type_entretien (type_entretien)," +
                    "CONSTRAINT fk_entretien_vehicule FOREIGN KEY (vehicule_id) REFERENCES vehicule(id) ON DELETE CASCADE ON UPDATE CASCADE" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
            stmt.executeUpdate(createEntretien);
            LOGGER.info("✅ Table 'entretien' créée");
            
            // Vérifier les contraintes FK
            if (!verifyForeignKeyConstraints()) {
                LOGGER.warning("⚠️ Problème avec les contraintes de clés étrangères");
            } else {
                LOGGER.info("🔗 Contraintes de clés étrangères vérifiées");
            }
            
            return true;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la création des tables", e);
            return false;
        }
    }
    
    /**
     * Vérifie les contraintes de clés étrangères
     */
    private static boolean verifyForeignKeyConstraints() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            LOGGER.info("🔍 Vérification des contraintes FK...");
            
            // Vérifier les contraintes FK dans information_schema
            String query = "SELECT " +
                    "TABLE_NAME, COLUMN_NAME, CONSTRAINT_NAME, " +
                    "REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME " +
                    "FROM information_schema.KEY_COLUMN_USAGE " +
                    "WHERE CONSTRAINT_SCHEMA = 'Bdd_charroi_auto' " +
                    "AND REFERENCED_TABLE_NAME IS NOT NULL " +
                    "ORDER BY TABLE_NAME";
            
            var rs = stmt.executeQuery(query);
            boolean hasConstraints = false;
            
            while (rs.next()) {
                hasConstraints = true;
                LOGGER.info(String.format("🔗 %s.%s -> %s.%s (%s)", 
                    rs.getString("TABLE_NAME"),
                    rs.getString("COLUMN_NAME"),
                    rs.getString("REFERENCED_TABLE_NAME"),
                    rs.getString("REFERENCED_COLUMN_NAME"),
                    rs.getString("CONSTRAINT_NAME")
                ));
            }
            
            return hasConstraints;
            
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "❌ Erreur lors de la vérification des contraintes", e);
            return false;
        }
    }
    
    /**
     * Insère les données de test
     */
    private static boolean insertSampleData() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            LOGGER.info("📝 Insertion des données de test...");
            
            // Données véhicules
            String insertVehicules = "INSERT INTO vehicule (matricule, marque, type, annee, disponible, date_assurance, date_vidange, date_visite_technique) " +
                    "VALUES " +
                    "('ABC123', 'Toyota', 'Pickup', 2018, TRUE, '2025-10-15', '2025-09-10', '2025-11-01')," +
                    "('DEF456', 'Hyundai', 'SUV', 2020, FALSE, '2025-08-20', '2025-07-15', '2025-09-30')," +
                    "('GHI789', 'Ford', 'Camionnette', 2016, TRUE, '2025-12-01', '2025-10-01', '2025-12-15')";
            stmt.executeUpdate(insertVehicules);
            
            // Données utilisateurs (mot de passe = "password123" hashé)
            String insertUtilisateurs = "INSERT INTO utilisateur (nom, role, mot_de_passe_hash) " +
                    "VALUES " +
                    "('Major Kabila', 'conducteur', 'hash_password_123')," +
                    "('Capitaine Mbayo', 'gestionnaire', 'hash_password_456')," +
                    "('Colonel Tshibanda', 'admin', 'hash_password_789')";
            stmt.executeUpdate(insertUtilisateurs);
            
            // Données affectations
            String insertAffectations = "INSERT INTO affectation (vehicule_id, conducteur_id, date_debut, date_fin, motif, statut) " +
                    "VALUES " +
                    "(1, 1, '2025-09-20', '2025-09-22', 'Transport matériel médical', 'terminee')," +
                    "(2, 1, '2025-08-10', '2025-08-12', 'Mission reconnaissance', 'terminee')," +
                    "(3, 1, '2025-09-25', NULL, 'Déplacement vers zone logistique', 'en_cours')";
            stmt.executeUpdate(insertAffectations);
            
            // Données entretiens
            String insertEntretiens = "INSERT INTO entretien (vehicule_id, date_entretien, type_entretien, commentaire, statut) " +
                    "VALUES " +
                    "(1, '2025-09-10', 'Vidange', 'Vidange moteur complète', 'termine')," +
                    "(2, '2025-07-15', 'Pneus', 'Remplacement des pneus arrière', 'termine')," +
                    "(3, '2025-10-01', 'Freins', 'Révision des plaquettes de frein', 'programme')";
            stmt.executeUpdate(insertEntretiens);
            
            LOGGER.info("✅ Données de test insérées");
            return true;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de l'insertion des données", e);
            return false;
        }
    }
    
    /**
     * Vérifie l'état de la base de données
     */
    public static void checkDatabaseStatus() {
        LOGGER.info("🔍 Vérification de l'état de la base de données...");
        
        if (!DatabaseConnection.isWampRunning()) {
            LOGGER.warning("⚠️ WAMP n'est pas démarré !");
            return;
        }
        
        if (DatabaseConnection.testConnection()) {
            LOGGER.info("✅ Connexion base de données OK");
            LOGGER.info(DatabaseConnection.getConnectionInfo());
        } else {
            LOGGER.warning("❌ Problème de connexion détecté");
        }
    }
}