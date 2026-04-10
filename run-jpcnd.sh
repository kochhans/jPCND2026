#!/bin/bash
# =====================================================
# run-jpcnd.sh - Starte jPCND (ZIP + DEB kompatibel)
# Version: 26.03325
# =====================================================
set -e

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
APP_JAR="$BASE_DIR/jpcnd.jar"
LIB_DIR="$BASE_DIR/jpcnd_lib"
FX_DIR="$BASE_DIR/javafx"

# ------------------------------------------------------------
# Java finden (MIT Runtime-Fallback)
# ------------------------------------------------------------
if [ -x "$BASE_DIR/runtime/bin/java" ]; then
    JAVA_CMD="$BASE_DIR/runtime/bin/java"
elif command -v java >/dev/null 2>&1; then
    JAVA_CMD="java"
else
    echo "❌ Java nicht gefunden. Bitte Java 21+ installieren."
    exit 1
fi

# ------------------------------------------------------------
# OS + Architektur erkennen
# ------------------------------------------------------------
OS="$(uname -s)"
ARCH="$(uname -m)"

case "$OS" in
    Linux*)
        FX_NATIVE="$FX_DIR/lib"
        ;;
    Darwin*)
        case "$ARCH" in
            x86_64)
                FX_NATIVE="$FX_DIR/macos-x64"
                ;;
            arm64)
                FX_NATIVE="$FX_DIR/macos-arm64"
                ;;
            *)
                echo "❌ Unbekannte macOS-Architektur: $ARCH"
                exit 1
                ;;
        esac
        ;;
    *)
        echo "❌ Nicht unterstütztes OS: $OS"
        exit 1
        ;;
esac

# ------------------------------------------------------------
# Checks
# ------------------------------------------------------------
[ -f "$APP_JAR" ] || { echo "❌ jpcnd.jar fehlt"; exit 1; }
[ -d "$LIB_DIR" ] || { echo "❌ jpcnd_lib fehlt"; exit 1; }
[ -d "$FX_NATIVE" ] || { echo "❌ JavaFX Natives fehlen: $FX_NATIVE"; exit 1; }

# ------------------------------------------------------------
# Native Libraries setzen
# ------------------------------------------------------------
export LD_LIBRARY_PATH="$FX_NATIVE:$LD_LIBRARY_PATH"
export DYLD_LIBRARY_PATH="$FX_NATIVE:$DYLD_LIBRARY_PATH"

# ------------------------------------------------------------
# JavaFX Workaround
# ------------------------------------------------------------
export JAVAFX_SW_RENDER=true

# ------------------------------------------------------------
# JavaFX Module Path
# ------------------------------------------------------------
FX_MODULE_PATH="$FX_NATIVE"

# ------------------------------------------------------------
# Start jPCND
# ------------------------------------------------------------
echo "=== Starte jPCND ==="
echo "Base Dir : $BASE_DIR"
echo "Java     : $JAVA_CMD"
echo "OS       : $OS"
echo "Arch     : $ARCH"
echo "FX Native: $FX_NATIVE"

exec "$JAVA_CMD" \
    -Dprism.order=sw \
    --module-path "$FX_MODULE_PATH" \
    --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base \
    -cp "$APP_JAR:$LIB_DIR/*" \
    application.Main
