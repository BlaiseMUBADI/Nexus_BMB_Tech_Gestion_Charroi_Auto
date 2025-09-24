/**
 * Test ultra-simple pour vérifier la configuration MySQL
 */
public class TestMySQL {
    public static void main(String[] args) {
        System.out.println("🧪 === TEST MYSQL SIMPLE ===");
        System.out.println("Configuration:");
        System.out.println("  Host: localhost:3306");
        System.out.println("  User: blaise");
        System.out.println("  Password: Blaise@Mub5991");
        System.out.println("  Database: Bdd_charroi_auto");
        System.out.println();
        
        // Test 1: Driver disponible ?
        System.out.println("🔍 Vérification du driver MySQL...");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ Driver MySQL trouvé dans le classpath !");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Driver MySQL NON trouvé !");
            System.out.println("💡 Solution: Ajoutez mysql-connector-j-8.x.x.jar au projet");
            System.out.println("💡 Téléchargement: https://dev.mysql.com/downloads/connector/j/");
            return;
        }
        
        // Test 2: Tentative de connexion
        System.out.println("\n🔗 Test de connexion...");
        java.sql.Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/Bdd_charroi_auto?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            conn = java.sql.DriverManager.getConnection(url, "blaise", "Blaise@Mub5991");
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ CONNEXION RÉUSSIE !");
                System.out.println("📊 Connecté à: " + conn.getCatalog());
                
                // Test requête simple
                java.sql.Statement stmt = conn.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery("SELECT 1 as test");
                if (rs.next()) {
                    System.out.println("🎯 Test requête: OK (résultat = " + rs.getInt("test") + ")");
                }
                rs.close();
                stmt.close();
            }
            
        } catch (java.sql.SQLException e) {
            System.out.println("❌ CONNEXION ÉCHOUÉE !");
            System.out.println("Code erreur: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
            System.out.println();
            
            // Diagnostics
            if (e.getErrorCode() == 1045) {
                System.out.println("🚨 Erreur d'authentification !");
                System.out.println("💡 Vérifiez que l'utilisateur 'blaise' existe avec le bon mot de passe");
                System.out.println("💡 Dans phpMyAdmin: Comptes utilisateurs → Ajouter un compte");
            } else if (e.getErrorCode() == 1049) {
                System.out.println("🚨 Base de données 'Bdd_charroi_auto' introuvable !");
                System.out.println("💡 Créez la base dans phpMyAdmin ou avec SQL:");
                System.out.println("   CREATE DATABASE Bdd_charroi_auto;");
            } else if (e.getMessage().contains("Communications link failure")) {
                System.out.println("🚨 WAMP/MySQL non accessible !");
                System.out.println("💡 Vérifiez que WAMP est démarré (icône verte)");
                System.out.println("💡 Vérifiez que MySQL fonctionne sur le port 3306");
            }
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (java.sql.SQLException e) {}
            }
        }
        
        System.out.println("\n🏁 === FIN DU TEST ===");
    }
}