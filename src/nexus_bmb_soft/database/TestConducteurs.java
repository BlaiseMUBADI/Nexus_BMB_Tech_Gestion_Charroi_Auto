package nexus_bmb_soft.database;

import java.util.List;
import nexus_bmb_soft.database.dao.UtilisateurDAO;
import nexus_bmb_soft.models.Utilisateur;

/**
 * Test spécifique pour les conducteurs
 */
public class TestConducteurs {
    
    public static void main(String[] args) {
        System.out.println("🔍 === DIAGNOSTIC CONDUCTEURS ===");
        
        try {
            UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
            System.out.println("✅ DAO Utilisateur créé avec succès");
            
            // Test 1: Tous les utilisateurs
            System.out.println("\n📊 TOUS LES UTILISATEURS:");
            List<Utilisateur> tousUtilisateurs = utilisateurDAO.lireTous();
            System.out.println("Nombre total: " + tousUtilisateurs.size());
            
            for (Utilisateur u : tousUtilisateurs) {
                System.out.println("  • ID:" + u.getId() + " | " + u.getNom() + " " + u.getPrenom() + 
                                 " | Rôle: " + u.getRole() + " | Statut: " + u.getStatut());
            }
            
            // Test 2: Conducteurs disponibles seulement
            System.out.println("\n🚗 CONDUCTEURS DISPONIBLES:");
            List<Utilisateur> conducteursDisponibles = utilisateurDAO.getConducteursDisponibles();
            System.out.println("Nombre disponible: " + conducteursDisponibles.size());
            
            if (conducteursDisponibles.isEmpty()) {
                System.out.println("⚠️ AUCUN CONDUCTEUR DISPONIBLE !");
                System.out.println("Raisons possibles:");
                System.out.println("1. Tous les conducteurs ont déjà une affectation active");
                System.out.println("2. Statut des conducteurs n'est pas 'ACTIF'");
                System.out.println("3. Rôle des utilisateurs n'est pas 'CONDUCTEUR' ou 'CONDUCTEUR_SENIOR'");
            } else {
                for (Utilisateur u : conducteursDisponibles) {
                    System.out.println("  • " + u.getNom() + " " + u.getPrenom() + 
                                     " (" + u.getRole() + ") - Statut: " + u.getStatut());
                }
            }
            
            // Test 3: Compter par rôle
            System.out.println("\n📈 STATISTIQUES PAR RÔLE:");
            String[] roles = {"CONDUCTEUR", "CONDUCTEUR_SENIOR", "GESTIONNAIRE", "ADMIN", "SUPER_ADMIN"};
            
            for (String role : roles) {
                int count = utilisateurDAO.compterParRole(role);
                System.out.println("  " + role + ": " + count);
            }
            
            // Test 4: Compter par statut
            System.out.println("\n📈 STATISTIQUES PAR STATUT:");
            String[] statuts = {"ACTIF", "INACTIF", "SUSPENDU"};
            
            for (String statut : statuts) {
                int count = utilisateurDAO.compterParStatut(statut);
                System.out.println("  " + statut + ": " + count);
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERREUR: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n🏁 === FIN DU TEST ===");
    }
}