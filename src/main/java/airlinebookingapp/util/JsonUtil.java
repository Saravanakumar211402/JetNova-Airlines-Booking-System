package airlinebookingapp.util;

import airlinebookingapp.model.Booking;
import airlinebookingapp.model.Flight;
import airlinebookingapp.model.Passenger;

import java.util.List;

public class JsonUtil {

    // ---- Flight ----
    public static String toJson(Flight f) {
        return "{" +
            "\"flightId\":\"" + esc(f.getFlightId()) + "\"," +
            "\"flightNumber\":\"" + esc(f.getFlightNumber()) + "\"," +
            "\"source\":\"" + esc(f.getSource()) + "\"," +
            "\"destination\":\"" + esc(f.getDestination()) + "\"," +
            "\"price\":" + f.getPrice() + "," +
            "\"totalSeats\":" + f.getTotalSeats() + "," +
            "\"availableSeats\":" + f.getAvailableSeats() +
            "}";
    }

    public static String toJsonArray(List<Flight> flights) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < flights.size(); i++) {
            sb.append(toJson(flights.get(i)));
            if (i < flights.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    // ---- Passenger ----
    public static String toJson(Passenger p) {
        return "{" +
            "\"passengerId\":\"" + esc(p.getPassengerId()) + "\"," +
            "\"name\":\"" + esc(p.getName()) + "\"," +
            "\"email\":\"" + esc(p.getEmail()) + "\"," +
            "\"phone\":" + p.getPhone() +
            "}";
    }

    // ---- Booking ----
    public static String toJson(Booking b) {
        String flightInfo = "{" +
            "\"flightId\":\"" + esc(b.getFlight().getFlightId()) + "\"," +
            "\"flightNumber\":\"" + esc(b.getFlight().getFlightNumber() != null ? b.getFlight().getFlightNumber() : "") + "\"," +
            "\"source\":\"" + esc(b.getFlight().getSource() != null ? b.getFlight().getSource() : "") + "\"," +
            "\"destination\":\"" + esc(b.getFlight().getDestination() != null ? b.getFlight().getDestination() : "") + "\"," +
            "\"price\":" + b.getFlight().getPrice() +
            "}";

        String passengerInfo = "{" +
            "\"passengerId\":\"" + esc(b.getPassenger().getPassengerId()) + "\"," +
            "\"name\":\"" + esc(b.getPassenger().getName() != null ? b.getPassenger().getName() : "") + "\"" +
            "}";

        return "{" +
            "\"bookingId\":\"" + esc(b.getBookingId()) + "\"," +
            "\"passenger\":" + passengerInfo + "," +
            "\"flight\":" + flightInfo + "," +
            "\"seats\":" + b.getSeats() + "," +
            "\"bookDate\":\"" + esc(b.getBookDate()) + "\"," +
            "\"status\":\"" + esc(b.getStatus()) + "\"," +
            "\"farePaid\":" + b.getFarePaid() +
            "}";
    }

    public static String toJsonBookingArray(List<Booking> bookings) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < bookings.size(); i++) {
            sb.append(toJson(bookings.get(i)));
            if (i < bookings.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    // ---- Success / Error wrappers ----
    public static String success(String dataJson) {
        return "{\"status\":\"success\",\"data\":" + dataJson + "}";
    }

    public static String error(String message) {
        return "{\"status\":\"error\",\"message\":\"" + esc(message) + "\"}";
    }

    // ---- Helpers ----
    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
