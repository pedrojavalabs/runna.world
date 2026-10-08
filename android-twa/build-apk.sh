#!/bin/bash
set -e
echo "=== RUNNA.WORLD - Local APK Builder ==="
command -v bubblewrap >/dev/null 2>&1 || npm install -g @bubblewrap/cli
if [ ! -f "android.keystore" ]; then
  echo "Creating keystore..."
  keytool -genkey -v -keystore android.keystore -alias android -keyalg RSA -keysize 2048 -validity 10000 -storepass android -keypass android -dname "CN=RUNNA.WORLD, OU=RUNNA, O=RUNNA.WORLD, L=Lisbon, S=Lisbon, C=PT"
fi
bubblewrap build
echo "APK: app/build/outputs/apk/debug/app-debug.apk"
echo "AAB: app/build/outputs/bundle/release/app-release-bundle.aab"
