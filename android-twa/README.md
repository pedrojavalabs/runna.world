
# Build APK Locally

## Quick Start
npm i -g @bubblewrap/cli
cd android-twa
./build-apk.sh  (or build-apk.bat on Windows)

## Prerequisites
- Node 18+, JDK 17, Android SDK
- Or use Docker:

docker run --rm -v %cd%:/app -w /app openjdk:17 bash -c "npm i -g @bubblewrap/cli && bubblewrap build"

## Output
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/bundle/release/app-release-bundle.aab

## Install
adb install app/build/outputs/apk/debug/app-debug.apk

## For local IP testing without domain:
Edit twa-manifest.json host to YOUR_IP:8080 and webManifestUrl to http://YOUR_IP:8080/manifest.webmanifest
Then enable chrome://flags #unsafely-treat-insecure-origin-as-secure

## Permissions (auto):
ACCESS_FINE_LOCATION, WAKE_LOCK, FOREGROUND_SERVICE
