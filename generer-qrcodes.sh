#!/bin/bash
# Lance le programme autonome de generation des QR codes.
# Prerequis : avoir lance ./deploy.sh (qui compile dans build/classes).

if [ ! -d build/classes ]; then
  echo "❌ build/classes introuvable : lance d'abord ./deploy.sh"
  exit 1
fi

CLASSPATH="build/classes:$(find lib -name '*.jar' | tr '\n' ':')"

java -Djava.awt.headless=true -cp "$CLASSPATH" mg.centre.qrcode.GenerateurQrCode