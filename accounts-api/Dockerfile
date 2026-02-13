# Multi-stage build for Accounts API
# Stage 1: Build
FROM maven:3.9-amazoncorretto-21 AS builder

WORKDIR /build

# Copy parent pom and all module poms (Maven needs to see all modules declared in parent)
COPY pom.xml .
COPY payments-api/pom.xml ./payments-api/
COPY accounts-api/pom.xml ./accounts-api/
COPY consumer-app/pom.xml ./consumer-app/
COPY consumer-client/pom.xml ./consumer-client/

# Download dependencies for this module
RUN mvn dependency:go-offline -pl accounts-api -am

# Copy source code for this module only
COPY accounts-api/src ./accounts-api/src

# Build the application
RUN mvn clean package -pl accounts-api -am -DskipTests

# Stage 2: Runtime
FROM amazoncorretto:21

WORKDIR /app

# Copy the built artifact from builder stage
COPY --from=builder /build/accounts-api/target/accounts-api-1.0.0-SNAPSHOT.jar app.jar

# Expose ports
EXPOSE 8083 9003

# Set the startup command
CMD ["java", "-jar", "app.jar"]