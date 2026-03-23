package flightbooking.dao;

import flightbooking.dto.GiaHangChuyenBayDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GiaHangChuyenBayDAO extends BaseDAO {

    private GiaHangChuyenBayDTO map(ResultSet rs) throws SQLException {

        GiaHangChuyenBayDTO d = new GiaHangChuyenBayDTO();

        d.setGiaHChuyenBayId(rs.getInt("giahchuyenbay_id"));

        int v = rs.getInt("chuyenbay_id");
        d.setChuyenBayId(rs.wasNull() ? null : v);

        v = rs.getInt("hangghe_id");
        d.setHangGheId(rs.wasNull() ? null : v);

        d.setGiaCoBan(rs.getBigDecimal("giacoban"));
        d.setThuePhi(rs.getBigDecimal("thuephi"));

        Timestamp t = rs.getTimestamp("capnhatluc");
        if (t != null) d.setCapNhatLuc(t.toLocalDateTime());

        return d;
    }

    public GiaHangChuyenBayDTO findByChuyenBayAndHangGhe(int chuyenBayId, int hangGheId) {

        String sql = "select * from giahchuyenbay where chuyenbay_id=? and hangghe_id=? limit 1";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, chuyenBayId);
            ps.setInt(2, hangGheId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("giahchuyenbay findByChuyenBayAndHangGhe failed", e);
        }
    }

    // lấy tất cả giá theo hạng của 1 chuyến bay
    public List<GiaHangChuyenBayDTO> findByChuyenBay(int chuyenBayId) {

        String sql = "select * from giahchuyenbay where chuyenbay_id=?";

        List<GiaHangChuyenBayDTO> list = new ArrayList<>();

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, chuyenBayId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    list.add(map(rs));
                }
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException("giahchuyenbay findByChuyenBay failed", e);
        }
    }

    // insert giá base
    public void insert(GiaHangChuyenBayDTO d) {

    String sql = "insert into giahchuyenbay(chuyenbay_id, hangghe_id, giacoban, thuephi) values (?,?,?,?)";

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, d.getChuyenBayId());
        ps.setInt(2, d.getHangGheId());
        ps.setBigDecimal(3, d.getGiaCoBan());
        ps.setBigDecimal(4, d.getThuePhi());

        ps.executeUpdate();

    } catch (SQLException e) {
        throw new RuntimeException("giahchuyenbay insert failed", e);
    }
}
    // update giá base
    public void update(GiaHangChuyenBayDTO d) {

    String sql = "update giahchuyenbay set giacoban=?, thuephi=? where chuyenbay_id=? and hangghe_id=?";

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setBigDecimal(1, d.getGiaCoBan());
        ps.setBigDecimal(2, d.getThuePhi());
        ps.setInt(3, d.getChuyenBayId());
        ps.setInt(4, d.getHangGheId());

        ps.executeUpdate();

    } catch (SQLException e) {
        throw new RuntimeException("giahchuyenbay update failed", e);
    }
}

    // delete
    public void delete(int chuyenBayId, int hangGheId) {

    String sql = "delete from giahchuyenbay where chuyenbay_id=? and hangghe_id=?";

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, chuyenBayId);
        ps.setInt(2, hangGheId);

        ps.executeUpdate();

    } catch (SQLException e) {
        throw new RuntimeException("giahchuyenbay delete failed", e);
    }
}
}