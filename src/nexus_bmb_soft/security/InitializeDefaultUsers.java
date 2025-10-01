package nexus_bmb_soft.security;

import nexus_bmb_soft.database.DatabaseConnection;
import java.sql.*;

/**
 * Utilitaire pour initialiser les utilisateurs par défaut avec des mots de passe sécurisés
 * À exécuter une seule fois après la création de la base de données
 * 
 * @author BlaiseMUBADI
 */
public class InitializeDefaultUsers {
    
    /**
     * Initialise les mots de passe des utilisateurs par défaut
     */
    public static void initializeDefaultPasswords() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            
                // Créer le compte admin par défaut s'il n'existe pas
                createDefaultAdminIfNotExists(conn);
            
                // Mot de passe par défaut pour l'admin (TOUS LES DROITS)
                String adminPassword = "Admin123!";
                String hashedAdminPassword = PasswordSecurity.hashPassword(adminPassword);
                updateUserPassword(conn, "admin", hashedAdminPassword);
            
                // Initialiser les mots de passe des utilisateurs migrés
                initializeMigratedUsers(conn);
            
                // Mots de passe par défaut pour les autres rôles (si ils existent)
                String gestionnairePassword = "Gestionnaire123!";
                String hashedGestionnairePassword = PasswordSecurity.hashPassword(gestionnairePassword);
                updateUserPasswordIfExists(conn, "gestionnaire", hashedGestionnairePassword);
            
                String mecanicienPassword = "Mecanicien123!";
                String hashedMecanicienPassword = PasswordSecurity.hashPassword(mecanicienPassword);
                updateUserPasswordIfExists(conn, "mecanicien", hashedMecanicienPassword);
            
                String chauffeurPassword = "Chauffeur123!";
                String hashedChauffeurPassword = PasswordSecurity.hashPassword(chauffeurPassword);
                updateUserPasswordIfExists(conn, "chauffeur", hashedChauffeurPassword);
            
            System.out.println("=== COMPTES UTILISATEURS INITIALISÉS ===");
            System.out.println("");
            System.out.println("🔐 ADMINISTRATEUR (TOUS LES DROITS) :");
            System.out.println("   Nom d'utilisateur : admin");
            System.out.println("   Mot de passe : " + adminPassword);
            System.out.println("   Droits : ADMINISTRATION COMPLÈTE");
            System.out.println("");
            System.out.println("👨‍💼 GESTIONNAIRE :");
            System.out.println("   Nom d'utilisateur : gestionnaire");
            System.out.println("   Mot de passe : " + gestionnairePassword);
            System.out.println("   Droits : Gestion flotte, affectations, rapports");
            System.out.println("");
            System.out.println("🔧 MÉCANICIEN :");
            System.out.println("   Nom d'utilisateur : mecanicien");
            System.out.println("   Mot de passe : " + mecanicienPassword);
            System.out.println("   Droits : Entretien, réparations, diagnostics");
            System.out.println("");
            System.out.println("🚗 CHAUFFEUR :");
            System.out.println("   Nom d'utilisateur : chauffeur");
            System.out.println("   Mot de passe : " + chauffeurPassword);
            System.out.println("   Droits : Consultation véhicules assignés");
            System.out.println("");
            System.out.println("⚠️  IMPORTANT : Changez ces mots de passe après la première connexion !");
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation des mots de passe : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Met à jour le mot de passe d'un utilisateur
     */
    private static void updateUserPassword(Connection conn, String username, String hashedPassword) throws SQLException {
           String sql = "UPDATE utilisateur SET password_hash = ? WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hashedPassword);
            stmt.setString(2, username);
            int rowsUpdated = stmt.executeUpdate();
            
            if (rowsUpdated > 0) {
                System.out.println("✅ Mot de passe initialisé pour : " + username);
            } else {
                System.out.println("❌ Échec pour : " + username);
            }
        }
    }
    
        /**
         * Crée le compte admin par défaut s'il n'existe pas
         */
        private static void createDefaultAdminIfNotExists(Connection conn) throws SQLException {
            String checkSql = "SELECT COUNT(*) FROM utilisateur WHERE username = 'admin'";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(checkSql)) {
            
                if (rs.next() && rs.getInt(1) == 0) {
                    // Créer le compte admin par défaut
                    String insertSql = """
                        INSERT INTO utilisateur (username, email, first_name, last_name, role, is_active, 
                                               employee_id, department, notes, must_change_password, password_hash) 
                        VALUES ('admin', 'admin@charroi-auto.com', 'Administrateur', 'Système', 'ADMIN', 1, 
                                'ADM001', 'Administration', 'Compte administrateur principal du système', 0, 
                                'TEMP_HASH_TO_REPLACE')
                    """;
                
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.executeUpdate();
                        System.out.println("✅ Compte admin par défaut créé");
                    }
                }
            }
        }
    
        /**
         * Initialise les mots de passe des utilisateurs migrés
         */
        private static void initializeMigratedUsers(Connection conn) throws SQLException {
            String selectSql = """
                SELECT username, first_name, last_name, role 
                FROM utilisateur 
                WHERE password_hash = 'MIGRATION_TEMP_HASH_TO_REPLACE'
            """;
        
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectSql)) {
            
                while (rs.next()) {
                    String username = rs.getString("username");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    String role = rs.getString("role");
                
                    // Générer un mot de passe temporaire basé sur le nom
                    String tempPassword = generateTempPassword(firstName, lastName);
                    String hashedPassword = PasswordSecurity.hashPassword(tempPassword);
                
                    updateUserPassword(conn, username, hashedPassword);
                
                    System.out.println("🔑 " + username + " (" + role + ") - mot de passe : " + tempPassword);
                }
            }
        }
    
        /**
         * Met à jour le mot de passe d'un utilisateur seulement s'il existe
         */
        private static void updateUserPasswordIfExists(Connection conn, String username, String hashedPassword) throws SQLException {
            String checkSql = "SELECT COUNT(*) FROM utilisateur WHERE username = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, username);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        updateUserPassword(conn, username, hashedPassword);
                    }
                }
            }
        }
    
        /**
         * Génère un mot de passe temporaire basé sur le nom
         */
        private static String generateTempPassword(String firstName, String lastName) {
            String base = (firstName != null ? firstName : "User") + "123!";
            return base.substring(0, Math.min(base.length(), 12));
        }
    
    /**
     * Méthode principale pour exécuter l'initialisation
     */
    public static void main(String[] args) {
        System.out.println("🚀 Initialisation des comptes utilisateurs par défaut...");
        System.out.println("");
        
        initializeDefaultPasswords();
        
        System.out.println("");
        System.out.println("✅ Initialisation terminée !");
        System.out.println("");
        System.out.println("📋 ÉTAPES SUIVANTES :");
        System.out.println("1. Connectez-vous avec le compte 'admin' et le mot de passe 'Admin123!'");
        System.out.println("2. Accédez au menu de gestion des utilisateurs");
        System.out.println("3. Créez de nouveaux comptes ou modifiez les mots de passe existants");
        System.out.println("4. Assignez les rôles appropriés aux utilisateurs");
    }
}