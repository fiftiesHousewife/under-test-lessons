"""Gathers every lesson's slide deck into one folder for publishing, with an index page to work through them in order.

    python3 tools/publish-decks.py <out folder>

The index follows the catalogue (lessons/CATALOGUE.md): its groups, and the built lessons in each, in order, marked
where a deck has its voice. Each deck becomes <out>/<lesson id>.html, with its voice clips (<lesson id>.deck-voice/)
beside it, and the asciinema player its live runs play in (vendor/), and gains series.js: its place in the series ("Lesson 7 of 38 · <group>"), links back to the index and on
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
    // The intro slide names the lesson's stage of the journey, above its title.
    var title = document.querySelector('.deck-slide h1');
    if (title) {
      var stage = document.createElement('p');
      stage.className = 'stage-kicker';
      stage.textContent = place;
      title.parentNode.insertBefore(stage, title);
    }
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
    return [(title, lessons) for title, _, lessons in groups_described()]


def groups_described():
    """The catalogue's groups as (title, what the catalogue says of it, [(lesson id, name)]), only built lessons, in
    order. What it says is the stage's question and why, then its techniques, tools and references, as markdown."""
    groups, current = [], None
    with open(CATALOGUE, encoding="utf-8") as f:
        for line in f:
            heading = re.match(r"^## (.+)$", line)
            if heading:
                current = (heading.group(1).strip(), [], [])
                groups.append(current)
                continue
            if current is not None and re.match(r"^\*\*[^*]+(\?|:)\*\*", line) and not current[2]:
                current[1].append(line.strip())
            row = re.match(r"^\| \[([^\]]+)\]\(([a-z0-9-]+)/README\.md\)", line)
            if row and current is not None:
                current[2].append((row.group(2), row.group(1)))
    seen, ordered = set(), []
    for title, said, lessons in groups:
        fresh = [(lesson, name) for lesson, name in lessons if lesson not in seen and deck_of(lesson)]
        seen.update(lesson for lesson, _ in fresh)
        if fresh:
            ordered.append((title, said, fresh))
    return ordered


def inline(markdown):
    """A line of the catalogue's markdown as HTML: links, bold and italics."""
    text = html.escape(markdown, quote=False)
    text = re.sub(r"\[([^\]]+)\]\(([^)]+)\)", lambda m: f'<a href="{html.escape(m.group(2))}">{m.group(1)}</a>', text)
    text = re.sub(r"\*\*([^*]+)\*\*", r"<b>\1</b>", text)
    return re.sub(r"(?<![\w*])\*([^*]+)\*(?![\w*])", r"<i>\1</i>", text)


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
    # The asciinema player the decks share, kept once in lessons/_shared/slides/vendor, is published beside them.
    page = page.replace('"../../_shared/slides/vendor/', '"vendor/')
    tag = (f'<script src="series.js" data-previous="{html.escape(before or "")}"'
           f' data-next="{html.escape(following or "")}"'
           f' data-place="{html.escape(place)}"></script>')
    page = page.replace("</body>", tag + "</body>", 1)
    with open(os.path.join(out, lesson + ".html"), "w", encoding="utf-8") as f:
        f.write(page)
    if voice_of(lesson):
        shutil.copytree(voice_of(lesson), os.path.join(out, lesson + ".deck-voice"), dirs_exist_ok=True)
    if '"vendor/asciinema-player/' in page:
        shutil.copytree(os.path.join("lessons", "_shared", "slides", "vendor"), os.path.join(out, "vendor"),
                        dirs_exist_ok=True)


LEVELS = ["beginner", "intermediate", "advanced"]


def setting(name):
    """A setting from gradle.properties, such as the lessons' repository address."""
    with open("gradle.properties", encoding="utf-8") as f:
        for line in f:
            if line.startswith(name + "="):
                return line.split("=", 1)[1].strip()
    return ""


def lesson_file(lesson):
    """A lesson's level and what it builds on, from its lesson.yaml."""
    path = os.path.join("lessons", lesson, "lesson.yaml")
    found = {"level": "", "buildsOn": []}
    if os.path.exists(path):
        with open(path, encoding="utf-8") as f:
            for line in f:
                key, _, value = line.partition(":")
                if key == "level":
                    found["level"] = value.strip()
                elif key == "buildsOn":
                    found["buildsOn"] = [v.strip() for v in value.strip().strip("[]").split(",") if v.strip()]
    return found


