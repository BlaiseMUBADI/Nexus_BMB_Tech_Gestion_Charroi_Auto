package nexus_bmb_soft.database;

import java.time.LocalDate;
import java.util.List;
import nexus_bmb_soft.database.dao.EntretienDAO;
import nexus_bmb_soft.models.Entretien;

/**
 * Test complet du système d'entretien et maintenance
 * 
 * @author BlaiseMUBADI
 */
public class TestSystemeEntretien {
    
    public static void main(String[] args) {
        System.out.println("🔧 Test complet du système d'entretien et maintenance...\n");
        
        EntretienDAO entretienDAO = new EntretienDAO();
        
        try {
            // Test 1: Création d'un nouvel entretien
            System.out.println("📝 Test 1: Création d'un nouvel entretien");
            Entretien nouvelEntretien = new Entretien();
            nouvelEntretien.setVehiculeId(1);
            nouvelEntretien.setDateEntretien(LocalDate.now().plusDays(7));
            nouvelEntretien.setTypeEntretien("Vidange");
            nouvelEntretien.setCommentaire("Vidange moteur complète avec filtre");
            nouvelEntretien.setCout(85.50);
            nouvelEntretien.setKilometrage(45000);
            nouvelEntretien.setStatut("programme");
            
            boolean ajoutReussi = entretienDAO.ajouterEntretien(nouvelEntretien);
            System.out.println(ajoutReussi ? "✅ Entretien créé avec succès - ID: " + nouvelEntretien.getId() 
                                          : "❌ Échec de la création");
            
            // Test 2: Lecture de tous les entretiens
            System.out.println("\n📋 Test 2: Lecture de tous les entretiens");
            List<Entretien> tousEntretiens = entretienDAO.listerTousEntretiens();
            System.out.println("📊 Total des entretiens: " + tousEntretiens.size());
            
            for (Entretien entretien : tousEntretiens) {
                System.out.println("  • " + entretien.getDescription() + 
                                 " | Statut: " + entretien.getStatutAffichage() + 
                                 " | Coût: " + entretien.getCoutFormate());
            }
            
            // Test 3: Recherche par véhicule
            System.out.println("\n🔍 Test 3: Recherche par véhicule (ID=1)");
            List<Entretien> entretiensVehicule1 = entretienDAO.listerEntretiensParVehicule(1);
            System.out.println("📊 Entretiens trouvés pour véhicule 1: " + entretiensVehicule1.size());
            
            // Test 4: Recherche par type
            System.out.println("\n🔍 Test 4: Recherche par type 'Vidange'");
            List<Entretien> entretiensVidange = entretienDAO.listerEntretiensParType("Vidange");
            System.out.println("📊 Entretiens de type 'Vidange': " + entretiensVidange.size());
            
            // Test 5: Recherche par statut
            System.out.println("\n🔍 Test 5: Recherche par statut 'programme'");
            List<Entretien> entretiensProgrammes = entretienDAO.listerEntretiensParStatut("programme");
            System.out.println("📊 Entretiens programmés: " + entretiensProgrammes.size());
            
            // Test 6: Recherche par période
            System.out.println("\n🔍 Test 6: Recherche par période (30 derniers jours)");
            LocalDate debutPeriode = LocalDate.now().minusDays(30);
            LocalDate finPeriode = LocalDate.now().plusDays(30);
            List<Entretien> entretiensPeriode = entretienDAO.listerEntretiensParPeriode(debutPeriode, finPeriode);
            System.out.println("📊 Entretiens sur 60 jours: " + entretiensPeriode.size());
            
            // Test 7: Statistiques
            System.out.println("\n📈 Test 7: Statistiques globales");
            EntretienDAO.EntretienStats stats = entretienDAO.obtenirStatistiques();
            System.out.println("📊 " + stats.toString());
            
            // Test 8: Modification de l'entretien créé
            if (ajoutReussi && nouvelEntretien.getId() > 0) {
                System.out.println("\n✏️ Test 8: Modification de l'entretien");
                nouvelEntretien.setStatut("en_cours");
                nouvelEntretien.setCommentaire("Entretien en cours d'exécution");
                nouvelEntretien.setCout(95.75); // Prix ajusté
                
                boolean modificationReussie = entretienDAO.modifierEntretien(nouvelEntretien);
                System.out.println(modificationReussie ? "✅ Modification réussie" : "❌ Échec de la modification");
                
                // Vérifier la modification
                Entretien entretienModifie = entretienDAO.obtenirEntretien(nouvelEntretien.getId());
                if (entretienModifie != null) {
                    System.out.println("✅ Vérification: Statut = " + entretienModifie.getStatutAffichage() + 
                                     ", Coût = " + entretienModifie.getCoutFormate());
                }
            }
            
            // Test 9: Recherche multi-critères
            System.out.println("\n🔍 Test 9: Recherche multi-critères");
            List<Entretien> recherche = entretienDAO.rechercherEntretiens(
                1, // vehicule ID
                "Vidange", // type
                "en_cours", // statut
                null, // date début
                null  // date fin
            );
            System.out.println("📊 Recherche multi-critères: " + recherche.size() + " résultat(s)");
            
            // Test 10: Test des méthodes utilitaires du modèle
            System.out.println("\n🧪 Test 10: Méthodes utilitaires du modèle");
            if (!tousEntretiens.isEmpty()) {
                Entretien test = tousEntretiens.get(0);
                System.out.println("  • isProgramme(): " + test.isProgramme());
                System.out.println("  • isEnCours(): " + test.isEnCours());
                System.out.println("  • isTermine(): " + test.isTermine());
                System.out.println("  • isMajeur(): " + test.isMajeur());
                System.out.println("  • isRoutine(): " + test.isRoutine());
                System.out.println("  • getNiveauPriorite(): " + test.getNiveauPriorite());
                System.out.println("  • getJoursDepuis(): " + test.getJoursDepuis());
            }
            
            System.out.println("\n🎉 Tests terminés avec succès!");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors des tests: " + e.getMessage());
            e.printStackTrace();
        }
    }
}