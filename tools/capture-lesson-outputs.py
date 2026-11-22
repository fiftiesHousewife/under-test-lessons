"""Runs each live command in the lessons' video scripts and keeps what it printed, for the decks and the checks.

    python3 tools/capture-lesson-outputs.py              every lesson
    python3 tools/capture-lesson-outputs.py <lesson id>  one lesson
    python3 tools/capture-lesson-outputs.py --list       the commands, without running them

Each live command (run_live or run_with_notes in lessons/<id>/media/<id>.tape) runs from the repository root, as a
recording runs it, and its output is kept in lessons/<id>/media/<id>.outputs/01.txt, 02.txt and so on, the command on
the first line. Colour codes, notices the JVM or a sandbox adds, and Gradle's closing time line are left out. A run
that couldn't start, or whose output still shows a personal path after paths are made machine-independent, isn't kept:
the script stops and says why.

It runs the commands itself, one after another, rather than from inside a Gradle build: a Gradle build that starts
more Gradle builds of the same project fails on the project's own lock files.
"""
import os
import re
import subprocess
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
LIVE = re.compile(r"^Type(?:@\S+)?\s+[`\"](?:run_live|run_with_notes)\s+\"([^\"]+)\"")
ESCAPE = re.compile(r"\x1b\[[0-9;?]*[A-Za-z]")
NOISE = ("Picked up JAVA_TOOL_OPTIONS", "Caught exception: Could not", "BUILD SUCCESSFUL in ", "BUILD FAILED in ")
PERSONAL_PATH = re.compile(r"/Users/[^/\s]+|/home/[^/\s]+|[A-Z]:\\Users\\")
# The settings a recording makes off camera, so the output matches the video's, and no Gradle daemon: each run is its
# own process, so one left over from another session, or a sandbox, can't get in the way. The commands stay as written.
QUIET = (" -Dorg.gradle.warning.mode=none -Dorg.gradle.configuration-cache=false -Dorg.gradle.logging.level=quiet"
         " -Dorg.gradle.daemon=false")


def commands(lesson):
    """The lesson's live commands, in script order."""
    tape = os.path.join(ROOT, "lessons", lesson, "media", lesson + ".tape")
    with open(tape, encoding="utf-8") as f:
        return [m.group(1) for m in (LIVE.match(line.strip()) for line in f) if m]


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


def capture(lesson):
    folder = os.path.join(ROOT, "lessons", lesson, "media", lesson + ".outputs")
    os.makedirs(folder, exist_ok=True)
    environment = dict(os.environ, GRADLE_OPTS=os.environ.get("GRADLE_OPTS", "") + QUIET)
    for index, command in enumerate(commands(lesson), 1):
        run = subprocess.run(["bash", "-c", command], cwd=ROOT, env=environment, stdin=subprocess.DEVNULL,
                             stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True, check=False)
        output = clean(run.stdout)
        reason = problem(output)
        if reason:
            message = f"`{command}` didn't run properly, so its output isn't kept: {reason}"
            if os.environ.get("GITHUB_ACTIONS"):
                print(f"::error title=Live command didn't run::{message}")  # shown as an annotation on the run
            sys.exit(message)
        with open(os.path.join(folder, f"{index:02d}.txt"), "w", encoding="utf-8") as f:
            f.write(f"$ {command}\n{output}\n")
    print(f"{lesson}: output of {len(commands(lesson))} live commands kept")


def lessons():
    found = []
    for lesson in sorted(os.listdir(os.path.join(ROOT, "lessons"))):
        if os.path.isfile(os.path.join(ROOT, "lessons", lesson, "media", lesson + ".tape")):
            found.append(lesson)
    return found


def main(args):
    if "--list" in args:
        for lesson in lessons():
            for command in commands(lesson):
                print(f"{lesson}: {command}")
        return
    for lesson in [a for a in args if not a.startswith("-")] or lessons():
        capture(lesson)


if __name__ == "__main__":
    main(sys.argv[1:])
