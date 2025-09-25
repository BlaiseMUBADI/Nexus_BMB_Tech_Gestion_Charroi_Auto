package nexus_bmb_soft.database;

import nexus_bmb_soft.models.RoleUtilisateur;

/**
 * Test de la conversion valeur BD vers enum
 */
public class TestConversionRole {
    
    public static void main(String[] args) {
        testConversionRoles();
    }
    
    public static void testConversionRoles() {
        System.out.println("🧪 Test de conversion valeurs BD vers enum");
        System.out.println("==========================================");
        
        // Test des valeurs qui viennent de la base de données
        String[] valeursBD = {"admin", "gestionnaire", "conducteur", "conducteur_senior", "super_admin"};
        
        System.out.println("✅ Test des conversions fromString() (valeurs BD) :");
        for (String valeur : valeursBD) {
            try {
                RoleUtilisateur role = RoleUtilisateur.fromString(valeur);
                System.out.println("   - BD: '" + valeur + "' → Enum: " + role.name() + " (" + role.getValeur() + ")");
            } catch (Exception e) {
                System.out.println("   - ERREUR pour '" + valeur + "': " + e.getMessage());
            }
        }
        
        // Test des noms d'enum qui viennent des ComboBox
        String[] nomsEnum = {"ADMIN", "GESTIONNAIRE", "CONDUCTEUR", "CONDUCTEUR_SENIOR", "SUPER_ADMIN"};
        
        System.out.println();
        System.out.println("✅ Test des conversions valueOf() (noms enum) :");
        for (String nom : nomsEnum) {
            try {
                RoleUtilisateur role = RoleUtilisateur.valueOf(nom);
                System.out.println("   - ComboBox: '" + nom + "' → Enum: " + role.name() + " (" + role.getValeur() + ")");
            } catch (Exception e) {
                System.out.println("   - ERREUR pour '" + nom + "': " + e.getMessage());
            }
        }
        
        System.out.println();
        System.out.println("🎉 Test terminé !");
        System.out.println("🔧 Erreur 'gestionnaire' devrait être résolue avec fromString()");
    }
}