"""The voice review server: serves the review page, voices a sentence as it is changed, and saves each change. See
README.md. Production only: it runs the lesson voice (Kokoro), set up by :production:video-render:setUpLessonVoice.

    ./gradlew :production:voice-review:reviewVoice          (or, from the repository root:)
    build/voice/venv/bin/python production/voice-review/server.py [port]
"""
import functools
import importlib.util
import json
import os
import re
import sys
import threading
import time
from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import narration  # noqa: E402

ROOT = os.path.abspath(os.path.join(HERE, "..", ".."))
REVIEW = os.path.join(ROOT, "build/voice-review")
LIST = os.path.join(ROOT, "lessons/_shared/pronunciations.txt")
RESULTS = os.path.join(REVIEW, "results.json")
PAGE = os.path.join(HERE, "page.html")
SECTION = "# From the voice review: stress chosen by ear."
PAUSE_MARK = "‖"  # a pause in what the page asks for: silence, as [pause 400ms] is in the decks
PAUSE_SECONDS = 0.4
VOICE = dict(voice=os.environ.get("KOKORO_VOICE", "bf_emma"), speed=float(os.environ.get("KOKORO_SPEED", "1.0")),
             lang="en-gb")

spec = importlib.util.spec_from_file_location("speak", os.path.join(ROOT, "production/video-render/speak-kokoro.py"))
speak = importlib.util.module_from_spec(spec)
spec.loader.exec_module(speak)

import numpy  # noqa: E402
import soundfile  # noqa: E402
from kokoro_onnx import Kokoro  # noqa: E402
from kokoro_onnx.tokenizer import Tokenizer  # noqa: E402

MODELS = os.environ.get("KOKORO_MODELS", os.path.join(ROOT, "build/voice"))
kokoro = Kokoro(os.path.join(MODELS, "kokoro-v1.0.onnx"), os.path.join(MODELS, "voices-v1.0.bin"))
tokenizer = Tokenizer()
LOCK = threading.RLock()  # one thing voiced at a time: two at once on one model only slow each other


@functools.lru_cache(maxsize=None)
def phonemize(words):
    with LOCK:
        return tokenizer.phonemize(words, "en-gb")


def table():
    return dict(narration.entries(LIST))


def voice(text, out):
    """Voices text (words, words with /sounds/, or all sounds between slashes) into a .wav; ‖ is a pause."""
    with LOCK:
        sounds = text.startswith("/") and text.endswith("/")
        parts = [p.strip() for p in (text.strip("/") if sounds else text).split(PAUSE_MARK) if p.strip()]
        pieces, rate = [], 24000
        for part in parts:
            if sounds:
                samples, rate = kokoro.create(part, is_phonemes=True, **VOICE)
            elif speak.SOUNDS.search(part):
                samples, rate = kokoro.create(speak.to_sounds(part, phonemize), is_phonemes=True, **VOICE)
            else:
                samples, rate = kokoro.create(part, **VOICE)
            pieces += [samples, numpy.zeros(int(rate * PAUSE_SECONDS), dtype=samples.dtype)]
        soundfile.write(out, numpy.concatenate(pieces[:-1]), rate)


def results():
    return json.loads(narration.read(RESULTS)) if os.path.exists(RESULTS) else {}


def record(answer):
    """Keeps a line's answer, merged with what it had: every click saves its state, the tick marks it done."""
    kept = results()
    kept[str(answer["id"])] = {**kept.get(str(answer["id"]), {}), **answer}
    narration.write(RESULTS, json.dumps(kept, ensure_ascii=False, indent=1))


