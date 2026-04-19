#!/bin/bash

set -e

echo "=== Build Mac Icon ==="

BASE="$(pwd)"
MAC_DIR="$BASE/lib/mac"
ICONSET="$MAC_DIR/iconset"

# ----------------------------
# CLEAN
# ----------------------------
rm -rf "$ICONSET"
mkdir -p "$ICONSET"

# ----------------------------
# SOURCE PNG
# ----------------------------
SRC="$MAC_DIR/jpcnd.png"

if [ ! -f "$SRC" ]; then
    echo "❌ PNG fehlt: $SRC"
    exit 1
fi

# ----------------------------
# GENERATE ICONSET (korrekt!)
# ----------------------------
convert "$SRC" -resize 16x16   "$ICONSET/icon_16x16.png"
convert "$SRC" -resize 32x32   "$ICONSET/icon_16x16@2x.png"

convert "$SRC" -resize 32x32   "$ICONSET/icon_32x32.png"
convert "$SRC" -resize 64x64   "$ICONSET/icon_32x32@2x.png"

convert "$SRC" -resize 128x128 "$ICONSET/icon_128x128.png"
convert "$SRC" -resize 256x256 "$ICONSET/icon_128x128@2x.png"

convert "$SRC" -resize 256x256 "$ICONSET/icon_256x256.png"
convert "$SRC" -resize 512x512 "$ICONSET/icon_256x256@2x.png"

convert "$SRC" -resize 512x512 "$ICONSET/icon_512x512.png"
convert "$SRC" -resize 1024x1024 "$ICONSET/icon_512x512@2x.png"

# ----------------------------
# REMOVE DUPLICATES (wichtig!)
# ----------------------------
rm "$ICONSET/icon_256x256.png"
rm "$ICONSET/icon_512x512.png"

# ----------------------------
# BUILD ICNS
# ----------------------------
cd "$MAC_DIR"

png2icns jpcnd.icns iconset/*.png

# ----------------------------
# TEST
# ----------------------------
echo ""
echo "=== TEST ==="
icns2png -x jpcnd.icns

echo ""
echo "✅ Fertig: $MAC_DIR/jpcnd.icns"