def readme_line(lesson):
    """The README's one bold line: what the technique does."""
    path = os.path.join("lessons", lesson, "README.md")
    if not os.path.exists(path):
        return ""
    with open(path, encoding="utf-8") as f:
        text = f.read()
    bold = re.search(r"^\*\*(.+?)\*\*\s*$", text, re.M | re.S)
    return re.sub(r"\s+", " ", bold.group(1)).strip() if bold else ""


def catalogue_rows():
    """Each built lesson's catalogue row: (stage number, stage, the stage's question, lesson id, name, what it shows,
    tools), in the catalogue's order; the first stage a lesson appears in is its stage."""
    rows, stage, number, question, seen = [], None, 0, "", set()
    with open(CATALOGUE, encoding="utf-8") as f:
        for line in f:
            heading = re.match(r"^## (.+)$", line)
            if heading:
                stage, question = heading.group(1).strip(), ""
                continue
            asked = re.match(r"^\*\*([^*]+\?)\*\*", line)
            if asked and stage:
                number += 1
                question = asked.group(1)
            row = re.match(r"^\| \[([^\]]+)\]\(([a-z0-9-]+)/README\.md\)(?::\s*([^|]*))?\s*\|\s*([^|]*)\|", line)
            if row and stage and row.group(2) not in seen and deck_of(row.group(2)):
                seen.add(row.group(2))
                rows.append((number if question else 0, stage, question, row.group(2), row.group(1),
                             (row.group(3) or "").strip(), row.group(4).strip()))
    return rows


def write_lessons_table(out):
    """lessons.html: every lesson in one table, by stage, reason, tools and level, to sort and filter."""
    repo = setting("lessonsRepoUrl")
    names = {}
    body = []
    rows = catalogue_rows()
    for _, _, _, lesson, name, _, _ in rows:
        names[lesson] = name
    for number, stage, question, lesson, name, shows, tools in rows:
        about = lesson_file(lesson)
        level = about["level"]
        first = ", ".join(f'<a href="{b}.html#1">{html.escape(names.get(b, b))}</a>' for b in about["buildsOn"])
        stage_cell = (f"{number}. " if number else "") + html.escape(stage)
        body.append(
            f'<tr data-stage="{number}" data-level="{LEVELS.index(level) if level in LEVELS else 9}">'
            f'<td><a href="{lesson}.html#1">{html.escape(name)}</a></td>'
            f'<td data-sort="{number:02d}">{stage_cell}</td>'
            f'<td>{inline(question)}</td>'
            f'<td>{html.escape(readme_line(lesson))}</td>'
            f'<td>{inline(tools)}</td>'
            f'<td data-sort="{LEVELS.index(level) if level in LEVELS else 9}"><span class="level {level}">{level}</span></td>'
            f'<td>{inline(shows)}</td>'
            f'<td>{first}</td>'
            f'<td><a href="{repo}/tree/main/lessons/{lesson}">code</a></td></tr>')
    stages = sorted({(r[0], r[1]) for r in rows})
    stage_options = "".join(f'<option value="{n}">{(str(n) + ". ") if n else ""}{html.escape(t)}</option>' for n, t in stages)
    with open(os.path.join(out, "lessons.html"), "w", encoding="utf-8") as f:
        f.write(LESSONS_PAGE.replace("__ROWS__", "".join(body)).replace("__STAGES__", stage_options)
                .replace("__COUNT__", str(len(rows))))


