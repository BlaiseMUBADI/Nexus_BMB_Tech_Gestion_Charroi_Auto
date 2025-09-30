package nexus_bmb_soft.database.dao;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import nexus_bmb_soft.database.DatabaseConnection;
import nexus_bmb_soft.models.AlerteEntretien;
import nexus_bmb_soft.models.Vehicule;

/**
 * DAO pour la gestion des alertes d'entretien
 * Système intelligent de notifications
 * 
 * @author BlaiseMUBADI
 */
public class AlerteEntretienDAO {
    
    private VehiculeDAO vehiculeDAO;
    private TypeEntretienDAO typeEntretienDAO;
    
    public AlerteEntretienDAO() {
        this.vehiculeDAO = new VehiculeDAO();
        this.typeEntretienDAO = new TypeEntretienDAO();
    }
    
    /**
     * Génère automatiquement les alertes pour tous les véhicules
     */
    public int genererAlertesAutomatiques() {
        int alertesCreees = 0;
        
        try {
            // Supprimer les anciennes alertes automatiques
            supprimerAlertesExpirees();
            
            // Générer les nouvelles alertes
            List<Vehicule> vehicules = vehiculeDAO.obtenirTousVehicules();
            
            for (Vehicule vehicule : vehicules) {
                alertesCreees += genererAlertesVehicule(vehicule);
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération automatique des alertes: " + e.getMessage());
        }
        
        return alertesCreees;
    }
    
    /**
     * Génère les alertes pour un véhicule spécifique
     */
    public int genererAlertesVehicule(Vehicule vehicule) {
        int alertesCreees = 0;
        LocalDate aujourdhui = LocalDate.now();
        
        try {
            // Supprimer les alertes existantes pour ce véhicule
            supprimerAlertesVehicule(vehicule.getId());
            
            // Alertes assurance
            if (vehicule.getDateAssurance() != null) {
                long jours = ChronoUnit.DAYS.between(aujourdhui, vehicule.getDateAssurance());
                if (jours <= 60) {
                    AlerteEntretien alerte = creerAlerteAssurance(vehicule, jours);
                    if (ajouterAlerte(alerte)) {
                        alertesCreees++;
                    }
                }
            }
            
            // Alertes visite technique
            if (vehicule.getDateVisiteTechnique() != null) {
                long jours = ChronoUnit.DAYS.between(aujourdhui, vehicule.getDateVisiteTechnique());
                if (jours <= 90) {
                    AlerteEntretien alerte = creerAlerteVisiteTechnique(vehicule, jours);
                    if (ajouterAlerte(alerte)) {
                        alertesCreees++;
                    }
                }
            }
            
            // Alertes vidange basées sur kilométrage et date
            alertesCreees += genererAlertesVidange(vehicule);
            
            // Alertes entretiens périodiques
            alertesCreees += genererAlertesPerodiques(vehicule);
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération des alertes pour " + vehicule.getMatricule() + ": " + e.getMessage());
        }
        
        return alertesCreees;
    }
    
    /**
     * Crée une alerte pour l'assurance
     */
    private AlerteEntretien creerAlerteAssurance(Vehicule vehicule, long joursRestants) {
        AlerteEntretien alerte = new AlerteEntretien();
        alerte.setVehiculeId(vehicule.getId());
        alerte.setTypeAlerte(AlerteEntretien.TypeAlerte.ECHEANCE_DATE);
        alerte.setDateEcheance(vehicule.getDateAssurance());
        alerte.setJoursRestants((int) joursRestants);
        
        if (joursRestants <= 0) {
            alerte.setNiveau(AlerteEntretien.Niveau.CRITIQUE);
            alerte.setTitre("ASSURANCE EXPIRÉE");
            alerte.setMessage("L'assurance du véhicule a expiré le " + vehicule.getDateAssurance());
        } else if (joursRestants <= 7) {
            alerte.setNiveau(AlerteEntretien.Niveau.URGENT);
            alerte.setTitre("Assurance expire dans " + joursRestants + " jour(s)");
            alerte.setMessage("Renouveler l'assurance rapidement");
        } else if (joursRestants <= 30) {
            alerte.setNiveau(AlerteEntretien.Niveau.ATTENTION);
            alerte.setTitre("Renouvellement assurance à prévoir");
            alerte.setMessage("L'assurance expire le " + vehicule.getDateAssurance());
        } else {
            alerte.setNiveau(AlerteEntretien.Niveau.INFO);
            alerte.setTitre("Échéance assurance approche");
            alerte.setMessage("Prévoir le renouvellement de l'assurance");
        }
        
        alerte.setActionRecommandee("Contacter la compagnie d'assurance pour le renouvellement");
        
        return alerte;
    }
    
    /**
     * Crée une alerte pour la visite technique
     */
    private AlerteEntretien creerAlerteVisiteTechnique(Vehicule vehicule, long joursRestants) {
        AlerteEntretien alerte = new AlerteEntretien();
        alerte.setVehiculeId(vehicule.getId());
        alerte.setTypeAlerte(AlerteEntretien.TypeAlerte.ECHEANCE_DATE);
        alerte.setDateEcheance(vehicule.getDateVisiteTechnique());
        alerte.setJoursRestants((int) joursRestants);
        
        if (joursRestants <= 0) {
            alerte.setNiveau(AlerteEntretien.Niveau.CRITIQUE);
            alerte.setTitre("VISITE TECHNIQUE EXPIRÉE");
            alerte.setMessage("La visite technique a expiré le " + vehicule.getDateVisiteTechnique());
        } else if (joursRestants <= 15) {
            alerte.setNiveau(AlerteEntretien.Niveau.URGENT);
            alerte.setTitre("Visite technique dans " + joursRestants + " jour(s)");
            alerte.setMessage("Programmer la visite technique rapidement");
        } else if (joursRestants <= 45) {
            alerte.setNiveau(AlerteEntretien.Niveau.ATTENTION);
            alerte.setTitre("Visite technique à programmer");
            alerte.setMessage("Échéance le " + vehicule.getDateVisiteTechnique());
        } else {
            alerte.setNiveau(AlerteEntretien.Niveau.INFO);
            alerte.setTitre("Échéance visite technique approche");
            alerte.setMessage("Préparer la visite technique");
        }
        
        alerte.setActionRecommandee("Prendre rendez-vous pour la visite technique");
        
        return alerte;
    }
    
    /**
     * Génère les alertes de vidange
     */
    private int genererAlertesVidange(Vehicule vehicule) {
        int alertes = 0;
        
        // Alerte basée sur le kilométrage (tous les 5000 km par défaut)
        int kmVidange = 5000;
        int kmActuel = vehicule.getKilometrageActuel();
            int kmDerniereVidange = vehicule.getKmDerniereVidange();        int kmDepuisVidange = kmActuel - kmDerniereVidange;
        int kmRestants = kmVidange - kmDepuisVidange;
        
        if (kmRestants <= 1000) { // Alerte dans les 1000 km
            AlerteEntretien alerte = new AlerteEntretien();
            alerte.setVehiculeId(vehicule.getId());
            alerte.setTypeAlerte(AlerteEntretien.TypeAlerte.ECHEANCE_KM);
            alerte.setKilometrageActuel(kmActuel);
            alerte.setKilometrageEcheance(kmDerniereVidange + kmVidange);
            alerte.setKmRestants(kmRestants);
            
            if (kmRestants <= 0) {
                alerte.setNiveau(AlerteEntretien.Niveau.URGENT);
                alerte.setTitre("VIDANGE DÉPASSÉE");
                alerte.setMessage("Vidange dépassée de " + Math.abs(kmRestants) + " km");
            } else if (kmRestants <= 200) {
                alerte.setNiveau(AlerteEntretien.Niveau.ATTENTION);
                alerte.setTitre("Vidange dans " + kmRestants + " km");
                alerte.setMessage("Programmer la vidange rapidement");
            } else {
                alerte.setNiveau(AlerteEntretien.Niveau.INFO);
                alerte.setTitre("Prochaine vidange dans " + kmRestants + " km");
                alerte.setMessage("Prévoir la vidange");
            }
            
            alerte.setActionRecommandee("Programmer vidange moteur + changement filtre");
            
            if (ajouterAlerte(alerte)) {
                alertes++;
            }
        }
        
        return alertes;
    }
    
    /**
     * Génère les alertes pour entretiens périodiques
     */
    private int genererAlertesPerodiques(Vehicule vehicule) {
        int alertes = 0;
        
        // À implémenter : alertes basées sur la planification des entretiens
        // Nécessite la table planification_entretien
        
        return alertes;
    }
    
    /**
     * Ajoute une alerte dans la base de données
     */
    public boolean ajouterAlerte(AlerteEntretien alerte) {
        String sql = "INSERT INTO alerte_entretien (vehicule_id, type_entretien_id, entretien_id, " +
                     "type_alerte, niveau, titre, message, date_echeance, " +
                     "kilometrage_echeance, kilometrage_actuel, jours_restants, " +
                     "km_restants, action_recommandee) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, alerte.getVehiculeId());
            stmt.setObject(2, alerte.getTypeEntretienId());
            stmt.setObject(3, alerte.getEntretienId());
            stmt.setString(4, alerte.getTypeAlerte().name());
            stmt.setString(5, alerte.getNiveau().name());
            stmt.setString(6, alerte.getTitre());
            stmt.setString(7, alerte.getMessage());
            stmt.setObject(8, alerte.getDateEcheance());
            stmt.setObject(9, alerte.getKilometrageEcheance());
            stmt.setObject(10, alerte.getKilometrageActuel());
            stmt.setObject(11, alerte.getJoursRestants());
            stmt.setObject(12, alerte.getKmRestants());
            stmt.setString(13, alerte.getActionRecommandee());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    alerte.setId(rs.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'alerte: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Obtient toutes les alertes actives
     */
    public List<AlerteEntretien> obtenirAlertesActives() {
        List<AlerteEntretien> alertes = new ArrayList<>();
        String sql = "SELECT a.*, v.matricule, v.marque, v.modele " +
                     "FROM alerte_entretien a " +
                     "JOIN vehicule v ON a.vehicule_id = v.id " +
                     "WHERE a.statut = 'ACTIVE' " +
                     "ORDER BY " +
                     "CASE a.niveau " +
                     "    WHEN 'CRITIQUE' THEN 1 " +
                     "    WHEN 'URGENT' THEN 2 " +
                     "    WHEN 'ATTENTION' THEN 3 " +
                     "    ELSE 4 " +
                     "END, " +
                     "a.jours_restants ASC, " +
                     "a.km_restants ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                AlerteEntretien alerte = mapperResultSetVersAlerte(rs);
                alertes.add(alerte);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des alertes actives: " + e.getMessage());
        }
        
        return alertes;
    }
    
    /**
     * Obtient les alertes par niveau
     */
    public List<AlerteEntretien> obtenirAlertesParNiveau(AlerteEntretien.Niveau niveau) {
        List<AlerteEntretien> alertes = new ArrayList<>();
        String sql = "SELECT a.*, v.matricule, v.marque, v.modele " +
                     "FROM alerte_entretien a " +
                     "JOIN vehicule v ON a.vehicule_id = v.id " +
                     "WHERE a.statut = 'ACTIVE' AND a.niveau = ? " +
                     "ORDER BY a.jours_restants ASC, a.km_restants ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, niveau.name());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                AlerteEntretien alerte = mapperResultSetVersAlerte(rs);
                alertes.add(alerte);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des alertes par niveau: " + e.getMessage());
        }
        
        return alertes;
    }
    
    /**
     * Marque une alerte comme lue
     */
    public boolean marquerCommeLue(int alerteId) {
        String sql = "UPDATE alerte_entretien SET lu = 1 WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, alerteId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors du marquage comme lu: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Traite une alerte (la marque comme traitée)
     */
    public boolean traiterAlerte(int alerteId, int utilisateurId) {
        String sql = "UPDATE alerte_entretien " +
                     "SET statut = 'TRAITEE', traite_par = ?, date_traitement = CURRENT_TIMESTAMP " +
                     "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, utilisateurId);
            stmt.setInt(2, alerteId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors du traitement de l'alerte: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Supprime les alertes expirées
     */
    public int supprimerAlertesExpirees() {
        String sql = "UPDATE alerte_entretien " +
                     "SET statut = 'EXPIREE' " +
                     "WHERE statut = 'ACTIVE' AND date_echeance < DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            return stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression des alertes expirées: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Supprime les alertes d'un véhicule
     */
    private int supprimerAlertesVehicule(int vehiculeId) {
        String sql = "DELETE FROM alerte_entretien WHERE vehicule_id = ? AND statut = 'ACTIVE'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, vehiculeId);
            return stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression des alertes du véhicule: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Mappe un ResultSet vers un objet AlerteEntretien
     */
    private AlerteEntretien mapperResultSetVersAlerte(ResultSet rs) throws SQLException {
        AlerteEntretien alerte = new AlerteEntretien();
        
        alerte.setId(rs.getInt("id"));
        alerte.setVehiculeId(rs.getInt("vehicule_id"));
        alerte.setTypeEntretienId(rs.getObject("type_entretien_id", Integer.class));
        alerte.setEntretienId(rs.getObject("entretien_id", Integer.class));
        alerte.setTypeAlerte(AlerteEntretien.TypeAlerte.valueOf(rs.getString("type_alerte")));
        alerte.setNiveau(AlerteEntretien.Niveau.valueOf(rs.getString("niveau")));
        alerte.setTitre(rs.getString("titre"));
        alerte.setMessage(rs.getString("message"));
        
        Date dateEcheance = rs.getDate("date_echeance");
        if (dateEcheance != null) {
            alerte.setDateEcheance(dateEcheance.toLocalDate());
        }
        
        alerte.setKilometrageEcheance(rs.getObject("kilometrage_echeance", Integer.class));
        alerte.setKilometrageActuel(rs.getObject("kilometrage_actuel", Integer.class));
        alerte.setJoursRestants(rs.getObject("jours_restants", Integer.class));
        alerte.setKmRestants(rs.getObject("km_restants", Integer.class));
        alerte.setStatut(AlerteEntretien.Statut.valueOf(rs.getString("statut")));
        alerte.setLu(rs.getBoolean("lu"));
        alerte.setTraitepar(rs.getObject("traite_par", Integer.class));
        
        Timestamp dateTraitement = rs.getTimestamp("date_traitement");
        if (dateTraitement != null) {
            alerte.setDateTraitement(dateTraitement.toLocalDateTime());
        }
        
        alerte.setActionRecommandee(rs.getString("action_recommandee"));
        alerte.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            alerte.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        // Informations du véhicule si disponibles
        try {
            String matricule = rs.getString("matricule");
            if (matricule != null) {
                Vehicule vehicule = new Vehicule();
                vehicule.setId(alerte.getVehiculeId());
                vehicule.setMatricule(matricule);
                vehicule.setMarque(rs.getString("marque"));
                alerte.setVehicule(vehicule);
            }
        } catch (SQLException e) {
            // Pas grave si les infos véhicule ne sont pas disponibles
        }
        
        return alerte;
    }
}