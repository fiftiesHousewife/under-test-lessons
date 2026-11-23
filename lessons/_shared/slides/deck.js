// Slide deck controls: the Back and Next buttons, arrow keys, space or a click (on the left third of a slide to go
// back) move between slides; N shows the speaker notes; the address remembers the slide (#3), so a link can point at
// one. A deck with its voice (buildLessonDeckVoice) reads each slide's narration as you move to it, and has a Listen
// button, or V, that plays the whole deck: each slide's narration, then the next slide when it ends.
(function () {
  var slides = Array.prototype.slice.call(document.querySelectorAll('.deck-slide'));
  var counter = document.createElement('div');
  counter.className = 'deck-counter';
  document.body.appendChild(counter);
  var current = Math.max(0, Math.min(slides.length - 1, (parseInt(location.hash.slice(1), 10) || 1) - 1));
  function fit() {
    var scale = Math.min(window.innerWidth / 1920, window.innerHeight / 1080);
    document.documentElement.style.setProperty('--scale', String(scale));
  }
  // One player for every slide: browsers that guard autoplay (Safari, and others) let an audio element play without a
  // click only once a click has started that same element, so a player per slide would fall silent after the first.
  // While listening: the pause after a slide's narration before the next slide, and how long a slide with no
  // narration stays.
  var GAP_MS = 150;
  var SILENT_SLIDE_MS = 2500;
  var listening = false;
  var listen = null;
  var nextTimer = null;
  var player = null;
  function clipSource(index) {
    var clip = slides[index] && slides[index].querySelector('audio.voice');
    return clip ? clip.getAttribute('src') : null;
  }
  function stopVoice() {
    clearTimeout(nextTimer);
    if (player) { player.pause(); }
  }
  function slideRead() {
    if (!listening) { return; }
    if (current === slides.length - 1) { document.dispatchEvent(new CustomEvent('deck-voice-finished')); }
    nextTimer = setTimeout(function () { advance(); }, GAP_MS);
  }
  function playVoice() {
    var source = clipSource(current);
    if (!source) { nextTimer = setTimeout(function () { advance(); }, SILENT_SLIDE_MS); return; }
    if (!player) { player = new Audio(); player.onended = slideRead; }
    player.src = source;
    player.play().catch(waitForClick);
    fetchNext();
  }
  // The next slide's clip is fetched while this one plays, so it starts the moment its slide shows.
  var fetched = null;
  function fetchNext() {
    var source = clipSource(current + 1);
    if (!source) { return; }
    fetched = new Audio();
    fetched.preload = 'auto';
    fetched.src = source;
  }
  // Moving to a slide by hand reads its narration once, without moving on when it ends.
  function readThisSlide() {
    stopVoice();
    var source = clipSource(current);
    if (!source) { return; }
    if (!player) { player = new Audio(); player.onended = slideRead; }
    player.src = source;
    player.play().catch(function () { /* the browser refused: the slide stays, silent, until the next move */ });
  }
  function advance() {
    if (current < slides.length - 1) { show(current + 1); } else { setListening(false); }
  }
  // A browser plays sound only after a click on the page. Arriving from the index's listen link, or from the end of the
  // last lesson, there hasn't been one, so the deck asks for it rather than falling silent.
  var waiting = false;
  function waitForClick() {
    setListening(false);
    waiting = true;
    if (listen) { listen.textContent = 'Click to listen'; listen.classList.add('waiting'); }
  }
  function setListening(on) {
    waiting = false;
    if (listen) { listen.classList.remove('waiting'); }
    listening = on;
    stopVoice();
    if (listen) { listen.textContent = on ? 'Pause' : 'Listen'; listen.setAttribute('aria-pressed', String(on)); }
    if (on) { playVoice(); }
  }
  function show(index, byHand) {
    current = Math.max(0, Math.min(slides.length - 1, index));
    slides.forEach(function (slide, i) { slide.classList.toggle('current', i === current); });
    counter.textContent = (current + 1) + ' / ' + slides.length;
    history.replaceState(null, '', '#' + (current + 1));
    if (listening) { stopVoice(); playVoice(); } else if (byHand && listen) { readThisSlide(); }
  }
  function move(step) { show(current + step, true); }
  function control(className, text, onClick) {
    var button = document.createElement('button');
    button.className = className;
    button.type = 'button';
    button.textContent = text;
    button.addEventListener('click', onClick);
    document.body.appendChild(button);
    return button;
  }
  control('deck-move deck-back', '\u2039 Back', function () { move(-1); });
  control('deck-move deck-next', 'Next \u203a', function () { move(1); });
  if (document.querySelector('audio.voice')) {
    listen = control('deck-listen', 'Listen', function () { setListening(!listening); });
    listen.setAttribute('aria-pressed', 'false');
  }
  document.addEventListener('keydown', function (event) {
    if (['ArrowRight', 'ArrowDown', 'PageDown', ' '].indexOf(event.key) >= 0) { move(1); event.preventDefault(); }
    if (['ArrowLeft', 'ArrowUp', 'PageUp', 'Backspace'].indexOf(event.key) >= 0) { move(-1); event.preventDefault(); }
    if (event.key === 'Home') { show(0, true); }
    if (event.key === 'End') { show(slides.length - 1, true); }
    if (event.key === 'n' || event.key === 'N') { document.body.classList.toggle('show-notes'); }
    if ((event.key === 'v' || event.key === 'V') && listen) { setListening(!listening); }
  });
  document.addEventListener('click', function (event) {
    if (waiting) { setListening(true); return; }
    if (!event.target.closest('a, .notes, button')) { move(event.clientX < window.innerWidth / 3 ? -1 : 1); }
  });
  window.addEventListener('resize', fit);
  fit();
  show(current);
}());
