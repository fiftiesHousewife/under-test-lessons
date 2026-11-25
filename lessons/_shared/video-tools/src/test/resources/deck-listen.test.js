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
    querySelector() { return null; }, closest() { return this; }, blur() { this.blurred = true; } };
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
// Downloads fail here, as for a deck opened from a file, so the player is given each clip's own address.
const fetchedUrls = [];
global.fetch = url => { fetchedUrls.push(url); return Promise.reject(new Error('offline')); };
// Lets the deck's downloads settle, running the timers they set, as a browser would over a second or two.
async function settle() { for (let k = 0; k < 6; k++) { await new Promise(resolve => setImmediate(resolve)); runTimers(); } }
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

(async () => {
eval(script);
const listen = appended.find(el => el.className === 'deck-listen');
check('a deck with voice has a sound button', Boolean(listen));
await settle(); // the deck's own start, with no click yet: refused
check('a deck tries to play by itself, and asks for a click when the browser refuses', listen.textContent === 'Click for sound');
listen.click();
check('the click starts it, and the button then mutes', listen.textContent === 'Mute');
check('the click plays the first slide', started[0] === 'a.m4a');
check('the next slide\'s clip is downloaded while this one plays', fetchedUrls.includes('b.m4a'));
await settle();
let finished = false;
document.addEventListener('deck-voice-finished', () => { finished = true; });
for (let i = 0; i < 3; i++) {
  const playing = everyAudio.find(a => !a.paused);
  if (playing) { playing.finish(); }
  await settle();
}
check('the second slide plays after the first, without another click', started[1] === 'b.m4a');
check('the third slide plays after the second', started[2] === 'c.m4a');
check('the deck says when its last slide has been read', finished);
check('when the deck ends, the button offers sound again', listen.textContent === 'Unmute');
check('the next slide comes up 3.2 s after the narration ends', delays.includes(3200));
check('its narration starts 0.8 s after the slide appears, and no later', delays.some(ms => ms > 700 && ms <= 800)
  && delays.every(ms => ms <= 800 || ms === 3200 || ms === 2500));

// Moving by hand, not listening: the Back and Next buttons are there, each move reads that slide's narration, and the
// deck stays on it when the narration ends.
const back = appended.find(el => el.className.indexOf('deck-back') >= 0);
const next = appended.find(el => el.className.indexOf('deck-next') >= 0);
check('every deck has a Back and a Next button', Boolean(back) && Boolean(next));
const counter = appended.find(el => el.className === 'deck-counter');
back.click();
check('Back goes to the slide before', counter.textContent === '2 / 3');
check('Back gives up the focus, so the space bar moves on rather than pressing Back again', back.blurred === true);
check('moving back reads that slide', started[started.length - 1] === 'b.m4a');
everyAudio.find(a => !a.paused).finish();
runTimers();
check('a slide reached by hand stays when its narration ends', counter.textContent === '2 / 3');
next.click();
check('Next reads the next slide', counter.textContent === '3 / 3' && started[started.length - 1] === 'c.m4a');
const press = key => (documentListeners.keydown || []).forEach(fn => fn({ key, preventDefault() {} }));
press('ArrowLeft');
check('the left arrow goes to the slide before', counter.textContent === '2 / 3');
press('ArrowRight');
check('the right arrow goes to the next slide', counter.textContent === '3 / 3');
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
await settle(); // the deck's own start, with no click yet: refused
{
  check('a start the browser refuses asks for a click, rather than falling silent', freshListen.textContent === 'Click for sound');
  clickInProgress = true;
  (documentListeners.click || []).forEach(fn => fn({ target: { closest: () => null }, clientX: 1500 }));
  clickInProgress = false;
  check('the next click on the page starts the voice', started.length > before && freshListen.textContent === 'Mute');
}
// Pause holds the voice and the slide; Play carries on from the same word, and the deck then moves on as before.
const pause = appended.find(el => el.className === 'deck-pause');
check('a deck with voice has a pause button, shown while listening', Boolean(pause) && pause.hidden === false);
pause.click();
await settle();
const freshPlayer = everyAudio[everyAudio.length - 1]; // this deck's own player, made when its voice started
check('Pause stops the voice and offers Play', freshPlayer.paused && pause.textContent === 'Play');
const startedBeforePlay = started.length;
pause.click();
check('Play carries on with the same clip', started[started.length - 1] === 'x.m4a' && pause.textContent === 'Pause'
  && started.length === startedBeforePlay + 1);
// Muting is remembered: a deck opened after a mute stays silent until unmuted.
const store = {};
global.window.localStorage = { getItem: k => (k in store ? store[k] : null), setItem: (k, v) => { store[k] = String(v); } };
freshListen.click(); // mute
check('Mute stops the voice and offers Unmute', freshListen.textContent === 'Unmute' && store['deck-voice-muted'] === '1');
appended.length = 0;
const startedBeforeMutedDeck = started.length;
eval(script);
await settle();
const mutedListen = appended.find(el => el.className === 'deck-listen');
check('a deck opened after a mute stays silent', started.length === startedBeforeMutedDeck && mutedListen.textContent === 'Unmute');
process.exit(failures ? 1 : 0);
})();
