package nexus_bmb_soft.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

/**
 * Classe modèle pour les véhicules - Version enrichie
 * Correspond à la table 'vehicule' de la base de données
 * 
 * @author BlaiseMUBADI
 */
public class Vehicule {
    
    // Énumérations
    public enum Statut {
        DISPONIBLE, AFFECTE, MAINTENANCE, HORS_SERVICE, VENDU
    }
    
    public enum Etat {
        EXCELLENT, BON, MOYEN, MAUVAIS, CRITIQUE
    }
    
    public enum Categorie {
        LEGER, UTILITAIRE, POIDS_LOURD, SPECIAL
    }
    
    public enum Carburant {
        ESSENCE, DIESEL, HYBRIDE, ELECTRIQUE, GAZ
    }
    
    // Champs de base
    private int id;
    private String matricule;
    private String immatriculation;
    private String marque;
    private String modele;
    private String type;
    private Categorie categorie;
    private Integer annee;
    private String couleur;
    private String numeroChasssis;
    private String numeroMoteur;
    private Carburant carburant;
    private BigDecimal consommation100km;
    private BigDecimal capaciteReservoir;
    
    // Kilométrage
    private int kilometrageInitial;
    private int kilometrageActuel;
    private LocalDateTime kilometrageDerniereMaj;
    
    // Statut et état
    private Statut statut;
    private Etat etat;
    private boolean disponible; // Maintenu pour compatibilité
    
    // Dates importantes
    private LocalDate dateAcquisition;
    private BigDecimal prixAcquisition;
    private LocalDate dateMiseService;
    private LocalDate dateAssurance;
    private String compagnieAssurance;
    private String policeAssurance;
    private LocalDate dateVisiteTechnique;
    private String lieuVisiteTechnique;
    private LocalDate dateDerniereVidange;
    private Integer kmDerniereVidange;
    
    // Autres
    private String localisation;
    private Integer responsableId;
    private String notes;
    private boolean actif;
    
    // Nouveaux champs professionnels
    private int periodiciteVidange; // Périodicité en kilomètres
    private Statut statutVehicule;
    private Etat etatVehicule;
    private Categorie categorieVehicule;
    private Carburant typeCarburant;
    private String numeroSerie;
    private LocalDate dateMiseEnService;
    private LocalDate dateDerniereRevision;
    private String observations;
    
    // Constructeurs
    public Vehicule() {
        this.disponible = true; // Par défaut, un véhicule est disponible
    }
    
    public Vehicule(String matricule, String marque, String type, Integer annee) {
        this();
        this.matricule = matricule;
        this.marque = marque;
        this.type = type;
        this.annee = annee;
    }
    
    public Vehicule(int id, String matricule, String marque, String type, Integer annee, 
                   boolean disponible, LocalDate dateAssurance, LocalDate dateVidange, 
                   LocalDate dateVisiteTechnique) {
        this.id = id;
        this.matricule = matricule;
        this.marque = marque;
        this.type = type;
        this.annee = annee;
        this.disponible = disponible;
        this.dateAssurance = dateAssurance;
        this.dateDerniereVidange = dateVidange;
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
    
    public Integer getAnnee() {
        return annee;
    }
    
    public void setAnnee(Integer annee) {
        this.annee = annee;
    }
    
    public boolean isDisponible() {
        // Nouveau système : utilise l'enum statut si disponible
        if (statutVehicule != null) {
            return statutVehicule == Statut.DISPONIBLE;
        }
        // Ancien système : utilise le boolean disponible pour compatibilité
        return disponible;
    }
    
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
        // Synchronise avec le nouveau système
        if (disponible) {
            this.statutVehicule = Statut.DISPONIBLE;
        } else {
            this.statutVehicule = Statut.AFFECTE;
        }
    }
    
    public LocalDate getDateAssurance() {
        return dateAssurance;
    }
    
    public void setDateAssurance(LocalDate dateAssurance) {
        this.dateAssurance = dateAssurance;
    }
    
    public LocalDate getDateVidange() {
        return dateDerniereVidange;
    }
    
    public void setDateVidange(LocalDate dateVidange) {
        this.dateDerniereVidange = dateVidange;
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
        if (dateDerniereVidange == null) return false;
        return dateDerniereVidange.isBefore(LocalDate.now().plusDays(30));
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
    
    // Getters et Setters pour les nouveaux champs professionnels
    
    public int getKilometrageActuel() {
        return kilometrageActuel;
    }
    
    public void setKilometrageActuel(int kilometrageActuel) {
        this.kilometrageActuel = kilometrageActuel;
    }
    
    public Integer getKmDerniereVidange() {
        return kmDerniereVidange;
    }
    
    public void setKmDerniereVidange(Integer kmDerniereVidange) {
        this.kmDerniereVidange = kmDerniereVidange;
    }
    
    public int getPeriodiciteVidange() {
        return periodiciteVidange;
    }
    
    public void setPeriodiciteVidange(int periodiciteVidange) {
        this.periodiciteVidange = periodiciteVidange;
    }
    
    public Statut getStatutVehicule() {
        return statutVehicule;
    }
    
    public void setStatutVehicule(Statut statutVehicule) {
        this.statutVehicule = statutVehicule;
    }
    
    public Etat getEtatVehicule() {
        return etatVehicule;
    }
    
    public void setEtatVehicule(Etat etatVehicule) {
        this.etatVehicule = etatVehicule;
    }
    
    public Categorie getCategorieVehicule() {
        return categorieVehicule;
    }
    
    public void setCategorieVehicule(Categorie categorieVehicule) {
        this.categorieVehicule = categorieVehicule;
    }
    
    public Carburant getTypeCarburant() {
        return typeCarburant;
    }
    
    public void setTypeCarburant(Carburant typeCarburant) {
        this.typeCarburant = typeCarburant;
    }
    
    public String getNumeroSerie() {
        return numeroSerie;
    }
    
    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }
    
