#!/usr/bin/env bash
# Usage:
#   ./run.sh                      run every test (practice mode: your code in exercises/)
#   ./run.sh collections          run one category (shows every test that ran)
#   ./run.sh core PointTest       run one test class in a category
#   ./run.sh --solutions [cat]    run against the reference solutions (sanity check / peek)
#   ./run.sh --learn [cat]        solutions + full per-test details with each test's purpose
#   ./run.sh --details ...        always show every test (default for a single category)
#   ./run.sh --verbose ...        also show the raw Maven output
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
details=0
solutions=0
while [ $# -gt 0 ]; do
  case "$1" in
    --list) categories; exit 0 ;;
    --solutions) flags+=(-Dsolutions); solutions=1; shift ;;
    --learn) flags+=(-Dsolutions); solutions=1; learn=1; details=1; shift ;;
    --details) details=1; shift ;;
    --verbose) verbose=1; shift ;;
    *) break ;;
  esac
done

target=()
category=""
if [ $# -ge 1 ]; then
  category="$1"
  categories | grep -qx "$category" || { echo "Unknown category '$category'. Categories:"; categories; exit 1; }
  details=1
  if [ $# -ge 2 ]; then target=(-Dtest="prep.$category.$2*"); else target=(-Dtest="prep.$category.*Test"); fi
fi

echo "Running tests${category:+ for '$category'}$([ "$solutions" -eq 1 ] && echo ' (against reference solutions)')..."
out=$(mktemp)
purposes=$(mktemp)
results=$(mktemp)
trap 'rm -f "$out" "$purposes" "$results"' EXIT

report_dir=target/surefire-reports
rm -f "$report_dir"/TEST-*.xml   # avoid listing results left over from an earlier run

set +e
"$MVN" "${flags[@]}" ${target[@]+"${target[@]}"} -Dsurefire.failIfNoSpecifiedTests=false test >"$out" 2>&1
status=$?
set -e

# grep exits 1 when it prints nothing (e.g. all tests passed quietly); that must not stop the script under set -e
if [ "$verbose" -eq 1 ]; then grep -v '^Picked up JAVA_TOOL_OPTIONS' "$out" || true; fi

if grep -q "COMPILATION ERROR" "$out"; then
  echo
  echo "Your code does not compile. Fix these first:"
  grep -E '^\[ERROR\] /.*\.java' "$out" | sed 's|^\[ERROR\] ||; s|.*/JavaTutorial/||' | sort -u | head -20
  echo
  echo "(run with --verbose for the full Maven output)"
  exit "$status"
fi

# ---- per-test report from Maven's XML files -------------------------------------------------
if ls "$report_dir"/TEST-*.xml >/dev/null 2>&1; then
  # 1) purpose comments:  Class.method <TAB> text   (from the "// PURPOSE:" line above each test)
  for f in src/test/java/prep/*/*Test.java; do
    awk -v cls="$(basename "$f" .java)" '
      /\/\/ PURPOSE:/ { p = $0; sub(/.*PURPOSE: */, "", p); next }
      /void [A-Za-z0-9_]+\(/ { if (p != "") { m = $0; sub(/.*void /, "", m); sub(/\(.*/, "", m); print cls "." m "\t" p; p = "" } }
    ' "$f"
  done >"$purposes"

  # 2) test results:  classname <TAB> name <TAB> status <TAB> seconds
  cat "$report_dir"/TEST-*.xml | awk '
    function attr(s, k,   i, r) {
      i = index(s, " " k "=\"")
      if (i == 0) return ""
      r = substr(s, i + length(k) + 3)
      return substr(r, 1, index(r, "\"") - 1)
    }
    function emit() { print cls "\t" name "\t" status "\t" secs }
    /<testcase / {
      name = attr($0, "name"); cls = attr($0, "classname"); secs = attr($0, "time"); status = "PASS"; open = 1
      if ($0 ~ /\/>[[:space:]]*$/) { emit(); open = 0 }
      next
    }
    open && /<failure/ { status = "FAIL" }
    open && /<error/   { status = "ERROR" }
    open && /<skipped/ { status = "SKIP" }
    open && /<\/testcase>/ { emit(); open = 0 }
  ' | sort >"$results"

  # 3) print it
  awk -F'\t' -v detail="$details" '
    NR == FNR { why[$1] = $2; next }
    {
      n = split($1, cp, "."); cat = cp[2]; short = cp[n]
      name = $2; base = name; sub(/[(\[].*/, "", base)
      disp = name; sub(/\([^)]*\)/, "", disp)
      st = $3; total++; count[st]++
      if (!(short in seen)) { seen[short] = 1; order[++nclasses] = short; catof[short] = cat }
      cc[short, st]++; ct[short]++
      if (detail) {
        if (short != last) { printf "\n  [%s] %s\n", cat, short; last = short }
        printf "    %-5s %s  (%ss)\n", st, disp, $4
        w = why[short "." base]
        if (w != "") printf "          purpose: %s\n", w
      }
    }
    END {
      if (!detail) {
        print ""
        for (i = 1; i <= nclasses; i++) {
          c = order[i]
          printf "  %-40s %2d tests: %d pass, %d fail, %d skipped\n", "[" catof[c] "] " c, ct[c], ct[c] - cc[c, "FAIL"] - cc[c, "ERROR"] - cc[c, "SKIP"], cc[c, "FAIL"] + cc[c, "ERROR"], cc[c, "SKIP"]
        }
      }
      bad = count["FAIL"] + count["ERROR"]
      printf "\n  Total: %d tests - %d passed, %d failed, %d skipped\n", total, total - bad - count["SKIP"], bad, count["SKIP"]
    }
  ' "$purposes" "$results"
fi

if [ "$status" -eq 0 ]; then
  echo
  echo "ALL GREEN - every test passed."
  [ "$learn" -eq 1 ] && echo "Reading order and what to notice in each file: docs/solutions-tour.md"
  exit 0
fi

# ---- why the failing tests failed ----------------------------------------------------------
echo
echo "Why tests failed:"
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

echo
echo "Tip: 'not implemented yet' means you haven't filled in that exercise. Open the file under exercises/java/prep/"
[ -n "$category" ] && echo "     (category '$category' -> exercises/java/prep/$category/) and replace the TODOs."
echo "     Rerun one exercise: ./run.sh <category> <ClassName>     Full Maven output: add --verbose"
exit "$status"
