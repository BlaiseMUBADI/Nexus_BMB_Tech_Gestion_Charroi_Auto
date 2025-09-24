package nexus_bmb_soft.database;

import nexus_bmb_soft.database.dao.VehiculeDAO;
import nexus_bmb_soft.models.Vehicule;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

/**
 * Classe utilitaire pour tester la configuration de la base de données
 * 
 * @author BlaiseMUBADI
 */
public class DatabaseTester {
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseTester.class.getName());
    
    /**
     * Test complet de la base de données
     */
    public static void runFullTest() {
        LOGGER.info("🧪 === TESTS BASE DE DONNÉES CHARROI AUTO ===");
        
        // Test 1: Connexion WAMP
        testWampConnection();
        
        // Test 2: Initialisation base
        testDatabaseInitialization();
        
        // Test 3: CRUD Véhicules
        testVehiculeOperations();
        
        LOGGER.info("🏁 === TESTS TERMINÉS ===");
    }
    
    /**
     * Test de connexion WAMP
     */
    private static void testWampConnection() {
        LOGGER.info("\n🔍 Test 1: Connexion WAMP MySQL 8.2.0");
        
        if (DatabaseConnection.isWampRunning()) {
            LOGGER.info("✅ WAMP est démarré et MySQL accessible");
            
            if (DatabaseConnection.testConnection()) {
                LOGGER.info("✅ Connexion à la base de données réussie");
                LOGGER.info(DatabaseConnection.getConnectionInfo());
            } else {
                LOGGER.warning("❌ Problème de connexion à la base");
            }
        } else {
            LOGGER.warning("⚠️ WAMP n'est pas démarré !");
            LOGGER.info("💡 Veuillez démarrer WAMP avant de continuer");
        }
    }
    
    /**
     * Test d'initialisation de la base
     */
    private static void testDatabaseInitialization() {
        LOGGER.info("\n🚀 Test 2: Initialisation Base de Données");
        
        DatabaseManager.checkDatabaseStatus();
        
        if (DatabaseManager.initializeDatabase()) {
            LOGGER.info("✅ Base de données initialisée avec succès");
        } else {
            LOGGER.warning("❌ Échec de l'initialisation");
        }
    }
    
    /**
     * Test des opérations CRUD sur les véhicules
     */
    private static void testVehiculeOperations() {
        LOGGER.info("\n🚗 Test 3: Opérations CRUD Véhicules");
        
        VehiculeDAO vehiculeDAO = new VehiculeDAO();
        
        // Test lecture
        LOGGER.info("📋 Lecture de tous les véhicules:");
        List<Vehicule> vehicules = vehiculeDAO.getTousVehicules();
        for (Vehicule v : vehicules) {
            LOGGER.info("  • " + v.toString());
        }
        
        // Test statistiques
        VehiculeDAO.VehiculeStats stats = vehiculeDAO.getStatistiques();
        LOGGER.info("📊 Statistiques: " + stats.toString());
        
        // Test recherche
        LOGGER.info("🔍 Test recherche 'Toyota':");
        List<Vehicule> recherche = vehiculeDAO.rechercherVehicules("Toyota");
        LOGGER.info("  Trouvé: " + recherche.size() + " véhicule(s)");
        
        // Test véhicules disponibles
        LOGGER.info("✅ Véhicules disponibles:");
        List<Vehicule> disponibles = vehiculeDAO.getVehiculesDisponibles();
        LOGGER.info("  Nombre disponibles: " + disponibles.size());
    }
    
    /**
     * Test rapide de connexion seulement
     */
    public static boolean quickConnectionTest() {
        LOGGER.info("⚡ Test rapide de connexion...");
        
        if (!DatabaseConnection.isWampRunning()) {
            LOGGER.warning("❌ WAMP non démarré");
            return false;
        }
        
        if (!DatabaseConnection.testConnection()) {
            LOGGER.warning("❌ Connexion échouée");
            return false;
        }
        
        LOGGER.info("✅ Connexion OK");
        return true;
    }
    
    /**
     * Main pour test indépendant
     */
    public static void main(String[] args) {
        System.out.println("🚀 Test de la configuration base de données");
        System.out.println("===========================================");
        
        runFullTest();
    }
}