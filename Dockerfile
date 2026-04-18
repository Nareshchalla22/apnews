# Step 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Run
FROM eclipse-temurin:17-jre
WORKDIR /app
# We use a specific wildcard to find your JAR
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# The -Dserver.port is a backup to ensure Spring Boot finds Render's port
ENTRYPOINT ["java", "-Xmx512m", "-Dserver.port=${PORT:8080}", "-jar", "app.jar"]