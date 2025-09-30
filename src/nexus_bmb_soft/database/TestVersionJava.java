package nexus_bmb_soft.database;

/**
 * Test rapide pour vérifier la version Java utilisée par NetBeans
 */
public class TestVersionJava {
    
    public static void main(String[] args) {
        System.out.println("=== INFORMATION VERSION JAVA ===");
        System.out.println("Version Java Runtime: " + System.getProperty("java.runtime.version"));
        System.out.println("Version Java: " + System.getProperty("java.version"));
        System.out.println("Vendor Java: " + System.getProperty("java.vendor"));
        System.out.println("Java Home: " + System.getProperty("java.home"));
        System.out.println("=================================");
        
        // Test des fonctionnalités modernes Java
        testTextBlocks();
        testSwitchExpression();
    }
    
    /**
     * Test des text blocks (Java 15+)
     */
    private static void testTextBlocks() {
        String sql = """
            SELECT * 
            FROM vehicule 
            WHERE actif = 1
            ORDER BY matricule
            """;
        System.out.println("✅ Text blocks fonctionnent !");
        System.out.println("Requête SQL:");
        System.out.println(sql);
    }
    
    /**
     * Test des switch expressions (Java 14+)
     */
    private static void testSwitchExpression() {
        String jour = "LUNDI";
        
        String message = switch (jour) {
            case "LUNDI" -> "Début de semaine";
            case "VENDREDI" -> "Fin de semaine";
            default -> "Milieu de semaine";
        };
        
        System.out.println("✅ Switch expressions fonctionnent !");
        System.out.println("Message: " + message);
    }
}