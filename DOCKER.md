# Docker Setup for Resilience4j Example

This document provides instructions for running the Resilience4j Example project using Docker and Docker Compose.

## Prerequisites

- Docker Engine 20.10+
- Docker Compose v2.0+
- At least 4GB of available RAM
- Ports 8080, 8081, 8083, 9001, 9002, 9003 available on your host machine

## Services Overview

The Docker Compose setup includes 4 services:

| Service | Container Name | Ports | Description |
|---------|---------------|-------|-------------|
| **payments-api** | `payments-api` | 8080, 9001 | Payment management service |
| **accounts-api** | `accounts-api` | 8083, 9003 | Account management service |
| **consumer-app** | `consumer-app` | 8081, 9002 | API aggregation service |
| **consumer-client** | `consumer-client` | - | Testing and monitoring client |

## Quick Start

### 1. Build and Start All Services

```bash
# Start all services in detached mode
docker-compose up -d

# View logs from all services
docker-compose logs -f

# View logs from specific service
docker-compose logs -f payments-api
```

### 2. Verify Services

Check that all services are healthy:

```bash
# Check container status
docker-compose ps

# Test service endpoints
curl http://localhost:8080/payments/health    # Payments API
curl http://localhost:8083/accounts/health    # Accounts API  
curl http://localhost:8081/consumer/health    # Consumer App
```

## Service Dependencies

The services start in the following order due to health check dependencies:

1. **payments-api** and **accounts-api** (parallel)
2. **consumer-app** (waits for both APIs to be healthy)
3. **consumer-client** (waits for consumer-app to be healthy)

## Individual Service Management

### Start Specific Services

```bash
# Start only core APIs
docker-compose up -d payments-api accounts-api

# Start APIs and consumer (without client)
docker-compose up -d payments-api accounts-api consumer-app

# Start everything
docker-compose up -d
```

### Scale Services

```bash
# Run multiple instances of an API (behind a load balancer)
docker-compose up -d --scale payments-api=2
```

## Environment Configuration

Services use the `docker` Spring profile which configures:

- **Network binding**: Services bind to `0.0.0.0` instead of `localhost`
- **Service discovery**: Uses Docker service names for inter-service communication
- **Health checks**: Properly configured for container health monitoring

### Key Environment Variables

| Service | Variable | Default | Description |
|---------|----------|---------|-------------|
| consumer-app | `PAYMENT_SERVICE_URL` | `http://payments-api:8080` | Payments API endpoint |
| consumer-app | `ACCOUNT_SERVICE_URL` | `http://accounts-api:8083` | Accounts API endpoint |
| consumer-client | `CONSUMER_URL` | `http://consumer-app:8081/consumer` | Consumer service endpoint |
| consumer-client | `PAYMENT_URL` | `http://payments-api:8080/payments` | Direct payment API access |
| consumer-client | `ACCOUNT_URL` | `http://accounts-api:8083/accounts` | Direct account API access |

## API Access

### External Access (from host machine)

```bash
# Payments API
curl http://localhost:8080/payments/payments
curl http://localhost:8080/actuator/health

# Accounts API  
curl http://localhost:8083/accounts/accounts
curl http://localhost:8083/actuator/health

# Consumer App
curl http://localhost:8081/consumer/payments
curl http://localhost:8081/consumer/accounts
curl http://localhost:8081/actuator/health
```

### Container-to-Container Communication

Services communicate using Docker service names:

- `payments-api:8080` - Payments API
- `accounts-api:8083` - Accounts API
- `consumer-app:8081` - Consumer service

## Monitoring and Logging

### Container Logs

```bash
# Real-time logs from all services
docker-compose logs -f

# Logs from specific service
docker-compose logs -f consumer-client

# Last 100 lines from payments API
docker-compose logs --tail=100 payments-api
```

### Health Checks

All services include health checks that Docker monitors:

```bash
# View health status
docker-compose ps

# Manual health check
docker-compose exec payments-api curl -f http://localhost:8080/payments/health
```

### JMX Monitoring

JMX ports are exposed for monitoring:

- **Payments API**: `localhost:9001`
- **Consumer App**: `localhost:9002`  
- **Accounts API**: `localhost:9003`

```bash
# Connect with JConsole
jconsole localhost:9001  # Payments API
jconsole localhost:9002  # Consumer App
jconsole localhost:9003  # Accounts API
```

## Development Workflow

### Rebuilding Services

After code changes, rebuild and restart services:

```bash
# Rebuild specific service
docker-compose build payments-api
docker-compose up -d payments-api

# Rebuild all services
docker-compose build
docker-compose up -d
```

### Debugging

Access container shells for debugging:

```bash
# Access payments API container
docker-compose exec payments-api /bin/bash

# View container environment
docker-compose exec consumer-app printenv
```

## Stopping Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# Stop and remove images
docker-compose down --rmi all
```

## Troubleshooting

### Common Issues

1. **Port Conflicts**
   ```bash
   # Check what's using ports
   lsof -i :8080
   lsof -i :8081
   lsof -i :8083
   ```

2. **Memory Issues**
   ```bash
   # Check Docker memory usage
   docker stats
   
   # Increase Docker desktop memory allocation to 4GB+
   ```

3. **Service Won't Start**
   ```bash
   # Check service logs
   docker-compose logs service-name
   
   # Restart specific service
   docker-compose restart service-name
   ```

4. **Network Issues**
   ```bash
   # Verify network connectivity
   docker network ls
   docker network inspect resilience4j-example_resilience4j-network
   ```

### Clean Reset

```bash
# Complete cleanup and restart
docker-compose down -v --rmi all
docker system prune -f
docker-compose build --no-cache
docker-compose up -d
```

## Production Considerations

For production deployment, consider:

- Using specific image tags instead of `latest`
- Implementing proper secrets management
- Configuring resource limits and requests
- Setting up proper logging aggregation
- Implementing external monitoring
- Using external databases instead of in-memory storage

## Additional Commands

```bash
# View resource usage
docker-compose top

# Export/import service configuration
docker-compose config > docker-compose.resolved.yml

# Update services (pull new images)
docker-compose pull
docker-compose up -d
```