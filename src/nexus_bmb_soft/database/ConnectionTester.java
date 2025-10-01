package nexus_bmb_soft.database;

import nexus_bmb_soft.security.AuthenticationDAO;
import nexus_bmb_soft.security.AuthenticationDAO.AuthResult;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Classe de test pour vérifier la connexion à la base de données
 * et l'authentification des utilisateurs
 */
public class ConnectionTester {
    
    public static void main(String[] args) {
        System.out.println("=== TEST DE CONNEXION BASE DE DONNÉES ===");
        
        // Test 1: Connexion à la base
        testDatabaseConnection();
        
        // Test 2: Vérification structure table utilisateur
        testUserTableStructure();
        
        // Test 3: Test authentification
        testAuthentication();
    }
    
    private static void testDatabaseConnection() {
        System.out.println("\n1. Test connexion base de données...");
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Connexion à la base réussie !");
                System.out.println("   URL: " + conn.getMetaData().getURL());
                System.out.println("   Driver: " + conn.getMetaData().getDriverName());
            } else {
                System.out.println("❌ Échec de connexion à la base");
            }
        } catch (Exception e) {
            System.out.println("❌ Erreur de connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testUserTableStructure() {
        System.out.println("\n2. Test structure table utilisateur...");
        String sql = "SELECT matricule, nom, prenom, role, actif, statut FROM utilisateur LIMIT 5";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            System.out.println("✅ Structure table utilisateur OK !");
            System.out.println("   Utilisateurs trouvés:");
            
            while (rs.next()) {
                String matricule = rs.getString("matricule");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String role = rs.getString("role");
                int actifInt = rs.getInt("actif");
                boolean actifBool = rs.getBoolean("actif");
                String statut = rs.getString("statut");
                
                System.out.printf("   - %s: %s %s (%s) - actif(int)=%d, actif(bool)=%s, statut=%s%n", 
                    matricule, prenom, nom, role, actifInt, actifBool, statut);
            }
            
        } catch (Exception e) {
            System.out.println("❌ Erreur lecture table utilisateur: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testAuthentication() {
        System.out.println("\n3. Test authentification...");
        
        AuthenticationDAO authDAO = new AuthenticationDAO();
        
        // Comptes de test
        String[][] testAccounts = {
            {"TEST01", "123456"},
            {"COND01", "123456"},
            {"GES01", "123456"},
            {"ADMIN001", "Admin12345"}
        };
        
        for (String[] account : testAccounts) {
            String username = account[0];
            String password = account[1];
            
            System.out.printf("\n   Test connexion: %s / %s%n", username, password);
            
            try {
                AuthResult result = authDAO.authenticate(username, password);
                
                if (result.isSuccess()) {
                    System.out.printf("   ✅ Connexion réussie pour %s%n", username);
                    if (result.getUser() != null) {
                        System.out.printf("      Utilisateur: %s %s (%s)%n", 
                            result.getUser().getPrenom(),
                            result.getUser().getNom(),
                            result.getUser().getRole());
                        System.out.printf("      Actif: %s, Statut: %s, ValidForLogin: %s%n",
                            result.getUser().isActif(),
                            result.getUser().getStatut(),
                            result.getUser().isValidForLogin());
                    }
                } else {
                    System.out.printf("   ❌ Échec connexion pour %s: %s%n", username, result.getMessage());
                }
                
            } catch (Exception e) {
                System.out.printf("   ❌ Erreur pour %s: %s%n", username, e.getMessage());
                e.printStackTrace();
            }
        }
    }
}