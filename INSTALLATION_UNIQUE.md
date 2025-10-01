# 🎯 FICHIER UNIQUE - GUIDE D'INSTALLATION
## Base de Données Charroi Auto Complète

---

## 📋 **INSTALLATION SIMPLE**

### **1️⃣ IMPORTER LE FICHIER**
```sql
-- Dans MySQL Workbench ou phpMyAdmin :
SOURCE C:/chemin/vers/bdd_charroi_auto.sql;

-- OU copier-coller le contenu dans l'interface
```

### **2️⃣ C'EST TOUT !** ✅
Un seul fichier = Installation complète avec :
- ✅ Tables principales (véhicules, utilisateurs, entretiens, etc.)
- ✅ Système de sécurité complet (27 permissions)
- ✅ Utilisateur super admin ADMIN001 créé
- ✅ Vues et fonctions de gestion
- ✅ Données de test incluses

---

## 🔐 **CONNEXION ADMINISTRATEUR**

### **COMPTE CRÉÉ AUTOMATIQUEMENT** :
- **Matricule** : `ADMIN001`
- **Mot de passe** : `Admin12345`
- **Rôle** : `super_admin`
- **Droits** : TOUS (27/27 permissions)

### **Test connexion** :
```java
// Dans votre application Java
String matricule = "ADMIN001";
String password = "Admin12345";
// Attendu : connexion réussie avec tous droits
```

---

## 📊 **CE QUI EST INCLUS**

### **TABLES PRINCIPALES** :
- `utilisateur` (avec colonnes sécurité)
- `vehicule` (gestion complète parc auto)
- `entretien` (planification et suivi)
- `affectation` (attribution véhicules)
- `permission` (27 permissions granulaires)
- `role_permission` (droits par rôle)
- `log_securite` (audit trail)

### **FONCTIONNALITÉS SÉCURITÉ** :
- **Permissions granulaires** : 27 permissions par module/action
- **Rôles hiérarchiques** : super_admin > admin > gestionnaire > conducteur_senior > conducteur
- **Audit automatique** : traçabilité complète des actions
- **Sécurité avancée** : verrouillage comptes, expiration mots de passe

### **VUES UTILES** :
- `vue_permissions_utilisateur` : Permissions effectives par utilisateur
- `vue_utilisateurs_securite` : Statut sécurité complet
- `vue_alertes_actives` : Alertes entretiens en cours
- `vue_dashboard_entretiens` : Tableau de bord maintenance

### **FONCTIONS DISPONIBLES** :
- `verifier_permission(user_id, permission_code)` : Vérifier droits utilisateur
- `check_user_permission(user_id, permission_code, result)` : Procédure alternative

---

## 🎯 **RÉPARTITION DES PERMISSIONS**

### **SUPER_ADMIN** (ADMIN001) :
✅ **TOUTES** les permissions (27/27)
- Administration système complète
- Gestion utilisateurs et permissions
- Accès à tous les modules

### **ADMIN** :
✅ **25/27** permissions 
- Toutes sauf système critique (backup, config)
- Gestion utilisateurs, véhicules, entretiens
- Génération rapports

### **GESTIONNAIRE** :
✅ **12/27** permissions
- Gestion opérationnelle quotidienne
- Création/modification véhicules et entretiens
- Consultation utilisateurs

### **CONDUCTEUR_SENIOR** :
✅ **6/27** permissions
- Consultation étendue
- Modification entretiens et affectations
- Lecture rapports

### **CONDUCTEUR** :
✅ **4/27** permissions
- Consultation uniquement
- Véhicules, entretiens, affectations (lecture seule)

---

## ✅ **VALIDATION POST-INSTALLATION**

### **Vérifier l'installation** :
```sql
-- 1. Compter les tables
SELECT COUNT(*) as total_tables 
FROM information_schema.tables 
WHERE table_schema = 'bdd_charroi_auto';
-- Attendu : 15+ tables

-- 2. Vérifier l'admin
SELECT * FROM utilisateur WHERE matricule = 'ADMIN001';
-- Attendu : 1 ligne avec super_admin

-- 3. Vérifier les permissions
SELECT COUNT(*) as total_permissions FROM permission;
-- Attendu : 27 permissions

-- 4. Tester la fonction sécurité
SELECT verifier_permission(6, 'SYSTEM_ADMIN') as test_admin;
-- Attendu : 1 (TRUE)
```

---

## 🚨 **EN CAS DE PROBLÈME**

### **Erreurs courantes** :
1. **MySQL version** : Nécessite MySQL 8.0+
2. **Privilèges insuffisants** : Accorder ALL PRIVILEGES
3. **Timeout import** : Augmenter `max_allowed_packet`

### **Solutions** :
```sql
-- Si problème de privilèges :
GRANT ALL PRIVILEGES ON bdd_charroi_auto.* TO 'votre_user'@'localhost';
FLUSH PRIVILEGES;

-- Si timeout :
SET GLOBAL max_allowed_packet = 1073741824;
```

---

## 📞 **SUPPORT**

✅ **Installation réussie si** :
- 15+ tables créées
- Utilisateur ADMIN001 présent
- 27 permissions définies
- Fonction `verifier_permission` opérationnelle
- Connexion admin fonctionnelle

🎉 **Votre base de données est prête !**

---

**Fichier unique** : `bdd_charroi_auto.sql`  
**Version** : Complète avec sécurité intégrée  
**Compatibilité** : MySQL 8.0+  
**Installation** : 1 seul fichier à importer