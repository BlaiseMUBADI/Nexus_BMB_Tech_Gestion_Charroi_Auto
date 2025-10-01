-- SOLUTION RAPIDE : Créer un compte test simple
INSERT INTO utilisateur (
    matricule, 
    nom, 
    prenom, 
    role, 
    mot_de_passe_hash, 
    email, 
    actif, 
    statut,
    tentatives_echec,
    compte_verrouille,
    force_changement_mdp
) VALUES (
    'TEST01', 
    'UTILISATEUR', 
    'TEST', 
    'admin', 
    '123456',  -- Mot de passe simple pour test
    'test@charroi.local', 
    1,  -- actif
    'ACTIF',  -- statut
    0,  -- tentatives_echec
    0,  -- compte_verrouille
    0   -- force_changement_mdp
);

-- Vérifier le compte créé
SELECT * FROM utilisateur WHERE matricule = 'TEST01';