#!/usr/bin/env bash
# Usage:
#   ./run.sh                      run every test (practice mode: your code in exercises/)
#   ./run.sh collections          run one category
#   ./run.sh core PointTest       run one test class in a category
#   ./run.sh --solutions [cat]    run against the reference solutions (sanity check / peek)
#   ./run.sh --learn [cat]        run reference solutions and list every behaviour that passes (read-only learning mode)
#   ./run.sh --verbose ...        show the raw Maven output
#   ./run.sh --list               list categories
set -euo pipefail
cd "$(dirname "$0")"

# Prefer an installed Maven; fall back to the wrapper (needs internet to download Maven once).
if command -v mvn >/dev/null; then MVN=mvn
elif [ -x ./mvnw ]; then MVN=./mvnw
else echo "Maven not found. Install JDK 21 + Maven (see README)."; exit 1; fi

categories() { ls src/test/java/prep | grep -v '\.java$' | sort; }

flags=(-q -B)
verbose=0
learn=0
while [ $# -gt 0 ]; do
  case "$1" in
    --list) categories; exit 0 ;;
    --solutions) flags+=(-Dsolutions); shift ;;
    --learn) flags+=(-Dsolutions); learn=1; shift ;;
    --verbose) verbose=1; shift ;;
    *) break ;;
  esac
done

target=()
category=""
if [ $# -ge 1 ]; then
  category="$1"
  categories | grep -qx "$category" || { echo "Unknown category '$category'. Categories:"; categories; exit 1; }
  if [ $# -ge 2 ]; then target=(-Dtest="prep.$category.$2*"); else target=(-Dtest="prep.$category.*Test"); fi
fi

echo "Running tests${category:+ for '$category'}$([[ " ${flags[*]} " == *Dsolutions* ]] && echo ' (against reference solutions)')..."
out=$(mktemp)
trap 'rm -f "$out"' EXIT
set +e
"$MVN" "${flags[@]}" ${target[@]+"${target[@]}"} -Dsurefire.failIfNoSpecifiedTests=false test >"$out" 2>&1
status=$?
set -e

if [ "$verbose" -eq 1 ]; then grep -v '^Picked up JAVA_TOOL_OPTIONS' "$out"; fi

if [ "$status" -eq 0 ]; then
  echo "ALL GREEN - every test passed."
  if [ "$learn" -eq 1 ]; then
    echo
    echo "What the reference solutions do (each line = one verified behaviour):"
    grep -h -o '<testcase name="[^"]*" classname="[^"]*"' target/surefire-reports/TEST-*.xml 2>/dev/null \
      | grep -vi 'name="level[0-9]' | sed -E 's/<testcase name="([^"]*)" classname="prep\.([^.]*)\.([^"]*)"/\2 \3 \1/' | sort -u \
      | awk '{ if ($1 != c) { c = $1; printf "\n[%s]  read: solutions/java/prep/%s/\n", c, c }
               if ($2 != k) { k = $2; printf "  %s\n", k }
               n = $3; sub(/\(\).*/, "", n); printf "      ok  %s\n", n }'
    echo
    echo "Reading order and what to notice in each file: docs/solutions-tour.md"
  fi
  exit 0
fi

if grep -q "COMPILATION ERROR" "$out"; then
  echo
  echo "Your code does not compile. Fix these first:"
  grep -E '^\[ERROR\] /.*\.java' "$out" | sed 's|^\[ERROR\] ||; s|.*/JavaTutorial/||' | sort -u | head -20
  echo
  echo "(run with --verbose for the full Maven output)"
  exit "$status"
fi

# Summarise failures from the "[ERROR] Failures:/Errors:" section of Maven's output.
echo
awk '
  /^\[ERROR\] (Failures|Errors): *$/ { sect = 1; next }
  /^\[ERROR\] Tests run:/ { sect = 0 }
  /^\[ERROR\] Failed to execute goal/ { sect = 0 }
  sect && /^\[ERROR\]   / {
    if (name != "") flush()
    line = $0; sub(/^\[ERROR\]   /, "", line)
    split(line, parts, " ~~ ")
    name = parts[1]; msg = (parts[2] != "") ? parts[2] : ""
    next
  }
  sect && name != "" && !/^\[ERROR\]/ && $0 !~ /^[[:space:]]*$/ { msg = msg (msg == "" ? "" : " ") $0 }
  function flush(   m) {
    sub(/:[0-9]+ *$/, "", name)
    m = msg
    if (m ~ /UnsupportedOperation(Exception)? TODO/) m = "not implemented yet (still the TODO stub)"
    gsub(/^ +| +$/, "", m)
    if (length(m) > 110) m = substr(m, 1, 107) "..."
    print "  FAIL " name "\n       -> " m
    name = ""; msg = ""
  }
  END { if (name != "") flush() }
' <(sed -e 's/ » / ~~ /' -e 's/\(:[0-9][0-9]*\) ? /\1 ~~ /' "$out")

total=$(grep -E '^\[ERROR\] Tests run: [0-9]+, Failures: [0-9]+, Errors: [0-9]+, Skipped: [0-9]+$' "$out" | tail -1 || true)
echo
echo "${total:-See details with --verbose}" | sed 's/^\[ERROR\] //'
echo
echo "Tip: 'not implemented yet' means you haven't filled in that exercise. Open the file under exercises/java/prep/"
[ -n "$category" ] && echo "     (category '$category' -> exercises/java/prep/$category/) and replace the TODOs."
echo "     Rerun one exercise: ./run.sh <category> <ClassName>     Full Maven output: add --verbose"
exit "$status"
