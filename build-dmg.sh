#!/bin/bash

set -e

APP_NAME="jpcnd"
APP_SUFFIX="macos-x64"

OUT_DIR="$(pwd)/zumkunde"

APP_DIR="${OUT_DIR}/${APP_NAME}-${APP_SUFFIX}.app"
DMG_NAME="${APP_NAME}-${APP_SUFFIX}-latest.dmg"

if [ ! -d "$APP_DIR" ]; then
    echo "❌ APP fehlt: $APP_DIR"
    exit 1
fi

echo "🍏 BUILD DMG"

rm -f "${OUT_DIR}/${DMG_NAME}"

if command -v create-dmg >/dev/null 2>&1; then

    create-dmg \
        --volname "$APP_NAME" \
        --window-pos 200 120 \
        --window-size 800 400 \
        --icon-size 100 \
        --icon "${APP_NAME}-${APP_SUFFIX}.app" 200 190 \
        --app-drop-link 600 185 \
        "${OUT_DIR}/${DMG_NAME}" \
        "${APP_DIR}"

else
    echo "⚠️ create-dmg nicht installiert → fallback ISO"

    rm -rf /tmp/dmgstage
    mkdir -p /tmp/dmgstage
    cp -r "$APP_DIR" /tmp/dmgstage/

    genisoimage \
        -V "$APP_NAME" \
        -D -R -apple \
        -o "${OUT_DIR}/${APP_NAME}.iso" \
        /tmp/dmgstage
fi

echo "✅ DMG READY: ${OUT_DIR}/${DMG_NAME}"