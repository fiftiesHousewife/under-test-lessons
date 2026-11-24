#!/usr/bin/env python3
"""Import the lessons backlog into GitHub Issues, using the GitHub REST API directly.

Copied from the toolkit's importer (under-test, tools/backlog-import), with the lessons backlog as its default and a
page written on a dry run too. Needs only Python 3 (standard library) and a token. No GitHub CLI or other tools.

    python3 tools/backlog-import/import_to_github.py OWNER/REPO                 # dry run of plan/lessons-backlog.yaml
    python3 tools/backlog-import/import_to_github.py OWNER/REPO --page docs/BACKLOG.md
                                                    # dry run that also writes the page, without issue numbers
    python3 tools/backlog-import/import_to_github.py OWNER/REPO --apply --page docs/BACKLOG.md
                                                    # creates and updates issues, then writes the page
                                                    # add --relink to post every issue's links, not only changed ones

Settings, from the environment:
    UNDERTEST_GITHUB_TOKEN (or GH_TOKEN, or GITHUB_TOKEN)   a token with permission to write issues in the repo
    GITHUB_API_URL   API address. Default https://api.github.com; for an internal GitHub server use
                     https://<your-host>/api/v3. A proxy is taken from HTTPS_PROXY as usual.

What it does, in order:
  1. Creates labels for item type, phase, owner role, who does it, and horizon (if missing).
  2. Creates one issue per item, or updates it if an issue with the same key already exists.
     The key is stored in a hidden comment in the issue body, so re-running never duplicates.
  3. Closes items marked status: done as completed, and closes, as not planned, any issue whose item is no longer in
     the file.
  4. Links each item to its parent as a sub-issue.
  5. Adds "blocked by" links between items.
  6. With --page <path>, writes a readable Markdown page of the open items, ordered and linked to their issues.
"""
import http.client
import json
import os
import re
import sys
import time
import urllib.error
import urllib.parse
import urllib.request
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[2]
DEFAULT_SEED = REPO_ROOT / "plan" / "lessons-backlog.yaml"
KEY_MARKER = "<!-- backlog-key: {} -->"
LINKS_MARKER = "<!-- backlog-links: parent={}; blocked-by={} -->"
LABEL_COLOURS = {"type": "5319e7", "phase": "0e8a16", "owner": "1d76db", "done-by": "fbca04", "horizon": "d93f0b"}
PAUSE_SECONDS = 1.5      # GitHub asks for at least a second between requests that create content
RATE_LIMIT_WAIT = 90     # wait this long, then retry, if a rate limit is hit and GitHub gives no time
NETWORK_RETRY_WAIT = 15  # wait this long, then retry, after a dropped connection, timeout or server error


def parse_seed(path):
    """Strict reader for the seed's simple shape: scalar values are JSON, lists are one JSON value per line."""
    items, current, list_key = [], None, None
    for number, line in enumerate(path.read_text(encoding="utf-8").splitlines(), 1):
        if not line.strip() or line.startswith("#") or line == "items:":
            continue
        m = re.fullmatch(r"(  - |    )([a-z_]+):(?: (.*))?", line)
        if m:
            if m.group(1) == "  - ":
                current = {}
                items.append(current)
            key, value = m.group(2), m.group(3)
            if value is None:
                current[key], list_key = [], key
            else:
                current[key], list_key = json.loads(value), None
            continue
        m = re.fullmatch(r"      - (.*)", line)
        if m and list_key:
            current[list_key].append(json.loads(m.group(1)))
            continue
        sys.exit(f"{path}:{number}: unexpected line: {line!r}")
    return items


