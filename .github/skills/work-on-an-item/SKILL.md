---
name: work-on-an-item
description: Pick up a work item from this repo's backlog (GitHub issues loaded from plan/lessons-backlog.yaml) and finish it. Use when asked to "do the next item", "work the backlog", or to work on a named item.
---

# Working on an item

Copied from the toolkit's skill of the same name, for the lessons.

## 1. Pick an item
- If the person named an item, use it. Otherwise read [the backlog page](../../../docs/BACKLOG.md): take the first
  item under "now" that waits on nothing. Or list open issues through GitHub's REST API, without assuming extra
  command-line tools:
  `curl -s -H "Authorization: Bearer $UNDERTEST_GITHUB_TOKEN" "https://api.github.com/repos/OWNER/REPO/issues?labels=horizon:%20now&state=open"`
  (then skip items with open "blocked by" links).
- Read the item's "done when" list, its notes, and any linked decision record. If it waits on an open item or decision,
  stop and say so.

## 2. Act according to who does it
| Done by | You may | You must not |
|---|---|---|
| **tool** | Build or configure a tool, with tests. Same input gives the same output. Look for an existing tool first. | Put an AI model inside the tool. |
| **ai** | Draft the work (code, lessons, scripts, slides) for the person to review. | Leave it unchecked: finish with `./gradlew check`. |
| **person** | Draft the decision record or document, with options and a recommendation; interview the person with the grilling skill. | Accept a decision, choose between open options, or invent facts. |

## 3. Rules
- Follow [AGENTS.md](../../../AGENTS.md) and the lesson skills it lists.
- Change the backlog only in [plan/lessons-backlog.yaml](../../../plan/lessons-backlog.yaml): mark an item
  `status: "done"` when it is finished, add new items with a key, a parent and what they wait on. Never edit the issues
  or `docs/BACKLOG.md` by hand.

## 4. Finish
1. Run `./gradlew check` and include the result.
2. Mark the item done in the backlog file, and push with `maintainer-tools/sync/sync.sh "What changed"`, which closes
   its issue. Where the sync can't run (no token, or a sandbox), say so and leave the push to the person.
