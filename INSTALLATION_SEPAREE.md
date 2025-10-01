# GUIDE INSTALLATION BASE DE DONNÉES CHARROI AUTO

## 📋 FICHIERS À IMPORTER

Vous disposez maintenant de **2 fichiers séparés** :

1. **`bdd_structure.sql`** - Structure complète (tables, vues, fonctions)  
2. **`bdd_data.sql`** - Données de test et système

## 🚀 PROCÉDURE D'INSTALLATION

### Étape 1 : Importer la structure
```sql
SOURCE C:/chemin/vers/bdd_structure.sql;
```

### Étape 2 : Importer les données  
```sql
SOURCE C:/chemin/vers/bdd_data.sql;
```

## ✅ VÉRIFICATION INSTALLATION

### 1. Vérifier les tables créées
```sql
SHOW TABLES;
-- Doit afficher 11 tables
```

### 2. Vérifier les données utilisateurs
```sql
SELECT matricule, nom, prenom, role FROM utilisateur;
-- Doit afficher 6 utilisateurs dont ADMIN001
```

### 3. Vérifier le système de permissions
```sql
SELECT COUNT(*) as total_permissions FROM permission;
-- Doit afficher 27 permissions

SELECT role, COUNT(*) as nb_permissions 
FROM role_permission 
GROUP BY role;
-- Doit afficher les permissions par rôle
```

### 4. Tester la connexion admin
```sql
SELECT * FROM utilisateur WHERE matricule = 'ADMIN001';
-- Vérifier que le compte super admin existe
```

## 🔐 COMPTES UTILISATEURS DISPONIBLES

| Matricule | Nom | Rôle | Mot de passe |
|-----------|-----|------|--------------|
| **ADMIN001** | **ADMINISTRATEUR Système** | **super_admin** | **Admin12345*** |
| COND03 | Major Kabila Jacque | conducteur | hash_password_123 |
| COND04 | Capitaine Mbayo Gabriel | gestionnaire | hash_password_456 |
| COND02 | Colonel Tshibanda Jean Didier | admin | hash_password_789 |
| COND01 | KAPINGA Papy | conducteur_senior | 123456 |
| GES01 | BADIBANGA Jeampy | gestionnaire | 123456 |

**\* Force changement de mot de passe à la première connexion**

## 📊 DONNÉES DE TEST INCLUSES

- ✅ **10 types d'entretien** (vidange, révision, contrôle technique, etc.)
- ✅ **3 véhicules** complets avec historique
- ✅ **Affectations et entretiens** en cours
- ✅ **27 permissions granulaires** avec attribution par rôle
- ✅ **Logs d'audit** pour traçabilité

## 🔧 FONCTIONNALITÉS AVANCÉES

### Vues disponibles
- `vue_alertes_actives` - Alertes en cours non lues
- `vue_dashboard_entretiens` - Tableau de bord maintenance
- `vue_permissions_utilisateur` - Permissions effectives
- `vue_utilisateurs_securite` - Statut sécurité utilisateurs

### Fonctions sécurité
- `verifier_permission(user_id, permission_code)` - Contrôle d'accès
- `check_user_permission()` - Procédure alternative

## ⚠️ POINTS IMPORTANTS

1. **Ordre d'import** : Toujours structure AVANT données
2. **Sécurité** : Changez le mot de passe ADMIN001 après première connexion
3. **Sauvegarde** : Faites une sauvegarde après installation réussie
4. **Permissions** : Système de droits granulaire opérationnel

## 🎯 PROCHAINES ÉTAPES

1. **Importer les 2 fichiers** dans l'ordre
2. **Se connecter avec ADMIN001/Admin12345**  
3. **Changer le mot de passe administrateur**
4. **Tester les fonctionnalités** dans votre application
5. **Configurer les utilisateurs** selon vos besoins

---

*🛡️ Système sécurisé avec audit trail complet*  
*📈 Base complète pour gestion de parc automobile*  
*⚡ Prêt pour utilisation en production*