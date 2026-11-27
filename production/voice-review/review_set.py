"""Builds the review set: build/voice-review/items.json and a .wav for each line, from review-set.txt and the decks'
narration. Each phrase or word in the list is heard in one real sentence from the decks: the shortest of a sensible
length, and, where one exists, one in which the phrase doesn't follow "the", so the line tests the phrase rather than
the article. "Punctuation and numbers" are found by pattern, two sentences each. A line keeps its number when the set is
built again, and only lines with no clip yet are voiced, so saved answers stay with their lines. See README.md.

    ./gradlew :production:voice-review:buildVoiceReviewSet          (or, from the repository root:)
    build/voice/venv/bin/python production/voice-review/review_set.py
"""
import glob
import json
import os
import re
import subprocess
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import narration  # noqa: E402

ROOT = os.path.abspath(os.path.join(HERE, "..", ".."))
REVIEW = os.path.join(ROOT, "build/voice-review")
LIST = os.path.join(ROOT, "lessons/_shared/pronunciations.txt")
SPEAK = os.path.join(ROOT, "production/deck-voice/speak-kokoro.py")
PUNCTUATION = [
    ("a colon before a list", r":\s+\w+,\s+\w+,? and "), ("a colon before an explanation", r"\w: [a-z]"),
    ("brackets", r"\("), ("a dash", r" (—|–|-) "), ("quotation marks", r"\"[^\"]+\""), ("a question", r"\?$"),
    ("a list of three, with commas", r"\w+, \w+(,)? and \w+"), ("a year", r"\b(19|20)\d\d\b"),
    ("money", r"(£|\$)\d|\bpence\b|\bpounds\b"), ("a percentage", r"\d+ ?(%|percent)"),
    ("a decimal or version", r"\b\d+\.\d+\b"), ("an HTTP status code", r"\b(200|201|404|409|500)\b"),
    ("a number range", r"\b\d+ (to|-) \d+\b"), ("a large number", r"\b\d{1,3}(,\d{3})+\b|\b\d{5,}\b"),
    ("a slash", r"\w/\w"), ("a semicolon", r";"), ("an abbreviation such as e.g.", r"\b(e\.g\.|i\.e\.|etc\.)"),
    ("a possessive name", r"\b[A-Z][a-z]+'s\b")]


def sentences():
    """Every sentence the decks say, with its lesson."""
    found = []
    for slides in sorted(glob.glob(os.path.join(ROOT, "lessons/*/media/*.slides.md"))):
        lesson = slides.split(os.sep)[-3]
        found += [(lesson, i["sentence"]) for i in narration.lesson_items(ROOT, lesson)]
    return found


def wanted():
    """(group, phrase) for each line of review-set.txt."""
    group, found = None, []
    for line in narration.read(os.path.join(HERE, "review-set.txt")).split("\n"):
        line = line.strip()
        if line.startswith("## "):
            group = line[3:]
        elif line and not line.startswith("#"):
            found.append((group, line))
    return found


def best(phrase, said):
    hits = [(lesson, s) for lesson, s in said if re.search(r"(?<![\w-])" + re.escape(phrase) + r"(?![\w-])", s)]
    after_the = lambda hit: bool(re.search(r"\b[Tt]he\s+" + re.escape(phrase) + r"(?![\w-])", hit[1]))  # noqa: E731
    return min(hits, key=lambda hit: (after_the(hit), len(hit[1]) if len(hit[1]) > 40 else 999)) if hits else None


def build():
    os.makedirs(REVIEW, exist_ok=True)
    path = os.path.join(REVIEW, "items.json")
    old = json.loads(narration.read(path)) if os.path.exists(path) else []
    numbers = {(i["group"], i["phrase"], i["sentence"] if i["group"] == "Punctuation and numbers" else ""): i["id"]
               for i in old}
    said = sentences()
    still_said = {sentence for _, sentence in said}
    kept = {(i["group"], i["phrase"]): i for i in old if i["sentence"] in still_said}
    lines = []
    for group, phrase in wanted():
        before = kept.get((group, phrase))
        hit = (before["lesson"], before["sentence"]) if before else best(phrase, said)
        if hit:
            lines.append(dict(group=group, phrase=phrase, lesson=hit[0], sentence=hit[1]))
        else:
            print("not in the narration any more:", phrase)
    taken = {line["sentence"] for line in lines}
    for kind, pattern in PUNCTUATION:
        hits = [(i["lesson"], i["sentence"]) for i in old
                if i["group"] == "Punctuation and numbers" and i["phrase"] == kind and i["sentence"] in still_said]
        hits += sorted((h for h in said if re.search(pattern, h[1]) and h[1] not in taken and h not in hits),
                       key=lambda h: abs(len(h[1]) - 110))[:max(0, 2 - len(hits))]
        for lesson, sentence in hits:
            lines.append(dict(group="Punctuation and numbers", phrase=kind, lesson=lesson, sentence=sentence))
            taken.add(sentence)
    next_number = max([i["id"] for i in old] + [0]) + 1
    for line in lines:
        key = (line["group"], line["phrase"], line["sentence"] if line["group"] == "Punctuation and numbers" else "")
        if key not in numbers:
            numbers[key], next_number = next_number, next_number + 1
        line["id"] = numbers[key]
    lines.sort(key=lambda line: line["id"])
    sentence_of = {i["id"]: i["sentence"] for i in old}
    for line in lines:  # a line whose sentence changed is voiced again
        if sentence_of.get(line["id"], line["sentence"]) != line["sentence"]:
            for ending in (".wav", ".txt"):
                wav = os.path.join(REVIEW, "%03d%s" % (line["id"], ending))
                if os.path.exists(wav):
                    os.remove(wav)
    narration.write(path, json.dumps(lines, ensure_ascii=False, indent=1))
    voice_missing(lines)
    print(len(lines), "lines in", os.path.relpath(path, ROOT))


def voice_missing(lines):
    """Voices each line with no clip yet, with the pronunciation list applied, as the decks apply it."""
    table = dict(narration.entries(LIST))
    todo = []
    for line in lines:
        wav = os.path.join(REVIEW, "%03d.wav" % line["id"])
        if not os.path.exists(wav):
            text = os.path.join(REVIEW, "%03d.txt" % line["id"])
            narration.write(text, narration.apply_list(line["sentence"], table))
            todo.append(text + "\t" + wav)
    if todo:
        listing = os.path.join(REVIEW, "to-voice.txt")
        narration.write(listing, "\n".join(todo) + "\n")
        subprocess.run([sys.executable, SPEAK, "--list", listing], check=True, cwd=ROOT)
        print(len(todo), "clips voiced")


if __name__ == "__main__":
    build()
