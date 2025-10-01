@echo off
echo ========================================
echo  INITIALISATION SYSTEME AUTHENTIFICATION
echo ========================================
echo.

echo 🔐 Configuration des comptes utilisateurs par defaut...
echo.

cd src
javac -cp "../lib/*" nexus_bmb_soft/security/InitializeDefaultUsers.java

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Erreur de compilation !
    pause
    exit /b 1
)

java -cp "../lib/*;." nexus_bmb_soft.security.InitializeDefaultUsers

echo.
echo ========================================
echo  CONFIGURATION TERMINEE !
echo ========================================
echo.
echo 🎯 UTILISEZ CES COORDONNEES POUR VOUS CONNECTER :
echo.
echo 👑 ADMINISTRATEUR (TOUS LES DROITS) :
echo    Utilisateur : admin
echo    Mot de passe : Admin123!
echo.
echo ⚠️  N'oubliez pas de changer ce mot de passe !
echo.
pause