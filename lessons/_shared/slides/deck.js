// Slide deck controls: arrow keys, space or a click move between slides; N shows the speaker notes; the address
// remembers the slide (#3), so a link can point at one. A deck with its voice (buildLessonDeckVoice) has a Listen
// button, or V: each slide's narration plays, and the deck moves on when it ends.
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
  var GAP_MS = 400;
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
    player.play().catch(function () { setListening(false); });
  }
  function advance() {
    if (current < slides.length - 1) { show(current + 1); } else { setListening(false); }
  }
  function setListening(on) {
    listening = on;
    stopVoice();
    if (listen) { listen.textContent = on ? 'Pause' : 'Listen'; listen.setAttribute('aria-pressed', String(on)); }
    if (on) { playVoice(); }
  }
  function show(index) {
    current = Math.max(0, Math.min(slides.length - 1, index));
    slides.forEach(function (slide, i) { slide.classList.toggle('current', i === current); });
    counter.textContent = (current + 1) + ' / ' + slides.length;
    history.replaceState(null, '', '#' + (current + 1));
    if (listening) { stopVoice(); playVoice(); }
  }
  if (document.querySelector('audio.voice')) {
    listen = document.createElement('button');
    listen.className = 'deck-listen';
    listen.type = 'button';
    listen.textContent = 'Listen';
    listen.setAttribute('aria-pressed', 'false');
    listen.addEventListener('click', function () { setListening(!listening); });
    document.body.appendChild(listen);
  }
  document.addEventListener('keydown', function (event) {
    if (['ArrowRight', 'ArrowDown', 'PageDown', ' '].indexOf(event.key) >= 0) { show(current + 1); event.preventDefault(); }
    if (['ArrowLeft', 'ArrowUp', 'PageUp'].indexOf(event.key) >= 0) { show(current - 1); event.preventDefault(); }
    if (event.key === 'Home') { show(0); }
    if (event.key === 'End') { show(slides.length - 1); }
    if (event.key === 'n' || event.key === 'N') { document.body.classList.toggle('show-notes'); }
    if ((event.key === 'v' || event.key === 'V') && listen) { setListening(!listening); }
  });
  document.addEventListener('click', function (event) {
    if (!event.target.closest('a, .notes, button')) { show(event.clientX < window.innerWidth / 3 ? current - 1 : current + 1); }
  });
  window.addEventListener('resize', fit);
  fit();
  show(current);
}());