    public LocalDate getDateMiseEnService() {
        return dateMiseEnService;
    }
    
    public void setDateMiseEnService(LocalDate dateMiseEnService) {
        this.dateMiseEnService = dateMiseEnService;
    }
    
    public LocalDate getDateDerniereRevision() {
        return dateDerniereRevision;
    }
    
    public void setDateDerniereRevision(LocalDate dateDerniereRevision) {
        this.dateDerniereRevision = dateDerniereRevision;
    }
    
    public String getObservations() {
        return observations;
    }
    
    public void setObservations(String observations) {
        this.observations = observations;
    }
    
    // Méthodes utilitaires professionnelles
    
    /**
     * Calcule les kilomètres restants avant la prochaine vidange
     */
    public int getKmAvantVidange() {
        if (kmDerniereVidange == null || kmDerniereVidange <= 0 || periodiciteVidange <= 0) return 0;
        int kmProchaine = kmDerniereVidange + periodiciteVidange;
        return Math.max(0, kmProchaine - kilometrageActuel);
    }
    
    /**
     * Vérifie si le véhicule nécessite une vidange selon le kilométrage
     */
    public boolean vidangeNecessaireKm() {
        return getKmAvantVidange() <= 0 && periodiciteVidange > 0;
    }
    
    /**
     * Calcule l'âge du véhicule en années
     */
    public int getAgeVehicule() {
        if (dateMiseEnService == null) return 0;
        return Period.between(dateMiseEnService, LocalDate.now()).getYears();
    }
    
    /**
     * Détermine la priorité d'entretien du véhicule
     */
    public String getPrioriteEntretien() {
        if (vidangeNecessaireKm() || vidangeProche()) return "URGENT";
        if (assuranceProche() || visiteTechniqueProche()) return "ELEVEE";
        if (getKmAvantVidange() <= 500) return "MOYENNE";
        return "NORMALE";
    }
    
    // ===== MÉTHODES SETTER/GETTER AJOUTÉES POUR NOUVEAUX CHAMPS =====
    
    public String getImmatriculation() { return immatriculation; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }
    
    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }
    
    public String getNumeroChasssis() { return numeroChasssis; }
    public void setNumeroChasssis(String numeroChasssis) { this.numeroChasssis = numeroChasssis; }
    
    public String getNumeroMoteur() { return numeroMoteur; }
    public void setNumeroMoteur(String numeroMoteur) { this.numeroMoteur = numeroMoteur; }
    
    public String getCouleur() { return couleur; }
    public void setCouleur(String couleur) { this.couleur = couleur; }
    
    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }
    
    public Carburant getCarburant() { return carburant; }
    public void setCarburant(Carburant carburant) { this.carburant = carburant; }
    
    public BigDecimal getConsommation100km() { return consommation100km; }
    public void setConsommation100km(BigDecimal consommation100km) { this.consommation100km = consommation100km; }
    
    public BigDecimal getCapaciteReservoir() { return capaciteReservoir; }
    public void setCapaciteReservoir(BigDecimal capaciteReservoir) { this.capaciteReservoir = capaciteReservoir; }
    
    public int getKilometrageInitial() { return kilometrageInitial; }
    public void setKilometrageInitial(int kilometrageInitial) { this.kilometrageInitial = kilometrageInitial; }
    
    public Etat getEtat() { return etat; }
    public void setEtat(Etat etat) { this.etat = etat; }
    
    public LocalDate getDateAcquisition() { return dateAcquisition; }
    public void setDateAcquisition(LocalDate dateAcquisition) { this.dateAcquisition = dateAcquisition; }
    
    public LocalDate getDateMiseService() { return dateMiseService; }
    public void setDateMiseService(LocalDate dateMiseService) { this.dateMiseService = dateMiseService; }
    
    public BigDecimal getPrixAcquisition() { return prixAcquisition; }
    public void setPrixAcquisition(BigDecimal prixAcquisition) { this.prixAcquisition = prixAcquisition; }
    
    public String getCompagnieAssurance() { return compagnieAssurance; }
    public void setCompagnieAssurance(String compagnieAssurance) { this.compagnieAssurance = compagnieAssurance; }
    
    public String getPoliceAssurance() { return policeAssurance; }
    public void setPoliceAssurance(String policeAssurance) { this.policeAssurance = policeAssurance; }
    
    public String getLieuVisiteTechnique() { return lieuVisiteTechnique; }
    public void setLieuVisiteTechnique(String lieuVisiteTechnique) { this.lieuVisiteTechnique = lieuVisiteTechnique; }
    
    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
    
    public Integer getResponsableId() { return responsableId; }
    public void setResponsableId(Integer responsableId) { this.responsableId = responsableId; }
}