# LMS Backend (Spring Boot)

Web-Based Library Management System backend for local demo. Scope: single library, web-only, local MySQL, run on localhost:8080. No cloud/deployment.

## Tech
- Java 21, Spring Boot 3.x
- Spring Web, Security, Data JPA
- JSP later (tomcat-embed-jasper, JSTL) — no Thymeleaf
- MySQL 8 (via Docker)

## Profiles
- `local` (default) — connects to local MySQL using env vars.

## Quick start

1) Prepare env file

Copy example and edit if needed:

```bash
cp infra/.env.example infra/.env
```

Ensure values are correct. The app reads these env vars for the datasource when `local` profile is active:
- MYSQL_HOST, MYSQL_PORT, MYSQL_DB, MYSQL_USER, MYSQL_PASSWORD

2) Start MySQL (Docker)

```bash
docker compose -f infra/docker-compose.yml up -d
```

Wait until container is healthy.

3) Run the app (IntelliJ)

- Open the project, run class `lk.sliit.lms.LmsBackendApplication` with VM options profile:
  - Active profile: `local`
- Ensure the following environment variables are available to the Run Configuration (IntelliJ Run/Debug Config → Environment → Environment variables). You can paste the contents of `infra/.env` or define the five MYSQL_* vars.

The app should start on http://localhost:8080.

Login (stub in-memory users for RBAC testing):
- admin/password → ROLE_ADMIN
- librarian/password → ROLE_LIBRARIAN
- assistant/password → ROLE_ASSISTANT
- student/password → ROLE_STUDENT
- itsupport/password → ROLE_IT_SUPPORT
- chief/password → ROLE_CHIEF_LIBRARIAN

4) Stop and reset data

- Stop app in IntelliJ.
- Stop MySQL:

```bash
docker compose -f infra/docker-compose.yml down
```

- Reset MySQL data (deletes volume):

```bash
docker compose -f infra/docker-compose.yml down -v
```

## Local Database

Follow these steps to run a local MySQL for the LMS demo using Docker Compose.

1) Create env file

```bash
cp infra/.env.example infra/.env
```

Adjust values if needed.

2) Start DB (from infra folder)

```bash
cd infra
docker compose up -d
```

3) Check health

```bash
docker compose ps
```

Wait until the mysql service shows "healthy".

4) Stop (data persists)

```bash
docker compose down
```

5) Reset data (DELETES all data)

```bash
docker compose down -v
```

Important:
- Do NOT use root credentials in the application.
- Use MYSQL_USER + MYSQL_PASSWORD for the app connection.

Troubleshooting:
- Port 3306 in use: Stop other MySQL instances or change MYSQL_PORT in infra/.env, then re-run compose.
- Docker not running: Start Docker Desktop (Windows/macOS) and retry.
- Wrong password or access denied: Ensure infra/.env matches the app datasource env vars and re-create the container (down, then up -d).

## Run the App (Local Profile)

Before running:
- Ensure the MySQL container is healthy (see Local Database section).

Steps (IntelliJ IDEA):
1) Open Run/Debug Configurations → your Spring Boot app
2) Set Active Profiles to: `local`
3) (Optional) Set Environment variables if your OS session doesn’t inherit them:
   - MYSQL_HOST=127.0.0.1
   - MYSQL_PORT=3306
   - MYSQL_DB=lms
   - MYSQL_USER=lms_user
   - MYSQL_PASSWORD=lms_password123
4) Run the app. Expect successful datasource validation.

Important:
- Do NOT use root credentials in the application; use MYSQL_USER + MYSQL_PASSWORD only.
- The app reads datasource vars from standard environment variables.

Troubleshooting:
- Communications link failure: DB not started or wrong host/port. Verify container is healthy and MYSQL_HOST/MYSQL_PORT.
- Access denied: Wrong user/pass; ensure the app is not using root and MYSQL_USER/MYSQL_PASSWORD are correct.
- Collation/charset issues: Keep utf8mb4/utf8 settings as configured in application-local.yml.

## Startup checklist (console output)
On successful boot you should see a checklist in logs similar to:
- [Profiles] active=[local]
- [Server] http://localhost:8080
- [Security] In-memory RBAC users loaded: 6/6
- [JPA Entities] User, Book, Loan, Reservation, Fine, AuditLog present (placeholders)
- [Config] JSP support added (tomcat-embed-jasper, JSTL)
- [Datasource] Using env vars MYSQL_HOST/PORT/DB/USER/PASSWORD (ddl-auto=none)
- [Scope] Web-only, single library, local MySQL; no cloud/mobile; basic security

## Project structure (initial)

- infra/ — docker-compose and env template
- docs/ — API docs and diagrams placeholders
- scripts/ — helper scripts (seed/reset)
- src/main/java/lk/sliit/lms/... — modules (config, auth, books, loans, reservations, fines, search, notifications, audit, admin, web)
- src/main/resources/ — application.yml, application-local.yml, templates/, static/

## Verification & Demo Checklist (Stage 2)

**Pre-checks:**
- [ ] Docker Desktop running
- [ ] `/infra/.env` created from `.env.example`
- [ ] `docker compose up -d` executed and MySQL is healthy

**IntelliJ run:**
- [ ] Profile = `local`
- [ ] Environment variables set (MYSQL_HOST, MYSQL_PORT, MYSQL_DB, MYSQL_USER, MYSQL_PASSWORD) if needed
- [ ] App starts on http://localhost:8080 without errors

**DB connectivity:**
- [ ] Datasource initialized without exceptions
- [ ] No use of ROOT from application

**Troubleshooting quick refs:**
- Port 3306 already in use → stop other MySQL, change MYSQL_PORT, or shut conflicting service
- Access denied for user → verify MYSQL_USER/MYSQL_PASSWORD match `infra/.env`
- Communications link failure → verify container health and host/port

**Note:** Business logic and UI are intentionally not implemented yet (next stages).

## Notes
- Non-functional: basic security (BCrypt), simple and maintainable, local scale.
- Limitations: single branch, local DB, basic encryption, no pen-test, no mobile app.
- Next steps: wire repositories/services, controllers, and UI pages incrementally.

## Repo & Branches

- Default branches: `main` (stable), `dev` (integration), `feature/*` (task branches)
- Create a feature branch:
  - `git checkout -b feature/<short-task-name>`
- Open a PR:
  - Target branch: `dev`
  - Keep PRs small; link the task/issue ID; ensure CI passes
- See CONTRIBUTING for details: [CONTRIBUTING.md](CONTRIBUTING.md)
