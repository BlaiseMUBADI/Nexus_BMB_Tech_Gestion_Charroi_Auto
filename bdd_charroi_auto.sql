-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : lun. 29 sep. 2025 à 14:05
-- Version du serveur : 8.2.0
-- Version de PHP : 8.2.13

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `bdd_charroi_auto`
--

-- --------------------------------------------------------

--
-- Structure de la table `affectation`
--

DROP TABLE IF EXISTS `affectation`;
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
-- Structure de la table `entretien`
--

DROP TABLE IF EXISTS `entretien`;
CREATE TABLE IF NOT EXISTS `entretien` (
  `id` int NOT NULL AUTO_INCREMENT,
  `vehicule_id` int NOT NULL,
  `date_entretien` date NOT NULL,
  `type_entretien` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `commentaire` text COLLATE utf8mb4_unicode_ci,
  `cout` decimal(10,2) DEFAULT NULL,
  `statut` enum('programme','en_cours','termine') COLLATE utf8mb4_unicode_ci DEFAULT 'programme',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_vehicule_id` (`vehicule_id`),
  KEY `idx_date_entretien` (`date_entretien`),
  KEY `idx_statut` (`statut`),
  KEY `idx_type_entretien` (`type_entretien`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `entretien`
--

INSERT INTO `entretien` (`id`, `vehicule_id`, `date_entretien`, `type_entretien`, `commentaire`, `cout`, `statut`, `created_at`, `updated_at`) VALUES
(1, 1, '2025-09-10', 'Vidange', 'Vidange moteur complète', NULL, 'termine', '2025-09-25 01:33:52', '2025-09-25 01:33:52'),
(2, 2, '2025-07-15', 'Pneus', 'Remplacement des pneus arrière', NULL, 'termine', '2025-09-25 01:33:52', '2025-09-25 01:33:52'),
(3, 3, '2025-10-01', 'Freins', 'Révision des plaquettes de frein', NULL, 'programme', '2025-09-25 01:33:52', '2025-09-25 01:33:52');

-- --------------------------------------------------------

--
-- Structure de la table `utilisateur`
--

DROP TABLE IF EXISTS `utilisateur`;
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `utilisateur`
--

INSERT INTO `utilisateur` (`id`, `matricule`, `nom`, `prenom`, `role`, `mot_de_passe_hash`, `actif`, `created_at`, `updated_at`, `email`, `statut`, `date_creation`) VALUES
(1, 'COND03', 'Major Kabila', 'Jacque', 'conducteur', 'hash_password_123', 1, '2025-09-25 01:33:52', '2025-09-25 11:14:56', '', 'ACTIF', '2025-09-25 01:35:31'),
(2, 'COND04', 'Capitaine Mbayo', 'Gabriel', 'gestionnaire', 'hash_password_456', 1, '2025-09-25 01:33:52', '2025-09-25 11:14:45', '', 'ACTIF', '2025-09-25 01:35:31'),
(3, 'COND02', 'Colonel Tshibanda', 'Jean Didier', 'admin', 'hash_password_789', 1, '2025-09-25 01:33:52', '2025-09-25 11:14:35', '', 'ACTIF', '2025-09-25 01:35:31'),
(4, 'COND01', 'KAPINGA', 'Papy', 'conducteur_senior', '123456', 1, '2025-09-25 02:14:42', '2025-09-25 02:14:42', 'blaise@gmail.com', 'ACTIF', '2025-09-25 01:14:43'),
(5, 'GES01', 'BADIBANGA', 'Jeampy', 'gestionnaire', '123456', 1, '2025-09-25 02:15:40', '2025-09-25 02:15:40', 'jeampy@gmail.com', 'ACTIF', '2025-09-25 01:15:41');

-- --------------------------------------------------------

--
-- Structure de la table `vehicule`
--

DROP TABLE IF EXISTS `vehicule`;
CREATE TABLE IF NOT EXISTS `vehicule` (
  `id` int NOT NULL AUTO_INCREMENT,
  `matricule` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `marque` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `annee` int DEFAULT NULL,
  `disponible` tinyint(1) DEFAULT '1',
  `date_assurance` date DEFAULT NULL,
  `date_vidange` date DEFAULT NULL,
  `date_visite_technique` date DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `matricule` (`matricule`),
  KEY `idx_matricule` (`matricule`),
  KEY `idx_disponible` (`disponible`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `vehicule`
--

INSERT INTO `vehicule` (`id`, `matricule`, `marque`, `type`, `annee`, `disponible`, `date_assurance`, `date_vidange`, `date_visite_technique`, `created_at`, `updated_at`) VALUES
(1, 'ABC123', 'Toyota', 'Pickup', 2018, 1, '2025-10-15', '2025-09-10', '2025-11-01', '2025-09-25 01:33:52', '2025-09-25 01:33:52'),
(2, 'DEF456', 'Hyundai', 'SUV', 2020, 1, '2025-08-20', '2025-07-15', '2025-09-30', '2025-09-25 01:33:52', '2025-09-25 14:08:13'),
(3, 'GHI789', 'Ford', 'Camionnette', 2016, 1, '2025-12-01', '2025-10-01', '2025-12-15', '2025-09-25 01:33:52', '2025-09-25 01:33:52');

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `affectation`
--
ALTER TABLE `affectation`
  ADD CONSTRAINT `fk_affectation_conducteur` FOREIGN KEY (`conducteur_id`) REFERENCES `utilisateur` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_affectation_vehicule` FOREIGN KEY (`vehicule_id`) REFERENCES `vehicule` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `entretien`
--
ALTER TABLE `entretien`
  ADD CONSTRAINT `fk_entretien_vehicule` FOREIGN KEY (`vehicule_id`) REFERENCES `vehicule` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
