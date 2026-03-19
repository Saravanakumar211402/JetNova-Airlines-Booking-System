package airlinebookingapp.dao;

import airlinebookingapp.model.Passenger;
import airlinebookingapp.util.DBConnection;

import java.sql.*;
import java.util.Optional;

public class PassengerDAO {

    public void savePassenger(Passenger passenger) {
        String sql = "INSERT INTO passengers (passenger_id, name, email, phone) " +
                     "VALUES (?, ?, ?, ?) ON CONFLICT (passenger_id) DO NOTHING";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, passenger.getPassengerId());
            pstmt.setString(2, passenger.getName());
            pstmt.setString(3, passenger.getEmail());
            pstmt.setLong(4, passenger.getPhone());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<Passenger> findById(String passengerId) {
        String sql = "SELECT * FROM passengers WHERE LOWER(passenger_id) = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, passengerId.toLowerCase());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Passenger(
                    rs.getString("passenger_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getLong("phone")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void insertPassenger(Passenger passenger) {
        savePassenger(passenger);
    }
}
