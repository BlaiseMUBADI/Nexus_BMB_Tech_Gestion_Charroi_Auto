package nexus_bmb_soft.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Test simple de connexion MySQL avec tes identifiants
 * 
 * @author BlaiseMUBADI
 */
public class SimpleConnectionTest {
    
    public static void main(String[] args) {
        System.out.println("🧪 === TEST CONNEXION MySQL PERSONNALISÉE ===");
        System.out.println("👤 Utilisateur: blaise");
        System.out.println("🏠 Serveur: localhost:3306");
        System.out.println("💾 Base: Bdd_charroi_auto");
        System.out.println();
        
        // Test 1: Vérifier le driver
        testDriver();
        
        // Test 2: Connexion de base
        testBasicConnection();
        
        // Test 3: Connexion avec la classe
        testWithDatabaseConnection();
        
        System.out.println("\n🏁 === TEST TERMINÉ ===");
    }
    
    private static void testDriver() {
        System.out.println("🔍 Test 1: Vérification du driver MySQL...");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ Driver MySQL trouvé !");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Driver MySQL NON trouvé !");
            System.out.println("💡 Ajoutez mysql-connector-j.jar au projet");
            return;
        }
    }
    
    private static void testBasicConnection() {
        System.out.println("\n🔗 Test 2: Connexion directe...");
        
        String url = "jdbc:mysql://localhost:3306/Bdd_charroi_auto" +
                    "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        
        try (Connection conn = DriverManager.getConnection(url, "blaise", "Blaise@Mub5991")) {
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Connexion directe réussie !");
                System.out.println("📊 Base de données: " + conn.getCatalog());
                System.out.println("🖥️ URL: " + conn.getMetaData().getURL());
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Connexion directe échouée :");
            System.out.println("   Code erreur: " + e.getErrorCode());
            System.out.println("   Message: " + e.getMessage());
            
            // Analyser l'erreur
            if (e.getErrorCode() == 1045) {
                System.out.println("💡 Problème d'authentification - vérifiez login/password");
            } else if (e.getErrorCode() == 1049) {
                System.out.println("💡 Base de données inexistante - créez 'Bdd_charroi_auto'");
            } else if (e.getMessage().contains("Communications link failure")) {
                System.out.println("💡 WAMP/MySQL non démarré ou port incorrect");
            }
        }
    }
    
    private static void testWithDatabaseConnection() {
        System.out.println("\n🏗️ Test 3: Avec DatabaseConnection...");
        try {
            if (DatabaseConnection.testConnection()) {
                System.out.println("✅ DatabaseConnection fonctionne !");
                System.out.println(DatabaseConnection.getConnectionInfo());
            } else {
                System.out.println("❌ DatabaseConnection a échoué");
            }
        } catch (Exception e) {
            System.out.println("❌ Erreur DatabaseConnection: " + e.getMessage());
        }
    }
}