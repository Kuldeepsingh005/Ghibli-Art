FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -Dmaven.test.skip=true

# Stage 2: Create the final image
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create a non-root user and group
RUN groupadd --system spring && useradd --system --gid spring spring
USER spring:spring

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your Spring Boot application listens on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]