class GitHub:
    def __init__(self, repo):
        self.base = os.environ.get("GITHUB_API_URL", "https://api.github.com").rstrip("/")
        self.repo = repo
        self.token = (os.environ.get("UNDERTEST_GITHUB_TOKEN") or os.environ.get("GH_TOKEN")
                      or os.environ.get("GITHUB_TOKEN"))

    def request(self, method, path, body=None, writes=False, allow_fail=False):
        url = path if path.startswith("http") else f"{self.base}/repos/{self.repo}/{path}"
        data = json.dumps(body).encode() if body is not None else None
        for _ in range(6):
            req = urllib.request.Request(url, data=data, method=method, headers={
                "Accept": "application/vnd.github+json",
                "X-GitHub-Api-Version": "2022-11-28",
                "Authorization": f"Bearer {self.token}",
                "User-Agent": "under-test-lessons-backlog-import",
                **({"Content-Type": "application/json"} if data else {}),
            })
            try:
                with urllib.request.urlopen(req, timeout=60) as response:
                    payload = response.read()
                    if writes:
                        time.sleep(PAUSE_SECONDS)
                    return (json.loads(payload) if payload else None), response.headers
            except urllib.error.HTTPError as error:
                text = error.read().decode(errors="replace")
                if error.code in (403, 429) and "rate limit" in text.lower():
                    wait = int(error.headers.get("Retry-After") or RATE_LIMIT_WAIT)
                    print(f"    rate limited; waiting {wait}s before retrying")
                    time.sleep(wait)
                    continue
                if error.code >= 500:
                    print(f"    server error {error.code}; retrying in {NETWORK_RETRY_WAIT}s")
                    time.sleep(NETWORK_RETRY_WAIT)
                    continue
                if allow_fail:
                    return None, None
                sys.exit(f"{method} {url} failed with {error.code}:\n{text}")
            except (urllib.error.URLError, http.client.HTTPException, ConnectionError, TimeoutError) as error:
                print(f"    connection problem ({error}); retrying in {NETWORK_RETRY_WAIT}s")
                time.sleep(NETWORK_RETRY_WAIT)
        sys.exit("still failing after several retries; run the importer again later (it carries on where it stopped)")

    def all_pages(self, path):
        url, results = f"{self.base}/repos/{self.repo}/{path}", []
        while url:
            page, headers = self.request("GET", url)
            results += page
            links = headers.get("Link", "") if headers else ""
            m = re.search(r'<([^>]+)>;\s*rel="next"', links)
            url = m.group(1) if m else None
        return results


def labels_for(item):
    return [f"type: {item['type']}", f"phase: {item['phase']}", f"owner: {item['owner_role']}",
            f"done-by: {item['done_by']}"] + ([f"horizon: {item['horizon']}"] if "horizon" in item else [])


def body_for(item, web_base):
    lines = [KEY_MARKER.format(item["key"]), "", "**Done when:**", ""]
    lines += [f"- [ ] {d}" for d in item["done_when"]]
    if "decision_record" in item:
        lines += ["", f"**Decision record:** [{item['title']}]({web_base}/blob/main/{item['decision_record']})"]
    if "notes" in item:
        lines += ["", item["notes"]]
    # Records the links, so a change to them changes the body and the issue is written again.
    lines += ["", LINKS_MARKER.format(item.get("parent", ""), ",".join(item.get("blocked_by", [])))]
    return "\n".join(lines)


def changed(issue, payload):
    """Whether an issue's title, body or labels differ from what the seed says."""
    body = (issue.get("body") or "").replace("\r\n", "\n")
    labels = {label["name"] for label in issue.get("labels", [])}
    return issue.get("title") != payload["title"] or body != payload["body"] or labels != set(payload["labels"])


HORIZON_ORDER = {"now": 0, "next": 1, "later": 2}


def write_page(items, by_key, path):
    """An ordered, readable table of open items, grouped by outcome, linked to their issues (before the first import,
    every item not marked done, without links)."""
    titles = {item["key"]: item["title"] for item in items}
    open_keys = ({key for key, issue in by_key.items() if issue.get("state") == "open"} if by_key
                 else {item["key"] for item in items if item.get("status") != "done"})
    def link(key):
        return f"[{titles[key]}]({by_key[key]['html_url']})" if key in by_key else titles[key]
    def waiting(item):
        return [b for b in item.get("blocked_by", []) if b in open_keys]
    lines = ["# Backlog", "",
             "Open work on the lessons and their tools, generated from the issue tracker by "
             "[tools/backlog-import](../tools/backlog-import/README.md) after each import. Do not edit by hand: change "
             "[the backlog file](../plan/lessons-backlog.yaml) and sync.", "",
             "Ordered by horizon (now, next, later), then items that are ready before items waiting on others.", ""]
    outcomes = [item for item in items if item["type"] == "outcome" and item["key"] in open_keys]
    for outcome in outcomes:
        children = [item for item in items if item.get("parent") == outcome["key"] and item["key"] in open_keys]
        if not children:
            continue
        children.sort(key=lambda item: (HORIZON_ORDER.get(item.get("horizon"), 3), bool(waiting(item)), item["type"] != "decision"))
        lines += [f"## {link(outcome['key'])}", "", "| | Item | Type | Owner | Done by | Waiting on |", "|---|---|---|---|---|---|"]
        for item in children:
            number = f"#{by_key[item['key']]['number']}" if item["key"] in by_key else ""
            blockers = ", ".join(link(b) for b in waiting(item)) or "–"
            lines.append(f"| {number} | {link(item['key'])} | {item['type']} | {item['owner_role']} | {item['done_by']} | {blockers} |")
        lines.append("")
    Path(path).write_text("\n".join(lines), encoding="utf-8")
    print(f"wrote {path}")


