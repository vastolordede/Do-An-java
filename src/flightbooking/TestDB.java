package flightbooking;

import java.sql.Connection;

import flightbooking.util.DBConnection;

public class TestDB {
    public static void main(String[] args) {
        try (Connection c = DBConnection.getConnection()) {
            System.out.println("CONNECTED NEON SUCCESS");
            System.out.println(c.getMetaData().getURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
