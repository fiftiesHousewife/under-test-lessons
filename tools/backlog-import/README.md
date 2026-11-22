# Backlog importer

Loads [the lessons backlog](../../plan/lessons-backlog.yaml) into this repo's GitHub issues, and writes
[the backlog page](../../docs/BACKLOG.md). After import, the issues are the list people read; change the backlog file
and sync, never the issues or the page by hand (see
[Lesson work is tracked in GitHub issues, loaded from a backlog file](../../docs/decisions/0022-lesson-work-is-tracked-in-github-issues-loaded-from-a-backlog-file.md)).
Copied from the toolkit's importer, [under-test](https://github.com/fiftiesHousewife/under-test)'s `tools/backlog-import`;
the file follows [schema/backlog.schema.json](../../schema/backlog.schema.json).

```bash
export UNDERTEST_GITHUB_TOKEN=...                        # a token that can write issues
export GITHUB_API_URL=https://github.example.com/api/v3  # only for an internal GitHub server
python3 tools/backlog-import/import_to_github.py OWNER/REPO --page docs/BACKLOG.md           # dry run, and the page
python3 tools/backlog-import/import_to_github.py OWNER/REPO --apply --page docs/BACKLOG.md   # create or update issues
```

Re-running updates issues by key rather than duplicating them; an item removed from the file is closed as not planned,
and one marked `status: "done"` is closed as completed. Maintainers push with
[maintainer-tools/sync/sync.sh](../../maintainer-tools/sync/sync.sh), which runs this before pushing.
