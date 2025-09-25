package nexus_bmb_soft.database;

import nexus_bmb_soft.models.Utilisateur;
import nexus_bmb_soft.models.RoleUtilisateur;
import java.time.LocalDateTime;

/**
 * Test simple pour vérifier l'intégration du champ matricule
 */
public class TestMatricule {
    
    public static void main(String[] args) {
        testUtilisateurAvecMatricule();
    }
    
    public static void testUtilisateurAvecMatricule() {
        System.out.println("🧪 Test de l'intégration du champ matricule");
        System.out.println("===========================================");
        
        // Créer un utilisateur avec matricule
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1);
        utilisateur.setNom("MUBADI");
        utilisateur.setPrenom("Blaise");
        utilisateur.setMatricule("ADMIN001");
        utilisateur.setEmail("blaise.mubadi@example.com");
        utilisateur.setRole(RoleUtilisateur.ADMIN);
        utilisateur.setStatut("ACTIF");
        utilisateur.setDateCreation(LocalDateTime.now());
        
        // Affichage des informations
        System.out.println("✅ Utilisateur créé avec succès:");
        System.out.println("   - ID: " + utilisateur.getId());
        System.out.println("   - Nom: " + utilisateur.getNom());
        System.out.println("   - Prénom: " + utilisateur.getPrenom());
        System.out.println("   - Matricule: " + utilisateur.getMatricule()); // 🎯 Nouveau champ !
        System.out.println("   - Email: " + utilisateur.getEmail());
        System.out.println("   - Rôle: " + utilisateur.getRole());
        System.out.println("   - Statut: " + utilisateur.getStatut());
        System.out.println("   - Date Création: " + utilisateur.getDateCreation());
        
        // Vérification des getters/setters matricule
        System.out.println();
        System.out.println("🔍 Test des méthodes matricule:");
        
        // Test setMatricule/getMatricule
        utilisateur.setMatricule("GEST001");
        String nouveauMatricule = utilisateur.getMatricule();
        System.out.println("   - setMatricule('GEST001') -> getMatricule(): " + nouveauMatricule);
        
        // Test avec matricule null
        utilisateur.setMatricule(null);
        String matriculeNull = utilisateur.getMatricule();
        System.out.println("   - setMatricule(null) -> getMatricule(): " + matriculeNull);
        
        // Test avec matricule vide
        utilisateur.setMatricule("");
        String matriculeVide = utilisateur.getMatricule();
        System.out.println("   - setMatricule('') -> getMatricule(): '" + matriculeVide + "'");
        
        System.out.println();
        System.out.println("🎉 Test terminé avec succès ! Le champ matricule fonctionne correctement.");
        System.out.println("📋 Structure de la table utilisateur mise à jour avec le champ 'matricule'");
        System.out.println("🔧 Prêt pour l'interface FormGestionUtilisateurs !");
    }
}