-- Script de migration rapide pour ajouter compatibilité avec l'ancien système
-- À exécuter sur votre base de données existante

USE gestion_charroi_auto;

-- Ajouter le champ 'disponible' temporaire si il n'existe pas
ALTER TABLE vehicule 
ADD COLUMN IF NOT EXISTS disponible BOOLEAN DEFAULT TRUE 
COMMENT 'Champ de compatibilité - à supprimer après migration';

-- Synchroniser le champ disponible avec le statut
UPDATE vehicule 
SET disponible = CASE 
    WHEN statut = 'DISPONIBLE' THEN TRUE 
    ELSE FALSE 
END;

-- Vérifier la synchronisation
SELECT 
    COUNT(*) as total,
    SUM(CASE WHEN disponible = TRUE THEN 1 ELSE 0 END) as disponibles,
    SUM(CASE WHEN statut = 'DISPONIBLE' THEN 1 ELSE 0 END) as statut_disponible
FROM vehicule;

SHOW TABLES;
DESCRIBE vehicule;