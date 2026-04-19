#!/bin/bash
set -e

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
FX_SRC="$ROOT/lib/javafx"

RUN_SCRIPT="$ROOT/run-jpcnd.sh"
README="$ROOT/README-user.txt"

# ----------------------------
# CHECKS
# ----------------------------
echo "===================================="
echo "  jPCND SIMPLE BUILD"
echo "  Version: $BUILD_VERSION"
echo "===================================="

[ -f "$APP_JAR" ] || { echo "❌ jpcnd.jar fehlt"; exit 1; }
[ -d "$LIB_DIR" ] || { echo "❌ jpcnd_lib fehlt"; exit 1; }
[ -f "$RUN_SCRIPT" ] || { echo "❌ run-jpcnd.sh fehlt"; exit 1; }
[ -f "$README" ] || { echo "❌ README fehlt"; exit 1; }

echo "✔ Checks OK"

rm -rf "$OUT"
mkdir -p "$OUT"

# ----------------------------
# JAVAFX COPY (FIXED)
# ----------------------------
copy_javafx() {
    DEST="$1"
    PLATFORM="$2"

    mkdir -p "$DEST/javafx"

    case "$PLATFORM" in
        linux)
            cp -r "$FX_SRC/linux-sdk/lib" "$DEST/javafx/"
        ;;
        macos-x64)
            mkdir -p "$DEST/javafx/macos-x64"
            cp "$FX_SRC/macos-x64"/*.dylib "$DEST/javafx/macos-x64/"
        ;;
        macos-arm64)
            mkdir -p "$DEST/javafx/macos-arm64"
            cp "$FX_SRC/macos-arm64"/*.dylib "$DEST/javafx/macos-arm64/"
        ;;
        *)
            echo "❌ Unknown platform: $PLATFORM"
            exit 1
        ;;
    esac
}

# ----------------------------
# VERIFY
# ----------------------------
verify_javafx() {
    BASE="$1"
    PLATFORM="$2"

    case "$PLATFORM" in
        macos-arm64)
            FX="$BASE/javafx/macos-arm64"
        ;;
        macos-x64)
            FX="$BASE/javafx/macos-x64"
        ;;
        linux)
            FX="$BASE/javafx/lib"
        ;;
    esac

    [ -d "$FX" ] || { echo "❌ JavaFX fehlt: $FX"; exit 1; }
}

# ----------------------------
# ZIP BUILD
# ----------------------------
build_zip() {
    NAME="$1"
    PLATFORM="$2"

    echo ""
    echo "📦 BUILD: $NAME"

    TMP="$(mktemp -d)"
    mkdir -p "$TMP/jpcnd"

    cp "$APP_JAR" "$TMP/jpcnd/"
    cp -r "$LIB_DIR" "$TMP/jpcnd/jpcnd_lib"
    cp "$RUN_SCRIPT" "$TMP/jpcnd/"
    cp "$README" "$TMP/jpcnd/"

    copy_javafx "$TMP/jpcnd" "$PLATFORM"
    verify_javafx "$TMP/jpcnd" "$PLATFORM"

    ZIP="$OUT/${NAME}-${BUILD_VERSION}.zip"
    LATEST="$OUT/${NAME}-latest.zip"

    (cd "$TMP" && zip -rq "$ZIP" jpcnd)

    cp -f "$ZIP" "$LATEST"
    rm -rf "$TMP"

    echo "✅ DONE: $ZIP"
}

# ----------------------------
# MAC APP
# ----------------------------
build_macos_app() {
    ARCH="$1"
    NAME="jpcnd-${ARCH}-app"

    echo "🍏 BUILD APP: $ARCH"

    TMP="$(mktemp -d)"
    APP="$TMP/jPCND.app/Contents"

    mkdir -p "$APP/MacOS" "$APP/Java"

    cp "$APP_JAR" "$APP/Java/"
    cp -r "$LIB_DIR" "$APP/Java/jpcnd_lib"

    copy_javafx "$APP/Java" "$ARCH"

    cat > "$APP/MacOS/jpcnd" << 'EOF'
#!/bin/bash
BASE="$(cd "$(dirname "$0")/../Java" && pwd)"

APP_JAR="$BASE/jpcnd.jar"
LIB_DIR="$BASE/jpcnd_lib"
FX_DIR="$BASE/javafx"

ARCH="$(uname -m)"

if [ "$ARCH" = "arm64" ]; then
    FX="$FX_DIR/macos-arm64"
else
    FX="$FX_DIR/macos-x64"
fi

java \
  --module-path "$FX" \
  --add-modules javafx.controls,javafx.fxml \
  -cp "$APP_JAR:$LIB_DIR/*" \
  application.Main
EOF

    chmod +x "$APP/MacOS/jpcnd"

    ZIP="$OUT/${NAME}-${BUILD_VERSION}.zip"
    LATEST="$OUT/${NAME}-latest.zip"

    (cd "$TMP" && zip -rq "$ZIP" jPCND.app)

    cp -f "$ZIP" "$LATEST"
    rm -rf "$TMP"

    echo "✅ APP DONE: $ZIP"
}

# ----------------------------
# MAC BUNDLE (with runtime)
# ----------------------------
build_macos_bundle() {
    ARCH="$1"
    NAME="jpcnd-${ARCH}-bundle"

    echo "🍏 BUILD BUNDLE: $ARCH"

    TMP="$(mktemp -d)"
    APP="$TMP/jPCND.app/Contents"

    mkdir -p "$APP/MacOS" "$APP/Java" "$APP/Runtime"

    cp "$APP_JAR" "$APP/Java/"
    cp -r "$LIB_DIR" "$APP/Java/jpcnd_lib"

    copy_javafx "$APP/Java" "$ARCH"

    cp -r "$ROOT/lib/runtime/$ARCH/jre" "$APP/Runtime/jre"

    cat > "$APP/MacOS/jpcnd" << 'EOF'
#!/bin/bash
BASE="$(cd "$(dirname "$0")/.." && pwd)"

JAVA_HOME="$BASE/Runtime/jre/Contents/Home"
JAVA="$JAVA_HOME/bin/java"

APP_JAR="$BASE/Java/jpcnd.jar"
LIB_DIR="$BASE/Java/jpcnd_lib"

ARCH="$(uname -m)"

if [ "$ARCH" = "arm64" ]; then
    FX="$BASE/Java/javafx/macos-arm64"
else
    FX="$BASE/Java/javafx/macos-x64"
fi

"$JAVA" \
  --module-path "$FX" \
  --add-modules javafx.controls,javafx.fxml \
  -cp "$APP_JAR:$LIB_DIR/*" \
  application.Main
EOF

    chmod +x "$APP/MacOS/jpcnd"

    ZIP="$OUT/${NAME}-${BUILD_VERSION}.zip"
    LATEST="$OUT/${NAME}-latest.zip"

    (cd "$TMP" && zip -rq "$ZIP" jPCND.app)

    cp -f "$ZIP" "$LATEST"
    rm -rf "$TMP"

    echo "✅ BUNDLE DONE: $ZIP"
}

# ----------------------------
# RUN BUILDS
# ----------------------------
build_zip "jpcnd-linux" "linux"
build_zip "jpcnd-macos-x64" "macos-x64"
build_zip "jpcnd-macos-arm64" "macos-arm64"

build_macos_app "macos-x64"
build_macos_app "macos-arm64"

build_macos_bundle "macos-x64"
build_macos_bundle "macos-arm64"

echo ""
echo "🎉 BUILD COMPLETE"
echo "OUT: $OUT"