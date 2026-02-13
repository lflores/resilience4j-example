# Consumer Client

A comprehensive HTTP client for testing and monitoring the consumer service endpoints. This client provides continuous testing, performance monitoring, and reliability validation for the consumer service.

## Purpose

The Consumer Client acts as a dedicated testing client that:
- **Tests** the consumer service endpoints continuously
- **Monitors** performance and availability 
- **Validates** response data correctness
- **Reports** real-time metrics and health status

## Features

### 🔄 Continuous Testing
- **Automated polling**: Tests consumer endpoints every 5 seconds
- **Multiple test scenarios**: Health checks, data retrieval, payment creation
- **Graceful shutdown**: Ctrl+C for clean exit with final metrics

### 📊 Comprehensive Metrics
- **Response times**: Average, minimum, maximum
- **Success rates**: Percentage of successful requests
- **Error tracking**: Failed requests, timeouts, connection issues
- **Throughput**: Requests per minute
- **Real-time reporting**: Metrics printed every 30 seconds

### 🧪 Test Scenarios

1. **GET /consumer/payments** - Retrieve all payments
   - Tests data correctness and response structure
   - Validates JSON response format
   - Counts returned payments

2. **GET /consumer/health** - Consumer health check
   - Verifies service availability
   - Quick response time test

3. **GET /consumer/payment-health** - Payments API connectivity check
   - Tests consumer → payments API communication
   - Validates service integration

4. **POST /consumer/payments** - Create payment (every 10th cycle)
   - Tests write operations
   - Validates request/response flow
   - Creates test payments with timestamp

## Prerequisites

1. **Consumer service running** on `http://localhost:8081`
2. **Payments API service running** on `http://localhost:8080`
3. **Java 21** installed
4. **Maven** for building and running

## Quick Start

### 1. Build the Consumer Client

```bash
cd tests/consumer-client
mvn clean compile
```

### 2. Start Required Services

```bash
# Terminal 1 - Start Payments API
cd ../../payments-api
mvn spring-boot:run

# Terminal 2 - Start Consumer
cd ../../consumer-app
mvn spring-boot:run
```

### 3. Run the Consumer Client

```bash
cd tests/consumer-client
mvn exec:java
```

### 4. Monitor Output

The client will display:
- Real-time test results for each API call
- Periodic metrics reports (every 30 seconds)
- Final summary when stopped

### 5. Stop Client

Press `Ctrl+C` to gracefully stop and see final metrics.

## Sample Output

```
🚀 Starting Consumer Client
📡 Target: http://localhost:8081/consumer
⏱️  Polling interval: 5 seconds
🛑 Press Ctrl+C to stop

🔄 [14:30:15] Running test cycle...
✅ Get Payments - 200 (245ms) - 1247 bytes
✅ Health Check - 200 (12ms) - 25 bytes
✅ Payment Health - 200 (156ms) - 25 bytes
📊 Found 4 payments

📈 === METRICS REPORT [14:30:45] ===
⏱️  Uptime: 30s
📊 Total Requests: 9
✅ Successful: 9
❌ Failed: 0
⏰ Timeouts: 0
📈 Success Rate: 100.00%
⚡ Response Times: avg=134ms, min=12ms, max=245ms
🔄 Requests/min: 18.0
=====================================
```

## Project Structure

```
tests/consumer-client/
├── pom.xml                                    # Maven dependencies
├── README.md                                  # This documentation
└── src/main/java/com/perficient/resilience4j/client/
    ├── ConsumerClient.java                    # Main client application
    ├── model/Payment.java                     # Payment model
    ├── config/ClientConfig.java               # Configuration constants
    └── metrics/ClientMetrics.java             # Metrics collection
```

## Configuration

### Default Settings
- **Polling interval**: 5 seconds
- **Metrics reporting**: 30 seconds 
- **Payment creation**: Every 10th cycle
- **Connect timeout**: 10 seconds
- **Read timeout**: 30 seconds
- **Write timeout**: 30 seconds

### Customization

Modify settings in `ClientConfig.java`:

