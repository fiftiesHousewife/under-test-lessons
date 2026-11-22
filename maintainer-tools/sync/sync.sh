#!/usr/bin/env bash
# For lessons maintainers: the only way to push. Checks the build, commits, brings this repo's GitHub issues up to date
# with plan/lessons-backlog.yaml, refreshes docs/BACKLOG.md (a readable, ordered table of the open issues), commits
# that, and only then pushes, once. Copied from the toolkit's maintainer-tools/sync/sync.sh.
#
#   maintainer-tools/sync/sync.sh "Commit message"
#
# Needs: JDK 25, git, Python 3, and a token that can write issues in UNDERTEST_GITHUB_TOKEN. If that isn't set and the
# GitHub CLI happens to be logged in, its token is borrowed. Safe to re-run: every step skips what is already done.
set -euo pipefail
cd "$(git rev-parse --show-toplevel)"
MESSAGE="${1:?usage: maintainer-tools/sync/sync.sh \"Commit message\"}"
REPO="$(git remote get-url origin | sed -E 's#(git@[^:]+:|https://[^/]+/)##; s#\.git$##')"
COAUTHOR='Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>'

echo "== Waiting for any earlier issue import to finish"
while pgrep -f "import_to_github.py" >/dev/null; do sleep 10; done

echo "== Build"
./gradlew --stop >/dev/null 2>&1 || true
./gradlew check

echo "== Commit"
git add -A -- . ':!.idea'
git diff --cached --quiet || git commit -q -m "$MESSAGE" -m "$COAUTHOR"

echo "== Issues and backlog page (before pushing)"
if [ -z "${UNDERTEST_GITHUB_TOKEN:-}" ] && command -v gh >/dev/null && env -u GITHUB_TOKEN gh auth status >/dev/null 2>&1; then
  UNDERTEST_GITHUB_TOKEN="$(env -u GITHUB_TOKEN gh auth token)"
fi
export UNDERTEST_GITHUB_TOKEN="${UNDERTEST_GITHUB_TOKEN:?set UNDERTEST_GITHUB_TOKEN to a token that can write issues}"
python3 -u tools/backlog-import/import_to_github.py "$REPO" --apply --page docs/BACKLOG.md
git add docs/BACKLOG.md
git diff --cached --quiet || git commit -q -m "Refresh backlog page" -m "$COAUTHOR"

echo "== Push"
LESSONS_SYNC=1 git push
echo "== Done"
