"""Installs the AI assistant skills that apm.yml declares, pinned, into .claude/skills/, so a fresh clone has them.

    python3 tools/install-skills.py              install every skill
    python3 tools/install-skills.py --if-missing install only if a skill is missing (the Claude Code start hook)
    python3 tools/install-skills.py --target DIR install into DIR instead of .claude/skills

With APM (the Agent Package Manager) installed, this runs `apm install`. Without it, each skill folder is fetched with
git over HTTPS at the tag or commit apm.yml pins, from https://github.com, or from a company's own mirror of GitHub
named by the SKILLS_GIT_BASE environment variable (for example https://git.example.com/github-mirror).
"""
import os
import re
import shutil
import subprocess
import sys
import tempfile

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))


def pins(manifest):
    """Each skill in apm.yml as (repository, folder in it, ref): the short form owner/repo/path#ref, or git:, path:
    and ref: lines."""
    found = []
    current = None
    for raw in manifest.splitlines():
        line = raw.split(" #", 1)[0].rstrip()
        short = re.match(r"^\s*-\s+([\w.-]+/[\w.-]+)/(\S+)#(\S+)$", line)
        start = re.match(r"^\s*-\s+git:\s*(\S+)$", line)
        field = re.match(r"^\s+(path|ref):\s*(\S+)$", line)
        if short:
            found.append(short.groups())
            current = None
        elif start:
            current = {"git": start.group(1)}
            found.append(current)
        elif field and isinstance(current, dict):
            current[field.group(1)] = field.group(2)
    return [p if isinstance(p, tuple) else (p["git"], p["path"], p["ref"]) for p in found]


def name(pin):
    return pin[1].rstrip("/").split("/")[-1]


def fetch(pin, target):
    """Copies one pinned skill folder into target/<name>, fetched with git over HTTPS."""
    repo, path, ref = pin
    base = os.environ.get("SKILLS_GIT_BASE", "https://github.com").rstrip("/")
    with tempfile.TemporaryDirectory() as work:
        def git(*args):
            subprocess.run(["git", "-C", work, *args], check=True, capture_output=True)
        git("init", "-q")
        git("remote", "add", "origin", f"{base}/{repo}.git")
        git("fetch", "-q", "--depth", "1", "origin", ref)
        git("checkout", "-q", "FETCH_HEAD", "--", path)
        destination = os.path.join(target, name(pin))
        shutil.rmtree(destination, ignore_errors=True)
        shutil.copytree(os.path.join(work, path), destination)


def main(args):
    with open(os.path.join(ROOT, "apm.yml"), encoding="utf-8") as f:
        declared = pins(f.read())
    target = args[args.index("--target") + 1] if "--target" in args else os.path.join(ROOT, ".claude", "skills")
    missing = [p for p in declared if not os.path.isfile(os.path.join(target, name(p), "SKILL.md"))]
    if "--if-missing" in args and not missing:
        return
    if shutil.which("apm") and "--target" not in args:
        subprocess.run(["apm", "install"], cwd=ROOT, check=True)
        return
    os.makedirs(target, exist_ok=True)
    for pin in declared:
        fetch(pin, target)
    print(f"{len(declared)} skills installed into {os.path.relpath(target, ROOT)}: "
          + ", ".join(name(p) for p in declared))


if __name__ == "__main__":
    main(sys.argv[1:])
