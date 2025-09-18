# Multi-stage build for Smart Clinic Management System
# Stage 1: Build the application
FROM maven:3.9.4-eclipse-temurin-17 AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml first to leverage Docker layer caching
COPY pom.xml .

# Download dependencies (this layer will be cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime environment
FROM eclipse-temurin:17-jre-alpine

# Add non-root user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Install necessary packages
RUN apk --no-cache add curl

# Copy the JAR file from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership of the app directory to appuser
RUN chown -R appuser:appgroup /app

# Create directories for logs and temporary files
RUN mkdir -p /app/logs /app/temp && \
    chown -R appuser:appgroup /app/logs /app/temp

# Switch to non-root user
USER appuser

# Expose port 8080
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Environment variables with default values
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENV SERVER_PORT=8080
ENV LOGGING_LEVEL_ROOT=INFO

# JVM options for containerized environment
ENV JAVA_TOOL_OPTIONS="-Djava.security.egd=file:/dev/./urandom"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Labels for better container management
LABEL maintainer="Smart Clinic Management Team"
LABEL version="1.0.0"
LABEL description="Smart Clinic Management System - Spring Boot Application"
LABEL org.opencontainers.image.source="https://github.com/smartcare/clinic-management-system"
LABEL org.opencontainers.image.title="Smart Clinic Management System"
LABEL org.opencontainers.image.description="A comprehensive clinic management system built with Spring Boot"
LABEL org.opencontainers.image.version="1.0.0"