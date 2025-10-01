@echo off
echo ========================================
echo  MIGRATION UTILISATEURS EXISTANTS
echo  Integration avec bdd_charroi_auto
echo ========================================
echo.

echo 🔄 Migration des utilisateurs de la base existante...
echo.

cd src
javac -cp "../lib/*" nexus_bmb_soft/security/MigrateExistingUsers.java

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Erreur de compilation !
    pause
    exit /b 1
)

java -cp "../lib/*;." nexus_bmb_soft.security.MigrateExistingUsers

echo.
echo ========================================
echo  MIGRATION TERMINEE !
echo ========================================
echo.
echo 📋 PROCHAINES ETAPES :
echo.
echo 1. Lancez init_users.bat pour initialiser les mots de passe
echo 2. Connectez-vous avec vos utilisateurs migres
echo 3. Changez les mots de passe au premier login
echo.
pause