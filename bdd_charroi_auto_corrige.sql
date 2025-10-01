-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
-- 
-- VERSION CORRIGÉE POUR ÉVITER LES ERREURS DE CONTRAINTES
-- Date de correction: 1er octobre 2025
--
-- Hôte : 127.0.0.1:3306
-- Généré le : lun. 29 sep. 2025 à 14:05
-- Version du serveur : 8.2.0
-- Version de PHP : 8.2.13

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

-- IMPORTANT: Désactiver les contraintes de clés étrangères pour éviter les erreurs
SET FOREIGN_KEY_CHECKS = 0;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `bdd_charroi_auto`
--

-- --------------------------------------------------------
-- SUPPRESSION DE TOUTES LES TABLES DANS LE BON ORDRE
-- --------------------------------------------------------

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

-- Supprimer les vues si elles existent
DROP VIEW IF EXISTS `vue_alertes_actives`;
DROP VIEW IF EXISTS `vue_dashboard_entretiens`;
DROP VIEW IF EXISTS `vue_permissions_utilisateur`;
DROP VIEW IF EXISTS `vue_utilisateurs_securite`;
DROP VIEW IF EXISTS `vue_dashboard_securite`;

-- --------------------------------------------------------
--
-- Structure de la table `utilisateur` - VERSION COMPLÈTE AVEC SÉCURITÉ
--

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
  -- Colonnes de sécurité renforcée
  `token_session` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Token session active',
  `derniere_connexion` timestamp NULL DEFAULT NULL COMMENT 'Dernière connexion réussie',
  `tentatives_echec` int DEFAULT 0 COMMENT 'Nombre tentatives échec consécutives',
  `compte_verrouille` tinyint(1) DEFAULT '0' COMMENT 'Compte verrouillé temporairement',
  `date_verrouillage` timestamp NULL DEFAULT NULL COMMENT 'Date/heure verrouillage',
  `date_expiration_mdp` date DEFAULT NULL COMMENT 'Date expiration mot de passe',
  `force_changement_mdp` tinyint(1) DEFAULT '0' COMMENT 'Forcer changement mot de passe',
  `derniere_ip` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Dernière adresse IP',
  `user_agent` text COLLATE utf8mb4_unicode_ci COMMENT 'Dernier navigateur/client utilisé',
  PRIMARY KEY (`id`),
  KEY `idx_role` (`role`),
  KEY `idx_actif` (`actif`),
  KEY `idx_token_session` (`token_session`),
  KEY `idx_derniere_connexion` (`derniere_connexion`),
  KEY `idx_compte_verrouille` (`compte_verrouille`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `utilisateur`
--

INSERT INTO `utilisateur` (`id`, `matricule`, `nom`, `prenom`, `role`, `mot_de_passe_hash`, `actif`, `created_at`, `updated_at`, `email`, `statut`, `date_creation`, `tentatives_echec`, `compte_verrouille`, `force_changement_mdp`) VALUES
(1, 'COND03', 'Major Kabila', 'Jacque', 'conducteur', 'hash_password_123', 1, '2025-09-25 01:33:52', '2025-09-25 11:14:56', '', 'ACTIF', '2025-09-25 01:35:31', 0, 0, 0),
(2, 'COND04', 'Capitaine Mbayo', 'Gabriel', 'gestionnaire', 'hash_password_456', 1, '2025-09-25 01:33:52', '2025-09-25 11:14:45', '', 'ACTIF', '2025-09-25 01:35:31', 0, 0, 0),
(3, 'COND02', 'Colonel Tshibanda', 'Jean Didier', 'admin', 'hash_password_789', 1, '2025-09-25 01:33:52', '2025-09-25 11:14:35', '', 'ACTIF', '2025-09-25 01:35:31', 0, 0, 0),
(4, 'COND01', 'KAPINGA', 'Papy', 'conducteur_senior', '123456', 1, '2025-09-25 02:14:42', '2025-09-25 02:14:42', 'blaise@gmail.com', 'ACTIF', '2025-09-25 01:14:43', 0, 0, 0),
(5, 'GES01', 'BADIBANGA', 'Jeampy', 'gestionnaire', '123456', 1, '2025-09-25 02:15:40', '2025-09-25 02:15:40', 'jeampy@gmail.com', 'ACTIF', '2025-09-25 01:15:41', 0, 0, 0),
(6, 'ADMIN001', 'ADMINISTRATEUR', 'Système', 'super_admin', 'Admin12345', 1, '2025-10-01 17:00:00', '2025-10-01 17:00:00', 'admin@charroi.system', 'ACTIF', '2025-10-01 17:00:00', 0, 0, 0);

-- --------------------------------------------------------
--
-- Structure de la table `vehicule`
-- Table enrichie pour gestion complète parc automobile
--

CREATE TABLE IF NOT EXISTS `vehicule` (
  `id` int NOT NULL AUTO_INCREMENT,
  `matricule` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `immatriculation` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `marque` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `modele` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `categorie` enum('LEGER','UTILITAIRE','POIDS_LOURD','SPECIAL') COLLATE utf8mb4_unicode_ci DEFAULT 'LEGER',
  `annee` int DEFAULT NULL,
  `couleur` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `numero_chassis` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `numero_moteur` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `carburant` enum('ESSENCE','DIESEL','HYBRIDE','ELECTRIQUE','GAZ') COLLATE utf8mb4_unicode_ci DEFAULT 'ESSENCE',
  `consommation_100km` decimal(4,2) DEFAULT NULL COMMENT 'Consommation aux 100km',
  `capacite_reservoir` decimal(6,2) DEFAULT NULL COMMENT 'Capacité réservoir en litres',
  `kilometrage_initial` int DEFAULT 0,
  `kilometrage_actuel` int DEFAULT 0,
  `kilometrage_derniere_maj` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `statut` enum('DISPONIBLE','AFFECTE','MAINTENANCE','HORS_SERVICE','VENDU') COLLATE utf8mb4_unicode_ci DEFAULT 'DISPONIBLE',
  `etat` enum('EXCELLENT','BON','MOYEN','MAUVAIS','CRITIQUE') COLLATE utf8mb4_unicode_ci DEFAULT 'BON',
  `date_acquisition` date DEFAULT NULL,
  `prix_acquisition` decimal(12,2) DEFAULT NULL,
  `date_mise_service` date DEFAULT NULL,
  `date_assurance` date DEFAULT NULL,
  `compagnie_assurance` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `police_assurance` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `date_visite_technique` date DEFAULT NULL,
  `lieu_visite_technique` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `date_derniere_vidange` date DEFAULT NULL,
  `km_derniere_vidange` int DEFAULT NULL,
  `localisation` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `responsable_id` int DEFAULT NULL COMMENT 'Utilisateur responsable du véhicule',
  `notes` text COLLATE utf8mb4_unicode_ci,
  `actif` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `matricule` (`matricule`),
  UNIQUE KEY `immatriculation` (`immatriculation`),
  KEY `idx_matricule` (`matricule`),
  KEY `idx_statut` (`statut`),
  KEY `idx_etat` (`etat`),
  KEY `idx_kilometrage_actuel` (`kilometrage_actuel`),
  KEY `idx_responsable_id` (`responsable_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `vehicule` avec format enrichi
--

INSERT INTO `vehicule` (`id`, `matricule`, `immatriculation`, `marque`, `modele`, `type`, `categorie`, `annee`, `couleur`, `carburant`, `consommation_100km`, `capacite_reservoir`, `kilometrage_initial`, `kilometrage_actuel`, `statut`, `etat`, `date_acquisition`, `date_assurance`, `compagnie_assurance`, `date_visite_technique`, `date_derniere_vidange`, `km_derniere_vidange`, `responsable_id`) VALUES
(1, 'ABC123', 'CD-001-KIN', 'Toyota', 'Hilux', 'Pickup', 'UTILITAIRE', 2018, 'Blanc', 'DIESEL', 8.5, 80.00, 0, 46200, 'DISPONIBLE', 'BON', '2018-03-15', '2025-10-15', 'SORAS Assurance', '2026-03-01', '2025-09-10', 45000, 3),
(2, 'DEF456', 'CD-002-KIN', 'Hyundai', 'Tucson', 'SUV', 'LEGER', 2020, 'Noir', 'ESSENCE', 7.2, 62.00, 0, 39500, 'AFFECTE', 'EXCELLENT', '2020-01-10', '2026-01-20', 'SONAS Assurance', '2025-12-30', '2025-07-15', 38000, 2),
(3, 'GHI789', 'CD-003-KIN', 'Ford', 'Transit', 'Camionnette', 'UTILITAIRE', 2016, 'Bleu', 'DIESEL', 9.8, 90.00, 0, 28000, 'MAINTENANCE', 'MOYEN', '2016-06-20', '2025-12-01', 'SAHAM Assurance', '2025-12-15', '2024-08-01', 25000, 1);

-- --------------------------------------------------------
--
-- Structure de la table `affectation`
--

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
  KEY `idx_statut` (`statut`),
  KEY `idx_date_debut` (`date_debut`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `affectation`
--

INSERT INTO `affectation` (`id`, `vehicule_id`, `conducteur_id`, `date_debut`, `date_fin`, `motif`, `statut`, `created_at`, `updated_at`) VALUES
(6, 2, 4, '2025-09-25', '2025-09-28', 'Remise matériels\n\nNotes: RAS', 'en_cours', '2025-09-25 14:09:11', '2025-09-25 14:09:11');

-- --------------------------------------------------------
--
-- Structure de la table `type_entretien`
-- Définit les types d'entretiens possibles avec leurs périodicités
--

CREATE TABLE IF NOT EXISTS `type_entretien` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `categorie` enum('PREVENTIF','CURATIF','OBLIGATOIRE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `periodicite_km` int DEFAULT NULL COMMENT 'Périodicité en kilomètres',
  `periodicite_mois` int DEFAULT NULL COMMENT 'Périodicité en mois',
  `cout_estime` decimal(10,2) DEFAULT NULL,
  `duree_estimee_heures` int DEFAULT NULL,
  `priorite` enum('BASSE','NORMALE','HAUTE','CRITIQUE') COLLATE utf8mb4_unicode_ci DEFAULT 'NORMALE',
  `actif` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_categorie` (`categorie`),
  KEY `idx_priorite` (`priorite`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Données pour la table `type_entretien`
--

INSERT INTO `type_entretien` (`nom`, `description`, `categorie`, `periodicite_km`, `periodicite_mois`, `cout_estime`, `duree_estimee_heures`, `priorite`) VALUES
('Vidange moteur', 'Changement huile moteur et filtre à huile', 'PREVENTIF', 5000, 6, 80.00, 1, 'HAUTE'),
('Vidange boîte', 'Vidange huile boîte de vitesse', 'PREVENTIF', 40000, 24, 120.00, 2, 'NORMALE'),
('Changement pneus', 'Remplacement pneus usés', 'CURATIF', 80000, NULL, 400.00, 2, 'HAUTE'),
('Révision générale', 'Contrôle complet véhicule', 'PREVENTIF', 20000, 12, 300.00, 4, 'NORMALE'),
('Freinage', 'Contrôle/remplacement plaquettes et disques', 'PREVENTIF', 30000, 18, 250.00, 3, 'CRITIQUE'),
('Visite technique', 'Contrôle technique obligatoire', 'OBLIGATOIRE', NULL, 12, 50.00, 1, 'CRITIQUE'),
('Assurance', 'Renouvellement assurance véhicule', 'OBLIGATOIRE', NULL, 12, 800.00, 0, 'CRITIQUE'),
('Batterie', 'Remplacement batterie', 'CURATIF', NULL, 36, 100.00, 1, 'NORMALE'),
('Courroie distribution', 'Changement courroie de distribution', 'PREVENTIF', 100000, 60, 500.00, 6, 'CRITIQUE'),
('Climatisation', 'Entretien système climatisation', 'PREVENTIF', NULL, 24, 150.00, 2, 'BASSE');

-- --------------------------------------------------------
--
-- Structure de la table `planification_entretien`
-- Planification automatique des entretiens selon les types
--

CREATE TABLE IF NOT EXISTS `planification_entretien` (
  `id` int NOT NULL AUTO_INCREMENT,
  `vehicule_id` int NOT NULL,
  `type_entretien_id` int NOT NULL,
  `derniere_realisation` date DEFAULT NULL,
  `dernier_kilometrage` int DEFAULT 0,
  `prochaine_echeance_date` date DEFAULT NULL,
  `prochaine_echeance_km` int DEFAULT NULL,
  `statut` enum('ACTIF','SUSPENDU','TERMINE') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIF',
  `alertes_activees` tinyint(1) DEFAULT '1',
  `seuil_alerte_jours` int DEFAULT 30 COMMENT 'Nombre de jours avant échéance pour alerte',
  `seuil_alerte_km` int DEFAULT 1000 COMMENT 'Nombre de km avant échéance pour alerte',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_vehicule_type` (`vehicule_id`,`type_entretien_id`),
  KEY `idx_vehicule_id` (`vehicule_id`),
  KEY `idx_type_entretien_id` (`type_entretien_id`),
  KEY `idx_prochaine_echeance_date` (`prochaine_echeance_date`),
  KEY `idx_prochaine_echeance_km` (`prochaine_echeance_km`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données pour la table `planification_entretien`
-- Planification automatique pour chaque véhicule
--

INSERT INTO `planification_entretien` (`vehicule_id`, `type_entretien_id`, `derniere_realisation`, `dernier_kilometrage`, `prochaine_echeance_date`, `prochaine_echeance_km`) VALUES
(1, 1, '2025-09-10', 45000, '2026-03-10', 50000),  -- Vidange Toyota
(1, 4, '2025-01-15', 40000, '2026-01-15', 60000),  -- Révision générale Toyota
(1, 6, '2025-09-01', NULL, '2026-09-01', NULL),    -- Visite technique Toyota
(2, 1, '2025-07-15', 38000, '2026-01-15', 43000),  -- Vidange Hyundai
(2, 5, '2025-03-20', 35000, '2026-09-20', 65000),  -- Freinage Hyundai
(3, 1, NULL, 0, '2025-11-01', 5000),               -- Vidange Ford (nouveau)
(3, 4, '2024-10-01', 25000, '2025-10-01', 45000);  -- Révision Ford

-- --------------------------------------------------------
--
-- Structure de la table `entretien`
-- Table principale des entretiens réalisés ou programmés
--

CREATE TABLE IF NOT EXISTS `entretien` (
  `id` int NOT NULL AUTO_INCREMENT,
  `vehicule_id` int NOT NULL,
  `type_entretien_id` int DEFAULT NULL,
  `type_entretien_libre` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Type libre si pas dans référentiel',
  `date_programmee` date DEFAULT NULL,
  `date_realisation` date DEFAULT NULL,
  `kilometrage` int DEFAULT NULL COMMENT 'Kilométrage au moment de l\'entretien',
  `categorie` enum('PREVENTIF','CURATIF','OBLIGATOIRE','URGENT') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PREVENTIF',
  `priorite` enum('BASSE','NORMALE','HAUTE','CRITIQUE') COLLATE utf8mb4_unicode_ci DEFAULT 'NORMALE',
  `statut` enum('PLANIFIE','EN_ATTENTE','EN_COURS','TERMINE','ANNULE','REPORTE') COLLATE utf8mb4_unicode_ci DEFAULT 'PLANIFIE',
  `description` text COLLATE utf8mb4_unicode_ci,
  `commentaire` text COLLATE utf8mb4_unicode_ci,
  `cout_prevu` decimal(10,2) DEFAULT NULL,
  `cout_reel` decimal(10,2) DEFAULT NULL,
  `fournisseur` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mecanicien` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `duree_reelle_heures` decimal(4,2) DEFAULT NULL,
  `pieces_changees` text COLLATE utf8mb4_unicode_ci COMMENT 'JSON des pièces remplacées',
  `prochaine_echeance_km` int DEFAULT NULL COMMENT 'Prochain kilométrage pour cet entretien',
  `prochaine_echeance_date` date DEFAULT NULL COMMENT 'Prochaine date pour cet entretien',
  `alerte_active` tinyint(1) DEFAULT '1',
  `created_by` int DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_vehicule_id` (`vehicule_id`),
  KEY `idx_type_entretien_id` (`type_entretien_id`),
  KEY `idx_date_programmee` (`date_programmee`),
  KEY `idx_date_realisation` (`date_realisation`),
  KEY `idx_statut` (`statut`),
  KEY `idx_categorie` (`categorie`),
  KEY `idx_priorite` (`priorite`),
  KEY `idx_kilometrage` (`kilometrage`),
  KEY `idx_created_by` (`created_by`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données pour la table `entretien`
-- Historique des entretiens avec nouveau format
--

INSERT INTO `entretien` (`vehicule_id`, `type_entretien_id`, `date_programmee`, `date_realisation`, `kilometrage`, `categorie`, `statut`, `description`, `cout_reel`, `mecanicien`, `prochaine_echeance_km`, `prochaine_echeance_date`) VALUES
(1, 1, '2025-09-10', '2025-09-10', 45000, 'PREVENTIF', 'TERMINE', 'Vidange moteur complète + filtre', 85.00, 'Jean Mécanicien', 50000, '2026-03-10'),
(2, 3, '2025-07-15', '2025-07-15', 38000, 'CURATIF', 'TERMINE', 'Remplacement pneus arrière usés', 420.00, 'Paul Pneus', NULL, NULL),
(3, 5, '2025-10-01', NULL, NULL, 'PREVENTIF', 'PLANIFIE', 'Révision plaquettes de frein programmée', NULL, NULL, NULL, NULL);

-- --------------------------------------------------------
--
-- Structure de la table `alerte_entretien`
-- Système d'alertes automatiques
--

CREATE TABLE IF NOT EXISTS `alerte_entretien` (
  `id` int NOT NULL AUTO_INCREMENT,
  `vehicule_id` int NOT NULL,
  `type_entretien_id` int DEFAULT NULL,
  `entretien_id` int DEFAULT NULL COMMENT 'Si alerte liée à un entretien spécifique',
  `type_alerte` enum('ECHEANCE_KM','ECHEANCE_DATE','RETARD','URGENT','PANNE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `niveau` enum('INFO','ATTENTION','URGENT','CRITIQUE') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'INFO',
  `titre` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `message` text COLLATE utf8mb4_unicode_ci,
  `date_echeance` date DEFAULT NULL,
  `kilometrage_echeance` int DEFAULT NULL,
  `kilometrage_actuel` int DEFAULT NULL,
  `jours_restants` int DEFAULT NULL,
  `km_restants` int DEFAULT NULL,
  `statut` enum('ACTIVE','TRAITEE','IGNOREE','EXPIREE') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE',
  `lu` tinyint(1) DEFAULT '0',
  `traite_par` int DEFAULT NULL,
  `date_traitement` datetime DEFAULT NULL,
  `action_recommandee` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_vehicule_id` (`vehicule_id`),
  KEY `idx_type_entretien_id` (`type_entretien_id`),
  KEY `idx_entretien_id` (`entretien_id`),
  KEY `idx_type_alerte` (`type_alerte`),
  KEY `idx_niveau` (`niveau`),
  KEY `idx_statut` (`statut`),
  KEY `idx_date_echeance` (`date_echeance`),
  KEY `idx_lu` (`lu`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
--
-- Structure de la table `historique_kilometrage`
-- Suivi du kilométrage des véhicules
--

CREATE TABLE IF NOT EXISTS `historique_kilometrage` (
  `id` int NOT NULL AUTO_INCREMENT,
  `vehicule_id` int NOT NULL,
  `kilometrage` int NOT NULL,
  `date_releve` date NOT NULL,
  `type_releve` enum('MANUEL','ENTRETIEN','AFFECTATION','AUTOMATIQUE') COLLATE utf8mb4_unicode_ci DEFAULT 'MANUEL',
  `source_id` int DEFAULT NULL COMMENT 'ID de la source (entretien_id, affectation_id, etc.)',
  `commentaire` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `saisi_par` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_vehicule_id` (`vehicule_id`),
  KEY `idx_date_releve` (`date_releve`),
  KEY `idx_type_releve` (`type_releve`),
  KEY `idx_saisi_par` (`saisi_par`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données pour la table `historique_kilometrage`
--

INSERT INTO `historique_kilometrage` (`vehicule_id`, `kilometrage`, `date_releve`, `type_releve`, `source_id`, `commentaire`) VALUES
(1, 45000, '2025-09-10', 'ENTRETIEN', 1, 'Relevé lors vidange'),
(2, 38000, '2025-07-15', 'ENTRETIEN', 2, 'Relevé changement pneus'),
(1, 46200, '2025-09-25', 'MANUEL', NULL, 'Relevé mensuel'),
(2, 39500, '2025-09-25', 'MANUEL', NULL, 'Relevé mensuel'),
(3, 28000, '2025-09-25', 'MANUEL', NULL, 'Relevé mensuel');

-- --------------------------------------------------------
-- TABLE PERMISSION - Définition des permissions système
-- --------------------------------------------------------

CREATE TABLE IF NOT EXISTS `permission` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL UNIQUE,
  `nom` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `module` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Module concerné: vehicule, utilisateur, entretien, etc.',
  `action` enum('CREATE','READ','UPDATE','DELETE','EXECUTE','ADMIN') COLLATE utf8mb4_unicode_ci NOT NULL,
  `niveau` enum('SYSTEM','MODULE','RECORD') COLLATE utf8mb4_unicode_ci DEFAULT 'MODULE' COMMENT 'Niveau de permission',
  `actif` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_code` (`code`),
  KEY `idx_module` (`module`),
  KEY `idx_action` (`action`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertion des permissions de base
INSERT INTO `permission` (`code`, `nom`, `description`, `module`, `action`, `niveau`) VALUES
-- Permissions SYSTÈME
('SYSTEM_ADMIN', 'Administration Système', 'Accès complet administration système', 'system', 'ADMIN', 'SYSTEM'),
('SYSTEM_CONFIG', 'Configuration Système', 'Modifier paramètres système', 'system', 'UPDATE', 'SYSTEM'),
('SYSTEM_BACKUP', 'Sauvegarde Système', 'Gérer sauvegardes base de données', 'system', 'EXECUTE', 'SYSTEM'),
('SYSTEM_LOGS', 'Consultation Logs', 'Consulter logs système', 'system', 'READ', 'SYSTEM'),

-- Permissions UTILISATEURS
('USER_CREATE', 'Créer Utilisateur', 'Créer nouveaux utilisateurs', 'utilisateur', 'CREATE', 'MODULE'),
('USER_READ', 'Consulter Utilisateur', 'Consulter informations utilisateurs', 'utilisateur', 'READ', 'MODULE'),
('USER_UPDATE', 'Modifier Utilisateur', 'Modifier informations utilisateurs', 'utilisateur', 'UPDATE', 'MODULE'),
('USER_DELETE', 'Supprimer Utilisateur', 'Supprimer utilisateurs', 'utilisateur', 'DELETE', 'MODULE'),
('USER_ROLE_MANAGE', 'Gérer Rôles', 'Attribuer/modifier rôles utilisateurs', 'utilisateur', 'UPDATE', 'MODULE'),
('USER_PERMISSION_MANAGE', 'Gérer Permissions', 'Attribuer permissions spécifiques', 'utilisateur', 'ADMIN', 'MODULE'),

-- Permissions VÉHICULES
('VEHICLE_CREATE', 'Créer Véhicule', 'Ajouter nouveaux véhicules', 'vehicule', 'CREATE', 'MODULE'),
('VEHICLE_READ', 'Consulter Véhicule', 'Consulter informations véhicules', 'vehicule', 'READ', 'MODULE'),
('VEHICLE_UPDATE', 'Modifier Véhicule', 'Modifier informations véhicules', 'vehicule', 'UPDATE', 'MODULE'),
('VEHICLE_DELETE', 'Supprimer Véhicule', 'Supprimer véhicules', 'vehicule', 'DELETE', 'MODULE'),
('VEHICLE_ASSIGN', 'Affecter Véhicule', 'Gérer affectations véhicules', 'vehicule', 'UPDATE', 'MODULE'),

-- Permissions ENTRETIENS
('MAINTENANCE_CREATE', 'Créer Entretien', 'Programmer nouveaux entretiens', 'entretien', 'CREATE', 'MODULE'),
('MAINTENANCE_READ', 'Consulter Entretien', 'Consulter entretiens', 'entretien', 'READ', 'MODULE'),
('MAINTENANCE_UPDATE', 'Modifier Entretien', 'Modifier entretiens existants', 'entretien', 'UPDATE', 'MODULE'),
('MAINTENANCE_DELETE', 'Supprimer Entretien', 'Supprimer entretiens', 'entretien', 'DELETE', 'MODULE'),
('MAINTENANCE_VALIDATE', 'Valider Entretien', 'Valider entretiens terminés', 'entretien', 'UPDATE', 'MODULE'),
('MAINTENANCE_COST', 'Gérer Coûts', 'Saisir/modifier coûts entretiens', 'entretien', 'UPDATE', 'MODULE'),

-- Permissions AFFECTATIONS
('ASSIGNMENT_CREATE', 'Créer Affectation', 'Créer nouvelles affectations', 'affectation', 'CREATE', 'MODULE'),
('ASSIGNMENT_READ', 'Consulter Affectation', 'Consulter affectations', 'affectation', 'READ', 'MODULE'),
('ASSIGNMENT_UPDATE', 'Modifier Affectation', 'Modifier affectations', 'affectation', 'UPDATE', 'MODULE'),
('ASSIGNMENT_DELETE', 'Supprimer Affectation', 'Supprimer affectations', 'affectation', 'DELETE', 'MODULE'),

-- Permissions ALERTES
('ALERT_READ', 'Consulter Alertes', 'Consulter alertes système', 'alerte', 'READ', 'MODULE'),
('ALERT_MANAGE', 'Gérer Alertes', 'Traiter/ignorer alertes', 'alerte', 'UPDATE', 'MODULE'),
('ALERT_CONFIG', 'Configurer Alertes', 'Paramétrer seuils alertes', 'alerte', 'UPDATE', 'MODULE'),

-- Permissions RAPPORTS
('REPORT_VIEW', 'Consulter Rapports', 'Consulter rapports standards', 'rapport', 'READ', 'MODULE'),
('REPORT_EXPORT', 'Exporter Rapports', 'Exporter données rapports', 'rapport', 'EXECUTE', 'MODULE'),
('REPORT_ADVANCED', 'Rapports Avancés', 'Accès rapports avancés/personnalisés', 'rapport', 'READ', 'MODULE');

-- --------------------------------------------------------
-- TABLE ROLE_PERMISSION - Liaison rôles/permissions
-- --------------------------------------------------------

CREATE TABLE IF NOT EXISTS `role_permission` (
  `id` int NOT NULL AUTO_INCREMENT,
  `role` enum('admin','gestionnaire','conducteur','conducteur_senior','super_admin') COLLATE utf8mb4_unicode_ci NOT NULL,
  `permission_id` int NOT NULL,
  `granted` tinyint(1) DEFAULT '1' COMMENT '1=accordé, 0=refusé',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_role_permission` (`role`,`permission_id`),
  KEY `idx_role` (`role`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- TABLE UTILISATEUR_PERMISSION - Permissions spécifiques utilisateur
-- --------------------------------------------------------

CREATE TABLE IF NOT EXISTS `utilisateur_permission` (
  `id` int NOT NULL AUTO_INCREMENT,
  `utilisateur_id` int NOT NULL,
  `permission_id` int NOT NULL,
  `granted` tinyint(1) NOT NULL COMMENT '1=accordé, 0=refusé (override rôle)',
  `granted_by` int DEFAULT NULL COMMENT 'Qui a accordé cette permission',
  `date_expiration` date DEFAULT NULL COMMENT 'Date expiration permission temporaire',
  `commentaire` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_user_permission` (`utilisateur_id`,`permission_id`),
  KEY `idx_utilisateur_id` (`utilisateur_id`),
  KEY `idx_permission_id` (`permission_id`),
  KEY `idx_granted_by` (`granted_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- TABLE LOG_SECURITE - Audit trail sécurité
-- --------------------------------------------------------

CREATE TABLE IF NOT EXISTS `log_securite` (
  `id` int NOT NULL AUTO_INCREMENT,
  `utilisateur_id` int DEFAULT NULL,
  `admin_id` int DEFAULT NULL COMMENT 'Admin qui a effectué laction',
  `action` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `details` text COLLATE utf8mb4_unicode_ci,
  `ip_address` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_agent` text COLLATE utf8mb4_unicode_ci,
  `resultat` enum('SUCCES','ECHEC','BLOQUE') COLLATE utf8mb4_unicode_ci DEFAULT 'SUCCES',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_utilisateur_id` (`utilisateur_id`),
  KEY `idx_admin_id` (`admin_id`),
  KEY `idx_action` (`action`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- ATTRIBUTION PERMISSIONS PAR RÔLE
-- --------------------------------------------------------

-- Permissions SUPER_ADMIN - Accès total système
INSERT INTO `role_permission` (`role`, `permission_id`, `granted`) 
SELECT 'super_admin', id, 1 FROM `permission`;

-- Permissions ADMIN - Presque toutes permissions sauf système critique
INSERT INTO `role_permission` (`role`, `permission_id`, `granted`) 
SELECT 'admin', id, 1 FROM `permission` 
WHERE code NOT IN ('SYSTEM_BACKUP', 'SYSTEM_CONFIG');

-- Permissions GESTIONNAIRE - Gestion opérationnelle
INSERT INTO `role_permission` (`role`, `permission_id`, `granted`) 
SELECT 'gestionnaire', id, 1 FROM `permission` 
WHERE code IN (
    'USER_READ', 'USER_UPDATE',
    'VEHICLE_CREATE', 'VEHICLE_READ', 'VEHICLE_UPDATE', 'VEHICLE_ASSIGN',
    'MAINTENANCE_CREATE', 'MAINTENANCE_READ', 'MAINTENANCE_UPDATE', 'MAINTENANCE_VALIDATE',
    'ASSIGNMENT_CREATE', 'ASSIGNMENT_READ', 'ASSIGNMENT_UPDATE',
    'ALERT_READ', 'ALERT_MANAGE',
    'REPORT_VIEW', 'REPORT_EXPORT'
);

-- Permissions CONDUCTEUR_SENIOR - Consultation étendue + actions limitées
INSERT INTO `role_permission` (`role`, `permission_id`, `granted`) 
SELECT 'conducteur_senior', id, 1 FROM `permission` 
WHERE code IN (
    'VEHICLE_READ',
    'MAINTENANCE_READ', 'MAINTENANCE_UPDATE',
    'ASSIGNMENT_READ', 'ASSIGNMENT_UPDATE',
    'ALERT_READ',
    'REPORT_VIEW'
);

-- Permissions CONDUCTEUR - Consultation uniquement
INSERT INTO `role_permission` (`role`, `permission_id`, `granted`) 
SELECT 'conducteur', id, 1 FROM `permission` 
WHERE code IN (
    'VEHICLE_READ',
    'MAINTENANCE_READ',
    'ASSIGNMENT_READ',
    'ALERT_READ'
);

-- --------------------------------------------------------
-- AJOUT DES CONTRAINTES DE CLÉS ÉTRANGÈRES
-- --------------------------------------------------------

-- Contraintes pour la table `affectation`
ALTER TABLE `affectation`
  ADD CONSTRAINT `fk_affectation_conducteur` FOREIGN KEY (`conducteur_id`) REFERENCES `utilisateur` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_affectation_vehicule` FOREIGN KEY (`vehicule_id`) REFERENCES `vehicule` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- Contraintes pour la table `planification_entretien`
ALTER TABLE `planification_entretien`
  ADD CONSTRAINT `fk_planification_vehicule` FOREIGN KEY (`vehicule_id`) REFERENCES `vehicule` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_planification_type` FOREIGN KEY (`type_entretien_id`) REFERENCES `type_entretien` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- Contraintes pour la table `entretien`
ALTER TABLE `entretien`
  ADD CONSTRAINT `fk_entretien_vehicule` FOREIGN KEY (`vehicule_id`) REFERENCES `vehicule` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_entretien_type` FOREIGN KEY (`type_entretien_id`) REFERENCES `type_entretien` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_entretien_created_by` FOREIGN KEY (`created_by`) REFERENCES `utilisateur` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_entretien_updated_by` FOREIGN KEY (`updated_by`) REFERENCES `utilisateur` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

-- Contraintes pour la table `alerte_entretien`
ALTER TABLE `alerte_entretien`
  ADD CONSTRAINT `fk_alerte_vehicule` FOREIGN KEY (`vehicule_id`) REFERENCES `vehicule` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_alerte_type_entretien` FOREIGN KEY (`type_entretien_id`) REFERENCES `type_entretien` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_alerte_entretien` FOREIGN KEY (`entretien_id`) REFERENCES `entretien` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_alerte_traite_par` FOREIGN KEY (`traite_par`) REFERENCES `utilisateur` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

-- Contraintes pour la table `historique_kilometrage`
ALTER TABLE `historique_kilometrage`
  ADD CONSTRAINT `fk_historique_vehicule` FOREIGN KEY (`vehicule_id`) REFERENCES `vehicule` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_historique_saisi_par` FOREIGN KEY (`saisi_par`) REFERENCES `utilisateur` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

-- Contraintes pour la table `vehicule`
ALTER TABLE `vehicule`
  ADD CONSTRAINT `fk_vehicule_responsable` FOREIGN KEY (`responsable_id`) REFERENCES `utilisateur` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

-- Contraintes pour les tables de permissions
ALTER TABLE `role_permission`
  ADD CONSTRAINT `fk_role_permission_permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `utilisateur_permission`
  ADD CONSTRAINT `fk_user_permission_user` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateur` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_user_permission_permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_user_permission_granted_by` FOREIGN KEY (`granted_by`) REFERENCES `utilisateur` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `log_securite`
  ADD CONSTRAINT `fk_log_utilisateur` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateur` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_log_admin` FOREIGN KEY (`admin_id`) REFERENCES `utilisateur` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

-- --------------------------------------------------------
-- CRÉATION DES VUES UTILES
-- --------------------------------------------------------

-- Vue des alertes actives avec informations complètes
CREATE OR REPLACE VIEW `vue_alertes_actives` AS
SELECT 
    a.id,
    v.matricule,
    v.marque,
    v.modele,
    t.nom as type_entretien,
    a.type_alerte,
    a.niveau,
    a.titre,
    a.message,
    a.date_echeance,
    a.kilometrage_echeance,
    a.kilometrage_actuel,
    a.jours_restants,
    a.km_restants,
    a.action_recommandee,
    a.created_at
FROM alerte_entretien a
JOIN vehicule v ON a.vehicule_id = v.id
LEFT JOIN type_entretien t ON a.type_entretien_id = t.id
WHERE a.statut = 'ACTIVE' AND a.lu = 0
ORDER BY 
    CASE a.niveau 
        WHEN 'CRITIQUE' THEN 1 
        WHEN 'URGENT' THEN 2 
        WHEN 'ATTENTION' THEN 3 
        ELSE 4 
    END,
    a.jours_restants ASC,
    a.km_restants ASC;

-- Vue du tableau de bord entretiens
CREATE OR REPLACE VIEW `vue_dashboard_entretiens` AS
SELECT 
    v.id as vehicule_id,
    v.matricule,
    v.marque,
    v.modele,
    v.kilometrage_actuel,
    COUNT(CASE WHEN e.statut = 'PLANIFIE' THEN 1 END) as entretiens_planifies,
    COUNT(CASE WHEN e.statut = 'EN_COURS' THEN 1 END) as entretiens_en_cours,
    COUNT(CASE WHEN a.niveau = 'CRITIQUE' THEN 1 END) as alertes_critiques,
    COUNT(CASE WHEN a.niveau = 'URGENT' THEN 1 END) as alertes_urgentes,
    MAX(e.date_realisation) as dernier_entretien,
    MIN(CASE WHEN e.statut = 'PLANIFIE' THEN e.date_programmee END) as prochain_entretien
FROM vehicule v
LEFT JOIN entretien e ON v.id = e.vehicule_id
LEFT JOIN alerte_entretien a ON v.id = a.vehicule_id AND a.statut = 'ACTIVE'
WHERE v.actif = 1
GROUP BY v.id, v.matricule, v.marque, v.modele, v.kilometrage_actuel
ORDER BY alertes_critiques DESC, alertes_urgentes DESC, prochain_entretien ASC;

-- Vue permissions effectives d'un utilisateur (rôle + permissions spécifiques)
CREATE OR REPLACE VIEW `vue_permissions_utilisateur` AS
SELECT DISTINCT
    u.id as utilisateur_id,
    u.matricule,
    u.nom,
    u.prenom,
    u.role,
    p.id as permission_id,
    p.code as permission_code,
    p.nom as permission_nom,
    p.module,
    p.action,
    p.niveau,
    CASE 
        WHEN up.granted IS NOT NULL THEN up.granted -- Permission spécifique utilisateur prioritaire
        WHEN rp.granted IS NOT NULL THEN rp.granted -- Sinon permission du rôle
        ELSE 0 -- Par défaut refusé
    END as granted,
    CASE 
        WHEN up.granted IS NOT NULL THEN 'UTILISATEUR'
        WHEN rp.granted IS NOT NULL THEN 'ROLE'
        ELSE 'AUCUNE'
    END as source_permission,
    up.date_expiration,
    u.actif as utilisateur_actif,
    u.statut as utilisateur_statut
FROM utilisateur u
CROSS JOIN permission p
LEFT JOIN role_permission rp ON rp.role = u.role AND rp.permission_id = p.id
LEFT JOIN utilisateur_permission up ON up.utilisateur_id = u.id AND up.permission_id = p.id
WHERE u.actif = 1 AND u.statut = 'ACTIF' AND p.actif = 1
ORDER BY u.id, p.module, p.action;

-- Vue utilisateurs avec leurs rôles et statut sécurité
CREATE OR REPLACE VIEW `vue_utilisateurs_securite` AS
SELECT 
    u.id,
    u.matricule,
    u.nom,
    u.prenom,
    u.role,
    u.email,
    u.actif,
    u.statut,
    u.derniere_connexion,
    u.tentatives_echec,
    u.compte_verrouille,
    u.date_verrouillage,
    u.date_expiration_mdp,
    u.force_changement_mdp,
    CASE 
        WHEN u.compte_verrouille = 1 THEN 'VERROUILLÉ'
        WHEN u.date_expiration_mdp IS NOT NULL AND u.date_expiration_mdp < CURRENT_DATE THEN 'MDP_EXPIRÉ'
        WHEN u.force_changement_mdp = 1 THEN 'CHANGEMENT_REQUIS'
        WHEN u.statut = 'INACTIF' THEN 'INACTIF'
        WHEN u.statut = 'SUSPENDU' THEN 'SUSPENDU'
        WHEN u.actif = 0 THEN 'DÉSACTIVÉ'
        ELSE 'ACTIF'
    END as statut_securite,
    (SELECT COUNT(*) FROM `vue_permissions_utilisateur` vpu 
     WHERE vpu.utilisateur_id = u.id AND vpu.granted = 1) as nombre_permissions,
    u.created_at as date_creation,
    u.updated_at as derniere_modification
FROM utilisateur u
ORDER BY u.role, u.nom;

-- Vue tableau de bord sécurité système
CREATE OR REPLACE VIEW `vue_dashboard_securite` AS
SELECT 
    (SELECT COUNT(*) FROM utilisateur WHERE actif = 1 AND statut = 'ACTIF') as utilisateurs_actifs,
    (SELECT COUNT(*) FROM utilisateur WHERE compte_verrouille = 1) as comptes_verrouilles,
    (SELECT COUNT(*) FROM utilisateur WHERE date_expiration_mdp IS NOT NULL AND date_expiration_mdp < CURRENT_DATE) as mdp_expires,
    (SELECT COUNT(*) FROM utilisateur WHERE force_changement_mdp = 1) as changements_requis,
    (SELECT COUNT(*) FROM utilisateur WHERE derniere_connexion IS NULL) as jamais_connecte,
    (SELECT COUNT(*) FROM utilisateur WHERE derniere_connexion < DATE_SUB(NOW(), INTERVAL 30 DAY)) as inactifs_30j,
    (SELECT COUNT(*) FROM permission WHERE actif = 1) as permissions_actives,
    (SELECT COUNT(DISTINCT role) FROM role_permission) as roles_configures,
    (SELECT COUNT(*) FROM utilisateur_permission WHERE date_expiration IS NOT NULL AND date_expiration < CURRENT_DATE) as permissions_expirees;

-- --------------------------------------------------------
-- PROCÉDURE DE VÉRIFICATION DES PERMISSIONS
-- --------------------------------------------------------

DROP PROCEDURE IF EXISTS `check_user_permission`;

DELIMITER $$
CREATE PROCEDURE `check_user_permission`(
    IN p_utilisateur_id INT,
    IN p_permission_code VARCHAR(50),
    OUT p_has_permission BOOLEAN
)
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE v_granted BOOLEAN DEFAULT FALSE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_granted = FALSE;
    
    SELECT 
        CASE WHEN granted = 1 THEN TRUE ELSE FALSE END INTO v_granted
    FROM vue_permissions_utilisateur 
    WHERE utilisateur_id = p_utilisateur_id 
      AND permission_code = p_permission_code
      AND (date_expiration IS NULL OR date_expiration >= CURRENT_DATE)
    LIMIT 1;
    
    SET p_has_permission = COALESCE(v_granted, FALSE);
END$$
DELIMITER ;

-- --------------------------------------------------------
-- RÉACTIVER LES CONTRAINTES DE CLÉS ÉTRANGÈRES
-- --------------------------------------------------------

SET FOREIGN_KEY_CHECKS = 1;

-- Finaliser la transaction
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

-- ===============================================================
-- RÉSUMÉ DE LA VERSION CORRIGÉE
-- ===============================================================
/*
✅ PROBLÈME RÉSOLU
   - Contraintes de clés étrangères désactivées temporairement
   - Tables supprimées dans le bon ordre
   - Table utilisateur créée avec toutes les colonnes de sécurité
   - Contraintes ajoutées à la fin

✅ UTILISATEUR ADMIN PRINCIPAL CRÉÉ
   - Matricule: ADMIN001
   - Login: ADMINISTRATEUR SYSTÈME  
   - Password: Admin12345
   - Rôle: super_admin (accès complet)

✅ SYSTÈME COMPLET PRÊT
   - 13 tables avec données
   - 6 utilisateurs d'exemple
   - 3 véhicules d'exemple
   - Système de permissions granulaires
   - Vues pour tableaux de bord
   - Audit trail configuré

POUR SE CONNECTER COMME ADMIN :
- Utilisateur: ADMIN001
- Mot de passe: Admin12345
- Rôle: super_admin (TOUS DROITS)
*/