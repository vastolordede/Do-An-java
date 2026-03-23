package flightbooking.dao;

import flightbooking.dto.NhanVienDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO extends BaseDAO {

    // ================= MAP RESULTSET =================
    private NhanVienDTO map(ResultSet rs) throws SQLException {
        NhanVienDTO nv = new NhanVienDTO();

        nv.setNhanVienId(rs.getInt("nhanvien_id"));
        nv.setHoTen(rs.getString("hoten"));
        nv.setDienThoai(rs.getString("dienthoai"));
        nv.setEmail(rs.getString("email"));

        // xử lý Integer có thể null
        int pbId = rs.getInt("phongban_id");
        nv.setPhongBanId(rs.wasNull() ? null : pbId);

        int cvId = rs.getInt("chucvu_id");
        nv.setChucVuId(rs.wasNull() ? null : cvId);

        Date ngayVaoLam = rs.getDate("ngayvaolam");
        nv.setNgayVaoLam(ngayVaoLam != null ? ngayVaoLam.toLocalDate() : null);

        nv.setTrangThai(rs.getInt("trangthai"));

        Date ngayNghi = rs.getDate("ngaynghi");
        nv.setNgayNghi(ngayNghi != null ? ngayNghi.toLocalDate() : null);

        nv.setLuongCoBan(rs.getBigDecimal("luongcoban"));

        // enrich nếu có JOIN
        try { nv.setTenPhongBan(rs.getString("tenphongban")); } catch (SQLException ignored) {}
        try { nv.setTenChucVu(rs.getString("tenchucvu")); } catch (SQLException ignored) {}

        return nv;
    }

    // ================= BASE SELECT =================
    private static final String BASE_SELECT =
            "SELECT nv.nhanvien_id, nv.hoten, nv.dienthoai, nv.email, " +
            "nv.phongban_id, nv.chucvu_id, nv.ngayvaolam, " +
            "nv.trangthai, nv.ngaynghi, nv.luongcoban, " +
            "pb.tenphongban, cv.tenchucvu " +
            "FROM nhanvien nv " +
            "LEFT JOIN phongban pb ON pb.phongban_id = nv.phongban_id " +
            "LEFT JOIN chucvu cv ON cv.chucvu_id = nv.chucvu_id ";

    // ================= FIND ALL ACTIVE =================
    public List<NhanVienDTO> findAll() {
        String sql = BASE_SELECT +
                "WHERE nv.trangthai = 1 " +
                "ORDER BY nv.nhanvien_id";

        List<NhanVienDTO> list = new ArrayList<>();

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("nhanvien findAll failed", e);
        }

        return list;
    }

    // ================= FIND BY ID =================
    public NhanVienDTO findById(int id) {
        String sql = BASE_SELECT + "WHERE nv.nhanvien_id = ?";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("nhanvien findById failed", e);
        }

        return null;
    }

    // ================= SEARCH BY NAME =================
    public List<NhanVienDTO> findByHoTen(String keyword) {
        String sql = BASE_SELECT +
                "WHERE LOWER(nv.hoten) LIKE LOWER(?) " +
                "AND nv.trangthai = 1 " +
                "ORDER BY nv.nhanvien_id";

        List<NhanVienDTO> list = new ArrayList<>();

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("nhanvien findByHoTen failed", e);
        }

        return list;
    }

    // ================= INSERT =================
    public int insert(NhanVienDTO nv) {
        String sql =
                "INSERT INTO nhanvien(" +
                "hoten, dienthoai, email, phongban_id, chucvu_id, " +
                "ngayvaolam, trangthai, luongcoban) " +
                "VALUES(?,?,?,?,?,?,?,?) RETURNING nhanvien_id";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nv.getHoTen());
            ps.setString(2, nv.getDienThoai());
            ps.setString(3, nv.getEmail());

            if (nv.getPhongBanId() != null)
                ps.setInt(4, nv.getPhongBanId());
            else
                ps.setNull(4, Types.INTEGER);

            if (nv.getChucVuId() != null)
                ps.setInt(5, nv.getChucVuId());
            else
                ps.setNull(5, Types.INTEGER);

            if (nv.getNgayVaoLam() != null)
                ps.setDate(6, Date.valueOf(nv.getNgayVaoLam()));
            else
                ps.setNull(6, Types.DATE);

            ps.setInt(7, nv.getTrangThai());
            if (nv.getLuongCoBan() != null)
                ps.setBigDecimal(8, nv.getLuongCoBan());
            else
                ps.setNull(8, Types.NUMERIC);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("nhanvien insert failed", e);
        }

        return -1;
    }

    // ================= UPDATE =================
    public void update(NhanVienDTO nv) {
        String sql =
                "UPDATE nhanvien SET hoten=?, dienthoai=?, email=?, phongban_id=?, " +
                "chucvu_id=?, ngayvaolam=?, trangthai=?, ngaynghi=?, luongcoban=? " +
                "WHERE nhanvien_id=?";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nv.getHoTen());
            ps.setString(2, nv.getDienThoai());
            ps.setString(3, nv.getEmail());

            if (nv.getPhongBanId() != null)
                ps.setInt(4, nv.getPhongBanId());
            else
                ps.setNull(4, Types.INTEGER);

            if (nv.getChucVuId() != null)
                ps.setInt(5, nv.getChucVuId());
            else
                ps.setNull(5, Types.INTEGER);

            if (nv.getNgayVaoLam() != null)
                ps.setDate(6, Date.valueOf(nv.getNgayVaoLam()));
            else
                ps.setNull(6, Types.DATE);

            ps.setInt(7, nv.getTrangThai());

            if (nv.getNgayNghi() != null)
                ps.setDate(8, Date.valueOf(nv.getNgayNghi()));
            else
                ps.setNull(8, Types.DATE);

            if (nv.getLuongCoBan() != null)
                ps.setBigDecimal(9, nv.getLuongCoBan());
            else
                ps.setNull(9, Types.NUMERIC);
            ps.setInt(10, nv.getNhanVienId());

            int rows = ps.executeUpdate();
            
            if (rows == 0) {
                throw new RuntimeException("Không tìm thấy nhân viên để cập nhật!");
            }

        } catch (SQLException e) {
            throw new RuntimeException("nhanvien update failed", e);
        }
    }

    // ================= SOFT DELETE =================
    public void softDelete(int id) {
        String sql =
                "UPDATE nhanvien " +
                "SET trangthai = 0, ngaynghi = CURRENT_DATE " +
                "WHERE nhanvien_id = ?";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            
            if (rows == 0) {
                throw new RuntimeException("Không tìm thấy nhân viên để cập nhật!");
            }

        } catch (SQLException e) {
            throw new RuntimeException("nhanvien softDelete failed", e);
        }
    }

    // ================= RESTORE =================
    public void restore(int id) {
        String sql =
                "UPDATE nhanvien " +
                "SET trangthai = 1, ngaynghi = NULL " +
                "WHERE nhanvien_id = ?";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new RuntimeException("Không tìm thấy nhân viên để cập nhật!");
            }

        } catch (SQLException e) {
            throw new RuntimeException("nhanvien restore failed", e);
        }
    }

    // ================= LẤY NHÓM QUYỀN CỦA NHÂN VIÊN =================
