package airlinebookingapp.util;

import java.sql.*;

public class DBConnection {
	private static final String URL = System.getenv("DATABASE_URL") != null ? System.getenv("DATABASE_URL") : "jdbc:postgresql://localhost:5432/airline_db";
	private static final String USER = System.getenv("PGUSER") != null ? System.getenv("PGUSER") : "postgres";
	private static final String PASSWORD = System.getenv("PGPASSWORD") != null ? System.getenv("PGPASSWORD") : "2114";

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
