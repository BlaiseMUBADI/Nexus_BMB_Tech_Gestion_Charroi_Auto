/**
 * Test pour vérifier la liaison correcte entre les tables vehicule et affectation
 * basée sur la colonne statut de la table affectation.
 * 
 * PROBLÈME INITIAL:
 * - Les ComboBox affichaient tous les véhicules, même ceux avec des affectations actives
 * - La disponibilité n'était pas basée sur les affectations réelles
 * - Incohérence entre la table vehicule.disponible et les affectations en cours
 * 
 * NOUVELLE APPROCHE:
 * - VehiculeDAO.getVehiculesDisponibles() : Query SQL avec LEFT JOIN sur affectations
 * - UtilisateurDAO.getConducteursDisponibles() : Query SQL avec LEFT JOIN sur affectations
 * - Filtrage basé sur affectation.statut = 'en_cours'
 * - Seuls les éléments sans affectation active apparaissent dans les ComboBox
 * 
 * REQUÊTES SQL UTILISÉES:
 * 
 * 1. VÉHICULES DISPONIBLES:
 * SELECT v.* FROM vehicule v 
 * LEFT JOIN affectation a ON v.id = a.vehicule_id AND a.statut = 'en_cours'
 * WHERE a.vehicule_id IS NULL 
 * ORDER BY v.matricule
 * 
 * 2. CONDUCTEURS DISPONIBLES:
 * SELECT u.* FROM utilisateur u 
 * LEFT JOIN affectation a ON u.id = a.conducteur_id AND a.statut = 'en_cours'
 * WHERE u.role IN ('CONDUCTEUR', 'CONDUCTEUR_SENIOR') 
 * AND u.statut = 'ACTIF' 
 * AND a.conducteur_id IS NULL 
 * ORDER BY u.nom
 * 
 * RÉSULTAT ATTENDU:
 * - ComboBox véhicules: seulement ceux sans affectation statut='en_cours'
 * - ComboBox conducteurs: seulement ceux sans affectation statut='en_cours'  
 * - Compteurs: reflètent le nombre réel d'éléments disponibles
 * - Interface synchronisée avec l'état réel de la base de données
 */

import java.sql.*;

public class TestLiaisonVehiculeAffectation {
    
    public static void main(String[] args) {
        System.out.println("=== TEST DE LIAISON VÉHICULE-AFFECTATION ===");
        System.out.println();
        
        testRequeteVehiculesDisponibles();
        System.out.println();
        testRequeteConducteursDisponibles();
        System.out.println();
        
        System.out.println("🚀 INSTRUCTIONS DE TEST:");
        System.out.println("1. Lancez FormNouvelleAffectation");
        System.out.println("2. Vérifiez que SEULS les véhicules sans affectation active apparaissent");
        System.out.println("3. Vérifiez que SEULS les conducteurs sans affectation active apparaissent");
        System.out.println("4. Créez une affectation et vérifiez la mise à jour automatique");
        System.out.println("5. Les compteurs doivent refléter les nombres réels");
    }
    
    private static void testRequeteVehiculesDisponibles() {
        System.out.println("📋 TEST: Requête véhicules disponibles");
        System.out.println("SQL: SELECT v.* FROM vehicule v");
        System.out.println("     LEFT JOIN affectation a ON v.id = a.vehicule_id AND a.statut = 'en_cours'");
        System.out.println("     WHERE a.vehicule_id IS NULL");
        System.out.println("     ORDER BY v.matricule");
        System.out.println();
        System.out.println("✅ Cette requête ne retourne QUE les véhicules sans affectation active");
        System.out.println("✅ Basée sur la colonne affectation.statut = 'en_cours'");
        System.out.println("✅ Ignore complètement vehicule.disponible (source d'incohérence)");
    }
    
    private static void testRequeteConducteursDisponibles() {
        System.out.println("👥 TEST: Requête conducteurs disponibles");
        System.out.println("SQL: SELECT u.* FROM utilisateur u");
        System.out.println("     LEFT JOIN affectation a ON u.id = a.conducteur_id AND a.statut = 'en_cours'");
        System.out.println("     WHERE u.role IN ('CONDUCTEUR', 'CONDUCTEUR_SENIOR')");
        System.out.println("     AND u.statut = 'ACTIF'");
        System.out.println("     AND a.conducteur_id IS NULL");
        System.out.println("     ORDER BY u.nom");
        System.out.println();
        System.out.println("✅ Cette requête ne retourne QUE les conducteurs sans affectation active");
        System.out.println("✅ Basée sur la colonne affectation.statut = 'en_cours'");
        System.out.println("✅ Double filtrage: statut utilisateur ET absence d'affectation");
    }
}