#!/usr/bin/env bash
# Starts (or tests) the Spring Boot demo server in ./spring-demo.
#   ./serve.sh                   start on http://localhost:8080          (stop with Ctrl+C)
#   ./serve.sh prod              start with the "prod" profile (different greeting, page size, no seed data)
#   ./serve.sh --port 9090       start on another port
#   ./serve.sh prod --port 9090  both
#   ./serve.sh test              run the demo's automated tests
# The first run downloads Spring Boot (about 1-2 minutes); later runs start in a few seconds.
set -euo pipefail
cd "$(dirname "$0")/spring-demo"

command -v mvn >/dev/null || { echo "Maven not found. Install JDK 21 + Maven (see README)."; exit 1; }

if [ "${1:-}" = "test" ]; then
  mvn -B test
  exit $?
fi

app_args=()
while [ $# -gt 0 ]; do
  case "$1" in
    prod) app_args+=("--spring.profiles.active=prod"); shift ;;
    --port) app_args+=("--server.port=${2:?--port needs a number}"); shift 2 ;;
    *) echo "Unknown option '$1'. Usage: ./serve.sh [prod] [--port N] | ./serve.sh test"; exit 1 ;;
  esac
done

echo "Starting the demo server... (Ctrl+C to stop). Try: curl localhost:8080/api/books   -   guide: docs/spring-server.md"
mvn -q -B spring-boot:run ${app_args[@]+"-Dspring-boot.run.arguments=${app_args[*]}"}
