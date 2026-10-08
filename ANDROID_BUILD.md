
# RUNNA.WORLD Android PWA

## Install (10 sec)
1. mvn package
2. java -jar target/*.jar --server.address=0.0.0.0
3. On Android Chrome open http://YOUR_PC_IP:8080/track
4. Menu -> Install App

## For HTTPS (required for GPS):
ngrok http 8080 -> use https URL

## Build real APK:
Go to pwabuilder.com -> enter your https URL -> Android -> Download APK
Package: world.runna.app
