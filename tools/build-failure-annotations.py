"""Turns each "What went wrong" section of a Gradle log into a GitHub annotation, so a failed build says why on its
page, and through the API, without the log being downloaded.

    python3 tools/build-failure-annotations.py <gradle log>
"""
import sys

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


def annotation(reason):
    """A GitHub error annotation; its message keeps its lines, written as GitHub's %0A."""
    text = reason[:LIMIT].replace("%", "%25").replace("\r", "").replace("\n", "%0A")
    return f"::error title=Why the checks failed::{text}"


def main(path):
    with open(path, encoding="utf-8", errors="replace") as f:
        for reason in reasons(f):
            print(annotation(reason))


if __name__ == "__main__":
    main(sys.argv[1])
