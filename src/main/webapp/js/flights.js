/* =============================================
   SKYWAY AIRLINES — Flights Page Logic
   ============================================= */

let currentFlights = [];
let activeFilter = 'all';

document.addEventListener('DOMContentLoaded', function() {
  bindEvents();
  setActiveNav('flights');

  // Read URL params from home page quick search
  var params = new URLSearchParams(window.location.search);
  var src  = params.get('source');
  var dest = params.get('destination');

  if (src || dest) {
    // Pre-fill the search inputs
    var srcEl  = document.getElementById('searchSource');
    var destEl = document.getElementById('searchDest');
    if (srcEl  && src)  srcEl.value  = src;
    if (destEl && dest) destEl.value = dest;
    // Run the search automatically
    onSearch();
  } else {
    loadFlights('all');
  }
});

function bindEvents() {
  document.getElementById('btnSearch') && document.getElementById('btnSearch').addEventListener('click', onSearch);
  document.getElementById('btnClear')  && document.getElementById('btnClear').addEventListener('click', onClear);
  document.querySelectorAll('[data-filter]').forEach(function(btn) {
    btn.addEventListener('click', function() {
      activeFilter = btn.dataset.filter;
      setActiveFilter(btn);
      loadFlights(activeFilter);
    });
  });
  var src  = document.getElementById('searchSource');
  var dest = document.getElementById('searchDest');
  if (src)  src.addEventListener('keydown',  function(e) { if (e.key === 'Enter') onSearch(); });
  if (dest) dest.addEventListener('keydown', function(e) { if (e.key === 'Enter') onSearch(); });
  document.getElementById('navbar-toggle') && document.getElementById('navbar-toggle').addEventListener('click', toggleNav);
}

async function loadFlights(filter) {
  showLoading(true);
  clearAlert();
  try {
    var res;
    if      (filter === 'available') res = await window.AirlineAPI.fetchAvailableFlights();
    else if (filter === 'price')     res = await window.AirlineAPI.sortFlightsByPrice();
    else                             res = await window.AirlineAPI.fetchAllFlights();
    currentFlights = window.AirlineAPI.extractData(res) || [];
    renderTable(currentFlights);
  } catch (err) {
    showAlert('error', '&#9888; ' + window.AirlineAPI.getErrorMessage(err));
    renderTable([]);
  } finally {
    showLoading(false);
  }
}

async function onSearch() {
  var src  = document.getElementById('searchSource')  ? document.getElementById('searchSource').value.trim()  : '';
  var dest = document.getElementById('searchDest')    ? document.getElementById('searchDest').value.trim()    : '';
  if (!src && !dest) { loadFlights(activeFilter); return; }
  showLoading(true);
  clearAlert();
  try {
    var res = await window.AirlineAPI.searchFlights(src, dest);
    currentFlights = window.AirlineAPI.extractData(res) || [];
    renderTable(currentFlights);
    if (currentFlights.length === 0) showAlert('info', 'No flights found for that route.');
  } catch (err) {
    showAlert('error', window.AirlineAPI.getErrorMessage(err));
    renderTable([]);
  } finally {
    showLoading(false);
  }
}

function onClear() {
  var s = document.getElementById('searchSource');
  var d = document.getElementById('searchDest');
  if (s) s.value = '';
  if (d) d.value = '';
  clearAlert();
  loadFlights(activeFilter);
}

function renderTable(flights) {
  var tb = document.getElementById('flightsBody');
  if (!tb) return;
  if (flights.length === 0) {
    var tw = document.getElementById('tableWrapper');
    var es = document.getElementById('emptyState');
    if (tw) tw.classList.add('hidden');
    if (es) es.classList.remove('hidden');
    var tc = document.getElementById('flightCount');
    if (tc) tc.textContent = '0';
    return;
  }
  var tw = document.getElementById('tableWrapper');
  var es = document.getElementById('emptyState');
  if (tw) tw.classList.remove('hidden');
  if (es) es.classList.add('hidden');
  var tc = document.getElementById('flightCount');
  if (tc) tc.textContent = flights.length;

  tb.innerHTML = '';
  flights.forEach(function(f, i) {
    var avail = f.availableSeats != null ? f.availableSeats : 0;
    var total = f.totalSeats     != null ? f.totalSeats     : 1;
    var pct   = Math.max(0, Math.min(100, (avail / total) * 100));
    var fillClass  = pct > 50 ? 'ok' : pct > 20 ? 'medium' : 'low';
    var seatsClass = avail === 0 ? 'low' : avail < 10 ? 'medium' : 'ok';
    var isFull = avail === 0;
    var tr = document.createElement('tr');
    tr.style.animationDelay = (i * 0.04) + 's';
    tr.className = 'animate-up';
    tr.innerHTML =
      '<td class="td-id">'   + escHtml(f.flightId)     + '</td>' +
      '<td><span class="mono text-cyan">' + escHtml(f.flightNumber) + '</span></td>' +
      '<td class="td-route"><span class="route-display"><span>' + escHtml(f.source) + '</span><span class="route-arrow">&rarr;</span><span>' + escHtml(f.destination) + '</span></span></td>' +
      '<td class="td-price">&#8377;' + formatNum(f.price) + '</td>' +
      '<td><div class="seats-bar"><span class="td-seats ' + seatsClass + '" style="min-width:36px">' + avail + '</span><div class="seats-track"><div class="seats-fill ' + fillClass + '" style="width:' + pct + '%"></div></div><span class="td-seats" style="font-size:0.74rem;color:var(--text-dim)">/' + total + '</span></div></td>' +
      '<td><div class="td-actions">' +
      (isFull
        ? '<span class="badge badge-error">Full</span>'
        : '<button class="btn btn-primary btn-sm" onclick="bookFlight(\'' + escHtml(f.flightId) + '\')">Book &rarr;</button>') +
      '</div></td>';
    tb.appendChild(tr);
  });
}

function bookFlight(flightId) {
  sessionStorage.setItem('selectedFlightId', flightId);
  window.location.href = 'booking.html?flightId=' + flightId;
}

function showLoading(on) {
  var el = document.getElementById('loadingOverlay');
  if (el) el.classList.toggle('hidden', !on);
}
function setActiveFilter(active) {
  document.querySelectorAll('[data-filter]').forEach(function(b) {
    b.classList.toggle('active', b === active);
    b.dataset.active = b === active ? 'true' : 'false';
  });
}
function showAlert(type, msg) {
  var el = document.getElementById('alertBox');
  if (!el) return;
  el.className = 'alert alert-' + type + ' animate-fade';
  el.innerHTML = '<span class="alert-icon">' + (type === 'error' ? '&#9888;' : '&#9432;') + '</span><span>' + msg + '</span>';
  el.classList.remove('hidden');
}
function clearAlert() { var el = document.getElementById('alertBox'); if (el) el.classList.add('hidden'); }
function escHtml(s)   { var d = document.createElement('div'); d.textContent = String(s != null ? s : ''); return d.innerHTML; }
function formatNum(n) { return Number(n).toLocaleString('en-IN'); }
function setActiveNav(page) { document.querySelectorAll('.nav-link').forEach(function(a) { a.classList.toggle('active', a.dataset.page === page); }); }
function toggleNav()  { var n = document.getElementById('navbar-nav'); if (n) n.classList.toggle('open'); }
