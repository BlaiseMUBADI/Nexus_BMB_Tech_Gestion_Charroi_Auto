package nexus_bmb_soft.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modèle pour les types d'entretiens
 * Système professionnel de maintenance périodique
 * 
 * @author BlaiseMUBADI
 */
public class TypeEntretien {
    
    // Énumérations
    public enum Categorie {
        PREVENTIF,    // Entretien préventif (révisions, vidanges...)
        CURATIF,      // Entretien curatif (réparations de pannes)
        OBLIGATOIRE   // Entretiens obligatoires (visite technique, assurance...)
    }
    
    public enum Priorite {
        BASSE,        // Peut être reporté
        NORMALE,      // Priorité normale
        HAUTE,        // Important, à faire rapidement
        CRITIQUE      // Critique, véhicule immobilisé si non fait
    }
    
    // Champs
    private int id;
    private String nom;
    private String description;
    private Categorie categorie;
    private Integer periodiciteKm;      // Périodicité en kilomètres
    private Integer periodiciteMois;    // Périodicité en mois
    private BigDecimal coutEstime;
    private Integer dureeEstimeeHeures;
    private Priorite priorite;
    private boolean actif;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructeurs
    public TypeEntretien() {
        this.actif = true;
        this.priorite = Priorite.NORMALE;
    }
    
    public TypeEntretien(String nom, Categorie categorie) {
        this();
        this.nom = nom;
        this.categorie = categorie;
    }
    
    public TypeEntretien(String nom, String description, Categorie categorie, 
                        Integer periodiciteKm, Integer periodiciteMois) {
        this(nom, categorie);
        this.description = description;
        this.periodiciteKm = periodiciteKm;
        this.periodiciteMois = periodiciteMois;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Categorie getCategorie() {
        return categorie;
    }
    
    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }
    
    public Integer getPeriodiciteKm() {
        return periodiciteKm;
    }
    
    public void setPeriodiciteKm(Integer periodiciteKm) {
        this.periodiciteKm = periodiciteKm;
    }
    
    public Integer getPeriodiciteMois() {
        return periodiciteMois;
    }
    
    public void setPeriodiciteMois(Integer periodiciteMois) {
        this.periodiciteMois = periodiciteMois;
    }
    
    public BigDecimal getCoutEstime() {
        return coutEstime;
    }
    
    public void setCoutEstime(BigDecimal coutEstime) {
        this.coutEstime = coutEstime;
    }
    
    public Integer getDureeEstimeeHeures() {
        return dureeEstimeeHeures;
    }
    
    public void setDureeEstimeeHeures(Integer dureeEstimeeHeures) {
        this.dureeEstimeeHeures = dureeEstimeeHeures;
    }
    
    public Priorite getPriorite() {
        return priorite;
    }
    
    public void setPriorite(Priorite priorite) {
        this.priorite = priorite;
    }
    
    public boolean isActif() {
        return actif;
    }
    
    public void setActif(boolean actif) {
        this.actif = actif;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Méthodes utilitaires
    
    /**
     * Vérifie si ce type d'entretien est périodique
     */
    public boolean estPeriodique() {
        return periodiciteKm != null || periodiciteMois != null;
    }
    
    /**
     * Retourne la périodicité la plus restrictive en jours
     */
    public Integer getPeriodiciteJours() {
        if (periodiciteMois != null) {
            return periodiciteMois * 30; // Approximation
        }
        return null;
    }
    
    /**
     * Retourne l'affichage de la périodicité
     */
    public String getAffichagePeriocicite() {
        StringBuilder sb = new StringBuilder();
        
        if (periodiciteKm != null) {
            sb.append("Tous les ").append(String.format("%,d", periodiciteKm)).append(" km");
        }
        
        if (periodiciteMois != null) {
            if (sb.length() > 0) {
                sb.append(" ou ");
            }
            sb.append("Tous les ").append(periodiciteMois).append(" mois");
        }
        
        if (sb.length() == 0) {
            sb.append("Selon nécessité");
        }
        
        return sb.toString();
    }
    
    /**
     * Retourne l'affichage du coût estimé
     */
    public String getAffichageCout() {
        if (coutEstime != null) {
            return String.format("%.2f €", coutEstime);
        }
        return "Non estimé";
    }
    
    /**
     * Retourne l'affichage de la durée
     */
    public String getAffichageDuree() {
        if (dureeEstimeeHeures != null) {
            if (dureeEstimeeHeures == 0) {
                return "Administratif";
            } else if (dureeEstimeeHeures == 1) {
                return "1 heure";
            } else {
                return dureeEstimeeHeures + " heures";
            }
        }
        return "Non estimée";
    }
    
    /**
     * Retourne l'icône correspondant à la catégorie
     */
    public String getIconeCategorie() {
        return switch (categorie) {
            case PREVENTIF -> "🔧";
            case CURATIF -> "🚨";
            case OBLIGATOIRE -> "📋";
        };
    }
    
    /**
     * Retourne la couleur correspondant à la priorité
     */
    public String getCouleurPriorite() {
        return switch (priorite) {
            case BASSE -> "#28a745";      // Vert
            case NORMALE -> "#007bff";    // Bleu
            case HAUTE -> "#ffc107";      // Orange
            case CRITIQUE -> "#dc3545";   // Rouge
        };
    }
    
    @Override
    public String toString() {
        return nom + " (" + categorie + " - " + priorite + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        TypeEntretien that = (TypeEntretien) obj;
        return id == that.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}