// Slide deck controls: arrow keys, space or a click move between slides; N shows the speaker notes; the address
// remembers the slide (#3), so a link can point at one.
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
  function show(index) {
    current = Math.max(0, Math.min(slides.length - 1, index));
    slides.forEach(function (slide, i) { slide.classList.toggle('current', i === current); });
    counter.textContent = (current + 1) + ' / ' + slides.length;
    history.replaceState(null, '', '#' + (current + 1));
  }
  document.addEventListener('keydown', function (event) {
    if (['ArrowRight', 'ArrowDown', 'PageDown', ' '].indexOf(event.key) >= 0) { show(current + 1); event.preventDefault(); }
    if (['ArrowLeft', 'ArrowUp', 'PageUp'].indexOf(event.key) >= 0) { show(current - 1); event.preventDefault(); }
    if (event.key === 'Home') { show(0); }
    if (event.key === 'End') { show(slides.length - 1); }
    if (event.key === 'n' || event.key === 'N') { document.body.classList.toggle('show-notes'); }
  });
  document.addEventListener('click', function (event) {
    if (!event.target.closest('a, .notes')) { show(event.clientX < window.innerWidth / 3 ? current - 1 : current + 1); }
  });
  window.addEventListener('resize', fit);
  fit();
  show(current);
}());
