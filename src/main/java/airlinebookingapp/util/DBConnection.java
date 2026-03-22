package airlinebookingapp.util;

import java.sql.*;

public class DBConnection {

    public static Connection getConnection() {
        try {
            String databaseUrl = System.getenv("DATABASE_URL");
            
            if (databaseUrl != null) {
                // Railway provides: postgresql://user:password@host:port/db
                // Convert to JDBC format
                String jdbcUrl = databaseUrl.replace("postgresql://", "jdbc:postgresql://");
                System.out.println("Connecting via DATABASE_URL (Railway)");
                Class.forName("org.postgresql.Driver");
                return DriverManager.getConnection(jdbcUrl);
            } else {
                // Local development
                String url      = "jdbc:postgresql://localhost:5432/airline_db";
                String user     = "postgres";
                String password = "2114";
                System.out.println("Connecting via localhost (local)");
                Class.forName("org.postgresql.Driver");
                return DriverManager.getConnection(url, user, password);
            }

        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
