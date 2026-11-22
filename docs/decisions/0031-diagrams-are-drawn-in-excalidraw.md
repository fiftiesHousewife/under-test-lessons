---
status: accepted
decision-makers: Lessons maintainers
group: Videos and slides
---

# Diagrams are drawn in Excalidraw and exported in the build

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

Several lessons explain a flow the slides can't show: a request through a stand-in, a message redelivered, a job
restarting part way. [Videos and decks are generated from text files, never edited by
hand](0002-videos-and-decks-are-generated-from-text-files-never-edited-by-hand.md) leaves no room for a drawing made in
an editor.

## Decision Drivers

* Diagrams match the hand-drawn look of the figures and objects
* Their source is text in git, so a change shows in a diff
* Exporting them runs inside a company

## Considered Options

* Excalidraw drawings, saved as `.excalidraw` files and exported to SVG in the build
* Diagrams generated from text, such as Mermaid
* No diagrams

## Decision Outcome

Chosen: **Excalidraw.** A diagram is drawn in Excalidraw and saved as an `.excalidraw` file, which is JSON. The build
exports it to SVG in the installed Chrome, so it stays inside. This is the one exception to
[Videos and decks are generated from text files, never edited by hand](0002-videos-and-decks-are-generated-from-text-files-never-edited-by-hand.md):
the `.excalidraw` file is the source, drawn by hand. Diagrams use a plain font, not Excalidraw's handwritten one, to
suit the newspaper typography. Excalidraw (MIT) runs inside a company as a VS Code extension or a self-hosted image,
so the hosted site isn't needed. Its community libraries are not used for slide pictures, because their quality and
sources vary.

### Consequences

* Good, because flows can be shown, in the same style as the rest.
* Bad, because a diagram is changed by redrawing it, not by editing a line of text.

### Confirmation

The build fails if an `.excalidraw` file has no exported SVG, or if the SVG is older than its source.

## Pros and Cons of the Options

### Diagrams from text, such as Mermaid

* Good, because they stay within "generated from text".
* Bad, because their look doesn't match the hand-drawn style.
