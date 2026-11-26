#!/bin/bash

# =========================
# CONFIG
# =========================
APP_NAME="centre-medical"                          # <-- MODIFIE
SRC_DIR="src/main/java"
RES_DIR="src/main/resources"                       # <-- NOUVEAU
WEBAPP_DIR="src/main/webapp"
BUILD_DIR="build"
LIB_DIR="lib"

TOMCAT_HOME="/opt/tomcat"
SERVLET_API_JAR="$LIB_DIR/servlet-api.jar"
FRAMEWORK_JAR="$LIB_DIR/framework.jar"

# sécurité : le framework doit déjà être présent dans lib/
if [ ! -f "$FRAMEWORK_JAR" ]; then
  echo "❌ ERREUR: $FRAMEWORK_JAR introuvable. Compile et copie le framework dans lib/ d'abord."
  exit 1
fi

# <-- NOUVEAU : la configuration doit exister
if [ ! -f "$RES_DIR/db.properties" ]; then
  echo "❌ ERREUR: $RES_DIR/db.properties introuvable."
  exit 1
fi

echo "===== CLEAN BUILD ====="
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR/classes"

echo "===== COMPILATION (avec tous les jars de lib/ en classpath) ====="

CLASSPATH=$(find "$LIB_DIR" -name "*.jar" | tr '\n' ':')

find "$SRC_DIR" -name "*.java" > sources.txt
javac -encoding UTF-8 -cp "$CLASSPATH" -d "$BUILD_DIR/classes" @sources.txt   # <-- MODIFIE
RESULTAT=$?                                                                   # <-- NOUVEAU
rm sources.txt

# <-- NOUVEAU : on s'arrête si la compilation a échoué
if [ $RESULTAT -ne 0 ]; then
  echo "❌ ECHEC DE LA COMPILATION : rien n'a été déployé."
  exit 1
fi

# <-- NOUVEAU : copie des ressources (db.properties) avec les classes
cp -r "$RES_DIR/." "$BUILD_DIR/classes/"

echo "===== PREPARATION WEBAPP (web.xml + vues) ====="

TOMCAT_WEBAPPS="$TOMCAT_HOME/webapps"
APP_DIR="$TOMCAT_WEBAPPS/$APP_NAME"

rm -rf "$APP_DIR"
cp -r "$WEBAPP_DIR" "$APP_DIR"

echo "===== DEPLOY DES JARS (WEB-INF/lib, sauf servlet-api.jar) ====="
mkdir -p "$APP_DIR/WEB-INF/lib"
find "$LIB_DIR" -name "*.jar" ! -name "servlet-api.jar" -exec cp {} "$APP_DIR/WEB-INF/lib/" \;

echo "===== DEPLOY CLASSES (WEB-INF/classes, NON jarrées) ====="
mkdir -p "$APP_DIR/WEB-INF/classes"
cp -r "$BUILD_DIR/classes/." "$APP_DIR/WEB-INF/classes/"

# <-- NOUVEAU : seul ton utilisateur peut lire le mot de passe de la BDD
chmod 600 "$APP_DIR/WEB-INF/classes/db.properties"

echo "===== CLEAN TOMCAT CACHE ====="
rm -rf "$TOMCAT_HOME/work/Catalina/localhost/$APP_NAME"

echo "===== DEPLOY DONE ====="
echo "URL: http://localhost:8080/$APP_NAME/"