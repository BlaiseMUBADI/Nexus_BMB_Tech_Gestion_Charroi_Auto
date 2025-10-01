-- DIAGNOSTIC : Vérifier le compte ADMIN001
SELECT 
    matricule,
    nom,
    prenom,
    role,
    mot_de_passe_hash,
    actif,
    statut,
    compte_verrouille,
    force_changement_mdp
FROM utilisateur 
WHERE matricule = 'ADMIN001';

-- Vérifier tous les utilisateurs actifs
SELECT 
    matricule,
    nom,
    role,
    actif,
    statut
FROM utilisateur 
ORDER BY actif DESC, role;

-- Si le compte ADMIN001 n'est pas actif, l'activer :
UPDATE utilisateur 
SET 
    actif = 1,
    statut = 'ACTIF',
    compte_verrouille = 0,
    force_changement_mdp = 0
WHERE matricule = 'ADMIN001';