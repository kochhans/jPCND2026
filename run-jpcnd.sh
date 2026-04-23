#!/bin/bash

# =====================================================
# jPCND START SCRIPT (ULTRA ROBUST)
# =====================================================

set -e

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"

APP_JAR="$BASE_DIR/jpcnd.jar"
LIB_DIR="$BASE_DIR/jpcnd_lib"
FX_DIR="$BASE_DIR/javafx"

echo "===================================="
echo "🚀 Starte jPCND"
echo "===================================="

# ----------------------------
# JAVA CHECK
# ----------------------------
# ----------------------------
# JAVA (BUNDLED FIRST)
# ----------------------------
if [ -x "$BASE_DIR/runtime/jre/bin/java" ]; then
    JAVA_CMD="$BASE_DIR/runtime/jre/bin/java"
    echo "☕ Verwende gebündelte Runtime"
elif command -v java >/dev/null 2>&1; then
    JAVA_CMD="java"
    echo "☕ Verwende System-Java"
else
    echo "❌ Kein Java gefunden"
    exit 1
fi

JAVA_VER=$("$JAVA_CMD" -version 2>&1 | head -n 1)
echo "☕ Java Version: $JAVA_VER"

# ----------------------------
# FILE CHECK
# ----------------------------
[ -f "$APP_JAR" ] || { echo "❌ jpcnd.jar fehlt"; exit 1; }
[ -d "$LIB_DIR" ] || { echo "❌ jpcnd_lib fehlt"; exit 1; }
if [ ! -d "$FX_DIR" ] && [ ! -d "$BASE_DIR/javafx_jars" ]; then
    echo "❌ JavaFX fehlt"
    exit 1
fi

# ----------------------------
# CLASSPATH
# ----------------------------
CP="$APP_JAR:$LIB_DIR/*"

# ----------------------------
# JAVAFX AUTO-DETECT
# ----------------------------

MODULE_PATH=""
LIB_PATH=""

echo "🔍 Suche JavaFX..."

# 1. Neue saubere Struktur (empfohlen)
if [ -d "$BASE_DIR/javafx_jars" ] && [ -d "$BASE_DIR/javafx_lib" ]; then
    MODULE_PATH="$BASE_DIR/javafx_jars"
    LIB_PATH="$BASE_DIR/javafx_lib"

# 2. SDK Struktur (Linux)
elif [ -d "$FX_DIR/lib" ]; then
    MODULE_PATH="$FX_DIR/lib"
    LIB_PATH="$FX_DIR/lib"

# 3. macOS Struktur
elif [ -d "$FX_DIR/macos-arm64" ] || [ -d "$FX_DIR/macos-x64" ]; then
    ARCH="$(uname -m)"

    if [[ "$ARCH" == "arm64" || "$ARCH" == "aarch64" ]]; then
        MODULE_PATH="$FX_DIR/macos-arm64/lib"
        LIB_PATH="$FX_DIR/macos-arm64/lib"
    else
        MODULE_PATH="$FX_DIR/macos-x64/lib"
        LIB_PATH="$FX_DIR/macos-x64/lib"
    fi

# 4. Flat Struktur (dein aktueller ZIP)
elif ls "$FX_DIR"/*.jar >/dev/null 2>&1; then
    MODULE_PATH="$FX_DIR"
    LIB_PATH="$FX_DIR"

else
    echo "❌ Kein gültiges JavaFX Setup gefunden"
    exit 1
fi

echo "✅ JavaFX Module: $MODULE_PATH"
echo "✅ JavaFX Native: $LIB_PATH"
echo "🔍 MODULE_PATH=$MODULE_PATH"
echo "🔍 LIB_PATH=$LIB_PATH"

# ----------------------------
# START
# ----------------------------
exec "$JAVA_CMD" \
  --enable-native-access=ALL-UNNAMED \
  --add-opens=java.base/java.lang=ALL-UNNAMED \
  --add-opens=java.base/java.nio=ALL-UNNAMED \
  --module-path "$MODULE_PATH" \
  --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.web \
  -Djava.library.path="$LIB_PATH" \
  -cp "$CP" \
  application.Main