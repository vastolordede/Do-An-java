package flightbooking.bus;

import flightbooking.dao.PhongBanDAO;
import flightbooking.dto.PhongBanDTO;
import java.util.List;

public class PhongBanBUS {

    private final PhongBanDAO phongBanDAO = new PhongBanDAO();

    // ===================== LẤY DANH SÁCH =====================
    public List<PhongBanDTO> layDanhSach() {
        return phongBanDAO.findAll();
    }

    // ===================== TÌM THEO ID =====================
    public PhongBanDTO timTheoId(int id) {
        return phongBanDAO.findById(id);
    }

    // ===================== THÊM =====================
    public void themPhongBan(PhongBanDTO pb) {

    if (pb == null)
        throw new RuntimeException("Dữ liệu không hợp lệ!");

    if (pb.getTenPhongBan() == null || pb.getTenPhongBan().trim().isEmpty())
        throw new RuntimeException("Tên phòng ban không được để trống!");

    String ten = pb.getTenPhongBan().trim();

    if (ten.length() > 100)
        throw new RuntimeException("Tên phòng ban quá dài!");

    if (daTonTaiTenKhacId(ten, -1))
        throw new RuntimeException("Tên phòng ban đã tồn tại!");

    pb.setTenPhongBan(ten);

    int newId = phongBanDAO.insert(pb);
    pb.setPhongBanId(newId);
}

    // ===================== SỬA =====================
    public void suaPhongBan(PhongBanDTO pb) {

    if (pb == null)
        throw new RuntimeException("Dữ liệu không hợp lệ!");

    if (pb.getPhongBanId() <= 0)
        throw new RuntimeException("ID không hợp lệ!");

    if (pb.getTenPhongBan() == null || pb.getTenPhongBan().trim().isEmpty())
        throw new RuntimeException("Tên phòng ban không được để trống!");

    String ten = pb.getTenPhongBan().trim();

    if (ten.length() > 100)
        throw new RuntimeException("Tên phòng ban quá dài!");

    if (daTonTaiTenKhacId(ten, pb.getPhongBanId()))
        throw new RuntimeException("Tên phòng ban đã tồn tại!");

    pb.setTenPhongBan(ten);

    phongBanDAO.update(pb);
}

    // ===================== XÓA =====================
    public void xoaPhongBan(int id) {

        if (id <= 0)
            throw new RuntimeException("ID không hợp lệ!");

        if (phongBanDAO.findById(id) == null)
            throw new RuntimeException("Phòng ban không tồn tại!");

        int count = phongBanDAO.countNhanVienByPhongBan(id);

        if (count > 0)
            throw new RuntimeException("Phòng ban đang có nhân viên, không thể xóa!");

        phongBanDAO.delete(id);
    }

    // ===================== KIỂM TRA TRÙNG =====================
    private boolean daTonTaiTenKhacId(String ten, int id) {
        PhongBanDTO pb = phongBanDAO.findExactByTen(ten);
        if (pb == null) return false;
        return pb.getPhongBanId() != id;
    }
}