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
public List<VeDTO> searchForQuanLyVe(
        Integer chuyenBayId,
        String hoTen,
        String soGiayTo,
        Integer trangThai
) {
    StringBuilder sql = new StringBuilder(
        "select v.ve_id, v.chuyenbay_id, v.ghe_id, v.hanhkhach_id, " +
        "       v.taikhoannhanvien_id, v.taikhoankhachhang_id, " +
        "       v.giachot, v.thuechot, v.trangthai, v.thoidiemtao, " +
        "       hk.hoten as ho_ten_hanh_khach, hk.sogiayto, " +
        "       g.tenghe, h.tenhangghe, " +
        "       kh.email as email_khachhang, " +
        "       nv.hoten as ten_nhan_vien, " +
        "       hd.ngaytao as ngay_tao_hoa_don, hd.tongtien as tong_tien_hoa_don " +
        "from ve v " +
        "left join hanhkhach hk on hk.hanhkhach_id = v.hanhkhach_id " +
        "left join ghe g on g.ghe_id = v.ghe_id " +
        "left join hangghemaybay h on h.hangghe_id = g.hangghe_id " +
        "left join taikhoankhachhang kh on kh.taikhoankhachhang_id = v.taikhoankhachhang_id " +
        "left join taikhoannhanvien tknv on tknv.taikhoannhanvien_id = v.taikhoannhanvien_id " +
        "left join nhanvien nv on nv.nhanvien_id = tknv.nhanvien_id " +
        "left join hoadonve hdv on hdv.ve_id = v.ve_id " +
        "left join hoadon hd on hd.hoadon_id = hdv.hoadon_id " +
        "where 1=1 "
    );

    List<Object> params = new ArrayList<>();

    if (chuyenBayId != null) {
        sql.append(" and v.chuyenbay_id = ? ");
        params.add(chuyenBayId);
    }

    if (hoTen != null && !hoTen.trim().isEmpty()) {
        sql.append(" and lower(hk.hoten) like ? ");
        params.add("%" + hoTen.trim().toLowerCase() + "%");
    }

    if (soGiayTo != null && !soGiayTo.trim().isEmpty()) {
        sql.append(" and lower(hk.sogiayto) like ? ");
        params.add("%" + soGiayTo.trim().toLowerCase() + "%");
    }

    if (trangThai != null && trangThai != -1) {
        sql.append(" and v.trangthai = ? ");
        params.add(trangThai);
    }

    sql.append(" order by v.ve_id desc ");

    List<VeDTO> list = new ArrayList<>();

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql.toString())) {

        for (int i = 0; i < params.size(); i++) {
            Object p = params.get(i);
            if (p instanceof Integer) {
                ps.setInt(i + 1, (Integer) p);
            } else {
                ps.setString(i + 1, String.valueOf(p));
            }
        }

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                VeDTO v = new VeDTO();

                v.setVeId(rs.getInt("ve_id"));
                v.setChuyenBayId((Integer) rs.getObject("chuyenbay_id"));
                v.setGheId((Integer) rs.getObject("ghe_id"));
                v.setHanhKhachId((Integer) rs.getObject("hanhkhach_id"));
                v.setTaiKhoanNhanVienId((Integer) rs.getObject("taikhoannhanvien_id"));
                v.setTaiKhoanKhachHangId((Integer) rs.getObject("taikhoankhachhang_id"));
                v.setGiaChot(rs.getBigDecimal("giachot"));
                v.setThueChot(rs.getBigDecimal("thuechot"));
                v.setTrangThai((Integer) rs.getObject("trangthai"));

                Timestamp ts1 = rs.getTimestamp("thoidiemtao");
                if (ts1 != null) v.setThoiDiemTao(ts1.toLocalDateTime());

                Timestamp ts2 = rs.getTimestamp("ngay_tao_hoa_don");
                if (ts2 != null) v.setNgayTaoHoaDon(ts2.toLocalDateTime());

                v.setTongTienHoaDon(rs.getBigDecimal("tong_tien_hoa_don"));
                v.setHoTenHanhKhach(rs.getString("ho_ten_hanh_khach"));
                v.setSoGiayTo(rs.getString("sogiayto"));
                v.setTenGhe(rs.getString("tenghe"));
                v.setTenHangGhe(rs.getString("tenhangghe"));
                v.setEmailKhachHang(rs.getString("email_khachhang"));
                v.setTenNhanVien(rs.getString("ten_nhan_vien"));

                list.add(v);
            }
        }

    } catch (SQLException e) {
        throw new RuntimeException("ve searchForQuanLyVe failed", e);
    }

    return list;
}

public void huyVe(int veId) {
    String sql = "update ve set trangthai = 0 where ve_id = ? and trangthai = 1";

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, veId);
        int n = ps.executeUpdate();

        if (n == 0) {
            throw new RuntimeException("Vé không tồn tại hoặc đã hủy.");
        }

    } catch (SQLException e) {
        throw new RuntimeException("ve huyVe failed", e);
    }
}

}
