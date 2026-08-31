# AI Usage Disclosure

## Summary
GitHub Copilot and Claude AI were used while completing this Spring Boot transaction service exercise.

## What AI Assisted With
- Project structure and package organization
- Entity and JPA repository implementation
- Request DTO and validation annotations
- Service layer business logic and rules
- REST controller endpoint implementations
- Exception handling and global exception handler
- MockMvc integration test design and assertions
- Documentation and README structure
- Postman-style API request examples

## What Was Verified
- Generated code was reviewed for correctness and Spring Boot patterns
- All 10 automated tests run successfully (9 controller tests + 1 context test)
- Manual API testing confirmed:
  - POST /api/transactions: creates transactions, rejects duplicates (409), validates input (400)
  - GET /api/transactions/{id}: retrieves transactions (200), returns 404 for missing
  - PATCH /api/transactions/{id}/status: updates status, enforces transitions, rejects invalid (400)
  - GET /api/customers/{id}/transactions: retrieves filtered list, returns empty array
- Status transition rules enforced: PENDING → COMPLETED/FAILED, terminal states protected
- H2 in-memory database integration working
- JDK 17 and Maven build successful

## What Was Changed
- Incorrect Maven wrapper Java 8 environment corrected to JDK 17
- Build artifacts (target/) removed for clean submission
- Duplicate generated project folder (toucan-springboot-starter/) removed
- README updated with complete, verified API documentation
- No changes to core Spring Boot version, dependencies, or application configuration