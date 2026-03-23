package flightbooking.dao;

import flightbooking.dto.HoaDonDTO;

import java.math.BigDecimal;
import java.sql.*;

public class HoaDonDAO extends BaseDAO {

    public int insert(HoaDonDTO hd) {
        String sql = "insert into hoadon(taikhoankhachhang_id, taikhoannhanvien_id, tongtien, trangthai) values (?,?,?,?) returning hoadon_id";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            if (hd.getTaiKhoanKhachHangId() != null)
    ps.setInt(1, hd.getTaiKhoanKhachHangId());
else
    ps.setNull(1, Types.INTEGER);

if (hd.getTaiKhoanNhanVienId() != null)
    ps.setInt(2, hd.getTaiKhoanNhanVienId());
else
    ps.setNull(2, Types.INTEGER);

ps.setBigDecimal(3,
        hd.getTongTien() != null ? hd.getTongTien() : BigDecimal.ZERO);

if (hd.getTrangThai() != null)
    ps.setInt(4, hd.getTrangThai());
else
    ps.setNull(4, Types.SMALLINT);


            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
    throw new RuntimeException("hoadon insert failed: " + e.getMessage() 
        + " | SQLState=" + e.getSQLState() 
        + " | ErrorCode=" + e.getErrorCode(), e);
}
    }

    public void updateTongTien(int hoaDonId, BigDecimal tongTien) {
        String sql = "update hoadon set tongtien=? where hoadon_id=?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, tongTien != null ? tongTien : BigDecimal.ZERO);
            ps.setInt(2, hoaDonId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("hoadon updateTongTien failed", e);
        }
    }
}
