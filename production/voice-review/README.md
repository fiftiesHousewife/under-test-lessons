# Voice review

A page for a person to hear what the lesson voice says and put it right by clicking: no phonetic symbols to read or
type. It belongs to the **production** group, with [video-render](../video-render), because it runs the lesson voice
(Kokoro); the build works without it, and nothing in a company needs it.

## Run it

```bash
./gradlew :production:voice-review:reviewVoice      # then open http://localhost:8765/
```

It needs the voice that `./gradlew :production:video-render:setUpLessonVoice` installs (the task sets it up first), and
runs until stopped with Ctrl+C. Choose another port with `-PvoiceReviewPort=<port>`; the decks look for 8765.

- **http://localhost:8765/** is the review set: the phrases, words and punctuation most likely to be said wrongly.
- **http://localhost:8765/lesson/&lt;id&gt;** is every sentence one lesson's deck says, under its slide's title, then its
  scenes'. Each sentence is voiced the first time it is played.
- **Edit voice** on a deck opens its lesson's page. The button shows only while the review runs on the same machine: the
  deck asks `localhost:8765/ping` and shows nothing if no answer comes within a second.

## Use it

One line a sentence, ending **✎ ▶ ✓**.

| To | Do |
|---|---|
| Hear it | **▶**, or **Space** for the current line, **N** for the next line not yet done |
| Change a word's emphasis | Click the word to open its syllables, then click a syllable: **strong** (solid underline), light (dotted), weak (grey) |
| Add a pause between words | Click the **·** between them: **‖** is a pause, a second click **‿** says them as one word, a third puts them back |
| Change a pause at punctuation | Click the **·** after it: **‖** a longer pause, **‿** no pause, then back |
| Change the words | **✎**, type, then **Enter** (or click away); **Esc** puts them back |
| Finish a line | **✓**, or **O** |

Every change is voiced and played 1.2 seconds after the last click, so quick clicks give one voice, not several over each
other; a clip still being voiced when another click comes is thrown away.

## What is saved, and where

Every click is saved as it is made, so a reload, a closed page or a restarted server loses nothing, and each line comes
back as it was left. The tick then does the rest:

| Change | When **✓** is clicked |
|---|---|
| Emphasis or a join inside the underlined phrase (review set) | A rule in [the pronunciation list](../../lessons/_shared/pronunciations.txt), under "From the voice review", or the phrase's rule replaced. It applies wherever the phrase is said. |
| Changed words, and pauses | Written into the lesson's narration, in the slide's `Say:` paragraph or the scene's line in `narration.md`, the paragraph rewrapped; a pause is `[pause 400ms]` |
| Emphasis anywhere else in a sentence | Kept in `build/voice-review/results.json`, for a person to fix: narration is plain words, so a sentence's emphasis can't be written into it, and a rule for one word would apply everywhere |

Nothing reaches the decks until they are voiced again (`./gradlew :production:video-render:buildLessonDeckVoice`), and
changed narration is checked as usual (`./gradlew :lessons:video-tools:checkVideoScripts`), so a batch of changes is
reviewed, then voiced once.

## The review set

`./gradlew :production:voice-review:buildVoiceReviewSet` builds it into `build/voice-review`, from
[review-set.txt](review-set.txt) and the decks' narration. Each phrase or word in the list is heard in one real
sentence from the decks: a short one, and one where the phrase doesn't follow "the", where there is one, so the line
tests the phrase, not the article. Two sentences each are found for kinds of punctuation and numbers (colons, lists,
years, money, versions and so on), by the patterns in [review_set.py](review_set.py). Building it again keeps each
line's number and sentence, so saved answers stay with their lines, and voices only the lines with no clip yet. Add a
phrase to the list and build again to hear it.

## How it works

| File | What it does |
|---|---|
| [narration.py](narration.py) | Everything that needs no voice: a lesson's sentences, writing a change back into its narration, the pronunciation list, and a sentence split into words with the sounds the voice gives them (the list's rules, a phrase's included, and the weak articles: "the" is "thee" before a vowel) |
| [server.py](server.py) | The web server on localhost: the pages, voicing (one clip at a time, sounds remembered once worked out), and saving |
| [page.html](page.html) | The page: words, syllables and marks to click, built from the sounds the server sends, shown as written letters |
| [review_set.py](review_set.py), [review-set.txt](review-set.txt) | The review set and its list |
| [test_narration.py](test_narration.py) | Tests of `narration.py` with a made-up lesson and a stand-in voice: `./gradlew :production:voice-review:testVoiceReview`, part of `check` |

Syllables are worked out from the voice's sounds, a syllable for each vowel sound, and labelled with the written word
split the same way where the spelling allows it ("Ja·Co·Co"); where it doesn't, the labels are an even split of the
word. A pause is 0.4 seconds of silence, made as the decks make `[pause 400ms]`, so what is heard here is what the deck
will say.
