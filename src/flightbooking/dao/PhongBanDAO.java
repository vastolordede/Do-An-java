package flightbooking.dao;

import flightbooking.dto.PhongBanDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhongBanDAO extends BaseDAO {

    private PhongBanDTO map(ResultSet rs) throws SQLException {
        PhongBanDTO pb = new PhongBanDTO();
        pb.setPhongBanId(rs.getInt("phongban_id"));
        pb.setTenPhongBan(rs.getString("tenphongban"));
        return pb;
    }

    public List<PhongBanDTO> findAll() {
        String sql = "SELECT phongban_id, tenphongban FROM phongban ORDER BY phongban_id";
        List<PhongBanDTO> list = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("phongban findAll failed", e);
        }
        return list;
    }

    public PhongBanDTO findById(int id) {
        String sql = "SELECT phongban_id, tenphongban FROM phongban WHERE phongban_id=?";
        try (Connection c = getConnection();
        PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("phongban findById failed", e);
        }
    }
    public int insert(PhongBanDTO pb) {

        String sql = "INSERT INTO phongban(tenphongban) VALUES(?)";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, pb.getTenPhongBan());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Insert failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("phongban insert failed", e);
        }
    }

    public void update(PhongBanDTO pb) {
        String sql = "UPDATE phongban SET tenphongban=? WHERE phongban_id=?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, pb.getTenPhongBan());
            ps.setInt(2, pb.getPhongBanId());
            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new RuntimeException("Không tìm thấy phòng ban để cập nhật!");
            }
        } catch (SQLException e) {
            throw new RuntimeException("phongban update failed", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM phongban WHERE phongban_id=?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new RuntimeException("Không tìm thấy phòng ban để xóa!");
            }
        } catch (SQLException e) {
            throw new RuntimeException("phongban delete failed", e);
        }
    }
 public PhongBanDTO findExactByTen(String ten) {

    String sql = "SELECT phongban_id, tenphongban FROM phongban WHERE LOWER(tenphongban) = LOWER(?)";

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setString(1, ten.trim());

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return map(rs);
        }

        return null;

    } catch (SQLException e) {
        throw new RuntimeException("phongban findExactByTen failed", e);
    }
}

    public int countNhanVienByPhongBan(int phongBanId) {

    String sql = "SELECT COUNT(*) AS total FROM nhanvien WHERE phongban_id = ?";

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, phongBanId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }

        return 0;

    } catch (SQLException e) {
        throw new RuntimeException("Lỗi khi đếm nhân viên theo phòng ban", e);
    }
}
}