#!/bin/bash
# Installationsskript für Menüeintrag + Icon
# Version 2026-04-22
# -------------------------------------------------------------

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
Exec=sh -c "$SCRIPT_DIR/run-jpcnd.sh"
Path=$SCRIPT_DIR
Icon=$ICON_TARGET
Terminal=false
Categories=Utility;
StartupWMClass=application.Main
StartupNotify=true
EOL

chmod +x "$SCRIPT_DIR/run-jpcnd.sh"

echo "✅ Menüeintrag erstellt: $DESKTOP_FILE"
echo "🎨 Icon installiert: $ICON_TARGET"

# ------------------------------------------------------------