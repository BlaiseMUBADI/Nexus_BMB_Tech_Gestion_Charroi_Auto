# 🔐 Guide de Configuration du Système d'Authentification
## Intégration avec votre base existante bdd_charroi_auto

## 🔄 **MIGRATION DE VOS UTILISATEURS EXISTANTS**

Vos utilisateurs actuels dans la table `utilisateur` de `bdd_charroi_auto` sont automatiquement migrés vers le nouveau système sécurisé !

### 📊 **Mapping des Rôles**
| Ancien Rôle | Nouveau Rôle | Niveau | Droits |
|--------------|--------------|--------|--------|
| `admin` | **ADMIN** | 1 | TOUS LES DROITS |
| `super_admin` | **ADMIN** | 1 | TOUS LES DROITS |
| `gestionnaire` | **GESTIONNAIRE** | 2 | Gestion flotte, rapports |
| `conducteur_senior` | **MÉCANICIEN** | 3 | Entretien, réparations |
| `conducteur` | **CHAUFFEUR** | 4 | Consultation véhicules |

---

## 👥 **VOS UTILISATEURS MIGRÉS**

D'après votre base `bdd_charroi_auto`, ces utilisateurs seront migrés :

### 👑 **ADMINISTRATEURS** (Accès Complet)
1. **admin** (compte par défaut)
   - **Mot de passe :** `Admin123!`
   - **Droits :** TOUS LES DROITS

2. **Colonel Tshibanda** (Jean Didier) - COND02
   - **Username :** `jean.tshibanda`
   - **Rôle :** ADMIN 
   - **Ancien rôle :** admin
   - **Email :** `jean.tshibanda@charroi-auto.com`

### 👨‍💼 **GESTIONNAIRES**
1. **Capitaine Mbayo** (Gabriel) - COND04
   - **Username :** `gabriel.mbayo`
   - **Rôle :** GESTIONNAIRE
   - **Email :** `gabriel.mbayo@charroi-auto.com`

2. **BADIBANGA** (Jeampy) - GES01
   - **Username :** `jeampy.badibanga`
   - **Rôle :** GESTIONNAIRE
   - **Email :** `jeampy@gmail.com` (existant)

### 🔧 **MÉCANICIENS**
1. **KAPINGA** (Papy) - COND01
   - **Username :** `papy.kapinga`
   - **Rôle :** MÉCANICIEN (ancien: conducteur_senior)
   - **Email :** `blaise@gmail.com` (existant)

### 🚗 **CHAUFFEURS**
1. **Major Kabila** (Jacque) - COND03
   - **Username :** `jacque.kabila`
   - **Rôle :** CHAUFFEUR
   - **Email :** `jacque.kabila@charroi-auto.com`

---

## 🚀 **Étapes de Migration et Configuration**

### 1. **Migration des Utilisateurs Existants**
```bash
# Exécutez le script de migration
migrate_users.bat
```

### 2. **Initialisation des Mots de Passe Sécurisés**
```bash
# Initialisez les mots de passe avec cryptage SHA-256
init_users.bat
```

### 3. **Première Connexion**
1. Lancez l'application : `run.bat`
2. **Connectez-vous avec le compte admin :** `admin` / `Admin123!`
3. Ou utilisez les comptes migrés avec leurs mots de passe temporaires

---

## 🔑 **Mots de Passe après Migration**

### **Compte Admin Principal**
- **Username :** `admin`
- **Password :** `Admin123!`

### **Utilisateurs Migrés**
Les utilisateurs migrés reçoivent des mots de passe temporaires basés sur leur prénom :
- **Format :** `[Prénom]123!`
- **Exemples :**
  - Jean Tshibanda → `Jean123!`
  - Gabriel Mbayo → `Gabriel123!`
  - Papy Kapinga → `Papy123!`
  - Jacque Kabila → `Jacque123!`
  - Jeampy Badibanga → `Jeampy123!`

⚠️ **IMPORTANT :** Tous les utilisateurs migrés devront **obligatoirement changer leur mot de passe** au premier login !

---

## 🛡️ **Nouvelles Fonctionnalités de Sécurité**

### **Cryptage Avancé**
- **Algorithme :** SHA-256 avec salt aléatoire (32 bytes)
- **Itérations :** 10,000 pour résister aux attaques par force brute
- **Protection :** Impossible de récupérer le mot de passe original

### **Gestion des Sessions**
- Tokens sécurisés avec expiration automatique
- Déconnexion automatique après inactivité
- Suivi des connexions multiples avec IP et navigateur

