package flightbooking.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/flightbooking";
    private static final String USER = "postgres";
    private static final String PASS = "123456";

    static {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("PostgreSQL Driver loaded");
        } catch (ClassNotFoundException e) {
            System.out.println("Khong tim thay PostgreSQL Driver");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            System.out.println("Connecting to local PostgreSQL...");
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Connected successfully!");
            return conn;
        } catch (SQLException e) {
            System.out.println("Ket noi that bai:");
            e.printStackTrace();
            return null;
        }
    }
}