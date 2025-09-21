# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
EduKit is an AI-powered student record management system for Korean teachers (생활기록부 작성 및 관리 서비스). The project uses a multi-module Spring Boot architecture with Java 21.

## Module Architecture

The project follows a layered architecture with clear separation of concerns:

### 🎯 Module Dependencies
```
edukit-api (REST API Layer)
├── edukit-core (Business Logic)
│   └── edukit-common (Shared Utilities)
└── edukit-external (External Integrations)
    ├── edukit-core
    └── edukit-common
```

### 📦 Module Descriptions

- **edukit-api**: REST API controllers, security config, main application entry point
  - Contains: Controllers (v1/v2), security, validation, Swagger/OpenAPI
  - Technologies: Spring Security, Spring Boot Actuator, Swagger, Prometheus metrics

- **edukit-core**: Core business logic and domain services
  - Contains: JPA entities, repositories, business services, domain logic
  - Technologies: Spring Data JPA, QueryDSL, Redis, JWT, Apache POI, Micrometer

- **edukit-external**: External service integrations
  - Contains: AI services (OpenAI), AWS services (S3, SES, SQS), Slack integration
  - Technologies: Spring AI, AWS SDK, WebFlux, Resilience4j, Thymeleaf

- **edukit-common**: Shared utilities and constants

## Build System & Common Commands

### 🔧 Build Commands
```bash
# Build entire project
./gradlew build

# Build specific module
./gradlew :edukit-api:build

# Run tests
./gradlew test

# Run specific module tests
./gradlew :edukit-core:test

# Clean build
./gradlew clean build

# Generate bootJar (executable)
./gradlew bootJar

# Run application locally
./gradlew bootRun
```

### 🐳 Docker Commands
```bash
# Development environment
docker-compose -f docker-compose.dev.yml up

# Production environment
docker-compose -f docker-compose.prod.yml up
```

## Key Technologies & Integrations

### 🗄️ Database & Storage
- **MySQL**: Primary database with Flyway migrations
- **Redis**: Caching and session management
- **AWS S3**: File storage

### 🤖 AI & External Services
- **Spring AI**: OpenAI integration for student record generation
- **AWS SES**: Email notifications
- **AWS SQS**: Message queuing
- **Slack**: Integration for notifications

### 📊 Monitoring & Observability
- **JMX + Prometheus**: Application metrics collection
- **Micrometer**: Metrics instrumentation
- **Spring Boot Actuator**: Health checks and monitoring endpoints
- **JMX Config**: Custom Tomcat and JVM metrics (see `jmx-config.yml`)

## Environment Configuration

### 🌍 Profiles
- `local`: Local development
- `dev`: Development environment (AWS RDS/Redis)
- `prod`: Production environment

### 📁 Configuration Files
- `application-{profile}.yml`: Profile-specific configurations
- Environment variables managed via `.env` files
- JMX monitoring configured in `jmx-config.yml`

## Domain Structure

### 📚 Core Domains
- **Student**: Student management and information
- **StudentRecord**: AI-powered student record generation and management
- **Member**: User/teacher management and authentication
- **Auth**: JWT-based authentication (v1 and v2 APIs)
- **Notice**: Announcements and notifications
- **Admin**: Administrative functions

### 🏗️ Package Structure Pattern
```
com.edukit.{domain}
├── controller/     # REST endpoints
├── service/        # Business logic
├── repository/     # Data access
├── entity/         # JPA entities
├── dto/           # Data transfer objects
└── exception/     # Domain-specific exceptions
```

## Testing Strategy

### 🧪 Test Structure
- Unit tests: `src/test/java`
- Integration tests: Available in all modules
- Test profile: `application-test.yml`

## Development Guidelines

### 🔐 Security Considerations
- JWT tokens for authentication
- AWS credentials managed via environment variables
- Database credentials in profile-specific configs
- Never commit `.env` files or sensitive data

### 🎯 Jira Integration
- Branch naming: `feat/EDMT-{ticket-number}`
- Commit format: `[feat/refac/fix] description`
- PR template includes Jira ticket references (Korean descriptions preferred)

### 🚀 Deployment
- **Blue-Green Deployment**: Configured in Docker Compose
- **Monitoring Ports**: 8081 (blue), 8082 (green) for JMX metrics
- **Health Checks**: Available via Spring Boot Actuator
- **Log Management**: Structured logging with Logback + Logstash encoder

## Common Development Patterns

### 🔄 Metrics Collection
- Custom metrics using `@Aspect` and Micrometer
- Student record generation tracking
- Performance monitoring for AI operations

### 🌐 API Versioning
- v1 and v2 controller patterns
- Backward compatibility considerations

### 📝 File Processing
- Excel file handling with Apache POI
- File upload/download via AWS S3

This architecture supports scalable AI-powered educational services with robust monitoring, security, and Korean localization support.