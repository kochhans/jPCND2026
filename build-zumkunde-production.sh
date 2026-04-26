#!/bin/bash
set -euo pipefail

# =====================================================
# jPCND BUILD SYSTEM - PRODUCTION LEVEL 3 (CLEAN FIX)
# Linux ZIP + macOS .app Bundle + LATEST artifacts
# Version 2026-04-26
# =====================================================

MAJOR=1
BUILD_VERSION="$MAJOR.$(date +%y.%m.%d.%H%M)"

ROOT="$(pwd)"
TARGET="$ROOT/target"
OUT="$ROOT/zumkunde"

APP_JAR="$TARGET/jpcnd.jar"
LIB_DIR="$TARGET/jpcnd_lib"
RUN_SCRIPT="$ROOT/run-jpcnd.sh"
README="$ROOT/README-user.txt"

FX_ROOT="$ROOT/lib/javafx"
RUNTIME_ROOT="$ROOT/lib/runtime"

INSTALLER="$ROOT/install-linuxuser.sh"

APP_ID="jpcnd"

NAME_LINUX="${APP_ID}-linux"
NAME_MAC_X64="${APP_ID}-macos-x64"
NAME_MAC_ARM64="${APP_ID}-macos-arm64"

LATEST_SUFFIX="latest"


echo "===================================="
echo " BUILD $BUILD_VERSION"
echo "===================================="


# =====================================================
# CHECKS
# =====================================================
for f in "$APP_JAR" "$RUN_SCRIPT" "$README" "$INSTALLER"; do
    [ -f "$f" ] || { echo "❌ fehlt: $f"; exit 1; }
done

[ -d "$LIB_DIR" ] || { echo "❌ fehlt lib dir"; exit 1; }

echo "🔍 JDBC CHECK"
if ls "$LIB_DIR" | grep -Ei "mysql|maria" >/dev/null; then
    echo "❌ Fremd-JDBC Treiber gefunden"
    exit 1
fi

rm -rf "$OUT"
mkdir -p "$OUT"


# =====================================================
# LINUX BUILD
# =====================================================
build_linux() {

    echo "🐧 BUILD LINUX"

    TMP="$(mktemp -d)"
    APP="$TMP/jpcnd"

    mkdir -p "$APP"

    # ----------------------------
    # CORE APP
    # ----------------------------
    cp "$APP_JAR" "$APP/"
    cp -r "$LIB_DIR" "$APP/jpcnd_lib"
    cp "$RUN_SCRIPT" "$APP/"
    cp "$README" "$APP/"

    # ----------------------------
    # INSTALLER (WICHTIG FIX)
    # ----------------------------
    cp "$INSTALLER" "$APP/"
    chmod +x "$APP/install-linuxuser.sh"

    # Debug CHECK (wichtig!)
    echo "📦 CHECK INSTALLER:"
    ls -l "$APP" | grep install || {
        echo "❌ INSTALLER NICHT IM PACKAGE"
        exit 1
    }

    # ----------------------------
    # JAVAFX
    # ----------------------------
    FX_LINUX="$FX_ROOT/linux/lib"

    mkdir -p "$APP/javafx_jars"
    mkdir -p "$APP/javafx_lib"

    cp "$FX_LINUX/"*.jar "$APP/javafx_jars/"
    cp "$FX_LINUX/"*.so "$APP/javafx_lib/"

    # ----------------------------
    # RUNTIME (jlink)
    # ----------------------------
    JDK_HOME="$RUNTIME_ROOT/linux-x64/jdk"

    "$JDK_HOME/bin/jlink" \
        --add-modules java.base,java.desktop,java.sql,java.xml,java.logging,jdk.unsupported \
        --strip-debug \
        --no-man-pages \
        --no-header-files \
        --compress=2 \
        --output "$APP/runtime"

    echo "✅ jlink Runtime fertig"

    # ----------------------------
    # ZIP
    # ----------------------------
    ZIP="$OUT/${NAME_LINUX}-${BUILD_VERSION}.zip"
    ZIP_LATEST="$OUT/${NAME_LINUX}-${LATEST_SUFFIX}.zip"

    (cd "$TMP" && zip -rq "$ZIP" jpcnd)

    cp -f "$ZIP" "$ZIP_LATEST"

    rm -rf "$TMP"

    echo "✅ Linux Build fertig"
    echo "📦 $ZIP"
}


