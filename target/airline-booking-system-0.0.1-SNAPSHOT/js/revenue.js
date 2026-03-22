/* =============================================
   SKYWAY AIRLINES — Revenue Admin Page Logic
   ============================================= */

document.addEventListener('DOMContentLoaded', function() {
  setActiveNav('revenue'); bindNav();
  loadRevenueDashboard();
});

async function loadRevenueDashboard() {
  setDashboardLoading(true);
  try {
    var revRes  = await window.AirlineAPI.fetchRevenue();
    var bookRes = await window.AirlineAPI.fetchConfirmedBookings();
    var revenueData = window.AirlineAPI.extractData(revRes);
    var bookings    = window.AirlineAPI.extractData(bookRes) || [];
    renderRevenueCard(revenueData);
    renderStats(bookings);
    renderConfirmedTable(bookings);
    var dash = document.getElementById('dashboard');
    if (dash) { dash.classList.remove('hidden'); dash.classList.add('animate-up'); }
  } catch (err) {
    showAlert('error', '&#9888; Failed to load: ' + window.AirlineAPI.getErrorMessage(err));
  } finally { setDashboardLoading(false); }
}

function renderRevenueCard(data) {
  var amount = typeof data === 'number' ? data : (data && data.totalRevenue != null ? data.totalRevenue : (data && data.revenue != null ? data.revenue : 0));
  setEl('totalRevenue', '\u20B9' + formatNum(amount));
  var bar = document.getElementById('revenueBar');
  if (bar) bar.style.width = Math.max(5, Math.min(100, (amount / 1000000) * 100)) + '%';
}

function renderStats(bookings) {
  var confirmed  = bookings.length;
  var totalFare  = bookings.reduce(function(s, b) { return s + (b.farePaid || 0); }, 0);
  var avgFare    = confirmed > 0 ? totalFare / confirmed : 0;
  var totalSeats = bookings.reduce(function(s, b) { return s + (b.seats || 0); }, 0);
  setEl('statConfirmed', confirmed);
  setEl('statAvgFare',   '\u20B9' + formatNum(Math.round(avgFare)));
  setEl('statSeats',     totalSeats);
}

function renderConfirmedTable(bookings) {
  var tbody = document.getElementById('confirmedBody');
  var wrap  = document.getElementById('tableWrapper');
  var empty = document.getElementById('emptyState');
  if (!tbody) return;
  if (bookings.length === 0) { if (wrap) wrap.classList.add('hidden'); if (empty) empty.classList.remove('hidden'); return; }
  if (wrap)  wrap.classList.remove('hidden');
  if (empty) empty.classList.add('hidden');
  setEl('bookingCount', bookings.length);
  tbody.innerHTML = '';
  bookings.forEach(function(b, i) {
    var p = b.passenger || {}, f = b.flight || {};
    var tr = document.createElement('tr');
    tr.style.animationDelay = (i * 0.03) + 's'; tr.className = 'animate-up';
    tr.innerHTML =
      '<td class="td-id">' + escHtml(b.bookingId) + '</td>' +
      '<td>' + escHtml(p.name || '\u2014') + '<br><span class="mono text-muted" style="font-size:0.72rem">' + escHtml(p.passengerId || '') + '</span></td>' +
      '<td class="td-route"><span class="route-display"><span>' + escHtml(f.source || '\u2014') + '</span><span class="route-arrow">&rarr;</span><span>' + escHtml(f.destination || '\u2014') + '</span></span></td>' +
      '<td><span class="mono text-cyan">' + escHtml(f.flightId || '\u2014') + '</span></td>' +
      '<td class="mono" style="text-align:center">' + (b.seats || '\u2014') + '</td>' +
      '<td class="td-price">&#8377;' + formatNum(b.farePaid || 0) + '</td>' +
      '<td class="td-date">' + escHtml(b.bookDate || '\u2014') + '</td>';
    tbody.appendChild(tr);
  });
}

function setEl(id, v)      { var el = document.getElementById(id); if (el) el.textContent = v; }
function formatNum(n)      { return Number(n).toLocaleString('en-IN'); }
function escHtml(s)        { var d = document.createElement('div'); d.textContent = String(s != null ? s : ''); return d.innerHTML; }
function showAlert(type, html) {
  var el = document.getElementById('alertBox'); if (!el) return;
  el.className = 'alert alert-' + type + ' animate-fade';
  el.innerHTML = '<span>' + html + '</span>';
  el.classList.remove('hidden');
}
function setDashboardLoading(on) { var el = document.getElementById('loadingOverlay'); if (el) el.classList.toggle('hidden', !on); }
function setActiveNav(p) { document.querySelectorAll('.nav-link').forEach(function(a) { a.classList.toggle('active', a.dataset.page === p); }); }
function bindNav()       { var t = document.getElementById('navbar-toggle'); if (t) t.addEventListener('click', function() { var n = document.getElementById('navbar-nav'); if (n) n.classList.toggle('open'); }); }
