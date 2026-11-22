# Glossary and word list

The toolkit's [glossary and word list](https://github.com/fiftiesHousewife/under-test/blob/main/GLOSSARY.md) applies
here in full: plain English, no ordinary words given special meanings, industry terms explained the first time they
appear, abbreviations spelt out, and things referred to by name with a link, never by a code or number. This file adds
the words the lessons themselves use.

## Words we use in lessons

| Word or phrase | Meaning | Replaces (don't use) |
|---|---|---|
| **lesson** | One technique taught with a runnable example, a narrated video and a slide deck. | module, unit, course |
| **today's code** | The example code as it is before the lesson's fix, chosen with `-Pcode=today` (or the lesson's own switch). | legacy version, broken version |
| **the fix** | The example code after the change the lesson teaches. The default, so the build passes. | fixed version, solution |
| **weak test** | A test that looks reasonable and passes, but would miss the bug the lesson shows. | bad test |
| **strong test** | The test the technique produces, which finds the bug. | good test |
| **scene** | One step of a video's terminal recording, marked `# ---- Scene N: title ----` in its script, with one narration item. | shot, clip |
| **narration** | The words spoken over a video, one item per scene, in `media/<id>.narration.md`. | script (that is the `.tape` file), voiceover text |
| **video script** | The `.tape` file that VHS records, scene by scene. | tape (in prose) |
| **slides file** | `media/<id>.slides.md`: the intro and closing slides, with their points, picture and narration. |  |
| **slide deck** | One self-contained HTML page per lesson, `media/<id>.deck.html`, with every slide and each scene's code. | presentation, PowerPoint |
| **code panel** | A piece of a file shown in a video or deck, with its line numbers and a label saying what it is. | snippet |
| **callout** | A short boxed sentence after a result, saying what it means. | caption (captions are the subtitles) |
| **captions** | The subtitles made from the narration. | subtitles (in prose) |
| **live run** | A command run on camera, whose real output the video shows. | demo |
| **look** | The stylesheet and picture set that slides, decks and videos share. | theme (except for the files in `themes/`), skin |
| **picture set** | A folder of SVG pictures, found by name; the ink set is drawn for black and white. | icon set |
| **container tests** | Tests that need a container runtime, kept in the `integrationTest` suite, skipped where there is none (as in the toolkit's glossary). | integration tests (in prose) |
