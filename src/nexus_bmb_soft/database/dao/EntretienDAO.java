package nexus_bmb_soft.database.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nexus_bmb_soft.database.DatabaseConnection;
import nexus_bmb_soft.models.Entretien;
import nexus_bmb_soft.models.Vehicule;

/**
 * DAO (Data Access Object) pour la gestion des entretiens
 * Gestion complète du CRUD et des recherches avancées
 * 
 * @author BlaiseMUBADI
 */
public class EntretienDAO {
    
    private static final Logger LOGGER = Logger.getLogger(EntretienDAO.class.getName());
    
    /**
     * Ajoute un nouvel entretien
     */
    public boolean ajouterEntretien(Entretien entretien) {
    String sql = "INSERT INTO entretien (vehicule_id, date_programmee, type_entretien_libre, " +
            "commentaire, cout_prevu, statut, kilometrage) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, entretien.getVehiculeId());
            pstmt.setDate(2, entretien.getDateEntretien() != null ? 
                Date.valueOf(entretien.getDateEntretien()) : Date.valueOf(LocalDate.now()));
            pstmt.setString(3, entretien.getTypeEntretien());
            pstmt.setString(4, entretien.getCommentaire());
            pstmt.setDouble(5, entretien.getCout());
            // Statut: aligné avec l'ENUM de la base ('PLANIFIE','EN_COURS','TERMINE', ...)
            String statut = entretien.getStatut();
            if (statut == null || statut.trim().isEmpty()) {
                statut = "PLANIFIE";
            }
            // Compatibilité avec anciens libellés
            switch (statut.toLowerCase()) {
                case "programme":
                    statut = "PLANIFIE"; break;
                case "en_cours":
                    statut = "EN_COURS"; break;
                case "termine":
                    statut = "TERMINE"; break;
                default:
                    // laisser tel quel si déjà au bon format ou autre statut supporté (ANNULE, REPORTE, ...)
                    break;
            }
            pstmt.setString(6, statut);
            pstmt.setInt(7, entretien.getKilometrage());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Récupérer l'ID généré
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        entretien.setId(rs.getInt(1));
                    }
                }
                LOGGER.info("✅ Entretien ajouté: " + entretien.getTypeEntretien() + 
                           " pour véhicule ID " + entretien.getVehiculeId());
                return true;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de l'ajout de l'entretien", e);
        }
        return false;
    }
    
    /**
     * Récupère un entretien par son ID
     */
    public Entretien obtenirEntretien(int id) {
        String sql = "SELECT e.*, v.matricule, v.marque, v.type " +
                    "FROM entretien e " +
                    "LEFT JOIN vehicule v ON e.vehicule_id = v.id " +
                    "WHERE e.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapperEntretien(rs);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la récupération de l'entretien " + id, e);
        }
        return null;
    }
    
    /**
     * Met à jour un entretien existant
     */
    public boolean modifierEntretien(Entretien entretien) {
        String sql = "UPDATE entretien SET vehicule_id = ?, date_programmee = ?, " +
                    "type_entretien_libre = ?, commentaire = ?, cout_prevu = ?, statut = ?, " +
                    "kilometrage = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, entretien.getVehiculeId());
            pstmt.setDate(2, entretien.getDateEntretien() != null ? 
                Date.valueOf(entretien.getDateEntretien()) : null);
            pstmt.setString(3, entretien.getTypeEntretien());
            pstmt.setString(4, entretien.getCommentaire());
            pstmt.setDouble(5, entretien.getCout());
            // Mapper le statut du modèle vers l'ENUM SQL
            String statut = entretien.getStatut();
            if (statut == null || statut.trim().isEmpty()) {
                statut = "PLANIFIE";
            }
            switch (statut.toLowerCase()) {
                case "programme": statut = "PLANIFIE"; break;
                case "en_cours": statut = "EN_COURS"; break;
                case "termine":  statut = "TERMINE";  break;
                default: /* laisser tel quel */
            }
            pstmt.setString(6, statut);
            pstmt.setInt(7, entretien.getKilometrage());
            pstmt.setInt(8, entretien.getId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                LOGGER.info("✅ Entretien modifié: ID " + entretien.getId());
                return true;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la modification de l'entretien", e);
        }
        return false;
    }
    
    /**
     * Supprime un entretien
     */
    public boolean supprimerEntretien(int id) {
        String sql = "DELETE FROM entretien WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                LOGGER.info("🗑️ Entretien supprimé: ID " + id);
                return true;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la suppression de l'entretien " + id, e);
        }
        return false;
    }
    
    /**
     * Liste tous les entretiens avec limite
     */
    public List<Entretien> listerEntretiens(int limite) {
        String sql = "SELECT e.*, v.matricule, v.marque, v.type " +
                    "FROM entretien e " +
                    "LEFT JOIN vehicule v ON e.vehicule_id = v.id " +
                    "ORDER BY e.date_programmee DESC " +
                    "LIMIT ?";
        
        return executerRequeteEntretiens(sql, limite);
    }
    
    /**
     * Liste tous les entretiens sans limite
     */
    public List<Entretien> listerTousEntretiens() {
        String sql = "SELECT e.*, v.matricule, v.marque, v.type " +
                    "FROM entretien e " +
                    "LEFT JOIN vehicule v ON e.vehicule_id = v.id " +
                    "ORDER BY e.date_programmee DESC";
        
        return executerRequeteEntretiensSansParametre(sql);
    }
    
    /**
     * Recherche des entretiens par véhicule
     */
    public List<Entretien> listerEntretiensParVehicule(int vehiculeId) {
        String sql = "SELECT e.*, v.matricule, v.marque, v.type " +
                    "FROM entretien e " +
                    "LEFT JOIN vehicule v ON e.vehicule_id = v.id " +
                    "WHERE e.vehicule_id = ? " +
                    "ORDER BY e.date_programmee DESC";
        
        return executerRequeteEntretiens(sql, vehiculeId);
    }
    
    /**
     * Recherche des entretiens par type
     */
    public List<Entretien> listerEntretiensParType(String typeEntretien) {
        String sql = "SELECT e.*, v.matricule, v.marque, v.type " +
                    "FROM entretien e " +
                    "LEFT JOIN vehicule v ON e.vehicule_id = v.id " +
                    "WHERE e.type_entretien_libre LIKE ? " +
                    "ORDER BY e.date_programmee DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + typeEntretien + "%");
            return executerRequeteEntretiens(pstmt);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la recherche par type", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Recherche des entretiens par statut
     */
    public List<Entretien> listerEntretiensParStatut(String statut) {
        String sql = "SELECT e.*, v.matricule, v.marque, v.type " +
                    "FROM entretien e " +
                    "LEFT JOIN vehicule v ON e.vehicule_id = v.id " +
                    "WHERE e.statut = ? " +
                    "ORDER BY e.date_programmee DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Mapper statut modèle -> DB
            String statutDb;
            if (statut == null) {
                statutDb = null;
            } else {
                switch (statut.toLowerCase()) {
                    case "programme": statutDb = "PLANIFIE"; break;
                    case "en_cours": statutDb = "EN_COURS"; break;
                    case "termine":  statutDb = "TERMINE";  break;
                    default:           statutDb = statut;      break;
                }
            }
            pstmt.setString(1, statutDb);
            return executerRequeteEntretiens(pstmt);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la recherche par statut", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Recherche des entretiens par période
     */
    public List<Entretien> listerEntretiensParPeriode(LocalDate dateDebut, LocalDate dateFin) {
        String sql = "SELECT e.*, v.matricule, v.marque, v.type " +
                    "FROM entretien e " +
                    "LEFT JOIN vehicule v ON e.vehicule_id = v.id " +
                    "WHERE e.date_programmee BETWEEN ? AND ? " +
                    "ORDER BY e.date_programmee DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(dateDebut));
            pstmt.setDate(2, Date.valueOf(dateFin));
            return executerRequeteEntretiens(pstmt);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la recherche par période", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Recherche multi-critères
     */
    public List<Entretien> rechercherEntretiens(Integer vehiculeId, String typeEntretien, 
                                               String statut, LocalDate dateDebut, LocalDate dateFin) {
        StringBuilder sql = new StringBuilder("SELECT e.*, v.matricule, v.marque, v.type " +
                                            "FROM entretien e " +
                                            "LEFT JOIN vehicule v ON e.vehicule_id = v.id " +
                                            "WHERE 1=1 ");
        
        List<Object> parametres = new ArrayList<>();
        
        if (vehiculeId != null) {
            sql.append("AND e.vehicule_id = ? ");
            parametres.add(vehiculeId);
        }
        
        if (typeEntretien != null && !typeEntretien.trim().isEmpty()) {
            sql.append("AND e.type_entretien_libre LIKE ? ");
            parametres.add("%" + typeEntretien + "%");
        }
        
        if (statut != null && !statut.trim().isEmpty()) {
            sql.append("AND e.statut = ? ");
            // Mapper statut modèle -> DB
            String statutDb;
            switch (statut.toLowerCase()) {
                case "programme": statutDb = "PLANIFIE"; break;
                case "en_cours": statutDb = "EN_COURS"; break;
                case "termine":  statutDb = "TERMINE";  break;
                default:           statutDb = statut;      break;
            }
            parametres.add(statutDb);
        }
        
        if (dateDebut != null) {
            sql.append("AND e.date_programmee >= ? ");
            parametres.add(Date.valueOf(dateDebut));
        }
        
        if (dateFin != null) {
            sql.append("AND e.date_programmee <= ? ");
            parametres.add(Date.valueOf(dateFin));
        }
        
        sql.append("ORDER BY e.date_programmee DESC");
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < parametres.size(); i++) {
                Object param = parametres.get(i);
                if (param instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof String) {
                    pstmt.setString(i + 1, (String) param);
                } else if (param instanceof Date) {
                    pstmt.setDate(i + 1, (Date) param);
                }
            }
            
            return executerRequeteEntretiens(pstmt);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de la recherche multi-critères", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtient les statistiques des entretiens
     */
    public EntretienStats obtenirStatistiques() {
        String sql = "SELECT " +
                    "COUNT(*) as total, " +
                    "SUM(CASE WHEN statut = 'PLANIFIE' THEN 1 ELSE 0 END) as programmes, " +
                    "SUM(CASE WHEN statut = 'EN_COURS' THEN 1 ELSE 0 END) as en_cours, " +
                    "SUM(CASE WHEN statut = 'TERMINE' THEN 1 ELSE 0 END) as termines, " +
                    "SUM(cout_prevu) as cout_total, " +
                    "AVG(cout_prevu) as cout_moyen " +
                    "FROM entretien";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return new EntretienStats(
                    rs.getInt("total"),
                    rs.getInt("programmes"),
                    rs.getInt("en_cours"),
                    rs.getInt("termines"),
                    rs.getDouble("cout_total"),
                    rs.getDouble("cout_moyen")
                );
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors du calcul des statistiques", e);
        }
        
        return new EntretienStats(0, 0, 0, 0, 0.0, 0.0);
    }
    
    // ==================== MÉTHODES PRIVÉES ====================
    
    /**
     * Exécute une requête SQL avec paramètres et retourne une liste d'entretiens
     */
    private List<Entretien> executerRequeteEntretiens(String sql, Object... parametres) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < parametres.length; i++) {
                Object param = parametres[i];
                if (param instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof String) {
                    pstmt.setString(i + 1, (String) param);
                } else if (param instanceof Date) {
                    pstmt.setDate(i + 1, (Date) param);
                }
            }
            
            return executerRequeteEntretiens(pstmt);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de l'exécution de la requête", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Exécute une requête SQL sans paramètres
     */
    private List<Entretien> executerRequeteEntretiensSansParametre(String sql) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            List<Entretien> entretiens = new ArrayList<>();
            while (rs.next()) {
                entretiens.add(mapperEntretien(rs));
            }
            return entretiens;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de l'exécution de la requête", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Exécute une PreparedStatement et retourne une liste d'entretiens
     */
    private List<Entretien> executerRequeteEntretiens(PreparedStatement pstmt) throws SQLException {
        List<Entretien> entretiens = new ArrayList<>();
        
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                entretiens.add(mapperEntretien(rs));
            }
        }
        
        return entretiens;
    }
    
    /**
     * Mappe un ResultSet vers un objet Entretien
     */
    private Entretien mapperEntretien(ResultSet rs) throws SQLException {
        Entretien entretien = new Entretien();
        
        // Données de l'entretien
        entretien.setId(rs.getInt("id"));
        entretien.setVehiculeId(rs.getInt("vehicule_id"));
        
        Date dateEntretien = rs.getDate("date_programmee");
        if (dateEntretien != null) {
            entretien.setDateEntretien(dateEntretien.toLocalDate());
        }
        
        entretien.setTypeEntretien(rs.getString("type_entretien_libre"));
        entretien.setCommentaire(rs.getString("commentaire"));
        entretien.setCout(rs.getDouble("cout_prevu"));
        // Convertir statut DB (ENUM en MAJUSCULES) vers format modèle (minuscule avec underscore)
        String statutDb = rs.getString("statut");
        String statutModele;
        if (statutDb == null) {
            statutModele = null;
        } else {
            switch (statutDb) {
                case "PLANIFIE": statutModele = "programme"; break;
                case "EN_COURS": statutModele = "en_cours"; break;
                case "TERMINE": statutModele = "termine"; break;
                default: statutModele = statutDb; // autres valeurs: ANNULE, REPORTE, etc.
            }
        }
        entretien.setStatut(statutModele);
        entretien.setKilometrage(rs.getInt("kilometrage"));
        
        // Créer l'objet Vehicule avec les données de la jointure (si disponibles)
        try {
            String matricule = rs.getString("matricule");
            if (matricule != null) {
                Vehicule vehicule = new Vehicule();
                vehicule.setId(rs.getInt("vehicule_id"));
                vehicule.setMatricule(matricule);
                vehicule.setMarque(rs.getString("marque"));
                vehicule.setType(rs.getString("type"));
                entretien.setVehicule(vehicule);
            }
        } catch (SQLException e) {
            // Pas de données véhicule dans cette requête, ignorer
        }
        
        return entretien;
    }
    
    /**
     * Classe interne pour les statistiques d'entretiens
     */
    public static class EntretienStats {
        public final int total;
        public final int programmes;
        public final int enCours;
        public final int termines;
        public final double coutTotal;
        public final double coutMoyen;
        
        public EntretienStats(int total, int programmes, int enCours, int termines, 
                             double coutTotal, double coutMoyen) {
            this.total = total;
            this.programmes = programmes;
            this.enCours = enCours;
            this.termines = termines;
            this.coutTotal = coutTotal;
            this.coutMoyen = coutMoyen;
        }
        
        @Override
        public String toString() {
            return String.format("Statistiques Entretiens: Total=%d, Programmés=%d, " +
                               "En cours=%d, Terminés=%d, Coût total=%.2f€, Coût moyen=%.2f€",
                               total, programmes, enCours, termines, coutTotal, coutMoyen);
        }
    }
}