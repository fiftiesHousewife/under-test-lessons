---
status: accepted
decision-makers: Lessons maintainers
group: How lessons are built
---

# Examples use several currencies, and spoken amounts are words

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

Most lessons' examples deal in money, and every one of them used pounds and pence, which makes the series read as if
it were written for one country only. Separately, the voice read an amount written with its symbol wrongly: "£3.99"
came out as "pound three point nine nine", and "€50" as "euros fifty".

## Decision Outcome

Chosen: **the examples use several currencies**, each lesson one, changed throughout: its code, tests, README, slides,
narration and kept outputs. Feature flags, embedded Jakarta EE tests and property-based testing use euros; Mockito well
used and message consumers use US dollars; old command-line programs uses Indian rupees; fake behind an interface uses
Swiss francs; the rest keep pounds. A lesson keeps its numbers when its currency changes, so its story is unchanged. A
new lesson picks the currency its example suits, preferring one the series uses least.

**Spoken amounts are written as words** ("3 euros 99", "50 pounds", "1 cent"); slide bullets and code keep the symbol
(`€3.99`). The don't-say list, `lessons/_shared/dont-say.txt`, refuses a currency symbol before a number in spoken
text, so the check catches it.

### Consequences

* Good, because readers from anywhere see their kind of example, and minor units (cents, pence, centimes, paise) show
  the same rounding faults.
* Good, because the voice says every amount as a person would.
* Neutral, because a lesson's currency is a choice made once, and changing it touches every file of the lesson.
