package nexus_bmb_soft.utils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import nexus_bmb_soft.database.dao.AffectationDAO;

/**
 * Utilitaire pour automatiser la synchronisation des affectations
 * Gère automatiquement :
 * - Le passage des affectations expirées en statut "terminee"
 * - La libération des conducteurs et véhicules
 * - Le démarrage des affectations programmées
 * 
 * @author BlaiseMUBADI
 */
public class SynchronisateurAffectations {
    
    private static final Logger LOGGER = Logger.getLogger(SynchronisateurAffectations.class.getName());
    
    private static Timer timer;
    private static AffectationDAO affectationDAO;
    
    /**
     * Initialise le synchronisateur automatique
     * @param intervalleHeures Intervalle de synchronisation en heures (recommandé: 1-6h)
     */
    public static void initialiser(int intervalleHeures) {
        if (timer != null) {
            timer.cancel(); // Arrêter l'ancien timer s'il existe
        }
        
        affectationDAO = new AffectationDAO();
        timer = new Timer("SynchronisateurAffectations", true); // Daemon thread
        
        // Synchronisation immédiate au démarrage
        LOGGER.info("🚀 Initialisation du synchronisateur d'affectations...");
        synchroniserMaintenant();
        
        // Synchronisation périodique
        long intervalleMs = TimeUnit.HOURS.toMillis(intervalleHeures);
        timer.scheduleAtFixedRate(new TacheSynchronisation(), intervalleMs, intervalleMs);
        
        LOGGER.info("⏰ Synchronisation automatique programmée toutes les " + intervalleHeures + " heures");
    }
    
    /**
     * Effectue une synchronisation immédiate
     * @return Rapport de synchronisation
     */
    public static String synchroniserMaintenant() {
        if (affectationDAO == null) {
            affectationDAO = new AffectationDAO();
        }
        
        try {
            String rapport = affectationDAO.effectuerSynchronisationComplete();
            LOGGER.info("✅ Synchronisation manuelle terminée");
            return rapport;
        } catch (Exception e) {
            String erreur = "❌ Erreur lors de la synchronisation: " + e.getMessage();
            LOGGER.severe(erreur);
            return erreur;
        }
    }
    
    /**
     * Arrête le synchronisateur automatique
     */
    public static void arreter() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            LOGGER.info("⏹️ Synchronisateur d'affectations arrêté");
        }
    }
    
    /**
     * Tâche de synchronisation périodique
     */
    private static class TacheSynchronisation extends TimerTask {
        @Override
        public void run() {
            try {
                LOGGER.info("🔄 Synchronisation automatique en cours...");
                affectationDAO.effectuerSynchronisationComplete();
            } catch (Exception e) {
                LOGGER.severe("❌ Erreur lors de la synchronisation automatique: " + e.getMessage());
            }
        }
    }
    
    /**
     * Vérifie si le synchronisateur est actif
     * @return true si le synchronisateur fonctionne
     */
    public static boolean estActif() {
        return timer != null;
    }
    
    /**
     * Configuration recommandée pour différents types d'usage
     */
    public static class Configuration {
        /** Synchronisation très fréquente - toutes les heures (pour tests/développement) */
        public static final int TRES_FREQUENT = 1;
        
        /** Synchronisation fréquente - toutes les 3 heures (usage intensif) */
        public static final int FREQUENT = 3;
        
        /** Synchronisation normale - toutes les 6 heures (usage standard) */
        public static final int NORMAL = 6;
        
        /** Synchronisation quotidienne - toutes les 24 heures (usage léger) */
        public static final int QUOTIDIEN = 24;
    }
}