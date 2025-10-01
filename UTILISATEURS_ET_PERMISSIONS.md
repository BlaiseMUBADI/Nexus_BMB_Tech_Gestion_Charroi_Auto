# 🔐 GUIDE COMPLET DES UTILISATEURS ET PERMISSIONS
## Système de Gestion Charroi Auto

---

## 📋 **LISTE DES UTILISATEURS SYSTÈME**

### 🔴 **SUPER ADMINISTRATEUR**
```
👤 Utilisateur: ADMIN001
📧 Email: admin@charroi.system
🔑 Mot de passe: Admin12345
🎯 Rôle: super_admin
📊 Statut: ACTIF
ℹ️  Description: Administrateur système avec tous les droits
```

### 🟠 **ADMINISTRATEURS**
```
👤 Utilisateur: COND02 (Colonel Tshibanda Jean Didier)
📧 Email: [non défini]
🔑 Mot de passe: hash_password_789
🎯 Rôle: admin
📊 Statut: ACTIF
ℹ️  Description: Administrateur opérationnel
```

### 🟡 **GESTIONNAIRES**
```
👤 Utilisateur: COND04 (Capitaine Mbayo Gabriel)
📧 Email: [non défini]
🔑 Mot de passe: hash_password_456
🎯 Rôle: gestionnaire
📊 Statut: ACTIF
ℹ️  Description: Gestionnaire du parc automobile

👤 Utilisateur: GES01 (BADIBANGA Jeampy)
📧 Email: jeampy@gmail.com
🔑 Mot de passe: 123456
🎯 Rôle: gestionnaire
📊 Statut: ACTIF
ℹ️  Description: Gestionnaire des opérations
```

### 🟢 **CONDUCTEURS SENIOR**
```
👤 Utilisateur: COND01 (KAPINGA Papy)
📧 Email: blaise@gmail.com
🔑 Mot de passe: 123456
🎯 Rôle: conducteur_senior
📊 Statut: ACTIF
ℹ️  Description: Conducteur senior avec privilèges étendus
```

### 🔵 **CONDUCTEURS**
```
👤 Utilisateur: COND03 (Major Kabila Jacque)
📧 Email: [non défini]
🔑 Mot de passe: hash_password_123
🎯 Rôle: conducteur
📊 Statut: ACTIF
ℹ️  Description: Conducteur standard
```

---

## 🎭 **HIÉRARCHIE DES RÔLES**

```
🔴 SUPER_ADMIN    → Contrôle total du système
    ↓
🟠 ADMIN          → Administration opérationnelle
    ↓
🟡 GESTIONNAIRE   → Gestion du parc et affectations
    ↓
🟢 CONDUCTEUR_SENIOR → Conducteur avec privilèges
    ↓
🔵 CONDUCTEUR     → Utilisateur final standard
```

---

## 🔐 **MATRICE DES PERMISSIONS PAR RÔLE**

### 📊 **MODULES ET ACTIONS DISPONIBLES**

| Module | Actions Disponibles |
|--------|-------------------|
| **SYSTEM** | BACKUP, CONFIG, AUDIT |
| **USER** | CREATE, READ, UPDATE, DELETE, MANAGE_PERMISSIONS |
| **VEHICLE** | CREATE, READ, UPDATE, DELETE |
| **MAINTENANCE** | CREATE, READ, UPDATE, DELETE, EXECUTE |
| **ASSIGNMENT** | CREATE, READ, UPDATE, DELETE, EXECUTE |
| **ALERT** | CREATE, READ, UPDATE, DELETE |
| **REPORT** | CREATE, READ, EXECUTE |

