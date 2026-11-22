"""Gathers every lesson's slide deck into one folder for publishing, with an index page linking them all.

    python3 tools/publish-decks.py <out folder>

Each deck becomes <out>/<lesson id>.html; the index lists them under each lesson's title, from its README.
"""
import html
import os
import re
import shutil
import sys


def title(lesson):
    readme = os.path.join("lessons", lesson, "README.md")
    with open(readme, encoding="utf-8") as f:
        heading = re.search(r"^# (.+)$", f.read(), re.M)
    return heading.group(1) if heading else lesson


def main(out):
    os.makedirs(out, exist_ok=True)
    rows = []
    for lesson in sorted(os.listdir("lessons")):
        deck = os.path.join("lessons", lesson, "media", lesson + ".deck.html")
        if os.path.isfile(deck):
            shutil.copy(deck, os.path.join(out, lesson + ".html"))
            rows.append(f'<li><a href="{lesson}.html">{html.escape(title(lesson))}</a></li>')
    with open(os.path.join(out, "index.html"), "w", encoding="utf-8") as f:
        f.write('<!doctype html><html lang="en"><head><meta charset="utf-8">'
                '<meta name="viewport" content="width=device-width, initial-scale=1"><title>Lesson slide decks</title>'
                '<style>body{font:18px/1.6 Georgia,serif;background:#f3efe4;color:#111;max-width:40rem;'
                'margin:2rem auto;padding:0 16px}h1{font-family:Helvetica,Arial,sans-serif;text-transform:uppercase}'
                'a{color:#111}</style></head><body><h1>Lesson slide decks</h1>'
                '<p>Open a deck; arrow keys move between slides, N shows the speaker notes.</p><ul>'
                + "".join(rows) + "</ul></body></html>\n")
    print(f"{len(rows)} decks gathered into {out}")


if __name__ == "__main__":
    main(sys.argv[1])
