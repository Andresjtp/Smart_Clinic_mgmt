# Smart Clinic Management System - Docker Setup

This directory contains Docker configuration files for containerizing and deploying the Smart Clinic Management System.

## Prerequisites

- Docker (version 20.10 or later)
- Docker Compose (version 2.0 or later)
- At least 4GB of available RAM
- At least 10GB of available disk space

## Quick Start

### Development Environment

1. **Clone the repository and navigate to the project directory:**
   ```bash
   git clone <repository-url>
   cd Smart_Clinic_mgmt
   ```

2. **Build and start the development environment:**
   ```bash
   docker-compose -f docker-compose.dev.yml up --build
   ```

3. **Access the application:**
   - Application: http://localhost:8080
   - API Documentation: http://localhost:8080/swagger-ui.html (when implemented)
   - MySQL: localhost:3306
   - MongoDB: localhost:27017

### Production Environment

1. **Build and start the production environment:**
   ```bash
   docker-compose up --build -d
   ```

2. **Access the application:**
   - Application: http://localhost (via Nginx)
   - Direct API: http://localhost:8080
   - MySQL: localhost:3307
   - MongoDB: localhost:27018
   - Redis: localhost:6379

## Architecture

The containerized application consists of:

- **smart-clinic-app**: Spring Boot application container
- **mysql**: MySQL 8.0 database for relational data
- **mongodb**: MongoDB 7.0 for document storage
- **redis**: Redis for caching (production only)
- **nginx**: Reverse proxy and load balancer (production only)

## Environment Variables

### Application Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `docker` |
| `MYSQL_HOST` | MySQL hostname | `mysql` |
| `MYSQL_PORT` | MySQL port | `3306` |
| `MYSQL_DATABASE` | MySQL database name | `smart_clinic_db` |
| `MYSQL_USER` | MySQL username | `clinic_user` |
| `MYSQL_PASSWORD` | MySQL password | `clinic_password` |
| `MONGO_HOST` | MongoDB hostname | `mongodb` |
| `MONGO_PORT` | MongoDB port | `27017` |
| `MONGO_DATABASE` | MongoDB database name | `smart_clinic_mongo_db` |
| `JWT_SECRET` | JWT signing secret | Generated |
| `JWT_EXPIRATION` | JWT token expiration (seconds) | `86400` |
| `LOG_LEVEL` | Application log level | `INFO` |

### Database Environment Variables

#### MySQL
| Variable | Description | Default |
|----------|-------------|---------|
| `MYSQL_ROOT_PASSWORD` | MySQL root password | `root_password` |
| `MYSQL_DATABASE` | Database name | `smart_clinic_db` |
| `MYSQL_USER` | Database user | `clinic_user` |
| `MYSQL_PASSWORD` | Database password | `clinic_password` |

#### MongoDB
| Variable | Description | Default |
|----------|-------------|---------|
| `MONGO_INITDB_ROOT_USERNAME` | MongoDB admin username | `mongo_admin` |
| `MONGO_INITDB_ROOT_PASSWORD` | MongoDB admin password | `mongo_password` |
| `MONGO_INITDB_DATABASE` | Initial database | `smart_clinic_mongo_db` |

## Volumes

The following volumes are created for data persistence:

- `mysql_data`: MySQL database files
- `mongodb_data`: MongoDB database files
- `redis_data`: Redis data files
- `app_logs`: Application log files
- `nginx_logs`: Nginx access and error logs

## Networking

All services communicate through a custom bridge network (`smart-clinic-network`) with subnet `172.20.0.0/16`.

## Health Checks

All services include health checks:

- **MySQL**: `mysqladmin ping`
- **MongoDB**: `mongosh ping`
- **Redis**: `redis-cli ping`
- **Application**: HTTP health endpoint at `/actuator/health`
- **Nginx**: Configuration test

## Commands

### Build and Start Services
```bash
# Development
docker-compose -f docker-compose.dev.yml up --build

# Production
docker-compose up --build -d
```

### Stop Services
```bash
# Development
docker-compose -f docker-compose.dev.yml down

# Production
docker-compose down
```

### View Logs
```bash
# All services
docker-compose logs

# Specific service
docker-compose logs smart-clinic-app

# Follow logs
docker-compose logs -f smart-clinic-app
```

### Execute Commands in Containers
```bash
# Access application container
docker-compose exec smart-clinic-app bash

# Access MySQL
docker-compose exec mysql mysql -u clinic_user -p smart_clinic_db

# Access MongoDB
docker-compose exec mongodb mongosh smart_clinic_mongo_db
```

### Database Operations
```bash
# Backup MySQL database
docker-compose exec mysql mysqldump -u clinic_user -p smart_clinic_db > backup.sql

# Restore MySQL database
docker-compose exec -i mysql mysql -u clinic_user -p smart_clinic_db < backup.sql

# Backup MongoDB
docker-compose exec mongodb mongodump --db smart_clinic_mongo_db --out /data/backup

# Access Redis CLI
docker-compose exec redis redis-cli
```

## Troubleshooting

### Common Issues

1. **Port Conflicts**: Ensure ports 3306, 3307, 27017, 27018, 6379, 8080, and 80 are available.

2. **Memory Issues**: Increase Docker memory allocation if containers fail to start.

3. **Database Connection Issues**: Wait for database health checks to pass before the application starts.

4. **Permission Issues**: Ensure Docker has proper permissions to create volumes and bind ports.

### Debugging

1. **Check container status:**
   ```bash
   docker-compose ps
   ```

2. **View container logs:**
   ```bash
   docker-compose logs [service-name]
   ```

3. **Check health status:**
   ```bash
   docker-compose exec smart-clinic-app curl http://localhost:8080/actuator/health
   ```

4. **Access container shell:**
   ```bash
   docker-compose exec smart-clinic-app bash
   ```

## Security Considerations

- Change default passwords in production
- Use environment files (.env) for sensitive data
- Enable HTTPS for production deployments
- Restrict network access using firewall rules
- Regularly update base images for security patches

## Performance Tuning

- Adjust JVM heap size using `JAVA_OPTS`
- Configure database connection pools
- Monitor resource usage with `docker stats`
- Use volume mounts for better I/O performance

## Monitoring

The setup includes basic monitoring endpoints:

- Application health: `/actuator/health`
- Application metrics: `/actuator/metrics`
- Nginx status: `/nginx_status` (restricted access)

For production, consider integrating with monitoring tools like Prometheus, Grafana, or ELK stack.