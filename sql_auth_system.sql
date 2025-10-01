-- ============================================================================
-- SCRIPT DE CRÉATION DES TABLES D'AUTHENTIFICATION ET DE SÉCURITÉ
-- Système de gestion des utilisateurs avec rôles et permissions
-- ============================================================================

-- Table des utilisateurs du système
DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL UNIQUE,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL UNIQUE,
  `password_hash` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Hash sécurisé du mot de passe avec salt',
  `first_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `last_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` enum('ADMIN','GESTIONNAIRE','MECANICIEN','CHAUFFEUR') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CHAUFFEUR',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Compte actif ou désactivé',
  `is_locked` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Compte verrouillé après tentatives échouées',
  `failed_login_attempts` int NOT NULL DEFAULT '0' COMMENT 'Nombre de tentatives de connexion échouées',
  `last_login` timestamp NULL DEFAULT NULL COMMENT 'Dernière connexion réussie',
  `last_failed_login` timestamp NULL DEFAULT NULL COMMENT 'Dernière tentative échouée',
  `password_expires_at` timestamp NULL DEFAULT NULL COMMENT 'Date d\'expiration du mot de passe',
  `must_change_password` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Forcer le changement de mot de passe',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `department` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Service/Département',
  `employee_id` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Matricule employé',
  `notes` text COLLATE utf8mb4_unicode_ci COMMENT 'Notes administratives',
  `created_by` int DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_role` (`role`),
  KEY `idx_is_active` (`is_active`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_created_by` (`created_by`),
  KEY `idx_last_login` (`last_login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des sessions utilisateur
DROP TABLE IF EXISTS `user_session`;
CREATE TABLE IF NOT EXISTS `user_session` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `session_token` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL UNIQUE,
  `ip_address` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Adresse IP de connexion',
  `user_agent` text COLLATE utf8mb4_unicode_ci COMMENT 'Navigateur/Agent utilisateur',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `expires_at` timestamp NOT NULL COMMENT 'Date d\'expiration de la session',
  `last_activity` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_session_token` (`session_token`),
  KEY `idx_is_active` (`is_active`),
  KEY `idx_expires_at` (`expires_at`),
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des logs d'authentification
DROP TABLE IF EXISTS `auth_log`;
CREATE TABLE IF NOT EXISTS `auth_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL COMMENT 'NULL pour les tentatives avec username inexistant',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `action` enum('LOGIN_SUCCESS','LOGIN_FAILED','LOGOUT','PASSWORD_CHANGED','ACCOUNT_LOCKED','ACCOUNT_UNLOCKED','PASSWORD_RESET','SESSION_EXPIRED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `ip_address` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_agent` text COLLATE utf8mb4_unicode_ci,
  `details` json DEFAULT NULL COMMENT 'Détails supplémentaires en JSON',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_username` (`username`),
  KEY `idx_action` (`action`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_ip_address` (`ip_address`),
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table de liaison entre conducteurs et utilisateurs
DROP TABLE IF EXISTS `conducteur_user`;
CREATE TABLE IF NOT EXISTS `conducteur_user` (
  `conducteur_id` int NOT NULL,
  `user_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`conducteur_id`, `user_id`),
  KEY `idx_user_id` (`user_id`),
  FOREIGN KEY (`conducteur_id`) REFERENCES `conducteur` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- DONNÉES D'EXEMPLE - UTILISATEURS PAR DÉFAUT
-- ============================================================================

-- Mot de passe par défaut : "Admin123!" (sera hashé par l'application)
INSERT INTO `user` (`username`, `email`, `password_hash`, `first_name`, `last_name`, `role`, `is_active`, `employee_id`, `department`, `notes`) VALUES
('admin', 'admin@charroi-auto.com', 'TEMP_HASH_TO_REPLACE', 'Administrateur', 'Système', 'ADMIN', 1, 'ADM001', 'Administration', 'Compte administrateur principal du système'),
('gestionnaire', 'gestionnaire@charroi-auto.com', 'TEMP_HASH_TO_REPLACE', 'Jean', 'Dupont', 'GESTIONNAIRE', 1, 'GES001', 'Gestion Flotte', 'Gestionnaire principal de la flotte automobile'),
('mecanicien', 'mecanicien@charroi-auto.com', 'TEMP_HASH_TO_REPLACE', 'Pierre', 'Martin', 'MECANICIEN', 1, 'MEC001', 'Atelier', 'Mécanicien principal de l\'atelier'),
('chauffeur', 'chauffeur@charroi-auto.com', 'TEMP_HASH_TO_REPLACE', 'Marie', 'Bernard', 'CHAUFFEUR', 1, 'CHA001', 'Transport', 'Chauffeur exemple pour tests');

-- ============================================================================
-- PROCÉDURES STOCKÉES UTILITAIRES
-- ============================================================================

DELIMITER //

-- Procédure pour nettoyer les sessions expirées
CREATE OR REPLACE PROCEDURE CleanExpiredSessions()
BEGIN
    DELETE FROM user_session 
    WHERE expires_at < NOW() OR is_active = 0;
    
    SELECT ROW_COUNT() as sessions_cleaned;
END //

-- Procédure pour verrouiller un compte après trop de tentatives
CREATE OR REPLACE PROCEDURE LockUserAccount(IN p_user_id INT)
BEGIN
    UPDATE user 
    SET is_locked = 1, 
        failed_login_attempts = failed_login_attempts + 1,
        last_failed_login = NOW()
    WHERE id = p_user_id;
    
    -- Log de l'action
    INSERT INTO auth_log (user_id, action, details) 
    VALUES (p_user_id, 'ACCOUNT_LOCKED', JSON_OBJECT('reason', 'too_many_failed_attempts'));
END //

-- Procédure pour réinitialiser les tentatives échouées
CREATE OR REPLACE PROCEDURE ResetFailedAttempts(IN p_user_id INT)
BEGIN
    UPDATE user 
    SET failed_login_attempts = 0,
        is_locked = 0
    WHERE id = p_user_id;
    
    -- Log de l'action
    INSERT INTO auth_log (user_id, action) 
    VALUES (p_user_id, 'ACCOUNT_UNLOCKED');
END //

-- Fonction pour vérifier si un utilisateur est actif et non verrouillé
CREATE OR REPLACE FUNCTION IsUserValid(p_user_id INT) 
RETURNS BOOLEAN
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE user_count INT DEFAULT 0;
    
    SELECT COUNT(*) INTO user_count
    FROM user 
    WHERE id = p_user_id 
      AND is_active = 1 
      AND is_locked = 0;
    
    RETURN user_count > 0;
END //

DELIMITER ;

-- ============================================================================
-- VUES UTILITAIRES
-- ============================================================================

-- Vue des utilisateurs actifs avec leurs dernières connexions
CREATE OR REPLACE VIEW v_active_users AS
SELECT 
    u.id,
    u.username,
    u.email,
    u.first_name,
    u.last_name,
    u.role,
    u.department,
    u.employee_id,
    u.last_login,
    u.failed_login_attempts,
    CASE 
        WHEN u.is_locked = 1 THEN 'Verrouillé'
        WHEN u.is_active = 0 THEN 'Inactif'
        ELSE 'Actif'
    END as status,
    COALESCE(sessions.active_sessions, 0) as active_sessions
FROM user u
LEFT JOIN (
    SELECT user_id, COUNT(*) as active_sessions
    FROM user_session 
    WHERE is_active = 1 AND expires_at > NOW()
    GROUP BY user_id
) sessions ON u.id = sessions.user_id
WHERE u.is_active = 1
ORDER BY u.last_login DESC;

-- Vue des statistiques d'authentification
CREATE OR REPLACE VIEW v_auth_stats AS
SELECT 
    DATE(created_at) as date,
    action,
    COUNT(*) as count
FROM auth_log 
WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY DATE(created_at), action
ORDER BY date DESC, action;

-- ============================================================================
-- INDEX POUR PERFORMANCES
-- ============================================================================

-- Index composés pour améliorer les performances des requêtes courantes
CREATE INDEX idx_user_role_active ON user (role, is_active);
CREATE INDEX idx_user_login_info ON user (username, is_active, is_locked);
CREATE INDEX idx_session_user_active ON user_session (user_id, is_active, expires_at);
CREATE INDEX idx_auth_log_user_date ON auth_log (user_id, created_at);

-- ============================================================================
-- TRIGGERS DE SÉCURITÉ
-- ============================================================================

DELIMITER //

-- Trigger pour logger les modifications d'utilisateurs
CREATE OR REPLACE TRIGGER tr_user_changes
    AFTER UPDATE ON user
    FOR EACH ROW
BEGIN
    IF OLD.is_active != NEW.is_active THEN
        INSERT INTO auth_log (user_id, action, details)
        VALUES (NEW.id, IF(NEW.is_active = 1, 'ACCOUNT_UNLOCKED', 'ACCOUNT_LOCKED'), 
                JSON_OBJECT('changed_by', NEW.updated_by));
    END IF;
    
    IF OLD.password_hash != NEW.password_hash THEN
        INSERT INTO auth_log (user_id, action, details)
        VALUES (NEW.id, 'PASSWORD_CHANGED', 
                JSON_OBJECT('changed_by', NEW.updated_by));
    END IF;
END //

DELIMITER ;

-- ============================================================================
-- COMMENTAIRES ET DOCUMENTATION
-- ============================================================================

/*
SYSTÈME D'AUTHENTIFICATION - DOCUMENTATION

1. RÔLES ET HIÉRARCHIE :
   - ADMIN (niveau 1) : Accès complet
   - GESTIONNAIRE (niveau 2) : Gestion de flotte
   - MECANICIEN (niveau 3) : Entretiens et réparations
   - CHAUFFEUR (niveau 4) : Consultation limitée

2. SÉCURITÉ :
   - Mots de passe hashés avec salt (SHA-256 + itérations)
   - Sessions avec expiration automatique
   - Verrouillage automatique après échecs
   - Logs complets d'authentification

3. PERFORMANCE :
   - Index optimisés pour les requêtes fréquentes
   - Vues pré-calculées pour les statistiques
   - Procédures stockées pour les opérations courantes

4. MAINTENANCE :
   - Nettoyage automatique des sessions expirées
   - Archivage des logs anciens (à implémenter)
   - Surveillance des tentatives suspectes
*/