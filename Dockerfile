 FROM eclipse-temurin:21-jdk-alpine AS builder
 WORKDIR /app
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
COPY src src
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/*.jar"]