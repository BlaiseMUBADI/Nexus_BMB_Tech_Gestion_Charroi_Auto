package nexus_bmb_soft.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modèle pour les alertes d'entretien
 * Système intelligent de notification
 * 
 * @author BlaiseMUBADI
 */
public class AlerteEntretien {
    
    // Énumérations
    public enum TypeAlerte {
        ECHEANCE_KM,     // Échéance basée sur kilométrage
        ECHEANCE_DATE,   // Échéance basée sur date
        RETARD,          // Entretien en retard
        URGENT,          // Situation urgente
        PANNE            // Panne signalée
    }
    
    public enum Niveau {
        INFO,            // Information simple
        ATTENTION,       // Attention requise
        URGENT,          // Action urgente
        CRITIQUE         // Action critique immédiate
    }
    
    public enum Statut {
        ACTIVE,          // Alerte active
        TRAITEE,         // Alerte traitée
        IGNOREE,         // Alerte ignorée volontairement
        EXPIREE          // Alerte expirée
    }
    
    // Champs
    private int id;
    private int vehiculeId;
    private Integer typeEntretienId;
    private Integer entretienId;
    private TypeAlerte typeAlerte;
    private Niveau niveau;
    private String titre;
    private String message;
    private LocalDate dateEcheance;
    private Integer kilometrageEcheance;
    private Integer kilometrageActuel;
    private Integer joursRestants;
    private Integer kmRestants;
    private Statut statut;
    private boolean lu;
    private Integer traitepar;
    private LocalDateTime dateTraitement;
    private String actionRecommandee;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Objets liés
    private Vehicule vehicule;
    private TypeEntretien typeEntretien;
    
    // Constructeurs
    public AlerteEntretien() {
        this.statut = Statut.ACTIVE;
        this.lu = false;
        this.niveau = Niveau.INFO;
    }
    
