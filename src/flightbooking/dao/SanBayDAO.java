package flightbooking.dao;

import flightbooking.dto.SanBayDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SanBayDAO extends BaseDAO {

    public List<SanBayDTO> findAll() {
        String sql = "select * from sanbay order by sanbay_id asc";
        List<SanBayDTO> list = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SanBayDTO s = new SanBayDTO();
                s.setSanBayId(rs.getInt("sanbay_id"));
                s.setTenSanBay(rs.getString("tensanbay"));
                s.setThanhPho(rs.getString("thanhpho"));
                s.setQuocGia(rs.getString("quocgia"));
                list.add(s);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("sanbay findAll failed", e);
        }
    }

    public int insert(SanBayDTO s) {
        String sql = "insert into sanbay(tensanbay, thanhpho, quocgia) values (?,?,?) returning sanbay_id";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, s.getTenSanBay());
            ps.setString(2, s.getThanhPho());
            ps.setString(3, s.getQuocGia());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("sanbay insert failed", e);
        }
    }

    public void update(SanBayDTO s) {
        String sql = "update sanbay set tensanbay=?, thanhpho=?, quocgia=? where sanbay_id=?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, s.getTenSanBay());
            ps.setString(2, s.getThanhPho());
            ps.setString(3, s.getQuocGia());
            ps.setInt(4, s.getSanBayId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("sanbay update failed", e);
        }
    }

    public void delete(int id) {
        String sql = "delete from sanbay where sanbay_id=?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("sanbay delete failed", e);
        }
    }
}
