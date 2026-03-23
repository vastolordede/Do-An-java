package flightbooking.dao;

import flightbooking.dto.MayBayDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MayBayDAO extends BaseDAO {

    private MayBayDTO map(ResultSet rs) throws SQLException {
        MayBayDTO m = new MayBayDTO();
        m.setMayBayId(rs.getInt("maybay_id"));
        m.setTenMayBay(rs.getString("tenmaybay"));
        m.setKieuMayBay(rs.getString("kieumaybay"));

        int v = rs.getInt("tongsoghe");
        m.setTongSoGhe(rs.wasNull() ? null : v);

        v = rs.getInt("so_tang");
        m.setSoTang(rs.wasNull() ? null : v);

        return m;
    }

    public List<MayBayDTO> findAll() {
        String sql = "select * from maybay order by maybay_id asc";
        List<MayBayDTO> list = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("maybay findAll failed", e);
        }
    }

    public MayBayDTO findById(int id) {
        String sql = "select * from maybay where maybay_id=?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("maybay findById failed", e);
        }
    }

    public int insert(MayBayDTO m) {
        String sql = "insert into maybay(tenmaybay, kieumaybay, tongsoghe, so_tang) values (?,?,?,?) returning maybay_id";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, m.getTenMayBay());
            ps.setString(2, m.getKieuMayBay());

            if (m.getTongSoGhe() != null) ps.setInt(3, m.getTongSoGhe());
            else ps.setNull(3, Types.INTEGER);

            if (m.getSoTang() != null) ps.setInt(4, m.getSoTang());
            else ps.setInt(4, 1);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("maybay insert failed", e);
        }
    }

    public void update(MayBayDTO m) {
        String sql = "update maybay set tenmaybay=?, kieumaybay=?, tongsoghe=?, so_tang=? where maybay_id=?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, m.getTenMayBay());
            ps.setString(2, m.getKieuMayBay());

            if (m.getTongSoGhe() != null) ps.setInt(3, m.getTongSoGhe());
            else ps.setNull(3, Types.INTEGER);

            if (m.getSoTang() != null) ps.setInt(4, m.getSoTang());
            else ps.setInt(4, 1);

            ps.setInt(5, m.getMayBayId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("maybay update failed", e);
        }
    }

    public void delete(int id) {
        String sql = "delete from maybay where maybay_id=?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("maybay delete failed", e);
        }
    }
}
