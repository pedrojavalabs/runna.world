#!/bin/bash
npm install @capacitor/core @capacitor/cli @capacitor/android @capacitor/geolocation
npx cap init "RUNNA.WORLD" "world.runna.app" --web-dir=dist
npx cap add android
echo "Edit capacitor.config.json with your IP"
npx cap copy android
npx cap open android