def main():
    if len(sys.argv) < 2:
        sys.exit(__doc__)
    for option in ("--page", "--file"):
        if option in sys.argv and sys.argv.index(option) + 1 >= len(sys.argv):
            sys.exit(f"{option} needs a path after it, for example {option} docs/BACKLOG.md")
    repo, apply = sys.argv[1], "--apply" in sys.argv
    seed = REPO_ROOT / sys.argv[sys.argv.index("--file") + 1] if "--file" in sys.argv else DEFAULT_SEED
    items = parse_seed(seed)
    print(f"{len(items)} items in {seed.name}; {'APPLYING to' if apply else 'dry run for'} {repo}")
    if not apply:
        for item in items:
            print(f"  would create or update: {item['title']}")
        if "--page" in sys.argv:
            write_page(items, {}, REPO_ROOT / sys.argv[sys.argv.index("--page") + 1])
        print("dry run complete; add --apply to make these changes")
        return

    github = GitHub(repo)
    if not github.token:
        sys.exit("Set UNDERTEST_GITHUB_TOKEN (or GH_TOKEN / GITHUB_TOKEN) to a token that can write issues.")
    web_base = github.base.replace("://api.github.com", "://github.com").replace("/api/v3", "") + f"/{repo}"

    existing = {label["name"] for label in github.all_pages("labels?per_page=100")}
    for label in sorted({label for item in items for label in labels_for(item)} - existing):
        print(f"  label: {label}")
        github.request("POST", "labels", {"name": label, "color": LABEL_COLOURS[label.split(":")[0]]}, writes=True)

    by_key = {}
    for issue in github.all_pages("issues?state=all&per_page=100"):
        if "pull_request" in issue:
            continue
        m = re.search(r"<!-- backlog-key: ([a-z0-9-]+) -->", issue.get("body") or "")
        if m:
            by_key[m.group(1)] = issue

    # Only issues that differ from the seed are written; each write waits out GitHub's pause between writes.
    written = set()
    for item in items:
        payload = {"title": item["title"], "body": body_for(item, web_base), "labels": labels_for(item)}
        if item["key"] in by_key:
            issue = by_key[item["key"]]
            if not changed(issue, payload):
                continue
            print(f"  update #{issue['number']}: {item['title']}")
            by_key[item["key"]], _ = github.request("PATCH", f"issues/{issue['number']}", payload, writes=True)
        else:
            print(f"  create: {item['title']}")
            by_key[item["key"]], _ = github.request("POST", "issues", payload, writes=True)
        written.add(item["key"])
    print(f"  {len(items) - len(written)} issue(s) unchanged")

    for item in items:
        issue = by_key[item["key"]]
        if item.get("status") == "done" and issue.get("state") == "open":
            print(f"  close as completed #{issue['number']}: {item['title']}")
            by_key[item["key"]], _ = github.request("PATCH", f"issues/{issue['number']}",
                                                    {"state": "closed", "state_reason": "completed"}, writes=True)

    seed_keys = {item["key"] for item in items}
    for key, issue in by_key.items():
        if key not in seed_keys and issue.get("state") == "open":
            print(f"  close as not planned #{issue['number']}: {issue['title']} (no longer in the seed)")
            github.request("PATCH", f"issues/{issue['number']}", {"state": "closed", "state_reason": "not_planned"},
                           writes=True)

    # Links are posted for issues written in this run, or for every issue with --relink. A link already there fails
    # harmlessly; any other failure is named, since an unchanged issue's links aren't tried again without --relink.
    failed = []
    for item in items:
        if item["key"] not in written and "--relink" not in sys.argv:
            continue
        issue = by_key[item["key"]]
        if "parent" in item:
            parent = by_key[item["parent"]]
            _, headers = github.request("POST", f"issues/{parent['number']}/sub_issues", {"sub_issue_id": issue["id"]},
                                        writes=True, allow_fail=True)
            if headers is None:
                failed.append(f"#{issue['number']} as a sub-issue of #{parent['number']}")
        for blocker in item.get("blocked_by", []):
            _, headers = github.request("POST", f"issues/{issue['number']}/dependencies/blocked_by",
                                        {"issue_id": by_key[blocker]["id"]}, writes=True, allow_fail=True)
            if headers is None:
                failed.append(f"#{issue['number']} blocked by #{by_key[blocker]['number']}")
    if failed:
        print(f"  {len(failed)} link(s) not made (some may already exist): {', '.join(failed)}")
        print("  run again with --relink to try every link")
    if "--page" in sys.argv:
        write_page(items, by_key, REPO_ROOT / sys.argv[sys.argv.index("--page") + 1])
    print("done")


if __name__ == "__main__":
    main()
