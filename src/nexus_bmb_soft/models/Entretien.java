package nexus_bmb_soft.models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Classe modèle pour les entretiens de véhicules
 * Correspond à la table 'entretien' de la base de données
 * 
 * @author BlaiseMUBADI
 */
public class Entretien {
    
    private int id;
    private int vehiculeId;
    private LocalDate dateEntretien;
    private String typeEntretien;
    private String commentaire;
    private double cout;
    private String statut; // programme, en_cours, termine
    private int kilometrage; // Kilométrage au moment de l'entretien
    
    // Objet lié (pour les jointures)
    private Vehicule vehicule;
    
    // Constructeurs
    public Entretien() {
    }
    
    public Entretien(int vehiculeId, LocalDate dateEntretien, String typeEntretien) {
        this.vehiculeId = vehiculeId;
        this.dateEntretien = dateEntretien;
        this.typeEntretien = typeEntretien;
    }
    
    public Entretien(int id, int vehiculeId, LocalDate dateEntretien, 
                    String typeEntretien, String commentaire) {
        this.id = id;
        this.vehiculeId = vehiculeId;
        this.dateEntretien = dateEntretien;
        this.typeEntretien = typeEntretien;
        this.commentaire = commentaire;
    }
    
    /**
     * Constructeur complet avec tous les champs
     */
    public Entretien(int id, int vehiculeId, LocalDate dateEntretien, 
                    String typeEntretien, String commentaire, double cout, 
                    String statut, int kilometrage) {
        this.id = id;
        this.vehiculeId = vehiculeId;
        this.dateEntretien = dateEntretien;
        this.typeEntretien = typeEntretien;
        this.commentaire = commentaire;
        this.cout = cout;
        this.statut = statut;
        this.kilometrage = kilometrage;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getVehiculeId() {
        return vehiculeId;
    }
    
    public void setVehiculeId(int vehiculeId) {
        this.vehiculeId = vehiculeId;
    }
    
    public LocalDate getDateEntretien() {
        return dateEntretien;
    }
    
    public void setDateEntretien(LocalDate dateEntretien) {
        this.dateEntretien = dateEntretien;
    }
    
    public String getTypeEntretien() {
        return typeEntretien;
    }
    
    public void setTypeEntretien(String typeEntretien) {
        this.typeEntretien = typeEntretien;
    }
    
    public String getCommentaire() {
        return commentaire;
    }
    
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
    
    public Vehicule getVehicule() {
        return vehicule;
    }
    
    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }
    
    public double getCout() {
        return cout;
    }
    
    public void setCout(double cout) {
        this.cout = cout;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public int getKilometrage() {
        return kilometrage;
    }
    
    public void setKilometrage(int kilometrage) {
        this.kilometrage = kilometrage;
    }
    
    // Méthodes utilitaires
    
    /**
     * Vérifie si l'entretien est récent (moins de 30 jours)
     */
    public boolean isRecent() {
        if (dateEntretien == null) return false;
        return ChronoUnit.DAYS.between(dateEntretien, LocalDate.now()) <= 30;
    }
    
    /**
     * Vérifie si l'entretien est ancien (plus de 365 jours)
     */
    public boolean isAncien() {
        if (dateEntretien == null) return false;
        return ChronoUnit.DAYS.between(dateEntretien, LocalDate.now()) > 365;
    }
    
    /**
     * Calcule le nombre de jours depuis l'entretien
     */
    public long getJoursDepuis() {
        if (dateEntretien == null) return -1;
        return ChronoUnit.DAYS.between(dateEntretien, LocalDate.now());
    }
    
    /**
     * Vérifie si c'est un entretien majeur
     */
    public boolean isMajeur() {
        if (typeEntretien == null) return false;
        String type = typeEntretien.toLowerCase();
        return type.contains("révision") || 
               type.contains("réparation") || 
               type.contains("moteur") ||
               type.contains("transmission") ||
               type.contains("freins");
    }
    
    /**
     * Vérifie si c'est un entretien de routine
     */
    public boolean isRoutine() {
        if (typeEntretien == null) return false;
        String type = typeEntretien.toLowerCase();
        return type.contains("vidange") || 
               type.contains("pneus") || 
               type.contains("lavage") ||
               type.contains("vérification");
    }
    
    /**
     * Vérifie si l'entretien est programmé
     */
    public boolean isProgramme() {
        return "programme".equals(statut);
    }
    
    /**
     * Vérifie si l'entretien est en cours
     */
    public boolean isEnCours() {
        return "en_cours".equals(statut);
    }
    
    /**
     * Vérifie si l'entretien est terminé
     */
    public boolean isTermine() {
        return "termine".equals(statut);
    }
    
    /**
     * Retourne le statut formaté pour l'affichage
     */
    public String getStatutAffichage() {
        if (statut == null) return "Non défini";
        switch (statut) {
            case "programme":
                return "Programmé";
            case "en_cours":
                return "En cours";
            case "termine":
                return "Terminé";
            default:
                return statut;
        }
    }
    
    /**
     * Retourne le coût formaté pour l'affichage
     */
    public String getCoutFormate() {
        if (cout <= 0) return "Non défini";
        return String.format("%.2f €", cout);
    }
    
    /**
     * Retourne le niveau de priorité de l'entretien
     */
    public String getNiveauPriorite() {
        if (isMajeur()) return "Haute";
        if (isRoutine()) return "Normale";
        return "Faible";
    }
    
    /**
     * Retourne une description courte de l'entretien
     */
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        
        if (vehicule != null) {
            sb.append(vehicule.getMatricule()).append(" - ");
        }
        
        if (typeEntretien != null) {
            sb.append(typeEntretien);
        }
        
        if (dateEntretien != null) {
            sb.append(" (").append(dateEntretien).append(")");
        }
        
        return sb.toString();
    }
    
    /**
     * Retourne un résumé détaillé pour l'affichage
     */
    public String getResume() {
        StringBuilder sb = new StringBuilder();
        sb.append(getDescription());
        
        if (commentaire != null && !commentaire.trim().isEmpty()) {
            sb.append("\nCommentaire: ").append(commentaire);
        }
        
        sb.append("\nPriorité: ").append(getNiveauPriorite());
        
        long jours = getJoursDepuis();
        if (jours >= 0) {
            if (jours == 0) {
                sb.append(" - Aujourd'hui");
            } else if (jours == 1) {
                sb.append(" - Hier");
            } else {
                sb.append(" - Il y a ").append(jours).append(" jours");
            }
        }
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return String.format("Entretien %d: %s - %s [%s]", 
                           id, 
                           typeEntretien != null ? typeEntretien : "Non spécifié",
                           dateEntretien != null ? dateEntretien.toString() : "Date inconnue",
                           getNiveauPriorite());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Entretien entretien = (Entretien) obj;
        return id == entretien.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}