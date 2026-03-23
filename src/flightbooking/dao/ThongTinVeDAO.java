package flightbooking.dao;

import flightbooking.dto.ThongTinVeDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ThongTinVeDAO extends BaseDAO {

    public ThongTinVeDTO getFullInfo(int chuyenBayId, int gheId) {

        String sql =
    "SELECT cb.chuyenbay_id, cb.giokhoihanh, cb.gioden, " +
    "sbd.tensanbay AS sanbay_di, sbdn.tensanbay AS sanbay_den, " +
    "mb.tenmaybay, mb.kieumaybay, " +
    "g.tenghe, hgm.tenhangghe " +
    "FROM chuyenbay cb " +
    "JOIN tuyenbay tb ON tb.tuyenbay_id = cb.tuyenbay_id " +
    "JOIN sanbay sbd ON sbd.sanbay_id = tb.sanbaydi_id " +
    "JOIN sanbay sbdn ON sbdn.sanbay_id = tb.sanbayden_id " +
    "JOIN maybay mb ON mb.maybay_id = cb.maybay_id " +
    "JOIN ghe g ON g.ghe_id = ? " +
    "LEFT JOIN hangghemaybay hgm ON hgm.hangghe_id = g.hangghe_id " +
    "WHERE cb.chuyenbay_id = ?";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, gheId);
            ps.setInt(2, chuyenBayId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) return null;

                ThongTinVeDTO t = new ThongTinVeDTO();

                t.setChuyenBayId(rs.getInt("chuyenbay_id"));
                t.setSanBayDi(rs.getString("sanbay_di"));
                t.setSanBayDen(rs.getString("sanbay_den"));
                t.setGioKhoiHanh(rs.getTimestamp("giokhoihanh").toLocalDateTime());
                t.setGioDen(rs.getTimestamp("gioden").toLocalDateTime());

                t.setTenMayBay(rs.getString("tenmaybay"));
                t.setKieuMayBay(rs.getString("kieumaybay"));

                t.setTenGhe(rs.getString("tenghe"));
                t.setHangGhe(rs.getString("tenhangghe"));

                return t;
            }

        } catch (SQLException e) {
            throw new RuntimeException("getFullInfo failed", e);
        }
    }
    public List<ThongTinVeDTO> getSimpleByKhachHang(int khachHangId) {

    List<ThongTinVeDTO> list = new ArrayList<>();

    String sql =
    "SELECT v.chuyenbay_id, v.ghe_id, " +
    "hk.hoten, hk.sogiayto, " +
    "sbd.tensanbay AS sanbay_di, sbdn.tensanbay AS sanbay_den, " +
    "cb.giokhoihanh, " +
    "g.tenghe, hg.tenhangghe, v.giachot " +   // 🔥 THÊM DÒNG NÀY
    "FROM ve v " +
    "JOIN hanhkhach hk ON v.hanhkhach_id = hk.hanhkhach_id " +
    "JOIN chuyenbay cb ON v.chuyenbay_id = cb.chuyenbay_id " +
    "JOIN tuyenbay tb ON cb.tuyenbay_id = tb.tuyenbay_id " +
    "JOIN sanbay sbd ON tb.sanbaydi_id = sbd.sanbay_id " +
    "JOIN sanbay sbdn ON tb.sanbayden_id = sbdn.sanbay_id " +
    "JOIN ghe g ON v.ghe_id = g.ghe_id " +
    "LEFT JOIN hangghemaybay hg ON g.hangghe_id = hg.hangghe_id " +
    "WHERE v.taikhoankhachhang_id = ? " +
    "ORDER BY cb.giokhoihanh DESC";

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, khachHangId);

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

    ThongTinVeDTO t = new ThongTinVeDTO();

    t.setChuyenBayId(rs.getInt("chuyenbay_id"));
    t.setGheId(rs.getInt("ghe_id"));

    t.setHoTen(rs.getString("hoten"));
    t.setSoGiayTo(rs.getString("sogiayto"));

    t.setSanBayDi(rs.getString("sanbay_di"));
    t.setSanBayDen(rs.getString("sanbay_den"));

    t.setGioKhoiHanh(
        rs.getTimestamp("giokhoihanh").toLocalDateTime()
    );

    t.setTenGhe(rs.getString("tenghe"));
    t.setHangGhe(rs.getString("tenhangghe"));

    t.setGia(rs.getBigDecimal("giachot")); // 🔥 QUAN TRỌNG

    list.add(t);
}
        }

    } catch (Exception e) {
        throw new RuntimeException("getSimpleByKhachHang failed", e);
    }

    return list;
}
public List<ThongTinVeDTO> getByNhanVien(int taiKhoanNhanVienId) {

    List<ThongTinVeDTO> list = new ArrayList<>();

    String sql =
    "SELECT v.chuyenbay_id, v.ghe_id, " +
    "hk.hoten, hk.sogiayto, " +
    "sbd.tensanbay AS sanbay_di, sbdn.tensanbay AS sanbay_den, " +
    "cb.giokhoihanh, " +
    "g.tenghe, hg.tenhangghe, v.giachot " +
    "FROM ve v " +
    "JOIN hanhkhach hk ON v.hanhkhach_id = hk.hanhkhach_id " +
    "JOIN chuyenbay cb ON v.chuyenbay_id = cb.chuyenbay_id " +
    "JOIN tuyenbay tb ON cb.tuyenbay_id = tb.tuyenbay_id " +
    "JOIN sanbay sbd ON tb.sanbaydi_id = sbd.sanbay_id " +
    "JOIN sanbay sbdn ON tb.sanbayden_id = sbdn.sanbay_id " +
    "JOIN ghe g ON v.ghe_id = g.ghe_id " +
    "LEFT JOIN hangghemaybay hg ON g.hangghe_id = hg.hangghe_id " +
    "WHERE v.taikhoannhanvien_id = ? " +
    "ORDER BY cb.giokhoihanh DESC";

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, taiKhoanNhanVienId);

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                ThongTinVeDTO t = new ThongTinVeDTO();

                t.setChuyenBayId(rs.getInt("chuyenbay_id"));
                t.setGheId(rs.getInt("ghe_id"));

                t.setHoTen(rs.getString("hoten"));
                t.setSoGiayTo(rs.getString("sogiayto"));

                t.setSanBayDi(rs.getString("sanbay_di"));
                t.setSanBayDen(rs.getString("sanbay_den"));

                t.setGioKhoiHanh(
                    rs.getTimestamp("giokhoihanh").toLocalDateTime()
                );

                t.setTenGhe(rs.getString("tenghe"));
                t.setHangGhe(rs.getString("tenhangghe"));

                t.setGia(rs.getBigDecimal("giachot"));

                list.add(t);
            }
        }

    } catch (Exception e) {
        throw new RuntimeException("getByNhanVien failed", e);
    }

    return list;
}
}