# =====================================================
# MAC BUILD
# =====================================================
build_macos_app() {

    ARCH="$1"

    echo "🍏 BUILD MAC $ARCH"

    if [ "$ARCH" == "macos-x64" ]; then
        NAME="$NAME_MAC_X64"
    else
        NAME="$NAME_MAC_ARM64"
    fi

    APP="$OUT/${NAME}.app"
    CONTENTS="$APP/Contents"

    rm -rf "$APP"

    mkdir -p "$CONTENTS/MacOS"
    mkdir -p "$CONTENTS/Java/jpcnd_lib"
    mkdir -p "$CONTENTS/Java/javafx_jars"
    mkdir -p "$CONTENTS/Java/javafx_lib"
    mkdir -p "$CONTENTS/Runtime"

    # ----------------------------
    # APP FILES
    # ----------------------------
    cp "$APP_JAR" "$CONTENTS/Java/"
    cp -r "$LIB_DIR/"* "$CONTENTS/Java/jpcnd_lib/" || true

    # ----------------------------
    # JAVAFX
    # ----------------------------
    FX_DIR=$(find "$FX_ROOT/$ARCH" -type d -name "lib" | head -n 1)

    if [ -z "$FX_DIR" ]; then
        echo "❌ JavaFX fehlt $ARCH"
        exit 1
    fi

    cp "$FX_DIR/"*.jar "$CONTENTS/Java/javafx_jars/"
    cp "$FX_DIR/"*.dylib "$CONTENTS/Java/javafx_lib/"

    # ----------------------------
    # RUNTIME
    # ----------------------------
    RUNTIME_SRC="$RUNTIME_ROOT/$ARCH"

    JAVA_HOME=$(find "$RUNTIME_SRC" -type d -path "*Home" | head -n 1)

    if [ -z "$JAVA_HOME" ]; then
        echo "❌ Runtime fehlt"
        exit 1
    fi

    cp -R "$JAVA_HOME/bin" "$CONTENTS/Runtime/"
    cp -R "$JAVA_HOME/lib" "$CONTENTS/Runtime/"

    # ----------------------------
    # LAUNCHER
    # ----------------------------
    cat > "$CONTENTS/MacOS/jpcnd" <<EOF
#!/bin/bash
DIR="\$(cd "\$(dirname "\$0")" && pwd)"

JAVA="\$DIR/../Runtime/bin/java"

exec "\$JAVA" \
 --module-path "\$DIR/../Java/javafx_jars" \
 --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.web \
 -Djava.library.path="\$DIR/../Java/javafx_lib" \
 -jar "\$DIR/../Java/jpcnd.jar"
EOF

    chmod +x "$CONTENTS/MacOS/jpcnd"

    # ----------------------------
    # INFO PLIST
    # ----------------------------
    cat > "$CONTENTS/Info.plist" <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<plist version="1.0">
<dict>
    <key>CFBundleName</key><string>jPCND</string>
    <key>CFBundleExecutable</key><string>jpcnd</string>
    <key>CFBundleIdentifier</key><string>com.jpcnd.app</string>
    <key>CFBundleVersion</key><string>$BUILD_VERSION</string>
</dict>
</plist>
EOF

    ZIP="$OUT/${NAME}-${BUILD_VERSION}.zip"
    ZIP_LATEST="$OUT/${NAME}-${LATEST_SUFFIX}.zip"

    (cd "$OUT" && zip -rq "$ZIP" "$(basename "$APP")")
    cp -f "$ZIP" "$ZIP_LATEST"

    echo "✅ Mac Build fertig: $ARCH"
}


# =====================================================
# RUN ALL
# =====================================================
build_linux
build_macos_app "macos-x64"
build_macos_app "macos-arm64"


echo ""
echo "===================================="
echo "🎉 BUILD COMPLETE LEVEL 3 FIXED"
echo "OUT: $OUT"
echo "VERSION: $BUILD_VERSION"
echo "===================================="