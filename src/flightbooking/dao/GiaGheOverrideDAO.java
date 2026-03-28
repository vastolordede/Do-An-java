package flightbooking.dao;

import flightbooking.dto.GiaGheOverrideDTO;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GiaGheOverrideDAO extends BaseDAO {

    public GiaGheOverrideDTO findByChuyenBayAndGhe(int chuyenBayId, int gheId) {
        String sql = "select * from giagheoverride where chuyenbay_id=? and ghe_id=? limit 1";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, chuyenBayId);
            ps.setInt(2, gheId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                GiaGheOverrideDTO d = new GiaGheOverrideDTO();

d.setGiaGheOverrideId(rs.getInt("giagheoverride_id"));

int v = rs.getInt("chuyenbay_id");
d.setChuyenBayId(rs.wasNull() ? null : v);

v = rs.getInt("ghe_id");
d.setGheId(rs.wasNull() ? null : v);

d.setGiaOverride(rs.getBigDecimal("giaoverride"));
d.setThuePhiOverride(rs.getBigDecimal("thuephioverride"));
d.setLyDo(rs.getString("lydo"));

Timestamp t = rs.getTimestamp("capnhatluc");
if (t != null) d.setCapNhatLuc(t.toLocalDateTime());

                return d;
            }
        } catch (SQLException e) {
            throw new RuntimeException("giagheoverride findByChuyenBayAndGhe failed", e);
        }
    }
    public BigDecimal findPrice(int chuyenBayId, int gheId){

    String sql = "select giaoverride from giagheoverride where chuyenbay_id=? and ghe_id=? limit 1";

    try(Connection c = getConnection();
        PreparedStatement ps = c.prepareStatement(sql)){

        ps.setInt(1,chuyenBayId);
        ps.setInt(2,gheId);

        try(ResultSet rs = ps.executeQuery()){

            if(rs.next())
                return rs.getBigDecimal("giaoverride");

        }

    }catch(Exception e){
        throw new RuntimeException(e);
    }

    return null;
}
public void insert(int chuyenBayId,int gheId,BigDecimal price){

    String sql = "INSERT INTO giagheoverride(chuyenbay_id, ghe_id, giaoverride) VALUES (?, ?, ?) "
               + "ON CONFLICT (chuyenbay_id, ghe_id) DO UPDATE SET giaoverride = EXCLUDED.giaoverride";

    try(Connection c = getConnection();
        PreparedStatement ps = c.prepareStatement(sql)){

        ps.setInt(1,chuyenBayId);
        ps.setInt(2,gheId);
        ps.setBigDecimal(3,price);

        ps.executeUpdate();

    }catch(Exception e){
        throw new RuntimeException(e);
    }
}

public List<GiaGheOverrideDTO> findByChuyenBay(int chuyenBayId) {
    String sql = "SELECT * FROM giagheoverride WHERE chuyenbay_id = ?";
    List<GiaGheOverrideDTO> result = new ArrayList<>();

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setInt(1, chuyenBayId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                GiaGheOverrideDTO d = new GiaGheOverrideDTO();
                d.setGheId(rs.getInt("ghe_id"));
                d.setGiaOverride(rs.getBigDecimal("giaoverride"));
                result.add(d);
            }
        }
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
    return result;
}
}
