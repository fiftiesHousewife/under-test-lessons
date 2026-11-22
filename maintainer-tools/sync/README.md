# Sync (for lessons maintainers)

Always push this way:

```bash
maintainer-tools/sync/sync.sh "What changed"
```

In order, it checks the build, commits, brings this repo's GitHub issues up to date with
[the lessons backlog](../../plan/lessons-backlog.yaml), refreshes [the backlog page](../../docs/BACKLOG.md) (an ordered,
readable table of the open issues), commits that, and only then pushes, once. Set `UNDERTEST_GITHUB_TOKEN` to a token
that can write issues. Copied from the toolkit's sync.

To make a plain `git push` refuse to run, turn on the repo's git hook once per clone:

```bash
git config core.hooksPath maintainer-tools/git-hooks
```
