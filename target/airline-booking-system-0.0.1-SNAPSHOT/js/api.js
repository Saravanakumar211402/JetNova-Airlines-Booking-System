/* =============================================
   SKYWAY AIRLINES — Centralized API Layer
   ============================================= */

   var API_BASE = window.location.hostname === 'localhost' 
       ? '/airline-booking-system/api' 
       : '/api';

function apiFetch(url, options) {
  var config = Object.assign({ headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' } }, options || {});
  return fetch(url, config).then(function(res) {
    return res.json().then(function(body) {
      if (!res.ok) {
        var msg = (body && body.message) || (body && body.error) || ('HTTP ' + res.status);
        var err = new Error(msg);
        err.status = res.status;
        err.body   = body;
        throw err;
      }
      return body;
    }).catch(function(e) {
      if (e.status) throw e;
      var err = new Error('Invalid server response');
      err.status = res.status;
      throw err;
    });
  });
}

function fetchAllFlights()           { return apiFetch(API_BASE + '/flights'); }
function searchFlights(src, dest)    { return apiFetch(API_BASE + '/flights?source=' + encodeURIComponent(src) + '&destination=' + encodeURIComponent(dest)); }
function fetchAvailableFlights()     { return apiFetch(API_BASE + '/flights?available=true'); }
function sortFlightsByPrice()        { return apiFetch(API_BASE + '/flights?sort=price'); }
function fetchFlightById(id)         { return apiFetch(API_BASE + '/flights/' + id); }

function createBooking(data) {
  return apiFetch(API_BASE + '/bookings', { method: 'POST', body: JSON.stringify(data) });
}
function cancelBooking(bookingId) {
  return apiFetch(API_BASE + '/bookings/' + bookingId + '/cancel', { method: 'PUT' });
}
function fetchBookingById(bookingId)         { return apiFetch(API_BASE + '/bookings/' + bookingId); }
function fetchPassengerBookings(passengerId) { return apiFetch(API_BASE + '/bookings?passengerId=' + passengerId); }
function fetchConfirmedBookings()            { return apiFetch(API_BASE + '/bookings?status=CONFIRMED'); }

function registerPassenger(data) {
  return apiFetch(API_BASE + '/passengers', { method: 'POST', body: JSON.stringify(data) });
}
function fetchPassengerById(id) { return apiFetch(API_BASE + '/passengers/' + id); }

function fetchRevenue() { return apiFetch(API_BASE + '/revenue'); }

function getErrorMessage(err) {
  if (err && err.body && err.body.message) return err.body.message;
  if (err && err.message) return err.message;
  return 'An unexpected error occurred.';
}

function isFlightFull(err) {
  var msg = getErrorMessage(err).toLowerCase();
  return msg.indexOf('full') >= 0 || msg.indexOf('no seats') >= 0 || (err && err.status === 409);
}

function extractData(response) {
  if (Array.isArray(response)) return response;
  if (response && response.data !== undefined) return response.data;
  return response;
}

window.AirlineAPI = {
  fetchAllFlights: fetchAllFlights,
  searchFlights: searchFlights,
  fetchAvailableFlights: fetchAvailableFlights,
  sortFlightsByPrice: sortFlightsByPrice,
  fetchFlightById: fetchFlightById,
  createBooking: createBooking,
  cancelBooking: cancelBooking,
  fetchBookingById: fetchBookingById,
  fetchPassengerBookings: fetchPassengerBookings,
  fetchConfirmedBookings: fetchConfirmedBookings,
  registerPassenger: registerPassenger,
  fetchPassengerById: fetchPassengerById,
  fetchRevenue: fetchRevenue,
  getErrorMessage: getErrorMessage,
  isFlightFull: isFlightFull,
  extractData: extractData
};
