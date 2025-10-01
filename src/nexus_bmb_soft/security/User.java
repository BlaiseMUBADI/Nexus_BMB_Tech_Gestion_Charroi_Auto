package nexus_bmb_soft.security;

import java.time.LocalDateTime;

/**
 * Modèle représentant un utilisateur du système
 * Contient toutes les informations d'authentification et de profil
 * 
 * @author BlaiseMUBADI
 */
public class User {
    
    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private UserRole role;
    private boolean isActive;
    private boolean isLocked;
    private int failedLoginAttempts;
    private LocalDateTime lastLogin;
    private LocalDateTime lastFailedLogin;
    private LocalDateTime passwordExpiresAt;
    private boolean mustChangePassword;
    private String phone;
    private String department;
    private String employeeId;
    private String notes;
    private Integer createdBy;
    private Integer updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructeurs
    public User() {}
    
    public User(String username, String email, String firstName, String lastName, UserRole role) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.isActive = true;
        this.isLocked = false;
        this.failedLoginAttempts = 0;
        this.mustChangePassword = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
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
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
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
    
    public String getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Méthodes utilitaires
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public String getDisplayName() {
        return getFullName() + " (" + username + ")";
    }
    
    public boolean canLogin() {
        return isActive && !isLocked;
    }
    
    public boolean isPasswordExpired() {
        return passwordExpiresAt != null && LocalDateTime.now().isAfter(passwordExpiresAt);
    }
    
    public boolean hasPermission(Permission permission) {
        return RolePermissionManager.hasPermission(this.role, permission);
    }
    
    public boolean hasAnyPermission(Permission... permissions) {
        return RolePermissionManager.hasAnyPermission(this.role, permissions);
    }
    
    public boolean hasAllPermissions(Permission... permissions) {
        return RolePermissionManager.hasAllPermissions(this.role, permissions);
    }
    
    public boolean canManage(User otherUser) {
        return RolePermissionManager.canManageUser(this.role, otherUser.getRole());
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", role=" + role +
                ", isActive=" + isActive +
                ", isLocked=" + isLocked +
                ", department='" + department + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return id == user.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}