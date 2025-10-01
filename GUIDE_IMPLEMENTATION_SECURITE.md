# 🔐 GUIDE D'IMPLÉMENTATION SÉCURITÉ
## Gestion des droits d'accès dans l'application Java

### 1. CLASSE DE GESTION DES PERMISSIONS

```java
public class SecurityManager {
    private Connection connection;
    
    public SecurityManager(Connection connection) {
        this.connection = connection;
    }
    
    /**
     * Vérifie si un utilisateur a une permission spécifique
     */
    public boolean hasPermission(int userId, String permissionCode) {
        try {
            CallableStatement stmt = connection.prepareCall("SELECT verifier_permission(?, ?)");
            stmt.setInt(1, userId);
            stmt.setString(2, permissionCode);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBoolean(1);
            }
        } catch (SQLException e) {
            logSecurityEvent(userId, "PERMISSION_CHECK_ERROR", e.getMessage(), "ECHEC");
        }
        return false;
    }
    
    /**
     * Log des événements de sécurité
     */
    public void logSecurityEvent(int userId, String action, String details, String result) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO log_securite (utilisateur_id, action, details, resultat, ip_address) VALUES (?, ?, ?, ?, ?)"
            );
            stmt.setInt(1, userId);
            stmt.setString(2, action);
            stmt.setString(3, details);
            stmt.setString(4, result);
            stmt.setString(5, getCurrentUserIP());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Authentification utilisateur
     */
    public User authenticate(String matricule, String password) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM utilisateur WHERE matricule = ? AND actif = 1 AND statut = 'ACTIF'"
            );
            stmt.setString(1, matricule);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String hashedPassword = rs.getString("mot_de_passe_hash");
                
                // Vérification mot de passe
                if (verifyPassword(password, hashedPassword)) {
                    // Mise à jour dernière connexion
                    updateLastLogin(rs.getInt("id"));
                    
                    // Reset tentatives d'échec
                    resetFailedAttempts(rs.getInt("id"));
                    
                    logSecurityEvent(rs.getInt("id"), "CONNEXION", "Connexion réussie", "SUCCES");
                    
                    return createUserFromResultSet(rs);
                } else {
                    // Incrémenter tentatives d'échec
                    incrementFailedAttempts(rs.getInt("id"));
                    logSecurityEvent(rs.getInt("id"), "CONNEXION_ECHEC", "Mot de passe incorrect", "ECHEC");
                }
            }
        } catch (SQLException e) {
            logSecurityEvent(0, "CONNEXION_ERROR", e.getMessage(), "ECHEC");
        }
        return null;
    }
}
```

### 2. ANNOTATIONS POUR LES CONTRÔLEURS

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequirePermission {
    String value();
}

// Utilisation
@RequirePermission("VEHICLE_CREATE")
public void createVehicle(Vehicle vehicle) {
    // Code de création véhicule
}

@RequirePermission("MAINTENANCE_READ")
public List<Maintenance> getMaintenances() {
    // Code lecture entretiens
}
```

### 3. INTERCEPTEUR DE SÉCURITÉ

```java
public class SecurityInterceptor {
    private SecurityManager securityManager;
    private User currentUser;
    
    public boolean checkPermission(String permissionCode) {
        if (currentUser == null) {
            throw new SecurityException("Utilisateur non authentifié");
        }
        
        boolean hasPermission = securityManager.hasPermission(
            currentUser.getId(), 
            permissionCode
        );
        
        if (!hasPermission) {
            securityManager.logSecurityEvent(
                currentUser.getId(),
                "ACCESS_DENIED",
                "Tentative d'accès refusée pour permission: " + permissionCode,
                "BLOQUE"
            );
            throw new SecurityException("Accès refusé - Permission insuffisante");
        }
        
        return true;
    }
}
```

### 4. CLASSES MÉTIER SÉCURISÉES

```java
public class VehicleService {
    private SecurityInterceptor security;
    private VehicleDAO vehicleDAO;
    
    public void createVehicle(Vehicle vehicle) {
        security.checkPermission("VEHICLE_CREATE");
        vehicleDAO.create(vehicle);
    }
    
    public List<Vehicle> getAllVehicles() {
        security.checkPermission("VEHICLE_READ");
        return vehicleDAO.findAll();
    }
    
    public void updateVehicle(Vehicle vehicle) {
        security.checkPermission("VEHICLE_UPDATE");
        vehicleDAO.update(vehicle);
    }
    
    public void deleteVehicle(int vehicleId) {
        security.checkPermission("VEHICLE_DELETE");
        vehicleDAO.delete(vehicleId);
    }
}

public class MaintenanceService {
    private SecurityInterceptor security;
    private MaintenanceDAO maintenanceDAO;
    
    public void createMaintenance(Maintenance maintenance) {
        security.checkPermission("MAINTENANCE_CREATE");
        maintenanceDAO.create(maintenance);
    }
    
