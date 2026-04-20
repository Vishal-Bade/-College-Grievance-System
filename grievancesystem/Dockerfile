# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Package stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/grievancesystem-0.0.1-SNAPSHOT.jar app.jar

# Render requires applications to bind to the port defined by the PORT environment variable
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
