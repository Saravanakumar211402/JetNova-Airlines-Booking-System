/* =============================================
   SKYWAY AIRLINES — Cancel Booking Logic
   ============================================= */

var currentBooking = null;

document.addEventListener('DOMContentLoaded', function() {
  setActiveNav('cancel'); bindNav();
  var btnSearch  = document.getElementById('btnSearchBooking');
  var btnConfirm = document.getElementById('btnConfirmCancel');
  var btnNo      = document.getElementById('btnConfirmNo');
  var btnShow    = document.getElementById('btnShowCancelConfirm');
  var input      = document.getElementById('bookingIdInput');
  if (btnSearch)  btnSearch.addEventListener('click', onSearchBooking);
  if (btnConfirm) btnConfirm.addEventListener('click', onConfirmCancel);
  if (btnNo)      btnNo.addEventListener('click', hideConfirmPanel);
  if (btnShow)    btnShow.addEventListener('click', function() { if (currentBooking && (currentBooking.status||'').toUpperCase() !== 'CANCELLED') showConfirmPanel(); });
  if (input)      input.addEventListener('keydown', function(e) { if (e.key === 'Enter') onSearchBooking(); });
});

async function onSearchBooking() {
  var input = document.getElementById('bookingIdInput');
  var id    = input ? input.value.trim() : '';
  if (!id) { showAlert('warning', '&#9888; Please enter a Booking ID.'); return; }
  clearAlert(); hideBookingCard(); hideConfirmPanel();
  setSearchLoading(true);
  try {
    var res = await window.AirlineAPI.fetchBookingById(id);
    currentBooking = window.AirlineAPI.extractData(res);
    renderBookingCard(currentBooking);
    showBookingCard();
  } catch (err) {
    var msg = window.AirlineAPI.getErrorMessage(err);
    if (err && err.status === 404 || msg.toLowerCase().indexOf('not found') >= 0) {
      showAlert('error', '&#9888; Booking <strong>' + escHtml(id) + '</strong> not found.');
    } else {
      showAlert('error', '&#9888; ' + msg);
    }
  } finally { setSearchLoading(false); }
}

function renderBookingCard(b) {
  var p = b.passenger || {}, f = b.flight || {};
  setEl('bc-booking-id',   b.bookingId   || '\u2014');
  setEl('bc-passenger',    p.name        || '\u2014');
  setEl('bc-passenger-id', p.passengerId || '\u2014');
  setEl('bc-flight',       (f.flightId   || '\u2014') + ' (' + (f.flightNumber || '') + ')');
  setEl('bc-route',        f.source ? f.source + ' \u2192 ' + f.destination : '\u2014');
  setEl('bc-seats',        b.seats    || '\u2014');
  setEl('bc-fare',         b.farePaid ? '\u20B9' + formatNum(b.farePaid) : '\u2014');
  setEl('bc-date',         b.bookDate || '\u2014');
  var statusEl = document.getElementById('bc-status');
  if (statusEl) {
    var st = (b.status || '').toUpperCase();
    statusEl.className   = 'badge badge-' + (st === 'CONFIRMED' ? 'confirmed' : 'cancelled');
    statusEl.textContent = st || '\u2014';
  }
  var cancelBtn      = document.getElementById('btnConfirmCancel');
  var alreadyCancMsg = document.getElementById('alreadyCancelledMsg');
  if ((b.status || '').toUpperCase() === 'CANCELLED') {
    if (cancelBtn)      cancelBtn.classList.add('hidden');
    if (alreadyCancMsg) alreadyCancMsg.classList.remove('hidden');
  } else {
    if (cancelBtn)      cancelBtn.classList.remove('hidden');
    if (alreadyCancMsg) alreadyCancMsg.classList.add('hidden');
  }
}

async function onConfirmCancel() {
  if (!currentBooking || !currentBooking.bookingId) return;
  var btn = document.getElementById('btnConfirmCancel');
  if (btn) { btn.disabled = true; btn.innerHTML = '<span class="loading-spinner"></span> Cancelling\u2026'; }
  clearAlert();
  try {
    await window.AirlineAPI.cancelBooking(currentBooking.bookingId);
    currentBooking.status = 'CANCELLED';
    renderBookingCard(currentBooking);
    hideConfirmPanel();
    showAlert('success', '\u2713 Booking ' + currentBooking.bookingId + ' successfully cancelled.');
  } catch (err) {
    showAlert('error', '&#9888; ' + window.AirlineAPI.getErrorMessage(err));
    if (btn) { btn.disabled = false; btn.innerHTML = '&#128465; Cancel This Booking'; }
  }
}

function showConfirmPanel()  { var el = document.getElementById('confirmPanel'); if (el) { el.classList.remove('hidden'); el.classList.add('animate-up'); } }
function hideConfirmPanel()  { var el = document.getElementById('confirmPanel'); if (el) el.classList.add('hidden'); }
function showBookingCard()   { var el = document.getElementById('bookingCard');  if (el) { el.classList.remove('hidden'); el.classList.add('animate-up'); } }
function hideBookingCard()   { var el = document.getElementById('bookingCard');  if (el) el.classList.add('hidden'); }
function setEl(id, v)        { var el = document.getElementById(id); if (el) el.textContent = v; }
function formatNum(n)        { return Number(n).toLocaleString('en-IN'); }
function escHtml(s)          { var d = document.createElement('div'); d.textContent = String(s); return d.innerHTML; }
function showAlert(type, html) {
  var el = document.getElementById('alertBox'); if (!el) return;
  el.className = 'alert alert-' + type + ' animate-fade';
  el.innerHTML = '<span class="alert-icon">' + (type === 'success' ? '\u2713' : '&#9888;') + '</span><span>' + html + '</span>';
  el.classList.remove('hidden');
}
function clearAlert()        { var el = document.getElementById('alertBox'); if (el) el.classList.add('hidden'); }
function setSearchLoading(on){ var btn = document.getElementById('btnSearchBooking'); if (!btn) return; btn.disabled = on; btn.innerHTML = on ? '<span class="loading-spinner"></span> Searching\u2026' : '&#128269; Search'; }
function setActiveNav(p)     { document.querySelectorAll('.nav-link').forEach(function(a) { a.classList.toggle('active', a.dataset.page === p); }); }
function bindNav()           { var t = document.getElementById('navbar-toggle'); if (t) t.addEventListener('click', function() { var n = document.getElementById('navbar-nav'); if (n) n.classList.toggle('open'); }); }
