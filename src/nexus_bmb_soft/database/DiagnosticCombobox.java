package nexus_bmb_soft.database;

import nexus_bmb_soft.database.dao.UtilisateurDAO;
import nexus_bmb_soft.database.dao.VehiculeDAO;
import nexus_bmb_soft.models.Utilisateur;
import nexus_bmb_soft.models.Vehicule;
import java.util.List;

/**
 * Test rapide pour diagnostiquer le problème des combobox vides
 */
public class DiagnosticCombobox {
    
    public static void main(String[] args) {
        System.out.println("🔍 === DIAGNOSTIC PROBLÈME COMBOBOX ===");
        
        // Test 1: Connexion de base
        System.out.println("\n1️⃣ Test connexion de base:");
        boolean connexionOk = DatabaseTester.quickConnectionTest();
        
        if (!connexionOk) {
            System.out.println("❌ PROBLÈME DÉTECTÉ: Base de données inaccessible");
            System.out.println("💡 Solution: Démarrez WAMP Server et MySQL");
            return;
        }
        
        // Test 2: Création DAO Utilisateur
        System.out.println("\n2️⃣ Test création DAO Utilisateur:");
        try {
            UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
            System.out.println("✅ DAO Utilisateur créé avec succès");
            
            // Test 3: Récupération des conducteurs
            System.out.println("\n3️⃣ Test récupération conducteurs:");
            List<Utilisateur> conducteurs = utilisateurDAO.getConducteursDisponibles();
            System.out.println("📊 Nombre de conducteurs disponibles: " + conducteurs.size());
            
            if (conducteurs.isEmpty()) {
                System.out.println("⚠️ PROBLÈME DÉTECTÉ: Aucun conducteur disponible");
                System.out.println("💡 Vérification des utilisateurs dans la base...");
                
                // Test tous les utilisateurs
                List<Utilisateur> tousUtilisateurs = utilisateurDAO.lireTous();
                System.out.println("📊 Nombre total d'utilisateurs: " + tousUtilisateurs.size());
                
                for (Utilisateur u : tousUtilisateurs) {
                    System.out.println("  • " + u.getNom() + " " + u.getPrenom() + 
                                     " (" + u.getRole() + ") - Statut: " + u.getStatut());
                }
            } else {
                System.out.println("✅ Conducteurs disponibles trouvés:");
                for (Utilisateur u : conducteurs) {
                    System.out.println("  • " + u.getNom() + " " + u.getPrenom() + 
                                     " (" + u.getRole() + ") - Statut: " + u.getStatut());
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERREUR lors du test utilisateurs: " + e.getMessage());
            return;
        }
        
        // Test 4: Création DAO Véhicule
        System.out.println("\n4️⃣ Test création DAO Véhicule:");
        try {
            VehiculeDAO vehiculeDAO = new VehiculeDAO();
            System.out.println("✅ DAO Véhicule créé avec succès");
            
            // Test 5: Récupération des véhicules
            System.out.println("\n5️⃣ Test récupération véhicules:");
            List<Vehicule> vehicules = vehiculeDAO.getVehiculesDisponibles();
            System.out.println("📊 Nombre de véhicules disponibles: " + vehicules.size());
            
            if (vehicules.isEmpty()) {
                System.out.println("⚠️ PROBLÈME DÉTECTÉ: Aucun véhicule disponible");
                System.out.println("💡 Vérification des véhicules dans la base...");
                
                // Test tous les véhicules
                List<Vehicule> tousVehicules = vehiculeDAO.getTousVehicules();
                System.out.println("📊 Nombre total de véhicules: " + tousVehicules.size());
                
                for (Vehicule v : tousVehicules) {
                    System.out.println("  • " + v.getMatricule() + " - " + v.getMarque() + 
                                     " " + v.getType() + " (Disponible: " + v.isDisponible() + ")");
                }
            } else {
                System.out.println("✅ Véhicules disponibles trouvés:");
                for (Vehicule v : vehicules) {
                    System.out.println("  • " + v.getMatricule() + " - " + v.getMarque() + 
                                     " " + v.getType() + " (Disponible: " + v.isDisponible() + ")");
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERREUR lors du test véhicules: " + e.getMessage());
        }
        
        System.out.println("\n🏁 === FIN DU DIAGNOSTIC ===");
    }
}