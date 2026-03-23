package flightbooking.bus;

import flightbooking.dao.ChucVuDAO;
import flightbooking.dto.ChucVuDTO;
import java.util.List;

public class ChucVuBUS {

    private final ChucVuDAO chucVuDAO = new ChucVuDAO();

    // ===================== LẤY DANH SÁCH =====================
    public List<ChucVuDTO> layDanhSach() {
        return chucVuDAO.findAll();
    }

    // ===================== TÌM THEO ID =====================
    public ChucVuDTO timTheoId(int id) {
       return chucVuDAO.findById(id);
    }

    // ===================== THÊM =====================
    public void themChucVu(ChucVuDTO cv) {

        if (cv == null)
            throw new RuntimeException("Dữ liệu không hợp lệ!");

        if (cv.getTenChucVu() == null || cv.getTenChucVu().trim().isEmpty())
            throw new RuntimeException("Tên chức vụ không được để trống!");

        String ten = cv.getTenChucVu().trim();

        if (ten.length() > 100)
            throw new RuntimeException("Tên chức vụ quá dài!");

        if (daTonTaiTenKhacId(ten, -1))
            throw new RuntimeException("Tên chức vụ đã tồn tại!");

        cv.setTenChucVu(ten);

        int newId = chucVuDAO.insert(cv);
        cv.setChucVuId(newId);
    }

    // ===================== SỬA =====================
    public void suaChucVu(ChucVuDTO cv) {

        if (cv == null)
            throw new RuntimeException("Dữ liệu không hợp lệ!");

        if (cv.getChucVuId() <= 0)
            throw new RuntimeException("ID không hợp lệ!");

        if (cv.getTenChucVu() == null || cv.getTenChucVu().trim().isEmpty())
            throw new RuntimeException("Tên chức vụ không được để trống!");

        String ten = cv.getTenChucVu().trim();

        if (ten.length() > 100)
            throw new RuntimeException("Tên chức vụ quá dài!");

        if (daTonTaiTenKhacId(ten, cv.getChucVuId()))
            throw new RuntimeException("Tên chức vụ đã tồn tại!");

        cv.setTenChucVu(ten);

        chucVuDAO.update(cv);
    }

    // ===================== XÓA =====================
    public void xoaChucVu(int id) {

        if (id <= 0)
            throw new RuntimeException("ID không hợp lệ!");

        if (chucVuDAO.findById(id) == null)
            throw new RuntimeException("Chức vụ không tồn tại!");

        int count = chucVuDAO.countNhanVienByChucVu(id);

        if (count > 0)
            throw new RuntimeException("Chức vụ đang có nhân viên, không thể xóa!");

        chucVuDAO.delete(id);  // Không cần try-catch ở đây
    }

    // ===================== KIỂM TRA TRÙNG =====================

    private boolean daTonTaiTenKhacId(String ten, int id) {
        ChucVuDTO cv = chucVuDAO.findExactByTen(ten);
        if (cv == null) return false;
        return cv.getChucVuId() != id;
}
    
}