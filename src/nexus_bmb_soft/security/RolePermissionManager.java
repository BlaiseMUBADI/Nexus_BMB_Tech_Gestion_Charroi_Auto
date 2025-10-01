package nexus_bmb_soft.security;

import java.util.*;

/**
 * Gestionnaire des permissions par rôle
 * Définit quelles permissions sont accordées à chaque rôle utilisateur
 * 
 * @author BlaiseMUBADI
 */
public class RolePermissionManager {
    
    private static final Map<UserRole, Set<Permission>> ROLE_PERMISSIONS = new HashMap<>();
    
    static {
        initializePermissions();
    }
    
    /**
     * Initialise les permissions pour chaque rôle
     */
    private static void initializePermissions() {
        
        // === ADMINISTRATEUR - Accès complet ===
        Set<Permission> adminPermissions = EnumSet.allOf(Permission.class);
        ROLE_PERMISSIONS.put(UserRole.ADMIN, adminPermissions);
        
        // === GESTIONNAIRE - Gestion de flotte ===
        Set<Permission> gestionnairePermissions = EnumSet.of(
            // Véhicules
            Permission.VEHICULE_VIEW,
            Permission.VEHICULE_CREATE,
            Permission.VEHICULE_EDIT,
            Permission.VEHICULE_MANAGE_ALL,
            
            // Affectations
            Permission.AFFECTATION_VIEW,
            Permission.AFFECTATION_CREATE,
            Permission.AFFECTATION_EDIT,
            Permission.AFFECTATION_DELETE,
            Permission.AFFECTATION_MANAGE_ALL,
            
            // Entretiens
            Permission.ENTRETIEN_VIEW,
            Permission.ENTRETIEN_CREATE,
            Permission.ENTRETIEN_EDIT,
            Permission.ENTRETIEN_MANAGE_ALL,
            
            // Utilisateurs (limité)
            Permission.USER_VIEW,
            Permission.USER_EDIT, // Seulement chauffeurs et mécaniciens
            
            // Rapports
            Permission.REPORT_VIEW_BASIC,
            Permission.REPORT_VIEW_ADVANCED,
            Permission.REPORT_EXPORT,
            Permission.REPORT_DASHBOARD,
            
            // Notifications
            Permission.NOTIFICATION_VIEW,
            Permission.NOTIFICATION_MANAGE,
            Permission.NOTIFICATION_SEND,
            
            // Données
            Permission.DATA_EXPORT
        );
        ROLE_PERMISSIONS.put(UserRole.GESTIONNAIRE, gestionnairePermissions);
        
        // === MÉCANICIEN - Entretiens et réparations ===
        Set<Permission> mecanicienPermissions = EnumSet.of(
            // Véhicules (consultation uniquement)
            Permission.VEHICULE_VIEW,
            
            // Affectations (consultation uniquement)
            Permission.AFFECTATION_VIEW,
            
            // Entretiens (gestion complète)
            Permission.ENTRETIEN_VIEW,
            Permission.ENTRETIEN_CREATE,
            Permission.ENTRETIEN_EDIT,
            Permission.ENTRETIEN_CLOSE,
            Permission.ENTRETIEN_MANAGE_ALL,
            
            // Rapports (de base)
            Permission.REPORT_VIEW_BASIC,
            Permission.REPORT_DASHBOARD,
            
            // Notifications
            Permission.NOTIFICATION_VIEW
        );
        ROLE_PERMISSIONS.put(UserRole.MECANICIEN, mecanicienPermissions);
        
        // === CHAUFFEUR - Consultation et signalement ===
        Set<Permission> chauffeurPermissions = EnumSet.of(
            // Véhicules (consultation limitée)
            Permission.VEHICULE_VIEW,
            
            // Affectations (ses propres uniquement)
            Permission.AFFECTATION_VIEW_OWN,
            
            // Entretiens (consultation et création de demandes)
            Permission.ENTRETIEN_VIEW,
            Permission.ENTRETIEN_CREATE, // Pour signaler des problèmes
            
            // Rapports (très limité)
            Permission.REPORT_VIEW_BASIC,
            
            // Notifications
            Permission.NOTIFICATION_VIEW
        );
        ROLE_PERMISSIONS.put(UserRole.CHAUFFEUR, chauffeurPermissions);
    }
    
    /**
     * Retourne toutes les permissions accordées à un rôle
     */
    public static Set<Permission> getPermissions(UserRole role) {
        return new HashSet<>(ROLE_PERMISSIONS.getOrDefault(role, Collections.emptySet()));
    }
    
    /**
     * Vérifie si un rôle possède une permission spécifique
     */
    public static boolean hasPermission(UserRole role, Permission permission) {
        Set<Permission> permissions = ROLE_PERMISSIONS.get(role);
        return permissions != null && permissions.contains(permission);
    }
    
    /**
     * Vérifie si un rôle possède toutes les permissions spécifiées
     */
    public static boolean hasAllPermissions(UserRole role, Permission... permissions) {
        Set<Permission> rolePermissions = ROLE_PERMISSIONS.get(role);
        if (rolePermissions == null) return false;
        
        for (Permission permission : permissions) {
            if (!rolePermissions.contains(permission)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Vérifie si un rôle possède au moins une des permissions spécifiées
     */
    public static boolean hasAnyPermission(UserRole role, Permission... permissions) {
        Set<Permission> rolePermissions = ROLE_PERMISSIONS.get(role);
        if (rolePermissions == null) return false;
        
        for (Permission permission : permissions) {
            if (rolePermissions.contains(permission)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Retourne une description textuelle des permissions d'un rôle
     */
    public static String getPermissionsSummary(UserRole role) {
        Set<Permission> permissions = getPermissions(role);
        StringBuilder summary = new StringBuilder();
        summary.append("Permissions pour ").append(role.getDisplayName()).append(":\n");
        
        if (permissions.isEmpty()) {
            summary.append("• Aucune permission spécifique");
        } else {
            permissions.stream()
                .sorted(Comparator.comparing(Permission::getDescription))
                .forEach(permission -> summary.append("• ").append(permission.getDescription()).append("\n"));
        }
        
        return summary.toString();
    }
    
    /**
     * Retourne les rôles qui possèdent une permission donnée
     */
    public static Set<UserRole> getRolesWithPermission(Permission permission) {
        Set<UserRole> rolesWithPermission = new HashSet<>();
        
        for (Map.Entry<UserRole, Set<Permission>> entry : ROLE_PERMISSIONS.entrySet()) {
            if (entry.getValue().contains(permission)) {
                rolesWithPermission.add(entry.getKey());
            }
        }
        
        return rolesWithPermission;
    }
    
    /**
     * Vérifie si un utilisateur peut gérer un autre utilisateur selon les règles hiérarchiques
     */
    public static boolean canManageUser(UserRole managerRole, UserRole targetRole) {
        // Un utilisateur peut gérer des utilisateurs de niveau hiérarchique inférieur
        return managerRole.canManage(targetRole) && 
               hasPermission(managerRole, Permission.USER_EDIT);
    }
}