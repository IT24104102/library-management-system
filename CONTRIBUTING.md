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

## Before you push
- [ ] Pull latest main/dev
- [ ] Rebase your branch if needed
- [ ] Build & tests pass locally
- [ ] Commit messages follow Conventional Commits
- [ ] PR targets `dev` (not `main`)

