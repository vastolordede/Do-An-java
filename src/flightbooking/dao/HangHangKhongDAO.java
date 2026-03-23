package flightbooking.dao;

import flightbooking.dto.HangHangKhongDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HangHangKhongDAO extends BaseDAO {

    private HangHangKhongDTO map(ResultSet rs) throws SQLException {
        HangHangKhongDTO h = new HangHangKhongDTO();
        h.setHangHangKhongId(rs.getInt("hanghangkhong_id"));
        h.setTenHang(rs.getString("tenhanghangkhong")); // Đảm bảo cột trong DB là tenhanghangkhong
        return h;
    }

    public List<HangHangKhongDTO> findAll() {
        String sql = "SELECT * FROM hanghangkhong ORDER BY hanghangkhong_id ASC";
        List<HangHangKhongDTO> list = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("hanghangkhong findAll failed", e);
        }
    }

    public void insert(HangHangKhongDTO h) {
        String sql = "INSERT INTO hanghangkhong (tenhanghangkhong) VALUES (?)";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, h.getTenHang());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("hanghangkhong insert failed", e);
        }
    }

    public void update(HangHangKhongDTO h) {
        String sql = "UPDATE hanghangkhong SET tenhanghangkhong=? WHERE hanghangkhong_id=?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, h.getTenHang());
            ps.setInt(2, h.getHangHangKhongId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("hanghangkhong update failed", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM hanghangkhong WHERE hanghangkhong_id=?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("hanghangkhong delete failed", e);
        }
    }
}