#!/bin/bash
set -e

APP_NAME="jPCND"
APP_DIR="$(pwd)/zumkunde/${APP_NAME}-macos-x64.app"
OUT_DIR="$(pwd)/zumkunde"

DMG_NAME="${APP_NAME}-macos-x64-latest.dmg"

if [ ! -d "$APP_DIR" ]; then
    echo "❌ APP fehlt: $APP_DIR"
    exit 1
fi

echo "🍏 BUILD DMG"

TMP_DMG="${OUT_DIR}/temp.dmg"

rm -f "$TMP_DMG" "$OUT_DIR/$DMG_NAME"

# Größe grob berechnen (500MB safe default)
APP_SIZE_MB=500

# create-dmg bevorzugt
if command -v create-dmg >/dev/null 2>&1; then

    create-dmg \
        --volname "$APP_NAME" \
        --window-pos 200 120 \
        --window-size 800 400 \
        --icon-size 100 \
        --icon "$APP_NAME.app" 200 190 \
        --hide-extension "$APP_NAME.app" \
        --app-drop-link 600 185 \
        "$OUT_DIR/$DMG_NAME" \
        "$APP_DIR"

else
    echo "⚠️ create-dmg nicht installiert → fallback ISO"

    mkdir -p /tmp/dmgstage
    rm -rf /tmp/dmgstage/*
    cp -r "$APP_DIR" /tmp/dmgstage/

    genisoimage \
        -V "$APP_NAME" \
        -D -R -apple \
        -no-pad \
        -o "$OUT_DIR/$APP_NAME.iso" \
        /tmp/dmgstage

    echo "⚠️ ISO erstellt statt DMG: $OUT_DIR/$APP_NAME.iso"
fi

echo "✅ DMG READY: $OUT_DIR/$DMG_NAME"