### 🔴 **SUPER_ADMIN - Permissions Complètes**
```
✅ SYSTEM_BACKUP           ✅ SYSTEM_CONFIG           ✅ SYSTEM_AUDIT
✅ USER_CREATE             ✅ USER_READ               ✅ USER_UPDATE
✅ USER_DELETE             ✅ USER_MANAGE_PERMISSIONS 
✅ VEHICLE_CREATE          ✅ VEHICLE_READ            ✅ VEHICLE_UPDATE
✅ VEHICLE_DELETE          
✅ MAINTENANCE_CREATE      ✅ MAINTENANCE_READ        ✅ MAINTENANCE_UPDATE
✅ MAINTENANCE_DELETE      ✅ MAINTENANCE_EXECUTE
✅ ASSIGNMENT_CREATE       ✅ ASSIGNMENT_READ         ✅ ASSIGNMENT_UPDATE
✅ ASSIGNMENT_DELETE       ✅ ASSIGNMENT_EXECUTE
✅ ALERT_CREATE            ✅ ALERT_READ              ✅ ALERT_UPDATE
✅ ALERT_DELETE
✅ REPORT_CREATE           ✅ REPORT_READ             ✅ REPORT_EXECUTE

📋 TOTAL: 27 permissions actives
```

### 🟠 **ADMIN - Permissions Étendues**
```
❌ SYSTEM_BACKUP           ❌ SYSTEM_CONFIG           ✅ SYSTEM_AUDIT
✅ USER_CREATE             ✅ USER_READ               ✅ USER_UPDATE
✅ USER_DELETE             ✅ USER_MANAGE_PERMISSIONS 
✅ VEHICLE_CREATE          ✅ VEHICLE_READ            ✅ VEHICLE_UPDATE
✅ VEHICLE_DELETE          
✅ MAINTENANCE_CREATE      ✅ MAINTENANCE_READ        ✅ MAINTENANCE_UPDATE
✅ MAINTENANCE_DELETE      ✅ MAINTENANCE_EXECUTE
✅ ASSIGNMENT_CREATE       ✅ ASSIGNMENT_READ         ✅ ASSIGNMENT_UPDATE
✅ ASSIGNMENT_DELETE       ✅ ASSIGNMENT_EXECUTE
✅ ALERT_CREATE            ✅ ALERT_READ              ✅ ALERT_UPDATE
✅ ALERT_DELETE
✅ REPORT_CREATE           ✅ REPORT_READ             ✅ REPORT_EXECUTE

📋 TOTAL: 25 permissions actives
⚠️  RESTRICTIONS: Pas de sauvegarde/config système
```

### 🟡 **GESTIONNAIRE - Gestion Opérationnelle**
```
❌ SYSTEM_*               
❌ USER_*                 
✅ VEHICLE_CREATE          ✅ VEHICLE_READ            ✅ VEHICLE_UPDATE
❌ VEHICLE_DELETE          
✅ MAINTENANCE_CREATE      ✅ MAINTENANCE_READ        ✅ MAINTENANCE_UPDATE
❌ MAINTENANCE_DELETE      ✅ MAINTENANCE_EXECUTE
✅ ASSIGNMENT_CREATE       ✅ ASSIGNMENT_READ         ✅ ASSIGNMENT_UPDATE
❌ ASSIGNMENT_DELETE       ✅ ASSIGNMENT_EXECUTE
✅ ALERT_CREATE            ✅ ALERT_READ              ✅ ALERT_UPDATE
❌ ALERT_DELETE
✅ REPORT_CREATE           ✅ REPORT_READ             ✅ REPORT_EXECUTE

📋 TOTAL: 14 permissions actives
⚠️  RESTRICTIONS: Pas de gestion utilisateurs, pas de suppressions critiques
```

### 🟢 **CONDUCTEUR_SENIOR - Supervision Étendue**
```
❌ SYSTEM_*               ❌ USER_*                 
❌ VEHICLE_CREATE          ✅ VEHICLE_READ            ❌ VEHICLE_UPDATE
❌ VEHICLE_DELETE          
✅ MAINTENANCE_CREATE      ✅ MAINTENANCE_READ        ✅ MAINTENANCE_UPDATE
❌ MAINTENANCE_DELETE      ❌ MAINTENANCE_EXECUTE
❌ ASSIGNMENT_CREATE       ✅ ASSIGNMENT_READ         ❌ ASSIGNMENT_UPDATE
❌ ASSIGNMENT_DELETE       ❌ ASSIGNMENT_EXECUTE
❌ ALERT_CREATE            ✅ ALERT_READ              ❌ ALERT_UPDATE
❌ ALERT_DELETE
❌ REPORT_CREATE           ❌ REPORT_READ             ❌ REPORT_EXECUTE

📋 TOTAL: 6 permissions actives
⚠️  RESTRICTIONS: Lecture principalement + maintenance
```

