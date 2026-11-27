"""Runs each live command in the lessons' scene files and keeps what it printed, for the decks and the checks.

    python3 tools/capture-lesson-outputs.py              every lesson
    python3 tools/capture-lesson-outputs.py <lesson id>  one lesson
    python3 tools/capture-lesson-outputs.py --list       the commands, without running them
    python3 tools/capture-lesson-outputs.py --from-kept <lesson id>   casts from the outputs kept, without running

Each live command (run_live or run_with_notes in lessons/<id>/media/<id>.scenes) runs from the repository root, and
its output is kept in lessons/<id>/media/<id>.outputs/01.txt, 02.txt and so on, the command on
the first line. A README's exercise answer command (-Pcode=answer) is kept as answer.txt beside them. Output goes
through lessons/_shared/screen-output.pl. Colour codes, notices the JVM or a sandbox
adds, and Gradle's closing time line are left out. A run that couldn't start, or whose output still shows a personal
path after paths are made machine-independent, isn't kept: the script stops and says why.

It runs the commands itself, one after another, rather than from inside a Gradle build: a Gradle build that starts
more Gradle builds of the same project fails on the project's own lock files.
"""
import os
import re
import json
import subprocess
import time
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SCREEN_FILTER = os.path.join(ROOT, "lessons", "_shared", "screen-output.pl")
# The README's command for a lesson's exercise answer, which no scene runs: kept as answer.txt.
ANSWER = re.compile(r"^(\./gradlew :lessons:\S+[^#]*-Pcode=answer)\b")
LIVE = re.compile(r"^(?:run_live|run_with_notes)\s+\"([^\"]+)\"")
ESCAPE = re.compile(r"\x1b\[[0-9;?]*[A-Za-z]")
NOISE = ("Picked up JAVA_TOOL_OPTIONS", "Caught exception: Could not", "BUILD SUCCESSFUL in ", "BUILD FAILED in ")
PERSONAL_PATH = re.compile(r"/Users/[^/\s]+|/home/[^/\s]+|[A-Z]:\\Users\\")
# Quiet Gradle, so the output shows what matters, and no Gradle daemon: each run is its
# own process, so one left over from another session, or a sandbox, can't get in the way. The commands stay as written.
QUIET = (" -Dorg.gradle.warning.mode=none -Dorg.gradle.configuration-cache=false -Dorg.gradle.logging.level=quiet"
         " -Dorg.gradle.daemon=false")


def commands(lesson):
    """The lesson's live commands, in scene file order."""
    scenes = os.path.join(ROOT, "lessons", lesson, "media", lesson + ".scenes")
    with open(scenes, encoding="utf-8") as f:
        return [m.group(1) for m in (LIVE.match(line.strip()) for line in f) if m]


def on_screen(output):
    """The output as a viewer sees it on screen: through lessons/_shared/screen-output.pl."""
    return subprocess.run(["perl", SCREEN_FILTER], input=output, stdout=subprocess.PIPE, text=True, check=True).stdout


def clean(output):
    """The output as a viewer sees it: no colour codes, no notices, no blank lines at either end, and paths that read
    the same on any machine (the repository's own folder made relative, any home folder shown as ~)."""
    text = ESCAPE.sub("", output).replace(ROOT + os.sep, "").replace(ROOT, ".")
    text = text.replace(os.path.expanduser("~"), "~")
    lines = [line.rstrip() for line in text.splitlines() if not line.startswith(NOISE)]
    return "\n".join(lines).strip()


def problem(output):
    """Why a run's output can't be kept, or None."""
    if "Gradle could not start your build" in output:
        return "Gradle could not start the build"
    for line in output.splitlines():
        if PERSONAL_PATH.search(line):
            return "its output shows a personal path: " + PERSONAL_PATH.sub("~", line).strip()[:160]
    return None


def answer_command(lesson):
    """The command the lesson's README gives for its exercise answer (-Pcode=answer), or None."""
    readme = os.path.join(ROOT, "lessons", lesson, "README.md")
    if not os.path.isfile(readme):
        return None
    with open(readme, encoding="utf-8") as f:
        for line in f:
            found = ANSWER.match(line.strip())
            if found:
                return " ".join(found.group(1).split())
    return None


def run_timed(command, environment):
    """Runs a command and returns its output, and each output line with the seconds since the start it appeared."""
    start = time.monotonic()
    process = subprocess.Popen(["bash", "-c", command], cwd=ROOT, env=environment, stdin=subprocess.DEVNULL,
                               stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True, bufsize=1)
    timed = [(time.monotonic() - start, line) for line in process.stdout]
    process.wait()
    return "".join(line for _, line in timed), timed


