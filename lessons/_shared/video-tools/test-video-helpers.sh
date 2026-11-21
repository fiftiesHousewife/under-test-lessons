#!/usr/bin/env bash
# Tests the in-terminal helpers for lesson videos (lessons/_shared/callout.sh) in a few seconds, so a broken helper is
# found before a real recording shows it. Run from the repository root:
#   bash lessons/_shared/video-tools/test-video-helpers.sh
set -uo pipefail
cd "$(dirname "$0")/../../.."
FAILURES=0
WORK="$(mktemp -d "${TMPDIR:-/tmp}/video-helpers-test.XXXXXX")"
trap 'rm -rf "$WORK"' EXIT

expect() { # expect "<what>" "<text>" "<must contain>"
  if [[ "$2" == *"$3"* ]]; then echo "ok   $1"; else echo "FAIL $1: expected to contain [$3], got [$2]"; FAILURES=$((FAILURES + 1)); fi
}
refuse() { # refuse "<what>" "<text>" "<must not contain>"
  if [[ "$2" != *"$3"* ]]; then echo "ok   $1"; else echo "FAIL $1: did not expect [$3] in [$2]"; FAILURES=$((FAILURES + 1)); fi
}
plain() { sed -E $'s/\x1b\\[[0-9;]*[A-Za-z]//g; s/\r//g'; }

source lessons/_shared/callout.sh
export TYPE_DELAY=0

# --- callout, note, spacing -----------------------------------------------------------------------------------------
expect "callout draws a box round its message" "$(callout 'Five of thirteen' | plain)" "│ Five of thirteen │"
expect "note prints its text" "$(note 'an aside' | plain)" "an aside"
OUT="$(_VIDEO_LAST=""; note 'first'; note 'second'; show "$0" 1 1; callout 'boxed' )"
OUT="$(printf '%s' "$OUT" | plain)"
refuse "consecutive captions have no blank line between them" "$OUT" $'first\n\nsecond'
expect "a code panel after captions starts after a blank line" "$OUT" $'second\n\n── '
expect "a callout after a code panel starts after a blank line" "$OUT" $'\n\n┌'
refuse "the first block on a fresh screen has no blank line above it" "$(_VIDEO_LAST=""; note 'top' | plain)" $'\ntop'
expect "gap prints a blank line" "$(gap | plain | wc -l | tr -d ' ')" "1"

# --- show ------------------------------------------------------------------------------------------------------------
printf 'one\ntwo\nthree\nfour\n' >"$WORK/sample.txt"
OUT="$(_VIDEO_LAST=""; show "$WORK/sample.txt" 2 3 | plain)"
expect "show names the file" "$OUT" "── sample.txt ──"
expect "show numbers the lines it prints" "$OUT" "  2  two"
refuse "show stops at the last line asked for" "$OUT" "four"
mkdir -p "$WORK/repo/lessons/x" "$WORK/repo/build-logic" "$WORK/repo/gradle"
printf 'test\n' >"$WORK/repo/lessons/x/CalcStrongTest.java"; printf 'code\n' >"$WORK/repo/lessons/x/Calc.java"
expect "show prefers an exact file name to a partial match" "$(cd "$WORK/repo" && show Calc | plain)" "── Calc.java ──"
expect "show uses a label when given one" "$(show "$WORK/sample.txt" 1 1 'Build settings' | plain)" "── Build settings ──"

printf 'plugins {\n    java\n    id("x.y")\n}\n\ndependencies {\n    implementation(libs.a)\n    implementation(libs.b)\n}\n' >"$WORK/build.gradle.kts"
OUT="$(_VIDEO_LAST=""; show "$WORK/build.gradle.kts" "/libs.b/" "deps" | plain)"
expect "show /pattern/ prints the whole block around a match" "$OUT" "  6  dependencies {"
expect "show /pattern/ prints to the block's closing brace" "$OUT" "  9  }"
refuse "show /pattern/ leaves out other blocks" "$OUT" "plugins"
expect "show /pattern/ prints a block the matching line opens" "$(show "$WORK/build.gradle.kts" "/^plugins/" | plain)" "  4  }"
printf '[versions]\na = "1"\npit = "2"\nb = "3"\n\n[libraries]\npit = { module = "x" }\n' >"$WORK/libs.versions.toml"
OUT="$(_VIDEO_LAST=""; show "$WORK/libs.versions.toml" "/^pit/" | plain)"
expect "show /pattern/ in a catalogue shows each match under its section" "$OUT" $'  1  [versions]\n  ⋮\n  3  pit = "2"'
expect "show /pattern/ in a catalogue shows the second section too" "$OUT" "  6  [libraries]"
expect "note highlights a *word* in yellow" "$(note 'the *weak* tests' | cat -v)" "[1;33mweak"

