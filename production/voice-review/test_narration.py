"""Tests of the voice review's narration handling, with a made-up lesson in a temporary folder and a fake voice.

    python3 -m unittest discover -s production/voice-review
"""
import os
import tempfile
import unittest

import narration

SLIDES = """# Demo

## intro: Our example
- A shop
Say: Our example is a small shop. It sells
  kettles. [pause 400ms] Each order has a safety net.

## closing: Find out more
Say: To find out more, read the guide.
"""
SCENES = """# Narration: demo

1. Here is the code under test, today's loan. A loan is requested, then approved, then repaid.
"""


def fake(words):
    """A stand-in for the voice's phonemizer: each word stressed on its first letter, in lower case."""
    return " ".join("ˈ" + w.lower() for w in words.split())


class Lesson(unittest.TestCase):
    def setUp(self):
        self.root = tempfile.mkdtemp()
        media = os.path.join(self.root, "lessons", "demo", "media")
        os.makedirs(media)
        self.slides = os.path.join(media, "demo.slides.md")
        self.scenes = os.path.join(media, "demo.narration.md")
        narration.write(self.slides, SLIDES)
        narration.write(self.scenes, SCENES)

    def test_lists_every_sentence_in_the_decks_order_opening_scenes_closing(self):
        items = narration.lesson_items(self.root, "demo")
        self.assertEqual(
            [(i["group"], i["sentence"]) for i in items],
            [("Our example", "Our example is a small shop."), ("Our example", "It sells kettles."),
             ("Our example", "Each order has a safety net."),
             ("Scene 1", "Here is the code under test, today's loan."),
             ("Scene 1", "A loan is requested, then approved, then repaid."),
             ("Find out more", "To find out more, read the guide.")])
        self.assertEqual({i["lesson"] for i in items}, {"demo"})

    def test_writes_a_change_across_line_breaks_and_pauses(self):
        changed = narration.apply_to_lesson(self.root, "demo", "It sells kettles. Each order",
                                            "It sells kettles and toasters. [pause 400ms] Each order")
        text = narration.read(self.slides)
        self.assertTrue(changed)
        self.assertIn("Say: Our example is a small shop. It sells kettles and toasters. [pause 400ms] Each order has a "
                      "safety net.\n", text)
        self.assertIn("## closing: Find out more\nSay: To find out more, read the guide.\n", text)

    def test_writes_a_scene_line_and_wraps_it(self):
        longer = "Here is the code under test, today's loan, " + "which the shop has run for many years " * 3 + "."
        self.assertTrue(narration.apply_to_lesson(self.root, "demo", "Here is the code under test, today's loan.",
                                                  longer))
        lines = narration.read(self.scenes).split("\n")
        self.assertTrue(lines[2].startswith("1. Here is"))
        self.assertTrue(lines[3].startswith("   "))
        self.assertTrue(all(len(line) <= narration.WIDTH for line in lines))

    def test_a_sentence_not_in_the_lesson_is_left_alone(self):
        self.assertFalse(narration.apply_to_lesson(self.root, "demo", "Nothing says this.", "Anything."))
        self.assertEqual(narration.read(self.slides), SLIDES)


class Words(unittest.TestCase):
    def test_articles_are_weak_and_the_is_thee_before_a_vowel(self):
        found = narration.tokens("The order is a kettle.", {}, fake)
        self.assertEqual([t.get("sounds") for t in found], ["ði", "ˈorder", "ˈis", "ɐ", "ˈkettle", None])

    def test_a_rule_gives_a_word_its_sounds(self):
        found = narration.tokens("JaCoCo counts.", {"JaCoCo": "/dʒakˈəʊkəʊ/"}, fake)
        self.assertEqual(found[0]["sounds"], "dʒakˈəʊkəʊ")

    def test_a_phrase_rule_said_as_one_word_marks_the_join(self):
        found = narration.tokens("a safety net", {"safety net": "/ˈsafetynet/"}, fake)
        self.assertEqual([(t["word"], t["sounds"], t.get("joinPrev")) for t in found[1:]],
                         [("safety", "ˈsafety", False), ("net", "net", True)])

    def test_a_phrase_rule_with_a_sound_for_each_word(self):
        found = narration.tokens("tests we have now", {"tests we have now": "/tˈɛsts wiː hˈav nˈaʊ/"}, fake)
        self.assertEqual([t["sounds"] for t in found], ["tˈɛsts", "wiː", "hˈav", "nˈaʊ"])


class Stress(unittest.TestCase):
    def word(self, written, sounds):
        return {"word": written, "sounds": sounds}

    def test_a_join_makes_a_rule_for_the_two_words(self):
        found = [self.word("a", "ɐ"), self.word("test", "tˈɛst"), self.word("dependency", "dɪpˈɛndənsi")]
        state = [{"st": ["weak"]}, {"st": ["strong"]}, {"st": ["weak", "weak", "weak", "weak"], "joinPrev": True}]
        self.assertEqual(narration.stress_rules(found, state), [("test dependency", "/tˈɛstdɪpɛndənsi/")])

    def test_stress_on_a_small_word_covers_its_whole_stretch(self):
        found = [self.word("can", "kan"), self.word("the", "ðə"), self.word("tests", "tˈɛsts"), {"punct": "?"}]
        state = [{"st": ["strong"]}, {"st": ["weak"]}, {"st": ["strong"]}, None]
        self.assertEqual(narration.stress_rules(found, state), [("can the tests", "/kˈan ðə tˈɛsts/")])

    def test_an_unchanged_sentence_needs_no_rule(self):
        found = [self.word("flaky", "flˈeɪki"), self.word("tests", "tˈɛsts")]
        self.assertEqual(narration.stress_rules(found, [{"st": ["strong", "weak"]}, {"st": ["strong"]}]), [])

    def test_a_syllable_round_trip_keeps_the_sounds(self):
        for sounds in ("dʒakˈəʊkəʊ", "tˈɛstkəntˌeɪnəz", "lˈɪkwɪbeɪs", "ˌɛskjˌuːˈɛl"):
            self.assertEqual(narration.stressed(sounds, narration.syllables(sounds)[2]), sounds)


class List(unittest.TestCase):
    def test_a_rule_is_added_under_its_heading_then_replaced(self):
        path = os.path.join(tempfile.mkdtemp(), "pronunciations.txt")
        narration.write(path, "# The list\nPIT = pit\n")
        narration.save_rule(path, "safety net", "/sˈeɪfti nɛt/", "# From the voice review.")
        narration.save_rule(path, "safety net", "/sˈeɪftinɛt/", "# From the voice review.")
        self.assertEqual(narration.read(path),
                         "# The list\nPIT = pit\n\n# From the voice review.\nsafety net = /sˈeɪftinɛt/\n")

    def test_the_list_applies_the_longest_match_first(self):
        table = {"code": "kode", "old code": "/əʊld kˈəʊd/"}
        self.assertEqual(narration.apply_list("the old code and the code", table), "the /əʊld kˈəʊd/ and the kode")


if __name__ == "__main__":
    unittest.main()
