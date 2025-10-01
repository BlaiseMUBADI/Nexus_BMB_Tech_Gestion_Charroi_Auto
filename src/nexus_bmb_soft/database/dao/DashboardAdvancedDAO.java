package nexus_bmb_soft.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nexus_bmb_soft.database.DatabaseConnection;

/**
 * DAO avancé pour les statistiques temporelles et tendances du dashboard
 * 
 * @author BlaiseMUBADI
 */
public class DashboardAdvancedDAO {
    
    private static final Logger LOGGER = Logger.getLogger(DashboardAdvancedDAO.class.getName());
    
    /**
     * Classe pour représenter un point de données temporel
     */
    public static class DataPoint {
        public LocalDate date;
        public String label;
        public int value;
        public double percentage;
        
        public DataPoint(LocalDate date, String label, int value) {
            this.date = date;
            this.label = label;
            this.value = value;
        }
        
        public DataPoint(String label, int value, double percentage) {
            this.label = label;
            this.value = value;
            this.percentage = percentage;
        }
    }
    
    /**
     * Statistiques détaillées par type de véhicule
     */
    public Map<String, Integer> getVehiculesByType() {
        Map<String, Integer> result = new HashMap<>();
        String sql = "SELECT type_vehicule, COUNT(*) as count FROM vehicule GROUP BY type_vehicule";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                result.put(rs.getString("type_vehicule"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Erreur lors de la récupération des véhicules par type", e);
        }
        return result;
    }
    
    /**
     * Évolution des affectations sur les 7 derniers jours
     */
    public List<DataPoint> getAffectationsTrend7Days() {
        List<DataPoint> trend = new ArrayList<>();
        String sql = """
            SELECT DATE(date_debut) as jour, COUNT(*) as count
            FROM affectation 
            WHERE date_debut >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
            GROUP BY DATE(date_debut)
            ORDER BY jour
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                LocalDate date = rs.getDate("jour").toLocalDate();
                int count = rs.getInt("count");
                trend.add(new DataPoint(date, date.toString(), count));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Erreur lors de la récupération des tendances d'affectations", e);
        }
        return trend;
    }
    
    /**
     * Évolution des entretiens sur les 30 derniers jours
     */
    public List<DataPoint> getEntretiensTrend30Days() {
        List<DataPoint> trend = new ArrayList<>();
        String sql = """
            SELECT DATE(date_programmee) as jour, COUNT(*) as count
            FROM entretien 
            WHERE date_programmee >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
            GROUP BY DATE(date_programmee)
            ORDER BY jour
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                LocalDate date = rs.getDate("jour").toLocalDate();
                int count = rs.getInt("count");
                trend.add(new DataPoint(date, date.toString(), count));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Erreur lors de la récupération des tendances d'entretiens", e);
        }
        return trend;
    }
    
