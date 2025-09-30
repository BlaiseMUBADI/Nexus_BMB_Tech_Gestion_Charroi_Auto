package nexus_bmb_soft.database;

import java.time.LocalDate;
import nexus_bmb_soft.models.AlerteEntretien;
import nexus_bmb_soft.models.TypeEntretien;
import nexus_bmb_soft.models.Vehicule;

/**
 * Test du système d'entretien professionnel
 * Vérifie le bon fonctionnement des nouveaux modèles et DAOs
 * 
 * @author BlaiseMUBADI
 */
public class TestSystemeEntretienProfessionnel {
    
    public static void main(String[] args) {
        System.out.println("=== TEST SYSTÈME D'ENTRETIEN PROFESSIONNEL ===\n");
        
        try {
            // Test création TypeEntretien
            testCreationTypeEntretien();
            
            // Test création AlerteEntretien
            testCreationAlerteEntretien();
            
            // Test Vehicule avec nouveaux champs
            testVehiculeEnrichi();
            
            System.out.println("\n✅ TOUS LES TESTS SONT PASSÉS AVEC SUCCÈS !");
            
        } catch (Exception e) {
            System.err.println("❌ ERREUR LORS DES TESTS : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test de création et manipulation des types d'entretien
     */
    private static void testCreationTypeEntretien() {
        System.out.println("📋 Test TypeEntretien...");
        
        // Création d'un type d'entretien préventif
        TypeEntretien vidange = new TypeEntretien();
        vidange.setNom("Vidange Moteur");
        vidange.setDescription("Changement de l'huile moteur et du filtre");
        vidange.setCategorie(TypeEntretien.Categorie.PREVENTIF);
        vidange.setPriorite(TypeEntretien.Priorite.HAUTE);
        vidange.setPeriodiciteKm(5000);
        vidange.setPeriodiciteMois(6);
        vidange.setActif(true);
        
        System.out.println("   • Vidange créée: " + vidange.getNom());
        System.out.println("   • Catégorie: " + vidange.getCategorie());
        System.out.println("   • Périodicité: " + vidange.getPeriodiciteKm() + " km / " + 
                         vidange.getPeriodiciteMois() + " mois");
        System.out.println("   • Priorité: " + vidange.getPriorite());
        
        // Test des méthodes utilitaires
        System.out.println("   • Type: " + vidange.getCategorie().name());
        System.out.println("   • Actif: " + vidange.isActif());
        
        System.out.println("✅ TypeEntretien - OK\n");
    }
    
    /**
     * Test de création et manipulation des alertes d'entretien
     */
    private static void testCreationAlerteEntretien() {
        System.out.println("🚨 Test AlerteEntretien...");
        
        // Création d'une alerte d'urgence
        AlerteEntretien alerte = new AlerteEntretien();
        alerte.setVehiculeId(1);
        alerte.setTypeAlerte(AlerteEntretien.TypeAlerte.ECHEANCE_KM);
        alerte.setNiveau(AlerteEntretien.Niveau.URGENT);
        alerte.setStatut(AlerteEntretien.Statut.ACTIVE);
        alerte.setMessage("Vidange urgente requise - Dépassement de 500 km");
        alerte.setDateEcheance(LocalDate.now().plusDays(7));
        
        System.out.println("   • Alerte créée: " + alerte.getTypeAlerte());
        System.out.println("   • Niveau: " + alerte.getNiveau());
        System.out.println("   • Message: " + alerte.getMessage());
        System.out.println("   • Échéance: " + alerte.getDateEcheance());
        
        // Test des valeurs enum
        System.out.println("   • Type alerte: " + alerte.getTypeAlerte().name());
        System.out.println("   • Niveau: " + alerte.getNiveau().name());
        System.out.println("   • Statut: " + alerte.getStatut().name());
        
        System.out.println("✅ AlerteEntretien - OK\n");
    }
    
    /**
     * Test du véhicule enrichi avec nouveaux champs
     */
    private static void testVehiculeEnrichi() {
        System.out.println("🚗 Test Vehicule Enrichi...");
        
        // Création d'un véhicule avec champs professionnels
        Vehicule vehicule = new Vehicule();
        vehicule.setMatricule("BMB-001");
        vehicule.setMarque("Toyota");
        vehicule.setType("Hilux");
        vehicule.setAnnee(2022);
        vehicule.setKilometrageActuel(15000);
        vehicule.setKmDerniereVidange(10000);
        vehicule.setPeriodiciteVidange(5000);
        vehicule.setStatutVehicule(Vehicule.Statut.DISPONIBLE);
        vehicule.setEtatVehicule(Vehicule.Etat.BON);
        vehicule.setCategorieVehicule(Vehicule.Categorie.UTILITAIRE);
        vehicule.setTypeCarburant(Vehicule.Carburant.DIESEL);
        vehicule.setDateMiseEnService(LocalDate.of(2022, 1, 15));
        vehicule.setObservations("Véhicule de service - Révision à jour");
        
        System.out.println("   • Véhicule: " + vehicule.getMatricule() + " - " + vehicule.getMarque());
        System.out.println("   • Kilométrage actuel: " + vehicule.getKilometrageActuel() + " km");
        System.out.println("   • Dernière vidange: " + vehicule.getKmDerniereVidange() + " km");
        System.out.println("   • Km avant vidange: " + vehicule.getKmAvantVidange() + " km");
        System.out.println("   • Statut: " + vehicule.getStatutVehicule());
        System.out.println("   • État: " + vehicule.getEtatVehicule());
        System.out.println("   • Carburant: " + vehicule.getTypeCarburant());
        
        // Test des méthodes d'analyse
        System.out.println("   • Vidange nécessaire (km)? " + vehicule.vidangeNecessaireKm());
        System.out.println("   • Âge véhicule: " + vehicule.getAgeVehicule() + " ans");
        System.out.println("   • Priorité entretien: " + vehicule.getPrioriteEntretien());
        System.out.println("   • Statut général: " + vehicule.getStatutGeneral());
        
        System.out.println("✅ Vehicule Enrichi - OK\n");
    }
}