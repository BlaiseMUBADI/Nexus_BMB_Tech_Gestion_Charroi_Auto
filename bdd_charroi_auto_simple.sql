-- =========================================================
-- BASE DE DONNÉES SIMPLIFIÉE POUR COMPATIBILITÉ JAVA
-- Cette version utilise la structure originale attendue par votre app
-- =========================================================

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";
SET FOREIGN_KEY_CHECKS = 0;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

-- Supprimer toutes les tables existantes
DROP TABLE IF EXISTS `log_securite`;
DROP TABLE IF EXISTS `utilisateur_permission`;
DROP TABLE IF EXISTS `role_permission`;
DROP TABLE IF EXISTS `permission`;
DROP TABLE IF EXISTS `alerte_entretien`;
DROP TABLE IF EXISTS `historique_kilometrage`;
DROP TABLE IF EXISTS `planification_entretien`;
DROP TABLE IF EXISTS `entretien`;
DROP TABLE IF EXISTS `type_entretien`;
DROP TABLE IF EXISTS `affectation`;
DROP TABLE IF EXISTS `vehicule`;
DROP TABLE IF EXISTS `utilisateur`;

-- --------------------------------------------------------
-- Table utilisateur SIMPLIFIÉE (structure originale)
-- --------------------------------------------------------

