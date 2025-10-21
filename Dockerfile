# Stage 1: Build the application
 
FROM maven:3.9.7-amazoncorretto-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the final, minimal runtime image
# Uses a lightweight JRE image to run the final application.
FROM amazoncorretto:21-alpine-jre
WORKDIR /app

# Best practice: Run as a non-root user for security.
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

# Expose the port your application will listen on.
EXPOSE 8080

# Copy the built JAR from the 'build' stage.
COPY --from=build /app/target/*.jar app.jar

# Define the command to run the Spring Boot application.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=80.0", "-XX:InitialRAMPercentage=80.0", "-jar", "app.jar"]
