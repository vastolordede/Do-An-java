package flightbooking.bus;

import flightbooking.dao.MayBayDAO;
import flightbooking.dto.MayBayDTO;

import java.util.List;

public class MayBayBUS {

    private final MayBayDAO dao = new MayBayDAO();

    public List<MayBayDTO> dsMayBay() {
        return dao.findAll();
    }

    public int themMayBay(MayBayDTO m) {
        validate(m);
        if (m.getSoTang() == null) m.setSoTang(1);
        return dao.insert(m);
    }

    public void capNhatMayBay(MayBayDTO m) {
        if (m == null || m.getMayBayId() <= 0) throw new RuntimeException("maybay_id không hợp lệ");
        validate(m);
        if (m.getSoTang() == null) m.setSoTang(1);
        dao.update(m);
    }

    public void xoaMayBay(int id) {
        if (id <= 0) return;
        dao.delete(id);
    }

    private void validate(MayBayDTO m) {
        if (m == null) throw new RuntimeException("Dữ liệu máy bay rỗng");
        if (m.getTenMayBay() == null || m.getTenMayBay().trim().isEmpty())
            throw new RuntimeException("Tên máy bay không được rỗng");
        if (m.getKieuMayBay() == null || m.getKieuMayBay().trim().isEmpty())
            throw new RuntimeException("Kiểu máy bay không được rỗng");
        // if (m.getSoTang() != null && m.getSoTang() <= 0)
        //     throw new RuntimeException("Số tầng phải >= 1");
        // if (m.getTongSoGhe() != null && m.getTongSoGhe() <= 0)
        //     throw new RuntimeException("Tổng số ghế phải > 0 (hoặc để trống)");
    }
}
