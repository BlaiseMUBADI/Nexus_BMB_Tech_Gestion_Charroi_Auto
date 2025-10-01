# 🔧 GUIDE D'INSTALLATION BASE DE DONNÉES
## Système de Gestion Charroi Auto - Nexus BMB Tech

---

## 📋 ÉTAPES D'INSTALLATION

### 1️⃣ **IMPORTER LE FICHIER PRINCIPAL**
```sql
-- Dans MySQL Workbench ou ligne de commande :
SOURCE C:/chemin/vers/bdd_charroi_auto.sql;

-- OU via interface graphique :
-- Importer le fichier bdd_charroi_auto.sql
```

### 2️⃣ **IMPORTER LES AMÉLIORATIONS SÉCURITÉ** *(Optionnel mais recommandé)*
```sql
-- Après l'import principal, exécuter :
SOURCE C:/chemin/vers/security_improvements.sql;
```

---

## ⚠️ SOLUTIONS AUX ERREURS COURANTES

### **🚨 ERREUR "ADD COLUMN IF NOT EXISTS"** :
**Votre erreur actuelle** - MySQL ne supporte pas `IF NOT EXISTS` pour les colonnes.

**✅ SOLUTION IMMÉDIATE** :
```sql
-- Utilisez le fichier patch_securite_simple.sql à la place
SOURCE C:/chemin/vers/patch_securite_simple.sql;
```

**OU Manual Fix** - Si vous voulez corriger le fichier principal :
1. Remplacer la section problématique par des `ALTER TABLE` simples
2. Une colonne par commande au lieu de toutes ensemble

### **ERREUR FUNCTION** : 
Si vous obtenez une erreur sur `CREATE FUNCTION` :

**Solution 1** - Vérifier les délimiteurs :
```sql
-- Assurer que les délimiteurs sont bien configurés
DELIMITER $$
-- ... votre fonction ...
DELIMITER ;
```

**Solution 2** - Utiliser le patch simple :
```sql
-- Le patch_securite_simple.sql contient une fonction compatible
-- qui fonctionne sur toutes les versions MySQL
```

### **ERREUR TRIGGER** :
Si les triggers posent problème :
1. Utilisez `patch_securite_simple.sql` qui n'en contient pas
2. OU importez section par section :
   - D'abord les tables
   - Puis les vues
   - Enfin les triggers et procédures

### **🎯 MÉTHODE RECOMMANDÉE** :
```sql
-- 1. Importer d'abord le fichier principal (corrigé)
SOURCE C:/chemin/vers/bdd_charroi_auto.sql;

-- 2. OU si problème, utiliser la version simple
SOURCE C:/chemin/vers/patch_securite_simple.sql;
```

---

## 🔐 CONNEXION ADMINISTRATEUR

### **COMPTE SUPER ADMIN CRÉÉ** :
- **Matricule** : `ADMIN001`
- **Mot de passe** : `Admin12345`
- **Rôle** : `super_admin`
- **Accès** : TOUS LES DROITS SYSTÈME

### **Test de connexion** :
```java
// Dans votre application Java
String matricule = "ADMIN001";
String password = "Admin12345";
// Role attendu : super_admin
```

---

## 🎯 VÉRIFICATION POST-INSTALLATION

### **1. Vérifier les tables principales** :
```sql
SHOW TABLES LIKE '%utilisateur%';
SHOW TABLES LIKE '%permission%';
```

### **2. Tester l'utilisateur admin** :
```sql
SELECT * FROM utilisateur WHERE matricule = 'ADMIN001';
```

### **3. Vérifier les permissions** :
```sql
SELECT COUNT(*) as total_permissions FROM permission;
SELECT COUNT(*) as permissions_super_admin FROM role_permission WHERE role = 'super_admin';
```

### **4. Tester les vues sécurisées** :
```sql
SELECT * FROM vue_permissions_utilisateur WHERE utilisateur_id = 6 LIMIT 5;
```

---

## 📊 STRUCTURE DES PERMISSIONS

### **27 PERMISSIONS DÉFINIES** :
- **3** Permissions SYSTÈME (backup, config, audit)
- **5** Permissions UTILISATEURS (CRUD + gestion permissions)  
- **4** Permissions VÉHICULES (CRUD)
- **5** Permissions ENTRETIENS (CRUD + exécution)
- **5** Permissions AFFECTATIONS (CRUD + exécution)
- **4** Permissions ALERTES (CRUD)
- **3** Permissions RAPPORTS (création, consultation, exécution)

### **RÉPARTITION PAR RÔLE** :
- **super_admin** : ✅ TOUTES (27/27)
- **admin** : ✅ Presque toutes (25/27) - sauf système critique
- **gestionnaire** : ✅ Gestion opérationnelle (15/27)
- **conducteur_senior** : ✅ Consultation étendue (6/27)
- **conducteur** : ✅ Consultation basique (4/27)

---

## 🚨 EN CAS DE PROBLÈME

### **Import partiel** :
1. Vérifier la version MySQL (8.0+ recommandé)
2. Augmenter `max_allowed_packet` si nécessaire
3. Importer section par section si le fichier est trop volumineux

### **Erreurs de syntaxe** :
- Utiliser MySQL Workbench plutôt que phpMyAdmin
- Vérifier le mode SQL : `SET sql_mode = '';`
- S'assurer d'utiliser MySQL et non MariaDB/PostgreSQL

### **Permissions refusées** :
```sql
-- Accorder tous les privilèges pour l'import :
GRANT ALL PRIVILEGES ON charroi_auto.* TO 'votre_user'@'localhost';
FLUSH PRIVILEGES;
```

---

## ✅ VALIDATION FINALE

Une installation réussie doit montrer :
- ✅ 15+ tables créées
- ✅ 6 utilisateurs (dont ADMIN001)
- ✅ 27 permissions définies
- ✅ 5 rôles configurés
- ✅ Vues sécurisées opérationnelles
- ✅ Connexion admin fonctionnelle

---

## 📞 CONTACT SUPPORT

En cas de problème persistant :
1. Vérifier les logs MySQL
2. Noter la version MySQL utilisée
3. Conserver le message d'erreur exact
4. Tester d'abord sur environnement de développement

---

**Date de création** : $(date)  
**Version BD** : 2.1 avec sécurité renforcée  
**Compatibilité** : MySQL 8.0+