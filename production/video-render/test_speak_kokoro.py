"""Tests speak-kokoro.py's handling of sounds given exactly, without the voice installed:
    python3 production/video-render/test_speak_kokoro.py
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
        self.assertEqual("ən ˈaʊtsaɪd ˈɪn <test.>", SPEAK.to_sounds("An /ˈaʊtsaɪd ˈɪn/ test.", fake))

    def test_sounds_at_the_start_and_before_punctuation(self):
        self.assertEqual("ɹɪkˈɔːd <what it does,> ɹɪkˈɔːd<, then.>",
                         SPEAK.to_sounds("/ɹɪkˈɔːd/ what it does, /ɹɪkˈɔːd/, then.", fake))

    def test_sounds_stressed_on_their_first_sound_and_two_runs_in_one_sentence(self):
        self.assertEqual("<In> ði ˈadmɪn peɪdʒ<, then> ði ˈɑːkɪtɛktʃə ɹuːlz<.>",
                         SPEAK.to_sounds("In the /ˈadmɪn peɪdʒ/, then the /ˈɑːkɪtɛktʃə ɹuːlz/.", fake))

    def test_an_article_before_given_sounds_is_said_weakly(self):
        self.assertEqual("ɐ bˈɒdifɪltə <replaces it>", SPEAK.to_sounds("A /bˈɒdifɪltə/ replaces it", fake))
        self.assertEqual("<Then> ðə stˈandɪn <answers>", SPEAK.to_sounds("Then the /stˈandɪn/ answers", fake))

    def test_the_before_a_vowel_is_said_thee(self):
        self.assertEqual("<Change> ði əʊld kˈəʊd", SPEAK.to_sounds("Change the /əʊld kˈəʊd/", fake))

    def test_slashes_without_a_stress_mark_are_ordinary_text(self):
        self.assertEqual("<and/or the input/output>", SPEAK.to_sounds("and/or the input/output", fake))


class ReadListTest(unittest.TestCase):

    def write(self, text):
        import tempfile
        f = tempfile.NamedTemporaryFile("w", suffix=".txt", delete=False, encoding="utf-8")
        f.write(text)
        f.close()
        self.addCleanup(os.unlink, f.name)
        return f.name

    def test_reads_a_text_file_and_a_wav_from_each_line_and_skips_blank_ones(self):
        listed = self.write("a b/one.txt\ta b/one.wav\n\ntwo.txt\ttwo.wav\n")
        self.assertEqual([("a b/one.txt", "a b/one.wav"), ("two.txt", "two.wav")], SPEAK.read_list(listed))

    def test_refuses_a_line_without_both(self):
        with self.assertRaises(ValueError):
            SPEAK.read_list(self.write("one.txt\n"))


if __name__ == "__main__":
    unittest.main()
