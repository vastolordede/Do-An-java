package flightbooking.dao;

import flightbooking.dto.NhomQuyenDTO;
import flightbooking.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhomQuyenDAO {

    public boolean hasPermission(int nhomQuyenId, int quyenId) {

        String sql = "SELECT 1 FROM nhomquyen_quyen WHERE nhomquyen_id = ? AND quyen_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, nhomQuyenId);
            ps.setInt(2, quyenId);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public int insertNhomQuyen(String ten) {

        String sql = "INSERT INTO nhomquyen (tennhomquyen) VALUES (?) RETURNING nhomquyen_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ten);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void insertQuyen(int nhomId, List<Integer> quyenIds) {

        String sql = "INSERT INTO nhomquyen_quyen (nhomquyen_id, quyen_id) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int qid : quyenIds) {
                ps.setInt(1, nhomId);
                ps.setInt(2, qid);
                ps.addBatch();
            }

            ps.executeBatch();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<Integer> getPermissionsByNhom(int nhomId) {

    List<Integer> list = new ArrayList<>();

    String sql = "SELECT quyen_id FROM nhomquyen_quyen WHERE nhomquyen_id = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, nhomId);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(rs.getInt(1));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}
public void updateNhomQuyen(int id, String ten) {
    String sql = "UPDATE nhomquyen SET tennhomquyen=? WHERE nhomquyen_id=?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, ten);
        ps.setInt(2, id);
        ps.executeUpdate();

    } catch (Exception e) {
        e.printStackTrace();
    }
}

public void deleteNhomQuyen(int id) {
    String sql = "DELETE FROM nhomquyen WHERE nhomquyen_id=?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, id);
        ps.executeUpdate();

    } catch (Exception e) {
        e.printStackTrace();
    }
}

public void deleteAllQuyen(int nhomId) {
    String sql = "DELETE FROM nhomquyen_quyen WHERE nhomquyen_id=?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, nhomId);
        ps.executeUpdate();

    } catch (Exception e) {
        e.printStackTrace();
    }
}

// 🔥 load danh sách nhóm
public List<NhomQuyenDTO> findAll() {

    List<NhomQuyenDTO> list = new ArrayList<>();

    String sql = "SELECT * FROM nhomquyen ORDER BY nhomquyen_id";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            NhomQuyenDTO n = new NhomQuyenDTO();
            n.setNhomQuyenId(rs.getInt("nhomquyen_id"));
            n.setTenNhomQuyen(rs.getString("tennhomquyen"));
            list.add(n);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}
    public List<Integer> getActionByNhom(int nhomId) {

    List<Integer> list = new ArrayList<>();

    String sql = "SELECT action_id FROM quyen_action_map WHERE quyen_id=?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, nhomId);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(rs.getInt("action_id"));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}
public void saveActionForNhom(int nhomId, List<Integer> actionIds) {

    String deleteSql = "DELETE FROM quyen_action_map WHERE quyen_id=?";
    String insertSql = "INSERT INTO quyen_action_map(action_id, quyen_id) VALUES(?, ?)";

    try (Connection conn = DBConnection.getConnection()) {

        // Xóa hết action cũ
        PreparedStatement psDel = conn.prepareStatement(deleteSql);
        psDel.setInt(1, nhomId);
        psDel.executeUpdate();

        // Thêm lại action mới
        PreparedStatement psIns = conn.prepareStatement(insertSql);

        for (int aid : actionIds) {
            psIns.setInt(1, aid);
            psIns.setInt(2, nhomId);
            psIns.executeUpdate();
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
}