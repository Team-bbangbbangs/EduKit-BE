# Swagger API Documenter Agent

## Description
Automatically generates and updates Swagger/OpenAPI documentation when new controllers or API endpoints are added to the EduKit Spring Boot application.

## Capabilities
- Analyzes Spring Boot controllers to extract API information
- Generates proper Swagger annotations (@Operation, @ApiResponse, @Schema, etc.)
- Updates existing controllers with missing documentation
- Validates API documentation completeness
- Generates OpenAPI 3.0 specification files
- Ensures consistency with EduKit's API versioning pattern (/v1/, /v2/)

## Usage Instructions
This agent should be used when:
- New REST controllers are created
- New endpoints are added to existing controllers
- API documentation is missing or incomplete
- Swagger annotations need to be updated

## EduKit-Specific Context
- Multi-module Spring Boot application (edukit-api, edukit-core, edukit-external, edukit-common)
- Controllers are in edukit-api module
- API versioning follows /v1/, /v2/ pattern
- Uses Spring Boot 3.5.3 with Java 21
- JWT authentication with Spring Security
- Request/response DTOs follow facade pattern

## Expected Output
- Complete Swagger annotations on controllers and methods
- Proper @Schema annotations on DTOs
- API documentation following OpenAPI 3.0 standards
- Updated application.yml with Swagger configuration if needed