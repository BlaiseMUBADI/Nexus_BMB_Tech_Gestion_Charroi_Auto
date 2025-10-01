# 🔐 Guide de Configuration du Système d'Authentification

## 📋 Comptes par Défaut - Coordonnées de Connexion

### 🔑 ADMINISTRATEUR (ACCÈS COMPLET)
- **Nom d'utilisateur :** `admin`
- **Mot de passe :** `Admin123!`
- **Droits :** TOUS LES DROITS (création/suppression utilisateurs, configuration système, accès complet)

### 👨‍💼 GESTIONNAIRE
- **Nom d'utilisateur :** `gestionnaire`
- **Mot de passe :** `Gestionnaire123!`
- **Droits :** Gestion flotte, affectations, rapports, supervision

### 🔧 MÉCANICIEN
- **Nom d'utilisateur :** `mecanicien`
- **Mot de passe :** `Mecanicien123!`
- **Droits :** Entretien, réparations, diagnostics, maintenance

### 🚗 CHAUFFEUR
- **Nom d'utilisateur :** `chauffeur`
- **Mot de passe :** `Chauffeur123!`
- **Droits :** Consultation véhicules assignés, rapports de conduite

---

## 🚀 Étapes de Configuration Initiale

### 1. Configuration de la Base de Données
```bash
# Exécutez le script SQL pour créer les tables d'authentification
mysql -u root -p votre_base_de_donnees < sql_auth_system.sql
```

### 2. Initialisation des Mots de Passe
```bash
# Compilez et exécutez la classe d'initialisation
cd src
javac -cp "../lib/*" nexus_bmb_soft/security/InitializeDefaultUsers.java
java -cp "../lib/*:." nexus_bmb_soft.security.InitializeDefaultUsers
```

### 3. Première Connexion
1. Lancez l'application : `run.bat`
2. Utilisez les coordonnées **admin** / **Admin123!**
3. Vous aurez accès à toutes les fonctionnalités

---

## 👥 Gestion des Utilisateurs (Rôle Admin)

### Création d'Utilisateurs
En tant qu'**admin**, vous pouvez :
- Créer de nouveaux comptes utilisateurs
- Assigner les rôles (ADMIN, GESTIONNAIRE, MECANICIEN, CHAUFFEUR)
- Définir les permissions spécifiques
- Activer/désactiver les comptes

### Modification des Mots de Passe
- Forcer le changement de mot de passe au premier login
- Réinitialiser les mots de passe oubliés
- Définir des politiques de mot de passe

### Attribution des Droits
Chaque rôle a des permissions prédéfinies :

#### 🔒 ADMIN (Niveau 1) - TOUS LES DROITS
- Gestion complète des utilisateurs
- Configuration système
- Accès à tous les modules
- Rapports complets et audit

#### 👨‍💼 GESTIONNAIRE (Niveau 2)
- Gestion de la flotte
- Attribution des véhicules
- Planification des entretiens
- Rapports de gestion

#### 🔧 MÉCANICIEN (Niveau 3)
- Suivi des entretiens
- Gestion des réparations
- Diagnostics techniques
- Inventaire des pièces

#### 🚗 CHAUFFEUR (Niveau 4)
- Consultation des véhicules assignés
- Saisie des kilomètres
- Signalement de problèmes
- Consultation des entretiens

---

## 🛡️ Sécurité Intégrée

### Cryptage des Mots de Passe
- **Algorithme :** SHA-256 avec salt aléatoire
- **Itérations :** 10,000 pour résister aux attaques par force brute
- **Salt :** 32 bytes générés aléatoirement pour chaque mot de passe

### Gestion des Sessions
- Tokens sécurisés avec expiration automatique
- Déconnexion automatique après inactivité
- Suivi des connexions multiples

### Audit et Logs
- Enregistrement de toutes les actions sensibles
- Traçabilité des connexions et modifications
- Détection des tentatives d'intrusion

---

## ⚠️ IMPORTANT - Sécurité

1. **Changez IMMÉDIATEMENT** le mot de passe admin après la première connexion
2. **Créez des comptes personnalisés** pour chaque utilisateur
3. **Désactivez les comptes de test** (gestionnaire, mecanicien, chauffeur) après configuration
4. **Configurez des mots de passe forts** pour tous les utilisateurs réels
5. **Effectuez des sauvegardes régulières** de la base de données

---

## 🆘 Dépannage

### Problème de Connexion
- Vérifiez que la base de données est accessible
- Confirmez que les tables d'authentification existent
- Vérifiez les logs d'erreur dans `auth_log`

### Mot de Passe Oublié
- Utilisez le compte admin pour réinitialiser
- Ou exécutez à nouveau `InitializeDefaultUsers.java`

### Permissions Insuffisantes
- Vérifiez le rôle de l'utilisateur
- Contactez l'administrateur pour ajustement des droits

---

## 📞 Support
En cas de problème, les logs système et la table `auth_log` contiennent toutes les informations nécessaires au diagnostic.