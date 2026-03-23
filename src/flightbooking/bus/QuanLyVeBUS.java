package flightbooking.bus;

import flightbooking.dao.VeDAO;
import flightbooking.dto.VeDTO;

import java.util.List;

public class QuanLyVeBUS {

    private final VeDAO veDAO = new VeDAO();

    public List<VeDTO> timKiem(
            Integer chuyenBayId,
            String hoTen,
            String soGiayTo,
            Integer trangThai
    ) {
        return veDAO.searchForQuanLyVe(chuyenBayId, hoTen, soGiayTo, trangThai);
    }

    public void huyVe(int veId) {
        if (veId <= 0) {
            throw new RuntimeException("ve_id không hợp lệ");
        }
        veDAO.huyVe(veId);
    }
}