LESSONS_PAGE = """<!doctype html><html lang="en"><head><meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1"><title>Every lesson, in one table</title>
<style>
body{font:15px/1.5 Georgia,serif;background:#f3efe4;color:#111;margin:2rem auto;padding:0 16px;max-width:110rem}
h1{font-family:Helvetica,Arial,sans-serif;text-transform:uppercase;font-size:1.4rem}
a{color:#111}.bar{font:14px Helvetica,Arial,sans-serif;margin:1rem 0;display:flex;gap:1rem;flex-wrap:wrap;align-items:center}
select,input{font:inherit;padding:.2rem .4rem;background:#fff;border:1px solid #999}
table{border-collapse:collapse;width:100%;background:#fbf9f3}
th{font:13px Helvetica,Arial,sans-serif;text-transform:uppercase;text-align:left;padding:.5rem;border-bottom:2px solid #111;
cursor:pointer;white-space:nowrap;position:sticky;top:0;background:#f3efe4}
th:hover{text-decoration:underline}td{padding:.5rem;border-bottom:1px solid #ddd6c4;vertical-align:top}
td:first-child{font-weight:bold;min-width:12rem}td:nth-child(2){white-space:nowrap}
.level{font:12px Helvetica,Arial,sans-serif;padding:.1rem .45rem;border:1px solid #111;border-radius:3px;white-space:nowrap}
.level.intermediate{background:#e2dccb}.level.advanced{background:#111;color:#f3efe4}
.count{color:#555}
</style></head><body>
<h1>Every lesson, in one table</h1>
<p class="bar"><a href="index.html">The lessons in order</a>
<label>Stage <select id="stage"><option value="">all</option>__STAGES__</select></label>
<label>Level <select id="level"><option value="">all</option><option value="0">beginner</option>
<option value="1">intermediate</option><option value="2">advanced</option></select></label>
<label>Find <input id="find" placeholder="a tool, a word"></label>
<span class="count"><span id="shown">__COUNT__</span> of __COUNT__ lessons</span></p>
<p class="bar" style="margin-top:-.5rem;color:#555">Click a heading to sort by it. The level is the technique's, whatever the
library: beginner is one idea you can use the same day; intermediate needs a judgement about what to check, or changes
how code is built; advanced changes how you think about correctness, or spans teams and systems.</p>
<table><thead><tr><th>Lesson</th><th>Stage</th><th>Its question</th><th>What it does</th><th>Tools</th><th>Level</th>
<th>What the lesson shows</th><th>Do first</th><th></th></tr></thead><tbody id="rows">__ROWS__</tbody></table>
<script>
const rows=[...document.querySelectorAll("#rows tr")],stage=document.getElementById("stage"),
  level=document.getElementById("level"),find=document.getElementById("find");
function filter(){const s=stage.value,l=level.value,q=find.value.toLowerCase();let n=0;
  rows.forEach(r=>{const show=(!s||r.dataset.stage===s)&&(!l||r.dataset.level===l)&&(!q||r.textContent.toLowerCase().includes(q));
    r.hidden=!show;if(show)n++;});document.getElementById("shown").textContent=n;}
[stage,level].forEach(e=>e.onchange=filter);find.oninput=filter;
document.querySelectorAll("th").forEach((th,i)=>th.onclick=()=>{const up=th.dataset.dir!=="up";th.dataset.dir=up?"up":"down";
  const key=r=>{const c=r.children[i];return c.dataset.sort??c.textContent.trim().toLowerCase();};
  rows.sort((a,b)=>(key(a)>key(b)?1:key(a)<key(b)?-1:0)*(up?1:-1)).forEach(r=>document.getElementById("rows").appendChild(r));});
</script></body></html>
"""


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
    stage = 0
    for title, said, lessons in groups_described():
        items = "".join(
            f'<li><a href="{lesson}.html#1">{html.escape(name)}</a>'
            + "</li>" for lesson, name in lessons)
        stage += 1 if said else 0
        heading = (f"{stage}. " if said else "") + html.escape(title)
        about = "".join(f'<p class="{"question" if i == 0 else "kit"}">{inline(line)}</p>' for i, line in enumerate(said))
        sections.append(f"<h2>{heading}</h2>{about}<ol>{items}</ol>")
    with open(os.path.join(out, "index.html"), "w", encoding="utf-8") as f:
        f.write('<!doctype html><html lang="en"><head><meta charset="utf-8">'
                '<meta name="viewport" content="width=device-width, initial-scale=1"><title>Lesson slide decks</title>'
                '<style>body{font:18px/1.6 Georgia,serif;background:#f3efe4;color:#111;max-width:44rem;'
                'margin:2rem auto;padding:0 16px}h1,h2{font-family:Helvetica,Arial,sans-serif;text-transform:uppercase}'
                'h2{font-size:1rem;margin-top:2rem}a{color:#111}a.listen{font:14px Helvetica,Arial,sans-serif;'
                'margin-left:.5rem}.start{font:16px Helvetica,Arial,sans-serif}.question{margin:.4rem 0}.kit{font:14px/1.55 Helvetica,Arial,sans-serif;color:#333;'
                'margin:.4rem 0 .8rem}.kit a{color:#333}</style></head><body>'
                '<h1>Lesson slide decks</h1><p class="start"><a href="lessons.html">Every lesson in one table: '
                'by stage, reason, tools and level</a></p>'
                + "".join(sections) + "</body></html>\n")
    write_lessons_table(out)
    print(f"{len(order)} decks gathered into {out}, in the catalogue's order, with every lesson in lessons.html")


if __name__ == "__main__":
    main(sys.argv[1])
