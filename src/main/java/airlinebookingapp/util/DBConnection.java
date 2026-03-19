package airlinebookingapp.util;

import java.sql.*;

public class DBConnection {
	private static final String URL = "jdbc:postgresql://localhost:5432/airline_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "2114";

    public static Connection getConnection() {
        try {
        	Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
        catch (Exception e) { 
            e.printStackTrace(); 
        } 
        return null;
    }
}
