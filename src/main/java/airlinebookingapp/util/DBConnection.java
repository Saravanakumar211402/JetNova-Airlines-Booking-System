package airlinebookingapp.util;

import java.sql.*;

public class DBConnection {

    public static Connection getConnection() {
        try {
            String host     = System.getenv("PGHOST");
            String db       = System.getenv("PGDATABASE");
            String port     = System.getenv("PGPORT") != null ? System.getenv("PGPORT") : "5432";
            String user     = System.getenv("PGUSER") != null ? System.getenv("PGUSER") : "postgres";
            String password = System.getenv("PGPASSWORD") != null ? System.getenv("PGPASSWORD") : "2114";

            String url;
            if (host != null && db != null) {
                url = "jdbc:postgresql://" + host + ":" + port + "/" + db;
            } else {
                url = "jdbc:postgresql://localhost:5432/airline_db";
            }

            System.out.println("Connecting to: " + url);
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(url, user, password);

        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}