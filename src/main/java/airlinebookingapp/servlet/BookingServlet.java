package airlinebookingapp.servlet;

import airlinebookingapp.dao.BookingDAO;
import airlinebookingapp.exception.BookingNotFoundException;
import airlinebookingapp.exception.FlightFullException;
import airlinebookingapp.exception.FlightNotFoundException;
import airlinebookingapp.model.Booking;
import airlinebookingapp.model.Flight;
import airlinebookingapp.model.Passenger;
import airlinebookingapp.service.BookingService;
import airlinebookingapp.service.FlightService;
import airlinebookingapp.util.JsonUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/bookings/*")
public class BookingServlet extends HttpServlet {

    private final BookingService bookingService = new BookingService();
    private final FlightService flightService = new FlightService();
    private final BookingDAO bookingDAO = new BookingDAO();

    private void cors(HttpServletResponse res) {
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type");
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res) throws IOException {
        cors(res); res.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        cors(res);
        String pathInfo = req.getPathInfo(); // null, /, /B123, /B123/cancel
        String passengerId = req.getParameter("passengerId");
        String status = req.getParameter("status");

        try {
            // GET /api/bookings/{id}  — single booking lookup
            if (pathInfo != null && pathInfo.length() > 1 && !pathInfo.substring(1).contains("/")) {
                String id = pathInfo.substring(1);
                Booking booking = bookingService.searchByBookingId(id);
                res.getWriter().write(JsonUtil.success(JsonUtil.toJson(booking)));
                return;
            }

            if (passengerId != null) {
                List<Booking> bookings = bookingDAO.findBookingsByPassenger(passengerId);
                res.getWriter().write(JsonUtil.success(JsonUtil.toJsonBookingArray(bookings)));
                return;
            }
            if ("CONFIRMED".equalsIgnoreCase(status)) {
                List<Booking> bookings = bookingDAO.findAllConfirmedBookings();
                res.getWriter().write(JsonUtil.success(JsonUtil.toJsonBookingArray(bookings)));
                return;
            }
            List<Booking> bookings = bookingDAO.findAllConfirmedBookings();
            res.getWriter().write(JsonUtil.success(JsonUtil.toJsonBookingArray(bookings)));

        } catch (BookingNotFoundException e) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write(JsonUtil.error(e.getMessage()));
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write(JsonUtil.error("Error: " + e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        cors(res);
        try {
            String body = readBody(req);
            String flightId      = extractJson(body, "flightId");
            String passengerName = extractJson(body, "passengerName");
            String email         = extractJson(body, "email");
            String phoneStr      = extractJson(body, "phone");
            String seatsStr      = extractJson(body, "seats");

            if (flightId == null || passengerName == null) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write(JsonUtil.error("Missing required fields: flightId, passengerName"));
                return;
            }

            int seats = seatsStr != null && !seatsStr.isBlank() ? Integer.parseInt(seatsStr.trim()) : 1;
            long phone = 0;
            try { phone = phoneStr != null && !phoneStr.isBlank() ? Long.parseLong(phoneStr.trim()) : 0; } catch (NumberFormatException ignored) {}

            Flight flight = flightService.getAvailabFlight(flightId);
            Passenger passenger = new Passenger(passengerName, email, phone);
            Booking booking = bookingService.createBooking(flight, passenger, seats);
            res.setStatus(HttpServletResponse.SC_CREATED);
            res.getWriter().write(JsonUtil.success(JsonUtil.toJson(booking)));

        } catch (FlightFullException e) {
            res.setStatus(HttpServletResponse.SC_CONFLICT);
            res.getWriter().write(JsonUtil.error(e.getMessage()));
        } catch (FlightNotFoundException e) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write(JsonUtil.error(e.getMessage()));
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write(JsonUtil.error("Error: " + e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        cors(res);
        // PUT /api/bookings/{id}/cancel
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.length() < 2) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write(JsonUtil.error("Missing booking ID"));
            return;
        }
        String[] parts = pathInfo.split("/");
        // parts[0] = "", parts[1] = bookingId, parts[2] = "cancel"
        String bookingId = parts.length > 1 ? parts[1] : null;
        if (bookingId == null) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write(JsonUtil.error("Invalid path"));
            return;
        }
        try {
            Booking booking = bookingService.searchByBookingId(bookingId);
            bookingService.cancelBooking(booking, bookingId);
            res.getWriter().write(JsonUtil.success(JsonUtil.toJson(booking)));
        } catch (BookingNotFoundException e) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write(JsonUtil.error(e.getMessage()));
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write(JsonUtil.error("Error: " + e.getMessage()));
        }
    }

    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = req.getReader()) {
            String line; while ((line = r.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    private String extractJson(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx == -1) return null;
        idx += search.length();
        while (idx < json.length() && (json.charAt(idx) == ':' || json.charAt(idx) == ' ')) idx++;
        if (idx >= json.length()) return null;
        if (json.charAt(idx) == '"') {
            int end = json.indexOf('"', idx + 1);
            return end == -1 ? null : json.substring(idx + 1, end);
        } else {
            int end = idx;
            while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '.' || json.charAt(end) == '-')) end++;
            return json.substring(idx, end);
        }
    }
}
