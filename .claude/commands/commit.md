# Auto Commit Command

## Slash Command
`/commit`

## Description
Automatically analyzes staged changes and generates appropriate commit messages following EduKit's commit conventions and Jira ticket format.

## Functionality
- Analyzes all staged files and changes
- Detects the type of changes (feature, fix, refactor, docs, etc.)
- Generates commit messages in EduKit format: `[prefix] descriptive message`
- Follows conventional commit patterns
- Includes Korean descriptions when appropriate for Korean team members
- Automatically stages relevant files if needed

## Commit Message Patterns
- `[feat] 새로운 기능: {feature description}` - for new features
- `[fix] 버그 수정: {bug description}` - for bug fixes
- `[refac] 리팩토링: {refactor description}` - for code refactoring
- `[docs] 문서 업데이트: {docs description}` - for documentation
- `[test] 테스트 추가: {test description}` - for tests
- `[infra] 설정 변경: {config description}` - for configuration changes
- `[dependency] 의존성 업데이트: {dependency description}` - for dependency updates

## EduKit-Specific Context
- Project uses Jira tickets with EDMT prefix
- Multi-module Spring Boot application
- Team uses both Korean and English in commit messages
- Follows clean architecture principles
- Common file patterns:
  - Controllers: `edukit-api/src/main/java/.../controller/`
  - Services: `edukit-core/src/main/java/.../service/`
  - Entities: `edukit-core/src/main/java/.../domain/`
  - Migrations: `edukit-api/src/main/resources/db/migration/`
  - Configuration: `application-*.yml`

## Usage Examples
```bash
# User types:
/commit

# Agent analyzes changes and generates:
[EDMT-123] 새로운 기능: 사용자 인증 API 엔드포인트 추가

🤖 Generated with Claude Code
Co-Authored-By: Claude <noreply@anthropic.com>
```

## Expected Behavior
1. Analyze current git status and staged changes
2. Determine the primary type of changes
3. Extract or prompt for Jira ticket number if not found
4. Generate descriptive commit message
5. Execute git commit with generated message
6. Include Claude Code attribution
