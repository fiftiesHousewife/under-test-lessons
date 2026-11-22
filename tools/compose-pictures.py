"""Builds the black-and-white picture set: each slide's picture is a hand-drawn figure, chosen by the kind of slide,
with a speech bubble of a few words saying what the slide is about.
(Objects as small badges beside the figures were tried; at that size they read as smudges, so figures stand alone.)

    python3 tools/compose-pictures.py

Reads lessons/_shared/slides/figures/ and the names in pictures/ (the colour set), and writes pictures/ink/.
"""
import html
import os
import re

SLIDES = "lessons/_shared/slides"
# Which figure each picture gets: the shared slides by their picture, everything else by whether it shows an example.
ROLES = {"scales": "compare", "magnifier": "why", "checklist": "why", "robot": "how", "blocks": "build",
         "map": "when", "book": "history", "globe": "more", "terminal": "explaining", "boundary": "idea",
         "coverage": "why"}
EXAMPLES = {"basket", "statement", "pizza", "tag"}
# A few words in a speech bubble, saying what the slide is about. Two lines at most, short ones.
BUBBLES = {
    "bug": "Caught it!", "basket": "5% off\nover £50", "boundary": "≥ or >?", "scales": "Weak or\nstrong?",
    "coverage": "100%...\nso what?", "robot": "Plant a bug,\nrun the tests", "blocks": "One plugin,\nevery module",
    "map": "Where do\nI start?", "book": "Since\nwhen?", "globe": "Read\nmore", "magnifier": "Why does\nit matter?",
    "checklist": "Why does\nit matter?", "statement": "Same output\nas today?", "pizza": "Fair\nshares?",
    "tag": "20% VAT", "camera": "Snapshot!", "dice": "Any input", "table": "Every case", "layers": "No\nshortcuts!",
    "plug": "Pass it in", "braces": "{ JSON }", "door": "201?", "queue": "Delivered?", "folders": "Grouped",
    "twins": "Old = new?", "switch": "On or off?", "files": "File in,\nfile out", "server": "Wired\nright?",
    "database": "Real\nPostgreSQL", "changelog": "Every\nchange", "terminal": "Watch\nthis",
}


def inner(svg):
    """The drawing inside an SVG file, with its viewBox."""
    box = re.search(r'viewBox="([^"]+)"', svg).group(1)
    body = re.sub(r"^.*?<svg[^>]*>", "", svg, flags=re.S)
    return box, re.sub(r"</svg>\s*$", "", body.strip())


def bubble(words):
    """A hand-drawn speech bubble, top right, pointing down towards the figure's head."""
    lines = words.split("\n")
    size = 26 if max(len(line) for line in lines) <= 10 else 21
    first = 82 - (len(lines) - 1) * (size + 4) // 2
    text = "".join(f'<text x="290" y="{first + i * (size + 4)}" text-anchor="middle" font-family="Georgia, serif" '
                   f'font-style="italic" font-weight="bold" font-size="{size}" fill="#111">{html.escape(line)}</text>'
                   for i, line in enumerate(lines))
    return ('<path d="M190 70 C188 30 240 10 292 12 C346 14 394 34 394 74 C395 114 352 136 298 136 '
            'C282 136 268 135 254 131 L218 160 L230 124 C204 113 191 94 190 70 Z" fill="#fff" stroke="#111" '
            f'stroke-width="5" stroke-linejoin="round"/>{text}')


def compose(figure, words):
    figure_box, figure_body = inner(figure)
    x, width = (-10, 330) if words else (10, 380)
    return ('<svg viewBox="0 0 400 400" xmlns="http://www.w3.org/2000/svg">'
            f'<svg x="{x}" y="40" width="{width}" height="360" viewBox="{figure_box}" '
            f'preserveAspectRatio="xMidYMax meet">{figure_body}</svg>'
            + (bubble(words) if words else "") + '</svg>\n')


def read(path):
    with open(path, encoding="utf-8") as f:
        return f.read()


def main():
    pictures = os.path.join(SLIDES, "pictures")
    out = os.path.join(pictures, "ink")
    os.makedirs(out, exist_ok=True)
    names = sorted(f[:-4] for f in os.listdir(pictures) if f.endswith(".svg") and f != "defs.svg")
    for name in names:
        role = ROLES.get(name, "example" if name in EXAMPLES else "explaining")
        figure = read(os.path.join(SLIDES, "figures", role + ".svg"))
        with open(os.path.join(out, name + ".svg"), "w", encoding="utf-8") as f:
            f.write(compose(figure, BUBBLES.get(name, "")))
    print(f"{len(names)} pictures written to {out}")


if __name__ == "__main__":
    main()
