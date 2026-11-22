"""Tests speak-kokoro.py's handling of sounds given exactly, without the voice installed:
    python3 lessons/_shared/video-tools/test_speak_kokoro.py
"""
import importlib.util
import os
import unittest

HERE = os.path.dirname(os.path.abspath(__file__))
SPEC = importlib.util.spec_from_file_location("speak_kokoro", os.path.join(HERE, "speak-kokoro.py"))
SPEAK = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(SPEAK)


def fake(words):
    return "<" + words + ">"


class ToSoundsTest(unittest.TestCase):

    def test_text_with_no_sounds_given_is_turned_into_sounds_whole(self):
        self.assertEqual("<An outside test.>", SPEAK.to_sounds("An outside test.", fake))

    def test_sounds_given_are_kept_and_the_spaces_round_them_too(self):
        self.assertEqual("<An> ˈaʊtsaɪd ˈɪn <test.>", SPEAK.to_sounds("An /ˈaʊtsaɪd ˈɪn/ test.", fake))

    def test_sounds_at_the_start_and_before_punctuation(self):
        self.assertEqual("ɹɪkˈɔːd <what it does,> ɹɪkˈɔːd<, then.>",
                         SPEAK.to_sounds("/ɹɪkˈɔːd/ what it does, /ɹɪkˈɔːd/, then.", fake))

    def test_slashes_without_a_stress_mark_are_ordinary_text(self):
        self.assertEqual("<and/or the input/output>", SPEAK.to_sounds("and/or the input/output", fake))


if __name__ == "__main__":
    unittest.main()
