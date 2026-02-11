# Resilience4j Example - Maven Monorepo

A Maven monorepo containing two Spring Boot applications demonstrating microservices communication patterns. The project consists of a producer service that manages payments and a consumer service that calls the producer via HTTP.

## Project Structure

```
resilience4j-example/
├── pom.xml                          # Parent Maven POM
├── producer-app/                    # Producer Spring Boot application
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/perficient/resilience4j/producer/
│       │   ├── ProducerApplication.java
│       │   ├── controller/PaymentController.java
│       │   ├── service/PaymentService.java
│       │   └── model/Payment.java
│       └── resources/application.yml
└── consumer-app/                    # Consumer Spring Boot application
    ├── pom.xml
    └── src/main/
        ├── java/com/perficient/resilience4j/consumer/
        │   ├── ConsumerApplication.java
        │   ├── controller/ConsumerController.java
        │   ├── service/ProducerClientService.java
        │   ├── config/WebClientConfig.java
        │   └── model/Payment.java
        └── resources/application.yml
```

## Technologies Used

- **Java 21** - OpenJDK 21 LTS
- **Spring Boot 3.4.1** - Latest Spring Boot framework
- **Maven 3.8.7** - Build and dependency management
- **Spring Web** - REST API endpoints
- **Spring WebFlux** - Reactive HTTP client (Consumer)
- **Spring Actuator** - Health monitoring and metrics
- **Maven Monorepo** - Multi-module project structure

## Applications

### Producer Application (Port 8080)

The producer service manages payment operations and exposes REST endpoints.

**Features:**
- Payment CRUD operations
- In-memory storage with dummy data
- Spring Actuator monitoring
- RESTful API with JSON responses

**Dummy Data:**
- Grocery payment: $125.50 (COMPLETED)
- Utility bill payment: $89.99 (PENDING) 
- Restaurant payment: $45.75 (COMPLETED)

### Consumer Application (Port 8081)

The consumer service acts as a proxy that communicates with the producer via HTTP.

**Features:**
- HTTP client using WebClient
- Producer connectivity health checks
- Spring Actuator monitoring
- Proxy endpoints for payment operations

## Prerequisites

- Java 21 (OpenJDK recommended)
- Maven 3.6+
- WSL2/Ubuntu 24.04 (for development)

## Quick Start

### 1. Build the Project

```bash
# Build entire monorepo
mvn clean package

# Or compile without packaging
mvn clean compile
```

### 2. Run Producer Application

```bash
cd producer-app
mvn spring-boot:run
```

The producer will start on `http://localhost:8080`

### 3. Run Consumer Application

```bash
# In a new terminal
cd consumer-app
mvn spring-boot:run
```

The consumer will start on `http://localhost:8081`

## API Endpoints

### Producer Service (Port 8080)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/payments` | Get all payments |
| POST | `/payments` | Create a new payment |
| GET | `/payments/{id}` | Get payment by ID |
| GET | `/payments/health` | Service health check |
| GET | `/actuator/health` | Spring Actuator health |
| GET | `/actuator/*` | All actuator endpoints |

### Consumer Service (Port 8081)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/consumer/payments` | Get all payments (via producer) |
| POST | `/consumer/payments` | Create payment (via producer) |
| GET | `/consumer/health` | Consumer health check |
| GET | `/consumer/producer-health` | Producer connectivity check |
| GET | `/actuator/health` | Spring Actuator health |
| GET | `/actuator/*` | All actuator endpoints |

## Usage Examples

### Get All Payments (Direct from Producer)

```bash
curl -X GET http://localhost:8080/payments
```

### Get All Payments (Via Consumer)

```bash
curl -X GET http://localhost:8081/consumer/payments
```

### Create a New Payment (Via Consumer)

```bash
curl -X POST http://localhost:8081/consumer/payments \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Online shopping",
    "amount": 199.99,
    "currency": "USD"
  }'
```

### Check Services Health

```bash
# Producer health
curl http://localhost:8080/payments/health
curl http://localhost:8080/actuator/health

# Consumer health  
curl http://localhost:8081/consumer/health
curl http://localhost:8081/actuator/health

# Consumer checking producer connectivity
curl http://localhost:8081/consumer/producer-health
```

## Configuration

### Producer Application (`producer-app/src/main/resources/application.yml`)

```yaml
server:
  port: 8080

spring:
  application:
    name: producer-app

management:
  endpoints:
    web:
      exposure:
        include: "*"
```

### Consumer Application (`consumer-app/src/main/resources/application.yml`)

```yaml
server:
  port: 8081

spring:
  application:
    name: consumer-app

producer:
  service:
    url: http://localhost:8080

management:
  endpoints:
    web:
      exposure:
        include: "*"
```

## Payment Model

Both applications use the same Payment model:

```java
public class Payment {
    private String id;
    private String description;
    private BigDecimal amount;
    private String currency;
    private String status;
    private LocalDateTime createdAt;
}
```

**Payment Status:**
- `PENDING` - Payment is being processed
- `COMPLETED` - Payment has been processed successfully
- `FAILED` - Payment processing failed

## Development

### Running Tests

```bash
# Run all tests
mvn test

# Run tests for specific module
mvn test -pl producer-app
mvn test -pl consumer-app
```

### Building Individual Modules

```bash
# Build only producer
mvn clean package -pl producer-app

# Build only consumer  
mvn clean package -pl consumer-app
```

### IDE Setup

Import the root `pom.xml` as a Maven project. Both IntelliJ IDEA and Eclipse should automatically recognize the multi-module structure.

## Monitoring

Both applications include Spring Actuator endpoints for monitoring:

- **Health**: `/actuator/health`
- **Info**: `/actuator/info` 
- **Metrics**: `/actuator/metrics`
- **Environment**: `/actuator/env`

Access all endpoints at:
- Producer: http://localhost:8080/actuator
- Consumer: http://localhost:8081/actuator

## Future Enhancements

This project is designed as a foundation for implementing Resilience4j patterns:

- **Circuit Breaker** - Prevent cascading failures
- **Retry** - Automatic retry on transient failures  
- **Rate Limiter** - Control request rate
- **Time Limiter** - Timeout handling
- **Bulkhead** - Isolate critical resources

## Troubleshooting

### Common Issues

1. **Port Already in Use**
   ```bash
   # Check what's using the port
   lsof -i :8080
   lsof -i :8081
   ```

2. **Java Version Issues**
   ```bash
   # Verify Java version
   java -version
   echo $JAVA_HOME
   ```

3. **Maven Issues**
   ```bash
   # Verify Maven version
   mvn -version
   echo $MAVEN_HOME
   ```

### Logs

Application logs are available in the console output when running with `mvn spring-boot:run`.

For production deployments, configure logging in `application.yml`:

```yaml
logging:
  level:
    com.perficient: DEBUG
    org.springframework: INFO
```

## License

This project is licensed under the MIT License.