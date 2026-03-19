/* =============================================
   SKYWAY AIRLINES — Booking Page Logic
   ============================================= */

var currentFlight = null;

document.addEventListener('DOMContentLoaded', function() {
  setActiveNav('booking');
  bindNav();
  loadFlightDetails();
  var form = document.getElementById('bookingForm');
  if (form) form.addEventListener('submit', onFormSubmit);
  var closeBtn  = document.getElementById('btnCloseModal');
  var overlay   = document.getElementById('modalOverlay');
  if (closeBtn) closeBtn.addEventListener('click', closeModal);
  if (overlay)  overlay.addEventListener('click', function(e) { if (e.target === overlay) closeModal(); });
  setupSeatsButtons();
});

async function loadFlightDetails() {
  var params   = new URLSearchParams(window.location.search);
  var flightId = params.get('flightId') || sessionStorage.getItem('selectedFlightId');
  if (!flightId) {
    showPageAlert('warning', '&#9889; No flight selected. <a href="flights.html" style="color:inherit;font-weight:600">Browse flights &rarr;</a>');
    disableForm(); return;
  }
  try {
    var res = await window.AirlineAPI.fetchFlightById(flightId);
    currentFlight = window.AirlineAPI.extractData(res);
    renderFlightCard(currentFlight);
    var fc = document.getElementById('flightCard');
    if (fc) fc.classList.remove('hidden');
  } catch (err) {
    showPageAlert('error', '&#9888; Could not load flight: ' + window.AirlineAPI.getErrorMessage(err));
    disableForm();
  }
}

function renderFlightCard(f) {
  setEl('fc-flight-id',     f.flightId);
  setEl('fc-flight-number', f.flightNumber);
  setEl('fc-route',         f.source + ' \u2192 ' + f.destination);
  setEl('fc-price',         '\u20B9' + formatNum(f.price) + ' / seat');
  setEl('fc-seats',         f.availableSeats + ' available');
  var seatsEl = document.getElementById('fc-seats');
  if (seatsEl) {
    seatsEl.className = f.availableSeats === 0 ? 'text-red' : f.availableSeats < 10 ? 'text-gold' : 'text-green';
  }
  updatePricePreview();
}

async function onFormSubmit(e) {
  e.preventDefault();
  clearPageAlert();
  var name  = val('passengerName');
  var email = val('passengerEmail');
  var phone = val('passengerPhone');
  var seats = parseInt(val('numSeats'), 10) || 1;
  if (!name || !email || !phone) { showPageAlert('error', '&#9888; Please fill in all required fields.'); return; }
  if (!isValidEmail(email))      { markInvalid('passengerEmail', 'Enter a valid email address.'); return; }
  if (!isValidPhone(phone))      { markInvalid('passengerPhone', 'Enter a valid phone number.'); return; }
  if (!currentFlight)            { showPageAlert('error', '&#9888; No flight selected.'); return; }
  if (seats > currentFlight.availableSeats) { showPageAlert('error', '&#9888; Only ' + currentFlight.availableSeats + ' seat(s) available.'); return; }

  setLoading(true);
  try {
    var payload = { flightId: currentFlight.flightId, passengerName: name, email: email, phone: phone, seats: seats };
    var res     = await window.AirlineAPI.createBooking(payload);
    var booking = window.AirlineAPI.extractData(res);
    showConfirmationModal(booking);
    e.target.reset();
    updatePricePreview();
  } catch (err) {
    if (window.AirlineAPI.isFlightFull(err)) {
      showPageAlert('error', '&#9992; Flight is full. <a href="flights.html" style="color:inherit;font-weight:600">Find alternate flights &rarr;</a>');
    } else {
      showPageAlert('error', '&#9888; ' + window.AirlineAPI.getErrorMessage(err));
    }
  } finally {
    setLoading(false);
  }
}

