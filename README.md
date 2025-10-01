# 🚗 Nexus BMB Tech - Système de Gestion Charroi Auto
## Application Complète de Gestion de Parc Automobile

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Swing](https://img.shields.io/badge/UI-Java%20Swing-blue.svg)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![MySQL](https://img.shields.io/badge/Database-MySQL%208.2-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

---

## 📋 **DESCRIPTION**

**Nexus BMB Tech Gestion Charroi Auto** est une application complète de gestion de parc automobile développée en Java Swing avec une interface moderne utilisant FlatLaf. Le système permet la gestion complète des véhicules, des utilisateurs, des affectations, de la maintenance et dispose d'un système de sécurité avancé avec gestion granulaire des permissions.

### 🎯 **Fonctionnalités Principales**

- 🚗 **Gestion Véhicules** - Suivi complet du parc automobile
- 👥 **Gestion Utilisateurs** - Système multi-rôles avec permissions granulaires
- 📋 **Affectations** - Attribution et suivi des véhicules aux conducteurs
- 🔧 **Maintenance** - Planification et suivi des entretiens
- 📊 **Tableaux de Bord** - Statistiques et indicateurs en temps réel
- 🔐 **Sécurité Avancée** - Authentification et contrôle d'accès complet
- 📱 **Interface Moderne** - Design responsive avec thèmes clair/sombre

---

## 🏗️ **ARCHITECTURE TECHNIQUE**

### **Technologies Utilisées**
```
☕ Java 17+                    - Langage principal
🎨 Java Swing + FlatLaf        - Interface utilisateur moderne
🗄️ MySQL 8.2                  - Base de données
🔧 NetBeans                    - IDE de développement
📦 Maven/Ant                   - Gestion des dépendances
```

### **Librairies Principales**
- `flatlaf-3.4.1.jar` - Look and Feel moderne
- `flatlaf-extras-3.4.1.jar` - Composants étendus
- `flatlaf-fonts-roboto-2.137.jar` - Police Roboto
- `jsvg-1.4.0.jar` - Support SVG
- `miglayout-core.jar` & `miglayout-swing.jar` - Gestionnaire de layout
- `swing-toast-notifications-1.0.2.jar` - Notifications
- `mysql-connector-j-9.3.0.jar` - Connecteur MySQL

---

## 🚀 **INSTALLATION ET DÉMARRAGE**

### **Prérequis**
- Java 17 ou supérieur
- MySQL 8.0+
- NetBeans IDE (recommandé)

### **Installation**
1. **Cloner le projet**
   ```bash
   git clone https://github.com/BlaiseMUBADI/Nexus_BMB_Tech_Gestion_Charroi_Auto.git
   cd Nexus_BMB_Tech_Gestion_Charroi_Auto
   ```

2. **Configuration Base de Données**
   - Créer une base MySQL nommée `bdd_charroi_auto`
   - Importer le fichier `bdd_charroi_auto.sql`
   - Configurer la connexion dans `DatabaseConnection.java`

3. **Compilation**
   ```bash
   compile.bat    # Windows
   # ou
   ant compile    # Multiplateforme
   ```

4. **Lancement**
   ```bash
   run.bat        # Windows
   # ou
   ant run        # Multiplateforme
   ```

### **Identifiants de Test**
```
🔴 Super Admin:  ADMIN001 / Admin12345
🟡 Gestionnaire: GES01 / 123456
🟢 Conducteur:   COND01 / 123456 (blaise@gmail.com)
```

---

## 📚 **DOCUMENTATION COMPLÈTE**

### 🔐 **Sécurité et Authentification**
- **[🔑 Utilisateurs et Permissions](UTILISATEURS_ET_PERMISSIONS.md)** - Guide complet des droits d'accès
- **[🚀 Création Super Admin](CREATION_SUPER_ADMIN.md)** - Configuration administrateur système
- **[🔒 Guide Authentification](GUIDE_AUTHENTIFICATION.md)** - Implémentation du système de sécurité
- **[🛡️ Implémentation Sécurité](GUIDE_IMPLEMENTATION_SECURITE.md)** - Architecture sécurisée

### 🔧 **Guides Techniques**
- **[⚙️ Migration Projet](GUIDE_MIGRATION_PROJET.md)** - Migration vers nouveau système
- **[🔗 Intégration Existante](GUIDE_INTEGRATION_EXISTANTE.md)** - Intégration avec systèmes existants
- **[🔄 Intégration Matricule](RESUME_INTEGRATION_MATRICULE.md)** - Gestion des identifiants

### 🐛 **Résolution de Problèmes**
- **[🔨 Correction Erreurs Compilation](CORRECTION_ERREURS_COMPILATION.md)** - Solutions aux erreurs communes
- **[💾 Correction Erreur BDD](CORRECTION_ERREUR_BDD.md)** - Résolution problèmes base de données
- **[📝 Corrections ENUM InnoDB](CORRECTIONS_ENUM_INNODB.md)** - Optimisations base de données

### 🚀 **Améliorations et Fonctionnalités**
- **[📊 Système Entretien Complet](SYSTEME_ENTRETIEN_COMPLET.md)** - Module de maintenance avancé
- **[📋 Amélioration Statut Affectations](AMELIORATION_STATUT_AFFECTATIONS.md)** - Gestion des affectations
- **[🧹 Nettoyage Projet](NETTOYAGE_PROJET.md)** - Optimisation et maintenance

### 🔌 **Intégrations**
- **[🔐 Authentification LoginForm](INTEGRATION_AUTHENTIFICATION_LOGINFORM.md)** - Interface de connexion

---

## 🎭 **SYSTÈME DE RÔLES**

```
🔴 SUPER_ADMIN     → Contrôle total (27 permissions)
    ↓
🟠 ADMIN           → Administration (25 permissions)
    ↓
🟡 GESTIONNAIRE    → Gestion opérationnelle (14 permissions)
    ↓
🟢 CONDUCTEUR_SENIOR → Supervision étendue (6 permissions)
    ↓
🔵 CONDUCTEUR      → Accès basique (3 permissions)
```

---

## 📊 **MODULES PRINCIPAUX**

### 🚗 **Gestion Véhicules**
- Inventaire complet du parc
- Suivi kilométrage et état
- Historique des modifications
- Géolocalisation et responsabilité

### 👥 **Gestion Utilisateurs**
- Comptes multi-rôles
- Permissions granulaires
- Audit des connexions
- Sécurité renforcée

### 📋 **Affectations**
- Attribution véhicule-conducteur
- Planification temporelle
- Suivi temps réel
- Synchronisation automatique

### 🔧 **Maintenance**
- Planification préventive
- Suivi curatif
- Alertes automatiques
- Historique complet

### 📈 **Reporting**
- Tableaux de bord dynamiques
- Statistiques d'utilisation
- Rapports personnalisables
- Export des données

---

## 🛡️ **SÉCURITÉ**

### **Fonctionnalités de Sécurité**
- ✅ Authentification SHA-256
- ✅ Gestion de sessions sécurisées
- ✅ Contrôle d'accès basé sur les rôles (RBAC)
- ✅ Audit complet des actions
- ✅ Verrouillage automatique des comptes
- ✅ Permissions granulaires (27 permissions)
- ✅ Chiffrement des mots de passe

### **Protection des Données**
- 🔒 Chiffrement des données sensibles
- 📊 Logs détaillés des accès
- 🚫 Protection contre les injections SQL
- ⏰ Expiration automatique des sessions

---

## 📁 **STRUCTURE DU PROJET**

```
Nexus_BMB_Tech_Gestion_Charroi_Auto/
├── 📄 README.md                          # Ce fichier
├── 🗄️ bdd_charroi_auto.sql             # Base de données complète
├── 📚 Documentation/
│   ├── UTILISATEURS_ET_PERMISSIONS.md
│   ├── GUIDE_AUTHENTIFICATION.md
│   ├── SYSTEME_ENTRETIEN_COMPLET.md
│   └── [autres guides.md]
├── 🔧 src/
│   └── nexus_bmb_soft/
│       ├── application/               # Interface principale
│       ├── database/                 # Accès données
│       ├── models/                   # Modèles de données
│       ├── security/                 # Système de sécurité
│       ├── utils/                    # Utilitaires
│       └── theme/                    # Thèmes et styles
├── 📦 lib/                           # Librairies externes
├── 🔨 build/                         # Fichiers compilés
└── 📋 nbproject/                     # Configuration NetBeans
```

---

## 🚀 **UTILISATION RAPIDE**

### **Démarrage de l'Application**
```java
// Point d'entrée principal
Application.main(args);

// Affichage d'un formulaire
Application.showForm(new VehiculeForm());

// Navigation menu
Application.setSelectedMenu(0, 0);
```

### **Connexion Base de Données**
```java
// Configuration dans DatabaseConnection.java
Connection conn = DatabaseConnection.getConnection();
```

### **Authentification**
```java
// Connexion utilisateur
AuthenticationDAO auth = new AuthenticationDAO();
AuthResult result = auth.authenticate(username, password);
```

---

## 🤝 **CONTRIBUTION**

### **Comment Contribuer**
1. 🍴 Fork le projet
2. 🔧 Créer une branche feature (`git checkout -b feature/AmazingFeature`)
3. 💾 Commit les changements (`git commit -m 'Add AmazingFeature'`)
4. 📤 Push la branche (`git push origin feature/AmazingFeature`)
5. 🔄 Ouvrir une Pull Request

### **Standards de Code**
- Code en français pour la logique métier
- Commentaires détaillés
- Respect des conventions Java
- Tests unitaires obligatoires

---

## 📝 **CHANGELOG**

### **Version 2.0.0** (Octobre 2025)
- ✨ Système de sécurité complet avec 27 permissions
- 🔐 Authentification SHA-256
- 📊 Module de maintenance avancé
- 🎨 Interface moderne avec FlatLaf
- 📱 Thèmes clair/sombre

### **Version 1.0.0** (Septembre 2025)
- 🚗 Gestion de base des véhicules
- 👥 Système utilisateurs simple
- 📋 Affectations basiques

---

## 📞 **SUPPORT**

### **Contact**
- 👨‍💻 **Développeur**: Blaise MUBADI
- 📧 **Email**: blaise@gmail.com
- 🐙 **GitHub**: [@BlaiseMUBADI](https://github.com/BlaiseMUBADI)

### **Signaler un Bug**
Utilisez le système d'[Issues GitHub](https://github.com/BlaiseMUBADI/Nexus_BMB_Tech_Gestion_Charroi_Auto/issues)

---

## 📄 **LICENCE**

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

---

## 🌟 **REMERCIEMENTS**

- **FlatLaf** pour le Look and Feel moderne
- **MySQL** pour la robustesse de la base de données
- **Java Swing** pour la flexibilité de l'interface
- La communauté **Open Source** pour l'inspiration

---

## 📊 **STATISTIQUES DU PROJET**

```
📁 Fichiers de code: 50+
📄 Documentation: 15 guides
🔐 Permissions: 27 granulaires
🎭 Rôles: 5 niveaux
🗄️ Tables BDD: 12 principales
⭐ Fonctionnalités: 20+ modules
```

---

*Dernière mise à jour: 1er octobre 2025*
*Version: 2.0.0 - Système de Gestion Charroi Auto*

---

## 🎯 **PROCHAINES ÉTAPES**

- [ ] 📱 Application mobile companion
- [ ] 🌐 Interface web responsive  
- [ ] 📊 Analytics avancées
- [ ] 🔔 Notifications push
- [ ] 🗺️ Intégration GPS temps réel
- [ ] 📋 Module de facturation
- [ ] 🚀 API REST complète

**Merci d'utiliser Nexus BMB Tech Gestion Charroi Auto !** ⭐