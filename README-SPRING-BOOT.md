# Inventory Management System - Spring Boot Microservices

This project contains Spring Boot microservices migrated from the Laravel backend.

## Architecture

The system consists of two microservices:

1. **auth-service-spring** (Port 8081) - Authentication and User Management
   - JWT-based authentication
   - User CRUD operations
   - Role-based access control (RBAC)
   - Permission management

2. **inventory-service-spring** (Port 8082) - Asset Management
   - Asset CRUD operations
   - Asset status lifecycle management
   - Supplier, Project, Location, and Asset Type management
   - Search and filtering capabilities

## Prerequisites

- **Java 17** or later
- **Maven 3.6+** or use the built-in IntelliJ Maven support
- **MySQL** (optional, H2 in-memory database is used by default)

## Quick Start

### Using IntelliJ IDEA

1. Open IntelliJ IDEA
2. File → Open → Select the service directory (`auth-service-spring` or `inventory-service-spring`)
3. IntelliJ will detect the Maven project and download dependencies
4. Run the main application class:
   - Auth Service: `com.inventory.auth.AuthServiceApplication`
   - Inventory Service: `com.inventory.asset.InventoryServiceApplication`

### Using Maven (Command Line)

```bash
# Build auth-service
cd auth-service-spring
mvn clean install

# Run auth-service
mvn spring-boot:run

# Build inventory-service
cd ../inventory-service-spring
mvn clean install

# Run inventory-service
mvn spring-boot:run
```

## API Documentation

Once running, access Swagger UI:

- Auth Service: http://localhost:8081/swagger-ui.html
- Inventory Service: http://localhost:8082/swagger-ui.html

## Default Credentials

The auth-service creates a default admin user on startup:

- **Email:** admin@inventory.com
- **Password:** password123

## Database Configuration

### Development (H2 In-Memory)
By default, both services use H2 in-memory database. Access H2 Console:
- Auth Service: http://localhost:8081/h2-console
- Inventory Service: http://localhost:8082/h2-console

### Production (MySQL)
To use MySQL, update `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/inventory_auth
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

## API Endpoints

### Auth Service (Port 8081)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/login | Authenticate user |
| POST | /api/auth/register | Register new user |
| GET | /api/auth/me | Get current user |
| POST | /api/auth/logout | Logout user |
| GET | /api/users | List all users |
| GET | /api/users/{id} | Get user by ID |
| POST | /api/users | Create user |
| PUT | /api/users/{id} | Update user |
| DELETE | /api/users/{id} | Delete user |
| GET | /api/users/roles | List all roles |

### Inventory Service (Port 8082)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/dashboard | Get dashboard statistics |
| GET | /api/assets | List assets (with filters) |
| GET | /api/assets/{id} | Get asset by ID |
| POST | /api/assets | Create asset |
| PUT | /api/assets/{id} | Update asset |
| DELETE | /api/assets/{id} | Delete asset |
| POST | /api/assets/{id}/change-status | Change asset status |
| GET | /api/assets/{id}/allowed-transitions | Get allowed status transitions |
| GET | /api/suppliers | List all suppliers |
| GET | /api/suppliers/active | List active suppliers |
| POST | /api/suppliers | Create supplier |
| PUT | /api/suppliers/{id} | Update supplier |
| DELETE | /api/suppliers/{id} | Delete supplier |
| GET | /api/projects | List all projects |
| POST | /api/projects | Create project |
| PUT | /api/projects/{id} | Update project |
| DELETE | /api/projects/{id} | Delete project |
| GET | /api/locations | List all locations |
| GET | /api/locations/active | List active locations |
| POST | /api/locations | Create location |
| PUT | /api/locations/{id} | Update location |
| DELETE | /api/locations/{id} | Delete location |
| GET | /api/asset-types | List all asset types |
| POST | /api/asset-types | Create asset type |
| PUT | /api/asset-types/{id} | Update asset type |
| DELETE | /api/asset-types/{id} | Delete asset type |

## Authentication

All endpoints (except auth endpoints) require a JWT token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

### Example: Login and Use Token

```bash
# Login
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@inventory.com","password":"password123"}'

# Use the returned token for subsequent requests
curl http://localhost:8082/api/assets \
  -H "Authorization: Bearer <token>"
```

## Asset Status Lifecycle

Valid status transitions:

```
new → in_service, disposed
in_service → maintenance, repair, decommissioned
maintenance → in_service, repair, decommissioned
repair → in_service, maintenance, decommissioned
decommissioned → disposed, in_service
disposed → (no transitions - final state)
```

## Running Tests

```bash
# Auth Service Tests
cd auth-service-spring
mvn test

# Inventory Service Tests
cd ../inventory-service-spring
mvn test
```

## Project Structure

```
auth-service-spring/
├── src/main/java/com/inventory/auth/
│   ├── config/          # Configuration classes
│   ├── controller/      # REST controllers
│   ├── dto/             # Data transfer objects
│   ├── entity/          # JPA entities
│   ├── exception/       # Exception handlers
│   ├── repository/      # JPA repositories
│   ├── security/        # Security components
│   └── service/         # Business logic
└── src/test/            # Test classes

inventory-service-spring/
├── src/main/java/com/inventory/asset/
│   ├── config/          # Configuration classes
│   ├── controller/      # REST controllers
│   ├── dto/             # Data transfer objects
│   ├── entity/          # JPA entities
│   ├── exception/       # Exception handlers
│   ├── repository/      # JPA repositories
│   ├── security/        # Security components
│   └── service/         # Business logic
└── src/test/            # Test classes
```

## Migration from Laravel

This Spring Boot implementation mirrors the Laravel backend with:

| Laravel | Spring Boot |
|---------|-------------|
| Eloquent Models | JPA Entities |
| Controllers | @RestController |
| Middleware | Spring Security Filters |
| Request Validation | @Valid + DTOs |
| Blade Templates | (API only, no views) |
| Laravel Passport/Sanctum | JWT (jjwt library) |
| Spatie Permission | Custom Role/Permission entities |

## License

MIT License

