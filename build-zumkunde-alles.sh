#!/bin/bash
# =====================================================
# build-zumkunde-alles.sh
# Schlanke ZIPs (inkl. README, User-Installation)
# Version: 1.26.0330
# =====================================================
set -e

# ----------------------------
# Version setzen
# ----------------------------
MAJOR=1
YEAR=$(date +"%y")
DATE_PART=$(date +"%m%d")
TIME_PART=$(date +"%H%M")
BUILD_VERSION="$MAJOR.$YEAR.$DATE_PART.$TIME_PART"

# ----------------------------
# Pfade
# ----------------------------
ROOT="$(pwd)"
TARGET="$ROOT/target"
APP_JAR="$TARGET/jpcnd.jar"
LIB_DIR="$TARGET/jpcnd_lib"
FX_SRC="$ROOT/lib/javafx"
RUN_SCRIPT="$ROOT/run-jpcnd.sh"
README="$ROOT/README-user.txt"

OUT="$ROOT/zumkunde"         # ZIP-Output

echo "============================================"
echo "    BUILD START - Version: $BUILD_VERSION"
echo "============================================"
echo " ZChecks ..."
# ----------------------------
# Checks
# ----------------------------
[ -f "$APP_JAR" ] || { echo "❌ jpcnd.jar fehlt"; exit 1; }
[ -d "$LIB_DIR" ] || { echo "❌ jpcnd_lib fehlt"; exit 1; }
[ -f "$RUN_SCRIPT" ] || { echo "❌ run-jpcnd.sh fehlt"; exit 1; }
[ -f "$README" ] || { echo "❌ README-user.txt fehlt"; exit 1; }
[ -f "$ROOT/resources/icons/linux/jpcnd.png" ] || { echo "❌ Icon fehlt"; exit 1; }

# ----------------------------
# Helper: JavaFX minimal kopieren
# ----------------------------
echo " JafaFX minimal kopieren ..."
copy_javafx() {
    DEST="$1"
    PLATFORM="$2"

    mkdir -p "$DEST/javafx"
    cp "$FX_SRC"/javafx*.jar "$DEST/javafx/" 2>/dev/null || true

    case "$PLATFORM" in
        linux)
            cp -r "$FX_SRC/linux-sdk/lib" "$DEST/javafx/" ;;
        macos-x64)
            cp -r "$FX_SRC/macos-x64" "$DEST/javafx/" ;;
        macos-arm64)
            cp -r "$FX_SRC/macos-arm64" "$DEST/javafx/" ;;
    esac
}
echo " ZIPs bauen ..."
# ----------------------------
# ZIP bauen
# ----------------------------
build_zip() {
    NAME="$1"
    PLATFORM="$2"

    echo "[ZIP] $NAME"

    TMP="$(mktemp -d)"
    mkdir -p "$TMP/jpcnd"

    cp "$APP_JAR" "$TMP/jpcnd/"
    cp -r "$LIB_DIR" "$TMP/jpcnd/jpcnd_lib"
    cp "$RUN_SCRIPT" "$TMP/jpcnd/"
    cp "$README" "$TMP/jpcnd/"
    
    
    chmod +x "$TMP/jpcnd/run-jpcnd.sh"
    
    cp "$ROOT/resources/icons/linux/jpcnd.png" "$TMP/jpcnd/jpcnd.png"

    copy_javafx "$TMP/jpcnd" "$PLATFORM"
# --------------------------------------------------
# ------- Optional: install-user.sh erstellen
cat > "$TMP/jpcnd/install-user.sh" << 'EOF'
#!/bin/bash
# Installationsskript für Menüeintrag + Icon

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

DESKTOP_FILE="$HOME/.local/share/applications/jpcnd.desktop"
ICON_TARGET="$HOME/.local/share/icons/jpcnd.png"

mkdir -p "$(dirname "$DESKTOP_FILE")"
mkdir -p "$(dirname "$ICON_TARGET")"

# 👉 Icon ins System kopieren
cp "$SCRIPT_DIR/jpcnd.png" "$ICON_TARGET"

# 👉 Desktop-Datei erstellen
cat > "$DESKTOP_FILE" << EOL
[Desktop Entry]
Type=Application
Name=jPCND
Exec=$SCRIPT_DIR/run-jpcnd.sh
Icon=$ICON_TARGET
Terminal=false
Categories=Utility;
StartupWMClass=application.Main
StartupNotify=true
EOL

chmod +x "$SCRIPT_DIR/run-jpcnd.sh"

echo "✅ Menüeintrag erstellt: $DESKTOP_FILE"
echo "🎨 Icon installiert: $ICON_TARGET"
EOF
# ------------------------------------------------------------
    chmod +x "$TMP/jpcnd/install-user.sh"

    # ZIP erstellen
    (cd "$TMP" && zip -rq "$OUT/${NAME}-$BUILD_VERSION.zip" jpcnd)
    cp "$OUT/${NAME}-$BUILD_VERSION.zip" "$OUT/${NAME}-latest.zip"
    rm -rf "$TMP"
}
echo " ZIPs Output vorbereiten ..."
# ----------------------------
# ZIP-Output vorbereiten
# ----------------------------
rm -rf "$OUT"
mkdir -p "$OUT"
echo " ZIPs Platformen ..."
# ----------------------------
# Plattformen ZIP
# ----------------------------
build_zip "jpcnd-linux" "linux"
build_zip "jpcnd-macos-x64" "macos-x64"
build_zip "jpcnd-macos-arm64" "macos-arm64"

echo "✅ ZIPs Fertig: $OUT"
echo "⚠ Hinweis: Nach Entpacken kann jPCND via run-jpcnd.sh gestartet werden."
echo "⚠ Menüeintrag erzeugen: ./install-user.sh ausführen."