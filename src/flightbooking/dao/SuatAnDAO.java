package flightbooking.dao;

import flightbooking.dto.SuatAnDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SuatAnDAO extends BaseDAO {

    public List<SuatAnDTO> findAll() {
        String sql = "select * from suatan order by suatan_id asc";
        List<SuatAnDTO> list = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SuatAnDTO s = new SuatAnDTO();
                s.setSuatAnId(rs.getInt("suatan_id"));
                s.setTenSuatAn(rs.getString("tensuatan"));
                s.setGia(rs.getBigDecimal("gia"));
                list.add(s);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("suatan findAll failed", e);
        }
    }
}
