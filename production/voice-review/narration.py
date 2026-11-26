"""The parts of the voice review that need no voice: a lesson's sentences, a change written back into a lesson's
narration, the pronunciation list, and a sentence split into words with their sounds. The sounds come from a phonemize
function passed in, so all of this is tested without the voice installed (test_narration.py).
"""
import hashlib
import os
import re
import textwrap

# Weak forms, as speak-kokoro.py gives them: an article is said weakly, and "the" is "thee" before a vowel sound.
WEAK = {"a": "ɐ", "an": "ən", "the": "ðə"}
BEFORE_A_VOWEL = {"the": "ði"}
VOWELS = "aeiouæɑɐɒɔəɛɜɪʊʌ"

PAUSE = re.compile(r"\[pause \d+m?s\]\s*")
WIDTH = 120  # the narration files' line length
SENTENCE_END = re.compile(r"(?<=[.?!])\s+(?=[A-Z0-9\"])")
TOKEN = re.compile(r"[\w][\w'’.+#-]*[\w]|[\w]|[^\w\s]")


def read(path):
    with open(path, encoding="utf-8") as f:
        return f.read()


def write(path, text):
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)


def entries(list_file):
    """The pronunciation list as (written, said) pairs, longest written form first, as the decks apply it."""
    found = []
    for line in read(list_file).split("\n"):
        if line.startswith("#") or " = " not in line:
            continue
        written, _, said = line.rstrip("\n").partition(" = ")
        found.append((written.strip(), said.strip()))
    return sorted(found, key=lambda e: -len(e[0]))


def apply_list(text, table):
    """The text with the list's rules applied: whole words, matching case, the longest first."""
    words = sorted(table, key=len, reverse=True)
    if not words:
        return text
    pattern = re.compile("|".join(r"(?<![\w-])" + re.escape(w) + r"(?![\w-])" for w in words))
    return pattern.sub(lambda m: table[m.group(0)], text)


def save_rule(list_file, phrase, sounds, section):
    """Adds the phrase's rule to the list, or replaces the rule it already has."""
    lines = read(list_file).rstrip("\n").split("\n")
    rule = phrase + " = " + sounds
    for i, line in enumerate(lines):
        if not line.startswith("#") and line.partition(" = ")[0].strip() == phrase:
            lines[i] = rule
            break
    else:
        if section not in lines:
            lines += ["", section]
        lines.append(rule)
    write(list_file, "\n".join(lines) + "\n")


def _paragraphs(root, lesson):
    """(file, shape, indent) for the lesson's narration: slides' Say paragraphs, then scenes' numbered lines."""
    media = os.path.join(root, "lessons", lesson, "media")
    return [(os.path.join(media, lesson + ".slides.md"), re.compile(r"^(Say: )(.*?)(?=\n\S|\n\n|\Z)", re.S | re.M), "  "),
            (os.path.join(media, lesson + ".narration.md"),
             re.compile(r"^(\d+\. )(.*?)(?=\n\d+\. |\n\n|\Z)", re.S | re.M), "   ")]


def lesson_items(root, lesson):
    """Every sentence a lesson's deck says, in the deck's order, each under its slide's title or scene's number."""
    opening, closing, scenes = [], [], []
    slides = os.path.join(root, "lessons", lesson, "media", lesson + ".slides.md")
    for block in re.split(r"^## ", read(slides), flags=re.M)[1:]:
        kind, _, title = block.split("\n", 1)[0].partition(":")
        said = re.search(r"^Say: (.*?)(?=\n\S|\n\n|\Z)", block, re.S | re.M)
        if said:
            (closing if kind.strip() == "closing" else opening).append((title.strip(), said.group(1)))
    narration = _paragraphs(root, lesson)[1]
    if os.path.exists(narration[0]):
        for m in narration[1].finditer(read(narration[0])):
            scenes.append(("Scene " + m.group(1).rstrip(". "), m.group(2)))
    paragraphs = opening + scenes + closing  # the deck's order: opening slides, the scenes, closing slides
    found, seen = [], {}
    for title, text in paragraphs:
        for sentence in SENTENCE_END.split(PAUSE.sub("", re.sub(r"\s+", " ", text)).strip()):
            key = line_id(lesson, plain(sentence))
            seen[key] = seen.get(key, 0) + 1
            found.append({"id": key if seen[key] == 1 else "%s-%d" % (key, seen[key]), "group": title, "phrase": "",
                          "sentence": sentence, "lesson": lesson})
    return found


