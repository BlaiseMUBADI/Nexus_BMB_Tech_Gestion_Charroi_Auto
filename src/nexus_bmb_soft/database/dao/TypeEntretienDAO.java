package nexus_bmb_soft.database.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import nexus_bmb_soft.database.DatabaseConnection;
import nexus_bmb_soft.models.TypeEntretien;

/**
 * DAO pour la gestion des types d'entretiens
 * Système professionnel de maintenance périodique
 * 
 * @author BlaiseMUBADI
 */
public class TypeEntretienDAO {
    
    /**
     * Ajouter un nouveau type d'entretien
     */
    public boolean ajouterTypeEntretien(TypeEntretien type) {
        String sql = "INSERT INTO type_entretien (nom, description, categorie, periodicite_km, " +
                     "periodicite_mois, cout_estime, duree_estimee_heures, priorite) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, type.getNom());
            stmt.setString(2, type.getDescription());
            stmt.setString(3, type.getCategorie().name());
            stmt.setObject(4, type.getPeriodiciteKm());
            stmt.setObject(5, type.getPeriodiciteMois());
            stmt.setBigDecimal(6, type.getCoutEstime());
            stmt.setObject(7, type.getDureeEstimeeHeures());
            stmt.setString(8, type.getPriorite().name());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    type.setId(rs.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du type d'entretien: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Obtenir tous les types d'entretiens actifs
     */
    public List<TypeEntretien> listerTypesEntretien() {
        List<TypeEntretien> types = new ArrayList<>();
        String sql = "SELECT id, nom, description, categorie, periodicite_km, periodicite_mois, " +
                     "cout_estime, duree_estimee_heures, priorite, actif, created_at " +
                     "FROM type_entretien " +
                     "WHERE actif = 1 " +
                     "ORDER BY categorie, priorite DESC, nom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                TypeEntretien type = new TypeEntretien();
                type.setId(rs.getInt("id"));
                type.setNom(rs.getString("nom"));
                type.setDescription(rs.getString("description"));
                type.setCategorie(TypeEntretien.Categorie.valueOf(rs.getString("categorie")));
                type.setPeriodiciteKm(rs.getObject("periodicite_km", Integer.class));
                type.setPeriodiciteMois(rs.getObject("periodicite_mois", Integer.class));
                type.setCoutEstime(rs.getBigDecimal("cout_estime"));
                type.setDureeEstimeeHeures(rs.getObject("duree_estimee_heures", Integer.class));
                type.setPriorite(TypeEntretien.Priorite.valueOf(rs.getString("priorite")));
                type.setActif(rs.getBoolean("actif"));
                type.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                
                types.add(type);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des types d'entretien: " + e.getMessage());
        }
        
        return types;
    }
    
    /**
     * Obtenir les types d'entretiens par catégorie
     */
    public List<TypeEntretien> listerTypesParCategorie(TypeEntretien.Categorie categorie) {
        List<TypeEntretien> types = new ArrayList<>();
        String sql = "SELECT id, nom, description, categorie, periodicite_km, periodicite_mois, " +
                     "cout_estime, duree_estimee_heures, priorite, actif " +
                     "FROM type_entretien " +
                     "WHERE actif = 1 AND categorie = ? " +
                     "ORDER BY priorite DESC, nom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categorie.name());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                TypeEntretien type = new TypeEntretien();
                type.setId(rs.getInt("id"));
                type.setNom(rs.getString("nom"));
                type.setDescription(rs.getString("description"));
                type.setCategorie(TypeEntretien.Categorie.valueOf(rs.getString("categorie")));
                type.setPeriodiciteKm(rs.getObject("periodicite_km", Integer.class));
                type.setPeriodiciteMois(rs.getObject("periodicite_mois", Integer.class));
                type.setCoutEstime(rs.getBigDecimal("cout_estime"));
                type.setDureeEstimeeHeures(rs.getObject("duree_estimee_heures", Integer.class));
                type.setPriorite(TypeEntretien.Priorite.valueOf(rs.getString("priorite")));
                type.setActif(rs.getBoolean("actif"));
                
                types.add(type);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des types par catégorie: " + e.getMessage());
        }
        
        return types;
    }
    