public Integer getNhomQuyenIdByNhanVien(int nhanVienId) {
    String sql = "SELECT nhomquyen_id FROM taikhoannhanvien WHERE nhanvien_id = ?";

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, nhanVienId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int id = rs.getInt("nhomquyen_id");
                return rs.wasNull() ? null : id;
            }
        }

    } catch (SQLException e) {
        throw new RuntimeException("getNhomQuyenIdByNhanVien failed", e);
    }

    return null;
}

public void saveNhomQuyenForNhanVien(int nhanVienId, int nhomQuyenId) {
    String sql = "UPDATE taikhoannhanvien SET nhomquyen_id = ? WHERE nhanvien_id = ? AND (nhomquyen_id IS NULL OR nhomquyen_id = 0)";

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, nhomQuyenId);
        ps.setInt(2, nhanVienId);

        int rows = ps.executeUpdate();
        if (rows == 0) {
            throw new RuntimeException("Nhân viên đã có nhóm quyền hoặc không tìm thấy tài khoản.");
        }

    } catch (SQLException e) {
        throw new RuntimeException("saveNhomQuyenForNhanVien failed", e);
    }
}

public void updateNhomQuyenForNhanVien(int nhanVienId, int nhomQuyenId) {
    String sql = "UPDATE taikhoannhanvien SET nhomquyen_id = ? WHERE nhanvien_id = ?";

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, nhomQuyenId);
        ps.setInt(2, nhanVienId);

        int rows = ps.executeUpdate();
        if (rows == 0) {
            throw new RuntimeException("Không tìm thấy tài khoản của nhân viên.");
        }

    } catch (SQLException e) {
        throw new RuntimeException("updateNhomQuyenForNhanVien failed", e);
    }
}

// ================= LẤY DANH SÁCH QUYỀN THEO NHÂN VIÊN =================
public List<Integer> getPermissionIdsByNhanVien(int nhanVienId) {
    Integer nhomQuyenId = getNhomQuyenIdByNhanVien(nhanVienId);

    if (nhomQuyenId == null || nhomQuyenId <= 0) {
        return new ArrayList<>();
    }

    String sql =
        "SELECT quyen_id " +
        "FROM nhomquyen_quyen " +
        "WHERE nhomquyen_id = ? " +
        "ORDER BY quyen_id";

    List<Integer> list = new ArrayList<>();

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, nhomQuyenId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getInt("quyen_id"));
            }
        }

    } catch (SQLException e) {
        throw new RuntimeException("getPermissionIdsByNhanVien failed", e);
    }

    return list;
}