def save(body):
    """A line's answer. On the tick, a changed phrase becomes a rule, and changed words or pauses are written into
    the lesson's narration."""
    if body.get("rule") and body.get("sounds"):
        narration.save_rule(LIST, body["phrase"], body["sounds"], SECTION)
    for word in body.get("word_rules") or [] if body.get("rule") else []:  # a word's stress moved: its default
        narration.save_rule(LIST, word["word"], word["sounds"], SECTION)
    if body.get("rule") and body.get("state"):  # the rest of the sentence's stress and joins: rules for this sentence
        found = narration.tokens(narration.plain(body.get("text") or body["sentence"]), table(), phonemize)
        made = narration.stress_rules(found, body["state"])
        for written, sounds in made:
            narration.save_rule(LIST, written, sounds, SECTION)
        body["rules_made"] = [written for written, _ in made]
    if body.get("rule") and body.get("written") and body.get("lesson"):
        before = results().get(str(body["id"]), {}).get("in_lesson") or body["sentence"]
        if before != body["written"]:
            if not narration.apply_to_lesson(ROOT, body["lesson"], before, body["written"]):
                return {"error": "That sentence isn't in the lesson's narration any more."}
            body["in_lesson"] = body["written"]
    record(body)
    return {"saved": True}


class Handler(SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=REVIEW, **kwargs)

    def log_message(self, *args):
        pass

    def end_headers(self):
        # A published deck (https) asks whether this runs here: allow it, and Chrome's private network check.
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Access-Control-Allow-Private-Network", "true")
        self.send_header("Access-Control-Allow-Headers", "Content-Type")
        self.send_header("Cache-Control", "no-store")
        super().end_headers()

    def reply(self, body):
        data = json.dumps(body, ensure_ascii=False).encode("utf-8")
        self.send_response(200)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(data)))
        self.end_headers()
        self.wfile.write(data)

    def do_OPTIONS(self):
        self.send_response(204)
        self.end_headers()

    def do_GET(self):
        path = self.path.split("?", 1)[0]
        if path == "/ping":
            self.reply({"voiceReview": True})
        elif path == "/" or path.startswith("/lesson/"):
            data = narration.read(PAGE).encode("utf-8")
            self.send_response(200)
            self.send_header("Content-Type", "text/html; charset=utf-8")
            self.send_header("Content-Length", str(len(data)))
            self.end_headers()
            self.wfile.write(data)
        elif path == "/items":
            items = os.path.join(REVIEW, "items.json")
            self.reply(json.loads(narration.read(items)) if os.path.exists(items) else [])
        elif path.startswith("/items/"):
            lesson = path[len("/items/"):]
            if not re.fullmatch(r"[a-z0-9-]+", lesson) or not os.path.isdir(os.path.join(ROOT, "lessons", lesson)):
                self.send_error(404)
                return
            items = narration.lesson_items(ROOT, lesson)
            reworded = {narration.plain(v["in_lesson"]): k for k, v in results().items()
                        if v.get("in_lesson") and v.get("lesson") == lesson}
            for item in items:  # a line reworded on the page keeps its answer under its new words
                item["id"] = reworded.get(item["sentence"], item["id"])
            self.reply(items)
        else:
            super().do_GET()

    def do_POST(self):
        body = json.loads(self.rfile.read(int(self.headers["Content-Length"])) or "{}")
        try:
            if self.path == "/sentences":
                rules = table()
                self.reply({"tokens": [narration.tokens(s, rules, phonemize) for s in body["sentences"]]})
            elif self.path == "/sentence":
                self.reply({"tokens": narration.tokens(body["sentence"], table(), phonemize)})
            elif self.path == "/try":
                text = body.get("raw") or narration.apply_list(body["sentence"], table())
                name = "try-%s-%d.wav" % (re.sub(r"[^\w-]", "-", str(body["id"])), int(time.time() * 1000))
                voice(text, os.path.join(REVIEW, name))
                self.reply({"url": name})
            elif self.path == "/save":
                self.reply(save(body))
            elif self.path == "/results":
                self.reply(results())
            else:
                self.send_error(404)
        except Exception as e:  # tell the page, and keep serving
            self.reply({"error": str(e)})


if __name__ == "__main__":
    os.makedirs(REVIEW, exist_ok=True)
    port = int(sys.argv[1]) if len(sys.argv) > 1 else 8765
    print("Voice review: http://localhost:%d/" % port, flush=True)
    ThreadingHTTPServer(("127.0.0.1", port), Handler).serve_forever()
