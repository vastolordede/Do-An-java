package flightbooking.dao;

import flightbooking.dto.QuyenDTO;
import flightbooking.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuyenDAO {

    public List<QuyenDTO> findAll() {

        List<QuyenDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM quyen WHERE danghoatdong = true ORDER BY quyen_id";
        

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                QuyenDTO q = new QuyenDTO();
                q.setQuyenId(rs.getInt("quyen_id"));
                q.setTenQuyen(rs.getString("tenquyen"));
                list.add(q);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}