#!/usr/bin/env bash
set -euo pipefail

echo "[reset] Stopping and removing MySQL and deleting data volume..."
docker compose -f infra/docker-compose.yml down -v || true

