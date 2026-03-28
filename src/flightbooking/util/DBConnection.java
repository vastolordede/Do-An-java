package flightbooking.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
        "jdbc:postgresql://localhost:5432/flightbooking";
    private static final String USER = "postgres";
    private static final String PASS = "N241206h@"; // đổi theo máy bạn

    static {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("PostgreSQL Driver loaded");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        System.out.println("Connecting to local PostgreSQL...");
        return DriverManager.getConnection(URL, USER, PASS);
    }
}