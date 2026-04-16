#!/bin/bash
# =====================================================
# jPCND ONE-COMMAND RELEASE PIPELINE
# =====================================================

set -e

echo "===================================="
echo "🚀 jPCND RELEASE PIPELINE START"
echo "===================================="

ROOT="$(pwd)"

BUILD_SCRIPT="$ROOT/build-zumkunde-alles.sh"

# ----------------------------
# CHECK BUILD SCRIPT
# ----------------------------
if [ ! -f "$BUILD_SCRIPT" ]; then
    echo "❌ build-zumkunde-alles.sh nicht gefunden!"
    exit 1
fi

if [ ! -x "$BUILD_SCRIPT" ]; then
    echo "🔧 mache build-script ausführbar..."
    chmod +x "$BUILD_SCRIPT"
fi

# ----------------------------
# CLEAN OLD LOGS (optional)
# ----------------------------
echo "🧹 Cleanup..."

# ----------------------------
# RUN BUILD
# ----------------------------
echo "🏗️  Starte Build..."
"$BUILD_SCRIPT"

# ----------------------------
# CHECK OUTPUT
# ----------------------------
OUT="$ROOT/zumkunde"

if [ ! -d "$OUT" ]; then
    echo "❌ Output Ordner fehlt!"
    exit 1
fi

echo ""
echo "===================================="
echo "🎉 RELEASE SUCCESSFUL"
echo "===================================="
echo "📦 Output:"
ls -lh "$OUT"
echo "===================================="
echo "✔ Fertig!"
echo ""