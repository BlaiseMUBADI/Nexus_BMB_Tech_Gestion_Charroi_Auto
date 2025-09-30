@echo off
echo === Lancement de Gestion Charroi Auto ===

REM Définir le classpath complet
set CLASSPATH=build\classes;lib\*;src\Librairies_perso\*

echo Lancement de l'application...
echo Classpath: %CLASSPATH%
echo.

REM Lancer l'application avec les paramètres Java 25
java --enable-native-access=ALL-UNNAMED --add-opens java.desktop/java.awt=ALL-UNNAMED --add-opens java.desktop/sun.font=ALL-UNNAMED -cp "%CLASSPATH%" nexus_bmb_soft.application.Application

echo.
echo Application fermée.
pause