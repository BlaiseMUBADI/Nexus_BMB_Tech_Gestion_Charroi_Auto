package nexus_bmb_soft.database.dao;

import nexus_bmb_soft.database.DatabaseConnection;
import nexus_bmb_soft.models.Vehicule;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO (Data Access Object) pour la gestion des véhicules
 * 
 * @author BlaiseMUBADI
 */
public class VehiculeDAO {
    
    private static final Logger LOGGER = Logger.getLogger(VehiculeDAO.class.getName());
    
    /**
     * Ajoute un nouveau véhicule - Version complète avec nouvelle architecture BDD
     */
    public boolean ajouterVehicule(Vehicule vehicule) {
        String sql = "INSERT INTO vehicule (" +
                    "matricule, immatriculation, marque, modele, type, categorie, annee, couleur, " +
                    "numero_chassis, numero_moteur, carburant, consommation_100km, capacite_reservoir, " +
                    "kilometrage_initial, kilometrage_actuel, statut, etat, " +
                    "date_acquisition, prix_acquisition, date_mise_service, date_assurance, " +
                    "compagnie_assurance, police_assurance, date_visite_technique, lieu_visite_technique, " +
                    "date_derniere_vidange, km_derniere_vidange, localisation, notes, actif" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            int paramIndex = 1;
            
            // Champs obligatoires
            pstmt.setString(paramIndex++, vehicule.getMatricule());
            pstmt.setString(paramIndex++, vehicule.getImmatriculation());
            pstmt.setString(paramIndex++, vehicule.getMarque());
            pstmt.setString(paramIndex++, vehicule.getModele());
            pstmt.setString(paramIndex++, vehicule.getType());
            
            // Énumérations - avec gestion null
            pstmt.setString(paramIndex++, vehicule.getCategorie() != null ? vehicule.getCategorie().toString() : null);
            
            // Année nullable
            if (vehicule.getAnnee() != null) {
                pstmt.setInt(paramIndex++, vehicule.getAnnee());
            } else {
                pstmt.setNull(paramIndex++, java.sql.Types.INTEGER);
            }
            
            pstmt.setString(paramIndex++, vehicule.getCouleur());
            pstmt.setString(paramIndex++, vehicule.getNumeroChasssis());
            pstmt.setString(paramIndex++, vehicule.getNumeroMoteur());
            
            pstmt.setString(paramIndex++, vehicule.getCarburant() != null ? vehicule.getCarburant().toString() : null);
            
            // Champs décimaux
            pstmt.setBigDecimal(paramIndex++, vehicule.getConsommation100km());
            pstmt.setBigDecimal(paramIndex++, vehicule.getCapaciteReservoir());
            
            // Kilométrage
            pstmt.setInt(paramIndex++, vehicule.getKilometrageInitial());
            pstmt.setInt(paramIndex++, vehicule.getKilometrageActuel());
            
            // Statut et état
            pstmt.setString(paramIndex++, vehicule.getStatutVehicule() != null ? vehicule.getStatutVehicule().toString() : "DISPONIBLE");
            pstmt.setString(paramIndex++, vehicule.getEtat() != null ? vehicule.getEtat().toString() : "BON");
            
            // Dates - avec gestion null
            pstmt.setDate(paramIndex++, vehicule.getDateAcquisition() != null ? 
                Date.valueOf(vehicule.getDateAcquisition()) : null);
            pstmt.setBigDecimal(paramIndex++, vehicule.getPrixAcquisition());
            pstmt.setDate(paramIndex++, vehicule.getDateMiseService() != null ? 
                Date.valueOf(vehicule.getDateMiseService()) : null);
            pstmt.setDate(paramIndex++, vehicule.getDateAssurance() != null ? 
                Date.valueOf(vehicule.getDateAssurance()) : null);
            
            // Informations d'assurance
            pstmt.setString(paramIndex++, vehicule.getCompagnieAssurance());
            pstmt.setString(paramIndex++, vehicule.getPoliceAssurance());
            
            // Visite technique
            pstmt.setDate(paramIndex++, vehicule.getDateVisiteTechnique() != null ? 
                Date.valueOf(vehicule.getDateVisiteTechnique()) : null);
            pstmt.setString(paramIndex++, vehicule.getLieuVisiteTechnique());
            
            // Vidange
            pstmt.setDate(paramIndex++, vehicule.getDateVidange() != null ? 
                Date.valueOf(vehicule.getDateVidange()) : null);
            if (vehicule.getKmDerniereVidange() != null) {
                pstmt.setInt(paramIndex++, vehicule.getKmDerniereVidange());
            } else {
                pstmt.setNull(paramIndex++, java.sql.Types.INTEGER);
            }
            
            // Informations générales
            pstmt.setString(paramIndex++, vehicule.getLocalisation());
            pstmt.setString(paramIndex++, vehicule.getNotes());
            pstmt.setBoolean(paramIndex++, vehicule.isActif());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Récupérer l'ID généré
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    vehicule.setId(rs.getInt(1));
                }
                LOGGER.info(String.format("✅ Véhicule ajouté: %s (%s)", 
                          vehicule.getMatricule(), vehicule.getMarque()));
                return true;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de l'ajout du véhicule: " + vehicule.getMatricule(), e);
        }
        return false;
    }
    
    /**
     * Récupère tous les véhicules
     */
    public List<Vehicule> getTousVehicules() {
        List<Vehicule> vehicules = new ArrayList<>();
        String sql = "SELECT * FROM vehicule ORDER BY matricule";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Vehicule vehicule = mapResultSetToVehicule(rs);
                vehicules.add(vehicule);
            }
            
            LOGGER.info("📋 " + vehicules.size() + " véhicules récupérés");
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la récupération des véhicules", e);
        }
        
        return vehicules;
    }
    
    /**
     * Récupère uniquement les véhicules réellement disponibles
     * Un véhicule est disponible s'il n'a PAS d'affectation active (statut = 'en_cours')
     */
    public List<Vehicule> getVehiculesDisponibles() {
        List<Vehicule> vehiculesDisponibles = new ArrayList<>();
        
        String sql = "SELECT v.* FROM vehicule v " +
                    "LEFT JOIN affectation a ON v.id = a.vehicule_id AND a.statut = 'en_cours' " +
                    "WHERE a.vehicule_id IS NULL " +
                    "ORDER BY v.matricule";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Vehicule vehicule = mapResultSetToVehicule(rs);
                vehiculesDisponibles.add(vehicule);
            }
            
            LOGGER.info("✅ " + vehiculesDisponibles.size() + " véhicules réellement disponibles récupérés");
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la récupération des véhicules disponibles", e);
        }
        
        return vehiculesDisponibles;
    }
    
    /**
     * Récupère un véhicule par son ID
     */
    public Vehicule getVehiculeParId(int id) {
        String sql = "SELECT * FROM vehicule WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToVehicule(rs);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la récupération du véhicule ID: " + id, e);
        }
        
        return null;
    }
    
    /**
     * Récupère un véhicule par son matricule
     */
    public Vehicule getVehiculeParMatricule(String matricule) {
        String sql = "SELECT * FROM vehicule WHERE matricule = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, matricule);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToVehicule(rs);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la récupération du véhicule: " + matricule, e);
        }
        
        return null;
    }
    
    /**
     * Met à jour un véhicule
     */
    public boolean modifierVehicule(Vehicule vehicule) {
        String sql = "UPDATE vehicule SET matricule = ?, marque = ?, type = ?, annee = ?, " +
                    "statut = ?, date_assurance = ?, date_derniere_vidange = ?, " +
                    "date_visite_technique = ? " +
                    "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, vehicule.getMatricule());
            pstmt.setString(2, vehicule.getMarque());
            pstmt.setString(3, vehicule.getType());
            
            // Gestion de l'année nullable
            if (vehicule.getAnnee() != null) {
                pstmt.setInt(4, vehicule.getAnnee());
            } else {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            }
            
            // Utiliser le statut au lieu du boolean disponible
            String statut = vehicule.isDisponible() ? "DISPONIBLE" : "AFFECTE";
            pstmt.setString(5, statut);
            
            pstmt.setDate(6, vehicule.getDateAssurance() != null ? 
                Date.valueOf(vehicule.getDateAssurance()) : null);
            pstmt.setDate(7, vehicule.getDateVidange() != null ? 
                Date.valueOf(vehicule.getDateVidange()) : null);
            pstmt.setDate(8, vehicule.getDateVisiteTechnique() != null ? 
                Date.valueOf(vehicule.getDateVisiteTechnique()) : null);
            
            pstmt.setInt(9, vehicule.getId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                LOGGER.info("✅ Véhicule modifié: " + vehicule.getMatricule());
                return true;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la modification du véhicule", e);
        }
        return false;
    }
    
    /**
     * Supprime un véhicule
     */
    public boolean supprimerVehicule(int id) {
        String sql = "DELETE FROM vehicule WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                LOGGER.info("✅ Véhicule supprimé ID: " + id);
                return true;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la suppression du véhicule ID: " + id, e);
        }
        return false;
    }
    
    /**
     * Récupère tous les véhicules
     */
    public List<Vehicule> obtenirTousVehicules() {
        List<Vehicule> vehicules = new ArrayList<>();
        String sql = "SELECT * FROM vehicule ORDER BY matricule";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                vehicules.add(mapResultSetToVehicule(rs));
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la récupération de tous les véhicules", e);
        }
        
        return vehicules;
    }
    
    /**
     * Recherche de véhicules par critères
     */
    public List<Vehicule> rechercherVehicules(String critere) {
        List<Vehicule> vehicules = new ArrayList<>();
        String sql = "SELECT * FROM vehicule " +
                    "WHERE matricule LIKE ? OR marque LIKE ? OR type LIKE ? " +
                    "ORDER BY matricule";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchTerm = "%" + critere + "%";
            pstmt.setString(1, searchTerm);
            pstmt.setString(2, searchTerm);
            pstmt.setString(3, searchTerm);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                vehicules.add(mapResultSetToVehicule(rs));
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la recherche de véhicules", e);
        }
        
        return vehicules;
    }
    
    /**
     * Conversion ResultSet vers objet Vehicule
     * Mise à jour pour correspondre à la structure de base de données actuelle
     */
    private Vehicule mapResultSetToVehicule(ResultSet rs) throws SQLException {
        Vehicule vehicule = new Vehicule();
        
        // Champs de base
        vehicule.setId(rs.getInt("id"));
        vehicule.setMatricule(rs.getString("matricule"));
        vehicule.setImmatriculation(rs.getString("immatriculation"));
        vehicule.setMarque(rs.getString("marque"));
        vehicule.setModele(rs.getString("modele"));
        vehicule.setType(rs.getString("type"));
        
        // Gestion de l'année nullable
        int annee = rs.getInt("annee");
        if (!rs.wasNull()) {
            vehicule.setAnnee(annee);
        }
        
        vehicule.setCouleur(rs.getString("couleur"));
        vehicule.setNumeroChasssis(rs.getString("numero_chassis"));
        vehicule.setNumeroMoteur(rs.getString("numero_moteur"));
        
        // Énumérations - avec gestion des valeurs nulles
        String categorieStr = rs.getString("categorie");
        if (categorieStr != null) {
            vehicule.setCategorie(Vehicule.Categorie.valueOf(categorieStr));
        }
        
        String carburantStr = rs.getString("carburant");
        if (carburantStr != null) {
            vehicule.setCarburant(Vehicule.Carburant.valueOf(carburantStr));
        }
        
        String statutStr = rs.getString("statut");
        if (statutStr != null) {
            vehicule.setStatutVehicule(Vehicule.Statut.valueOf(statutStr));
            // Conversion pour compatibilité avec l'ancien système
            vehicule.setDisponible(statutStr.equals("DISPONIBLE"));
        }
        
        String etatStr = rs.getString("etat");
        if (etatStr != null) {
            vehicule.setEtat(Vehicule.Etat.valueOf(etatStr));
        }
        
        // Kilométrage
        vehicule.setKilometrageInitial(rs.getInt("kilometrage_initial"));
        vehicule.setKilometrageActuel(rs.getInt("kilometrage_actuel"));
        
        // Données financières
        BigDecimal consommation = rs.getBigDecimal("consommation_100km");
        if (consommation != null) {
            vehicule.setConsommation100km(consommation);
        }
        
        BigDecimal capacite = rs.getBigDecimal("capacite_reservoir");
        if (capacite != null) {
            vehicule.setCapaciteReservoir(capacite);
        }
        
        BigDecimal prixAcquisition = rs.getBigDecimal("prix_acquisition");
        if (prixAcquisition != null) {
            vehicule.setPrixAcquisition(prixAcquisition);
        }
        
        // Gestion des dates nulles
        Date dateAssurance = rs.getDate("date_assurance");
        if (dateAssurance != null) {
            vehicule.setDateAssurance(dateAssurance.toLocalDate());
        }
        
        Date dateDerniereVidange = rs.getDate("date_derniere_vidange");
        if (dateDerniereVidange != null) {
            vehicule.setDateVidange(dateDerniereVidange.toLocalDate());
        }
        
        Date dateVisiteTechnique = rs.getDate("date_visite_technique");
        if (dateVisiteTechnique != null) {
            vehicule.setDateVisiteTechnique(dateVisiteTechnique.toLocalDate());
        }
        
        Date dateAcquisition = rs.getDate("date_acquisition");
        if (dateAcquisition != null) {
            vehicule.setDateAcquisition(dateAcquisition.toLocalDate());
        }
        
        Date dateMiseService = rs.getDate("date_mise_service");
        if (dateMiseService != null) {
            vehicule.setDateMiseService(dateMiseService.toLocalDate());
        }
        
        // Informations d'assurance
        vehicule.setCompagnieAssurance(rs.getString("compagnie_assurance"));
        vehicule.setPoliceAssurance(rs.getString("police_assurance"));
        
        // Informations techniques
        vehicule.setLieuVisiteTechnique(rs.getString("lieu_visite_technique"));
        int kmDerniereVidange = rs.getInt("km_derniere_vidange");
        if (!rs.wasNull()) {
            vehicule.setKmDerniereVidange(kmDerniereVidange);
        }
        
        // Informations générales
        vehicule.setLocalisation(rs.getString("localisation"));
        vehicule.setNotes(rs.getString("notes"));
        vehicule.setActif(rs.getBoolean("actif"));
        
        int responsableId = rs.getInt("responsable_id");
        if (!rs.wasNull()) {
            vehicule.setResponsableId(responsableId);
        }
        
        return vehicule;
    }
    
    /**
     * Obtient les statistiques des véhicules
     */
    public VehiculeStats getStatistiques() {
        String sql = "SELECT " +
                    "COUNT(*) as total, " +
                    "SUM(CASE WHEN statut = 'DISPONIBLE' THEN 1 ELSE 0 END) as disponibles, " +
                    "SUM(CASE WHEN statut = 'AFFECTE' THEN 1 ELSE 0 END) as affectes " +
                    "FROM vehicule";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return new VehiculeStats(
                    rs.getInt("total"),
                    rs.getInt("disponibles"),
                    rs.getInt("affectes")
                );
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la récupération des statistiques", e);
        }
        
        return new VehiculeStats(0, 0, 0);
    }
    
    /**
     * Classe pour les statistiques des véhicules
     */
    public static class VehiculeStats {
        private final int total;
        private final int disponibles;
        private final int affectes;
        
        public VehiculeStats(int total, int disponibles, int affectes) {
            this.total = total;
            this.disponibles = disponibles;
            this.affectes = affectes;
        }
        
        public int getTotal() { return total; }
        public int getDisponibles() { return disponibles; }
        public int getAffectes() { return affectes; }
        
        @Override
        public String toString() {
            return String.format("Total: %d, Disponibles: %d, Affectés: %d", 
                               total, disponibles, affectes);
        }
    }
    
    /**
     * Vérifie si un véhicule est réellement disponible en se basant sur les affectations actives
     * @param vehiculeId ID du véhicule
     * @return true si le véhicule est disponible
     */
    public boolean estRealementDisponible(int vehiculeId) {
        String sql = "SELECT COUNT(*) FROM affectation " +
                    "WHERE vehicule_id = ? " +
                    "AND date_debut <= CURDATE() " +
                    "AND (date_fin IS NULL OR date_fin >= CURDATE()) " +
                    "AND statut = 'ACTIVE'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, vehiculeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0; // Aucune affectation active
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la vérification de disponibilité réelle", e);
        }
        
        return false; // En cas d'erreur, considérer comme non disponible
    }
    
    /**
     * Met à jour le statut de disponibilité d'un véhicule basé sur ses affectations actives
     * @param vehiculeId ID du véhicule
     */
    public void synchroniserDisponibilite(int vehiculeId) {
        boolean realementDisponible = estRealementDisponible(vehiculeId);
        
        // Utiliser la colonne 'statut' au lieu de 'disponible'
        String nouveauStatut = realementDisponible ? "DISPONIBLE" : "AFFECTE";
        String sql = "UPDATE vehicule SET statut = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nouveauStatut);
            pstmt.setInt(2, vehiculeId);
            
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                LOGGER.info(String.format("✅ Véhicule ID %d synchronisé: %s", 
                          vehiculeId, nouveauStatut));
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la synchronisation de disponibilité", e);
        }
    }
    
    /**
     * Synchronise la disponibilité de tous les véhicules
     */
    public void synchroniserToutesLesDisponibilites() {
        LOGGER.info("🔄 Synchronisation de la disponibilité de tous les véhicules...");
        
        List<Vehicule> vehicules = getTousVehicules();
        for (Vehicule vehicule : vehicules) {
            synchroniserDisponibilite(vehicule.getId());
        }
        
        LOGGER.info("✅ Synchronisation terminée pour " + vehicules.size() + " véhicules");
    }
}