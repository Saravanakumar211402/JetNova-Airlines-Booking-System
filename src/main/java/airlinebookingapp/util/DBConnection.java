package airlinebookingapp.util;

import java.sql.*;

public class DBConnection {

    public static Connection getConnection() {
        try {
            // Railway environment variables
            String host = System.getenv("PGHOST");
            String port = System.getenv("PGPORT");
            String db   = System.getenv("PGDATABASE");
            String user = System.getenv("PGUSER");
            String pass = System.getenv("PGPASSWORD");

            if (host != null && port != null && db != null && user != null && pass != null) {
                String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + db;

                // Debug logs (safe — no password printed)
                System.out.println("Connecting via Railway PG variables");
                System.out.println("JDBC URL: " + jdbcUrl);
                System.out.println("User: " + user);

                Class.forName("org.postgresql.Driver");
                return DriverManager.getConnection(jdbcUrl, user, pass);
            } else {
                // Local development fallback
                String url      = "jdbc:postgresql://localhost:5432/airline_db";
                String localUser     = "postgres";
                String localPassword = "2114";

                System.out.println("Connecting via localhost (local)");

                Class.forName("org.postgresql.Driver");
                return DriverManager.getConnection(url, localUser, localPassword);
            }

        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}