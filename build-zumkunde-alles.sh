#!/bin/bash
# =====================================================
# SIMPLE jPCND ZIP RELEASE BUILDER
# =====================================================

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

APP_JAR="$TARGET/jpcnd.jar"
LIB_DIR="$TARGET/jpcnd_lib"

FX_SRC="$ROOT/lib/javafx"

RUN_SCRIPT="$ROOT/run-jpcnd.sh"
README="$ROOT/README-user.txt"

OUT="$ROOT/zumkunde"

# ----------------------------
# CHECKS
# ----------------------------
echo "===================================="
echo "  jPCND SIMPLE ZIP BUILD"
echo "  Version: $BUILD_VERSION"
echo "===================================="

[ -f "$APP_JAR" ] || { echo "❌ jpcnd.jar fehlt"; exit 1; }
[ -d "$LIB_DIR" ] || { echo "❌ jpcnd_lib fehlt"; exit 1; }
[ -f "$RUN_SCRIPT" ] || { echo "❌ run-jpcnd.sh fehlt"; exit 1; }
[ -f "$README" ] || { echo "❌ README fehlt"; exit 1; }

echo "✔ Checks OK"

# ----------------------------
# CLEAN OUTPUT
# ----------------------------
rm -rf "$OUT"
mkdir -p "$OUT"

# ----------------------------
# JAVAFX COPY
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
            cp -r "$FX_SRC/macos-x64" "$DEST/javafx/"
        ;;
        macos-arm64)
            cp -r "$FX_SRC/macos-arm64" "$DEST/javafx/"
        ;;
        *)
            echo "❌ Unknown platform: $PLATFORM"
            exit 1
        ;;
    esac
}

# ----------------------------
# BUILD ZIP FUNCTION
# ----------------------------
build_zip() {

    NAME="$1"
    PLATFORM="$2"

    echo ""
    echo "===================================="
    echo "📦 BUILD: $NAME ($PLATFORM)"
    echo "===================================="

    TMP="$(mktemp -d)"
    mkdir -p "$TMP/jpcnd"

    # ----------------------------
    # CORE FILES
    # ----------------------------
    cp "$APP_JAR" "$TMP/jpcnd/"
    cp -r "$LIB_DIR" "$TMP/jpcnd/jpcnd_lib"
    cp "$RUN_SCRIPT" "$TMP/jpcnd/"
    cp "$README" "$TMP/jpcnd/"

    chmod +x "$TMP/jpcnd/run-jpcnd.sh"

    # ----------------------------
    # JAVAFX
    # ----------------------------
    copy_javafx "$TMP/jpcnd" "$PLATFORM"

    # ----------------------------
    # ZIP OUTPUT
    # ----------------------------
    ZIP_NAME="$OUT/${NAME}-${BUILD_VERSION}.zip"
    LATEST="$OUT/${NAME}-latest.zip"

    (cd "$TMP" && zip -rq "$ZIP_NAME" jpcnd)

    cp "$ZIP_NAME" "$LATEST"

    rm -rf "$TMP"

    echo "✅ DONE: $ZIP_NAME"
}

# ----------------------------
# BUILDS
# ----------------------------
build_zip "jpcnd-linux" "linux"
build_zip "jpcnd-macos-x64" "macos-x64"
build_zip "jpcnd-macos-arm64" "macos-arm64"

# ----------------------------
# FINISH
# ----------------------------
echo ""
echo "===================================="
echo "🎉 BUILD COMPLETE"
echo "Output: $OUT"
echo "Version: $BUILD_VERSION"
echo "===================================="