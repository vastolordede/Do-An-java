package flightbooking.bus;

import java.util.List;

import flightbooking.dao.ThongTinVeDAO;
import flightbooking.dto.ThongTinVeDTO;

public class ThongTinVeBUS {

    private final ThongTinVeDAO dao = new ThongTinVeDAO();

    public ThongTinVeDTO getFullInfo(int chuyenBayId, int gheId, String hoTen, String soGiayTo) {

        ThongTinVeDTO t = dao.getFullInfo(chuyenBayId, gheId);

        if (t != null) {
            t.setHoTen(hoTen);
            t.setSoGiayTo(soGiayTo);
        }

        return t;
    }
    public List<ThongTinVeDTO> getSimpleByKhachHang(int khachHangId) {
    return dao.getSimpleByKhachHang(khachHangId);
}
public List<ThongTinVeDTO> getByNhanVien(int taiKhoanNhanVienId) {
    return dao.getByNhanVien(taiKhoanNhanVienId);
}
}