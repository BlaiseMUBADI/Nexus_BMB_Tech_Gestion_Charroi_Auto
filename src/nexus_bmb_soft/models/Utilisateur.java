package nexus_bmb_soft.models;

import java.time.LocalDateTime;

/**
 * Classe modèle pour les utilisateurs - Intégrée avec le système d'authentification sécurisé
 * Correspond à la table 'utilisateur' enrichie de la base de données
 * 
 * @author BlaiseMUBADI
 */
public class Utilisateur {
    
    // Colonnes existantes
    private int id;
    private String nom;
    private String prenom;
    private String matricule;
    private String email;
    private RoleUtilisateur role;
    private String statut; // ACTIF, INACTIF, SUSPENDU
    private String motDePasseHash; // Ancien système (conservé pour compatibilité)
    private boolean actif;
    private LocalDateTime dateCreation;
    private LocalDateTime updated_at;
    
    // Nouvelles colonnes de sécurité
    private String username;
    private String passwordHash; // Nouveau système sécurisé SHA-256
    private String firstName;
    private String lastName;
    private boolean isActive;
    private boolean isLocked;
    private int failedLoginAttempts;
    private LocalDateTime lastLogin;
    private LocalDateTime lastFailedLogin;
    private LocalDateTime passwordExpiresAt;
    private boolean mustChangePassword;
    private String phone;
    private String department;
    private String notes;
    private Integer createdBy;
    private Integer updatedBy;
    
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
    
    public String getMatricule() {
        return matricule;
    }
    
    public void setMatricule(String matricule) {
        this.matricule = matricule;
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
    
    // ========================================================================
    // GETTERS/SETTERS POUR LES NOUVELLES COLONNES DE SÉCURITÉ
    // ========================================================================
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public boolean isLocked() {
        return isLocked;
    }
    
    public void setLocked(boolean locked) {
        isLocked = locked;
    }
    
    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }
    
    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public LocalDateTime getLastFailedLogin() {
        return lastFailedLogin;
    }
    
    public void setLastFailedLogin(LocalDateTime lastFailedLogin) {
        this.lastFailedLogin = lastFailedLogin;
    }
    
    public LocalDateTime getPasswordExpiresAt() {
        return passwordExpiresAt;
    }
    
    public void setPasswordExpiresAt(LocalDateTime passwordExpiresAt) {
        this.passwordExpiresAt = passwordExpiresAt;
    }
    
    public boolean isMustChangePassword() {
        return mustChangePassword;
    }
    
    public void setMustChangePassword(boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Integer getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }
    
    public Integer getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    public boolean isActif() {
        return actif;
    }
    
    public void setActif(boolean actif) {
        this.actif = actif;
    }
    
    public LocalDateTime getUpdated_at() {
        return updated_at;
    }
    
    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }
    
    // ========================================================================
    // MÉTHODES UTILITAIRES POUR LA SÉCURITÉ
    // ========================================================================
    
    /**
     * Vérifie si le compte est valide pour la connexion
     * Version simplifiée compatible avec la base de données actuelle
     */
    public boolean isValidForLogin() {
        // Vérifier d'abord les propriétés de base
        boolean baseActive = actif; // Propriété principale
        boolean statusOk = (statut == null || "ACTIF".equals(statut));
        
        // Si les nouvelles propriétés sont disponibles, les utiliser aussi
        boolean newPropsOk = true;
        if (isActive != baseActive) {
            // Si isActive est différent de actif, privilégier actif (base de données)
            newPropsOk = baseActive;
        }
        
        return baseActive && statusOk && newPropsOk && !isLocked;
    }
    
    /**
     * Vérifie si le mot de passe a expiré
     */
    public boolean isPasswordExpired() {
        return passwordExpiresAt != null && passwordExpiresAt.isBefore(LocalDateTime.now());
    }
    
    /**
     * Retourne le nom complet basé sur les nouvelles colonnes si disponibles, sinon les anciennes
     */
    public String getDisplayName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return getNomComplet();
    }
    
    /**
     * Retourne l'identifiant de connexion (username ou matricule)
     */
    public String getLoginIdentifier() {
        return username != null ? username : matricule;
    }
    
    /**
     * Vérifie si l'utilisateur a le droit d'accéder à une fonctionnalité selon son rôle
     */
    public boolean hasPermission(String permission) {
        // Implémentation basique selon les rôles
        switch (role.getModernRole()) {
            case ADMIN:
                return true; // Admin a tous les droits
            case GESTIONNAIRE:
                return permission.startsWith("VEHICULE_") || 
                       permission.startsWith("AFFECTATION_") ||
                       permission.startsWith("ENTRETIEN_") ||
                       permission.startsWith("REPORT_");
            case MECANICIEN:
                return permission.startsWith("ENTRETIEN_") ||
                       permission.startsWith("VEHICULE_VIEW");
            case CHAUFFEUR:
                return permission.startsWith("AFFECTATION_VIEW_OWN") ||
                       permission.startsWith("VEHICULE_VIEW");
            default:
                return false;
        }
    }
    
    /**
     * Vérifie si l'utilisateur peut gérer un autre utilisateur
     */
    public boolean canManage(Utilisateur other) {
        return this.role.canManage(other.role);
    }
    
    /**
     * Vérifie si l'utilisateur a une permission système spécifique
     * Compatible avec le nouveau système de permissions granulaires
     */
    public boolean hasSystemPermission(String permissionCode) {
        switch (this.role) {
            case SUPER_ADMIN:
                return true; // Tous droits
            case ADMIN:
                // Admin sauf système critique
                return !permissionCode.equals("SYSTEM_BACKUP") && !permissionCode.equals("SYSTEM_CONFIG");
            case GESTIONNAIRE:
                // Permissions opérationnelles uniquement
                return permissionCode.startsWith("VEHICLE_") || 
                       permissionCode.startsWith("MAINTENANCE_") || 
                       permissionCode.startsWith("ASSIGNMENT_") ||
                       permissionCode.startsWith("ALERT_") ||
                       permissionCode.startsWith("REPORT_");
            case CONDUCTEUR_SENIOR:
                // Lecture + quelques modifications
                return permissionCode.equals("VEHICLE_READ") ||
                       permissionCode.equals("MAINTENANCE_READ") ||
                       permissionCode.equals("MAINTENANCE_CREATE") ||
                       permissionCode.equals("MAINTENANCE_UPDATE") ||
                       permissionCode.equals("ASSIGNMENT_READ") ||
                       permissionCode.equals("ALERT_READ");
            case CONDUCTEUR:
                // Lecture uniquement
                return permissionCode.equals("VEHICLE_READ") ||
                       permissionCode.equals("ASSIGNMENT_READ") ||
                       permissionCode.equals("ALERT_READ");
            default:
                return false;
        }
    }
    
    /**
     * Méthode simplifiée pour vérifier les permissions courantes
     */
    public boolean canCreateVehicle() { return hasSystemPermission("VEHICLE_CREATE"); }
    public boolean canUpdateVehicle() { return hasSystemPermission("VEHICLE_UPDATE"); }
    public boolean canDeleteVehicle() { return hasSystemPermission("VEHICLE_DELETE"); }
    public boolean canCreateMaintenance() { return hasSystemPermission("MAINTENANCE_CREATE"); }
    public boolean canManageUsers() { return hasSystemPermission("USER_CREATE"); }
    public boolean canViewReports() { return hasSystemPermission("REPORT_VIEW"); }
}