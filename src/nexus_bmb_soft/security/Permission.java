package nexus_bmb_soft.security;

/**
 * Énumération des permissions spécifiques dans le système
 * Utilisée pour un contrôle granulaire des accès
 * 
 * @author BlaiseMUBADI
 */
public enum Permission {
    
    // === PERMISSIONS VÉHICULES ===
    VEHICULE_VIEW("Consulter les véhicules"),
    VEHICULE_CREATE("Créer des véhicules"),
    VEHICULE_EDIT("Modifier les véhicules"),
    VEHICULE_DELETE("Supprimer des véhicules"),
    VEHICULE_MANAGE_ALL("Gérer tous les véhicules"),
    
    // === PERMISSIONS AFFECTATIONS ===
    AFFECTATION_VIEW("Consulter les affectations"),
    AFFECTATION_VIEW_OWN("Consulter ses propres affectations"),
    AFFECTATION_CREATE("Créer des affectations"),
    AFFECTATION_EDIT("Modifier les affectations"),
    AFFECTATION_DELETE("Supprimer des affectations"),
    AFFECTATION_MANAGE_ALL("Gérer toutes les affectations"),
    
    // === PERMISSIONS ENTRETIENS ===
    ENTRETIEN_VIEW("Consulter les entretiens"),
    ENTRETIEN_CREATE("Créer des entretiens"),
    ENTRETIEN_EDIT("Modifier les entretiens"),
    ENTRETIEN_DELETE("Supprimer des entretiens"),
    ENTRETIEN_CLOSE("Clôturer les entretiens"),
    ENTRETIEN_MANAGE_ALL("Gérer tous les entretiens"),
    
    // === PERMISSIONS UTILISATEURS ===
    USER_VIEW("Consulter les utilisateurs"),
    USER_CREATE("Créer des utilisateurs"),
    USER_EDIT("Modifier les utilisateurs"),
    USER_DELETE("Supprimer des utilisateurs"),
    USER_MANAGE_ROLES("Gérer les rôles utilisateur"),
    USER_RESET_PASSWORD("Réinitialiser les mots de passe"),
    
    // === PERMISSIONS RAPPORTS ===
    REPORT_VIEW_BASIC("Consulter les rapports de base"),
    REPORT_VIEW_ADVANCED("Consulter les rapports avancés"),
    REPORT_EXPORT("Exporter les rapports"),
    REPORT_DASHBOARD("Accéder au tableau de bord"),
    
    // === PERMISSIONS ADMINISTRATION ===
    ADMIN_SYSTEM_CONFIG("Configuration système"),
    ADMIN_BACKUP("Sauvegarde et restauration"),
    ADMIN_LOGS("Consulter les logs système"),
    ADMIN_MAINTENANCE("Maintenance système"),
    
    // === PERMISSIONS NOTIFICATIONS ===
    NOTIFICATION_VIEW("Consulter les notifications"),
    NOTIFICATION_MANAGE("Gérer les notifications"),
    NOTIFICATION_SEND("Envoyer des notifications"),
    
    // === PERMISSIONS DONNÉES ===
    DATA_IMPORT("Importer des données"),
    DATA_EXPORT("Exporter des données"),
    DATA_PURGE("Purger les anciennes données");
    
    private final String description;
    
    Permission(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return description;
    }
}