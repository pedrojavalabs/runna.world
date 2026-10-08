FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copy EVERYTHING - Vaadin needs frontend/, package.json, etc.
COPY . .
RUN mvn clean package -Pproduction -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -Dserver.address=0.0.0.0 -jar app.jar"]