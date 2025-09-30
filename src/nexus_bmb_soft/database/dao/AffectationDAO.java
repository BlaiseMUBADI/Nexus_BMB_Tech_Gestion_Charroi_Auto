package nexus_bmb_soft.database.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nexus_bmb_soft.database.DatabaseConnection;
import nexus_bmb_soft.models.Affectation;
import nexus_bmb_soft.models.Utilisateur;
import nexus_bmb_soft.models.Vehicule;

/**
 * DAO (Data Access Object) pour la gestion des affectations
 * Fournit toutes les opérations CRUD pour les affectations véhicule-conducteur
 * 
 * @author BlaiseMUBADI
 */
public class AffectationDAO {
    
    private static final Logger LOGGER = Logger.getLogger(AffectationDAO.class.getName());
    
    /**
     * Crée une nouvelle affectation
     * @param affectation L'affectation à créer
     * @return true si la création a réussi, false sinon
     */
    public boolean creer(Affectation affectation) {
        // Vérifier la disponibilité du véhicule avant de créer l'affectation
        if (!verifierDisponibiliteVehicule(affectation.getVehiculeId(), affectation.getDateDebut(), affectation.getDateFin())) {
            LOGGER.warning("Véhicule " + affectation.getVehiculeId() + " non disponible pour la période demandée");
            return false;
        }
        
        String sql = "INSERT INTO affectation (vehicule_id, conducteur_id, date_debut, date_fin, motif, statut) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, affectation.getVehiculeId());
            pstmt.setInt(2, affectation.getConducteurId());
            pstmt.setDate(3, Date.valueOf(affectation.getDateDebut()));
            
            if (affectation.getDateFin() != null) {
                pstmt.setDate(4, Date.valueOf(affectation.getDateFin()));
            } else {
                pstmt.setNull(4, Types.DATE);
            }
            
            pstmt.setString(5, affectation.getMotif());
            
            // Déterminer le statut automatiquement
            String statut = determinerStatut(affectation.getDateDebut(), affectation.getDateFin());
            pstmt.setString(6, statut);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Récupérer l'ID généré
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        affectation.setId(generatedKeys.getInt(1));
                        LOGGER.info("✅ Affectation créée avec ID: " + affectation.getId());
                        return true;
                    }
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la création de l'affectation", e);
        }
        
        return false;
    }
    
    /**
     * Récupère toutes les affectations actives (en cours)
     * @return Liste des affectations actives avec véhicules et conducteurs
     */
    public List<Affectation> listerActives() {
        String sql = "SELECT a.*, v.matricule, v.marque, v.type, " +
                    "u.nom, u.prenom, u.matricule as conducteur_matricule " +
                    "FROM affectation a " +
                    "INNER JOIN vehicule v ON a.vehicule_id = v.id " +
                    "INNER JOIN utilisateur u ON a.conducteur_id = u.id " +
                    "WHERE a.date_debut <= CURDATE() AND (a.date_fin IS NULL OR a.date_fin >= CURDATE()) " +
                    "ORDER BY a.date_debut DESC";
        
        return executerRequeteAffectations(sql);
    }
    
    /**
     * Récupère toutes les affectations programmées (futures)
     * @return Liste des affectations programmées
     */
    public List<Affectation> listerProgrammees() {
        String sql = "SELECT a.*, v.matricule, v.marque, v.type, " +
                    "u.nom, u.prenom, u.matricule as conducteur_matricule " +
                    "FROM affectation a " +
                    "INNER JOIN vehicule v ON a.vehicule_id = v.id " +
                    "INNER JOIN utilisateur u ON a.conducteur_id = u.id " +
                    "WHERE a.date_debut > CURDATE() " +
                    "ORDER BY a.date_debut ASC";
        
        return executerRequeteAffectations(sql);
    }
    
    /**
     * Récupère l'historique complet des affectations
     * @param limite Nombre maximum d'enregistrements (0 = pas de limite)
     * @return Liste de l'historique des affectations
     */
    public List<Affectation> listerHistorique(int limite) {
        String sql = "SELECT a.*, v.matricule, v.marque, v.type, " +
                    "u.nom, u.prenom, u.matricule as conducteur_matricule " +
                    "FROM affectation a " +
                    "INNER JOIN vehicule v ON a.vehicule_id = v.id " +
                    "INNER JOIN utilisateur u ON a.conducteur_id = u.id " +
                    "ORDER BY a.date_debut DESC";
        
        if (limite > 0) {
            sql += " LIMIT " + limite;
        }
        
        return executerRequeteAffectations(sql);
    }
    
    /**
     * Recherche des affectations par conducteur
     * @param conducteurId ID du conducteur
     * @return Liste des affectations du conducteur
     */
    public List<Affectation> listerParConducteur(int conducteurId) {
        String sql = "SELECT a.*, v.matricule, v.marque, v.type, " +
                    "u.nom, u.prenom, u.matricule as conducteur_matricule " +
                    "FROM affectation a " +
                    "INNER JOIN vehicule v ON a.vehicule_id = v.id " +
                    "INNER JOIN utilisateur u ON a.conducteur_id = u.id " +
                    "WHERE a.conducteur_id = ? " +
                    "ORDER BY a.date_debut DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, conducteurId);
            return executerRequeteAffectations(pstmt);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la recherche par conducteur", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Recherche des affectations par véhicule
     * @param vehiculeId ID du véhicule
     * @return Liste des affectations du véhicule
     */
    public List<Affectation> listerParVehicule(int vehiculeId) {
        String sql = "SELECT a.*, v.matricule, v.marque, v.type, " +
                    "u.nom, u.prenom, u.matricule as conducteur_matricule " +
                    "FROM affectation a " +
                    "INNER JOIN vehicule v ON a.vehicule_id = v.id " +
                    "INNER JOIN utilisateur u ON a.conducteur_id = u.id " +
                    "WHERE a.vehicule_id = ? " +
                    "ORDER BY a.date_debut DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, vehiculeId);
            return executerRequeteAffectations(pstmt);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la recherche par véhicule", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Recherche des affectations par période
     * @param dateDebut Date de début de la période
     * @param dateFin Date de fin de la période
     * @return Liste des affectations dans la période
     */
    public List<Affectation> listerParPeriode(LocalDate dateDebut, LocalDate dateFin) {
        String sql = "SELECT a.*, v.matricule, v.marque, v.type, " +
                    "u.nom, u.prenom, u.matricule as conducteur_matricule " +
                    "FROM affectation a " +
                    "INNER JOIN vehicule v ON a.vehicule_id = v.id " +
                    "INNER JOIN utilisateur u ON a.conducteur_id = u.id " +
                    "WHERE a.date_debut <= ? AND (a.date_fin IS NULL OR a.date_fin >= ?) " +
                    "ORDER BY a.date_debut DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(dateFin));
            pstmt.setDate(2, Date.valueOf(dateDebut));
            return executerRequeteAffectations(pstmt);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la recherche par période", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Termine une affectation
     * @param affectationId ID de l'affectation à terminer
     * @param dateFin Date de fin (si null, utilise la date actuelle)
     * @return true si la terminaison a réussi
     */
    public boolean terminer(int affectationId, LocalDate dateFin) {
        if (dateFin == null) {
            dateFin = LocalDate.now();
        }
        
        String sql = "UPDATE affectation SET date_fin = ?, statut = 'terminee' WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(dateFin));
            pstmt.setInt(2, affectationId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                LOGGER.info("✅ Affectation " + affectationId + " terminée le " + dateFin);
                return true;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la terminaison de l'affectation " + affectationId, e);
        }
        
        return false;
    }
    
    /**
     * Obtient les statistiques des affectations
     * @return Map contenant les statistiques
     */
    public Map<String, Object> obtenirStatistiques() {
        Map<String, Object> stats = new HashMap<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            // Affectations actives
            String sqlActives = "SELECT COUNT(*) FROM affectation WHERE date_debut <= CURDATE() AND (date_fin IS NULL OR date_fin >= CURDATE())";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlActives);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("affectationsActives", rs.getInt(1));
                }
            }
            
            // Affectations programmées
            String sqlProgrammees = "SELECT COUNT(*) FROM affectation WHERE date_debut > CURDATE()";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlProgrammees);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("affectationsProgrammees", rs.getInt(1));
                }
            }
            
            // Affectations terminées cette semaine
            String sqlSemaine = "SELECT COUNT(*) FROM affectation WHERE date_fin >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND date_fin <= CURDATE()";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlSemaine);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("affectationsTermineesSemaine", rs.getInt(1));
                }
            }
            
            // Total des affectations
            String sqlTotal = "SELECT COUNT(*) FROM affectation";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlTotal);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("totalAffectations", rs.getInt(1));
                }
            }
            
            // Durée moyenne des affectations terminées
            String sqlDureeMoyenne = "SELECT AVG(DATEDIFF(date_fin, date_debut)) FROM affectation WHERE date_fin IS NOT NULL";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDureeMoyenne);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("dureeMoyenneJours", Math.round(rs.getDouble(1) * 100.0) / 100.0);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors du calcul des statistiques", e);
        }
        
        return stats;
    }
    
    /**
     * Vérifie si un véhicule est disponible pour une période donnée
     * @param vehiculeId ID du véhicule
     * @param dateDebut Date de début souhaitée
     * @param dateFin Date de fin souhaitée (peut être null)
     * @return true si le véhicule est disponible
     */
    public boolean verifierDisponibiliteVehicule(int vehiculeId, LocalDate dateDebut, LocalDate dateFin) {
        String sql = "SELECT COUNT(*) FROM affectation " +
                    "WHERE vehicule_id = ? " +
                    "AND date_debut <= ? " +
                    "AND (date_fin IS NULL OR date_fin >= ?) " +
                    "AND id != IFNULL(?, 0)"; // Pour exclure l'affectation en cours de modification
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, vehiculeId);
            pstmt.setDate(2, Date.valueOf(dateFin != null ? dateFin : dateDebut.plusDays(365))); // Si pas de date fin, on vérifie sur 1 an
            pstmt.setDate(3, Date.valueOf(dateDebut));
            pstmt.setNull(4, Types.INTEGER); // Pour les nouvelles affectations
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0; // Aucune affectation en conflit
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la vérification de disponibilité", e);
        }
        
        return false;
    }
    
    // Méthodes utilitaires privées
    
    /**
     * Détermine le statut d'une affectation selon ses dates
     */
    private String determinerStatut(LocalDate dateDebut, LocalDate dateFin) {
        LocalDate maintenant = LocalDate.now();
        
        if (dateDebut.isAfter(maintenant)) {
            return "programmee";
        } else if (dateFin == null || !dateFin.isBefore(maintenant)) {
            return "en_cours";
        } else {
            return "terminee";
        }
    }
    
    /**
     * Exécute une requête SQL et retourne une liste d'affectations
     */
    private List<Affectation> executerRequeteAffectations(String sql) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            return executerRequeteAffectations(pstmt);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de l'exécution de la requête", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Exécute une PreparedStatement et retourne une liste d'affectations
     */
    private List<Affectation> executerRequeteAffectations(PreparedStatement pstmt) throws SQLException {
        List<Affectation> affectations = new ArrayList<>();
        
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Affectation affectation = new Affectation();
                
                // Données de l'affectation
                affectation.setId(rs.getInt("id"));
                affectation.setVehiculeId(rs.getInt("vehicule_id"));
                affectation.setConducteurId(rs.getInt("conducteur_id"));
                
                Date dateDebut = rs.getDate("date_debut");
                if (dateDebut != null) {
                    affectation.setDateDebut(dateDebut.toLocalDate());
                }
                
                Date dateFin = rs.getDate("date_fin");
                if (dateFin != null) {
                    affectation.setDateFin(dateFin.toLocalDate());
                }
                
                affectation.setMotif(rs.getString("motif"));
                
                // Créer l'objet Vehicule avec les données de la jointure
                try {
                    Vehicule vehicule = new Vehicule();
                    vehicule.setId(rs.getInt("vehicule_id"));
                    vehicule.setMatricule(rs.getString("matricule"));
                    vehicule.setMarque(rs.getString("marque"));
                    vehicule.setType(rs.getString("type"));
                    affectation.setVehicule(vehicule);
                } catch (SQLException e) {
                    // Pas de données véhicule dans cette requête
                }
                
                // Créer l'objet Utilisateur avec les données de la jointure
                try {
                    Utilisateur conducteur = new Utilisateur();
                    conducteur.setId(rs.getInt("conducteur_id"));
                    conducteur.setNom(rs.getString("nom"));
                    conducteur.setPrenom(rs.getString("prenom"));
                    conducteur.setMatricule(rs.getString("conducteur_matricule"));
                    affectation.setConducteur(conducteur);
                } catch (SQLException e) {
                    // Pas de données conducteur dans cette requête
                }
                
                affectations.add(affectation);
            }
        }
        
        return affectations;
    }
}