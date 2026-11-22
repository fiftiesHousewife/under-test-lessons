# Figures

Hand-drawn people for the black-and-white slides, one for each kind of slide:

| Figure | Slide |
|---|---|
| `explaining.svg` | What is it? |
| `example.svg` | Our example |
| `compare.svg` | What we'll compare |
| `why.svg` | Why it matters |
| `how.svg` | How the tool works |
| `build.svg` | How we build |
| `when.svg` | When to use it |
| `history.svg` | Where it came from |
| `more.svg` | Find out more |
| `idea.svg` | An idea the lesson needs, such as boundaries |

They are made from [Open Peeps](https://www.openpeeps.com/) by Pablo Stanley, a hand-drawn illustration library
released into the public domain (CC0), using the parts in the npm package `@opeepsfun/open-peeps` (MIT licence),
rendered once to plain SVG in black ink on white. Nothing here downloads at run time.

`tools/compose-pictures.py` writes the ink picture set, `pictures/ink/`: for every picture name a slide can use, the
figure for that kind of slide. Rerun it after changing a figure, or after adding a picture name to the colour set.
