# Consumer API Response Structure

## Overview
The consumer-app now returns consistent JSON responses following the IResponse pattern with proper error handling and circuit breaker integration.

## Response Structure

### Success Response Example
```json
{
  "data": [
    {
      "id": "1",
      "description": "Coffee purchase",
      "amount": 4.50,
      "currency": "USD",
      "status": "COMPLETED",
      "created_at": "2026-02-12T21:30:00"
    }
  ],
  "errors": []
}
```

### Error Response Example (Circuit Breaker)
```json
{
  "data": [],
  "errors": [
    {
      "message": "Fallback payment - Service temporarily unavailable",
      "code": "CIRCUIT_BREAKER_OPEN",
      "timestamp": "2026-02-12T21:45:30"
    }
  ]
}
```

### REST Endpoints

#### GET /consumer/payments
- **Response Type**: `GetPaymentsResponse`
- **Success Status**: `200 OK`
- **Error Status**: `503 Service Unavailable` (when circuit breaker is open)
- **Content-Type**: `application/json`

#### POST /consumer/payments
- **Request Body**: `Payment` object
- **Response Type**: `CreatePaymentResponse`
- **Success Status**: `201 Created`
- **Error Status**: `503 Service Unavailable` (when circuit breaker is open)
- **Content-Type**: `application/json`

## JSON Configuration Features

### Applied Settings:
- **Snake Case Naming**: Field names use `snake_case` convention
- **Non-null Inclusion**: Only non-null values are serialized
- **Date Format**: ISO 8601 format (`yyyy-MM-dd'T'HH:mm:ss`)
- **Pretty Printing**: JSON responses are indented for readability
- **Global Exception Handling**: Consistent error responses across all endpoints

### Circuit Breaker Integration:
- **Fallback Responses**: Automatically return structured error responses
- **Status Codes**: Appropriate HTTP status codes based on error presence
- **Error Details**: Timestamp and error codes for better debugging
- **Graceful Degradation**: Service continues to respond even when producer is down

## Testing Commands
```bash
# Get all payments
curl -X GET http://localhost:8081/consumer/payments \
  -H "Accept: application/json"

# Create a payment
curl -X POST http://localhost:8081/consumer/payments \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "description": "Test payment",
    "amount": 100.00,
    "currency": "USD"
  }'
```