COLOURS = ((re.compile(r"\bPASSED\b"), "32"), (re.compile(r"\bFAILED\b"), "31"), (re.compile(r"\bSKIPPED\b"), "33"))
SUMMARY = re.compile(r"^(All \d+ tests? passed\.|\d+ tests? passed\.|\d+ of \d+ tests? passed.*|\d+ tests? completed, \d+ failed.*|No tests ran.*)$")


def coloured(line):
    """A line as a terminal colours it: passed green, failed red, skipped yellow, and the summary in bold."""
    for word, colour in COLOURS:
        line = word.sub(lambda m: f"\x1b[{colour}m{m.group(0)}\x1b[0m", line)
    return f"\x1b[1m{line}\x1b[0m" if SUMMARY.match(line) else line


def cast(command, output, timed):
    """The run as an asciicast (version 2, asciinema's format): the command, then each line the viewer sees, at the time
    it appeared, so a deck can play the run as it happened. A kept line takes the time of the first raw line that reads
    the same; the decks shorten long waits when they play it."""
    seconds, at, events = 0.0, 0, [[0.0, "o", "$ " + command + "\r\n"]]
    raw = [(t, clean(line)) for t, line in timed]
    for line in output.splitlines():
        for k in range(at, len(raw)):
            if raw[k][1] and (raw[k][1] == line or line.startswith(raw[k][1]) or raw[k][1].startswith(line)):
                seconds, at = max(seconds, raw[k][0]), k + 1
                break
        events.append([round(seconds + 0.01 * len(events), 3), "o", coloured(line) + "\r\n"])
    width = min(160, max(80, max((len(line) for line in output.splitlines()), default=80) + 2))
    header = {"version": 2, "width": width, "height": min(36, len(output.splitlines()) + 3),
              "env": {"TERM": "xterm-256color"}, "title": command}
    return "\n".join(json.dumps(item) for item in [header] + events) + "\n"


def keep(command, path):
    """Runs a command and keeps what it showed, as text and as a cast to play in the deck, or
    stops and says why it can't be kept."""
    environment = dict(os.environ, GRADLE_OPTS=os.environ.get("GRADLE_OPTS", "") + QUIET)
    printed, timed = run_timed(command, environment)
    output = clean(on_screen(printed))
    reason = problem(output)
    if reason:
        message = f"`{command}` didn't run properly, so its output isn't kept: {reason}"
        if os.environ.get("GITHUB_ACTIONS"):
            print(f"::error title=Live command didn't run::{message}")  # shown as an annotation on the run
        sys.exit(message)
    with open(path, "w", encoding="utf-8") as f:
        f.write(f"$ {command}\n{output}\n")
    with open(path[:-len(".txt")] + ".cast", "w", encoding="utf-8") as f:
        f.write(cast(command, output, timed))


def capture(lesson):
    folder = os.path.join(ROOT, "lessons", lesson, "media", lesson + ".outputs")
    os.makedirs(folder, exist_ok=True)
    for index, command in enumerate(commands(lesson), 1):
        keep(command, os.path.join(folder, f"{index:02d}.txt"))
    answer = answer_command(lesson)
    if answer:
        keep(answer, os.path.join(folder, "answer.txt"))
    answered = ", and the exercise answer's" if answer else ""
    print(f"{lesson}: output of {len(commands(lesson))} live commands kept{answered}")


def lessons():
    found = []
    for lesson in sorted(os.listdir(os.path.join(ROOT, "lessons"))):
        if os.path.isfile(os.path.join(ROOT, "lessons", lesson, "media", lesson + ".scenes")):
            found.append(lesson)
    return found


def from_kept(lesson):
    """Casts made from the outputs already kept, for runs that can't be repeated here (a lesson that needs a container
    runtime, say): each line follows the last at an even pace, after a second's start."""
    folder = os.path.join(ROOT, "lessons", lesson, "media", lesson + ".outputs")
    made = 0
    for name in sorted(os.listdir(folder)):
        if not name.endswith(".txt"):
            continue
        with open(os.path.join(folder, name), encoding="utf-8") as f:
            lines = f.read().rstrip("\n").split("\n")
        command = lines[0][2:] if lines[0].startswith("$ ") else lines[0]
        timed = [(1.0 + 0.15 * i, line + "\n") for i, line in enumerate(lines[1:])]
        with open(os.path.join(folder, name[:-len(".txt")] + ".cast"), "w", encoding="utf-8") as f:
            f.write(cast(command, "\n".join(lines[1:]), timed))
        made += 1
    print(f"{lesson}: {made} casts made from the kept outputs")


def main(args):
    if "--from-kept" in args:
        for lesson in [a for a in args if not a.startswith("-")]:
            from_kept(lesson)
        return
    if "--list" in args:
        for lesson in lessons():
            for command in commands(lesson):
                print(f"{lesson}: {command}")
        return
    for lesson in [a for a in args if not a.startswith("-")] or lessons():
        capture(lesson)


if __name__ == "__main__":
    main(sys.argv[1:])
