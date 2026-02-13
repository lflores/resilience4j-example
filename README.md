# Resilience4j Example - Maven Monorepo

A Maven monorepo containing three Spring Boot applications and a testing client demonstrating microservices communication patterns. The project consists of a payments API service that manages payments, an accounts API service that manages accounts, a consumer service that calls both APIs via HTTP, and a consumer-client testing tool for continuous monitoring.

## Project Structure

```
resilience4j-example/
├── pom.xml                          # Parent Maven POM
├── payments-api/                    # Payments API Spring Boot application
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/perficient/resilience4j/payments/
│       │   ├── PaymentsApplication.java
│       │   ├── controller/PaymentController.java
│       │   ├── service/PaymentService.java
│       │   └── model/Payment.java
│       └── resources/application.yml
├── accounts-api/                    # Accounts API Spring Boot application
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/perficient/resilience4j/accounts/
│       │   ├── AccountsApplication.java
│       │   ├── controller/AccountController.java
│       │   ├── service/AccountService.java
│       │   └── model/Account.java
│       └── resources/application.yml
├── consumer-app/                    # Consumer Spring Boot application
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/perficient/resilience4j/consumer/
│       │   ├── ConsumerApplication.java
│       │   ├── controller/ConsumerController.java
│       │   ├── service/PaymentClientService.java
│       │   ├── service/AccountClientService.java
│       │   ├── config/WebClientConfig.java
│       │   ├── model/Payment.java
│       │   └── model/Account.java
│       └── resources/application.yml
└── consumer-client/                 # HTTP testing client
    ├── pom.xml
    └── src/main/
        └── java/com/perficient/resilience4j/client/
            ├── ConsumerClient.java
            ├── ClientConfig.java
            ├── ClientMetrics.java
            ├── model/Payment.java
            └── model/Account.java
```

## Technologies Used

- **Java 21** - OpenJDK 21 LTS
- **Spring Boot 3.4.1** - Latest Spring Boot framework
- **Maven 3.8.7** - Build and dependency management
- **Spring Web** - REST API endpoints
- **Spring WebFlux** - Reactive HTTP client (Consumer)
- **Spring Actuator** - Health monitoring and metrics
- **OkHttp 4.12.0** - HTTP client for testing client
- **Jackson 2.16.1** - JSON processing for testing client
- **Maven Monorepo** - Multi-module project structure

## Applications

### Payments API Application (Port 8080)

The payments API service manages payment operations and exposes REST endpoints.

**Features:**
- Payment CRUD operations
- In-memory storage with dummy data
- Spring Actuator monitoring
- RESTful API with JSON responses

**Dummy Data:**
- Grocery payment: $125.50 (COMPLETED)
- Utility bill payment: $89.99 (PENDING) 
- Restaurant payment: $45.75 (COMPLETED)

### Accounts API Application (Port 8083)

The accounts API service manages account operations and exposes REST endpoints.

**Features:**
- Account CRUD operations
- In-memory storage with dummy data
- Spring Actuator monitoring
- RESTful API with JSON responses

**Dummy Data:**
- Grocery Account: $125.50 (IDA - Individual Deposit Account)
- Utility Account: $89.99 (IDA - Individual Deposit Account) 
- Restaurant Account: $45.75 (CCA - Corporate Checking Account)

### Consumer Application (Port 8081)

The consumer service acts as a proxy that communicates with both the payments API and accounts API via HTTP.

**Features:**
- HTTP client using WebClient
- Payments API connectivity health checks
- Accounts API connectivity health checks
- Spring Actuator monitoring
- Proxy endpoints for payment operations

### Consumer Client (Testing Tool)

The consumer-client is a standalone HTTP testing tool that continuously monitors the consumer application.

**Features:**
- Continuous API testing with 5-second polling intervals
- HTTP client using OkHttp
- Performance metrics and response time tracking
- JSON response parsing and validation
- Configurable endpoints and timeouts
- Comprehensive logging and error reporting

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

### 2. Run Payments API Application

```bash
cd payments-api
mvn spring-boot:run
```

### 3. Run Payments API Application

```bash
cd payments-api
mvn spring-boot:run
```

The payments API will start on `http://localhost:8080`

### 4. Run Consumer Application

```bash
# In a new terminal
cd consumer-app
mvn spring-boot:run
```

The consumer will start on `http://localhost:8081`

### 5. Run Consumer Client (Optional)

The consumer-client provides continuous monitoring and testing of the consumer API:

```bash
# In a new terminal (ensure both apps are running)
cd consumer-client
mvn compile exec:java -Dexec.mainClass="com.perficient.resilience4j.client.ConsumerClient"
```

The client will poll `http://localhost:8081/consumer/payments` every 5 seconds and display:
- Response times and HTTP status codes
- JSON payload validation
- Request/response metrics
- Error tracking and recovery

## API Endpoints

### Payments API Service (Port 8080)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/payments/payments` | Get all payments |
| POST | `/payments/payments` | Create a new payment |
| GET | `/payments/payments/{id}` | Get payment by ID |
| GET | `/payments/health` | Service health check |
| GET | `/actuator/health` | Spring Actuator health |
| GET | `/actuator/*` | All actuator endpoints |

### Accounts API Service (Port 8083)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/accounts/accounts` | Get all accounts |
| POST | `/accounts/accounts` | Create a new account |
| GET | `/accounts/accounts/{id}` | Get account by ID |
| GET | `/accounts/health` | Service health check |
| GET | `/actuator/health` | Spring Actuator health |
| GET | `/actuator/*` | All actuator endpoints |

