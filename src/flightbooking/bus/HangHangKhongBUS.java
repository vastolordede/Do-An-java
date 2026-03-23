package flightbooking.bus;

import flightbooking.dao.HangHangKhongDAO;
import flightbooking.dto.HangHangKhongDTO;
import java.util.List;

public class HangHangKhongBUS {
    private final HangHangKhongDAO hhkDAO = new HangHangKhongDAO();

    public List<HangHangKhongDTO> getDsHHK() {
        return hhkDAO.findAll();
    }

    public void themHHK(HangHangKhongDTO h) {
        hhkDAO.insert(h);
    }

    public void capNhatHHK(HangHangKhongDTO h) {
        hhkDAO.update(h);
    }

    public void xoaHHK(int id) {
        hhkDAO.delete(id);
    }
}