package flightbooking.dao;

import flightbooking.dto.ThanhToanDTO;

import java.sql.*;

public class ThanhToanDAO extends BaseDAO {

    public int insert(ThanhToanDTO t) {
        String sql = "insert into thanhtoan(hoadon_id, sotien, phuongthuc, trangthai) values (?,?,?,?) returning thanhtoan_id";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            if (t.getHoaDonId() != null) ps.setInt(1, t.getHoaDonId());
            else ps.setNull(1, Types.INTEGER);

            ps.setBigDecimal(2, t.getSoTien());
            ps.setString(3, t.getPhuongThuc());

            if (t.getTrangThai() != null) ps.setInt(4, t.getTrangThai());
            else ps.setNull(4, Types.SMALLINT);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("thanhtoan insert failed", e);
        }
    }
}
