---
status: accepted
decision-makers: Lessons maintainers
group: Using the lessons in a company
---

# No lesson needs an AI tool

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

Many companies allow only the AI tools they have approved, and some allow none. Some refuse any tool that sends
code or data to an outside service. [Every lesson works inside a company](0021-every-lesson-works-inside-a-company.md)
already rules out downloads, Docker Hub and containers on Windows. AI tools are another restriction of the same kind.

AI appears in this repo in three places:

* **The voice.** Kokoro, the offline voice that reads the decks and videos, is an AI speech model. It is a neural
  text-to-speech model with open weights under the Apache 2.0 licence, run by kokoro-onnx (MIT) on the maintainer's
  own machine, as [Videos are recorded with VHS and voiced offline](0011-videos-are-recorded-with-vhs-and-voiced-offline.md)
  says. Nothing it reads leaves that machine.
* **The overview.** The overview lesson says an AI assistant can draft tests.
* **The planned lesson on AI-written tests.**

The repo is also kept up with the help of AI coding assistants, and its skills (`.github/skills/`) guide them.

## Decision Drivers

* A person in a company that allows no AI tools can follow every lesson, run it and change it
* Nothing a learner runs sends code or data to an AI service
* Where a company does approve an assistant, the lessons can still say how it helps

## Considered Options

* No lesson needs an AI tool: AI is optional everywhere, the voice is made by the maintainers, and it can be replaced
* Leave AI out of the lessons altogether
* Let lessons use an AI service where it helps

## Decision Outcome

Chosen: **no lesson needs an AI tool**. Learners use the lessons with no AI tool, and AI is only ever an option the
company approves.

* **Running a lesson needs no AI tool.** Its code, tests, build and checks use no AI library or service, and every
  technique is shown working by hand.
* **The voice is made by the maintainers, not by learners.** A learner plays recorded sound files and runs no AI tool,
  and no speech engine runs inside a company
  ([Companies receive finished videos and never run the voice](0025-companies-receive-finished-videos-and-never-run-the-voice.md)).
  When a company changes a lesson, a person records the changed slides
  ([Inside a company, a person records the voice](0027-inside-a-company-a-person-records-the-voice.md)). A deck also
  works with its voice turned off.
* **AI help is always optional, and always "where your company has approved one".** The overview says so on its AI
  slide and adds that nothing in the lessons needs an AI tool. The planned lesson on AI-written tests must work with
  whichever assistant a company has approved. Skipping it must cost nothing in any other lesson.
* **The maintainers' assistants and skills are optional.** A person can add or change a lesson by following the
  skills as written instructions, with no assistant.

### Consequences

* Good, because a company with no approved AI tool can still use every lesson as it is.
* Good, because it is clear that only the maintainers use the voice, and a person's recording can stand in for it
  without changing a lesson.
* Bad, because a company that forbids content made with AI has to turn the voice off, or record a person's voice,
  until the open question in [Inside a company, a person records the voice](0027-inside-a-company-a-person-records-the-voice.md)
  is settled. Until then, the published decks and videos keep the Kokoro voice.

### Confirmation

Review checks that no lesson's build adds an AI library or calls an AI service. The overview's AI slide says AI is
optional and approved by the company. Any new lesson that mentions AI help says the same.

## Pros and Cons of the Options

### Leave AI out altogether

* Good, because nothing in the lessons touches AI.
* Bad, because many teams do use approved assistants to draft tests, and the lessons would have nothing to say about
  checking what they draft.

### Let lessons use an AI service

* Good, because some steps would be quicker.
* Bad, because those lessons would fail in exactly the companies [Every lesson works inside a
  company](0021-every-lesson-works-inside-a-company.md) is written for.
