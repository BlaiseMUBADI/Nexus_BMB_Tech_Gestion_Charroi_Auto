package nexus_bmb_soft.database.dao;

import nexus_bmb_soft.database.DatabaseConnection;
import nexus_bmb_soft.models.Vehicule;
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
     * Ajoute un nouveau véhicule
     */
    public boolean ajouterVehicule(Vehicule vehicule) {
        String sql = "INSERT INTO vehicule (matricule, marque, type, annee, disponible, " +
                    "date_assurance, date_vidange, date_visite_technique) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, vehicule.getMatricule());
            pstmt.setString(2, vehicule.getMarque());
            pstmt.setString(3, vehicule.getType());
            
            // Gestion de l'année nullable
            if (vehicule.getAnnee() != null) {
                pstmt.setInt(4, vehicule.getAnnee());
            } else {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            }
            
            pstmt.setBoolean(5, vehicule.isDisponible());
            
            // Gestion des dates nulles
            pstmt.setDate(6, vehicule.getDateAssurance() != null ? 
                Date.valueOf(vehicule.getDateAssurance()) : null);
            pstmt.setDate(7, vehicule.getDateVidange() != null ? 
                Date.valueOf(vehicule.getDateVidange()) : null);
            pstmt.setDate(8, vehicule.getDateVisiteTechnique() != null ? 
                Date.valueOf(vehicule.getDateVisiteTechnique()) : null);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Récupérer l'ID généré
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    vehicule.setId(rs.getInt(1));
                }
                LOGGER.info("✅ Véhicule ajouté: " + vehicule.getMatricule());
                return true;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de l'ajout du véhicule", e);
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
                    "disponible = ?, date_assurance = ?, date_vidange = ?, " +
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
            
            pstmt.setBoolean(5, vehicule.isDisponible());
            
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
     * Récupère les véhicules disponibles
     */
    public List<Vehicule> getVehiculesDisponibles() {
        List<Vehicule> vehicules = new ArrayList<>();
        String sql = "SELECT * FROM vehicule WHERE disponible = TRUE ORDER BY matricule";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                vehicules.add(mapResultSetToVehicule(rs));
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la récupération des véhicules disponibles", e);
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
     */
    private Vehicule mapResultSetToVehicule(ResultSet rs) throws SQLException {
        Vehicule vehicule = new Vehicule();
        vehicule.setId(rs.getInt("id"));
        vehicule.setMatricule(rs.getString("matricule"));
        vehicule.setMarque(rs.getString("marque"));
        vehicule.setType(rs.getString("type"));
        
        // Gestion de l'année nullable
        int annee = rs.getInt("annee");
        if (!rs.wasNull()) {
            vehicule.setAnnee(annee);
        }
        
        vehicule.setDisponible(rs.getBoolean("disponible"));
        
        // Gestion des dates nulles
        Date dateAssurance = rs.getDate("date_assurance");
        if (dateAssurance != null) {
            vehicule.setDateAssurance(dateAssurance.toLocalDate());
        }
        
        Date dateVidange = rs.getDate("date_vidange");
        if (dateVidange != null) {
            vehicule.setDateVidange(dateVidange.toLocalDate());
        }
        
        Date dateVisiteTechnique = rs.getDate("date_visite_technique");
        if (dateVisiteTechnique != null) {
            vehicule.setDateVisiteTechnique(dateVisiteTechnique.toLocalDate());
        }
        
        return vehicule;
    }
    
    /**
     * Obtient les statistiques des véhicules
     */
    public VehiculeStats getStatistiques() {
        String sql = "SELECT " +
                    "COUNT(*) as total, " +
                    "SUM(CASE WHEN disponible = TRUE THEN 1 ELSE 0 END) as disponibles, " +
                    "SUM(CASE WHEN disponible = FALSE THEN 1 ELSE 0 END) as affectes " +
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
}