def line_id(lesson, sentence):
    """A lesson line's name, from its words, so a saved answer stays with its sentence when lines move around it."""
    return "%s:%s" % (lesson, hashlib.sha1(sentence.encode("utf-8")).hexdigest()[:10])


def apply_to_lesson(root, lesson, old, new):
    """Writes a changed sentence into the lesson's narration, in place of the old one: the slide's Say paragraph or
    the scene's numbered line that holds it, found across line breaks and pauses, then rewrapped. True when written."""
    between = r"\s+(?:\[pause \d+m?s\]\s+)?"
    wanted = re.compile(between.join(re.escape(chunk) for chunk in old.split()))
    for path, paragraph, indent in _paragraphs(root, lesson):
        if not os.path.exists(path):
            continue
        text = read(path)
        for m in paragraph.finditer(text):
            flat = re.sub(r"\s+", " ", m.group(2)).strip()
            hit = wanted.search(flat)
            if not hit:
                continue
            changed = flat[:hit.start()] + new + flat[hit.end():]
            wrapped = textwrap.fill(changed, width=WIDTH, initial_indent=m.group(1), subsequent_indent=indent,
                                    break_on_hyphens=False, break_long_words=False)
            write(path, text[:m.start()] + wrapped + text[m.end():])
            return True
    return False


def _phrase_rules(found, table, phonemize):
    """Gives words a rule for several words covers its sounds: one sound for each word or, where the rule says them
    as one word, each word its share of the joined sounds, the later ones marked as joined to the word before."""
    words = [i for i, t in enumerate(found) if "word" in t]
    for rule in sorted((r for r in table if " " in r and table[r].startswith("/")), key=lambda r: -len(r.split())):
        parts, said = rule.split(), table[rule][1:-1].split(" ")
        for a in range(len(words) - len(parts) + 1):
            run = [found[words[a + j]] for j in range(len(parts))]
            if [t["word"] for t in run] != parts or any(t.get("ruled") for t in run):
                continue
            if len(said) == len(parts):
                for t, sounds in zip(run, said):
                    t["sounds"], t["ruled"] = sounds, True
            elif len(said) == 1:
                joined, at = said[0], 0
                lengths = [len(re.sub("[ˈˌ]", "", phonemize(t["word"]))) for t in run]
                for j, t in enumerate(run):
                    take, piece = lengths[j] if j < len(run) - 1 else 10 ** 6, ""
                    while at < len(joined) and (take > 0 or joined[at] in "ˈˌ"):
                        take -= 0 if joined[at] in "ˈˌ" else 1
                        piece += joined[at]
                        at += 1
                    t["sounds"], t["ruled"], t["joinPrev"] = piece or t["sounds"], True, j > 0


def tokens(sentence, table, phonemize):
    """The sentence as words and punctuation, each word with the sounds the voice gives it: the list's rules first,
    a phrase's included; the plain words sounded together where that gives one sound a word, else one by one; and the
    articles in their weak forms."""
    found = [{"word": t} if re.match(r"\w", t) else {"punct": t} for t in TOKEN.findall(sentence)]
    plain = [t for t in found if "word" in t and t["word"] not in table]
    together = phonemize(" ".join(t["word"] for t in plain)).split() if plain else []
    for t, sounds in zip(plain, together if len(together) == len(plain) else [None] * len(plain)):
        t["sounds"] = sounds or phonemize(t["word"]).replace(" ", "")
    for t in found:
        if "word" in t and "sounds" not in t:
            said = table[t["word"]]
            t["sounds"] = (said[1:-1] if said.startswith("/") else phonemize(said)).replace(" ", "")
    _phrase_rules(found, table, phonemize)
    for i, t in enumerate(found):
        article = t.get("word", "").lower()
        if article in WEAK:
            after = next((x["sounds"] for x in found[i + 1:] if "word" in x), "")
            vowel = after.lstrip("ˈˌ")[:1] in VOWELS
            t["sounds"] = BEFORE_A_VOWEL.get(article, WEAK[article]) if vowel else WEAK[article]
    return found


