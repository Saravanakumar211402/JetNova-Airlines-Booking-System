
package airlinebookingapp.dao;

import airlinebookingapp.model.Flight;
import airlinebookingapp.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlightDAO {
	  public void saveFlight(Flight flight) {
	        String sql = "INSERT INTO airplanes (flight_id, flight_number, source, destination, price, total_seats, available_seats) " +
	                     "VALUES (?, ?, ?, ?, ?, ?, ?) ON CONFLICT (flight_id) DO NOTHING";
	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(sql)) {

	            pstmt.setString(1, flight.getFlightId());
	            pstmt.setString(2, flight.getFlightNumber());
	            pstmt.setString(3, flight.getSource());
	            pstmt.setString(4, flight.getDestination());
	            pstmt.setDouble(5, flight.getPrice());
	            pstmt.setInt(6, flight.getTotalSeats());
	            pstmt.setInt(7, flight.getAvailableSeats());

	            pstmt.executeUpdate();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    } 

	  public List<Flight> fetchAllFlights() {
	        List<Flight> flights = new ArrayList<>();
	        String sql = "SELECT * FROM airplanes";
	        try (Connection conn = DBConnection.getConnection();
	             Statement stmt = conn.createStatement();
	             ResultSet rs = stmt.executeQuery(sql)) {

	            while (rs.next()) {
	                flights.add(new Flight(
	                    rs.getString("flight_id"),
	                    rs.getString("flight_number"),
	                    rs.getString("source"),
	                    rs.getString("destination"),
	                    rs.getDouble("price"),
	                    rs.getInt("total_seats"),
	                    rs.getInt("available_seats")
	                ));
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return flights;
	    }


	  public List<Flight> findFlightsByRoute(String source, String destination) {
	        List<Flight> flights = new ArrayList<>();
	        String sql = "SELECT * FROM airplanes WHERE LOWER(source) = ? AND LOWER(destination) = ? ORDER BY price ASC";
	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(sql)) {

	            pstmt.setString(1, source.toLowerCase());
	            pstmt.setString(2, destination.toLowerCase());

	            ResultSet rs = pstmt.executeQuery();
	            while (rs.next()) {
	                flights.add(new Flight(
	                    rs.getString("flight_id"),
	                    rs.getString("flight_number"),
	                    rs.getString("source"),
	                    rs.getString("destination"),
	                    rs.getDouble("price"),
	                    rs.getInt("total_seats"),
	                    rs.getInt("available_seats")
	                ));
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return flights;
	    }

	  public Flight fetchAvailableFlightById(String id) {
	        String sql = "SELECT * FROM airplanes WHERE LOWER(flight_id) = ?";
	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(sql)) {

	            pstmt.setString(1, id.toLowerCase());
	            ResultSet rs = pstmt.executeQuery();
	            if (rs.next()) {
	                int availableSeats = rs.getInt("available_seats");
	                if (availableSeats > 0) {
	                    return new Flight(
	                        rs.getString("flight_id"),
	                        rs.getString("flight_number"),
	                        rs.getString("source"),
	                        rs.getString("destination"),
	                        rs.getDouble("price"),
	                        rs.getInt("total_seats"),
	                        availableSeats
	                    );
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	    }

	  public boolean updateAvailableSeats(String flightId, int seatChange) {
	        String sql = "UPDATE airplanes SET available_seats = available_seats + ? WHERE flight_id = ?";
	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(sql)) {

	            pstmt.setInt(1, seatChange);
	            pstmt.setString(2, flightId);
	            int rowsUpdated = pstmt.executeUpdate();
	            return rowsUpdated > 0;
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return false;
	    }

}
