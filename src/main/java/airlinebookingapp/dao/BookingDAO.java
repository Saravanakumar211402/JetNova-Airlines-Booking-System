package airlinebookingapp.dao;

import airlinebookingapp.model.Booking;
import airlinebookingapp.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookingDAO {

    public void saveBooking(Booking booking) {
        String sql = "INSERT INTO bookings (booking_id, passenger_id, flight_id, seats, book_date, status, fare_paid) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, booking.getBookingId());
            pstmt.setString(2, booking.getPassenger().getPassengerId());
            pstmt.setString(3, booking.getFlight().getFlightId());
            pstmt.setInt(4, booking.getSeats());
            pstmt.setString(5, booking.getBookDate());
            pstmt.setString(6, booking.getStatus());
            pstmt.setDouble(7, booking.getFarePaid());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<Booking> findBookingById(String bookingId) {
        String sql = "SELECT b.booking_id, b.passenger_id, p.name, b.flight_id, f.flight_number, " +
                "f.source, f.destination, f.price, b.seats, b.book_date, b.status, b.fare_paid " +
                "FROM bookings b " +
                "JOIN airplanes f ON b.flight_id = f.flight_id " +
                "LEFT JOIN passengers p ON b.passenger_id = p.passenger_id " +
                "WHERE LOWER(b.booking_id) = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookingId.toLowerCase());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Booking booking = new Booking(
                    rs.getString("booking_id"),
                    rs.getString("passenger_id"),
                    rs.getString("name"),
                    rs.getString("flight_id"),
                    rs.getInt("seats"),
                    rs.getString("book_date"),
                    rs.getString("status"),
                    rs.getDouble("fare_paid")
                );
                booking.getFlight().setFlightNumber(rs.getString("flight_number"));
                booking.getFlight().setSource(rs.getString("source"));
                booking.getFlight().setDestination(rs.getString("destination"));
                booking.getFlight().setPrice(rs.getDouble("price"));
                return Optional.of(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Booking> findBookingsByPassenger(String passengerId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.booking_id, b.passenger_id, p.name, b.flight_id, f.flight_number, " +
                "f.source, f.destination, f.price, b.seats, b.book_date, b.status, b.fare_paid " +
                "FROM bookings b " +
                "JOIN airplanes f ON b.flight_id = f.flight_id " +
                "LEFT JOIN passengers p ON b.passenger_id = p.passenger_id " +
                "WHERE LOWER(b.passenger_id) = ? ORDER BY b.book_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, passengerId.toLowerCase());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Booking b = new Booking(
                    rs.getString("booking_id"),
                    rs.getString("passenger_id"),
                    rs.getString("name"),
                    rs.getString("flight_id"),
                    rs.getInt("seats"),
                    rs.getString("book_date"),
                    rs.getString("status"),
                    rs.getDouble("fare_paid")
                );
                b.getFlight().setFlightNumber(rs.getString("flight_number"));
                b.getFlight().setSource(rs.getString("source"));
                b.getFlight().setDestination(rs.getString("destination"));
                b.getFlight().setPrice(rs.getDouble("price"));
                bookings.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public List<Booking> findAllConfirmedBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.booking_id, b.passenger_id, p.name, b.flight_id, f.flight_number, " +
                "f.source, f.destination, f.price, b.seats, b.book_date, b.status, b.fare_paid " +
                "FROM bookings b " +
                "JOIN airplanes f ON b.flight_id = f.flight_id " +
                "LEFT JOIN passengers p ON b.passenger_id = p.passenger_id " +
                "WHERE b.status='CONFIRMED' ORDER BY b.book_date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Booking b = new Booking(
                    rs.getString("booking_id"),
                    rs.getString("passenger_id"),
                    rs.getString("name"),
                    rs.getString("flight_id"),
                    rs.getInt("seats"),
                    rs.getString("book_date"),
                    rs.getString("status"),
                    rs.getDouble("fare_paid")
                );
                b.getFlight().setFlightNumber(rs.getString("flight_number"));
                b.getFlight().setSource(rs.getString("source"));
                b.getFlight().setDestination(rs.getString("destination"));
                b.getFlight().setPrice(rs.getDouble("price"));
                bookings.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public void cancelBooking(String bookingId) {
        String sql = "UPDATE bookings SET status='CANCELLED' WHERE LOWER(booking_id)=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookingId.toLowerCase());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int countBookingsByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE status=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double calculateRevenue() {
        String sql = "SELECT COALESCE(SUM(fare_paid),0) FROM bookings WHERE status='CONFIRMED'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public double calculateCancelledRevenue() {
        String sql = "SELECT COALESCE(SUM(fare_paid),0) FROM bookings WHERE status='CANCELLED'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
