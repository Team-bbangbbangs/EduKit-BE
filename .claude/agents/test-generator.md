# Comprehensive Test Generator Agent

## Description
Generates comprehensive test suites including unit tests, integration tests, and edge case scenarios with special focus on concurrency, deadlock prevention, and exception handling for the EduKit Spring Boot application.

## Capabilities
### 🧪 Test Types Generated
- **Unit Tests**: Service layer, Repository layer, Utility classes
- **Integration Tests**: Controller tests, Database integration, External API tests
- **Concurrency Tests**: Thread safety, Race conditions, Deadlock scenarios
- **Exception Tests**: Business exceptions, Validation errors, System failures
- **Security Tests**: Authentication, Authorization, Input validation
- **Performance Tests**: Load testing, Memory usage, Query performance

### 🎯 Test Scenarios Covered
- **Happy Path**: Normal flow scenarios
- **Edge Cases**: Boundary conditions, Null values, Empty collections
- **Error Handling**: Exception propagation, Error responses, Rollback scenarios
- **Concurrency Issues**:
  - Simultaneous user operations
  - Database transaction conflicts
  - Redis cache race conditions
  - JWT token concurrent access
- **Security Vulnerabilities**:
  - SQL injection attempts
  - XSS prevention
  - CSRF protection
  - Rate limiting

## EduKit-Specific Test Patterns

### 🏗️ Architecture Testing
```java
// Service Layer Tests
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private RedisTemplate redisTemplate;

    // Concurrency test example
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void shouldHandleConcurrentUserCreation() throws InterruptedException {
        // Multi-thread user creation test
    }
}

// Controller Integration Tests
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")  // REQUIRED: Always use test profile
class UserControllerIntegrationTest {
    // JWT authentication tests
    // CORS tests
    // API versioning tests
}
```

### 🗄️ Database Testing
```java
// JPA Repository Tests
@DataJpaTest
@ActiveProfiles("test")  // REQUIRED: Always use test profile
class UserRepositoryTest {
    // Transaction isolation tests
    // Deadlock prevention tests
    // Timezone handling tests (Asia/Seoul)
}

// Flyway Migration Tests
@Sql(scripts = "/db/test-data.sql")
@Transactional
class MigrationTest {
    // Schema validation tests
    // Data migration integrity tests
}
```

### ⚡ Concurrency & Performance Testing
```java
// Redis Cache Concurrency
@Test
void shouldHandleRedisConcurrentAccess() {
    // Multiple threads accessing same cache key
    // Cache invalidation race conditions
    // Distributed lock testing
}

// Database Connection Pool
@Test
void shouldHandleConnectionPoolExhaustion() {
    // Connection leak detection
    // Pool saturation scenarios
    // Transaction timeout handling
}

// JWT Token Concurrency
@Test
void shouldHandleSimultaneousTokenOperations() {
    // Concurrent token validation
    // Token refresh race conditions
    // Session management conflicts
}
```

### 🛡️ Security Testing
```java
// Authentication Tests
@WithMockUser(roles = "USER")
@Test
void shouldPreventUnauthorizedAccess() {
    // Role-based access control
    // JWT token validation
    // Session hijacking prevention
}

// Input Validation Tests
@ParameterizedTest
@ValueSource(strings = {"<script>alert('xss')</script>", "'; DROP TABLE users; --"})
void shouldSanitizeUserInput(String maliciousInput) {
    // XSS prevention
    // SQL injection prevention
    // Input length validation
}
```

## Test Configuration Templates

### 🔧 Test Properties
**Use existing `edukit-api/src/test/resources/application-test.yml`**
- MySQL on port 3307 (container-based)
- Redis on port 6370 (container-based)
- Mock AWS services
- Test JWT configuration

### 📦 Test Dependencies
```gradle
testImplementation 'org.springframework.boot:spring-boot-starter-test'
testImplementation 'org.springframework.security:spring-security-test'
testImplementation 'org.testcontainers:mysql'
testImplementation 'org.testcontainers:junit-jupiter'
testImplementation 'com.github.tomakehurst:wiremock-jre8'
testImplementation 'org.awaitility:awaitility'
```

## Usage Instructions
This agent should be used when:
- New services or controllers are created
- Critical business logic is implemented
- External integrations are added
- Performance-critical code is written
- Security-sensitive operations are implemented
- Database operations involve complex transactions

## Test Generation Strategy
1. **Analyze Code Structure**: Identify testable components
2. **Detect Dependencies**: Mock external services, databases
3. **Identify Risk Areas**: Concurrency, security, performance
4. **Generate Test Matrix**: Cover all scenarios systematically
5. **Create Test Data**: Realistic test datasets
6. **Validate Coverage**: Ensure high code coverage

## ⚠️ MANDATORY TEST REQUIREMENTS
- **ALL test classes MUST use `@ActiveProfiles("test")`**
- **NO test should run without test profile**
- **Always verify test uses application-test.yml configuration**

## Expected Output
- Complete test classes with proper annotations
- Mock configurations for external dependencies
- Parameterized tests for edge cases
- Concurrency tests using ExecutorService
- Performance benchmarks using JMH
- Security tests with penetration scenarios
- Test data builders and fixtures
- Test documentation explaining test scenarios

## EduKit-Specific Context
- Multi-module Spring Boot application structure
- MySQL database with Asia/Seoul timezone
- Redis caching layer
- JWT authentication with 2-week expiration
- AWS services integration (S3, SES, SQS)
- OpenAI API integration
- Blue-green deployment considerations
- Korean localization requirements