### Consumer Service (Port 8081)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/consumer/payments` | Get all payments (via payments API) |
| POST | `/consumer/payments` | Create payment (via payments API) |
| GET | `/consumer/accounts` | Get all accounts (via accounts API) |
| POST | `/consumer/accounts` | Create account (via accounts API) |
| GET | `/consumer/health` | Consumer health check |
| GET | `/consumer/payment-health` | Payments API connectivity check |
| GET | `/consumer/account-health` | Accounts API connectivity check |
| GET | `/actuator/health` | Spring Actuator health |
| GET | `/actuator/*` | All actuator endpoints |

## Usage Examples

### Get All Payments (Direct from Payments API)

```bash
curl -X GET http://localhost:8080/payments/payments
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

### Get All Accounts (Direct from Accounts API)

```bash
curl -X GET http://localhost:8083/accounts/accounts
```

### Get All Accounts (Via Consumer)

```bash
curl -X GET http://localhost:8081/consumer/accounts
```

### Create a New Account (Via Consumer)

```bash
curl -X POST http://localhost:8081/consumer/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "description": "New checking account",
    "type": "CCA",
    "balance": 1000.00,
    "currency": "USD"
  }'
```

### Check Services Health

```bash
# Payments API health
curl http://localhost:8080/payments/health
curl http://localhost:8080/actuator/health

# Accounts API health
curl http://localhost:8083/accounts/health
curl http://localhost:8083/actuator/health

# Consumer health  
curl http://localhost:8081/consumer/health
curl http://localhost:8081/actuator/health

# Consumer checking payments API connectivity
curl http://localhost:8081/consumer/payment-health

# Consumer checking accounts API connectivity
curl http://localhost:8081/consumer/account-health
```

## Configuration

### Payments API Application (`payments-api/src/main/resources/application.yml`)

```yaml
server:
  port: 8080

spring:
  application:
    name: payments
  jmx:
    enabled: true

management:
  endpoints:
    web:
      exposure:
        include: "*"
  server:
    port: 9001

com.sun.management.jmxremote:
  port: 9001
  rmi.port: 9001
  authenticate: false
  ssl: false
```

### Accounts API Application (`accounts-api/src/main/resources/application.yml`)

```yaml
server:
  port: 8083

spring:
  application:
    name: accounts
  jmx:
    enabled: true

management:
  endpoints:
    web:
      exposure:
        include: "*"
  server:
    port: 9003

com.sun.management.jmxremote:
  port: 9003
  rmi.port: 9003
  authenticate: false
  ssl: false
```

### Consumer Application (`consumer-app/src/main/resources/application.yml`)

```yaml
server:
  port: 8081

spring:
  application:
    name: consumer-app
  jmx:
    enabled: true

payment:
  service:
    url: http://localhost:8080

account:
  service:
    url: http://localhost:8083

management:
  endpoints:
    web:
      exposure:
        include: "*"
  server:
    port: 9002

com.sun.management.jmxremote:
  port: 9002
  rmi.port: 9002
  authenticate: false
  ssl: false
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
mvn test -pl payments-api
mvn test -pl consumer-app
mvn test -pl consumer-client
```

### Building Individual Modules

```bash
# Build only payments API
mvn clean package -pl payments-api

# Build only consumer  
mvn clean package -pl consumer-app

# Build only consumer-client
mvn clean package -pl consumer-client
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
- Payments API: http://localhost:8080/actuator (Management Port: 9001)
- Accounts API: http://localhost:8083/actuator (Management Port: 9003)
- Consumer: http://localhost:8081/actuator (Management Port: 9002)

### JMX Monitoring

Each application runs on different JMX ports to avoid conflicts:
- **Payments API JMX Port**: 9001
- **Accounts API JMX Port**: 9003
- **Consumer JMX Port**: 9002

You can connect to JMX using tools like JConsole or VisualVM:
```bash
# Connect to Payments API JMX
jconsole localhost:9001

# Connect to Accounts API JMX
jconsole localhost:9003

# Connect to Consumer JMX  
jconsole localhost:9002
```

## Future Enhancements

This project is designed as a foundation for implementing Resilience4j patterns:

- **Circuit Breaker** - Prevent cascading failures
- **Retry** - Automatic retry on transient failures  
- **Rate Limiter** - Control request rate
- **Time Limiter** - Timeout handling
- **Bulkhead** - Isolate critical resources

## Troubleshooting

### Common Issues

1. **Port Already in Use - JMX Conflict**
   ```bash
   # Error: Address already in use on port 9001/9002/9003
   # Check JMX ports
   lsof -i :9001
   lsof -i :9002
   lsof -i :9003
   
   # Kill processes if needed
   kill -9 <PID>
   ```
   
   The applications use different JMX ports to avoid conflicts:
   - Payments API: 9001
   - Accounts API: 9003
   - Consumer: 9002

2. **Port Already in Use - Application Ports**
   ```bash
   # Check what's using the application ports
   lsof -i :8080  # Payments API
   lsof -i :8081  # Consumer
   lsof -i :8083  # Accounts API
   ```

3. **Java Version Issues**
   ```bash
   # Verify Java version
   java -version
   echo $JAVA_HOME
   ```

4. **Maven Issues**
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