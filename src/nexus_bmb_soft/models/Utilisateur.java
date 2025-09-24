package nexus_bmb_soft.models;

/**
 * Classe modèle pour les utilisateurs
 * Correspond à la table 'utilisateur' de la base de données
 * 
 * @author BlaiseMUBADI
 */
public class Utilisateur {
    
    private int id;
    private String nom;
    private RoleUtilisateur role;
    private String motDePasseHash;
    
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
    
    public RoleUtilisateur getRole() {
        return role;
    }
    
    public void setRole(RoleUtilisateur role) {
        this.role = role;
    }
    
    public String getMotDePasseHash() {
        return motDePasseHash;
    }
    
    public void setMotDePasseHash(String motDePasseHash) {
        this.motDePasseHash = motDePasseHash;
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