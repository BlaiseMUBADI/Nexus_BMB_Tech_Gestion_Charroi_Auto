package nexus_bmb_soft.models;

import java.time.LocalDate;

/**
 * Classe modèle pour les véhicules
 * Correspond à la table 'vehicule' de la base de données
 * 
 * @author BlaiseMUBADI
 */
public class Vehicule {
    
    private int id;
    private String matricule;
    private String marque;
    private String type;
    private int annee;
    private boolean disponible;
    private LocalDate dateAssurance;
    private LocalDate dateVidange;
    private LocalDate dateVisiteTechnique;
    
    // Constructeurs
    public Vehicule() {
        this.disponible = true; // Par défaut, un véhicule est disponible
    }
    
    public Vehicule(String matricule, String marque, String type, int annee) {
        this();
        this.matricule = matricule;
        this.marque = marque;
        this.type = type;
        this.annee = annee;
    }
    
    public Vehicule(int id, String matricule, String marque, String type, int annee, 
                   boolean disponible, LocalDate dateAssurance, LocalDate dateVidange, 
                   LocalDate dateVisiteTechnique) {
        this.id = id;
        this.matricule = matricule;
        this.marque = marque;
        this.type = type;
        this.annee = annee;
        this.disponible = disponible;
        this.dateAssurance = dateAssurance;
        this.dateVidange = dateVidange;
        this.dateVisiteTechnique = dateVisiteTechnique;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getMatricule() {
        return matricule;
    }
    
    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }
    
    public String getMarque() {
        return marque;
    }
    
    public void setMarque(String marque) {
        this.marque = marque;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public int getAnnee() {
        return annee;
    }
    
    public void setAnnee(int annee) {
        this.annee = annee;
    }
    
    public boolean isDisponible() {
        return disponible;
    }
    
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
    
    public LocalDate getDateAssurance() {
        return dateAssurance;
    }
    
    public void setDateAssurance(LocalDate dateAssurance) {
        this.dateAssurance = dateAssurance;
    }
    
    public LocalDate getDateVidange() {
        return dateVidange;
    }
    
    public void setDateVidange(LocalDate dateVidange) {
        this.dateVidange = dateVidange;
    }
    
    public LocalDate getDateVisiteTechnique() {
        return dateVisiteTechnique;
    }
    
    public void setDateVisiteTechnique(LocalDate dateVisiteTechnique) {
        this.dateVisiteTechnique = dateVisiteTechnique;
    }
    
    // Méthodes utilitaires
    
    /**
     * Vérifie si le véhicule nécessite une vidange bientôt (dans les 30 prochains jours)
     */
    public boolean vidangeProche() {
        if (dateVidange == null) return false;
        return dateVidange.isBefore(LocalDate.now().plusDays(30));
    }
    
    /**
     * Vérifie si l'assurance va expirer bientôt (dans les 30 prochains jours)
     */
    public boolean assuranceProche() {
        if (dateAssurance == null) return false;
        return dateAssurance.isBefore(LocalDate.now().plusDays(30));
    }
    
    /**
     * Vérifie si la visite technique va expirer bientôt (dans les 30 prochains jours)
     */
    public boolean visiteTechniqueProche() {
        if (dateVisiteTechnique == null) return false;
        return dateVisiteTechnique.isBefore(LocalDate.now().plusDays(30));
    }
    
    /**
     * Retourne le statut général du véhicule
     */
    public String getStatutGeneral() {
        if (!disponible) return "Non disponible";
        if (assuranceProche() || vidangeProche() || visiteTechniqueProche()) {
            return "Maintenance requise";
        }
        return "Opérationnel";
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s %s (%d) - %s", 
                           matricule, marque, type, annee, getStatutGeneral());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vehicule vehicule = (Vehicule) obj;
        return matricule != null ? matricule.equals(vehicule.matricule) : vehicule.matricule == null;
    }
    
    @Override
    public int hashCode() {
        return matricule != null ? matricule.hashCode() : 0;
    }
}