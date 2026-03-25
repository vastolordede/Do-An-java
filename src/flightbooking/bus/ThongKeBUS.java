package flightbooking.bus;

import flightbooking.dao.ThongKeDAO;
import flightbooking.dto.ThongKeChuyenBayDTO;
import flightbooking.dto.ThongKeHangGheDTO;
import flightbooking.dto.ThongKeTongQuanDTO;

import java.time.LocalDate;
import java.util.List;

public class ThongKeBUS {
    private final ThongKeDAO dao = new ThongKeDAO();

    public ThongKeTongQuanDTO getTongQuan(LocalDate fromDate, LocalDate toDate, Integer tuyenBayId, Integer chuyenBayId) {
        return dao.getTongQuan(fromDate, toDate, tuyenBayId, chuyenBayId);
    }

    public List<ThongKeChuyenBayDTO> getDoanhThuTheoChuyenBay(LocalDate fromDate, LocalDate toDate, Integer tuyenBayId) {
        return dao.getDoanhThuTheoChuyenBay(fromDate, toDate, tuyenBayId);
    }

    public List<ThongKeHangGheDTO> getDoanhThuTheoHangGhe(LocalDate fromDate, LocalDate toDate, Integer tuyenBayId, Integer chuyenBayId) {
        return dao.getDoanhThuTheoHangGhe(fromDate, toDate, tuyenBayId, chuyenBayId);
    }
}