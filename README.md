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

Login: use a DB-backed user (see RBAC & Authentication for creating a test user and roles).

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
- Port 3307 in use: Stop other MySQL instances or change MYSQL_PORT in infra/.env, then re-run compose.
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
   - MYSQL_PORT=3307
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

## Run (Local)

Use these exact settings for the local demo environment.

1) Start MySQL via Docker

```bash
cd infra
docker compose up -d
# wait until STATUS is healthy
docker compose ps
```

2) IntelliJ Run/Debug Configuration
- Active Profiles: `local`
- Environment variables (add each separately):
  - `MYSQL_HOST=127.0.0.1`
  - `MYSQL_PORT=3307`
  - `MYSQL_DB=lms`
  - `MYSQL_USER=lms_user`
  - `MYSQL_PASSWORD=lms_password123`
  - `SERVER_PORT=8081`  # default is 8081 to avoid conflicts

3) Start the app (default port 8081)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
# or explicitly
mvn spring-boot:run -Dspring-boot.run.profiles=local -Dserver.port=8081
```

On startup (profile=local) you should see this banner in the console:

```
LMS started (profile=local, DB=lms@127.0.0.1:3307, PORT=8081)
[Datasource] JDBC URL: jdbc:mysql://127.0.0.1:3307/lms?useUnicode=true&characterEncoding=utf8&connectionCollation=utf8mb4_0900_ai_ci&serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false
```

Prefer port 8080?
- Free it first, then start with `-Dserver.port=8080` or `SERVER_PORT=8080`.
  - macOS/Linux:
    ```bash
    lsof -ti:8080 | xargs kill -9
    ```
  - Windows (PowerShell):
    ```powershell
    Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess | Stop-Process -Force
    ```

4) IntelliJ Database Data Source (optional, for browsing)
- Host: `127.0.0.1`
- Port: `3307`
- User: `lms_user`
- Password: `lms_password123`
- Database: `lms`
- URL:
  `jdbc:mysql://127.0.0.1:3307/lms?useUnicode=true&characterEncoding=utf8&connectionCollation=utf8mb4_0900_ai_ci&serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false`

5) Login to the app
- URL: `http://localhost:8081/login`

DEMO CREDENTIALS (LOCAL ONLY)

| Email               | Password       | Roles     |
|---------------------|----------------|-----------|
| admin@lms.local     | Admin@123      | ADMIN     |
| librarian@lms.local | Librarian@123  | LIBRARIAN |
| student1@lms.local  | Student@123    | STUDENT   |

Notes
- For classroom demo only — do not use in production.
- To reset database:

```bash
cd infra
docker compose down -v
docker compose up -d
```

## Startup checklist (console output)
On successful boot you should see a checklist in logs similar to:
- [Profiles] active=[local]
- [Server] http://localhost:8080
- [Security] DB-backed authentication enabled (BCrypt), RBAC enforced
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

**RBAC quick test:**
- [ ] Create a user and attach roles (see RBAC & Authentication)
- [ ] Login via form at /login
- [ ] GET /api/me returns your principal (email, name, roles)
- [ ] Try these pings according to your role and expect 200/403 as applicable:
  - /api/admin/ping
  - /api/catalog/ping (GET allowed for STUDENT; write ops restricted to staff)
  - /api/loans/ping
  - /api/reservations/ping
  - /api/reports/ping

**Troubleshooting quick refs:**
- Port 3307 already in use → stop other MySQL, change MYSQL_PORT, or shut conflicting service
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

## Migrations

- **Tool**: Flyway (auto-runs on startup in `local` profile)
- **Location**: `src/main/resources/db/migration` (V1__..., V2__..., etc.)
- **Run**: Start app with profile `local` (IntelliJ → Active Profiles: local)
- **Add new migration**:
  1. Create file `V<next>__short_description.sql` under `db/migration`
  2. Use MySQL 8 syntax; keep utf8mb4
  3. Start the app to apply it (or re-run)
- **Schema management**: Keep `spring.jpa.hibernate.ddl-auto=none` (schema managed by Flyway)

**Current migrations**:
- V1: Core tables (roles, users, user_roles, books, loans, reservations, fines, audit_log)
- V2: Performance indexes and reporting views (v_popular_books, v_overdue_loans)
- V3: Baseline roles seeding (ADMIN, CHIEF_LIBRARIAN, LIBRARIAN, ASSISTANT, STUDENT, IT_SUPPORT, ACADEMIC_COORD)

## RBAC & Authentication

- Auth: Session-based form login; passwords stored as BCrypt hashes.
- Roles (from docs): ADMIN, CHIEF_LIBRARIAN, LIBRARIAN, ASSISTANT, STUDENT, IT_SUPPORT, ACADEMIC_COORD.
- Access (high-level):
  - /api/admin/** → ADMIN, IT_SUPPORT
  - /api/catalog/** → CHIEF_LIBRARIAN, LIBRARIAN, ASSISTANT; GET allowed for STUDENT
  - /api/loans/** → CHIEF_LIBRARIAN, LIBRARIAN, ASSISTANT
  - /api/reservations/** → CHIEF_LIBRARIAN, LIBRARIAN, ASSISTANT, STUDENT
  - /api/reports/** → CHIEF_LIBRARIAN, LIBRARIAN, ASSISTANT, ADMIN
  - /api/me → any authenticated user (returns principal info)

### Create a test user (temporary manual seed)
Run in the MySQL container (adjust email/role as needed):

```bash
# Create ACTIVE user with BCrypt password = "password"
docker exec -i lms-mysql mysql -h 127.0.0.1 -P ${MYSQL_PORT:-3307} -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DB" <<'SQL'
INSERT INTO users(name, email, password_hash, status, created_at)
VALUES ('Test Student','student@example.edu',
  '$2a$10$7EqJtq98hPqEX7fNZaFWoOa8GJ9qG8zZZQzQH0imeISFRCGDpa2G',
  'ACTIVE', NOW());

-- attach STUDENT role (ensure V3 seeded roles exists)
INSERT INTO user_roles(user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email='student@example.edu' AND r.code='STUDENT';
SQL
```

### Test
1) Start DB and app (profile=local).
2) Open http://localhost:8080 and log in via the default login page.
   - Username: student@example.edu
   - Password: password
3) Call GET http://localhost:8080/api/me → should return email, name, roles.
4) Try protected routes you do/do not have (e.g., /api/admin/ping vs /api/catalog/ping) → expect 403 vs 200 as per roles.

Audit trail: login successes/failures are recorded in `audit_log` with actions LOGIN_SUCCESS / LOGIN_FAILED.
