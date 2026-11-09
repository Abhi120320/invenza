# Multi-stage build for Invenza Java Spring Boot Backend (Supports both linux/arm64 and linux/amd64)
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8000

ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
