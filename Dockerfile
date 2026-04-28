# ════════════════════════════════════════════════════════════════════
# AP13 NEWS — Dockerfile (fixed)
# ════════════════════════════════════════════════════════════════════

# ── Stage 1: Build ───────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies first (cached layer — speeds up rebuilds)
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# ── Stage 2: Run ─────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Health check for Render/EC2
HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8080}/actuator/health || exit 1

EXPOSE 8080

# ERROR WAS HERE: ${PORT:8080} — colon without dash is NOT valid shell syntax
# in a Dockerfile ENTRYPOINT exec form. The JVM received the literal
# string "${PORT:8080}" instead of the actual port number.
# FIX: Use ${PORT:-8080} — dash before default value is correct shell syntax.
ENTRYPOINT ["sh", "-c", "java \
  -Xms128m \
  -Xmx450m \
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=70.0 \
  -Dserver.port=${PORT:-8080} \
  -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-default} \
  -jar app.jar"]