# --- run_with_notes --------------------------------------------------------------------------------------------------
OUT="$(NOTE_INTERVAL=1 run_with_notes 'sleep 2.5; echo RESULT-LINE' '# first note' '# second note' '# never shown' | plain)"
expect "run_with_notes shows the command as if typed" "$OUT" '$ sleep 2.5; echo RESULT-LINE'
expect "run_with_notes shows notes while the command runs" "$OUT" "# second note"
expect "run_with_notes prints the command's output" "$OUT" "RESULT-LINE"
refuse "run_with_notes skips notes once the command has finished" "$OUT" "never shown"
expect "run_with_notes includes the error stream" "$(NOTE_INTERVAL=1 run_with_notes 'echo to-stderr >&2' | plain)" "to-stderr"
expect "run_with_notes gives the command no terminal input" \
  "$(NOTE_INTERVAL=1 run_with_notes 'read -r line; echo "read-status-$?"' | plain)" "read-status-1"

# --- run_live --------------------------------------------------------------------------------------------------------
export VIDEO_TIMINGS="$WORK/demo.timings"
OUT="$(run_live 'sleep 1.2; echo LIVE-OUTPUT' | plain)"
expect "run_live shows the command" "$OUT" '$ sleep 1.2; echo LIVE-OUTPUT'
expect "run_live shows the command's own output" "$OUT" "LIVE-OUTPUT"
run_live 'true' >/dev/null
expect "run_live records one measured time per run" "$(wc -l <"$VIDEO_TIMINGS" | tr -d ' ')" "2"
expect "run_live records how long the run took" "$(head -1 "$VIDEO_TIMINGS")" "1."

# --- erasing typed commands -------------------------------------------------------------------------------------------
expect "a short typed command takes one row" "rows $(COLUMNS=80 _typed_rows callout 'short')" "rows 1"
expect "a typed command wider than the terminal takes more rows" "rows $(COLUMNS=20 _typed_rows callout 'abcdefghij')" "rows 2"
ERASES="$(COLUMNS=20 callout 'abcdefghij' | grep -o $'\x1b\\[1A' | wc -l | tr -d ' ')"
expect "callout erases every row its typed command took" "erases $ERASES" "erases 2"

# Inside VHS the helpers run in an interactive shell on a real terminal, where a background job that reads the
# terminal is stopped and job messages like "[1] 1234" appear on screen.
in_terminal() {
  if script --version >/dev/null 2>&1; then script -qec "bash --norc -i -c '$1'" /dev/null
  else script -q /dev/null bash --norc -i -c "$1"; fi
}
if command -v script >/dev/null && [[ "$(in_terminal 'echo TERMINAL-OK' 2>&1)" == *TERMINAL-OK* ]]; then
  OUT="$(in_terminal 'source lessons/_shared/callout.sh; NOTE_INTERVAL=1 TYPE_DELAY=0 run_with_notes "read -r x; echo READ-FINISHED" "# a note"' 2>&1 | plain)"
  expect "run_with_notes finishes in an interactive terminal when the command reads input" "$OUT" "READ-FINISHED"
  refuse "run_with_notes shows no job messages in a terminal" "$OUT" "Stopped"
else
  echo "skip terminal tests: no pseudo-terminal available here (needs the script command and a terminal)"
fi

if [ "$FAILURES" -gt 0 ]; then echo "$FAILURES video helper test(s) failed"; exit 1; fi
echo "All video helper tests passed."