```java
public static final int DEFAULT_POLLING_INTERVAL = 5; // seconds
public static final boolean ENABLE_CREATE_PAYMENT_TEST = true;
public static final String DEFAULT_CONSUMER_URL = "http://localhost:8081/consumer";
```

## Testing Scenarios

### 1. Response Data Correctness ✅
- Validates JSON structure
- Checks required fields (id, amount, etc.)
- Verifies payment count
- Detects malformed responses

### 2. Performance Testing ⚡
- Measures response times for each request
- Tracks min/max/average response times  
- Calculates requests per minute
- Identifies performance degradation

### 3. Error Handling & Resilience 🔄
- Detects HTTP error codes (4xx, 5xx)
- Handles connection timeouts
- Tracks failure rates
- Logs error details

### 4. Service Availability Monitoring 📡
- Continuous health checks
- Payments API connectivity verification
- Service availability percentage
- Uptime tracking

## Advanced Usage

### Building Standalone JAR

```bash
mvn clean package
java -jar target/consumer-client-1.0.0-SNAPSHOT.jar
```

### Integration with CI/CD

Use for:
- **Smoke tests**: Run for 1-2 minutes after deployment
- **Load testing**: Extended runs to test system under load  
- **Monitoring**: Continuous execution for health checks

Example script:
```bash
#!/bin/bash
# Run client for 5 minutes then stop
timeout 300 mvn exec:java
echo "5-minute test completed"
```

### Custom Test Scenarios

Enable/disable specific tests in `ClientConfig.java`:
```java
public static final boolean ENABLE_GET_PAYMENTS_TEST = true;
public static final boolean ENABLE_HEALTH_CHECK_TEST = true;
public static final boolean ENABLE_PAYMENT_HEALTH_TEST = true;
public static final boolean ENABLE_CREATE_PAYMENT_TEST = true;
public static final boolean ENABLE_RESPONSE_VALIDATION = true;
```

## Metrics Interpretation

### Success Rate
- **> 99%**: Excellent service reliability
- **95-99%**: Good performance 
- **< 95%**: Investigation required

### Response Times  
- **< 100ms**: Fast response
- **100-500ms**: Acceptable performance
- **> 500ms**: Slow response (investigate)

### Error Types
- **4xx errors**: Client/request issues
- **5xx errors**: Server-side problems
- **Timeouts**: Network/performance issues

## Troubleshooting

### Common Issues

1. **Connection refused**
   ```
   ❌ Get Payments - ERROR: Connection refused
   ```
   **Solution**: Ensure consumer service is running on port 8081

2. **Timeout errors**
   ```
   ⏰ Get Payments - TIMEOUT (10000ms)
   ```
   **Solution**: Check network connectivity and service performance

3. **JSON parsing errors**  
   ```
   ⚠️ Warning: Failed to parse payments response
   ```
   **Solution**: Verify API response format

### Verification Steps

1. **Check services are running**:
   ```bash
   curl http://localhost:8081/consumer/health
   curl http://localhost:8080/payments/health  
   ```

2. **Test manual API calls**:
   ```bash
   curl http://localhost:8081/consumer/payments
   ```

3. **Check for port conflicts**:
   ```bash
   lsof -i :8080
   lsof -i :8081
   ```

## Use Cases

### Development Testing
- Validate API changes during development
- Test consumer service integration
- Monitor performance during development

### Performance Testing  
- Continuous load testing
- Response time monitoring
- Throughput analysis

### System Monitoring
- Health check automation
- Service availability monitoring
- Integration testing

### Reliability Testing
- Test service resilience
- Validate error handling
- Monitor recovery patterns

## Dependencies

- **OkHttp 4.12.0**: HTTP client for reliable API calls
- **Jackson 2.16.1**: JSON processing and validation
- **JUnit 5.9.2**: Testing framework (for future unit tests)
- **Java 21**: Modern Java features and performance

## Future Enhancements

This client can be extended for:
- **Load testing**: Multiple concurrent threads
- **Resilience testing**: Simulate failures and test recovery
- **Performance benchmarking**: Compare different configurations  
- **Automated reporting**: Export metrics to monitoring systems
- **Circuit breaker testing**: Test resilience patterns