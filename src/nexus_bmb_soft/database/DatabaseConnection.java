package nexus_bmb_soft.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestionnaire de connexion à la base de données MySQL via WAMP
 * Configuration pour MySQL 8.2.0
 * 
 * @author BlaiseMUBADI
 */
public class DatabaseConnection {
    
    // Configuration WAMP personnalisée
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "Bdd_charroi_auto";
    private static final String DB_USER = "blaise";
    private static final String DB_PASSWORD = "Blaise@Mub5991";
    
    // URL de connexion pour MySQL 8.2.0
    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME 
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static Connection connection = null;
    
    /**
     * Obtient une connexion à la base de données
     * Utilise le pattern Singleton pour une seule connexion
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Chargement du driver MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Création de la connexion
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                
                LOGGER.info("✅ Connexion à la base de données établie avec succès");
                LOGGER.info("📊 Base de données: " + DB_NAME);
                LOGGER.info("🖥️ Serveur: " + DB_HOST + ":" + DB_PORT);
            }
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "❌ Driver MySQL introuvable", e);
            throw new RuntimeException("Driver MySQL non trouvé. Ajoutez mysql-connector-java à votre projet.", e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur de connexion à la base de données", e);
            throw new RuntimeException("Impossible de se connecter à la base de données. Vérifiez que WAMP est démarré.", e);
        }
        return connection;
    }
    
    /**
     * Ferme la connexion à la base de données
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                LOGGER.info("🔌 Connexion à la base de données fermée");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "⚠️ Erreur lors de la fermeture de la connexion", e);
        }
    }
    
    /**
     * Teste la connexion à la base de données
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                // Test simple avec une requête
                Statement stmt = conn.createStatement();
                stmt.executeQuery("SELECT 1");
                stmt.close();
                
                LOGGER.info("✅ Test de connexion réussi");
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "❌ Test de connexion échoué", e);
        }
        return false;
    }
    
    /**
     * Obtient les informations de connexion
     */
    public static String getConnectionInfo() {
        return String.format(
            "🔗 Configuration Base de Données:\n" +
            "   • Serveur: %s:%s\n" +
            "   • Base: %s\n" +
            "   • Utilisateur: %s\n" +
            "   • Driver: MySQL Connector/J\n" +
            "   • Version MySQL: 8.2.0 (WAMP)",
            DB_HOST, DB_PORT, DB_NAME, DB_USER
        );
    }
    
    /**
     * Vérifie si WAMP est démarré en testant la connexion
     */
    public static boolean isWampRunning() {
        try {
            // Test de connexion basique au serveur MySQL
            Connection testConn = DriverManager.getConnection(
                "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/information_schema" +
                "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", 
                DB_USER, DB_PASSWORD
            );
            testConn.close();
            return true;
        } catch (SQLException e) {
            LOGGER.warning("⚠️ WAMP ne semble pas démarré ou MySQL non accessible");
            return false;
        }
    }
    
    /**
     * Créer la base de données si elle n'existe pas
     */
    public static boolean createDatabaseIfNotExists() {
        try {
            // Connexion à MySQL sans spécifier la base
            String baseUrl = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + 
                           "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            
            Connection conn = DriverManager.getConnection(baseUrl, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();
            
            // Créer la base si elle n'existe pas
            String createDB = "CREATE DATABASE IF NOT EXISTS " + DB_NAME + 
                            " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            stmt.executeUpdate(createDB);
            
            LOGGER.info("✅ Base de données créée ou vérifiée: " + DB_NAME);
            
            stmt.close();
            conn.close();
            return true;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la création de la base", e);
            return false;
        }
    }
}