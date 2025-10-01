package nexus_bmb_soft.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nexus_bmb_soft.database.DatabaseConnection;

/**
 * DAO pour les statistiques du tableau de bord.
 * Requêtes simples de comptage pour alimenter l'écran Dashboard.
 */
public class DashboardDAO {

    private static final Logger LOGGER = Logger.getLogger(DashboardDAO.class.getName());

    private int count(String sql) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Erreur lors du comptage SQL: " + sql, e);
        }
        return 0;
    }

    public int getTotalVehicules() {
        return count("SELECT COUNT(*) FROM vehicule");
    }

    public int getVehiculesDisponibles() {
        // ENUM DB en majuscules
        return count("SELECT COUNT(*) FROM vehicule WHERE statut = 'DISPONIBLE'");
    }

    public int getVehiculesMaintenance() {
        return count("SELECT COUNT(*) FROM vehicule WHERE statut = 'MAINTENANCE'");
    }

    public int getAffectationsActives() {
        // Dans bdd_charroi_auto.sql: enum('programmee','en_cours','terminee')
        return count("SELECT COUNT(*) FROM affectation WHERE statut = 'en_cours'");
    }

    public int getEntretiensPlanifies() {
        // Dans bdd_charroi_auto.sql: ENUM('PLANIFIE','EN_ATTENTE','EN_COURS','TERMINE','ANNULE','REPORTE')
        return count("SELECT COUNT(*) FROM entretien WHERE statut = 'PLANIFIE'");
    }

    public int getAlertesActives() {
        // Table alerte_entretien: statut ENUM('ACTIVE','TRAITEE','IGNOREE','EXPIREE')
        return count("SELECT COUNT(*) FROM alerte_entretien WHERE statut = 'ACTIVE'");
    }
    
    // Nouvelles métriques pour les graphiques modernes
    
    public int getVehiculesEnMission() {
        // Véhicules actuellement affectés (ni disponibles, ni en maintenance)
        return count("SELECT COUNT(*) FROM vehicule WHERE statut NOT IN ('DISPONIBLE', 'MAINTENANCE')");
    }
    
    public int getEntretiensEnCours() {
        return count("SELECT COUNT(*) FROM entretien WHERE statut = 'EN_COURS'");
    }
    
    public int getEntretiensTermines() {
        return count("SELECT COUNT(*) FROM entretien WHERE statut = 'TERMINE'");
    }
    
    public int getAffectationsProgrammees() {
        return count("SELECT COUNT(*) FROM affectation WHERE statut = 'programmee'");
    }
    
    public int getAffectationsTerminees() {
        return count("SELECT COUNT(*) FROM affectation WHERE statut = 'terminee'");
    }
    
    public int getTotalConducteurs() {
        return count("SELECT COUNT(*) FROM utilisateur WHERE role = 'CONDUCTEUR'");
    }
    
    public int getTotalGestionnaires() {
        return count("SELECT COUNT(*) FROM utilisateur WHERE role = 'GESTIONNAIRE'");
    }
    
    // Métriques pour les graphiques temporels (ce mois)
    
    public int getEntretiensCeMois() {
        return count("SELECT COUNT(*) FROM entretien WHERE MONTH(date_programmee) = MONTH(CURDATE()) AND YEAR(date_programmee) = YEAR(CURDATE())");
    }
    
    public int getAffectationsCeMois() {
        return count("SELECT COUNT(*) FROM affectation WHERE MONTH(date_debut) = MONTH(CURDATE()) AND YEAR(date_debut) = YEAR(CURDATE())");
    }
    
    public int getAlertesTraiteesCeMois() {
        return count("SELECT COUNT(*) FROM alerte_entretien WHERE statut = 'TRAITEE' AND MONTH(date_creation) = MONTH(CURDATE()) AND YEAR(date_creation) = YEAR(CURDATE())");
    }
    
    // Métriques par type de véhicule/entretien
    
    public int getVehiculesByType(String type) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM vehicule WHERE type_vehicule = ?")) {
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Erreur lors du comptage par type: " + type, e);
        }
        return 0;
    }
    
    public int getEntretiensByType(String type) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM entretien WHERE type_entretien = ?")) {
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Erreur lors du comptage entretiens par type: " + type, e);
        }
        return 0;
    }
    
    // Calculs de pourcentages pour les barres de progression
    
    public double getTauxDisponibilite() {
        int total = getTotalVehicules();
        if (total == 0) return 0.0;
        return (double) getVehiculesDisponibles() / total * 100;
    }
    
    public double getTauxMaintenance() {
        int total = getTotalVehicules();
        if (total == 0) return 0.0;
        return (double) getVehiculesMaintenance() / total * 100;
    }
    
    public double getTauxUtilisation() {
        int total = getTotalVehicules();
        if (total == 0) return 0.0;
        return (double) getVehiculesEnMission() / total * 100;
    }
}
