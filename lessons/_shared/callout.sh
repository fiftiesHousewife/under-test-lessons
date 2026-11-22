# Helpers for lesson videos: draw attention to important text in the terminal.
# Run them while the recording is hidden (Hide ... Show); each erases its own command line first, so viewers see only
# the result.
#   callout "Only 5 of 13 planted bugs were caught"   -> a bold, coloured box around the message
#   note "The *weak* tests"                           -> a line of narration (caption text on screen), in cyan;
#                                                        *word* is highlighted in yellow
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
  # *word* is highlighted in yellow, for the words a scene turns on (weak, strong).
  printf '\033[1;36m%s\033[0m\n' "$*" | sed -E $'s/\\*([^*]+)\\*/\033[1;33m\\1\033[1;36m/g'
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

#   show <file, or part of its name> <first line> <last line> [label]
#   show <file, or part of its name> /pattern/ [label]
# Prints part of a file with line numbers, under a header naming the file (or the label given). Given line numbers, it
# prints those lines. Given a /pattern/, it prints the context a viewer needs: for a .toml file, every matching line
# under its [section] header, with a gap marker between; for other files, the whole { ... } block around the first
# matching line. Searches lessons/, build-logic/ and gradle/ when given part of a name. Run it off-camera: it erases
# its own command line, so viewers see only the code.
# Prints one numbered line; lines named by --mark (a list such as 22,24 or 22-24) are drawn black on yellow.
_SHOW_AWK='
  BEGIN { n = split(m, parts, ","); for (i = 1; i <= n; i++) { if (split(parts[i], r, "-") == 2) { for (j = r[1]; j <= r[2]; j++) mark[j] = 1 } else if (parts[i] != "") mark[parts[i] + 0] = 1 } }
  function out(number, text) {
    if (number in mark) printf "\033[30;43m%3d  %s\033[0m\n", number, text
    else printf "\033[2;37m%3d\033[0m  %s\n", number, text
  }'

# The terminal row the cursor is on (from 1), asked of the terminal itself; fails where there is no terminal to ask.
_cursor_row() {
  local reply
  [ -t 0 ] || return 1
  IFS= read -rsd R -t 2 -p $'\e[6n' reply || return 1
  reply="${reply#*[}"
  echo "${reply%%;*}"
}

# After show --zoom has printed its lines: writes the screen rows of the first and last zoomed line, and the rows on
# screen, to $VIDEO_ZOOMS, one line per zoom, for the video's zoom. If the rows can't be found it writes "unknown",
# so zooms still pair up with the script's in order.
_report_zoom() { # _report_zoom "<printed lines>" "<first>-<last>"
  [ -n "${VIDEO_ZOOMS:-}" ] || return 0
  local printed="$1" first="${2%%-*}" last="${2##*-}" row screen
  screen="${LINES:-$(tput lines 2>/dev/null || echo 0)}"
  if ! row="$(_cursor_row)" || [ "$screen" -le 0 ]; then echo unknown >> "$VIDEO_ZOOMS"; return 0; fi
  # The cursor sits on the row after the last printed line, so the k-th of n lines is on row - 1 - (n - k).
  printf '%s\n' "$printed" | sed -E $'s/\x1b\\[[0-9;]*[A-Za-z]//g' | awk -v f="$first" -v l="$last" -v r="$row" -v s="$screen" '
    { n++; if (match($0, /^ *[0-9]+/)) { number = substr($0, RSTART, RLENGTH) + 0
        if (number >= f && !a) a = n
        if (number <= l) b = n } }
    END { if (a && b) print r - 1 - (n - a), r - 1 - (n - b), s; else print "unknown" }' >> "$VIDEO_ZOOMS"
}

show() {
  _erase_typed show "$@"
  local marks="" zoom="" args=()
  while [ $# -gt 0 ]; do
    case "$1" in
      --mark) marks="$2"; shift 2 ;;
      --zoom) zoom="$2"; marks="${marks:+$marks,}$2"; shift 2 ;;
      *) args+=("$1"); shift ;;
    esac
  done
  set -- "${args[@]}"
  local target="$1" file
  if [ -f "$target" ]; then
    file="$target"
  else
    # An exact file name wins over a partial match, so "DiscountCalculator" never picks DiscountCalculatorTest.
    # Search the lesson's own folder first, so a file name used in two lessons never shows the other lesson's file.
    local places="lessons/${LESSON:-} lessons/_shared build-logic gradle"
    file=$(find $places \( -name "$target.java" -o -name "$target" \) -type f 2>/dev/null \
      | grep -v '/build/' | head -1)
    [ -n "$file" ] || file=$(find $places -name "*$target*" -type f 2>/dev/null \
      | grep -v '/build/' | head -1)
  fi
  [ -n "$file" ] || { echo "show: nothing found for $target" >&2; return 1; }
  local printed
  if [[ "${2:-}" == /*/ ]]; then
    _show_header "${3:-${file##*/}}"
    printed="$(_show_pattern "$file" "${2:1:${#2}-2}" "$marks")"
  else
    _show_header "${4:-${file##*/}}"
    printed="$(awk -v f="${2:-1}" -v t="${3:-0}" -v m="$marks" "$_SHOW_AWK"'
      NR >= f && (t == 0 || NR <= t) { out(NR, $0) }' "$file")"
  fi
  printf '%s\n' "$printed"
  [ -z "$zoom" ] || _report_zoom "$printed" "$zoom"
}

_show_header() {
  _gap_before show
  printf '\033[1;35m── %s ──\033[0m\n' "$1"
}

# The lines a pattern needs to make sense: in a .toml file, each match under its [section]; elsewhere, the whole
# { ... } block around the first match.
_show_pattern() {
  local file="$1" pattern="$2" marks="${3:-}"
  if [[ "$file" == *.toml ]]; then
    awk -v p="$pattern" -v m="$marks" "$_SHOW_AWK"'
      /^\[/ { section = $0; sectionLine = NR }
      $0 ~ p {
        if (sectionLine != shownSection) {
          if (shownAny) print "\033[2;37m  ⋮\033[0m"
          out(sectionLine, section); shownSection = sectionLine; last = sectionLine
        }
        if (NR > last + 1) print "\033[2;37m  ⋮\033[0m"
        out(NR, $0); last = NR; shownAny = 1
      }' "$file"
  else
    awk -v p="$pattern" -v m="$marks" "$_SHOW_AWK"'
      { line[NR] = $0 }
      !hit && $0 ~ p { hit = NR }
      END {
        if (!hit) exit
        indent = match(line[hit], /[^ ]/) - 1
        start = hit
        if (line[hit] !~ /\{[ \t]*$/) {
          for (start = hit; start > 1; start--) {
            i = match(line[start], /[^ ]/) - 1
            if (i < indent && line[start] ~ /\{[ \t]*$/) break
          }
        }
        open = match(line[start], /[^ ]/) - 1
        for (end = hit; end <= NR; end++) {
          i = match(line[end], /[^ ]/) - 1
          if (end > start && i == open && line[end] ~ /^[ \t]*\}/) break
        }
        for (n = start; n <= end && n <= NR; n++) out(n, line[n])
      }' "$file"
  fi
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
