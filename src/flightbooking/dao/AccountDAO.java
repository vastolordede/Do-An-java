package flightbooking.dao;

import flightbooking.dto.AccountDTO;
import flightbooking.util.DBConnection;

import java.sql.*;

public class AccountDAO {

    public AccountDTO getAccountByUsername(String tenDangNhap) {
        AccountDTO account = null;
        String sql = "SELECT * FROM taikhoannhanvien WHERE tendangnhap = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tenDangNhap);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                account = new AccountDTO();
                account.setTaiKhoanId(rs.getInt("taikhoannhanvien_id"));
                account.setNhanVienId(rs.getInt("nhanvien_id"));
                account.setTenDangNhap(rs.getString("tendangnhap"));
                account.setMatKhauMaHoa(rs.getString("matkhau_mahoa"));

                int nhomId = rs.getInt("nhomquyen_id");
                account.setNhomQuyenId(rs.wasNull() ? 0 : nhomId);

                account.setDangHoatDong(rs.getBoolean("danghoatdong"));
            }
        } catch (SQLException e) {
            System.err.println("getAccountByUsername failed: " + e.getMessage());
            e.printStackTrace();
        }

        return account;
    }

    public boolean insertAccount(AccountDTO account) {
        String sql = "INSERT INTO taikhoannhanvien (nhanvien_id, tendangnhap, matkhau_mahoa, nhomquyen_id, danghoatdong) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, account.getNhanVienId());
            ps.setString(2, account.getTenDangNhap());
            ps.setString(3, account.getMatKhauMaHoa());

            if (account.getNhomQuyenId() == 0) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, account.getNhomQuyenId());
            }

            ps.setBoolean(5, true);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("insertAccount failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updateRoleByNhanVienId(int nhanVienId, int nhomQuyenId) {
        String sql = "UPDATE taikhoannhanvien SET nhomquyen_id=? WHERE nhanvien_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (nhomQuyenId == 0) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, nhomQuyenId);
            }

            ps.setInt(2, nhanVienId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("updateRole failed: " + e.getMessage());
        }

        return false;
    }

    public boolean existsNhanVien(int id) {
        String sql = "SELECT 1 FROM nhanvien WHERE nhanvien_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            System.err.println("existsNhanVien failed: " + e.getMessage());
        }

        return false;
    }

    public boolean disableByNhanVienId(int nvId) {
        String sql = "UPDATE taikhoannhanvien SET danghoatdong=false WHERE nhanvien_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, nvId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("disable failed: " + e.getMessage());
        }

        return false;
    }

    public boolean enableByNhanVienId(int nvId) {
        String sql = "UPDATE taikhoannhanvien SET danghoatdong=true WHERE nhanvien_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, nvId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("enable failed: " + e.getMessage());
        }

        return false;
    }

    public boolean updatePassword(int taiKhoanId, String newPass) {
        String sql = "UPDATE taikhoannhanvien SET matkhau_mahoa=? WHERE taikhoannhanvien_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPass);
            ps.setInt(2, taiKhoanId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("updatePassword failed: " + e.getMessage());
        }

        return false;
    }
}