### **Audit et Traçabilité**
- Table `auth_log` : toutes les connexions enregistrées
- Détection des tentatives d'intrusion
- Verrouillage automatique après 5 tentatives échouées

### **Contrôle d'Accès Granulaire**
- 32 permissions spécifiques par fonctionnalité
- Système hiérarchique de rôles
- Possibilité d'ajuster les droits individuellement

---

## 👨‍💼 **Gestion des Utilisateurs (Rôle Admin)**

En tant qu'administrateur, vous pouvez :

### **Création d'Utilisateurs**
- ✅ Créer de nouveaux comptes
- ✅ Assigner les rôles et permissions
- ✅ Définir les informations de profil
- ✅ Activer/désactiver les comptes

### **Gestion des Mots de Passe**
- ✅ Forcer le changement au premier login
- ✅ Réinitialiser les mots de passe oubliés
- ✅ Définir des politiques de complexité
- ✅ Expiration automatique des mots de passe

### **Surveillance et Audit**
- ✅ Consulter l'historique des connexions
- ✅ Voir les tentatives d'intrusion
- ✅ Gérer les comptes verrouillés
- ✅ Analyser les activités suspectes

---

## 🔒 **Permissions par Rôle**

### 👑 **ADMIN (Niveau 1)**
- **TOUTES** les permissions (32/32)
- Gestion complète du système
- Administration des utilisateurs
- Configuration avancée

### 👨‍💼 **GESTIONNAIRE (Niveau 2)**
- Gestion de la flotte automobile
- Attribution et suivi des véhicules
- Planification des entretiens
- Génération de rapports
- Supervision des équipes

### 🔧 **MÉCANICIEN (Niveau 3)**
- Consultation de tous les véhicules
- Création et modification des entretiens
- Gestion des réparations
- Suivi des coûts et pièces
- Diagnostics techniques

### 🚗 **CHAUFFEUR (Niveau 4)**
- Consultation des véhicules assignés
- Saisie des kilomètres
- Signalement de problèmes
- Consultation de l'historique d'entretien
- Rapports de conduite

---

## ⚠️ **SÉCURITÉ CRITIQUE**

### **Actions Obligatoires**
1. **Changez IMMÉDIATEMENT** le mot de passe admin `Admin123!`
2. **Forcez** tous les utilisateurs migrés à changer leur mot de passe
3. **Désactivez** les anciens comptes de test si nécessaire
4. **Sauvegardez** régulièrement la base de données

### **Mots de Passe Forts**
- Minimum 8 caractères
- Combinaison de lettres, chiffres et symboles
- Pas de mots du dictionnaire
- Unique pour chaque utilisateur

### **Surveillance**
- Vérifiez régulièrement les logs d'authentification
- Surveillez les tentatives d'intrusion
- Gérez les comptes inactifs
- Auditez les permissions périodiquement

---

## 🆘 **Dépannage**

### **Problème de Connexion**
1. Vérifiez que la migration a été exécutée
2. Confirmez que les tables de sécurité existent
3. Vérifiez les logs dans `auth_log`
4. Utilisez le compte admin par défaut

### **Mot de Passe Oublié**
1. Connectez-vous avec le compte admin
2. Réinitialisez le mot de passe via l'interface
3. Ou relancez `init_users.bat` en cas d'urgence

### **Permissions Insuffisantes**
1. Vérifiez le rôle dans la table `utilisateur`
2. Consultez la colonne `role` (doit être en majuscules)
3. Contactez un administrateur pour ajustement

---

## 📁 **Structure des Données**

### **Base de Données : `bdd_charroi_auto`**
```
Tables existantes (conservées) :
├── utilisateur (enrichie avec colonnes de sécurité)
├── vehicule
├── affectation
├── entretien
├── type_entretien
└── ...

Nouvelles tables de sécurité :
├── user_session (gestion des sessions)
└── auth_log (audit des connexions)
```

### **Nouvelles Colonnes dans `utilisateur`**
- `username` : Nom d'utilisateur unique
- `password_hash` : Mot de passe crypté SHA-256
- `first_name` / `last_name` : Noms structurés
- `is_active` / `is_locked` : États du compte
- `failed_login_attempts` : Sécurité anti-brute force
- `last_login` : Suivi des connexions
- `must_change_password` : Forcer changement
- Et plus...

---

## 📞 **Support Technique**

En cas de problème :
1. Consultez les logs dans la table `auth_log`
2. Vérifiez la structure des tables avec la migration
3. Utilisez le compte admin pour débloquer les situations
4. Relancez la migration si nécessaire

**Le système est maintenant 100% compatible avec votre base existante tout en ajoutant une sécurité de niveau professionnel !**