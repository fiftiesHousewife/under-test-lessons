"""Gathers every lesson's slide deck into one folder for publishing, with an index page to work through them in order.

    python3 tools/publish-decks.py <out folder>

The index follows the catalogue (lessons/CATALOGUE.md): its groups, and the built lessons in each, in order, marked
where a deck has its voice. Each deck becomes <out>/<lesson id>.html, with its voice clips (<lesson id>.deck-voice/)
beside it, and gains series.js: its place in the series ("Lesson 7 of 38 · <group>"), links back to the index and on
to the next lesson, and, while you listen, the next lesson carries on when one ends. The decks themselves are not changed in the repository.
"""
import html
import os
import re
import shutil
import sys

CATALOGUE = os.path.join("lessons", "CATALOGUE.md")
SERIES_JS = """// Added to each published deck: links back to the index and on to the next lesson, and, unless muted, the next
// lesson carries on when this one's last slide has been read.
(function () {
  var me = document.currentScript;
  var previous = me.getAttribute('data-previous');
  var next = me.getAttribute('data-next');
  var place = me.getAttribute('data-place');
  if (place) {
    var where = document.createElement('div');
    where.className = 'series-place';
    where.textContent = place;
    document.body.appendChild(where);
  }
  var nav = document.createElement('nav');
  nav.className = 'series-links';
  nav.innerHTML = (previous ? '<a href="' + previous + '.html#1">\u2039 Previous lesson</a>' : '')
    + '<a href="index.html">All lessons</a>'
    + (next ? '<a href="' + next + '.html#1">Next lesson \u203a</a>' : '');
  document.body.appendChild(nav);
  if (next) {
    document.addEventListener('deck-voice-finished', function () {
      setTimeout(function () { location.href = next + '.html#1'; }, 1500);
    });
  }
}());
"""


def lessons_in_order():
    """The catalogue's groups, each with its built lessons in order, as (group title, [(lesson id, name)])."""
    groups, current = [], None
    with open(CATALOGUE, encoding="utf-8") as f:
        for line in f:
            heading = re.match(r"^## (.+)$", line)
            if heading:
                current = (heading.group(1).strip(), [])
                groups.append(current)
                continue
            row = re.match(r"^\| \[([^\]]+)\]\(([a-z0-9-]+)/README\.md\)", line)
            if row and current is not None:
                current[1].append((row.group(2), row.group(1)))
    seen, ordered = set(), []
    for title, lessons in groups:
        fresh = [(lesson, name) for lesson, name in lessons if lesson not in seen and deck_of(lesson)]
        seen.update(lesson for lesson, _ in fresh)
        if fresh:
            ordered.append((title, fresh))
    return ordered


def deck_of(lesson):
    path = os.path.join("lessons", lesson, "media", lesson + ".deck.html")
    return path if os.path.isfile(path) else None


def voice_of(lesson):
    path = os.path.join("lessons", lesson, "media", lesson + ".deck-voice")
    return path if os.path.isdir(path) else None


def place_label(number, total, group):
    """Where a deck sits in the series, as its page shows it: "Lesson 7 of 38 · Stand in for the systems around it"."""
    return f"Lesson {number} of {total} · {group}"


def publish_deck(lesson, neighbours, out, place=""):
    """Writes one deck with its series links; neighbours is (the lesson before it, the lesson after it), either None."""
    before, following = neighbours
    with open(deck_of(lesson), encoding="utf-8") as f:
        page = f.read()
    # Links to other lessons point at their deck files in the repository; published, each deck is <id>.html.
    page = re.sub(r'href="\.\./\.\./([a-z0-9-]+)/media/\1\.deck\.html', r'href="\1.html', page)
    tag = (f'<script src="series.js" data-previous="{html.escape(before or "")}"'
           f' data-next="{html.escape(following or "")}"'
           f' data-place="{html.escape(place)}"></script>')
    page = page.replace("</body>", tag + "</body>", 1)
    with open(os.path.join(out, lesson + ".html"), "w", encoding="utf-8") as f:
        f.write(page)
    if voice_of(lesson):
        shutil.copytree(voice_of(lesson), os.path.join(out, lesson + ".deck-voice"), dirs_exist_ok=True)
    clips = os.path.join("lessons", lesson, "media", lesson + ".deck-clips")
    if os.path.isdir(clips):
        shutil.copytree(clips, os.path.join(out, lesson + ".deck-clips"), dirs_exist_ok=True)


def main(out):
    os.makedirs(out, exist_ok=True)
    groups = lessons_in_order()
    order = [lesson for _, lessons in groups for lesson, _ in lessons]
    group_of = {lesson: title for title, lessons in groups for lesson, _ in lessons}
    for i, lesson in enumerate(order):
        neighbours = (order[i - 1] if i > 0 else None, order[i + 1] if i + 1 < len(order) else None)
        publish_deck(lesson, neighbours, out,
                     place_label(i + 1, len(order), group_of[lesson]))
    with open(os.path.join(out, "series.js"), "w", encoding="utf-8") as f:
        f.write(SERIES_JS)
    sections = []
    for title, lessons in groups:
        items = "".join(
            f'<li><a href="{lesson}.html#1">{html.escape(name)}</a>'
            + "</li>" for lesson, name in lessons)
        sections.append(f"<h2>{html.escape(title)}</h2><ol>{items}</ol>")
    first = order[0] if order else ""
    with open(os.path.join(out, "index.html"), "w", encoding="utf-8") as f:
        f.write('<!doctype html><html lang="en"><head><meta charset="utf-8">'
                '<meta name="viewport" content="width=device-width, initial-scale=1"><title>Lesson slide decks</title>'
                '<style>body{font:18px/1.6 Georgia,serif;background:#f3efe4;color:#111;max-width:44rem;'
                'margin:2rem auto;padding:0 16px}h1,h2{font-family:Helvetica,Arial,sans-serif;text-transform:uppercase}'
                'h2{font-size:1rem;margin-top:2rem}a{color:#111}a.listen{font:14px Helvetica,Arial,sans-serif;'
                'margin-left:.5rem}.start{font:16px Helvetica,Arial,sans-serif}</style></head><body>'
                '<h1>Lesson slide decks</h1>'
                '<p>The lessons in the catalogue\'s order. Arrow keys move between slides, N shows the speaker notes, '
                'and each deck reads its slides aloud as it plays, then carries on into the next lesson. Mute (or V) '
                'stops the voice.</p>'
                + (f'<p class="start"><a href="{first}.html#1">Start the series from the beginning</a>'
                   '</p>' if first else "")
                + "".join(sections) + "</body></html>\n")
    print(f"{len(order)} decks gathered into {out}, in the catalogue's order")


if __name__ == "__main__":
    main(sys.argv[1])