### 🔵 **CONDUCTEUR - Accès Basique**
```
❌ SYSTEM_*               ❌ USER_*                 
❌ VEHICLE_CREATE          ✅ VEHICLE_READ            ❌ VEHICLE_UPDATE
❌ VEHICLE_DELETE          
❌ MAINTENANCE_CREATE      ❌ MAINTENANCE_READ        ❌ MAINTENANCE_UPDATE
❌ MAINTENANCE_DELETE      ❌ MAINTENANCE_EXECUTE
❌ ASSIGNMENT_CREATE       ✅ ASSIGNMENT_READ         ❌ ASSIGNMENT_UPDATE
❌ ASSIGNMENT_DELETE       ❌ ASSIGNMENT_EXECUTE
❌ ALERT_CREATE            ✅ ALERT_READ              ❌ ALERT_UPDATE
❌ ALERT_DELETE
❌ REPORT_CREATE           ❌ REPORT_READ             ❌ REPORT_EXECUTE

📋 TOTAL: 3 permissions actives
⚠️  RESTRICTIONS: Lecture uniquement des informations personnelles
```

---

## 🔑 **IDENTIFIANTS DE TEST**

### **Pour les Tests de Connexion:**

```bash
# Super Administrateur
Login: ADMIN001
Password: Admin12345

# Administrateur
Login: COND02
Password: hash_password_789

# Gestionnaire 1
Login: COND04
Password: hash_password_456

# Gestionnaire 2
Login: GES01 ou jeampy@gmail.com
Password: 123456

# Conducteur Senior
Login: COND01 ou blaise@gmail.com
Password: 123456

# Conducteur
Login: COND03
Password: hash_password_123
```

---

## ⚙️ **FONCTIONNALITÉS DE SÉCURITÉ**

### 🛡️ **Protection des Comptes**
- ✅ Verrouillage automatique après 5 tentatives échouées
- ✅ Expiration des mots de passe (configurable)
- ✅ Tokens de session sécurisés
- ✅ Audit complet des connexions
- ✅ Contrôle d'accès par IP

### 📊 **Audit et Monitoring**
- ✅ Log de toutes les actions de sécurité
- ✅ Traçabilité des modifications de permissions
- ✅ Rapports d'accès détaillés
- ✅ Monitoring temps réel des sessions

### 🔄 **Permissions Flexibles**
- ✅ Override de permissions par utilisateur
- ✅ Permissions temporaires avec expiration
- ✅ Héritage basé sur les rôles
- ✅ Révocation instantanée

---

## 📝 **NOTES IMPORTANTES**

1. **Changement de Mots de Passe**: 
   - Les mots de passe de test doivent être changés en production
   - Le système supporte SHA-256 pour les nouveaux mots de passe

2. **Gestion des Sessions**:
   - Les sessions sont automatiquement invalidées après inactivité
   - Un utilisateur ne peut avoir qu'une session active à la fois

3. **Évolution des Permissions**:
   - Le système permet d'ajouter de nouvelles permissions facilement
   - Les rôles peuvent être modifiés sans affecter les utilisateurs existants

4. **Sauvegarde de Sécurité**:
   - Toutes les actions critiques sont loggées
   - Les permissions peuvent être restaurées en cas de problème

---

## 🚀 **PROCHAINES ÉTAPES**

1. Tester la connexion avec les identifiants fournis
2. Configurer les mots de passe de production
3. Personnaliser les permissions selon les besoins métier
4. Mettre en place les procédures de sauvegarde sécurisée

---

*Document généré le 1er octobre 2025*
*Version: 1.0 - Système de Gestion Charroi Auto*