    /**
     * Répartition des entretiens par type avec pourcentages
     */
    public List<DataPoint> getEntretiensParType() {
        List<DataPoint> distribution = new ArrayList<>();
        String sql = """
            SELECT 
                COALESCE(te.nom, e.type_entretien_libre, 'Autre') as type_entretien,
                COUNT(*) as count,
                ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM entretien), 1) as percentage
            FROM entretien e
            LEFT JOIN type_entretien te ON e.type_entretien_id = te.id
            GROUP BY COALESCE(te.nom, e.type_entretien_libre, 'Autre')
            ORDER BY count DESC
            LIMIT 10
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String type = rs.getString("type_entretien");
                int count = rs.getInt("count");
                double percentage = rs.getDouble("percentage");
                distribution.add(new DataPoint(type, count, percentage));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Erreur lors de la récupération des entretiens par type", e);
        }
        return distribution;
    }
    
    /**
     * Top 5 des véhicules les plus utilisés (par nombre d'affectations)
     */
    public List<DataPoint> getTop5VehiculesUtilises() {
        List<DataPoint> top5 = new ArrayList<>();
        String sql = """
            SELECT 
                v.matricule,
                v.modele,
                COUNT(a.id) as nb_affectations
            FROM vehicule v
            LEFT JOIN affectation a ON v.id = a.vehicule_id
            GROUP BY v.id, v.matricule, v.modele
            ORDER BY nb_affectations DESC
            LIMIT 5
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String label = rs.getString("matricule") + " (" + rs.getString("modele") + ")";
                int count = rs.getInt("nb_affectations");
                top5.add(new DataPoint(null, label, count));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Erreur lors de la récupération du top 5 des véhicules", e);
        }
        return top5;
    }
    
    /**
     * Alertes par priorité/urgence
     */
    public Map<String, Integer> getAlertesByUrgence() {
        Map<String, Integer> result = new HashMap<>();
        String sql = """
            SELECT 
                CASE 
                    WHEN DATEDIFF(date_echeance, CURDATE()) <= 0 THEN 'URGENT'
                    WHEN DATEDIFF(date_echeance, CURDATE()) <= 7 THEN 'PROCHE'
                    ELSE 'NORMAL'
                END as urgence,
                COUNT(*) as count
            FROM alerte_entretien 
            WHERE statut = 'ACTIVE'
            GROUP BY urgence
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                result.put(rs.getString("urgence"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Erreur lors de la récupération des alertes par urgence", e);
        }
        return result;
    }
    
    /**
     * Statistiques comparative du mois actuel vs mois précédent
     */
    public Map<String, Map<String, Integer>> getComparaisonMensuelle() {
        Map<String, Map<String, Integer>> comparison = new HashMap<>();
        
        // Affectations
        Map<String, Integer> affectations = new HashMap<>();
        affectations.put("ce_mois", getCountForCurrentMonth("affectation", "date_debut"));
        affectations.put("mois_precedent", getCountForPreviousMonth("affectation", "date_debut"));
        comparison.put("affectations", affectations);
        
        // Entretiens
        Map<String, Integer> entretiens = new HashMap<>();
        entretiens.put("ce_mois", getCountForCurrentMonth("entretien", "date_programmee"));
        entretiens.put("mois_precedent", getCountForPreviousMonth("entretien", "date_programmee"));
        comparison.put("entretiens", entretiens);
        
        // Alertes créées
        Map<String, Integer> alertes = new HashMap<>();
        alertes.put("ce_mois", getCountForCurrentMonth("alerte_entretien", "date_creation"));
        alertes.put("mois_precedent", getCountForPreviousMonth("alerte_entretien", "date_creation"));
        comparison.put("alertes", alertes);
        
        return comparison;
    }
    
    private int getCountForCurrentMonth(String table, String dateColumn) {
        String sql = String.format(
            "SELECT COUNT(*) FROM %s WHERE MONTH(%s) = MONTH(CURDATE()) AND YEAR(%s) = YEAR(CURDATE())",
            table, dateColumn, dateColumn
        );
        return executeCountQuery(sql);
    }
    
    private int getCountForPreviousMonth(String table, String dateColumn) {
        String sql = String.format(
            "SELECT COUNT(*) FROM %s WHERE MONTH(%s) = MONTH(DATE_SUB(CURDATE(), INTERVAL 1 MONTH)) AND YEAR(%s) = YEAR(DATE_SUB(CURDATE(), INTERVAL 1 MONTH))",
            table, dateColumn, dateColumn
        );
        return executeCountQuery(sql);
    }
    
    private int executeCountQuery(String sql) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Erreur lors de l'exécution de la requête: " + sql, e);
        }
        return 0;
    }
    
    /**
     * Prochaines échéances d'entretien (7 prochains jours)
     */
    public List<DataPoint> getProchaines7Echeances() {
        List<DataPoint> echeances = new ArrayList<>();
        String sql = """
            SELECT 
                v.matricule,
                e.type_entretien,
                e.date_programmee
            FROM entretien e
            JOIN vehicule v ON e.vehicule_id = v.id
            WHERE e.statut = 'PLANIFIE' 
            AND e.date_programmee BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)
            ORDER BY e.date_programmee
            LIMIT 10
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                LocalDate date = rs.getDate("date_programmee").toLocalDate();
                String label = rs.getString("matricule") + " - " + rs.getString("type_entretien");
                echeances.add(new DataPoint(date, label, 1));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Erreur lors de la récupération des prochaines échéances", e);
        }
        return echeances;
    }
}