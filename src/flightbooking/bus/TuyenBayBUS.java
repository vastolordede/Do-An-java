package flightbooking.bus;

import flightbooking.dao.TuyenBayDAO;
import flightbooking.dto.TuyenBayDTO;

import java.util.List;

public class TuyenBayBUS {

    private final TuyenBayDAO dao = new TuyenBayDAO();

    public List<TuyenBayDTO> dsTuyenBay() {
        return dao.findAll();
    }

    public int themTuyenBay(TuyenBayDTO t) {
        validate(t);
        if (dao.existsByDiDen(t.getSanBayDiId(), t.getSanBayDenId(), null)) {
            throw new RuntimeException("Tuyến bay đã tồn tại (sân bay đi/đến)");
        }
        return dao.insert(t);
    }

    public void capNhatTuyenBay(TuyenBayDTO t) {
        if (t == null || t.getTuyenBayId() <= 0) throw new RuntimeException("tuyenBayId không hợp lệ");
        validate(t);
        if (dao.existsByDiDen(t.getSanBayDiId(), t.getSanBayDenId(), t.getTuyenBayId())) {
            throw new RuntimeException("Tuyến bay đã tồn tại (trùng với tuyến khác)");
        }
        dao.update(t);
    }

    public void xoaTuyenBay(int id) {
        if (id <= 0) throw new RuntimeException("id không hợp lệ");
        dao.delete(id);
    }

    public TuyenBayDTO getById(int id) {
        if (id <= 0) return null;
        return dao.findById(id);
    }

    private void validate(TuyenBayDTO t) {
        if (t == null) throw new RuntimeException("Dữ liệu rỗng");
        if (t.getSanBayDiId() == null || t.getSanBayDiId() <= 0) throw new RuntimeException("Sân bay đi không hợp lệ");
        if (t.getSanBayDenId() == null || t.getSanBayDenId() <= 0) throw new RuntimeException("Sân bay đến không hợp lệ");
        if (t.getSanBayDiId().intValue() == t.getSanBayDenId().intValue())
            throw new RuntimeException("Sân bay đi và đến không được trùng nhau");
        if (t.getSoDam() <= 0) throw new RuntimeException("Số dặm phải > 0");
    }
}
