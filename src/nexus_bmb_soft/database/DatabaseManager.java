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
            
            // Table des véhicules
            String createVehicule = "CREATE TABLE IF NOT EXISTS vehicule (" +
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
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
            stmt.executeUpdate(createVehicule);
            LOGGER.info("✅ Table 'vehicule' créée");
            
            // Table des utilisateurs
            String createUtilisateur = "CREATE TABLE IF NOT EXISTS utilisateur (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "nom VARCHAR(100) NOT NULL," +
                    "role ENUM('admin', 'gestionnaire', 'conducteur') NOT NULL," +
                    "mot_de_passe_hash VARCHAR(255) NOT NULL," +
                    "actif BOOLEAN DEFAULT TRUE," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
            stmt.executeUpdate(createUtilisateur);
            LOGGER.info("✅ Table 'utilisateur' créée");
            
            // Table des affectations
            String createAffectation = "CREATE TABLE IF NOT EXISTS affectation (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "vehicule_id INT NOT NULL," +
                    "conducteur_id INT NOT NULL," +
                    "date_debut DATE NOT NULL," +
                    "date_fin DATE," +
                    "motif TEXT," +
                    "statut ENUM('programmee', 'en_cours', 'terminee') DEFAULT 'programmee'," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (vehicule_id) REFERENCES vehicule(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (conducteur_id) REFERENCES utilisateur(id) ON DELETE CASCADE" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
            stmt.executeUpdate(createAffectation);
            LOGGER.info("✅ Table 'affectation' créée");
            
            // Table des entretiens
            String createEntretien = "CREATE TABLE IF NOT EXISTS entretien (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "vehicule_id INT NOT NULL," +
                    "date_entretien DATE NOT NULL," +
                    "type_entretien VARCHAR(50)," +
                    "commentaire TEXT," +
                    "cout DECIMAL(10,2)," +
                    "statut ENUM('programme', 'en_cours', 'termine') DEFAULT 'programme'," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (vehicule_id) REFERENCES vehicule(id) ON DELETE CASCADE" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
            stmt.executeUpdate(createEntretien);
            LOGGER.info("✅ Table 'entretien' créée");
            
            return true;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la création des tables", e);
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
            String insertVehicules = "INSERT IGNORE INTO vehicule (matricule, marque, type, annee, disponible, date_assurance, date_vidange, date_visite_technique) " +
                    "VALUES " +
                    "('ABC123', 'Toyota', 'Pickup', 2018, TRUE, '2025-10-15', '2025-09-10', '2025-11-01')," +
                    "('DEF456', 'Hyundai', 'SUV', 2020, FALSE, '2025-08-20', '2025-07-15', '2025-09-30')," +
                    "('GHI789', 'Ford', 'Camionnette', 2016, TRUE, '2025-12-01', '2025-10-01', '2025-12-15')";
            stmt.executeUpdate(insertVehicules);
            
            // Données utilisateurs (mot de passe = "password123" hashé)
            String insertUtilisateurs = "INSERT IGNORE INTO utilisateur (nom, role, mot_de_passe_hash) " +
                    "VALUES " +
                    "('Major Kabila', 'conducteur', 'hash_password_123')," +
                    "('Capitaine Mbayo', 'gestionnaire', 'hash_password_456')," +
                    "('Colonel Tshibanda', 'admin', 'hash_password_789')";
            stmt.executeUpdate(insertUtilisateurs);
            
            // Données affectations
            String insertAffectations = "INSERT IGNORE INTO affectation (vehicule_id, conducteur_id, date_debut, date_fin, motif, statut) " +
                    "VALUES " +
                    "(1, 1, '2025-09-20', '2025-09-22', 'Transport matériel médical', 'terminee')," +
                    "(2, 1, '2025-08-10', '2025-08-12', 'Mission reconnaissance', 'terminee')," +
                    "(3, 1, '2025-09-25', NULL, 'Déplacement vers zone logistique', 'en_cours')";
            stmt.executeUpdate(insertAffectations);
            
            // Données entretiens
            String insertEntretiens = "INSERT IGNORE INTO entretien (vehicule_id, date_entretien, type_entretien, commentaire, statut) " +
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