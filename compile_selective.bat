@echo off
echo === Compilation SELECTIVE du projet Gestion Charroi Auto ===

REM Nettoyer les anciens builds
if exist build rmdir /s /q build
mkdir build\classes

REM Définir le classpath avec toutes les JARs
set CLASSPATH=lib\*;src\Librairies_perso\*

echo Compilation en cours (sans FormGestionVehicules.java)...

REM Compiler les fichiers Java essentiels seulement
echo Compilation des modèles...
javac -cp "%CLASSPATH%" -d build\classes -sourcepath src src\nexus_bmb_soft\models\*.java

echo Compilation de la base de données...
javac -cp "%CLASSPATH%" -d build\classes -sourcepath src src\nexus_bmb_soft\database\*.java src\nexus_bmb_soft\database\dao\*.java

echo Compilation des utilitaires...
javac -cp "%CLASSPATH%" -d build\classes -sourcepath src src\nexus_bmb_soft\utils\*.java

echo Compilation du menu...
javac -cp "%CLASSPATH%" -d build\classes -sourcepath src src\nexus_bmb_soft\menu\*.java src\nexus_bmb_soft\menu\mode\*.java

echo Compilation de l'application principale...
javac -cp "%CLASSPATH%" -d build\classes -sourcepath src src\nexus_bmb_soft\application\*.java src\nexus_bmb_soft\application\form\*.java

echo Compilation du nouveau formulaire...
javac -cp "%CLASSPATH%" -d build\classes -sourcepath src src\nexus_bmb_soft\application\form\other\FormGestionVehiculesNew.java

if %ERRORLEVEL% == 0 (
    echo === COMPILATION SÉLECTIVE RÉUSSIE ===
    echo Les classes sont dans build\classes
    
    REM Copier les ressources
    xcopy /s /y src\nexus_bmb_soft\icon build\classes\nexus_bmb_soft\icon\
    xcopy /s /y src\nexus_bmb_soft\theme build\classes\nexus_bmb_soft\theme\
    xcopy /s /y src\nexus_bmb_soft\menu\icon build\classes\nexus_bmb_soft\menu\icon\
    
    echo === RESSOURCES COPIÉES ===
) else (
    echo === ERREUR DE COMPILATION ===
    echo Vérifiez les erreurs ci-dessus
)

pause