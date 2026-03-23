package flightbooking.bus;

import flightbooking.dao.SanBayDAO;
import flightbooking.dto.SanBayDTO;

import java.util.List;

public class SanBayBUS {

    private final SanBayDAO dao = new SanBayDAO();

    public List<SanBayDTO> dsSanBay() {
        return dao.findAll();
    }

    public int themSanBay(SanBayDTO s) {
        if (s == null || s.getTenSanBay() == null || s.getTenSanBay().trim().isEmpty()) {
            throw new RuntimeException("Ten san bay khong duoc rong");
        }
        return dao.insert(s);
    }

    public void capNhatSanBay(SanBayDTO s) {
        if (s == null || s.getSanBayId() <= 0) throw new RuntimeException("sanbay_id khong hop le");
        dao.update(s);
    }

    public void xoaSanBay(int id) {
        if (id <= 0) return;
        dao.delete(id);
    }
}
