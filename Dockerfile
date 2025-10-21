# ============================
# 🏗️ Stage 1: Build the JAR using Maven
# ============================
FROM maven:3.9.9-eclipse-temurin-21 AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies (cache optimization)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy all source code
COPY src ./src

# Package the application (skip tests for faster builds)
RUN mvn clean package -DskipTests


# ============================
# 🚀 Stage 2: Run the Spring Boot application
# ============================
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy built JAR from builder
COPY --from=builder /app/target/*.jar app.jar

# Render provides PORT dynamically via environment variable
ENV PORT=8080

# Expose the same port for local testing
EXPOSE 8080

# Run the Spring Boot application on Render's assigned port
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=${PORT}"]
