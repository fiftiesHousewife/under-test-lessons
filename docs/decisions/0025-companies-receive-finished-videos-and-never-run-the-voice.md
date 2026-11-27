---
status: superseded by 0041
decision-makers: Lessons maintainers
group: Using the lessons in a company
---

# Companies receive finished videos and never run the voice

**Decided by:** Lessons maintainers · **Status:** superseded by [Lessons are decks with their voice, and the videos are removed](0041-lessons-are-decks-with-their-voice-and-the-videos-are-removed.md) · **Replaces:** the voice parts of
[Videos are recorded with VHS and voiced offline](0011-videos-are-recorded-with-vhs-and-voiced-offline.md) and
[Every lesson works inside a company](0021-every-lesson-works-inside-a-company.md)

## Context and Problem Statement

The voice is Kokoro-82M. It is an open-weight neural text-to-speech model under Apache 2.0, built on StyleTTS 2 with an
iSTFTNet vocoder. It runs through `kokoro-onnx` (MIT) on ONNX Runtime, and espeak-ng turns the words into sounds.
Making speech needs no network. The network is used once, at set-up: for the PyPI packages, and for two model files,
`kokoro-v1.0.onnx` (about 310 MB) and `voices-v1.0.bin` (about 27 MB). Those files come from the wrapper maintainer's
own GitHub release, `thewh1teagle/kokoro-onnx` `model-files-v1.0`, as an ONNX conversion of hexgrad's model.

Until now, [Every lesson works inside a company](0021-every-lesson-works-inside-a-company.md) told companies to mirror
the model and its packages and make the voice themselves. That doesn't suit a company:

* Governance counts it as running a third-party AI model on company machines, even though no data leaves them.
* The model files come from one person's GitHub release, and nothing checks their checksums.
* espeak-ng, bundled through `espeakng-loader`, is GPL-3.0.
* ONNX Runtime, numpy and phonemizer can change version between set-ups.

A separate PyPI package, `kokoro`, is hexgrad's own PyTorch version. It downloads its weights from Hugging Face the
first time it runs, so it is **not** offline. This repo doesn't use it, but a search for "kokoro" may find it first.

## Decision Drivers

* No company runs an AI model, or downloads one, to use the lessons
* Every company sees the same, finished video
* A published video can be traced to the commit, narration and voice settings that made it

## Considered Options

* Companies receive finished videos, made here; no speech engine runs inside a company
* Companies mirror the model and packages and make the voice themselves (the old advice)
* Companies use their own approved text-to-speech tool

## Decision Outcome

Chosen: **companies receive finished videos, made here, and no speech engine runs inside a company.**

* **Videos are published as release assets.** Each lesson's `<id>.mp4`, with its `.srt` and `.vtt` captions, is
  attached to a GitHub Release. Videos are not committed to git. Each release has a small manifest that records, for
  each video, the commit, the narration file's hash and the voice settings. Lesson READMEs and the catalogue link to
  each video at a stable address.
* **The deck voice clips stay in git** (`<id>.deck-voice/*.m4a`). A company adds its own recordings to those folders
  when it changes a lesson, as [Inside a company, a person records the voice](0027-inside-a-company-a-person-records-the-voice.md)
  says, so the clips can't move to release assets. Each time a lesson is voiced again, the repository grows by about 2 MB.
* **The voice is made only on the machine that renders,** and it is made the same way each time. The whole Python set
  is pinned with hashes (`pip install --require-hashes`). Both model files have a recorded SHA-256, and
  `setUpLessonVoice` fails if a file doesn't match.
* **`-PlessonVoiceCommand` stays, for that machine only.** It is not a route for companies, and the company guide
  no longer offers it, nor mirroring the model (`lessonVoiceModelsUrl`).
* **Only `kokoro-onnx` is used, never the `kokoro` package,** because `kokoro` downloads its weights when it first runs.

### Consequences

* Good, because a company runs no speech engine and approves no AI model, and its people still hear every lesson.
* Good, because a video can be traced to what made it.
* Bad, because a company that changes a lesson can't make a new video of it; the change comes back here to be
  published. It voices its changed deck slides by recording a person.
* Bad, because the repository grows with each deck revoicing.

### Confirmation

`docs/using-inside-a-company.md` says companies use the published videos and record any changed deck slides. Every
video in a release appears in that release's manifest. `setUpLessonVoice` checks the model files' checksums.

## Pros and Cons of the Options

### Companies mirror the model and make the voice

* Good, because a company could remake any video itself.
* Bad, because it means approving and hosting a 337 MB AI model from a personal GitHub account, a GPL-3.0 binary and
  an unpinned set of Python packages.

### Companies use their own approved text-to-speech tool

* Good, because some companies already have one.
* Bad, because a deck would then mix two machine voices, and the tool is still an AI tool many companies don't allow.
  Where a company changes a lesson, a person records the changed slides instead.
