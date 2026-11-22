"""Reads a text file aloud with the Kokoro voice (offline; model Apache-2.0, kokoro-onnx MIT) and writes a WAV file.

    python speak-kokoro.py <text file> <output .wav>

Needs the kokoro-onnx and soundfile packages, and the model files kokoro-v1.0.onnx and voices-v1.0.bin in the folder
named by KOKORO_MODELS (default: build/voice). Voice and speed come from KOKORO_VOICE (default bf_emma, a British
English voice) and KOKORO_SPEED (default 1.0). Any other speech tool can replace this script: the video tools only
need a command that turns a text file into a WAV file.

A word's sounds can be given exactly, between slashes, with its stress marked (ˈ or ˌ): "an /ˈaʊtsaɪd ˈɪn/ test".
The pronunciation list (lessons/_shared/pronunciations.txt) uses this where spelling a word differently can't put the
stress in the right place. The rest of the text is turned into sounds as usual, and the voice reads them together.
"""
import os
import re
import sys

# Sounds given exactly: between slashes, with a stress mark, so an ordinary "and/or" is left alone.
SOUNDS = re.compile(r"/([^/\s][^/]*[ˈˌ][^/]*)/")


def to_sounds(text, phonemize):
    """The whole text as sounds: the parts given between slashes kept as they are, the rest turned into sounds by
    phonemize, with the spaces between them kept."""
    pieces = []
    for i, part in enumerate(SOUNDS.split(text)):
        if i % 2:
            pieces.append(part)
            continue
        middle = part.strip()
        lead = " " if part[:1].isspace() else ""
        trail = " " if part[-1:].isspace() else ""
        pieces.append(lead + (phonemize(middle) if middle else "") + trail)
    return "".join(pieces)


def main(text_file, out_file):
    import soundfile  # here, so to_sounds can be tested without the voice installed
    from kokoro_onnx import Kokoro
    from kokoro_onnx.tokenizer import Tokenizer

    models = os.environ.get("KOKORO_MODELS", "build/voice")
    kokoro = Kokoro(os.path.join(models, "kokoro-v1.0.onnx"), os.path.join(models, "voices-v1.0.bin"))
    with open(text_file, encoding="utf-8") as f:
        text = f.read().strip()
    settings = dict(voice=os.environ.get("KOKORO_VOICE", "bf_emma"),
                    speed=float(os.environ.get("KOKORO_SPEED", "1.0")), lang="en-gb")
    if SOUNDS.search(text):
        tokenizer = Tokenizer()
        sounds = to_sounds(text, lambda words: tokenizer.phonemize(words, "en-gb"))
        samples, rate = kokoro.create(sounds, is_phonemes=True, **settings)
    else:
        samples, rate = kokoro.create(text, **settings)
    soundfile.write(out_file, samples, rate)


if __name__ == "__main__":
    if len(sys.argv) != 3:
        sys.exit("usage: speak-kokoro.py <text file> <output .wav>")
    main(sys.argv[1], sys.argv[2])
