#!/bin/bash
# =====================================================
# jPCND BUILD SYSTEM - PRODUCTION LEVEL 3 (CLEAN)
# Linux ZIP + macOS .app Bundle + LATEST artifacts
# =====================================================

set -euo pipefail

# ----------------------------
# VERSION
# ----------------------------
MAJOR=1
BUILD_VERSION="$MAJOR.$(date +%y.%m.%d.%H%M)"

# ----------------------------
# PATHS
# ----------------------------
ROOT="$(pwd)"
TARGET="$ROOT/target"
OUT="$ROOT/zumkunde"

APP_JAR="$TARGET/jpcnd.jar"
LIB_DIR="$TARGET/jpcnd_lib"
RUN_SCRIPT="$ROOT/run-jpcnd.sh"
README="$ROOT/README-user.txt"

FX_ROOT="$ROOT/lib/javafx"
RUNTIME_ROOT="$ROOT/lib/runtime"


# =====================================================
# NAMING STRATEGY (SINGLE SOURCE OF TRUTH)
# =====================================================
APP_ID="jpcnd"

NAME_LINUX="${APP_ID}-linux"
NAME_MAC_X64="${APP_ID}-macos-x64"
NAME_MAC_ARM64="${APP_ID}-macos-arm64"

LATEST_SUFFIX="latest"

# ----------------------------
# CHECKS
# ----------------------------
echo "===================================="
echo " jPCND LEVEL 3 CLEAN BUILD"
echo " Version: $BUILD_VERSION"
echo "===================================="

for f in "$APP_JAR" "$RUN_SCRIPT" "$README"; do
    [ -f "$f" ] || { echo "❌ fehlt: $f"; exit 1; }
done

[ -d "$LIB_DIR" ] || { echo "❌ fehlt lib dir"; exit 1; }

rm -rf "$OUT"
mkdir -p "$OUT"

# =====================================================
# LINUX ZIP
# =====================================================
build_linux() {

    echo ""
    echo "📦 BUILD LINUX ZIP"

    TMP="$(mktemp -d)"
    mkdir -p "$TMP/jpcnd"

    cp "$APP_JAR" "$TMP/jpcnd/"
    cp -r "$LIB_DIR" "$TMP/jpcnd/jpcnd_lib"
    cp "$RUN_SCRIPT" "$TMP/jpcnd/"
    cp "$README" "$TMP/jpcnd/"
    cp -r "$FX_ROOT/linux-sdk/lib" "$TMP/jpcnd/javafx"

    ZIP="$OUT/${NAME_LINUX}-${BUILD_VERSION}.zip"
    ZIP_LATEST="$OUT/${NAME_LINUX}-${LATEST_SUFFIX}.zip"

    (cd "$TMP" && zip -rq "$ZIP" jpcnd)

    cp -f "$ZIP" "$ZIP_LATEST"

    rm -rf "$TMP"

    echo "✅ Linux ZIP: $ZIP"
    echo "📦 Linux LATEST: $ZIP_LATEST"
}

# =====================================================
# MAC APP BUNDLE
# =====================================================
build_macos_app() {

    local ARCH="$1"

    echo ""
    echo "🍏 BUILD MAC APP ($ARCH)"

    # ----------------------------
    # NAME RESOLUTION
    # ----------------------------
    if [ "$ARCH" == "macos-x64" ]; then
        NAME="$NAME_MAC_X64"
    else
        NAME="$NAME_MAC_ARM64"
    fi

    APP="$OUT/${NAME}.app"
    CONTENTS="$APP/Contents"

    rm -rf "$APP"

    # ----------------------------
    # STRUCTURE
    # ----------------------------
    mkdir -p "$CONTENTS/MacOS"
    mkdir -p "$CONTENTS/Java/jpcnd_lib"
    mkdir -p "$CONTENTS/Java/javafx"
    mkdir -p "$CONTENTS/Runtime"

    # ----------------------------
    # LAUNCHER
    # ----------------------------
    cat > "$CONTENTS/MacOS/jpcnd" <<EOF
#!/bin/bash
DIR="\$(cd "\$(dirname "\$0")" && pwd)"

JAVA_HOME="\$DIR/../Runtime/jre"
if [ -d "\$JAVA_HOME" ]; then
  JAVA_BIN="\$JAVA_HOME/bin/java"
else
  JAVA_BIN="java"
fi

exec "\$JAVA_BIN" \\
  --module-path "\$DIR/../Java/javafx/$ARCH" \\
  --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.web \\
  -jar "\$DIR/../Java/jpcnd.jar"
EOF

    chmod +x "$CONTENTS/MacOS/jpcnd"

    # ----------------------------
    # APP FILES
    # ----------------------------
    cp "$APP_JAR" "$CONTENTS/Java/"
    cp -r "$LIB_DIR/"* "$CONTENTS/Java/jpcnd_lib/" || true

    if [ -d "$FX_ROOT/$ARCH" ]; then
        cp -r "$FX_ROOT/$ARCH" "$CONTENTS/Java/javafx/"
    else
        echo "❌ JavaFX fehlt: $ARCH"
        exit 1
    fi

		# ----------------------------
		# Runtime CLEAN FIX v2 (robust)
		# ----------------------------
		
		RUNTIME_SRC="$RUNTIME_ROOT/$ARCH"
		
		# finde echte JDK root (Home ist entscheidend)
		if [ -d "$RUNTIME_SRC/jre/Contents/Home" ]; then
		    RUNTIME_SRC="$RUNTIME_SRC/jre/Contents/Home"
		elif [ -d "$RUNTIME_SRC/Contents/Home" ]; then
		    RUNTIME_SRC="$RUNTIME_SRC/Contents/Home"
		elif [ -d "$RUNTIME_SRC/jre" ]; then
		    RUNTIME_SRC="$RUNTIME_SRC/jre"
		fi
		
		mkdir -p "$CONTENTS/Runtime/jre"
		
		cp -R "$RUNTIME_SRC/"* "$CONTENTS/Runtime/jre/"

    # ----------------------------
    # Info.plist
    # ----------------------------
    cat > "$CONTENTS/Info.plist" <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<plist version="1.0">
<dict>
    <key>CFBundleName</key>
    <string>jPCND</string>

    <key>CFBundleExecutable</key>
    <string>jpcnd</string>

    <key>CFBundleIdentifier</key>
    <string>com.jpcnd.app</string>

    <key>CFBundleVersion</key>
    <string>$BUILD_VERSION</string>

    <key>CFBundlePackageType</key>
    <string>APPL</string>

    <key>LSMinimumSystemVersion</key>
    <string>11.0</string>
</dict>
</plist>
EOF

    # ----------------------------
    # ZIP OUTPUT
    # ----------------------------
    ZIP="$OUT/${NAME}-${BUILD_VERSION}.zip"
    ZIP_LATEST="$OUT/${NAME}-${LATEST_SUFFIX}.zip"

    (cd "$OUT" && zip -rq "$ZIP" "$(basename "$APP")")

    cp -f "$ZIP" "$ZIP_LATEST"

    echo "✅ Mac App fertig: $APP"
    echo "📦 ZIP: $ZIP"
    echo "📦 LATEST: $ZIP_LATEST"
}

# =====================================================
# BUILD ALL
# =====================================================
build_linux
build_macos_app "macos-x64"
build_macos_app "macos-arm64"

echo ""
echo "===================================="
echo "🎉 BUILD COMPLETE (LEVEL 3 CLEAN)"
echo "OUTPUT: $OUT"
echo "VERSION: $BUILD_VERSION"
echo "===================================="

# optional DMG step
bash ./build-dmg.sh || true