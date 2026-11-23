// Drives lessons/_shared/slides/deck.js with a stand-in page, the way a browser that guards autoplay does (Safari, and
// others): an audio element may start playing without a click only if a click has already started that same element.
// Run with: node deck-listen.test.js <path to deck.js>. Prints "ok" lines and exits 1 on the first failure.
const fs = require('fs');
const script = fs.readFileSync(process.argv[2], 'utf8');
let failures = 0;
function check(what, ok) { console.log((ok ? 'ok   ' : 'FAIL ') + what); if (!ok) { failures++; } }

let clickInProgress = false;
const started = [];
const everyAudio = [];
class FakeAudio {
  constructor(src) { everyAudio.push(this); this.src = src || ''; this.unlocked = false; this.paused = true; this.listeners = {}; this.currentTime = 0; }
  play() {
    if (!clickInProgress && !this.unlocked) { return Promise.reject(new Error('NotAllowedError')); }
    this.unlocked = true; this.paused = false; started.push(this.src); return Promise.resolve();
  }
  pause() { this.paused = true; }
  addEventListener(name, fn) { (this.listeners[name] = this.listeners[name] || []).push(fn); }
  finish() { this.paused = true; if (this.onended) { this.onended(); } (this.listeners.ended || []).forEach(fn => fn()); }
  getAttribute(name) { return name === 'src' ? this.src : null; }
}
function element(tag) {
  return { tagName: tag, className: '', textContent: '', attributes: {}, listeners: {}, classList: { toggle() {}, add() {}, remove() {} },
    setAttribute(k, v) { this.attributes[k] = v; }, getAttribute(k) { return this.attributes[k]; },
    addEventListener(n, fn) { (this.listeners[n] = this.listeners[n] || []).push(fn); },
    click() { clickInProgress = true; (this.listeners.click || []).forEach(fn => fn({ target: this })); clickInProgress = false; },
    querySelector() { return null; }, closest() { return this; } };
}
const clips = ['a.m4a', 'b.m4a', 'c.m4a'].map(src => new FakeAudio(src));
const slides = clips.map(clip => Object.assign(element('section'), { querySelector: s => (s === 'audio.voice' ? clip : null) }));
const appended = [];
const documentListeners = {};
global.window = { innerWidth: 1920, innerHeight: 1080, addEventListener() {} };
global.location = { hash: '', search: '' };
global.history = { replaceState() {} };
global.Audio = FakeAudio;
const timers = [];
const delays = [];
global.setTimeout = (fn, ms) => { timers.push(fn); delays.push(ms); return timers.length; };
global.clearTimeout = id => { if (id) { timers[id - 1] = null; } };
function runTimers() { while (timers.some(Boolean)) { const i = timers.findIndex(Boolean); const fn = timers[i]; timers[i] = null; fn(); } }
global.document = {
  documentElement: { style: { setProperty() {} } },
  body: Object.assign(element('body'), { appendChild(el) { appended.push(el); } }),
  createElement: tag => element(tag),
  querySelectorAll: s => (s === '.deck-slide' ? slides : []),
  querySelector: s => (s === 'audio.voice' ? clips[0] : null),
  addEventListener(n, fn) { (documentListeners[n] = documentListeners[n] || []).push(fn); },
  dispatchEvent(e) { (documentListeners[e.type] || []).forEach(fn => fn(e)); }
};
global.CustomEvent = function (type) { this.type = type; };

eval(script);
const listen = appended.find(el => el.className === 'deck-listen');
check('a deck with voice has a Listen button', Boolean(listen));
listen.click();
check('Listen plays the first slide', started[0] === 'a.m4a');
let finished = false;
document.addEventListener('deck-voice-finished', () => { finished = true; });
for (let i = 0; i < 3; i++) {
  const playing = everyAudio.find(a => !a.paused);
  if (playing) { playing.finish(); }
  runTimers();
}
check('the second slide plays after the first, without another click', started[1] === 'b.m4a');
check('the third slide plays after the second', started[2] === 'c.m4a');
check('the deck says when its last slide has been read', finished);
check('the next slide follows its narration after a short gap, under a fifth of a second', delays.length > 0 && delays.every(ms => ms < 200));

// Moving by hand, not listening: the Back and Next buttons are there, each move reads that slide's narration, and the
// deck stays on it when the narration ends.
const back = appended.find(el => el.className.indexOf('deck-back') >= 0);
const next = appended.find(el => el.className.indexOf('deck-next') >= 0);
check('every deck has a Back and a Next button', Boolean(back) && Boolean(next));
const counter = appended.find(el => el.className === 'deck-counter');
back.click();
check('Back goes to the slide before', counter.textContent === '2 / 3');
check('moving back reads that slide', started[started.length - 1] === 'b.m4a');
everyAudio.find(a => !a.paused).finish();
runTimers();
check('a slide reached by hand stays when its narration ends', counter.textContent === '2 / 3');
next.click();
check('Next reads the next slide', counter.textContent === '3 / 3' && started[started.length - 1] === 'c.m4a');
// Arriving with no click yet (the index's listen link, or the last lesson carrying on): the browser refuses to play,
// so the deck asks for a click, and the next click on the page starts it. A fresh deck, as a newly opened page has.
const freshClips = ['x.m4a', 'y.m4a'].map(src => new FakeAudio(src));
const freshSlides = freshClips.map(clip => Object.assign(element('section'), { querySelector: s => (s === 'audio.voice' ? clip : null) }));
appended.length = 0;
Object.keys(documentListeners).forEach(name => delete documentListeners[name]);
global.document.querySelectorAll = s => (s === '.deck-slide' ? freshSlides : []);
global.document.querySelector = s => (s === 'audio.voice' ? freshClips[0] : null);
eval(script);
const freshListen = appended.find(el => el.className === 'deck-listen');
const before = started.length;
(documentListeners.keydown || []).forEach(fn => fn({ key: 'v', preventDefault() {} })); // no click: refused
Promise.resolve().then(() => Promise.resolve()).then(() => {
  check('a start the browser refuses asks for a click, rather than falling silent', freshListen.textContent === 'Click to listen');
  clickInProgress = true;
  (documentListeners.click || []).forEach(fn => fn({ target: { closest: () => null }, clientX: 1500 }));
  clickInProgress = false;
  check('the next click on the page starts the voice', started.length > before && freshListen.textContent === 'Pause');
  process.exit(failures ? 1 : 0);
});
