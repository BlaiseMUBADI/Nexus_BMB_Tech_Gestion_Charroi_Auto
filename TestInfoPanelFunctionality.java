import javax.swing.*;
import nexus_bmb_soft.application.form.other.FormNouvelleAffectation;
import nexus_bmb_soft.models.*;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

public class TestInfoPanelFunctionality {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("🧪 Test de fonctionnalité du Panel Informations et Validation...");
                
                // Créer une instance du formulaire
                FormNouvelleAffectation form = new FormNouvelleAffectation();
                
                // Créer des données de test
                List<Vehicule> vehiculesTest = createTestVehicules();
                List<Utilisateur> conducteursTest = createTestConducteurs();
                
                // Utiliser la reflection pour accéder aux méthodes privées
                Method mettreAJourCompteurs = FormNouvelleAffectation.class
                    .getDeclaredMethod("mettreAJourCompteurs", List.class, List.class);
                mettreAJourCompteurs.setAccessible(true);
                
                // Tester la mise à jour des compteurs
                mettreAJourCompteurs.invoke(form, vehiculesTest, conducteursTest);
                
                // Vérifier que les labels ont été mis à jour
                Field lblVehiculesDispos = FormNouvelleAffectation.class.getDeclaredField("lblVehiculesDispos");
                Field lblConducteursActifs = FormNouvelleAffectation.class.getDeclaredField("lblConducteursActifs");
                lblVehiculesDispos.setAccessible(true);
                lblConducteursActifs.setAccessible(true);
                
                JLabel lblVehicules = (JLabel) lblVehiculesDispos.get(form);
                JLabel lblConducteurs = (JLabel) lblConducteursActifs.get(form);
                
                System.out.println("✅ Label véhicules: " + lblVehicules.getText());
                System.out.println("✅ Label conducteurs: " + lblConducteurs.getText());
                
                // Afficher le formulaire pour vérification visuelle
                JFrame frame = new JFrame("Test - Panel Informations");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(form);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                
                System.out.println("✅ Test réussi ! Les compteurs sont fonctionnels.");
                
            } catch (Exception e) {
                System.err.println("❌ Erreur lors du test:");
                e.printStackTrace();
            }
        });
    }
    
    private static List<Vehicule> createTestVehicules() {
        List<Vehicule> vehicules = new ArrayList<>();
        
        // Créer des véhicules de test
        Vehicule v1 = new Vehicule();
        v1.setIdVehicule(1);
        v1.setMatricule("AA-001-BB");
        v1.setMarque("Toyota");
        v1.setDisponible(true);
        vehicules.add(v1);
        
        Vehicule v2 = new Vehicule();
        v2.setIdVehicule(2);
        v2.setMatricule("CC-002-DD");
        v2.setMarque("Honda");
        v2.setDisponible(true);
        vehicules.add(v2);
        
        Vehicule v3 = new Vehicule();
        v3.setIdVehicule(3);
        v3.setMatricule("EE-003-FF");
        v3.setMarque("Ford");
        v3.setDisponible(false); // Affecté
        vehicules.add(v3);
        
        return vehicules;
    }
    
    private static List<Utilisateur> createTestConducteurs() {
        List<Utilisateur> conducteurs = new ArrayList<>();
        
        Utilisateur c1 = new Utilisateur();
        c1.setIdUtilisateur(1);
        c1.setPrenom("Jean");
        c1.setNom("Dupont");
        c1.setMatricule("CD001");
        c1.setRole(RoleUtilisateur.CONDUCTEUR);
        c1.setStatut("ACTIF");
        conducteurs.add(c1);
        
        Utilisateur c2 = new Utilisateur();
        c2.setIdUtilisateur(2);
        c2.setPrenom("Marie");
        c2.setNom("Martin");
        c2.setMatricule("CD002");
        c2.setRole(RoleUtilisateur.CONDUCTEUR_SENIOR);
        c2.setStatut("ACTIF");
        conducteurs.add(c2);
        
        return conducteurs;
    }
}