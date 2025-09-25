import nexus_bmb_soft.database.dao.VehiculeDAO;
import nexus_bmb_soft.database.dao.AffectationDAO;
import nexus_bmb_soft.models.Vehicule;

public class TestSynchronisationDisponibilite {
    public static void main(String[] args) {
        try {
            System.out.println("🧪 Test de synchronisation de la disponibilité des véhicules...");
            
            VehiculeDAO vehiculeDAO = new VehiculeDAO();
            
            // Afficher l'état avant synchronisation
            System.out.println("\n📊 ÉTAT AVANT SYNCHRONISATION:");
            afficherEtatVehicules(vehiculeDAO);
            
            // Synchroniser les disponibilités
            System.out.println("\n🔄 SYNCHRONISATION EN COURS...");
            vehiculeDAO.synchroniserToutesLesDisponibilites();
            
            // Afficher l'état après synchronisation
            System.out.println("\n📊 ÉTAT APRÈS SYNCHRONISATION:");
            afficherEtatVehicules(vehiculeDAO);
            
            System.out.println("\n✅ Test de synchronisation terminé !");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du test de synchronisation:");
            e.printStackTrace();
        }
    }
    
    private static void afficherEtatVehicules(VehiculeDAO vehiculeDAO) {
        try {
            var vehicules = vehiculeDAO.getTousVehicules();
            System.out.println("Véhicules dans la base:");
            for (Vehicule v : vehicules) {
                boolean realementDispo = vehiculeDAO.estRealementDisponible(v.getId());
                String etatBD = v.isDisponible() ? "DISPONIBLE" : "AFFECTÉ";
                String etatReel = realementDispo ? "DISPONIBLE" : "AFFECTÉ";
                String indicateur = etatBD.equals(etatReel) ? "✅" : "❌";
                
                System.out.printf("  %s %s - %s %s (BD: %s, Réel: %s)%n", 
                    indicateur, v.getMatricule(), v.getMarque(), v.getType(), etatBD, etatReel);
            }
            
            // Compter les disponibles
            long disponiblesBD = vehicules.stream().filter(Vehicule::isDisponible).count();
            long disponiblesReels = vehicules.stream()
                .filter(v -> {
                    try {
                        return vehiculeDAO.estRealementDisponible(v.getId());
                    } catch (Exception e) {
                        return false;
                    }
                }).count();
            
            System.out.printf("  📊 Résumé: %d véhicules au total, %d disponibles (BD), %d disponibles (réel)%n", 
                vehicules.size(), disponiblesBD, disponiblesReels);
                
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'affichage des véhicules: " + e.getMessage());
        }
    }
}