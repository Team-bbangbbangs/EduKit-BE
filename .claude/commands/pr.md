# Pull Request Automation Command

## Slash Command
`/pr`

## Description
Automatically creates pull requests with comprehensive summaries, test plans, and proper formatting for the EduKit project using GitHub CLI.

## Functionality
- Analyzes all commits in the current branch since diverging from develop
- Generates detailed PR title and description
- Creates comprehensive test plan based on changes
- Includes Jira ticket references
- Follows EduKit's PR template format
- Automatically pushes branch if needed
- Uses Korean descriptions when appropriate

## PR Template Format
```markdown
## Summary
- 🎯 **목적**: {Purpose of the changes}
- 🔧 **변경사항**: {Key changes made}
- 📋 **Jira 티켓**: [EDMT-XXX](jira-ticket-url)

## Changes
### 🆕 New Features
- {List of new features}

### 🐛 Bug Fixes
- {List of bug fixes}

### 🔄 Refactoring
- {List of refactoring changes}

### 📚 Documentation
- {Documentation updates}

## Test Plan
### ✅ Manual Testing Checklist
- [ ] {Test case 1}
- [ ] {Test case 2}
- [ ] {Test case 3}

### 🧪 Automated Tests
- [ ] Unit tests pass: `./gradlew test`
- [ ] Integration tests pass
- [ ] Build succeeds: `./gradlew build`

### 🔍 Code Quality
- [ ] Code review completed
- [ ] No new warnings or errors
- [ ] Security considerations reviewed

## Deployment Notes
- [ ] Database migrations included (if applicable)
- [ ] Environment variables updated (if needed)
- [ ] AWS resources configured (if required)

🤖 Generated with Claude Code
```

## EduKit-Specific Context
- Base branch: `develop`
- Jira project: EDMT
- Multi-module Spring Boot application
- Uses blue-green deployment
- AWS infrastructure (S3, SES, SQS, RDS, ElastiCache)
- Korean team communication preferred
- Common change patterns:
  - API additions in edukit-api
  - Business logic in edukit-core
  - External integrations in edukit-external
  - Database migrations in Flyway

## Usage Examples
```bash
# User types:
/pr

# Agent analyzes branch and creates PR with title:
[EDMT-123] 사용자 인증 API 개선 및 JWT 토큰 갱신 로직 추가
```

## Expected Behavior
1. Check git status and current branch
2. Compare with develop branch to get all commits
3. Analyze changed files and commit messages
4. Extract Jira ticket numbers
5. Generate comprehensive PR title and description
6. Push branch to remote if needed
7. Create PR using GitHub CLI
8. Return PR URL for easy access