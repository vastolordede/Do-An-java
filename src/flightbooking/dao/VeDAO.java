package flightbooking.dao;

import flightbooking.dto.VeDTO;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VeDAO extends BaseDAO {

    public List<Integer> findGheIdDaDat(int chuyenBayId) {
        String sql = "select ghe_id from ve where chuyenbay_id=? and trangthai=1";
        List<Integer> list = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, chuyenBayId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(rs.getInt("ghe_id"));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("ve findGheIdDaDat failed", e);
        }
    }

    public int insert(VeDTO v) {
    String sql =
        "insert into ve(chuyenbay_id, ghe_id, hanhkhach_id, " +
        "taikhoannhanvien_id, taikhoankhachhang_id, giachot, thuechot, trangthai) " +
        "values (?, ?, ?, ?, ?, ?, ?, ?) RETURNING ve_id";

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        // 1. chuyenbay_id
        if (v.getChuyenBayId() != null)
            ps.setInt(1, v.getChuyenBayId());
        else
            ps.setNull(1, Types.INTEGER);

        // 2. ghe_id
        if (v.getGheId() != null)
            ps.setInt(2, v.getGheId());
        else
            ps.setNull(2, Types.INTEGER);

        // 3. hanhkhach_id
        if (v.getHanhKhachId() != null)
            ps.setInt(3, v.getHanhKhachId());
        else
            ps.setNull(3, Types.INTEGER);

        // 4. taikhoannhanvien_id
        if (v.getTaiKhoanNhanVienId() != null)
            ps.setInt(4, v.getTaiKhoanNhanVienId());
        else
            ps.setNull(4, Types.INTEGER);

        // 🔥 5. taikhoankhachhang_id (THIẾU TRƯỚC ĐÓ)
        if (v.getTaiKhoanKhachHangId() != null)
            ps.setInt(5, v.getTaiKhoanKhachHangId());
        else
            ps.setNull(5, Types.INTEGER);

        // 6. giachot
        ps.setBigDecimal(6,
                v.getGiaChot() != null ? v.getGiaChot() : BigDecimal.ZERO);

        // 7. thuechot
        ps.setBigDecimal(7,
                v.getThueChot() != null ? v.getThueChot() : BigDecimal.ZERO);

        // 8. trangthai
        ps.setInt(8,
                v.getTrangThai() != null ? v.getTrangThai() : 1);

        try (ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt("ve_id");
        }

    } catch (SQLException e) {
        throw new RuntimeException("ve insert failed", e);
    }
}
    public List<VeDTO> findWithNhanVien(int chuyenBayId) {
    String sql = 
    "select v.ve_id, v.chuyenbay_id, v.ghe_id, v.hanhkhach_id, " +
    "v.giachot, v.thuechot, v.trangthai, " +
    "nv.hoten as ten_nhan_vien, " +
    "kh.email as email_khachhang " +
    "from ve v " +
    "left join hoadonve hdv on hdv.ve_id = v.ve_id " +
    "left join hoadon hd on hd.hoadon_id = hdv.hoadon_id " +
    "left join taikhoannhanvien tknv on tknv.taikhoannhanvien_id = hd.taikhoannhanvien_id " +
    "left join nhanvien nv on nv.nhanvien_id = tknv.nhanvien_id " +
    "left join taikhoankhachhang kh on kh.taikhoankhachhang_id = v.taikhoankhachhang_id " +
    "where v.chuyenbay_id = ? " +
    "order by v.ve_id";

    List<VeDTO> list = new ArrayList<>();

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, chuyenBayId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                VeDTO v = new VeDTO();

                v.setVeId(rs.getInt("ve_id"));
                v.setChuyenBayId(rs.getInt("chuyenbay_id"));
                v.setGheId(rs.getInt("ghe_id"));
                v.setHanhKhachId(rs.getInt("hanhkhach_id"));
                v.setGiaChot(rs.getBigDecimal("giachot"));
                v.setThueChot(rs.getBigDecimal("thuechot"));
                v.setTrangThai(rs.getInt("trangthai"));

                // 👉 thêm field hiển thị (optional)
                try {
                    v.setTenNhanVien(rs.getString("ten_nhan_vien"));
                } catch (Exception ignored) {}

                list.add(v);
            }
        }

    } catch (SQLException e) {
        throw new RuntimeException("ve findWithNhanVien failed", e);
    }

    return list;
}
}