// ================= TẠO NHÓM QUYỀN RIÊNG CHO NHÂN VIÊN =================
private int createPrivateNhomQuyen(Connection c, int nhanVienId) throws SQLException {
    String getNameSql = "SELECT hoten FROM nhanvien WHERE nhanvien_id = ?";
    String tenNhanVien = "NV_" + nhanVienId;

    try (PreparedStatement ps = c.prepareStatement(getNameSql)) {
        ps.setInt(1, nhanVienId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String hoten = rs.getString("hoten");
                if (hoten != null && !hoten.trim().isEmpty()) {
                    tenNhanVien = hoten.trim();
                }
            }
        }
    }

    String insertNhomSql =
        "INSERT INTO nhomquyen(tennhomquyen) VALUES (?) RETURNING nhomquyen_id";

    String tenNhom = "ROLE_NV_" + nhanVienId + "_" + tenNhanVien;

    try (PreparedStatement ps = c.prepareStatement(insertNhomSql)) {
        ps.setString(1, tenNhom);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
    }

    throw new RuntimeException("Không tạo được nhóm quyền riêng cho nhân viên");
}

// ================= GÁN NHÓM QUYỀN CHO TÀI KHOẢN =================
private void assignNhomQuyenToNhanVienAccount(Connection c, int nhanVienId, int nhomQuyenId) throws SQLException {
    String sql = "UPDATE taikhoannhanvien SET nhomquyen_id = ? WHERE nhanvien_id = ?";

    try (PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setInt(1, nhomQuyenId);
        ps.setInt(2, nhanVienId);

        int rows = ps.executeUpdate();
        if (rows == 0) {
            throw new RuntimeException("Không tìm thấy tài khoản của nhân viên để gán nhóm quyền");
        }
    }
}

// ================= XÓA HẾT QUYỀN CŨ CỦA NHÓM =================
private void clearPermissionsOfNhom(Connection c, int nhomQuyenId) throws SQLException {
    String sql = "DELETE FROM nhomquyen_quyen WHERE nhomquyen_id = ?";

    try (PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setInt(1, nhomQuyenId);
        ps.executeUpdate();
    }
}

// ================= THÊM QUYỀN VÀO NHÓM =================
private void insertPermissionsToNhom(Connection c, int nhomQuyenId, List<Integer> permissionIds) throws SQLException {
    if (permissionIds == null || permissionIds.isEmpty()) return;

    String sql = "INSERT INTO nhomquyen_quyen(nhomquyen_id, quyen_id) VALUES (?, ?)";

    try (PreparedStatement ps = c.prepareStatement(sql)) {
        for (Integer quyenId : permissionIds) {
            if (quyenId == null || quyenId <= 0) continue;

            ps.setInt(1, nhomQuyenId);
            ps.setInt(2, quyenId);
            ps.addBatch();
        }
        ps.executeBatch();
    }
}

// ================= LƯU QUYỀN LẦN ĐẦU CHO NHÂN VIÊN =================
public void savePermissionsForNhanVien(int nhanVienId, List<Integer> permissionIds) {
    try (Connection c = getConnection()) {
        c.setAutoCommit(false);

        try {
            Integer currentNhomQuyenId = getNhomQuyenIdByNhanVien(nhanVienId);

            if (currentNhomQuyenId != null && currentNhomQuyenId > 0) {
                List<Integer> currentPermissions = getPermissionIdsByNhanVien(nhanVienId);
                if (currentPermissions != null && !currentPermissions.isEmpty()) {
                    throw new RuntimeException("Nhân viên đã có quyền, hãy dùng cập nhật.");
                }
            }

            int nhomQuyenId;
            if (currentNhomQuyenId == null || currentNhomQuyenId <= 0) {
                nhomQuyenId = createPrivateNhomQuyen(c, nhanVienId);
                assignNhomQuyenToNhanVienAccount(c, nhanVienId, nhomQuyenId);
            } else {
                nhomQuyenId = currentNhomQuyenId;
            }

            clearPermissionsOfNhom(c, nhomQuyenId);
            insertPermissionsToNhom(c, nhomQuyenId, permissionIds);

            c.commit();

        } catch (Exception ex) {
            c.rollback();
            throw ex;
        } finally {
            c.setAutoCommit(true);
        }

    } catch (SQLException e) {
        throw new RuntimeException("savePermissionsForNhanVien failed", e);
    }
}

// ================= CẬP NHẬT QUYỀN CHO NHÂN VIÊN =================
public void updatePermissionsForNhanVien(int nhanVienId, List<Integer> permissionIds) {
    try (Connection c = getConnection()) {
        c.setAutoCommit(false);

        try {
            Integer nhomQuyenId = getNhomQuyenIdByNhanVien(nhanVienId);

            if (nhomQuyenId == null || nhomQuyenId <= 0) {
                throw new RuntimeException("Nhân viên chưa có nhóm quyền để cập nhật. Hãy dùng Lưu lần đầu.");
            }

            clearPermissionsOfNhom(c, nhomQuyenId);
            insertPermissionsToNhom(c, nhomQuyenId, permissionIds);

            c.commit();

        } catch (Exception ex) {
            c.rollback();
            throw ex;
        } finally {
            c.setAutoCommit(true);
        }

    } catch (SQLException e) {
        throw new RuntimeException("updatePermissionsForNhanVien failed", e);
    }
}
}