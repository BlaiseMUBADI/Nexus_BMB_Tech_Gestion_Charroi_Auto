package nexus_bmb_soft.models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Classe modèle pour les affectations de véhicules
 * Correspond à la table 'affectation' de la base de données
 * 
 * @author BlaiseMUBADI
 */
public class Affectation {
    
    private int id;
    private int vehiculeId;
    private int conducteurId;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String motif;
    
    // Objets liés (pour les jointures)
    private Vehicule vehicule;
    private Utilisateur conducteur;
    
    // Constructeurs
    public Affectation() {
    }
    
    public Affectation(int vehiculeId, int conducteurId, LocalDate dateDebut, String motif) {
        this.vehiculeId = vehiculeId;
        this.conducteurId = conducteurId;
        this.dateDebut = dateDebut;
        this.motif = motif;
    }
    
    public Affectation(int id, int vehiculeId, int conducteurId, LocalDate dateDebut, 
                      LocalDate dateFin, String motif) {
        this.id = id;
        this.vehiculeId = vehiculeId;
        this.conducteurId = conducteurId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.motif = motif;
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
    
    public int getConducteurId() {
        return conducteurId;
    }
    
    public void setConducteurId(int conducteurId) {
        this.conducteurId = conducteurId;
    }
    
    public LocalDate getDateDebut() {
        return dateDebut;
    }
    
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public LocalDate getDateFin() {
        return dateFin;
    }
    
    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
    
    public String getMotif() {
        return motif;
    }
    
    public void setMotif(String motif) {
        this.motif = motif;
    }
    
    public Vehicule getVehicule() {
        return vehicule;
    }
    
    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }
    
    public Utilisateur getConducteur() {
        return conducteur;
    }
    
    public void setConducteur(Utilisateur conducteur) {
        this.conducteur = conducteur;
    }
    
    // Méthodes utilitaires
    
    /**
     * Vérifie si l'affectation est actuellement active
     */
    public boolean isActive() {
        LocalDate maintenant = LocalDate.now();
        return dateDebut != null && 
               !dateDebut.isAfter(maintenant) && 
               (dateFin == null || !dateFin.isBefore(maintenant));
    }
    
    /**
     * Vérifie si l'affectation est terminée
     */
    public boolean isTerminee() {
        return dateFin != null && dateFin.isBefore(LocalDate.now());
    }
    
    /**
     * Vérifie si l'affectation est programmée pour le futur
     */
    public boolean isProgrammee() {
        return dateDebut != null && dateDebut.isAfter(LocalDate.now());
    }
    
    /**
     * Calcule la durée de l'affectation en jours
     */
    public long getDureeDansJours() {
        if (dateDebut == null) return 0;
        
        LocalDate dateFinal = dateFin != null ? dateFin : LocalDate.now();
        return ChronoUnit.DAYS.between(dateDebut, dateFinal) + 1;
    }
    
    /**
     * Retourne le statut de l'affectation
     */
    public String getStatut() {
        if (isProgrammee()) return "Programmée";
        if (isActive()) return "En cours";
        if (isTerminee()) return "Terminée";
        return "Indéterminé";
    }
    
    /**
     * Retourne une description courte de l'affectation
     */
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        
        if (vehicule != null) {
            sb.append(vehicule.getMatricule()).append(" - ");
        }
        
        if (conducteur != null) {
            sb.append(conducteur.getNom());
        }
        
        if (motif != null && !motif.trim().isEmpty()) {
            sb.append(" (").append(motif).append(")");
        }
        
        return sb.toString();
    }
    
    /**
     * Termine l'affectation à la date actuelle
     */
    public void terminer() {
        this.dateFin = LocalDate.now();
    }
    
    /**
     * Termine l'affectation à une date spécifique
     */
    public void terminer(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
    
    @Override
    public String toString() {
        return String.format("Affectation %d: %s [%s - %s] - %s", 
                           id, 
                           getDescription(),
                           dateDebut != null ? dateDebut.toString() : "?",
                           dateFin != null ? dateFin.toString() : "En cours",
                           getStatut());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Affectation affectation = (Affectation) obj;
        return id == affectation.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}