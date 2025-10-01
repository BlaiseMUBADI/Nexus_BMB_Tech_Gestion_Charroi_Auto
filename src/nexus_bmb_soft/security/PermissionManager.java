package nexus_bmb_soft.security;

import nexus_bmb_soft.database.DatabaseConnection;
import nexus_bmb_soft.models.Utilisateur;
import java.sql.*;

/**
 * Gestionnaire des permissions pour le nouveau système sécurisé
 * Compatible avec l'ancienne structure utilisateur
 * 
 * @author BlaiseMUBADI
 */
public class PermissionManager {
    
    /**
     * Vérifie si un utilisateur a une permission spécifique
     * Compatible avec l'ancien et le nouveau système
     */
    public static boolean hasPermission(Utilisateur user, String permissionCode) {
        if (user == null) return false;
        
        // Utiliser d'abord la logique intégrée dans Utilisateur
        boolean hasBasicPermission = user.hasSystemPermission(permissionCode);
        
        // Si on a la nouvelle base de données, vérifier aussi via SQL
        try {
            return hasBasicPermission || checkDatabasePermission(user.getId(), permissionCode);
        } catch (Exception e) {
            // Fallback sur la logique de base si la nouvelle base n'est pas encore déployée
            return hasBasicPermission;
        }
    }
    
    /**
     * Vérifie les permissions via la base de données (nouveau système)
     */
    private static boolean checkDatabasePermission(int userId, String permissionCode) {
        String sql = """
            SELECT CASE 
                WHEN up.granted IS NOT NULL THEN up.granted 
                WHEN rp.granted IS NOT NULL THEN rp.granted 
                ELSE 0 
            END as has_permission
            FROM utilisateur u
            CROSS JOIN permission p
            LEFT JOIN role_permission rp ON rp.role = u.role AND rp.permission_id = p.id
            LEFT JOIN utilisateur_permission up ON up.utilisateur_id = u.id AND up.permission_id = p.id
            WHERE u.id = ? AND p.code = ? AND u.actif = 1 AND u.statut = 'ACTIF' AND p.actif = 1
            AND (up.date_expiration IS NULL OR up.date_expiration >= CURRENT_DATE)
            LIMIT 1
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, permissionCode);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("has_permission");
            }
        } catch (SQLException e) {
            System.err.println("Erreur vérification permission BDD: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Méthodes utilitaires pour les permissions courantes
     */
    public static boolean canCreateVehicle(Utilisateur user) {
        return hasPermission(user, "VEHICLE_CREATE");
    }
    
    public static boolean canUpdateVehicle(Utilisateur user) {
        return hasPermission(user, "VEHICLE_UPDATE");
    }
    
    public static boolean canDeleteVehicle(Utilisateur user) {
        return hasPermission(user, "VEHICLE_DELETE");
    }
    
    public static boolean canCreateMaintenance(Utilisateur user) {
        return hasPermission(user, "MAINTENANCE_CREATE");
    }
    
    public static boolean canUpdateMaintenance(Utilisateur user) {
        return hasPermission(user, "MAINTENANCE_UPDATE");
    }
    
    public static boolean canDeleteMaintenance(Utilisateur user) {
        return hasPermission(user, "MAINTENANCE_DELETE");
    }
    
    public static boolean canCreateUser(Utilisateur user) {
        return hasPermission(user, "USER_CREATE");
    }
    
    public static boolean canUpdateUser(Utilisateur user) {
        return hasPermission(user, "USER_UPDATE");
    }
    
    public static boolean canDeleteUser(Utilisateur user) {
        return hasPermission(user, "USER_DELETE");
    }
    
    public static boolean canCreateAssignment(Utilisateur user) {
        return hasPermission(user, "ASSIGNMENT_CREATE");
    }
    
    public static boolean canViewReports(Utilisateur user) {
        return hasPermission(user, "REPORT_VIEW");
    }
    
    public static boolean canExportReports(Utilisateur user) {
        return hasPermission(user, "REPORT_EXPORT");
    }
    
    public static boolean canManageAlerts(Utilisateur user) {
        return hasPermission(user, "ALERT_MANAGE");
    }
    
    public static boolean isSystemAdmin(Utilisateur user) {
        return hasPermission(user, "SYSTEM_ADMIN");
    }
    
    /**
     * Authentification avec le nouveau système
     */
    public static Utilisateur authenticate(String login, String password) {
        // Priorité au matricule si c'est le format ADMIN001, COND001, etc.
        String sql;
        if (login.matches("^[A-Z]+\\d+$")) {
            sql = "SELECT * FROM utilisateur WHERE matricule = ? AND actif = 1 AND statut = 'ACTIF'";
        } else {
            sql = "SELECT * FROM utilisateur WHERE (matricule = ? OR nom LIKE ?) AND actif = 1 AND statut = 'ACTIF'";
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, login);
            if (!login.matches("^[A-Z]+\\d+$")) {
                stmt.setString(2, "%" + login + "%");
            }
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Vérification simple du mot de passe (à améliorer avec hash)
                String storedHash = rs.getString("mot_de_passe_hash");
                
                // Support pour l'ancien et nouveau hash
                boolean passwordMatch = false;
                if (storedHash.length() == 64) {
                    // Nouveau hash SHA-256
                    String expectedHash = hashPassword(password);
                    passwordMatch = expectedHash.equals(storedHash);
                } else {
                    // Ancien système ou hash simple
                    passwordMatch = password.equals(storedHash) || 
                                  hashPassword(password).equals(storedHash);
                }
                
                if (passwordMatch) {
                    // Créer l'objet utilisateur
                    Utilisateur user = new Utilisateur();
                    user.setId(rs.getInt("id"));
                    user.setNom(rs.getString("nom"));
                    user.setPrenom(rs.getString("prenom"));
                    user.setMatricule(rs.getString("matricule"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(nexus_bmb_soft.models.RoleUtilisateur.fromString(rs.getString("role")));
                    user.setStatut(rs.getString("statut"));
                    user.setMotDePasseHash(storedHash);
                    
                    // Mettre à jour dernière connexion
                    updateLastLogin(user.getId());
                    
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur authentification: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Hash simple du mot de passe (SHA-256 + salt)
     */
    private static String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            String saltedPassword = password + "BMB_SALT_2025";
            byte[] hashedBytes = md.digest(saltedPassword.getBytes("UTF-8"));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return password; // Fallback
        }
    }
    
    /**
     * Met à jour la dernière connexion
     */
    private static void updateLastLogin(int userId) {
        String sql = "UPDATE utilisateur SET derniere_connexion = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Ignore si les nouvelles colonnes n'existent pas encore
        }
    }
}