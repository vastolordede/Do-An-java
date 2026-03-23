package flightbooking.bus;

import flightbooking.dao.GheDAO;
import flightbooking.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class GheGeneratorBUS {

    private final GheDAO gheDAO = new GheDAO();

    public void taoGheTheoHang(
        int mayBayId,
        int hangGheId,
        String tenHang,
        int soGhe,
        int soHang,
        int soCot,
        int rowOffset
) {
    int seatCreated = 0;

    String sql =
            "INSERT INTO ghe (maybay_id, hangghe_id, tang, row_index, col_index, tenghe, trangthai) " +
            "VALUES (?,?,?,?,?,?,?)";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        conn.setAutoCommit(false);

        outer:
        for (int r = 0; r < soHang; r++) {
            int rowIndex = rowOffset + r + 1;

            for (int c = 1; c <= soCot; c++) {

                if (seatCreated >= soGhe) break outer; // ✅ FIX

                char colLetter = (char) ('A' + c - 1);
                String tenGheMoi = rowIndex + "" + colLetter;

                ps.setInt(1, mayBayId);
                ps.setInt(2, hangGheId);
                ps.setInt(3, 1);
                ps.setInt(4, rowIndex);
                ps.setInt(5, c);
                ps.setString(6, tenGheMoi);
                ps.setInt(7, 1);

                ps.addBatch();
                seatCreated++;
            }
        }

        ps.executeBatch();
        conn.commit();

        System.out.println("Đã tạo " + seatCreated + " ghế cho " + tenHang);

    } catch (Exception e) {
        e.printStackTrace();
        try {
            DBConnection.getConnection().rollback(); // ❌ sai kiểu cũ → FIX dưới
        } catch (Exception ignored) {}
    }
}
}