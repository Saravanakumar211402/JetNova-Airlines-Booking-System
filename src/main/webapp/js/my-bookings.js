/* =============================================
   SKYWAY AIRLINES — My Bookings Page Logic
   ============================================= */

var allBookings = [];

document.addEventListener('DOMContentLoaded', function() {
  setActiveNav('my-bookings'); bindNav();
  var btn   = document.getElementById('btnFetchBookings');
  var input = document.getElementById('passengerIdInput');
  if (btn)   btn.addEventListener('click', onFetch);
  if (input) input.addEventListener('keydown', function(e) { if (e.key === 'Enter') onFetch(); });
});

async function onFetch() {
  var input = document.getElementById('passengerIdInput');
  var pid   = input ? input.value.trim() : '';
  if (!pid) { showAlert('warning', '&#9888; Please enter a Passenger ID.'); return; }
  clearAlert();
  document.getElementById('resultsSection') && document.getElementById('resultsSection').classList.add('hidden');
  setFetchLoading(true);
  try {
    var pRes = await window.AirlineAPI.fetchPassengerById(pid).catch(function() { return null; });
    var bRes = await window.AirlineAPI.fetchPassengerBookings(pid);
    var passenger = pRes ? window.AirlineAPI.extractData(pRes) : null;
    allBookings   = window.AirlineAPI.extractData(bRes) || [];
    if (passenger) renderPassengerCard(passenger);
    else { var pc = document.getElementById('passengerCard'); if (pc) pc.classList.add('hidden'); }
    renderBookingsTable(allBookings);
    renderSummary(allBookings);
    var rs = document.getElementById('resultsSection');
    if (rs) { rs.classList.remove('hidden'); rs.classList.add('animate-up'); }
  } catch (err) {
    showAlert('error', '&#9888; ' + window.AirlineAPI.getErrorMessage(err));
  } finally { setFetchLoading(false); }
}

function renderPassengerCard(p) {
  var card = document.getElementById('passengerCard'); if (!card) return;
  setEl('pc-id',    p.passengerId || '\u2014');
  setEl('pc-name',  p.name  || '\u2014');
  setEl('pc-email', p.email || '\u2014');
  setEl('pc-phone', p.phone || '\u2014');
  card.classList.remove('hidden');
}

function renderBookingsTable(bookings) {
  var tbody = document.getElementById('bookingsBody');
  var wrap  = document.getElementById('tableWrapper');
  var empty = document.getElementById('emptyState');
  if (!tbody) return;
  if (bookings.length === 0) {
    if (wrap)  wrap.classList.add('hidden');
    if (empty) empty.classList.remove('hidden');
    return;
  }
  if (wrap)  wrap.classList.remove('hidden');
  if (empty) empty.classList.add('hidden');
  setEl('bookingCount', bookings.length);
  tbody.innerHTML = '';
  bookings.forEach(function(b, i) {
    var p  = b.passenger || {}, f = b.flight || {};
    var st = (b.status || '').toUpperCase();
    var tr = document.createElement('tr');
    tr.style.animationDelay = (i * 0.04) + 's'; tr.className = 'animate-up';
    tr.innerHTML =
      '<td class="td-id">'  + escHtml(b.bookingId) + '</td>' +
      '<td><span class="mono text-cyan">' + escHtml(f.flightId || '\u2014') + '</span></td>' +
      '<td class="td-route"><span class="route-display"><span>' + escHtml(f.source || '\u2014') + '</span><span class="route-arrow">&rarr;</span><span>' + escHtml(f.destination || '\u2014') + '</span></span></td>' +
      '<td><span class="badge badge-' + (st === 'CONFIRMED' ? 'confirmed' : 'cancelled') + '">' + st + '</span></td>' +
      '<td class="td-price">&#8377;' + formatNum(b.farePaid || 0) + '</td>' +
      '<td class="td-date">' + escHtml(b.bookDate || '\u2014') + '</td>' +
      '<td><div class="td-actions">' +
      (st === 'CONFIRMED'
        ? '<button class="btn btn-danger btn-sm" onclick="handleCancel(\'' + escHtml(b.bookingId) + '\', this)">Cancel</button>'
        : '<span class="text-muted" style="font-size:0.8rem">\u2014</span>') +
      '</div></td>';
    tbody.appendChild(tr);
  });
}

function renderSummary(bookings) {
  var total     = bookings.length;
  var active    = bookings.filter(function(b) { return (b.status||'').toUpperCase() === 'CONFIRMED'; }).length;
  setEl('sumTotal', total); setEl('sumActive', active); setEl('sumCancelled', total - active);
}

async function handleCancel(bookingId, btn) {
  if (!confirm('Cancel booking ' + bookingId + '?')) return;
  btn.disabled = true; btn.innerHTML = '<span class="loading-spinner"></span>';
  try {
    await window.AirlineAPI.cancelBooking(bookingId);
    for (var i = 0; i < allBookings.length; i++) { if (allBookings[i].bookingId === bookingId) { allBookings[i].status = 'CANCELLED'; break; } }
    renderBookingsTable(allBookings); renderSummary(allBookings);
    showAlert('success', '\u2713 Booking ' + bookingId + ' cancelled.');
  } catch (err) {
    showAlert('error', '&#9888; ' + window.AirlineAPI.getErrorMessage(err));
    btn.disabled = false; btn.innerHTML = 'Cancel';
  }
}

function setEl(id, v)      { var el = document.getElementById(id); if (el) el.textContent = v; }
function formatNum(n)      { return Number(n).toLocaleString('en-IN'); }
function escHtml(s)        { var d = document.createElement('div'); d.textContent = String(s != null ? s : ''); return d.innerHTML; }
function showAlert(type, html) {
  var el = document.getElementById('alertBox'); if (!el) return;
  el.className = 'alert alert-' + type + ' animate-fade';
  el.innerHTML = '<span class="alert-icon">' + (type === 'success' ? '\u2713' : '&#9888;') + '</span><span>' + html + '</span>';
  el.classList.remove('hidden');
}
function clearAlert()      { var el = document.getElementById('alertBox'); if (el) el.classList.add('hidden'); }
function setFetchLoading(on){ var btn = document.getElementById('btnFetchBookings'); if (!btn) return; btn.disabled = on; btn.innerHTML = on ? '<span class="loading-spinner"></span> Loading\u2026' : '&#128269; View Bookings'; }
function setActiveNav(p)   { document.querySelectorAll('.nav-link').forEach(function(a) { a.classList.toggle('active', a.dataset.page === p); }); }
function bindNav()         { var t = document.getElementById('navbar-toggle'); if (t) t.addEventListener('click', function() { var n = document.getElementById('navbar-nav'); if (n) n.classList.toggle('open'); }); }
