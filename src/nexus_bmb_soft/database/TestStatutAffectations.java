package nexus_bmb_soft.database;

import java.util.List;
import nexus_bmb_soft.database.dao.AffectationDAO;
import nexus_bmb_soft.models.Affectation;

/**
 * Test de la nouvelle fonctionnalité des statuts d'affectations
 * 
 * @author BlaiseMUBADI
 */
public class TestStatutAffectations {
    
    public static void main(String[] args) {
        System.out.println("🧪 Test du système de statuts d'affectations...\n");
        
        try {
            AffectationDAO affectationDAO = new AffectationDAO();
            
            System.out.println("📋 Chargement de toutes les affectations avec statuts calculés...");
            List<Affectation> affectations = affectationDAO.listerToutesAffectations(20);
            
            System.out.println("✅ " + affectations.size() + " affectations trouvées\n");
            
            // Afficher les statuts
            System.out.println("📊 Répartition par statut :");
            int programmees = 0, enCours = 0, terminees = 0, autres = 0;
            
            for (Affectation affectation : affectations) {
                String statut = affectation.getStatut();
                System.out.printf("ID %d: %s - Véhicule %d - Conducteur %d - %s -> %s%n",
                    affectation.getId(),
                    statut,
                    affectation.getVehiculeId(),
                    affectation.getConducteurId(),
                    affectation.getDateDebut(),
                    affectation.getDateFin() != null ? affectation.getDateFin() : "Non définie");
                
                switch (statut) {
                    case "Programmée":
                        programmees++;
                        break;
                    case "En cours":
                        enCours++;
                        break;
                    case "Terminée":
                        terminees++;
                        break;
                    default:
                        autres++;
                        break;
                }
            }
            
            System.out.println("\n📈 Résumé :");
            System.out.println("🔮 Programmées : " + programmees);
            System.out.println("▶️ En cours : " + enCours);
            System.out.println("✅ Terminées : " + terminees);
            System.out.println("❓ Autres : " + autres);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du test : " + e.getMessage());
            e.printStackTrace();
        }
    }
}