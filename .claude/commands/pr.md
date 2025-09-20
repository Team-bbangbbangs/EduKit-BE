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
- **SAFETY**: Requires user confirmation before pushing to remote
- Uses Korean descriptions when appropriate

## PR Template Format
```markdown
## 📣 Jira Ticket
<!-- 지라 티켓 번호를 작성해주세요 -->
[EDMT-]


## 👩‍💻 작업 내용

<!-- 작업 내용을 적어주세요 -->

## 📝 리뷰 요청 & 논의하고 싶은 내용

<!-- PR과정에서 든 생각이나 개선할 내용이 있다면 적어주세요. -->

## 📸 스크린 샷 (선택)

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
6. **Check if branch needs pushing** - ask user for confirmation
7. Create PR using GitHub CLI (only if remote branch exists)
8. Return PR URL for easy access

## Safety Options
- Default: No automatic pushing - user confirmation required
- `--push`: Auto-push without confirmation (use with caution)
- `--no-push`: Create PR draft only (local analysis)

## Usage Examples
```bash
# Safe default - asks before pushing
/pr

# Auto-push (use carefully)
/pr --push

# Analysis only, no remote operations
/pr --no-push
```
