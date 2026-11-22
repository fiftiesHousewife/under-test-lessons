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
  return { tagName: tag, className: '', textContent: '', attributes: {}, listeners: {}, classList: { toggle() {}, add() {} },
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
global.setTimeout = fn => { timers.push(fn); return timers.length; };
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
process.exit(failures ? 1 : 0);
