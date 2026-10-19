#!/usr/bin/env bash
# Usage:
#   ./run.sh                      run every test (practice mode: your code in exercises/)
#   ./run.sh collections          run one category
#   ./run.sh core PointTest       run one test class in a category
#   ./run.sh --solutions [cat]    run against the reference solutions (sanity check / peek)
#   ./run.sh --list               list categories
set -euo pipefail
cd "$(dirname "$0")"

# Prefer an installed Maven; fall back to the wrapper (needs internet to download Maven once).
if command -v mvn >/dev/null; then MVN=mvn
elif [ -x ./mvnw ]; then MVN=./mvnw
else echo "Maven not found. Install JDK 21 + Maven (see README)."; exit 1; fi

categories() { ls src/test/java/prep | sort; }

flags=(-q -B)
if [ "${1:-}" = "--list" ]; then categories; exit 0; fi
if [ "${1:-}" = "--solutions" ]; then flags+=(-Dsolutions); shift; fi

target=()
if [ $# -ge 1 ]; then
  categories | grep -qx "$1" || { echo "Unknown category '$1'. Categories:"; categories; exit 1; }
  if [ $# -ge 2 ]; then target=(-Dtest="prep.$1.$2*"); else target=(-Dtest="prep.$1.*Test"); fi
fi

echo ">> $MVN test ${flags[*]} ${target[*]-}"
set +e
"$MVN" "${flags[@]}" ${target[@]+"${target[@]}"} -Dsurefire.failIfNoSpecifiedTests=false test 2>&1 | grep -v '^Picked up JAVA_TOOL_OPTIONS'
status=${PIPESTATUS[0]}
set -e
if [ "$status" -eq 0 ]; then echo "ALL GREEN"; else echo "Some tests failed (expected for unsolved exercises). Exit code $status"; fi
exit "$status"
