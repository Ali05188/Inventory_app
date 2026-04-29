# Enterprise Asset & Inventory Management System

## Spring Boot Microservices Architecture

A production-ready enterprise asset management system built with Spring Boot microservices architecture.

## 🏗 Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        API Gateway (8080)                        │
│                    Spring Cloud Gateway                          │
└─────────────────────────────────────────────────────────────────┘
                              │
          ┌───────────────────┼───────────────────┐
          │                   │                   │
          ▼                   ▼                   ▼
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│  Auth Service   │  │Inventory Service│  │Analytics Service│
│     (8081)      │  │     (8082)      │  │     (8083)      │
│  JWT + RBAC     │  │  Asset CRUD     │  │    Reports      │
└─────────────────┘  └─────────────────┘  └─────────────────┘
          │                   │                   │
          └───────────────────┼───────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Discovery Server (8761)                       │
│                      Netflix Eureka                              │
└─────────────────────────────────────────────────────────────────┘
```

## 🛠 Technology Stack

| Component | Technology |
|-----------|------------|
| **Framework** | Spring Boot 3.2.x |
| **Language** | Java 17 |
| **Service Discovery** | Netflix Eureka |
| **API Gateway** | Spring Cloud Gateway |
| **Security** | Spring Security + JWT |
| **Database** | MySQL 8.0 |
| **ORM** | Spring Data JPA / Hibernate |
| **Build Tool** | Maven |
| **Containerization** | Docker + Docker Compose |

## 📦 Microservices

### 1. Discovery Server (Port: 8761)
- Service registration and discovery
- Health monitoring
- Load balancing support

### 2. API Gateway (Port: 8080)
- Single entry point for all requests
- Request routing
- Load balancing
- Cross-cutting concerns

### 3. Auth Service (Port: 8081)
- User authentication (JWT)
- User registration
- Role-based access control (RBAC)
- Permission management

### 4. Inventory Service (Port: 8082)
- Asset CRUD operations
- Supplier management
- Project management
- Location management
- Asset lifecycle tracking

### 5. Analytics Service (Port: 8083)
- Dashboard KPIs
- Asset reports
- Export functionality

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0
- Docker (optional)

### Running Locally

1. **Start MySQL**
```bash
# Create databases
mysql -u root -p
CREATE DATABASE inventory_auth;
CREATE DATABASE inventory_assets;
```

2. **Build all services**
```bash
cd spring-backend
mvn clean install
```

3. **Start services in order**
```bash
# 1. Discovery Server
cd discovery-server && mvn spring-boot:run

# 2. Config Server (new terminal)
cd config-server && mvn spring-boot:run

# 3. Auth Service (new terminal)
cd auth-service && mvn spring-boot:run

# 4. Inventory Service (new terminal)
cd inventory-service && mvn spring-boot:run

# 5. Analytics Service (new terminal)
cd analytics-service && mvn spring-boot:run

# 6. API Gateway (new terminal)
cd api-gateway && mvn spring-boot:run
```

### Running with Docker

```bash
cd spring-backend
docker-compose up -d
```

## 📡 API Endpoints

### Auth Service
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | User login |
| GET | `/api/auth/validate` | Validate JWT token |

### Inventory Service
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/assets` | List all assets |
| GET | `/api/assets/{id}` | Get asset by ID |
| POST | `/api/assets` | Create new asset |
| PUT | `/api/assets/{id}` | Update asset |
| DELETE | `/api/assets/{id}` | Delete asset |
| PATCH | `/api/assets/{id}/status` | Change asset status |

## 🔐 Security

- JWT-based authentication
- Role-based access control (RBAC)
- Roles: `SUPER_ADMIN`, `ASSET_MANAGER`, `AUDITOR`, `FINANCE`, `VIEWER`
- Password encryption with BCrypt

## 📊 Eureka Dashboard

Access the service registry dashboard:
```
http://localhost:8761
Username: eureka
Password: eureka123
```

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run specific service tests
cd auth-service && mvn test
```

## 📝 License

This project is developed as part of a PFE (Projet de Fin d'Études) internship.

---

**Author:** Ali Mellouk  
**Year:** 2026

