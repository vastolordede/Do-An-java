package flightbooking.dao;

import flightbooking.dto.HoaDonVeDTO;

import java.sql.*;

public class HoaDonVeDAO extends BaseDAO {

    public void insert(HoaDonVeDTO d) {
        String sql = "insert into hoadonve(hoadon_id, ve_id) values (?,?)";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            // dùng getter
            ps.setInt(1, d.getHoaDonId());
            ps.setInt(2, d.getVeId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("hoadonve insert failed", e);
        }
    }
}
