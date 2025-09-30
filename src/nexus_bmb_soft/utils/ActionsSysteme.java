package nexus_bmb_soft.utils;

import javax.swing.JOptionPane;

/**
 * Utilitaires pour les actions système dans les interfaces
 * 
 * @author BlaiseMUBADI
 */
public class ActionsSysteme {
    
    /**
     * Lance une synchronisation manuelle des affectations avec feedback utilisateur
     */
    public static void lancerSynchronisationManuelle() {
        try {
            String rapport = SynchronisateurAffectations.synchroniserMaintenant();
            
            // Afficher le rapport de synchronisation
            JOptionPane.showMessageDialog(null, 
                rapport,
                "✅ Synchronisation Terminée", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "❌ Erreur de synchronisation: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Affiche le statut du synchronisateur automatique
     */
    public static void afficherStatutSynchronisateur() {
        boolean actif = SynchronisateurAffectations.estActif();
        String message = actif ? 
            "✅ Le synchronisateur automatique est ACTIF\n\n" +
            "Les affectations expirées sont automatiquement terminées\n" +
            "et les conducteurs/véhicules sont libérés." :
            "⚠️ Le synchronisateur automatique est INACTIF\n\n" +
            "Les affectations expirées ne sont pas automatiquement gérées.\n" +
            "Vous devez lancer la synchronisation manuellement.";
        
        JOptionPane.showMessageDialog(null, 
            message,
            "Statut du Synchronisateur", 
            actif ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Permet de redémarrer le synchronisateur avec un nouvel intervalle
     */
    public static void configurerSynchronisateur() {
        String[] options = {
            "Très fréquent (1h) - Développement",
            "Fréquent (3h) - Usage intensif", 
            "Normal (6h) - Recommandé",
            "Quotidien (24h) - Usage léger",
            "Arrêter"
        };
        
        int choix = JOptionPane.showOptionDialog(null,
            "Choisissez la fréquence de synchronisation automatique :",
            "Configuration Synchronisateur",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[2]); // Normal par défaut
        
        switch (choix) {
            case 0:
                SynchronisateurAffectations.initialiser(
                    SynchronisateurAffectations.Configuration.TRES_FREQUENT);
                JOptionPane.showMessageDialog(null, 
                    "⏰ Synchronisation configurée : toutes les heures",
                    "Configuration", JOptionPane.INFORMATION_MESSAGE);
                break;
            case 1:
                SynchronisateurAffectations.initialiser(
                    SynchronisateurAffectations.Configuration.FREQUENT);
                JOptionPane.showMessageDialog(null, 
                    "⏰ Synchronisation configurée : toutes les 3 heures",
                    "Configuration", JOptionPane.INFORMATION_MESSAGE);
                break;
            case 2:
                SynchronisateurAffectations.initialiser(
                    SynchronisateurAffectations.Configuration.NORMAL);
                JOptionPane.showMessageDialog(null, 
                    "⏰ Synchronisation configurée : toutes les 6 heures",
                    "Configuration", JOptionPane.INFORMATION_MESSAGE);
                break;
            case 3:
                SynchronisateurAffectations.initialiser(
                    SynchronisateurAffectations.Configuration.QUOTIDIEN);
                JOptionPane.showMessageDialog(null, 
                    "⏰ Synchronisation configurée : quotidienne",
                    "Configuration", JOptionPane.INFORMATION_MESSAGE);
                break;
            case 4:
                SynchronisateurAffectations.arreter();
                JOptionPane.showMessageDialog(null, 
                    "⏹️ Synchronisation automatique arrêtée",
                    "Configuration", JOptionPane.WARNING_MESSAGE);
                break;
        }
    }
}