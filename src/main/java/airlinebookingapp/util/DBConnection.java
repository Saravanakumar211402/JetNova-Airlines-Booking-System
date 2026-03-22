package airlinebookingapp.util;

import java.sql.*;
import java.net.URI;

public class DBConnection {

    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            
            String databaseUrl = System.getenv("DATABASE_URL");
            
            if (databaseUrl != null) {
                // Parse Railway URL: postgresql://user:password@host:port/db
                URI uri = new URI(databaseUrl);
                String host     = uri.getHost();
                int    port     = uri.getPort();
                String db       = uri.getPath().substring(1);
                String userInfo = uri.getUserInfo();
                String user     = userInfo.split(":")[0];
                String password = userInfo.split(":")[1];
                String url      = "jdbc:postgresql://" + host + ":" + port + "/" + db;
                
                System.out.println("Connecting to: " + url);
                return DriverManager.getConnection(url, user, password);
            } else {
                System.out.println("Connecting to localhost");
                return DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/airline_db", 
                    "postgres", 
                    "2114"
                );
            }

        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}