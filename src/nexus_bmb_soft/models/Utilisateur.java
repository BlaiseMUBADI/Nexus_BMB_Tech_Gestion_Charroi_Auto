package nexus_bmb_soft.models;

import java.time.LocalDateTime;

/**
 * Classe modèle pour les utilisateurs
 * Correspond à la table 'utilisateur' de la base de données
 * 
 * @author BlaiseMUBADI
 */
public class Utilisateur {
    
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private RoleUtilisateur role;
    private String statut; // ACTIF, INACTIF, SUSPENDU
    private String motDePasseHash;
    private LocalDateTime dateCreation;
    
    // Constructeurs
    public Utilisateur() {
    }
    
    public Utilisateur(String nom, RoleUtilisateur role) {
        this.nom = nom;
        this.role = role;
    }
    
    public Utilisateur(int id, String nom, RoleUtilisateur role, String motDePasseHash) {
        this.id = id;
        this.nom = nom;
        this.role = role;
        this.motDePasseHash = motDePasseHash;
    }
    
    public Utilisateur(String nom, String prenom, String email, RoleUtilisateur role, String statut) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
        this.statut = statut;
        this.dateCreation = LocalDateTime.now();
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
    
    public String getPrenom() {
        return prenom;
    }
    
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public RoleUtilisateur getRole() {
        return role;
    }
    
    public void setRole(RoleUtilisateur role) {
        this.role = role;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public String getMotDePasseHash() {
        return motDePasseHash;
    }
    
    public void setMotDePasseHash(String motDePasseHash) {
        this.motDePasseHash = motDePasseHash;
    }
    
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    // Méthode pour définir le mot de passe (sera hashé)
    public void setMotDePasse(String motDePasse) {
        // Pour le moment, simple stockage - en production, utiliser BCrypt ou similaire
        this.motDePasseHash = motDePasse;
    }
    
    // Méthodes utilitaires
    
    /**
     * Vérifie si l'utilisateur a les permissions d'administrateur
     */
    public boolean isAdmin() {
        return role == RoleUtilisateur.ADMIN;
    }
    
    /**
     * Vérifie si l'utilisateur peut gérer les véhicules et affectations
     */
    public boolean peutGerer() {
        return role == RoleUtilisateur.ADMIN || role == RoleUtilisateur.GESTIONNAIRE;
    }
    
    /**
     * Vérifie si l'utilisateur est un conducteur
     */
    public boolean isConducteur() {
        return role == RoleUtilisateur.CONDUCTEUR;
    }
    
    /**
     * Extrait le grade militaire du nom (pour l'affichage)
     */
    public String getGrade() {
        if (nom == null) return "";
        
        String[] parties = nom.split(" ");
        if (parties.length >= 2) {
            String premierMot = parties[0].toLowerCase();
            if (premierMot.equals("major") || premierMot.equals("capitaine") || 
                premierMot.equals("colonel") || premierMot.equals("général") ||
                premierMot.equals("lieutenant") || premierMot.equals("sergent")) {
                return parties[0];
            }
        }
        return "";
    }
    
    /**
     * Retourne le nom sans le grade
     */
    public String getNomSansGrade() {
        if (nom == null) return "";
        
        String grade = getGrade();
        if (!grade.isEmpty()) {
            return nom.substring(grade.length()).trim();
        }
        return nom;
    }
    
    /**
     * Retourne le nom complet formaté pour l'affichage
     */
    public String getNomComplet() {
        String grade = getGrade();
        if (!grade.isEmpty()) {
            return String.format("%s %s (%s)", grade, getNomSansGrade(), role.getValeur().toUpperCase());
        }
        return String.format("%s (%s)", nom, role.getValeur().toUpperCase());
    }
    
    @Override
    public String toString() {
        return getNomComplet();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Utilisateur utilisateur = (Utilisateur) obj;
        return id == utilisateur.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}