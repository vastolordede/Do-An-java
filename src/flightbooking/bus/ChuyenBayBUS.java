package flightbooking.bus;

import flightbooking.dao.ChuyenBayDAO;
import flightbooking.dto.ChuyenBayDTO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ChuyenBayBUS {

    private final ChuyenBayDAO dao = new ChuyenBayDAO();

    public List<ChuyenBayDTO> dsChuyenBay() {
        try {
            return dao.findAllWithRouteName(); // data thật
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi dsChuyenBay: " + e.getMessage(), e);
        }
    }

    public void themChuyenBay(ChuyenBayDTO x) {
        try {
            dao.insert(x); // data thật
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi themChuyenBay: " + e.getMessage(), e);
        }
    }

    public void capNhatChuyenBay(ChuyenBayDTO x) {
        try {
            dao.update(x); // data thật
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi capNhatChuyenBay: " + e.getMessage(), e);
        }
    }

    public void xoaChuyenBay(int id) {
        try {
            dao.delete(id); // data thật
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xoaChuyenBay: " + e.getMessage(), e);
        }
    }

    // USER: search theo sân bay đi/đến + ngày
    public List<ChuyenBayDTO> timChuyen(int sanBayDiId, int sanBayDenId, LocalDate ngay) {
        try {
            return dao.search(sanBayDiId, sanBayDenId, ngay); // data thật
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi timChuyen: " + e.getMessage(), e);
        }
    }

    public ChuyenBayDTO findById(int id) {
        try {
            return dao.findById(id); // ✅ hàm bạn đang thiếu
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi findById: " + e.getMessage(), e);
        }
    }
    public List<ChuyenBayDTO> timChuyenTheoNgay(int sanBayDiId, int sanBayDenId, LocalDate ngay) {
    try {
        return dao.searchByNgay(sanBayDiId, sanBayDenId, ngay);
    } catch (SQLException e) {
        throw new RuntimeException("Lỗi timChuyenTheoNgay: " + e.getMessage(), e);
    }
}
}
