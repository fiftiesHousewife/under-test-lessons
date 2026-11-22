"""Turns each "What went wrong" section of a Gradle log, each problem the lesson checks name (a line naming a lesson's
file, then what is wrong with it), and each failed test in the test results, into a GitHub annotation, so a failed
build says why on its page, and through the API, without the log being downloaded.

    python3 tools/build-failure-annotations.py <gradle log> [<folder to search for test results>]
"""
import glob
import os
import sys
import re
import xml.etree.ElementTree as ElementTree

# A problem the lesson checks print: a file of a lesson, then what is wrong with it.
LESSON_PROBLEM = re.compile(r"(lessons/[^\s:]+\.(?:tape|md|yaml)): (.+)")

LIMIT = 4000  # GitHub cuts an annotation's message near here


def reasons(lines):
    """The text of each "What went wrong" section, up to its "Try" section."""
    found, current = [], None
    for line in lines:
        if line.startswith("* What went wrong:"):
            current = []
            found.append(current)
        elif line.startswith("* Try:"):
            current = None
        elif current is not None and line.strip():
            current.append(line.rstrip())
    return ["\n".join(reason) for reason in found if reason]


def failed_tests(folder):
    """Each failed test in the JUnit XML results under folder, as "class > test: message"."""
    found = []
    pattern = os.path.join(folder, "**", "build", "test-results", "*", "TEST-*.xml")
    for result in sorted(glob.glob(pattern, recursive=True)):
        for case in ElementTree.parse(result).getroot().iter("testcase"):
            for failure in list(case.iter("failure")) + list(case.iter("error")):
                message = (failure.get("message") or (failure.text or "").strip().split("\n")[0])[:1000]
                found.append(f"{case.get('classname')} > {case.get('name')}: {message}")
    return found


def annotation(reason, title="Why the checks failed"):
    """A GitHub error annotation; its message keeps its lines, written as GitHub's %0A."""
    text = reason[:LIMIT].replace("%", "%25").replace("\r", "").replace("\n", "%0A")
    return f"::error title={title}::{text}"


def lesson_problems(lines):
    """Each problem the lesson checks printed, as "file: what is wrong", once each."""
    found = []
    for line in lines:
        problem = LESSON_PROBLEM.search(line)
        if problem and problem.group(0) not in found:
            found.append(problem.group(0))
    return found


def main(path, folder=None):
    with open(path, encoding="utf-8", errors="replace") as f:
        lines = f.readlines()
    for reason in reasons(lines):
        print(annotation(reason))
    for problem in lesson_problems(lines):
        print(annotation(problem, "A lesson check failed"))
    if folder:
        for test in failed_tests(folder):
            print(annotation(test, "A test failed"))


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2] if len(sys.argv) > 2 else None)
