package nexus_bmb_soft.security;

/**
 * Gestionnaire de session global pour l'application
 * Maintient l'état de l'utilisateur connecté et gère les permissions
 * Pattern Singleton pour un accès global
 * 
 * @author BlaiseMUBADI
 */
public class SessionManager {
    
    private static SessionManager instance;
    private UserSession currentSession;
    private User currentUser;
    private final AuthenticationDAO authDAO;
    
    private SessionManager() {
        this.authDAO = new AuthenticationDAO();
    }
    
    /**
     * Récupère l'instance unique du gestionnaire de session
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Connecte un utilisateur au système
     */
    public boolean login(String username, String password) {
        try {
            AuthenticationDAO.AuthResult result = authDAO.authenticate(username, password);
            
            if (result.isSuccess()) {
                User user = result.getUser();
                UserSession session = authDAO.createSession(
                    user.getId(), 
                    getClientIP(), 
                    getClientUserAgent()
                );
                
                if (session != null) {
                    session.setUser(user);
                    this.currentSession = session;
                    this.currentUser = user;
                    
                    System.out.println("🔐 Connexion réussie pour " + user.getDisplayName() + 
                                     " (Rôle: " + user.getRole().getDisplayName() + ")");
                    return true;
                }
            } else {
                System.out.println("❌ Échec de connexion: " + result.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la connexion: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Déconnecte l'utilisateur actuel
     */
    public void logout() {
        if (currentSession != null) {
            authDAO.invalidateSession(currentSession.getSessionToken());
            System.out.println("🔓 Déconnexion de " + currentUser.getDisplayName());
        }
        
        this.currentSession = null;
        this.currentUser = null;
    }
    
    /**
     * Vérifie si un utilisateur est connecté
     */
    public boolean isLoggedIn() {
        if (currentSession == null || currentUser == null) {
            return false;
        }
        
        // Vérifier si la session est toujours valide
        if (!currentSession.isValid()) {
            logout();
            return false;
        }
        
        // Valider périodiquement avec la base de données
        if (shouldRevalidate()) {
            UserSession validatedSession = authDAO.validateSession(currentSession.getSessionToken());
            if (validatedSession == null) {
                logout();
                return false;
            } else {
                currentSession = validatedSession;
                currentUser = validatedSession.getUser();
            }
        }
        
        return true;
    }
    
    /**
     * Récupère l'utilisateur connecté
     */
    public User getCurrentUser() {
        if (isLoggedIn()) {
            return currentUser;
        }
        return null;
    }
    
    /**
     * Récupère la session actuelle
     */
    public UserSession getCurrentSession() {
        if (isLoggedIn()) {
            return currentSession;
        }
        return null;
    }
    
    /**
     * Vérifie si l'utilisateur actuel a une permission
     */
    public boolean hasPermission(Permission permission) {
        User user = getCurrentUser();
        return user != null && user.hasPermission(permission);
    }
    
    /**
     * Vérifie si l'utilisateur actuel a toutes les permissions
     */
    public boolean hasAllPermissions(Permission... permissions) {
        User user = getCurrentUser();
        return user != null && user.hasAllPermissions(permissions);
    }
    
    /**
     * Vérifie si l'utilisateur actuel a au moins une des permissions
     */
    public boolean hasAnyPermission(Permission... permissions) {
        User user = getCurrentUser();
        return user != null && user.hasAnyPermission(permissions);
    }
    
    /**
     * Vérifie si l'utilisateur actuel a un rôle spécifique ou supérieur
     */
    public boolean hasRoleOrHigher(UserRole role) {
        User user = getCurrentUser();
        return user != null && user.getRole().hasHigherOrEqualLevel(role);
    }
    
    /**
     * Force la revalidation de la session
     */
    public void revalidateSession() {
        if (currentSession != null) {
            UserSession validatedSession = authDAO.validateSession(currentSession.getSessionToken());
            if (validatedSession != null) {
                currentSession = validatedSession;
                currentUser = validatedSession.getUser();
            } else {
                logout();
            }
        }
    }
    
    /**
     * Étend la durée de la session
     */
    public void extendSession() {
        if (currentSession != null && currentSession.isValid()) {
            currentSession.extendSession(8); // 8 heures supplémentaires
            // Note: Il faudrait aussi mettre à jour en base de données
        }
    }
    
    /**
     * Met à jour l'activité de la session
     */
    public void refreshActivity() {
        if (currentSession != null) {
            currentSession.refreshActivity();
        }
    }
    
    /**
     * Récupère des informations sur la session pour l'interface
     */
    public String getSessionInfo() {
        if (!isLoggedIn()) {
            return "Non connecté";
        }
        
        StringBuilder info = new StringBuilder();
        info.append("👤 ").append(currentUser.getDisplayName()).append("\n");
        info.append("🎭 ").append(currentUser.getRole().getDisplayName()).append("\n");
        
        if (currentUser.getDepartment() != null) {
            info.append("🏢 ").append(currentUser.getDepartment()).append("\n");
        }
        
        if (currentSession != null) {
            long minutesLeft = currentSession.getMinutesUntilExpiry();
            if (minutesLeft > 0) {
                info.append("⏰ Session expire dans ").append(minutesLeft).append(" minutes\n");
            }
        }
        
        return info.toString().trim();
    }
    
    /**
     * Change le mot de passe de l'utilisateur connecté
     */
    public boolean changePassword(String newPassword) {
        if (currentUser != null) {
            return authDAO.changePassword(currentUser.getId(), newPassword);
        }
        return false;
    }
    
    /**
     * Vérifie si l'utilisateur doit changer son mot de passe
     */
    public boolean mustChangePassword() {
        return currentUser != null && currentUser.isMustChangePassword();
    }
    
    /**
     * Vérifie si le mot de passe est expiré
     */
    public boolean isPasswordExpired() {
        return currentUser != null && currentUser.isPasswordExpired();
    }
    
    // === MÉTHODES UTILITAIRES PRIVÉES ===
    
    private boolean shouldRevalidate() {
        if (currentSession == null) return false;
        
        // Revalider toutes les 5 minutes
        long minutesSinceLastActivity = currentSession.getMinutesSinceLastActivity();
        return minutesSinceLastActivity > 5;
    }
    
    private String getClientIP() {
        // Pour une application desktop, on peut retourner l'IP locale
        // Dans une application web, on récupérerait l'IP du client
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "localhost";
        }
    }
    
    private String getClientUserAgent() {
        // Pour une application desktop, on retourne des infos sur le système
        return "Desktop App - " + 
               System.getProperty("os.name") + " " + 
               System.getProperty("os.version") + " - " +
               "Java " + System.getProperty("java.version");
    }
    
    /**
     * Méthode pour les tests - ne pas utiliser en production
     */
    public void setTestUser(User testUser) {
        if (System.getProperty("test.mode") != null) {
            this.currentUser = testUser;
            this.currentSession = new UserSession(testUser.getId(), "test-token", "localhost", "test");
        }
    }
    
    /**
     * Nettoie la session (appelé à la fermeture de l'application)
     */
    public void cleanup() {
        logout();
        instance = null;
    }
}