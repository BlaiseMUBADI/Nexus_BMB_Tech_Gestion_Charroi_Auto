# 📚 DOCUMENTATION TECHNIQUE COMPLÈTE
## Nexus BMB Tech - Système de Gestion Charroi Auto

*Guide consolidé de tous les aspects techniques du système*

---

## 📑 **TABLE DES MATIÈRES**

1. [🔐 Système de Sécurité](#-système-de-sécurité)
2. [👥 Gestion Utilisateurs et Permissions](#-gestion-utilisateurs-et-permissions)
3. [🔧 Installation et Configuration](#-installation-et-configuration)
4. [🐛 Résolution de Problèmes](#-résolution-de-problèmes)
5. [📊 Modules Fonctionnels](#-modules-fonctionnels)
6. [🔄 Migration et Intégration](#-migration-et-intégration)
7. [🛠️ Maintenance et Optimisation](#️-maintenance-et-optimisation)

---

## 🔐 **SYSTÈME DE SÉCURITÉ**

### **Architecture de Sécurité**

Le système implémente une architecture de sécurité à 3 niveaux :

```
🔴 NIVEAU SYSTÈME    → Sauvegarde, Configuration globale
🟡 NIVEAU MODULE     → Gestion par fonctionnalité
🟢 NIVEAU ENREGISTREMENT → Contrôle granulaire des données
```

### **Authentification**

#### **Méthodes de Connexion**
- **Matricule** : ADMIN001, COND01, GES01, etc.
- **Email** : blaise@gmail.com, jeampy@gmail.com, etc.
- **Mot de passe** : SHA-256 pour les nouveaux, legacy pour les anciens

#### **Implémentation Code**
```java
// Authentification utilisateur
AuthenticationDAO auth = new AuthenticationDAO();
AuthResult result = auth.authenticate(username, password);

if (result.isSuccess()) {
    Utilisateur user = result.getUser();
    // Créer session
    String sessionToken = auth.createSession(user);
}
```

### **Gestion des Sessions**
- **Token sécurisé** généré à chaque connexion
- **Expiration automatique** après inactivité
- **Validation côté serveur** à chaque requête
- **Révocation immédiate** en cas de déconnexion

### **Permissions Granulaires (27 Total)**

#### **SYSTÈME (4 permissions)**
- `SYSTEM_ADMIN` - Administration complète
- `SYSTEM_CONFIG` - Configuration système
- `SYSTEM_BACKUP` - Gestion sauvegardes
- `SYSTEM_LOGS` - Consultation logs

#### **UTILISATEURS (6 permissions)**
- `USER_CREATE` - Créer utilisateurs
- `USER_READ` - Consulter utilisateurs
- `USER_UPDATE` - Modifier utilisateurs
- `USER_DELETE` - Supprimer utilisateurs
- `USER_ROLE_MANAGE` - Gérer rôles
- `USER_PERMISSION_MANAGE` - Gérer permissions

#### **VÉHICULES (5 permissions)**
- `VEHICLE_CREATE` - Créer véhicules
- `VEHICLE_READ` - Consulter véhicules
- `VEHICLE_UPDATE` - Modifier véhicules
- `VEHICLE_DELETE` - Supprimer véhicules
- `VEHICLE_ASSIGN` - Gérer affectations

#### **MAINTENANCE (4 permissions)**
- `MAINTENANCE_CREATE` - Programmer entretiens
- `MAINTENANCE_READ` - Consulter entretiens
- `MAINTENANCE_UPDATE` - Modifier entretiens
- `MAINTENANCE_DELETE` - Supprimer entretiens

### **Protection des Données**
- **Chiffrement** des mots de passe (SHA-256)
- **Audit trail** complet des actions
- **Validation** des entrées utilisateur
- **Protection** contre les injections SQL

---

## 👥 **GESTION UTILISATEURS ET PERMISSIONS**

### **Hiérarchie des Rôles**

```
🔴 SUPER_ADMIN (ID: 6 - ADMIN001)
   📧 admin@charroi.system
   🔑 Admin12345
   ✅ 27/27 permissions (100%)

🟠 ADMIN (ID: 3 - COND02)
   👤 Colonel Tshibanda Jean Didier
   🔑 hash_password_789
   ✅ 25/27 permissions (93%)
   ❌ Pas de sauvegarde/config système

🟡 GESTIONNAIRE (ID: 2,5 - COND04, GES01)
   👤 Capitaine Mbayo Gabriel / BADIBANGA Jeampy
   🔑 hash_password_456 / 123456
   ✅ 14/27 permissions (52%)
   ❌ Pas de gestion utilisateurs, pas de suppressions critiques

🟢 CONDUCTEUR_SENIOR (ID: 4 - COND01)
   👤 KAPINGA Papy
   📧 blaise@gmail.com
   🔑 123456
   ✅ 6/27 permissions (22%)
   ❌ Lecture principalement + maintenance

🔵 CONDUCTEUR (ID: 1 - COND03)
   👤 Major Kabila Jacque
   🔑 hash_password_123
   ✅ 3/27 permissions (11%)
   ❌ Lecture uniquement des informations personnelles
```

### **Gestion des Permissions**

#### **Attribution par Rôle**
```sql
-- Exemple : Permissions GESTIONNAIRE
INSERT INTO role_permission (role, permission_id, granted)
SELECT 'gestionnaire', id, 1 FROM permission 
WHERE module IN ('vehicule', 'entretien', 'affectation', 'alerte', 'rapport')
AND action IN ('CREATE', 'READ', 'UPDATE', 'EXECUTE');
```

#### **Override Utilisateur**
```sql
-- Permission spécifique à un utilisateur
INSERT INTO utilisateur_permission (utilisateur_id, permission_id, granted, date_expiration)
VALUES (4, 15, 1, '2025-12-31');  -- Permission temporaire
```

### **Vérification des Droits**
```java
// Vérifier permission utilisateur
PermissionManager pm = new PermissionManager();
boolean canCreate = pm.hasPermission(user, "VEHICLE_CREATE");
boolean canAccess = pm.hasSystemPermission(user, "SYSTEM_ADMIN");
```

---

## 🔧 **INSTALLATION ET CONFIGURATION**

### **Prérequis Système**
- **Java 17+** (OpenJDK ou Oracle JDK)
- **MySQL 8.0+** (MariaDB 10.5+ compatible)
- **NetBeans IDE 15+** (recommandé)
- **4GB RAM minimum** (8GB recommandé)

### **Installation Étape par Étape**

#### **1. Configuration Base de Données**
```sql
-- Créer la base de données
CREATE DATABASE bdd_charroi_auto 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Créer utilisateur dédié
CREATE USER 'charroi_user'@'localhost' IDENTIFIED BY 'SecurePassword123!';
GRANT ALL PRIVILEGES ON bdd_charroi_auto.* TO 'charroi_user'@'localhost';
FLUSH PRIVILEGES;

-- Importer le schéma
SOURCE bdd_charroi_auto.sql;
```

#### **2. Configuration Application**

**DatabaseConnection.java**
```java
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/bdd_charroi_auto";
    private static final String USER = "charroi_user";
    private static final String PASSWORD = "SecurePassword123!";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
}
```

#### **3. Compilation et Lancement**
```bash
# Compilation
compile.bat    # Windows
ant compile    # Multiplateforme

# Lancement
run.bat        # Windows
ant run        # Multiplateforme
```

### **Configuration Avancée**

#### **Variables d'Environnement**
```bash
# Configuration base de données
DB_HOST=localhost
DB_PORT=3306
DB_NAME=bdd_charroi_auto
DB_USER=charroi_user
DB_PASSWORD=SecurePassword123!

# Configuration application
APP_ENV=production
LOG_LEVEL=INFO
SESSION_TIMEOUT=30
MAX_LOGIN_ATTEMPTS=5
```

#### **Paramètres JVM Recommandés**
```bash
java -Xms512m -Xmx2g -XX:+UseG1GC -Dfile.encoding=UTF-8 Application
```

---

## 🐛 **RÉSOLUTION DE PROBLÈMES**

### **Erreurs de Compilation**

#### **Problème : Classes Manquantes**
```
Error: cannot find symbol User, UserSession
```
**Solution :**
- Vérifier que toutes les classes sont dans le bon package
- Utiliser `Utilisateur` au lieu de `User`
- Nettoyer et recompiler : `ant clean compile`

#### **Problème : Dépendances Manquantes**
```
Error: package com.mysql.cj.jdbc does not exist
```
**Solution :**
- Vérifier que `mysql-connector-j-9.3.0.jar` est dans `/lib`
- Ajouter au classpath si nécessaire

### **Erreurs de Base de Données**

#### **Problème : Column 'matricule' not found**
```
SQLSyntaxErrorException: Column 'matricule' not found
```
**Solution :**
- Vérifier que la base contient la colonne `matricule`
- Adapter les requêtes SQL aux colonnes existantes
- Utiliser la vraie structure de la base

#### **Problème : Connection refused**
```
CommunicationsException: Communications link failure
```
**Solution :**
- Vérifier que MySQL est démarré
- Contrôler les paramètres de connexion
- Tester la connectivité : `mysql -u charroi_user -p`

### **Erreurs d'Authentification**

#### **Problème : "Compte inactif"**
**Causes possibles :**
- Utilisateur inexistant dans la base
- Champ `actif` = 0
- Statut = 'INACTIF' ou 'SUSPENDU'

**Solution :**
```sql
-- Vérifier l'utilisateur
SELECT * FROM utilisateur WHERE matricule = 'ADMIN001';

-- Activer si nécessaire
UPDATE utilisateur SET actif = 1, statut = 'ACTIF' 
WHERE matricule = 'ADMIN001';
```

### **Problèmes de Performance**

#### **Lenteur au Démarrage**
- Vérifier les index sur les tables principales
- Optimiser les requêtes d'initialisation
- Augmenter la mémoire JVM

#### **Interface qui Freeze**
- Utiliser SwingWorker pour les opérations longues
- Implémenter des indicateurs de progression
- Optimiser les requêtes SQL

---

## 📊 **MODULES FONCTIONNELS**

### **Module Véhicules**

#### **Fonctionnalités**
- ✅ CRUD complet des véhicules
- ✅ Suivi kilométrage automatique
- ✅ Gestion des statuts (DISPONIBLE, AFFECTE, MAINTENANCE, etc.)
- ✅ Historique des modifications
- ✅ Alertes maintenance préventive

#### **Structure Données**
```sql
vehicule (
  id, matricule, immatriculation, marque, modele,
  kilometrage_actuel, statut, etat, responsable_id,
  date_acquisition, date_assurance, date_visite_technique
)
```

### **Module Utilisateurs**

#### **Fonctionnalités**
- ✅ Gestion multi-rôles (5 niveaux)
- ✅ Permissions granulaires (27 permissions)
- ✅ Authentification sécurisée
- ✅ Audit des connexions
- ✅ Sessions sécurisées

### **Module Affectations**

#### **Fonctionnalités**
- ✅ Attribution véhicule-conducteur
- ✅ Planification temporelle
- ✅ Synchronisation automatique
- ✅ Suivi temps réel des statuts

#### **Synchronisation Automatique**
```java
// Classe SynchronisateurAffectations
- Démarrage/arrêt automatique des affectations
- Mise à jour statuts véhicules
- Synchronisation toutes les 3 heures
```

### **Module Maintenance**

#### **Système Complet**
- ✅ **Types d'entretien** prédéfinis (10 types)
- ✅ **Planification automatique** basée sur km/dates
- ✅ **Alertes préventives** multiniveaux
- ✅ **Historique complet** des interventions
- ✅ **Suivi coûts** et fournisseurs

#### **Types d'Entretien**
```
PREVENTIF  : Vidange, révision, freinage, courroie
CURATIF    : Pneus, batterie, réparations
OBLIGATOIRE: Visite technique, assurance
```

#### **Alertes Automatiques**
```sql
-- Niveaux d'alerte
INFO      : Information générale
ATTENTION : Échéance proche (30 jours/1000 km)
URGENT    : Échéance très proche (7 jours/200 km)
CRITIQUE  : Dépassement d'échéance
```

---

## 🔄 **MIGRATION ET INTÉGRATION**

### **Migration depuis Ancien Système**

#### **Étapes de Migration**
1. **Sauvegarde** des données existantes
2. **Analyse** de la structure actuelle
3. **Mapping** des champs vers nouveau schéma
4. **Migration** progressive par module
5. **Validation** et tests complets

#### **Script de Migration Utilisateurs**
```sql
-- Migration utilisateurs existants
INSERT INTO utilisateur (matricule, nom, prenom, role, mot_de_passe_hash, email)
SELECT 
    ancien_matricule,
    ancien_nom,
    ancien_prenom,
    CASE ancien_role 
        WHEN 'ADMIN' THEN 'admin'
        WHEN 'GESTIONNAIRE' THEN 'gestionnaire'
        ELSE 'conducteur'
    END,
    ancien_password,
    ancien_email
FROM ancien_systeme_utilisateurs;
```

### **Intégration Systèmes Existants**

#### **API REST (Futur)**
```java
// Endpoints prévus
GET    /api/vehicules          - Liste véhicules
POST   /api/vehicules          - Créer véhicule
PUT    /api/vehicules/{id}     - Modifier véhicule
DELETE /api/vehicules/{id}     - Supprimer véhicule
GET    /api/affectations       - Liste affectations
POST   /api/affectations       - Créer affectation
```

#### **Import/Export Données**
```java
// Classes utilitaires
DataExporter.exportToCSV(vehicules, "vehicules.csv");
DataImporter.importFromExcel("nouveau_parc.xlsx");
```

---

## 🛠️ **MAINTENANCE ET OPTIMISATION**

### **Maintenance Base de Données**

#### **Tâches Quotidiennes**
```sql
-- Nettoyage logs anciens
DELETE FROM log_securite WHERE created_at < DATE_SUB(NOW(), INTERVAL 90 DAY);

-- Mise à jour statistiques
ANALYZE TABLE vehicule, utilisateur, affectation;

-- Vérification intégrité
CHECK TABLE vehicule, affectation, entretien;
```

#### **Optimisations Index**
```sql
-- Index de performance
CREATE INDEX idx_vehicule_statut_date ON vehicule(statut, updated_at);
CREATE INDEX idx_affectation_periode ON affectation(date_debut, date_fin);
CREATE INDEX idx_entretien_echeance ON entretien(date_programmee, statut);
```

### **Maintenance Application**

#### **Nettoyage Périodique**
```bash
# Nettoyage fichiers temporaires
ant clean

# Suppression logs anciens
find logs/ -name "*.log" -mtime +30 -delete

# Optimisation JVM
jcmd <pid> GC.run_finalization
```

#### **Monitoring Performance**
```java
// Métriques à surveiller
- Temps de réponse authentification (< 2s)
- Mémoire JVM utilisée (< 80%)
- Connexions base de données actives
- Taille logs d'audit
```

### **Sauvegarde et Restauration**

#### **Sauvegarde Automatique**
```bash
#!/bin/bash
# backup_daily.sh
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -u charroi_user -p bdd_charroi_auto > backup_${DATE}.sql
gzip backup_${DATE}.sql
```

#### **Restauration**
```bash
# Restauration complète
mysql -u charroi_user -p bdd_charroi_auto < backup_20251001_120000.sql

# Restauration sélective
mysql -u charroi_user -p -e "SOURCE restore_users_only.sql" bdd_charroi_auto
```

---

## 🔧 **OUTILS DE DÉVELOPPEMENT**

### **Scripts Utilitaires**

#### **compile.bat**
```batch
@echo off
echo === Compilation du projet Gestion Charroi Auto ===
echo Compilation en cours...
ant clean compile
if %errorlevel%==0 (
    echo === COMPILATION RÉUSSIE ===
) else (
    echo === ERREUR DE COMPILATION ===
)
pause
```

#### **run.bat**
```batch
@echo off
echo === Lancement Gestion Charroi Auto ===
ant run
```

### **Configuration NetBeans**

#### **project.properties (extraits)**
```properties
application.title=Nexus BMB Tech Gestion Charroi Auto
main.class=nexus_bmb_soft.application.Application
source.encoding=UTF-8
src.dir=src
build.dir=build
dist.dir=dist
```

---

## 📈 **INDICATEURS DE PERFORMANCE**

### **Métriques Système**
```
📊 Utilisateurs actifs : 6
🚗 Véhicules gérés : 3+
📋 Affectations : Synchronisation auto 3h
🔧 Types entretien : 10 prédéfinis
🔐 Permissions : 27 granulaires
📄 Documentation : 15+ guides
```

### **Temps de Réponse Cibles**
```
🔐 Authentification : < 2 secondes
📊 Chargement dashboard : < 3 secondes
🚗 Liste véhicules : < 1 seconde
📋 Création affectation : < 2 secondes
🔍 Recherche : < 1 seconde
```

---

## 🎯 **ROADMAP TECHNIQUE**

### **Version 2.1** (Prévue T1 2026)
- [ ] API REST complète
- [ ] Interface web responsive
- [ ] Module de facturation
- [ ] Rapports avancés

### **Version 2.2** (Prévue T2 2026)
- [ ] Application mobile
- [ ] Intégration GPS
- [ ] Notifications push
- [ ] Analytics avancées

### **Version 3.0** (Prévue T4 2026)
- [ ] Architecture microservices
- [ ] Cloud native
- [ ] Intelligence artificielle
- [ ] IoT véhicules

---

*Documentation technique mise à jour le 1er octobre 2025*
*Version 2.0.0 - Nexus BMB Tech Gestion Charroi Auto*

---

## 📞 **SUPPORT TECHNIQUE**

Pour toute question technique :
- 📧 **Email** : blaise@gmail.com
- 🐙 **Issues GitHub** : [Ouvrir un ticket](https://github.com/BlaiseMUBADI/Nexus_BMB_Tech_Gestion_Charroi_Auto/issues)
- 📱 **Documentation** : Consultez ce guide complet

**Bonne utilisation du système ! 🚀**