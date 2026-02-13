# Multi-stage build for Consumer Client
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
RUN mvn dependency:go-offline -pl consumer-client -am

# Copy source code for this module only
COPY consumer-client/src ./consumer-client/src

# Build the application
RUN mvn clean package -pl consumer-client -am -DskipTests

# Stage 2: Runtime
FROM amazoncorretto:21

WORKDIR /app

# Copy the built artifact from builder stage
COPY --from=builder /build/consumer-client/target/consumer-client.jar app.jar

# Set the startup command
CMD ["java", "-jar", "app.jar"]