    public AlerteEntretien(int vehiculeId, TypeAlerte typeAlerte, Niveau niveau, String titre) {
        this();
        this.vehiculeId = vehiculeId;
        this.typeAlerte = typeAlerte;
        this.niveau = niveau;
        this.titre = titre;
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
    
    public Integer getTypeEntretienId() {
        return typeEntretienId;
    }
    
    public void setTypeEntretienId(Integer typeEntretienId) {
        this.typeEntretienId = typeEntretienId;
    }
    
    public Integer getEntretienId() {
        return entretienId;
    }
    
    public void setEntretienId(Integer entretienId) {
        this.entretienId = entretienId;
    }
    
    public TypeAlerte getTypeAlerte() {
        return typeAlerte;
    }
    
    public void setTypeAlerte(TypeAlerte typeAlerte) {
        this.typeAlerte = typeAlerte;
    }
    
    public Niveau getNiveau() {
        return niveau;
    }
    
    public void setNiveau(Niveau niveau) {
        this.niveau = niveau;
    }
    
    public String getTitre() {
        return titre;
    }
    
    public void setTitre(String titre) {
        this.titre = titre;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDate getDateEcheance() {
        return dateEcheance;
    }
    
    public void setDateEcheance(LocalDate dateEcheance) {
        this.dateEcheance = dateEcheance;
    }
    
    public Integer getKilometrageEcheance() {
        return kilometrageEcheance;
    }
    
    public void setKilometrageEcheance(Integer kilometrageEcheance) {
        this.kilometrageEcheance = kilometrageEcheance;
    }
    
    public Integer getKilometrageActuel() {
        return kilometrageActuel;
    }
    
    public void setKilometrageActuel(Integer kilometrageActuel) {
        this.kilometrageActuel = kilometrageActuel;
    }
    
    public Integer getJoursRestants() {
        return joursRestants;
    }
    
    public void setJoursRestants(Integer joursRestants) {
        this.joursRestants = joursRestants;
    }
    
    public Integer getKmRestants() {
        return kmRestants;
    }
    
    public void setKmRestants(Integer kmRestants) {
        this.kmRestants = kmRestants;
    }
    
    public Statut getStatut() {
        return statut;
    }
    
    public void setStatut(Statut statut) {
        this.statut = statut;
    }
    
    public boolean isLu() {
        return lu;
    }
    
    public void setLu(boolean lu) {
        this.lu = lu;
    }
    
    public Integer getTraitepar() {
        return traitepar;
    }
    
    public void setTraitepar(Integer traitepar) {
        this.traitepar = traitepar;
    }
    
    public LocalDateTime getDateTraitement() {
        return dateTraitement;
    }
    
    public void setDateTraitement(LocalDateTime dateTraitement) {
        this.dateTraitement = dateTraitement;
    }
    
    public String getActionRecommandee() {
        return actionRecommandee;
    }
    
    public void setActionRecommandee(String actionRecommandee) {
        this.actionRecommandee = actionRecommandee;
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
    
    public Vehicule getVehicule() {
        return vehicule;
    }
    
    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }
    
    public TypeEntretien getTypeEntretien() {
        return typeEntretien;
    }
    
    public void setTypeEntretien(TypeEntretien typeEntretien) {
        this.typeEntretien = typeEntretien;
    }
    
    // Méthodes utilitaires
    
    /**
     * Retourne l'icône correspondant au niveau
     */
    public String getIconeNiveau() {
        switch (niveau) {
            case INFO: return "ℹ️";
            case ATTENTION: return "⚠️";
            case URGENT: return "🚨";
            case CRITIQUE: return "🔴";
            default: return "❓";
        }
    }
    
    /**
     * Retourne la couleur correspondant au niveau
     */
    public String getCouleurNiveau() {
        switch (niveau) {
            case INFO: return "#17a2b8";       // Bleu info
            case ATTENTION: return "#ffc107";  // Orange
            case URGENT: return "#fd7e14";     // Orange foncé
            case CRITIQUE: return "#dc3545";   // Rouge
            default: return "#6c757d";         // Gris par défaut
        }
    }
    
    /**
     * Retourne l'icône correspondant au type d'alerte
     */
    public String getIconeTypeAlerte() {
        switch (typeAlerte) {
            case ECHEANCE_KM: return "📏";
            case ECHEANCE_DATE: return "📅";
            case RETARD: return "⏰";
            case URGENT: return "🚨";
            case PANNE: return "⚠️";
            default: return "📋";
        }
    }
    
    /**
     * Vérifie si l'alerte est critique (nécessite action immédiate)
     */
    public boolean estCritique() {
        return niveau == Niveau.CRITIQUE || niveau == Niveau.URGENT;
    }
    
    /**
     * Vérifie si l'alerte est expirée
     */
    public boolean estExpiree() {
        if (dateEcheance != null && LocalDate.now().isAfter(dateEcheance)) {
            return true;
        }
        return joursRestants != null && joursRestants < 0;
    }
    
    /**
     * Retourne un message formaté pour affichage
     */
    public String getMessageFormate() {
        StringBuilder sb = new StringBuilder();
        
        if (vehicule != null) {
            sb.append("[").append(vehicule.getMatricule()).append("] ");
        }
        
        sb.append(titre);
        
        if (joursRestants != null) {
            if (joursRestants > 0) {
                sb.append(" - Dans ").append(joursRestants).append(" jour(s)");
            } else if (joursRestants == 0) {
                sb.append(" - AUJOURD'HUI");
            } else {
                sb.append(" - RETARD de ").append(Math.abs(joursRestants)).append(" jour(s)");
            }
        }
        
        if (kmRestants != null) {
            if (kmRestants > 0) {
                sb.append(" - Dans ").append(String.format("%,d", kmRestants)).append(" km");
            } else if (kmRestants <= 0) {
                sb.append(" - DÉPASSÉ de ").append(String.format("%,d", Math.abs(kmRestants))).append(" km");
            }
        }
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return getIconeNiveau() + " " + titre + 
               (vehicule != null ? " [" + vehicule.getMatricule() + "]" : "");
    }
}