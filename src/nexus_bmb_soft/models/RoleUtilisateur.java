package nexus_bmb_soft.models;

/**
 * Énumération des rôles utilisateurs - Compatible avec le système d'authentification sécurisé
 * Support des anciens et nouveaux rôles pour la migration
 * 
 * @author BlaiseMUBADI
 */
public enum RoleUtilisateur {
    // Nouveaux rôles sécurisés alignés avec la base de données
    SUPER_ADMIN("super_admin", 1, "Super Administrateur - Accès complet système"),
    ADMIN("admin", 2, "Administrateur - Gestion complète"),
    GESTIONNAIRE("gestionnaire", 3, "Gestionnaire de flotte - Gestion opérationnelle"),
    CONDUCTEUR_SENIOR("conducteur_senior", 4, "Conducteur senior - Lecture + entretiens"),
    CONDUCTEUR("conducteur", 5, "Conducteur - Lecture limitée"),
    
    // Anciens rôles (pour compatibilité)
    ADMIN_OLD("ADMIN", 2, "Administrateur (ancien format)"),
    GESTIONNAIRE_OLD("GESTIONNAIRE", 3, "Gestionnaire (ancien format)"),
    MECANICIEN("MECANICIEN", 4, "Mécanicien - Entretiens et réparations"),
    CHAUFFEUR("CHAUFFEUR", 5, "Chauffeur - Consultation véhicules assignés");
    
    private final String valeur;
    private final int niveau;
    private final String description;
    
    RoleUtilisateur(String valeur, int niveau, String description) {
        this.valeur = valeur;
        this.niveau = niveau;
        this.description = description;
    }
    
    public String getValeur() {
        return valeur;
    }
    
    public int getNiveau() {
        return niveau;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Vérifie si ce rôle a un niveau supérieur ou égal à un autre
     */
    public boolean hasHigherOrEqualLevel(RoleUtilisateur other) {
        return this.niveau <= other.niveau; // Plus le niveau est bas, plus il est élevé
    }
    
    /**
     * Vérifie si ce rôle peut gérer un autre rôle
     */
    public boolean canManage(RoleUtilisateur other) {
        return this.niveau < other.niveau;
    }
    
    /**
     * Convertit un rôle de string vers l'énumération avec support des anciens et nouveaux formats
     */
    public static RoleUtilisateur fromString(String role) {
        if (role == null) return CHAUFFEUR; // Rôle par défaut
        
        // Nettoyage de la chaîne
        String cleanRole = role.trim();
        
        // Recherche exacte d'abord
        for (RoleUtilisateur r : RoleUtilisateur.values()) {
            if (r.valeur.equalsIgnoreCase(cleanRole)) {
                return r;
            }
        }
        
        // Migration automatique des anciens vers nouveaux rôles
        switch (cleanRole.toLowerCase()) {
            case "admin":
            case "super_admin":
                return ADMIN;
            case "gestionnaire":
                return GESTIONNAIRE;
            case "conducteur_senior":
                return MECANICIEN;
            case "conducteur":
                return CHAUFFEUR;
            default:
                System.out.println("⚠️ Rôle inconnu: " + role + " - Attribution du rôle CHAUFFEUR par défaut");
                return CHAUFFEUR;
        }
    }
    
    /**
     * Obtient le rôle moderne équivalent
     */
    public RoleUtilisateur getModernRole() {
        switch (this) {
            case ADMIN_OLD:
            case SUPER_ADMIN:
                return ADMIN;
            case GESTIONNAIRE_OLD:
                return GESTIONNAIRE;
            case CONDUCTEUR_SENIOR:
                return MECANICIEN;
            case CONDUCTEUR:
                return CHAUFFEUR;
            default:
                return this; // Déjà moderne
        }
    }
    
    /**
     * Vérifie si c'est un rôle administrateur
     */
    public boolean isAdmin() {
        return this == ADMIN || this == ADMIN_OLD || this == SUPER_ADMIN;
    }
    
    /**
     * Vérifie si c'est un rôle de gestion
     */
    public boolean isManager() {
        return this == GESTIONNAIRE || this == GESTIONNAIRE_OLD;
    }
    
    @Override
    public String toString() {
        return valeur;
    }
}