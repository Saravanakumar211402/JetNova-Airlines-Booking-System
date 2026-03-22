package airlinebookingapp.util;

import java.sql.*;

public class DBConnection {

    private static String getUrl() {
        String host = System.getenv("PGHOST");
        String db   = System.getenv("PGDATABASE");
        String port = System.getenv("PGPORT") != null ? System.getenv("PGPORT") : "5432";
        if (host != null && db != null) {
            return "jdbc:postgresql://" + host + ":" + port + "/" + db;
        }
        return "jdbc:postgresql://localhost:5432/airline_db";
    }

    private static final String URL      = getUrl();
    private static final String USER     = System.getenv("PGUSER")     != null ? System.getenv("PGUSER")     : "postgres";
    private static final String PASSWORD = System.getenv("PGPASSWORD") != null ? System.getenv("PGPASSWORD") : "2114";

    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}