    public void validateMaintenance(int maintenanceId) {
        security.checkPermission("MAINTENANCE_VALIDATE");
        maintenanceDAO.updateStatus(maintenanceId, "TERMINE");
    }
}
```

### 5. INTERFACE UTILISATEUR ADAPTATIVE

```java
public class UIPermissionManager {
    private SecurityManager securityManager;
    private User currentUser;
    
    /**
     * Active/désactive les boutons selon les permissions
     */
    public void configureUI(JPanel panel) {
        // Bouton Créer Véhicule
        JButton btnCreateVehicle = findButton(panel, "btnCreateVehicle");
        btnCreateVehicle.setEnabled(
            securityManager.hasPermission(currentUser.getId(), "VEHICLE_CREATE")
        );
        
        // Bouton Gérer Utilisateurs
        JButton btnManageUsers = findButton(panel, "btnManageUsers");
        btnManageUsers.setEnabled(
            securityManager.hasPermission(currentUser.getId(), "USER_CREATE")
        );
        
        // Menu Administration
        JMenu adminMenu = findMenu(panel, "adminMenu");
        adminMenu.setVisible(
            securityManager.hasPermission(currentUser.getId(), "SYSTEM_ADMIN")
        );
    }
}
```

### 6. GESTION DES SESSIONS

```java
public class SessionManager {
    private static Map<String, UserSession> activeSessions = new ConcurrentHashMap<>();
    
    public static class UserSession {
        private User user;
        private String token;
        private LocalDateTime lastActivity;
        private String ipAddress;
        
        // getters/setters
    }
    
    public UserSession createSession(User user, String ipAddress) {
        String token = generateSecureToken();
        UserSession session = new UserSession();
        session.setUser(user);
        session.setToken(token);
        session.setLastActivity(LocalDateTime.now());
        session.setIpAddress(ipAddress);
        
        activeSessions.put(token, session);
        
        // Mise à jour token en base
        updateUserToken(user.getId(), token, ipAddress);
        
        return session;
    }
    
    public boolean isValidSession(String token) {
        UserSession session = activeSessions.get(token);
        if (session == null) return false;
        
        // Vérifier timeout (30 minutes)
        if (session.getLastActivity().isBefore(LocalDateTime.now().minusMinutes(30))) {
            activeSessions.remove(token);
            return false;
        }
        
        // Mise à jour dernière activité
        session.setLastActivity(LocalDateTime.now());
        return true;
    }
}
```

### 7. REQUÊTES UTILES POUR L'APPLICATION

```sql
-- Obtenir les permissions d'un utilisateur
SELECT permission_code, granted 
FROM vue_permissions_utilisateur 
WHERE utilisateur_id = ? AND granted = 1;

-- Vérifier si compte verrouillé
SELECT compte_verrouille, tentatives_echec 
FROM utilisateur 
WHERE id = ?;

-- Logs de sécurité récents
SELECT * FROM log_securite 
WHERE utilisateur_id = ? 
ORDER BY created_at DESC 
LIMIT 10;

-- Dashboard sécurité
SELECT * FROM vue_dashboard_securite;
```

### 8. CONFIGURATION DES RÔLES

| RÔLE | PERMISSIONS PRINCIPALES |
|------|------------------------|
| **super_admin** | TOUTES (27 permissions) |
| **admin** | 25 permissions (sauf SYSTEM_BACKUP, SYSTEM_CONFIG) |
| **gestionnaire** | 15 permissions (gestion opérationnelle) |
| **conducteur_senior** | 8 permissions (lecture + entretiens) |
| **conducteur** | 3 permissions (lecture limitée) |

### 9. BONNES PRATIQUES

1. **Toujours vérifier les permissions** avant chaque action
2. **Logger tous les événements** de sécurité
3. **Utiliser des sessions sécurisées** avec timeout
4. **Adapter l'interface** selon les droits utilisateur
5. **Gérer les exceptions** de sécurité proprement
6. **Monitorer les tentatives** de connexion échouées
7. **Forcer le changement** des mots de passe par défaut

### 10. UTILISATION PRATIQUE

```java
// Dans votre Application.java
public class Application {
    private SecurityManager security;
    private User currentUser;
    
    private void initializeSecurity() {
        security = new SecurityManager(databaseConnection);
        
        // Authentification
        currentUser = security.authenticate(login, password);
        if (currentUser == null) {
            showLoginError();
            return;
        }
        
        // Configuration UI selon rôle
        configureUIForUser(currentUser);
    }
    
    private void configureUIForUser(User user) {
        // Masquer/afficher menus selon permissions
        if (!security.hasPermission(user.getId(), "VEHICLE_CREATE")) {
            btnAddVehicle.setVisible(false);
        }
        
        if (!security.hasPermission(user.getId(), "USER_READ")) {
            menuUsers.setVisible(false);
        }
    }
}
```

Ce système vous garantit une sécurité robuste et une gestion granulaire des accès ! 🛡️