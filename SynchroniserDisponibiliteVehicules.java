import nexus_bmb_soft.database.dao.VehiculeDAO;

/**
 * Utilitaire pour synchroniser manuellement la disponibilité des véhicules
 * À utiliser quand vous remarquez des incohérences dans les statuts
 */
public class SynchroniserDisponibiliteVehicules {
    public static void main(String[] args) {
        try {
            System.out.println("🔧 UTILITAIRE DE SYNCHRONISATION DES VÉHICULES");
            System.out.println("=" .repeat(50));
            
            VehiculeDAO vehiculeDAO = new VehiculeDAO();
            
            System.out.println("🔄 Synchronisation en cours...");
            vehiculeDAO.synchroniserToutesLesDisponibilites();
            
            System.out.println("✅ Synchronisation terminée avec succès !");
            System.out.println("\nVous pouvez maintenant relancer votre interface.");
            System.out.println("Les statuts de disponibilité seront maintenant corrects.");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la synchronisation:");
            e.printStackTrace();
            System.err.println("\nVeuillez vérifier la connexion à la base de données.");
        }
    }
}