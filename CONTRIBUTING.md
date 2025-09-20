# Contributing

## Branch model
- main: stable, release-ready
- dev: integration branch
- feature/*: task branches (e.g., feature/auth-login)

## Pull Requests
- Small PRs; link task/issue ID in title or description
- At least 1 reviewer approval
- CI must pass; fix lint/build/test before merge

## Conventional Commits
- Types: feat, fix, docs, chore, refactor, test, build, ci
- Scopes: auth, books, loans, reservations, fines, search, notifications, audit, admin, web, infra
- Format: `<type>(<scope>): <short summary>`

### Examples
- feat(auth): add BCrypt encoder for passwords
- fix(loans): correct due date calculation on renew
- docs(infra): add local DB instructions
- chore(repo): update .editorconfig

## Conventional Commits Quick Table
| Type | Usage | Example |
|------|-------|---------|
| `feat(scope)` | New feature | feat(auth): add RBAC guards |
| `fix(scope)` | Bug fix | fix(loans): null dueAt edge case |
| `docs(scope)` | Documentation only | docs(infra): add troubleshooting |
| `refactor(scope)` | Code change, no behavior change | refactor(books): extract validation |
| `chore(scope)` | Tooling or repo hygiene | chore(infra): docker compose healthcheck tune |
| `test(scope)` | Tests only | test(auth): add role guard unit tests |
| `ci(scope)` | CI/pipeline changes | ci: add Maven build workflow |
| `build(scope)` | Build config changes | build: update Spring Boot to 3.6 |

### Project Examples
- `feat(auth): add RBAC role guards`
- `feat(books): catalog search filters`
- `feat(loans): implement renewal logic`
- `fix(reservations): queue position calculation`
- `docs(readme): update verification checklist`
- `chore(infra): tune MySQL healthcheck interval`

## Before you push
- [ ] Pull latest main/dev
- [ ] Rebase your branch if needed
- [ ] Build & tests pass locally
- [ ] Commit messages follow Conventional Commits
- [ ] PR targets `dev` (not `main`)