    /**
     * Obtenir les types d'entretiens périodiques (avec périodicité)
     */
    public List<TypeEntretien> listerTypesPeriodiques() {
        List<TypeEntretien> types = new ArrayList<>();
        String sql = "SELECT id, nom, description, categorie, periodicite_km, periodicite_mois, " +
                     "cout_estime, duree_estimee_heures, priorite " +
                     "FROM type_entretien " +
                     "WHERE actif = 1 AND (periodicite_km IS NOT NULL OR periodicite_mois IS NOT NULL) " +
                     "ORDER BY priorite DESC, periodicite_km ASC, periodicite_mois ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                TypeEntretien type = new TypeEntretien();
                type.setId(rs.getInt("id"));
                type.setNom(rs.getString("nom"));
                type.setDescription(rs.getString("description"));
                type.setCategorie(TypeEntretien.Categorie.valueOf(rs.getString("categorie")));
                type.setPeriodiciteKm(rs.getObject("periodicite_km", Integer.class));
                type.setPeriodiciteMois(rs.getObject("periodicite_mois", Integer.class));
                type.setCoutEstime(rs.getBigDecimal("cout_estime"));
                type.setDureeEstimeeHeures(rs.getObject("duree_estimee_heures", Integer.class));
                type.setPriorite(TypeEntretien.Priorite.valueOf(rs.getString("priorite")));
                
                types.add(type);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des types périodiques: " + e.getMessage());
        }
        
        return types;
    }
    
    /**
     * Obtenir un type d'entretien par ID
     */
    public TypeEntretien obtenirTypeEntretien(int id) {
        String sql = "SELECT id, nom, description, categorie, periodicite_km, periodicite_mois, " +
                     "cout_estime, duree_estimee_heures, priorite, actif, created_at " +
                     "FROM type_entretien " +
                     "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                TypeEntretien type = new TypeEntretien();
                type.setId(rs.getInt("id"));
                type.setNom(rs.getString("nom"));
                type.setDescription(rs.getString("description"));
                type.setCategorie(TypeEntretien.Categorie.valueOf(rs.getString("categorie")));
                type.setPeriodiciteKm(rs.getObject("periodicite_km", Integer.class));
                type.setPeriodiciteMois(rs.getObject("periodicite_mois", Integer.class));
                type.setCoutEstime(rs.getBigDecimal("cout_estime"));
                type.setDureeEstimeeHeures(rs.getObject("duree_estimee_heures", Integer.class));
                type.setPriorite(TypeEntretien.Priorite.valueOf(rs.getString("priorite")));
                type.setActif(rs.getBoolean("actif"));
                type.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                
                return type;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du type d'entretien: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Modifier un type d'entretien
     */
    public boolean modifierTypeEntretien(TypeEntretien type) {
        String sql = "UPDATE type_entretien " +
                     "SET nom = ?, description = ?, categorie = ?, periodicite_km = ?, " +
                     "periodicite_mois = ?, cout_estime = ?, duree_estimee_heures = ?, priorite = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, type.getNom());
            stmt.setString(2, type.getDescription());
            stmt.setString(3, type.getCategorie().name());
            stmt.setObject(4, type.getPeriodiciteKm());
            stmt.setObject(5, type.getPeriodiciteMois());
            stmt.setBigDecimal(6, type.getCoutEstime());
            stmt.setObject(7, type.getDureeEstimeeHeures());
            stmt.setString(8, type.getPriorite().name());
            stmt.setInt(9, type.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du type d'entretien: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Désactiver un type d'entretien (soft delete)
     */
    public boolean desactiverTypeEntretien(int id) {
        String sql = "UPDATE type_entretien SET actif = 0 WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la désactivation du type d'entretien: " + e.getMessage());
        }
        
        return false;
    }
}