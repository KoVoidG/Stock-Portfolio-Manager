# Multi-stage build for Stock Portfolio Manager
# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first for better layer caching
COPY .mvn/ .mvn/
COPY mvnw mvnw.cmd pom.xml ./

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src/ ./src/

# Build the application (creates jar-with-dependencies)
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/*jar-with-dependencies.jar app.jar

# Create directory for portfolio data persistence
RUN mkdir -p /app/data

# Expose port 8080
EXPOSE 8080

# Set environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/ || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
