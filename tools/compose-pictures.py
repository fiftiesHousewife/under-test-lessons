"""Builds the black-and-white picture set: each slide's picture is a hand-drawn figure, chosen by the kind of slide.
(Objects as small badges beside the figures were tried; at that size they read as smudges, so figures stand alone.)

    python3 tools/compose-pictures.py

Reads lessons/_shared/slides/figures/ and the names in pictures/ (the colour set), and writes pictures/ink/.
"""
import os
import re

SLIDES = "lessons/_shared/slides"
# Which figure each picture gets: the shared slides by their picture, everything else by whether it shows an example.
ROLES = {"scales": "compare", "magnifier": "why", "checklist": "why", "robot": "how", "blocks": "build",
         "map": "when", "book": "history", "globe": "more", "terminal": "explaining", "boundary": "idea",
         "coverage": "why"}
EXAMPLES = {"basket", "statement", "pizza", "tag"}


def inner(svg):
    """The drawing inside an SVG file, with its viewBox."""
    box = re.search(r'viewBox="([^"]+)"', svg).group(1)
    body = re.sub(r"^.*?<svg[^>]*>", "", svg, flags=re.S)
    return box, re.sub(r"</svg>\s*$", "", body.strip())


def compose(figure):
    figure_box, figure_body = inner(figure)
    return ('<svg viewBox="0 0 400 400" xmlns="http://www.w3.org/2000/svg">'
            f'<svg x="10" y="24" width="380" height="376" viewBox="{figure_box}" preserveAspectRatio="xMidYMax meet">'
            f'{figure_body}</svg></svg>\n')


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
            f.write(compose(figure))
    print(f"{len(names)} pictures written to {out}")


if __name__ == "__main__":
    main()
