package flightbooking.dao;

import flightbooking.dto.ChuyenBayDTO;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChuyenBayDAO extends BaseDAO {

    private ChuyenBayDTO map(ResultSet rs) throws SQLException {
        ChuyenBayDTO c = new ChuyenBayDTO();
        c.setChuyenBayId(rs.getInt("chuyenbay_id"));
        c.setTuyenBayId((Integer) rs.getObject("tuyenbay_id"));
        c.setHangHangKhongId((Integer) rs.getObject("hanghangkhong_id"));
        c.setMayBayId((Integer) rs.getObject("maybay_id"));

        Timestamp t1 = rs.getTimestamp("giokhoihanh");
        Timestamp t2 = rs.getTimestamp("gioden");
        c.setGioKhoiHanh(t1 != null ? t1.toLocalDateTime() : null);
        c.setGioDen(t2 != null ? t2.toLocalDateTime() : null);

        c.setTrangThai((Integer) rs.getObject("trangthai"));

        // enrich (nếu query có)
        try { c.setSanBayDiTen(rs.getString("sanbay_di_ten")); } catch (SQLException ignored) {}
        try { c.setSanBayDenTen(rs.getString("sanbay_den_ten")); } catch (SQLException ignored) {}
        try { c.setSoDam((Integer) rs.getObject("sodam")); } catch (SQLException ignored) {}

        return c;
    }

    public ChuyenBayDTO findById(int id) throws SQLException {
        String sql =
                "SELECT cb.chuyenbay_id, cb.tuyenbay_id, cb.hanghangkhong_id, cb.maybay_id, " +
                "cb.giokhoihanh, cb.gioden, cb.trangthai " +
                "FROM chuyenbay cb " +
                "WHERE cb.chuyenbay_id = ?";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public List<ChuyenBayDTO> findAllWithRouteName() throws SQLException {
        String sql =
                "SELECT cb.chuyenbay_id, cb.tuyenbay_id, cb.hanghangkhong_id, cb.maybay_id, " +
                "cb.giokhoihanh, cb.gioden, cb.trangthai, " +
                "tb.sodam AS sodam, " +
                "sbd.tensanbay AS sanbay_di_ten, " +
                "sbdn.tensanbay AS sanbay_den_ten " +
                "FROM chuyenbay cb " +
                "JOIN tuyenbay tb ON tb.tuyenbay_id = cb.tuyenbay_id " +
                "JOIN sanbay sbd ON sbd.sanbay_id = tb.sanbaydi_id " +
                "JOIN sanbay sbdn ON sbdn.sanbay_id = tb.sanbayden_id " +
                "ORDER BY cb.chuyenbay_id DESC";

        List<ChuyenBayDTO> list = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void insert(ChuyenBayDTO x) throws SQLException {
        String sql =
                "INSERT INTO chuyenbay(tuyenbay_id, hanghangkhong_id, maybay_id, giokhoihanh, gioden, trangthai) " +
                "VALUES(?,?,?,?,?,?)";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setObject(1, x.getTuyenBayId());
            ps.setObject(2, x.getHangHangKhongId());
            ps.setObject(3, x.getMayBayId());
            ps.setTimestamp(4, Timestamp.valueOf(x.getGioKhoiHanh()));
            ps.setTimestamp(5, Timestamp.valueOf(x.getGioDen()));
            ps.setObject(6, x.getTrangThai());

            ps.executeUpdate();
        }
    }

    public void update(ChuyenBayDTO x) throws SQLException {
        String sql =
                "UPDATE chuyenbay SET tuyenbay_id=?, hanghangkhong_id=?, maybay_id=?, giokhoihanh=?, gioden=?, trangthai=? " +
                "WHERE chuyenbay_id=?";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setObject(1, x.getTuyenBayId());
            ps.setObject(2, x.getHangHangKhongId());
            ps.setObject(3, x.getMayBayId());
            ps.setTimestamp(4, Timestamp.valueOf(x.getGioKhoiHanh()));
            ps.setTimestamp(5, Timestamp.valueOf(x.getGioDen()));
            ps.setObject(6, x.getTrangThai());
            ps.setInt(7, x.getChuyenBayId());

            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM chuyenbay WHERE chuyenbay_id=?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // USER search: theo sân bay đi/đến + ngày, chỉ lấy chuyến đang mở
    public List<ChuyenBayDTO> search(int sanBayDiId, int sanBayDenId, LocalDate ngay) throws SQLException {
    String sql =
            "SELECT cb.chuyenbay_id, cb.tuyenbay_id, cb.hanghangkhong_id, cb.maybay_id, " +
            "cb.giokhoihanh, cb.gioden, cb.trangthai, " +
            "tb.sodam AS sodam, " +
            "sbd.tensanbay AS sanbay_di_ten, " +
            "sbdn.tensanbay AS sanbay_den_ten " +
            "FROM chuyenbay cb " +
            "JOIN tuyenbay tb ON tb.tuyenbay_id = cb.tuyenbay_id " +
            "JOIN sanbay sbd ON sbd.sanbay_id = tb.sanbaydi_id " +
            "JOIN sanbay sbdn ON sbdn.sanbay_id = tb.sanbayden_id " +
            "WHERE tb.sanbaydi_id = ? " +
            "AND tb.sanbayden_id = ? " +
            "AND cb.trangthai = 1 " +
            "ORDER BY cb.giokhoihanh";

    List<ChuyenBayDTO> list = new ArrayList<>();
    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, sanBayDiId);
        ps.setInt(2, sanBayDenId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
    }
    return list;
}
    public List<ChuyenBayDTO> findByTuyenBayAndTimeRange(int tuyenBayId, LocalDateTime tu, LocalDateTime den) throws SQLException {
    String sql =
            "SELECT cb.chuyenbay_id, cb.tuyenbay_id, cb.hanghangkhong_id, cb.maybay_id, " +
            "cb.giokhoihanh, cb.gioden, cb.trangthai, " +
            "tb.sodam AS sodam, " +
            "sbd.ten_san_bay AS sanbay_di_ten, " +
            "sbdn.ten_san_bay AS sanbay_den_ten " +
            "FROM chuyenbay cb " +
            "JOIN tuyenbay tb ON tb.tuyenbay_id = cb.tuyenbay_id " +
            "JOIN sanbay sbd ON sbd.sanbay_id = tb.sanbay_di_id " +
            "JOIN sanbay sbdn ON sbdn.sanbay_id = tb.sanbay_den_id " +
            "WHERE cb.tuyenbay_id = ? " +
            "AND cb.trangthai = 1 " +
            "AND cb.giokhoihanh >= ? " +
            "AND cb.giokhoihanh <= ? " +
            "ORDER BY cb.giokhoihanh";

    List<ChuyenBayDTO> list = new ArrayList<>();
    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, tuyenBayId);
        ps.setTimestamp(2, Timestamp.valueOf(tu));
        ps.setTimestamp(3, Timestamp.valueOf(den));

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
    }
    return list;
}
public List<ChuyenBayDTO> searchByNgay(int sanBayDiId, int sanBayDenId, LocalDate ngay) throws SQLException {
    String sql =
            "SELECT cb.chuyenbay_id, cb.tuyenbay_id, cb.hanghangkhong_id, cb.maybay_id, " +
            "cb.giokhoihanh, cb.gioden, cb.trangthai, " +
            "tb.sodam AS sodam, " +
            "sbd.tensanbay AS sanbay_di_ten, " +
            "sbdn.tensanbay AS sanbay_den_ten " +
            "FROM chuyenbay cb " +
            "JOIN tuyenbay tb ON tb.tuyenbay_id = cb.tuyenbay_id " +
            "JOIN sanbay sbd ON sbd.sanbay_id = tb.sanbaydi_id " +
            "JOIN sanbay sbdn ON sbdn.sanbay_id = tb.sanbayden_id " +
            "WHERE tb.sanbaydi_id = ? " +
            "AND tb.sanbayden_id = ? " +
            "AND cb.trangthai = 1 " +
            "AND DATE(cb.giokhoihanh) = ? " + // ✅ filter ngày
            "ORDER BY cb.giokhoihanh";

    List<ChuyenBayDTO> list = new ArrayList<>();
    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, sanBayDiId);
        ps.setInt(2, sanBayDenId);
        ps.setDate(3, Date.valueOf(ngay)); // ✅ truyền ngày

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
    }
    return list;
}
}
