package flightbooking.dao;

import flightbooking.dto.TuyenBayDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TuyenBayDAO extends BaseDAO {

    private TuyenBayDTO map(ResultSet rs) throws SQLException {
        TuyenBayDTO t = new TuyenBayDTO();
        t.setTuyenBayId(rs.getInt("tuyenbay_id"));

        int v = rs.getInt("sanbaydi_id");
        t.setSanBayDiId(rs.wasNull() ? null : v);

        v = rs.getInt("sanbayden_id");
        t.setSanBayDenId(rs.wasNull() ? null : v);

        v = rs.getInt("sodam"); // đúng tên cột của bạn
        t.setSoDam(rs.wasNull() ? 0 : v);

        return t;
    }

    public List<TuyenBayDTO> findAll() {
        String sql = "select * from tuyenbay order by tuyenbay_id asc";
        List<TuyenBayDTO> list = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("tuyenbay findAll failed", e);
        }
    }

    public TuyenBayDTO findById(int id) {
        String sql = "select * from tuyenbay where tuyenbay_id=?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("tuyenbay findById failed", e);
        }
    }

    public int insert(TuyenBayDTO t) {
        String sql = "insert into tuyenbay(sanbaydi_id, sanbayden_id, sodam) values (?,?,?) returning tuyenbay_id";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, t.getSanBayDiId());
            ps.setInt(2, t.getSanBayDenId());
            ps.setInt(3, t.getSoDam());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("tuyenbay insert failed", e);
        }
    }

    public void update(TuyenBayDTO t) {
        String sql = "update tuyenbay set sanbaydi_id=?, sanbayden_id=?, sodam=? where tuyenbay_id=?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, t.getSanBayDiId());
            ps.setInt(2, t.getSanBayDenId());
            ps.setInt(3, t.getSoDam());
            ps.setInt(4, t.getTuyenBayId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("tuyenbay update failed", e);
        }
    }

    public void delete(int id) {
        String sql = "delete from tuyenbay where tuyenbay_id=?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("tuyenbay delete failed", e);
        }
    }

    // dùng cho DatVeBUS (bạn đã có)
    public Integer findTuyenBayId(int sanBayDiId, int sanBayDenId) {
        String sql = "select tuyenbay_id from tuyenbay where sanbaydi_id=? and sanbayden_id=? limit 1";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, sanBayDiId);
            ps.setInt(2, sanBayDenId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("tuyenbay_id");
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("tuyenbay findTuyenBayId failed", e);
        }
    }

    // check trùng tuyến theo (đi, đến)
   public boolean existsByDiDen(int sanBayDiId, int sanBayDenId, Integer excludeId) {
    String sql =
            "select 1 " +
            "from tuyenbay " +
            "where sanbaydi_id=? and sanbayden_id=? " +
            "  and (? is null or tuyenbay_id <> ?) " +
            "limit 1";

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, sanBayDiId);
        ps.setInt(2, sanBayDenId);

        if (excludeId == null) ps.setNull(3, Types.INTEGER);
        else ps.setInt(3, excludeId);

        if (excludeId == null) ps.setNull(4, Types.INTEGER);
        else ps.setInt(4, excludeId);

        try (ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    } catch (SQLException e) {
        throw new RuntimeException("tuyenbay existsByDiDen failed", e);
    }
}

}
