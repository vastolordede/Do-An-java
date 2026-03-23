package flightbooking.dao;

import flightbooking.dto.QuyenActionDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuyenActionDAO extends BaseDAO {

    public List<QuyenActionDTO> findAll() {

        List<QuyenActionDTO> list = new ArrayList<>();

        String sql = "SELECT * FROM quyen_action";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                QuyenActionDTO a = new QuyenActionDTO();
                a.setId(rs.getInt("id"));
                a.setTenquyen(rs.getString("tenquyen"));
                list.add(a);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


}