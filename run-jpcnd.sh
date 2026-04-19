#!/bin/bash

# =====================================================
# jPCND START SCRIPT (BULLETPROOF)
# =====================================================

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"

APP_JAR="$BASE_DIR/jpcnd.jar"
LIB_DIR="$BASE_DIR/jpcnd_lib"
FX_DIR="$BASE_DIR/javafx"

# ----------------------------
# JAVA CHECK
# ----------------------------
if ! command -v java >/dev/null 2>&1; then
    echo ""
    echo "===================================="
    echo "❌ JAVA NICHT GEFUNDEN"
    echo "===================================="
    echo "Bitte installiere Java (mindestens Java 17)."
    echo "Empfohlen: https://adoptium.net"
    echo ""
    exit 1
fi

# ----------------------------
# CHECK FILES
# ----------------------------
if [ ! -f "$APP_JAR" ]; then
    echo "❌ jpcnd.jar fehlt in $BASE_DIR"
    exit 1
fi

if [ ! -d "$LIB_DIR" ]; then
    echo "❌ jpcnd_lib fehlt in $BASE_DIR"
    exit 1
fi

if [ ! -d "$FX_DIR" ]; then
    echo "❌ JavaFX Ordner fehlt in $BASE_DIR"
    exit 1
fi

# ----------------------------
# JAVA VERSION CHECK (optional safety)
# ----------------------------
JAVA_VER=$(java -version 2>&1 | awk -F\" '/version/ {print $2}')

echo "===================================="
echo "🚀 Starte jPCND"
echo "Java Version: $JAVA_VER"
echo "===================================="

# ----------------------------
# CLASSPATH BUILD
# ----------------------------
CP="$APP_JAR:$LIB_DIR/*"

# ----------------------------
# JAVAFX DETECTION
# ----------------------------

# macOS / Linux: beide unterstützen gleiche Struktur
if [ -d "$FX_DIR/macos-arm64" ] || [ -d "$FX_DIR/macos-x64" ]; then

    ARCH="$(uname -m)"

    if [ "$ARCH" = "arm64" ]; then
        FX_PATH="$FX_DIR/macos-arm64/lib"
    elif [ "$ARCH" = "x86_64" ]; then
        FX_PATH="$FX_DIR/macos-x64/lib"
    else
        echo "❌ Unbekannte Architektur: $ARCH"
        exit 1
    fi

elif [ -d "$FX_DIR/lib" ]; then
    # Linux fallback
    FX_PATH="$FX_DIR/lib"

else
    echo "❌ Kein gültiges JavaFX Setup gefunden"
    exit 1
fi

# ----------------------------
# START APPLICATION
# ----------------------------
exec java \
  --module-path "$FX_PATH" \
  --add-modules javafx.controls,javafx.fxml \
  -cp "$CP" \
  application.Main