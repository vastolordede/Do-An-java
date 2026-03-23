package flightbooking.dao;

import flightbooking.dto.HanhKhachDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HanhKhachDAO extends BaseDAO {

    private HanhKhachDTO map(ResultSet rs) throws SQLException {
    HanhKhachDTO hk = new HanhKhachDTO();

    hk.setHanhKhachId(rs.getInt("hanhkhach_id"));
    hk.setHoTen(rs.getString("hoten"));

    Date ngaySinh = rs.getDate("ngaysinh");
    if (ngaySinh != null)
        hk.setNgaySinh(ngaySinh.toLocalDate());

    hk.setSoGiayTo(rs.getString("sogiayto"));
    hk.setQuocTich(rs.getString("quoctich"));

    return hk;
}

public List<HanhKhachDTO> findByTaiKhoan(int taiKhoanId) {
    String sql =
        "SELECT hk.* " +
        "FROM hanhkhach hk " +
        "JOIN hanhkhachcuataikhoan hktk ON hk.hanhkhach_id = hktk.hanhkhach_id " +
        "WHERE hktk.taikhoankhachhang_id = ?";

    List<HanhKhachDTO> list = new ArrayList<>();

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, taiKhoanId);

        try (ResultSet rs = ps.executeQuery()) {
    while (rs.next()) {
        list.add(map(rs));
    }
}

    } catch (SQLException e) {
        throw new RuntimeException("hanhkhach findByTaiKhoan failed", e);
    }

    return list;
}

    public int insert(HanhKhachDTO hk) {
        String sql = "INSERT INTO hanhkhach(hoten, ngaysinh, sogiayto, quoctich) "
                   + "VALUES (?,?,?,?) RETURNING hanhkhach_id";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            // họ tên
            ps.setString(1, hk.getHoTen());

            // ngày sinh
            if (hk.getNgaySinh() != null) {
                ps.setDate(2, Date.valueOf(hk.getNgaySinh()));
            } else {
                ps.setNull(2, Types.DATE);
            }

            // số giấy tờ
            if (hk.getSoGiayTo() != null && !hk.getSoGiayTo().trim().isEmpty()) {
                ps.setString(3, hk.getSoGiayTo().trim());
            } else {
                ps.setNull(3, Types.VARCHAR);
            }

            // quốc tịch
            if (hk.getQuocTich() != null && !hk.getQuocTich().trim().isEmpty()) {
                ps.setString(4, hk.getQuocTich().trim());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("hanhkhach_id");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("HanhKhach insert failed", e);
        }

        return -1;
    }

    public void update(HanhKhachDTO hk) {
    String sql = "UPDATE hanhkhach "
               + "SET hoten=?, ngaysinh=?, sogiayto=?, quoctich=? "
               + "WHERE hanhkhach_id=?";

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setString(1, hk.getHoTen());

        if (hk.getNgaySinh() != null)
            ps.setDate(2, Date.valueOf(hk.getNgaySinh()));
        else
            ps.setNull(2, Types.DATE);

        if (hk.getSoGiayTo() != null)
            ps.setString(3, hk.getSoGiayTo());
        else
            ps.setNull(3, Types.VARCHAR);

        if (hk.getQuocTich() != null)
            ps.setString(4, hk.getQuocTich());
        else
            ps.setNull(4, Types.VARCHAR);

        ps.setInt(5, hk.getHanhKhachId());

        ps.executeUpdate();

    } catch (SQLException e) {
        throw new RuntimeException("hanhkhach update failed", e);
    }
}

public void delete(int id) {
    String sql = "DELETE FROM hanhkhach WHERE hanhkhach_id=?";

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, id);
        ps.executeUpdate();

    } catch (SQLException e) {
        throw new RuntimeException("hanhkhach delete failed", e);
    }
} 

public List<HanhKhachDTO> findAll() {
    String sql = "SELECT * FROM hanhkhach ORDER BY hanhkhach_id";

    List<HanhKhachDTO> list = new ArrayList<>();

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
    list.add(map(rs));
}

    } catch (SQLException e) {
        throw new RuntimeException("hanhkhach findAll failed", e);
    }

    return list;
}
public HanhKhachDTO findById(int id) {
    String sql = "SELECT * FROM hanhkhach WHERE hanhkhach_id=?";

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, id);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return map(rs);
            }
        }

    } catch (SQLException e) {
        throw new RuntimeException("hanhkhach findById failed", e);
    }

    return null;
}
}