# How the page shows a word: a syllable for each vowel sound, each strong (ˈ), light (ˌ) or weak; page.html does the same.
SYLLABLE_VOWELS = "aeiouæɑɐɒɔəɛɜɪʊʌɚɝœøyɨʉ"
MARKS = {"strong": "ˈ", "light": "ˌ", "weak": ""}
SMALL = set("a an the and or but of to in on at for with by from as is are was were be been it its this that these those "
            "which who what when where how why than then so if not no do does did can could will would should may might "
            "must has have had we you they he she i our your their there here one each every all any some just only also "
            "into out up over after before about such same other another both".split())


def plain(sentence):
    """A sentence without its pauses, as the page and the decks' sentences are compared."""
    return re.sub(r"\s+", " ", PAUSE.sub("", sentence)).strip()


def syllables(sounds):
    """A word's sounds as (bare sounds, where each vowel sound starts, each syllable's stress)."""
    marks, bare = {}, ""
    for c in sounds:
        if c in "ˈˌ":
            marks[len(bare)] = c
        else:
            bare += c
    starts, i = [], 0
    while i < len(bare):
        if bare[i] in SYLLABLE_VOWELS:
            starts.append(i)
            while i < len(bare) and bare[i] in SYLLABLE_VOWELS:
                i += 1
            if i < len(bare) and bare[i] == "ː":
                i += 1
        else:
            i += 1
    if not starts:
        return bare, [0], ["strong"]
    onsets = [0] + [s - 1 if bare[s - 1] not in SYLLABLE_VOWELS else s for s in starts[1:]]
    stress = []
    for k, onset in enumerate(onsets):
        end = onsets[k + 1] if k + 1 < len(onsets) else len(bare)
        found = [marks[p] for p in marks if onset <= p < end]
        stress.append("strong" if "ˈ" in found else "light" if "ˌ" in found else "weak")
    return bare, starts, stress


def stressed(sounds, stress):
    """A word's sounds with each syllable's stress marked just before its vowel, as the voice expects."""
    bare, starts, _ = syllables(sounds)
    out, last = "", 0
    for k, start in enumerate(starts):
        out += bare[last:start] + MARKS[stress[k]]
        last = start
    return out + bare[last:]


def stress_rules(found, state):
    """The rules a sentence's changed stress and joins need, as (written, sounds). A run of words between punctuation
    and pauses is cut down to the changed words, with the word before each join; a stress on a small word ("can",
    "is") covers the whole run, so the rule belongs to this sentence alone; one word takes a neighbour, so a rule is
    always a phrase. A word's own pronunciation (its stress moved) is a word rule, made elsewhere."""
    if len(state) != len(found):
        return []
    runs, changed = [[]], set()
    for i, (t, c) in enumerate(zip(found, state)):
        if "word" not in t:
            runs.append([])
            continue
        if c and (c.get("pausePrev") or c.get("longPause") or c.get("dropPunct")):
            runs.append([])
        runs[-1].append(i)
        if not c:
            continue
        if c["st"] != syllables(t["sounds"])[2]:
            changed.add(i)
        if c.get("joinPrev") and not t.get("joinPrev"):
            changed.add(i)
            if len(runs[-1]) > 1:
                changed.add(runs[-1][-2])
    rules = []
    for run in runs:
        hit = [i for i in run if i in changed]
        if not hit:
            continue
        a, b = run.index(hit[0]), run.index(hit[-1])
        if any(found[i]["word"].lower() in SMALL for i in hit):
            a, b = 0, len(run) - 1
        elif a == b:
            if b + 1 < len(run):
                b += 1
            elif a > 0:
                a -= 1
            else:
                continue
        span = run[a:b + 1]
        sounds = "".join((" " if j and not state[i].get("joinPrev") else "") + stressed(found[i]["sounds"], state[i]["st"])
                         for j, i in enumerate(span))
        rules.append((" ".join(found[i]["word"] for i in span), "/" + sounds + "/"))
    return rules
