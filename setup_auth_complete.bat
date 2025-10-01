@echo off
echo ====================================================
echo  CONFIGURATION COMPLETE SYSTEME AUTHENTIFICATION
echo  Integration avec base existante bdd_charroi_auto
echo ====================================================
echo.

echo 🏗️  ETAPE 1: Migration des utilisateurs existants...
echo.
call migrate_users.bat

echo.
echo 🔐 ETAPE 2: Initialisation des mots de passe securises...
echo.
call init_users.bat

echo.
echo ====================================================
echo  CONFIGURATION TERMINEE !
echo ====================================================
echo.
echo 📋 VOS COMPTES UTILISATEURS :
echo.
echo 👑 ADMINISTRATEUR PRINCIPAL :
echo    Username : admin
echo    Password : Admin123!
echo    Droits   : TOUS LES DROITS
echo.
echo 👥 UTILISATEURS MIGRES DE VOTRE BASE :
echo    - Colonel Tshibanda : jean.tshibanda (ADMIN)
echo    - Capitaine Mbayo   : gabriel.mbayo (GESTIONNAIRE)  
echo    - KAPINGA Papy      : papy.kapinga (MECANICIEN)
echo    - Major Kabila      : jacque.kabila (CHAUFFEUR)
echo    - BADIBANGA Jeampy  : jeampy.badibanga (GESTIONNAIRE)
echo.
echo 🔑 Mots de passe temporaires generes automatiquement
echo    Format: [Prenom]123! (ex: Jean123!, Gabriel123!)
echo.
echo ⚠️  IMPORTANT :
echo    1. Changez le mot de passe admin Admin123!
echo    2. Les utilisateurs migres doivent changer leur mot de passe
echo    3. Consultez GUIDE_INTEGRATION_EXISTANTE.md pour plus de details
echo.
echo 🚀 Votre systeme est pret ! Lancez run.bat pour commencer
echo.
pause