@echo off
echo === Compilation du projet Gestion Charroi Auto ===

REM Nettoyer les anciens builds
if exist build rmdir /s /q build
mkdir build\classes

REM Définir le classpath avec toutes les JARs
set CLASSPATH=lib\*;src\Librairies_perso\*

echo Compilation en cours...

REM Créer la liste des fichiers Java
dir /s /b src\*.java > sources.txt

REM Compiler tous les fichiers Java
javac -cp "%CLASSPATH%" -d build\classes @sources.txt

if %ERRORLEVEL% == 0 (
    echo === COMPILATION RÉUSSIE ===
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