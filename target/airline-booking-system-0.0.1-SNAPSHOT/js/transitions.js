/* =============================================
   SKYWAY AIRLINES — Page Transitions
   ============================================= */

(function() {
  // Inject styles
  var style = document.createElement('style');
  style.textContent = `
    #sky-loader {
      position: fixed; inset: 0; z-index: 9999;
      background: var(--navy, #060d1a);
      display: flex; flex-direction: column;
      align-items: center; justify-content: center;
      gap: 1.5rem;
      transition: opacity 0.4s ease, visibility 0.4s ease;
    }
    #sky-loader.hide { opacity: 0; visibility: hidden; }
    .sky-loader-plane {
      font-size: 2.8rem;
      animation: planeBounce 0.8s ease-in-out infinite alternate;
    }
    .sky-loader-bar-track {
      width: 180px; height: 3px;
      background: rgba(0,183,255,0.15);
      border-radius: 999px; overflow: hidden;
    }
    .sky-loader-bar-fill {
      height: 100%; width: 0%;
      background: linear-gradient(90deg, #00b7ff, #00e5a0);
      border-radius: 999px;
      animation: barFill 0.7s ease forwards;
    }
    .sky-loader-text {
      font-size: 0.72rem; letter-spacing: 0.18em;
      color: rgba(0,183,255,0.6);
      font-family: 'Space Mono', monospace;
      text-transform: uppercase;
      animation: textPulse 1s ease-in-out infinite alternate;
    }
    @keyframes planeBounce {
      from { transform: translateY(-6px) rotate(-5deg); }
      to   { transform: translateY(6px)  rotate(5deg);  }
    }
    @keyframes barFill {
      0%   { width: 0%; }
      100% { width: 100%; }
    }
    @keyframes textPulse {
      from { opacity: 0.4; }
      to   { opacity: 1;   }
    }
    .page-wrapper { animation: pageFadeIn 0.35s ease forwards; }
    @keyframes pageFadeIn {
      from { opacity: 0; transform: translateY(12px); }
      to   { opacity: 1; transform: translateY(0); }
    }
    #sky-exit {
      position: fixed; inset: 0; z-index: 9998;
      background: var(--navy, #060d1a);
      opacity: 0; visibility: hidden;
      transition: opacity 0.3s ease;
      pointer-events: none;
    }
    #sky-exit.show { opacity: 1; visibility: visible; pointer-events: all; }

    /* Scroll animations */
    .scroll-hidden {
      opacity: 0;
      transform: translateY(35px);
      transition: opacity 0.55s ease, transform 0.55s ease;
    }
    .scroll-hidden.scroll-visible {
      opacity: 1;
      transform: translateY(0);
    }
  `;
  document.head.appendChild(style);

  // Create elements but DON'T touch body yet
  var loader = document.createElement('div');
  loader.id = 'sky-loader';
  loader.innerHTML =
    '<div class="sky-loader-plane">✈</div>' +
    '<div class="sky-loader-bar-track"><div class="sky-loader-bar-fill"></div></div>' +
    '<div class="sky-loader-text">Loading JETNOVA&hellip;</div>';

  var exitOverlay = document.createElement('div');
  exitOverlay.id = 'sky-exit';

  // Wait for body to exist
  document.addEventListener('DOMContentLoaded', function() {
    document.body.prepend(loader);
    document.body.appendChild(exitOverlay);

    // Hide loader after page loads
    window.addEventListener('load', function() {
      setTimeout(function() {
        loader.classList.add('hide');
        setTimeout(function() { loader.remove(); }, 450);
      }, 600);

      // Scroll animations — start after loader finishes
      setTimeout(function() {
        var selectors = ['.card', '.stat-card', '.section-label', '.page-title', '.page-subtitle', '.table-wrapper'];
        selectors.forEach(function(sel) {
          document.querySelectorAll(sel).forEach(function(el, i) {
            el.classList.add('scroll-hidden');
            el.style.transitionDelay = Math.min(i * 0.07, 0.25) + 's';
          });
        });

        var observer = new IntersectionObserver(function(entries) {
          entries.forEach(function(entry) {
            if (entry.isIntersecting) {
              entry.target.classList.add('scroll-visible');
            } else {
              entry.target.classList.remove('scroll-visible');
            }
          });
        }, { threshold: 0.1 });

        document.querySelectorAll('.scroll-hidden').forEach(function(el) {
          observer.observe(el);
        });
      }, 900);
    });
  });

  // Exit animation on link click
  document.addEventListener('click', function(e) {
    var link = e.target.closest('a[href]');
    if (!link) return;
    var href = link.getAttribute('href');
    if (!href || href.startsWith('#') || href.startsWith('http') || href.startsWith('mailto')) return;
    if (link.target === '_blank') return;
    e.preventDefault();
    exitOverlay.classList.add('show');
    setTimeout(function() { window.location.href = href; }, 280);
  }, true);

})();