# Branding the decks for your company

**Status: proposed.** This page describes how a company gives every lesson deck its own look, its own words at the
start and end, and its own header and footer, and keeps them through every new release of the lessons. The pieces
marked *(to build)* don't exist yet.

## What you can change

| What | How | Where it shows |
|---|---|---|
| The look: fonts, colours, layout | your stylesheet, `theme.css` | every slide |
| The pictures | your pictures folder, found by name before the shared set | every slide with a picture |
| Header and footer *(to build)* | your logo and a line of text, in `branding.properties` | the top and bottom of every slide |
| Your own opening slides *(to build)* | `intro.md`, in the slides format every lesson uses | before every lesson's first slide |
| Your own closing slides *(to build)* | `closing.md`, in the same format | after every lesson's last slide |
| The voice, if you want one | a person reads a timed script; unvoiced by default | your slides, or every slide if you re-voice the decks |

The lessons' own slides, code, narration and clips stay as they are: they're the lessons, not the branding.

## One folder

Everything a company changes lives in one folder in its own copy of the repository, which the lessons never touch,
so a new release never conflicts with it:

```
branding/
  theme.css               the look
  pictures/               pictures, by the same names as the shared set, to replace any of them
  logo.svg                the logo, for the header
  branding.properties     the header and footer text, and which slides go where
  intro.md                your opening slides
  closing.md              your closing slides
  voice/                  only if you voice your own slides: their recordings
```

`branding.properties` names the rest:

```properties
header.logo=logo.svg
header.text=Acme Engineering · Testing academy
footer.text=Internal: do not share outside Acme
intro=intro.md
closing=closing.md
```

One setting points the build at the folder, in `gradle.properties` or on the command line:

```properties
lessonBranding=branding
```

The older settings still work, and a branding folder's own files take their place: `lessonSlideCss` for the
stylesheet, `lessonPictures` for the pictures, and `lessonPictureFrame` for how pictures are framed.

## Your opening and closing slides

`intro.md` and `closing.md` are written like any lesson's slides: a heading, a few points, a picture, and what the
voice says.

```markdown
## intro: Welcome to the testing academy
- Each lesson takes about five minutes
- Run its example on your own machine afterwards
Picture: road
Say: Welcome to Acme's testing academy. Each lesson takes about five minutes, and you can run its example on your
  own machine afterwards.
```

They follow the same rules as the lessons' slides: the [glossary](../GLOSSARY.md), its "Writing for the voice" rules,
and the [don't-say list](../lessons/_shared/dont-say.txt), which the checks apply to them too.

## The voice

Your opening and closing slides are **unvoiced by default**: a deck shows them silent, with their words in the speaker
notes, and plays the lesson's own voice as usual. Nothing has to be recorded to brand the decks.

If you want a voice, for your own slides or for whole decks in your company's own voice, a person reads it
([why](decisions/0027-inside-a-company-a-person-records-the-voice.md)), from a timed script:

```bash
./gradlew :lessons:video-tools:recordingScript -Plesson=<id>         # one lesson's script, as a printable page
./gradlew :lessons:video-tools:recordingScript -Pbranding=true       # your own opening and closing slides (to build)
```

The script is written by the lessons' own tool, from your slides' words. Like every step that runs inside a company,
it needs only the JDK and Gradle: no VHS, ffmpeg, Python or voice engine. The recording needs only a microphone and
any app that saves `.m4a`, `.wav` or `.mp3`, which the decks play as they are: Windows' Sound Recorder saves `.m4a`,
and so does QuickTime on a Mac. The live runs play from casts that the capture step writes, so nothing in a company
ever records a terminal.

The script gives each clip the file name to save it under, how long it takes at the decks' pace ("about 12 seconds"),
the words with their pauses as directions, and how to say the names the voice list covers. Read each clip in one
take, in the same voice, microphone and room throughout; a recording kept under its file name replaces the machine
voice, and the checks report which clips each deck still lacks. Your own opening and closing slides are recorded once,
and every deck plays the same recording.

## Building and publishing at scale

1. **Once:** add the branding folder to your copy of the repository, and set `lessonBranding`.
2. **Every release:** take the new release of the lessons, then rebuild every deck in your CI:
   `./gradlew :lessons:video-tools:buildLessonDecks`. The build needs only the JDK, Gradle and your Maven repository,
   as every step that runs inside a company does.
3. **Publish** the decks, with their clips, to your internal site, as `tools/publish-decks.py` does for the public one.
4. **Check** before publishing: `./gradlew :lessons:video-tools:checkVideoScripts` checks your slides as it checks the
   lessons' own: the same words and rules.

Because your folder holds only what's yours, taking a new release is a pull and a rebuild. Nothing of yours is merged
by hand.

## Many companies, or many teams

One copy of the lessons can carry several branding folders, one per team or company, each built on its own with
`-PlessonBranding=<folder>`. Their decks go to different places, from the same lessons.
