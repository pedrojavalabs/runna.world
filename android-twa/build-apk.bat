@echo off
echo === RUNNA.WORLD APK Builder ===
where bubblewrap >nul 2>nul || npm install -g @bubblewrap/cli
if not exist "android.keystore" (
  keytool -genkey -v -keystore android.keystore -alias android -keyalg RSA -keysize 2048 -validity 10000 -storepass android -keypass android -dname "CN=RUNNA.WORLD, OU=RUNNA, O=RUNNA.WORLD, L=Lisbon, S=Lisbon, C=PT"
)
bubblewrap build
echo APK built
pause
