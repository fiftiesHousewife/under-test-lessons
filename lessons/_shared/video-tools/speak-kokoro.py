"""Reads a text file aloud with the Kokoro voice (offline; model Apache-2.0, kokoro-onnx MIT) and writes a WAV file.

    python speak-kokoro.py <text file> <output .wav>

Needs the kokoro-onnx and soundfile packages, and the model files kokoro-v1.0.onnx and voices-v1.0.bin in the folder
named by KOKORO_MODELS (default: build/voice). Voice and speed come from KOKORO_VOICE (default bf_emma, a British
English voice) and KOKORO_SPEED (default 1.0). Any other speech tool can replace this script: the video tools only
need a command that turns a text file into a WAV file.
"""
import os
import sys

import soundfile
from kokoro_onnx import Kokoro


def main(text_file, out_file):
    models = os.environ.get("KOKORO_MODELS", "build/voice")
    kokoro = Kokoro(os.path.join(models, "kokoro-v1.0.onnx"), os.path.join(models, "voices-v1.0.bin"))
    with open(text_file, encoding="utf-8") as f:
        text = f.read().strip()
    samples, rate = kokoro.create(text, voice=os.environ.get("KOKORO_VOICE", "bf_emma"),
                                  speed=float(os.environ.get("KOKORO_SPEED", "1.0")), lang="en-gb")
    soundfile.write(out_file, samples, rate)


if __name__ == "__main__":
    if len(sys.argv) != 3:
        sys.exit("usage: speak-kokoro.py <text file> <output .wav>")
    main(sys.argv[1], sys.argv[2])