function showConfirmationModal(booking) {
  var b = booking || {}, p = b.passenger || {}, f = b.flight || {};
  setEl('conf-booking-id',   b.bookingId   || '\u2014');
  setEl('conf-passenger',    p.name        || '\u2014');
  setEl('conf-passenger-id', p.passengerId || '\u2014');
  setEl('conf-flight',       (f.flightId || '\u2014') + ' (' + (f.flightNumber || '') + ')');
  setEl('conf-route',        (f.source || '\u2014') + ' \u2192 ' + (f.destination || '\u2014'));
  setEl('conf-seats',        b.seats    || '\u2014');
  setEl('conf-fare',         b.farePaid ? '\u20B9' + formatNum(b.farePaid) : '\u2014');
  setEl('conf-date',         b.bookDate || new Date().toLocaleDateString());
  setEl('conf-status',       b.status   || 'CONFIRMED');
  var overlay = document.getElementById('modalOverlay');
  if (overlay) { overlay.classList.remove('hidden'); overlay.classList.add('animate-fade'); }
}

function closeModal() { var el = document.getElementById('modalOverlay'); if (el) el.classList.add('hidden'); }

function setupSeatsButtons() {
  var plus  = document.getElementById('seatsPlus');
  var minus = document.getElementById('seatsMinus');
  var input = document.getElementById('numSeats');
  if (plus)  plus.addEventListener('click',  function() { adjustSeats(1); });
  if (minus) minus.addEventListener('click', function() { adjustSeats(-1); });
  if (input) input.addEventListener('input', updatePricePreview);
}
function adjustSeats(delta) {
  var el  = document.getElementById('numSeats');
  if (!el) return;
  var max = currentFlight ? currentFlight.availableSeats : 10;
  el.value = Math.max(1, Math.min(max, parseInt(el.value || 1) + delta));
  updatePricePreview();
}
function updatePricePreview() {
  var seats = parseInt((document.getElementById('numSeats') || {}).value || 1);
  var price = currentFlight ? currentFlight.price : 0;
  setEl('pricePreview', '\u20B9' + formatNum(seats * price));
}

function val(id)      { var el = document.getElementById(id); return el ? el.value.trim() : ''; }
function setEl(id, v) { var el = document.getElementById(id); if (el) el.textContent = v; }
function formatNum(n) { return Number(n).toLocaleString('en-IN'); }
function isValidEmail(e) { return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(e); }
function isValidPhone(p) { return /^[0-9]{7,15}$/.test(p.replace(/[\s\-+()]/g, '')); }
function markInvalid(id, msg) {
  var el = document.getElementById(id);
  if (el) { el.classList.add('is-invalid'); el.addEventListener('input', function() { el.classList.remove('is-invalid'); }, { once: true }); }
  showPageAlert('error', '&#9888; ' + msg);
}
function showPageAlert(type, html) {
  var el = document.getElementById('pageAlert');
  if (!el) return;
  el.className = 'alert alert-' + type + ' animate-fade';
  el.innerHTML = '<span class="alert-icon">' + (type === 'error' ? '&#9888;' : '&#9889;') + '</span><span>' + html + '</span>';
  el.classList.remove('hidden');
  el.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}
function clearPageAlert() { var el = document.getElementById('pageAlert'); if (el) el.classList.add('hidden'); }
function disableForm()    { document.querySelectorAll('#bookingForm input, #bookingForm button').forEach(function(el) { el.disabled = true; }); }
function setLoading(on) {
  var btn = document.getElementById('btnBook');
  if (!btn) return;
  btn.disabled  = on;
  btn.innerHTML = on ? '<span class="loading-spinner"></span> Processing\u2026' : '&#9992; Confirm Booking';
}
function setActiveNav(page) { document.querySelectorAll('.nav-link').forEach(function(a) { a.classList.toggle('active', a.dataset.page === page); }); }
function bindNav() { var t = document.getElementById('navbar-toggle'); if (t) t.addEventListener('click', function() { var n = document.getElementById('navbar-nav'); if (n) n.classList.toggle('open'); }); }
