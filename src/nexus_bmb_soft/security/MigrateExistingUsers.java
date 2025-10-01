package nexus_bmb_soft.security;

import nexus_bmb_soft.database.DatabaseConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilitaire pour migrer les utilisateurs existants vers le système d'authentification sécurisé
 * Intègre la table "utilisateur" existante avec le nouveau système de sécurité
 * 
 * @author BlaiseMUBADI
 */
public class MigrateExistingUsers {
    
    /**
     * Migre tous les utilisateurs existants vers le nouveau système
     */
    public static void migrateUsers() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            System.out.println("🔄 MIGRATION DES UTILISATEURS EXISTANTS");
            System.out.println("==========================================");
            
            // Étape 1: Ajouter les nouvelles colonnes si elles n'existent pas
            addSecurityColumns(conn);
            
            // Étape 2: Migrer les utilisateurs existants
            migrateUserData(conn);
            
            // Étape 3: Créer les tables complémentaires
            createSecurityTables(conn);
            
            // Étape 4: Afficher le résumé
            displayMigrationSummary(conn);
            
            System.out.println("✅ MIGRATION TERMINÉE AVEC SUCCÈS !");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la migration : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Ajoute les nouvelles colonnes de sécurité à la table utilisateur existante
     */
    private static void addSecurityColumns(Connection conn) throws SQLException {
        System.out.println("📝 Ajout des colonnes de sécurité...");
        
        String[] alterStatements = {
            "ALTER TABLE utilisateur ADD COLUMN username VARCHAR(50) UNIQUE AFTER matricule",
            "ALTER TABLE utilisateur ADD COLUMN password_hash TEXT COMMENT 'Hash sécurisé SHA-256 avec salt' AFTER mot_de_passe_hash",
            "ALTER TABLE utilisateur ADD COLUMN first_name VARCHAR(50) AFTER password_hash",
            "ALTER TABLE utilisateur ADD COLUMN last_name VARCHAR(50) AFTER first_name",
            "ALTER TABLE utilisateur ADD COLUMN is_active TINYINT(1) DEFAULT 1 AFTER actif",
            "ALTER TABLE utilisateur ADD COLUMN is_locked TINYINT(1) DEFAULT 0 COMMENT 'Compte verrouillé' AFTER is_active",
            "ALTER TABLE utilisateur ADD COLUMN failed_login_attempts INT DEFAULT 0 COMMENT 'Tentatives échouées' AFTER is_locked",
            "ALTER TABLE utilisateur ADD COLUMN last_login TIMESTAMP NULL COMMENT 'Dernière connexion' AFTER failed_login_attempts",
            "ALTER TABLE utilisateur ADD COLUMN last_failed_login TIMESTAMP NULL COMMENT 'Dernière tentative échouée' AFTER last_login",
            "ALTER TABLE utilisateur ADD COLUMN password_expires_at TIMESTAMP NULL COMMENT 'Expiration mot de passe' AFTER last_failed_login",
            "ALTER TABLE utilisateur ADD COLUMN must_change_password TINYINT(1) DEFAULT 0 COMMENT 'Forcer changement' AFTER password_expires_at",
            "ALTER TABLE utilisateur ADD COLUMN phone VARCHAR(20) AFTER must_change_password",
            "ALTER TABLE utilisateur ADD COLUMN department VARCHAR(50) COMMENT 'Service' AFTER phone",
            "ALTER TABLE utilisateur ADD COLUMN employee_id VARCHAR(20) COMMENT 'Matricule employé' AFTER department",
            "ALTER TABLE utilisateur ADD COLUMN notes TEXT COMMENT 'Notes admin' AFTER employee_id",
            "ALTER TABLE utilisateur ADD COLUMN created_by INT AFTER notes",
            "ALTER TABLE utilisateur ADD COLUMN updated_by INT AFTER created_by"
        };
        
        for (String sql : alterStatements) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
            } catch (SQLException e) {
                // Ignorer si la colonne existe déjà
                if (!e.getMessage().contains("Duplicate column")) {
                    System.out.println("⚠️  " + e.getMessage());
                }
            }
        }
        
        // Modifier la colonne role pour supporter les nouveaux rôles
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE utilisateur MODIFY role ENUM('admin','gestionnaire','conducteur','conducteur_senior','super_admin','ADMIN','GESTIONNAIRE','MECANICIEN','CHAUFFEUR') NOT NULL");
        } catch (SQLException e) {
            System.out.println("⚠️  Modification colonne role : " + e.getMessage());
        }
        
        System.out.println("✅ Colonnes de sécurité ajoutées");
    }
    
    /**
     * Migre les données des utilisateurs existants
     */
    private static void migrateUserData(Connection conn) throws SQLException {
        System.out.println("🔄 Migration des données utilisateurs...");
        
        // Mapper les anciens rôles vers les nouveaux
        Map<String, String> roleMapping = new HashMap<>();
        roleMapping.put("admin", "ADMIN");
        roleMapping.put("super_admin", "ADMIN");
        roleMapping.put("gestionnaire", "GESTIONNAIRE");
        roleMapping.put("conducteur", "CHAUFFEUR");
        roleMapping.put("conducteur_senior", "MECANICIEN");
        
        // Récupérer tous les utilisateurs existants sans username
        String selectSql = "SELECT * FROM utilisateur WHERE username IS NULL OR username = ''";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSql)) {
            
            int migratedCount = 0;
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String matricule = rs.getString("matricule");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String oldRole = rs.getString("role");
                String email = rs.getString("email");
                boolean actif = rs.getBoolean("actif");
                
                // Générer un username unique
                String username = generateUsername(nom, prenom, id);
                
                // Mapper le rôle
                String newRole = roleMapping.getOrDefault(oldRole, "CHAUFFEUR");
                
                // Mettre à jour l'utilisateur
                String updateSql = """
                    UPDATE utilisateur SET 
                        username = ?,
                        first_name = ?,
                        last_name = ?,
                        role = ?,
                        is_active = ?,
                        employee_id = ?,
                        must_change_password = 1,
                        password_hash = 'MIGRATION_TEMP_HASH_TO_REPLACE',
                        email = CASE WHEN email = '' THEN CONCAT(?, '@charroi-auto.com') ELSE email END
                    WHERE id = ?
                """;
                
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, username);
                    updateStmt.setString(2, prenom);
                    updateStmt.setString(3, nom);
                    updateStmt.setString(4, newRole);
                    updateStmt.setBoolean(5, actif);
                    updateStmt.setString(6, matricule);
                    updateStmt.setString(7, username);
                    updateStmt.setInt(8, id);
                    
                    updateStmt.executeUpdate();
                    migratedCount++;
                    
                    System.out.println("✅ Migré: " + username + " (" + newRole + ")");
                }
            }
            
            System.out.println("✅ " + migratedCount + " utilisateurs migrés");
        }
    }
    
    /**
     * Génère un username unique basé sur le nom et prénom
     */
    private static String generateUsername(String nom, String prenom, int id) {
        StringBuilder username = new StringBuilder();
        
        if (prenom != null && !prenom.trim().isEmpty()) {
            username.append(prenom.trim().toLowerCase().replaceAll("[^a-zA-Z0-9]", ""));
        }
        
        if (nom != null && !nom.trim().isEmpty()) {
            String cleanNom = nom.trim().toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
            if (username.length() > 0) {
                username.append(".");
            }
            username.append(cleanNom);
        }
        
        // Si le username est vide ou trop court, utiliser l'ID
        if (username.length() < 3) {
            username = new StringBuilder("user" + id);
        }
        
        return username.toString();
    }
    
    /**
     * Crée les tables de sécurité complémentaires
     */
    private static void createSecurityTables(Connection conn) throws SQLException {
        System.out.println("🔧 Création des tables de sécurité...");
        
        // Table des sessions
        String sessionTableSql = """
            CREATE TABLE IF NOT EXISTS user_session (
                id INT NOT NULL AUTO_INCREMENT,
                user_id INT NOT NULL,
                session_token VARCHAR(255) NOT NULL UNIQUE,
                ip_address VARCHAR(45) DEFAULT NULL COMMENT 'Adresse IP de connexion',
                user_agent TEXT COMMENT 'Navigateur/Agent utilisateur',
                is_active TINYINT(1) NOT NULL DEFAULT 1,
                expires_at TIMESTAMP NOT NULL COMMENT 'Date expiration session',
                last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                PRIMARY KEY (id),
                KEY idx_user_id (user_id),
                KEY idx_session_token (session_token),
                KEY idx_is_active (is_active),
                KEY idx_expires_at (expires_at),
                FOREIGN KEY (user_id) REFERENCES utilisateur (id) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        """;
        
        // Table des logs d'authentification
        String authLogTableSql = """
            CREATE TABLE IF NOT EXISTS auth_log (
                id INT NOT NULL AUTO_INCREMENT,
                user_id INT DEFAULT NULL COMMENT 'NULL pour username inexistant',
                username VARCHAR(50) DEFAULT NULL,
                action ENUM('LOGIN_SUCCESS','LOGIN_FAILED','LOGOUT','PASSWORD_CHANGED','ACCOUNT_LOCKED','ACCOUNT_UNLOCKED','PASSWORD_RESET','SESSION_EXPIRED') NOT NULL,
                ip_address VARCHAR(45) DEFAULT NULL,
                user_agent TEXT,
                details JSON DEFAULT NULL COMMENT 'Détails supplémentaires en JSON',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                PRIMARY KEY (id),
                KEY idx_user_id (user_id),
                KEY idx_username (username),
                KEY idx_action (action),
                KEY idx_created_at (created_at),
                KEY idx_ip_address (ip_address),
                FOREIGN KEY (user_id) REFERENCES utilisateur (id) ON DELETE SET NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        """;
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sessionTableSql);
            System.out.println("✅ Table user_session créée");
            
            stmt.execute(authLogTableSql);
            System.out.println("✅ Table auth_log créée");
        }
    }
    
    /**
     * Affiche un résumé de la migration
     */
    private static void displayMigrationSummary(Connection conn) throws SQLException {
        System.out.println("\n📊 RÉSUMÉ DE LA MIGRATION");
        System.out.println("==========================");
        
        String summarySql = """
            SELECT 
                COUNT(*) as total_utilisateurs,
                SUM(CASE WHEN username IS NOT NULL AND username != '' THEN 1 ELSE 0 END) as utilisateurs_migres,
                SUM(CASE WHEN password_hash = 'MIGRATION_TEMP_HASH_TO_REPLACE' THEN 1 ELSE 0 END) as mots_de_passe_a_initialiser
            FROM utilisateur
        """;
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(summarySql)) {
            
            if (rs.next()) {
                System.out.println("👥 Total utilisateurs : " + rs.getInt("total_utilisateurs"));
                System.out.println("✅ Utilisateurs migrés : " + rs.getInt("utilisateurs_migres"));
                System.out.println("🔑 Mots de passe à initialiser : " + rs.getInt("mots_de_passe_a_initialiser"));
            }
        }
        
        // Afficher les utilisateurs migrés
        System.out.println("\n👤 UTILISATEURS MIGRÉS:");
        String usersSql = """
            SELECT 
                username,
                CONCAT(first_name, ' ', last_name) as nom_complet,
                role,
                employee_id,
                email,
                CASE WHEN password_hash = 'MIGRATION_TEMP_HASH_TO_REPLACE' THEN 'À INITIALISER' ELSE 'MIGRÉ' END as statut_password
            FROM utilisateur
            WHERE username IS NOT NULL AND username != ''
            ORDER BY role, username
        """;
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(usersSql)) {
            
            while (rs.next()) {
                System.out.printf("   🔹 %s (%s) - %s - %s - %s%n",
                    rs.getString("username"),
                    rs.getString("nom_complet"),
                    rs.getString("role"),
                    rs.getString("employee_id"),
                    rs.getString("statut_password")
                );
            }
        }
    }
    
    /**
     * Méthode principale pour exécuter la migration
     */
    public static void main(String[] args) {
        System.out.println("🚀 MIGRATION SYSTÈME D'AUTHENTIFICATION");
        System.out.println("Intégration avec la base existante bdd_charroi_auto");
        System.out.println("====================================================");
        System.out.println();
        
        migrateUsers();
        
        System.out.println();
        System.out.println("📋 ÉTAPES SUIVANTES :");
        System.out.println("1. Exécutez InitializeDefaultUsers.java pour créer les mots de passe sécurisés");
        System.out.println("2. Les utilisateurs migrés devront changer leur mot de passe au premier login");
        System.out.println("3. Connectez-vous avec les comptes migrés pour tester le système");
        System.out.println();
        System.out.println("🔐 COMPTES ADMIN DISPONIBLES :");
        System.out.println("   - Compte 'admin' (créé par défaut) : admin / Admin123!");
        System.out.println("   - Comptes migrés avec rôle ADMIN : voir la liste ci-dessus");
    }
}