CREATE TABLE IF NOT EXISTS `utilisateur` (
  `id` int NOT NULL AUTO_INCREMENT,
  `matricule` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nom` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `prenom` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` enum('admin','gestionnaire','conducteur','conducteur_senior','super_admin') COLLATE utf8mb4_unicode_ci NOT NULL,
  `mot_de_passe_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `actif` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `statut` enum('ACTIF','INACTIF','SUSPENDU') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIF',
  `date_creation` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_role` (`role`),
  KEY `idx_actif` (`actif`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Données utilisateurs SIMPLIFIÉES
INSERT INTO `utilisateur` (`id`, `matricule`, `nom`, `prenom`, `role`, `mot_de_passe_hash`, `actif`, `email`, `statut`) VALUES
(1, 'ADMIN001', 'ADMINISTRATEUR', 'Système', 'super_admin', 'Admin12345', 1, 'admin@charroi.system', 'ACTIF'),
(2, 'TEST01', 'UTILISATEUR', 'Test', 'admin', '123456', 1, 'test@charroi.local', 'ACTIF'),
(3, 'COND01', 'KAPINGA', 'Papy', 'conducteur_senior', '123456', 1, 'blaise@gmail.com', 'ACTIF'),
(4, 'GES01', 'BADIBANGA', 'Jeampy', 'gestionnaire', '123456', 1, 'jeampy@gmail.com', 'ACTIF'),
(5, 'COND02', 'Colonel Tshibanda', 'Jean Didier', 'admin', 'password123', 1, 'colonel@charroi.local', 'ACTIF'),
(6, 'COND03', 'Major Kabila', 'Jacques', 'conducteur', 'password123', 1, 'major@charroi.local', 'ACTIF');

-- --------------------------------------------------------
-- Table vehicule SIMPLIFIÉE
-- --------------------------------------------------------

CREATE TABLE IF NOT EXISTS `vehicule` (
  `id` int NOT NULL AUTO_INCREMENT,
  `matricule` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `immatriculation` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `marque` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `modele` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `annee` int DEFAULT NULL,
  `couleur` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `carburant` enum('ESSENCE','DIESEL','HYBRIDE','ELECTRIQUE') COLLATE utf8mb4_unicode_ci DEFAULT 'ESSENCE',
  `kilometrage_actuel` int DEFAULT 0,
  `statut` enum('DISPONIBLE','AFFECTE','MAINTENANCE','HORS_SERVICE') COLLATE utf8mb4_unicode_ci DEFAULT 'DISPONIBLE',
  `responsable_id` int DEFAULT NULL,
  `actif` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `matricule` (`matricule`),
  KEY `idx_matricule` (`matricule`),
  KEY `idx_statut` (`statut`),
  KEY `idx_responsable_id` (`responsable_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Données véhicules
INSERT INTO `vehicule` (`id`, `matricule`, `immatriculation`, `marque`, `modele`, `type`, `annee`, `couleur`, `carburant`, `kilometrage_actuel`, `statut`, `responsable_id`) VALUES
(1, 'ABC123', 'CD-001-KIN', 'Toyota', 'Hilux', 'Pickup', 2018, 'Blanc', 'DIESEL', 46200, 'DISPONIBLE', 3),
(2, 'DEF456', 'CD-002-KIN', 'Hyundai', 'Tucson', 'SUV', 2020, 'Noir', 'ESSENCE', 39500, 'AFFECTE', 2),
(3, 'GHI789', 'CD-003-KIN', 'Ford', 'Transit', 'Camionnette', 2016, 'Bleu', 'DIESEL', 28000, 'MAINTENANCE', 1);

-- --------------------------------------------------------
-- Table affectation SIMPLIFIÉE
-- --------------------------------------------------------

CREATE TABLE IF NOT EXISTS `affectation` (
  `id` int NOT NULL AUTO_INCREMENT,
  `vehicule_id` int NOT NULL,
  `conducteur_id` int NOT NULL,
  `date_debut` date NOT NULL,
  `date_fin` date DEFAULT NULL,
  `motif` text COLLATE utf8mb4_unicode_ci,
  `statut` enum('programmee','en_cours','terminee') COLLATE utf8mb4_unicode_ci DEFAULT 'programmee',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_vehicule_id` (`vehicule_id`),
  KEY `idx_conducteur_id` (`conducteur_id`),
  KEY `idx_statut` (`statut`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Données affectations
INSERT INTO `affectation` (`id`, `vehicule_id`, `conducteur_id`, `date_debut`, `date_fin`, `motif`, `statut`) VALUES
(1, 2, 4, '2025-09-25', '2025-10-10', 'Mission de service', 'en_cours');

-- --------------------------------------------------------
-- Table entretien SIMPLIFIÉE
-- --------------------------------------------------------

CREATE TABLE IF NOT EXISTS `entretien` (
  `id` int NOT NULL AUTO_INCREMENT,
  `vehicule_id` int NOT NULL,
  `type_entretien` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `date_programmee` date DEFAULT NULL,
  `date_realisation` date DEFAULT NULL,
  `kilometrage` int DEFAULT NULL,
  `statut` enum('PLANIFIE','EN_COURS','TERMINE','ANNULE') COLLATE utf8mb4_unicode_ci DEFAULT 'PLANIFIE',
  `description` text COLLATE utf8mb4_unicode_ci,
  `cout_reel` decimal(10,2) DEFAULT NULL,
  `mecanicien` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_vehicule_id` (`vehicule_id`),
  KEY `idx_statut` (`statut`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Données entretiens
INSERT INTO `entretien` (`vehicule_id`, `type_entretien`, `date_programmee`, `date_realisation`, `kilometrage`, `statut`, `description`, `cout_reel`, `mecanicien`) VALUES
(1, 'Vidange moteur', '2025-09-10', '2025-09-10', 45000, 'TERMINE', 'Vidange moteur complète + filtre', 85.00, 'Jean Mécanicien'),
(2, 'Changement pneus', '2025-07-15', '2025-07-15', 38000, 'TERMINE', 'Remplacement pneus arrière usés', 420.00, 'Paul Pneus'),
(3, 'Révision freins', '2025-10-15', NULL, NULL, 'PLANIFIE', 'Révision plaquettes de frein programmée', NULL, NULL);

-- --------------------------------------------------------
-- CONTRAINTES SIMPLIFIÉES
-- --------------------------------------------------------

ALTER TABLE `affectation`
  ADD CONSTRAINT `fk_affectation_conducteur` FOREIGN KEY (`conducteur_id`) REFERENCES `utilisateur` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_affectation_vehicule` FOREIGN KEY (`vehicule_id`) REFERENCES `vehicule` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `entretien`
  ADD CONSTRAINT `fk_entretien_vehicule` FOREIGN KEY (`vehicule_id`) REFERENCES `vehicule` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `vehicule`
  ADD CONSTRAINT `fk_vehicule_responsable` FOREIGN KEY (`responsable_id`) REFERENCES `utilisateur` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

-- Réactiver les contraintes
SET FOREIGN_KEY_CHECKS = 1;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

-- ===============================================
-- COMPTES DE TEST GARANTIS FONCTIONNELS
-- ===============================================
/*
UTILISEZ CES COMPTES POUR TESTER :

1. ADMIN001 / Admin12345 (super_admin)
2. TEST01 / 123456 (admin) 
3. COND01 / 123456 (conducteur_senior)
4. GES01 / 123456 (gestionnaire)
5. COND02 / password123 (admin)
6. COND03 / password123 (conducteur)

TOUS ont actif=1 et statut='ACTIF'
Structure simplifiée compatible avec votre app Java
*/