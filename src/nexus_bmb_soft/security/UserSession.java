package nexus_bmb_soft.security;

import java.time.LocalDateTime;

/**
 * Modèle représentant une session utilisateur active
 * Gère l'authentification et le suivi des sessions
 * 
 * @author BlaiseMUBADI
 */
public class UserSession {
    
    private int id;
    private int userId;
    private String sessionToken;
    private String ipAddress;
    private String userAgent;
    private boolean isActive;
    private LocalDateTime expiresAt;
    private LocalDateTime lastActivity;
    private LocalDateTime createdAt;
    
    // Référence à l'utilisateur (pas stockée en base, chargée dynamiquement)
    private User user;
    
    // Constructeurs
    public UserSession() {}
    
    public UserSession(int userId, String sessionToken, String ipAddress, String userAgent) {
        this.userId = userId;
        this.sessionToken = sessionToken;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
        // Session expire dans 8 heures par défaut
        this.expiresAt = LocalDateTime.now().plusHours(8);
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getSessionToken() {
        return sessionToken;
    }
    
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public LocalDateTime getLastActivity() {
        return lastActivity;
    }
    
    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    // Méthodes utilitaires
    public boolean isValid() {
        return isActive && 
               expiresAt != null && 
               LocalDateTime.now().isBefore(expiresAt) &&
               (user == null || user.canLogin());
    }
    
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    public void extendSession(int hours) {
        if (isValid()) {
            this.expiresAt = LocalDateTime.now().plusHours(hours);
            this.lastActivity = LocalDateTime.now();
        }
    }
    
    public void refreshActivity() {
        if (isActive) {
            this.lastActivity = LocalDateTime.now();
        }
    }
    
    public void invalidate() {
        this.isActive = false;
        this.expiresAt = LocalDateTime.now().minusMinutes(1);
    }
    
    public long getMinutesUntilExpiry() {
        if (expiresAt == null) return -1;
        return java.time.Duration.between(LocalDateTime.now(), expiresAt).toMinutes();
    }
    
    public long getMinutesSinceLastActivity() {
        if (lastActivity == null) return -1;
        return java.time.Duration.between(lastActivity, LocalDateTime.now()).toMinutes();
    }
    
    public String getBrowserName() {
        if (userAgent == null || userAgent.isEmpty()) return "Inconnu";
        
        String ua = userAgent.toLowerCase();
        if (ua.contains("chrome")) return "Chrome";
        if (ua.contains("firefox")) return "Firefox";
        if (ua.contains("safari")) return "Safari";
        if (ua.contains("edge")) return "Edge";
        if (ua.contains("internet explorer")) return "Internet Explorer";
        if (ua.contains("opera")) return "Opera";
        
        return "Autre";
    }
    
    @Override
    public String toString() {
        return "UserSession{" +
                "id=" + id +
                ", userId=" + userId +
                ", sessionToken='" + sessionToken.substring(0, Math.min(8, sessionToken.length())) + "...' " +
                ", ipAddress='" + ipAddress + '\'' +
                ", isActive=" + isActive +
                ", expiresAt=" + expiresAt +
                ", lastActivity=" + lastActivity +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserSession session = (UserSession) obj;
        return sessionToken != null && sessionToken.equals(session.sessionToken);
    }
    
    @Override
    public int hashCode() {
        return sessionToken != null ? sessionToken.hashCode() : 0;
    }
}