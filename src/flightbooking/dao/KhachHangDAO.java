    package flightbooking.dao;

    import flightbooking.dto.KhachHangDTO;
    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;

    public class KhachHangDAO extends BaseDAO {

        // ================= MAP RESULTSET =================
        private KhachHangDTO map(ResultSet rs) throws SQLException {
        KhachHangDTO kh = new KhachHangDTO();
        
        // Dùng try/catch riêng vì không phải query nào cũng có cột này
        try { kh.setTaiKhoanKhachHangId(rs.getInt("taikhoankhachhang_id")); } 
        catch (SQLException ignored) {}
        
        try {
    kh.setKhachHangId(rs.getInt("taikhoankhachhang_id"));
} catch (Exception ignored) {}
        kh.setHoTen(rs.getString("hoten"));
        kh.setDienThoai(rs.getString("dienthoai"));
        kh.setTenDangNhap(rs.getString("email"));
        kh.setMatKhauMaHoa(rs.getString("matkhau_mahoa"));
        kh.setDangHoatDong(rs.getInt("trangthai") == 1);
          try { kh.setDiemTichLuy(rs.getInt("diemtichluy")); } catch (Exception ignored) {}
        return kh;
    }

        // ================= CHECK USER EXISTS =================
        public boolean existsByUsername(String email) {
            String sql = "SELECT 1 FROM taikhoankhachhang WHERE LOWER(email) = LOWER(?)";

            try (Connection c = getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setString(1, email.trim());

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }

            } catch (SQLException e) {
                throw new RuntimeException("khachhang existsByUsername failed", e);
            }
        }

        // ================= REGISTER =================
        public boolean register(String hoTen, String sdt, String email, String pass) {

        if (existsByUsername(email)) return false;

        String sql =
            "INSERT INTO taikhoankhachhang(email, dienthoai, matkhau_mahoa, trangthai, hoten) " +
            "VALUES(?, ?, ?, ?, ?)";

        try (Connection c = getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email.trim());
            ps.setString(2, sdt.trim());
            ps.setString(3, pass);
            ps.setInt(4, 1);
            ps.setString(5, hoTen.trim());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("khachhang register failed", e);
        }
    }

        // ================= LOGIN =================
        public boolean checkLogin(String email, String pass) {

            String sql = "SELECT 1 FROM taikhoankhachhang " +
                    "WHERE LOWER(email) = LOWER(?) " +
                    "AND matkhau_mahoa = ? " +
                    "AND trangthai = 1";

            try (Connection c = getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setString(1, email.trim());
                ps.setString(2, flightbooking.util.PasswordUtil.hash(pass));

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }

            } catch (SQLException e) {
                throw new RuntimeException("khachhang checkLogin failed", e);
            }
        }

        // ================= FIND BY EMAIL =================
        public KhachHangDTO findByUsername(String email) {
        String sql =
        "SELECT taikhoankhachhang_id, email, dienthoai, matkhau_mahoa, trangthai, hoten, diemtichluy " +
        "FROM taikhoankhachhang " +
        "WHERE LOWER(email) = LOWER(?)";

        try (Connection c = getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("khachhang findByUsername failed", e);
        }
        return null;
    }

        // ================= FIND BY ID =================
        public KhachHangDTO findById(int hanhkhachId) {

            String sql =
                    "SELECT kh.hanhkhach_id, kh.hoten, kh.dienthoai, " +
                    "tk.email, tk.matkhau_mahoa, tk.trangthai " +
                    "FROM hanhkhach kh " +
                    "JOIN taikhoankhachhang tk ON tk.dienthoai = kh.dienthoai " +
                    "WHERE kh.hanhkhach_id = ?";

            try (Connection c = getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, hanhkhachId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return map(rs);
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException("khachhang findById failed", e);
            }

            return null;
        }

        // ================= FIND ALL =================
        public List<KhachHangDTO> findAll() {

            String sql =
                    "SELECT kh.hanhkhach_id, kh.hoten, kh.dienthoai, " +
                    "tk.email, tk.matkhau_mahoa, tk.trangthai " +
                    "FROM hanhkhach kh " +
                    "JOIN taikhoankhachhang tk ON tk.dienthoai = kh.dienthoai " +
                    "WHERE tk.trangthai = 1 " +
                    "ORDER BY kh.hanhkhach_id DESC";

            List<KhachHangDTO> list = new ArrayList<>();

            try (Connection c = getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    list.add(map(rs));
                }

            } catch (SQLException e) {
                throw new RuntimeException("khachhang findAll failed", e);
            }

            return list;
        }
        public void congDiem(int taiKhoanKhachHangId, int diem) {

        String sql = 
            "UPDATE taikhoankhachhang " +
            "SET diemtichluy = COALESCE(diemtichluy, 0) + ? " +
            "WHERE taikhoankhachhang_id = ?";

        try (Connection c = getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, diem);
            ps.setInt(2, taiKhoanKhachHangId);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("congDiem failed", e);
        }
    }
    public int getDiem(int id) {
        String sql = "SELECT diemtichluy FROM taikhoankhachhang WHERE taikhoankhachhang_id=?";

        try (Connection c = getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("diemtichluy");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return 0;
    }
    public void truDiem(int id, int diem) {
        String sql =
            "UPDATE taikhoankhachhang " +
            "SET diemtichluy = GREATEST(COALESCE(diemtichluy,0) - ?, 0) " +
            "WHERE taikhoankhachhang_id=?";

        try (Connection c = getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, diem);
            ps.setInt(2, id);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void updatePassword(int id, String newHash) {
    String sql = "UPDATE taikhoankhachhang SET matkhau_mahoa = ? WHERE taikhoankhachhang_id = ?";

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setString(1, newHash);
        ps.setInt(2, id);
        ps.executeUpdate();

    } catch (SQLException e) {
        throw new RuntimeException("updatePassword failed", e);
    }
}
    }