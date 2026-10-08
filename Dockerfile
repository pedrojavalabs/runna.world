# ---- Build stage ----
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copy only the boot jar, ignore the .original one
# COPY --from=build /app/target/*-SNAPSHOT.jar app.jar
# Alternative if your jar isn't SNAPSHOT:
COPY --from=build /app/target/*.jar app.jar

EXPOSE 10000
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]