package flightbooking.dao;

import flightbooking.dto.ChucVuDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChucVuDAO extends BaseDAO {

    private ChucVuDTO map(ResultSet rs) throws SQLException {  // map() → ánh xạ một dòng dữ liệu từ ResultSet thành một đối tượng ChucVuDTO
        ChucVuDTO cv = new ChucVuDTO();
        cv.setChucVuId(rs.getInt("chucvu_id"));  //rs.getInt() → lấy giá trị cột dạng số nguyên, nếu cột có giá trị NULL thì trả về 0
        cv.setTenChucVu(rs.getString("tenchucvu"));  //rs.getString() → lấy giá trị cột dạng chuỗi, nếu cột có giá trị NULL thì trả về null
        return cv;
    }

    public List<ChucVuDTO> findAll() { // findAll() → lấy tất cả chức vụ từ cơ sở dữ liệu và trả về dưới dạng danh sách ChucVuDTO
        String sql = "SELECT chucvu_id, tenchucvu FROM chucvu ORDER BY chucvu_id ASC";// câu lệnh SQL để lấy tất cả chức vụ, sắp xếp theo ID tăng dần
        List<ChucVuDTO> list = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("chucvu findAll failed", e);
        }
    }

    public ChucVuDTO findById(int id) { // tìm chức vụ theo ID, nếu không tìm thấy trả về null
        String sql = "SELECT chucvu_id, tenchucvu FROM chucvu WHERE chucvu_id = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("chucvu findById failed", e);
        }
    }

    public int insert(ChucVuDTO cv) {
        String sql = "INSERT INTO chucvu(tenchucvu) VALUES (?)";

        try (Connection c = getConnection();
            PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cv.getTenChucVu());

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
            throw new RuntimeException("chucvu insert failed", e);
        }
}

    public void update(ChucVuDTO cv) {
        String sql = "UPDATE chucvu SET tenchucvu = ? WHERE chucvu_id = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cv.getTenChucVu());
            ps.setInt(2, cv.getChucVuId());
            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new RuntimeException("Không tìm thấy chức vụ để cập nhật!");
            }
        } catch (SQLException e) {
            throw new RuntimeException("chucvu update failed", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM chucvu WHERE chucvu_id = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new RuntimeException("Không tìm thấy chức vụ để xóa!");
            }
        } catch (SQLException e) {
            throw new RuntimeException("chucvu delete failed", e);
        }
    }

    public ChucVuDTO findExactByTen(String ten) {
        String sql = "SELECT chucvu_id, tenchucvu FROM chucvu WHERE LOWER(tenchucvu) = LOWER(?)";
        try (Connection c = getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, ten.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("chucvu findExactByTen failed", e);
        }
    }

    public List<ChucVuDTO> searchByTen(String ten) {
        String sql = "SELECT chucvu_id, tenchucvu FROM chucvu WHERE LOWER(tenchucvu) LIKE LOWER(?)";
        List<ChucVuDTO> list = new ArrayList<>();

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + ten.trim() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
           }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("chucvu searchByTen failed", e);
        }
    }

    public int countNhanVienByChucVu(int chucVuId) {

    String sql = "SELECT COUNT(*) AS total FROM nhanvien WHERE chucvu_id = ?";

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, chucVuId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }

        return 0;

    } catch (SQLException e) {
        throw new RuntimeException("Lỗi khi đếm số nhân viên của chức vụ", e);
    }
}
}