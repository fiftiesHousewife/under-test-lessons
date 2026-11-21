# Helpers for lesson videos: draw attention to important text in the terminal.
# Run them while the recording is hidden (Hide ... Show); each erases its own command line first, so viewers see only
# the result.
#   callout "Only 5 of 13 planted bugs were caught"   -> a bold, coloured box around the message
#   note "Same coverage, different strength"          -> a line of narration (caption text on screen), in cyan
#   run_with_notes "<command>" "<note>" ...           -> runs a slow command, with notes while it works

# How many terminal rows the typed command took: "$ " + the function name + each argument in quotes. Long commands wrap,
# so erasing only one row would leave the rest on screen.
_typed_rows() {
  local typed="\$ $1"
  shift
  local arg
  for arg in "$@"; do typed+=" \"$arg\""; done
  local columns="${COLUMNS:-$(tput cols 2>/dev/null || echo 80)}"
  echo $(((${#typed} + columns - 1) / columns))
}

# Erases the typed command: moves up and clears one row for each row it took.
_erase_typed() {
  local rows
  rows=$(_typed_rows "$@")
  local i
  for ((i = 0; i < rows; i++)); do printf '\033[1A\033[2K'; done
  printf '\r'
}

# Spacing: one blank line between blocks of different kinds (captions, code panels, commands, callouts), none between
# consecutive caption lines. _VIDEO_LAST remembers what was drawn last; "scene" starts a fresh screen.
_VIDEO_LAST=""
_gap_before() { [ -n "$_VIDEO_LAST" ] && { [ "$1" != note ] || [ "$_VIDEO_LAST" != note ]; } && printf '\n'; _VIDEO_LAST="$1"; }

#   scene  -> clears the screen for a new scene
scene() {
  clear
  _VIDEO_LAST=""
}

#   gap    -> a blank line, for example between captions and a command typed on camera
gap() {
  _erase_typed gap "$@"
  printf '\n'
  _VIDEO_LAST=""
}

callout() {
  _erase_typed callout "$@"
  _gap_before callout
  local text=" $* "
  local line
  line=$(printf '%*s' "${#text}" '' | tr ' ' '─')
  printf '\033[1;33m┌%s┐\n│%s│\n└%s┘\033[0m\n' "$line" "$text" "$line"
}

note() {
  _erase_typed note "$@"
  _gap_before note
  printf '\033[1;36m%s\033[0m\n' "$*"
}

#   run_with_notes "<command>" "<note>" "<note>" ...
# Shows the command as if typed, runs it, and prints each note (dimmed) a few seconds apart while it works, so viewers
# know what is happening during a slow step. Then prints the command's output.
# The command runs with no terminal input (</dev/null): a background job that reads the terminal is stopped by the
# shell and never finishes. The work happens in a subshell, which has no job control, so no "[1] 1234" messages appear.
run_with_notes() {
  _erase_typed run_with_notes "$@"
  local command="$1"
  shift
  printf '$ '
  local i
  for ((i = 0; i < ${#command}; i++)); do printf '%s' "${command:$i:1}"; sleep "${TYPE_DELAY:-0.03}"; done
  printf '\n'
  (
    output="$(mktemp)"
    bash -c "$command" </dev/null >"$output" 2>&1 &
    job=$!
    for note in "$@"; do
      sleep "${NOTE_INTERVAL:-5}"
      kill -0 "$job" 2>/dev/null || break
      printf '\033[2;37m%s\033[0m\n' "$note"
    done
    wait "$job"
    cat "$output"
    rm -f "$output"
  )
}

#   show <file, or part of its name> [first line] [last line] [label]
# Prints part of a file with line numbers, under a dimmed header naming the file (or the label given). Searches
# lessons/, build-logic/ and gradle/ when given part of a name. Run it off-camera: it erases its own command line, so viewers
# see only the code.
show() {
  _erase_typed show "$@"
  local target="$1" from="${2:-1}" to="${3:-0}" label="${4:-}" file
  if [ -f "$target" ]; then
    file="$target"
  else
    # An exact file name wins over a partial match, so "DiscountCalculator" never picks DiscountCalculatorTest.
    file=$(find lessons build-logic gradle \( -name "$target.java" -o -name "$target" \) -type f 2>/dev/null \
      | grep -v '/build/' | head -1)
    [ -n "$file" ] || file=$(find lessons build-logic gradle -name "*$target*" -type f 2>/dev/null \
      | grep -v '/build/' | head -1)
  fi
  [ -n "$file" ] || { echo "show: nothing found for $target" >&2; return 1; }
  _gap_before show
  printf '\033[1;35m── %s ──\033[0m\n' "${label:-${file##*/}}"
  awk -v f="$from" -v t="$to" 'NR >= f && (t == 0 || NR <= t) { printf "\033[2;37m%3d\033[0m  %s\n", NR, $0 }' "$file"
}

#   run_live "<command>"
# Shows the command as typed and runs it live, on camera, with its own progress output. It records how long the run
# took (one line per run, in seconds) in the file named by VIDEO_TIMINGS, so captions and voice can be timed to the
# real recording. Call it off-camera and Show straight after its Enter.
run_live() {
  _erase_typed run_live "$@"
  _gap_before live
  printf '$ %s\n' "$1"
  local start end
  start=$(perl -MTime::HiRes=time -e 'printf "%.3f", time')
  bash -c "$1" </dev/null
  end=$(perl -MTime::HiRes=time -e 'printf "%.3f", time')
  [ -n "${VIDEO_TIMINGS:-}" ] && perl -e "printf \"%.3f\\n\", $end - $start" >>"$VIDEO_TIMINGS"
  _VIDEO_LAST=live
}
