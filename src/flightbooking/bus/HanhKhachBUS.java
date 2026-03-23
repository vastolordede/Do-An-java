package flightbooking.bus;

import flightbooking.dao.HanhKhachDAO;
import flightbooking.dto.HanhKhachDTO;
import java.util.List;

public class HanhKhachBUS {
    private final HanhKhachDAO hanhKhachDAO = new HanhKhachDAO();

    public List<HanhKhachDTO> getAll() {
        return hanhKhachDAO.findAll();
    }

    public HanhKhachDTO getById(int id) {
        return hanhKhachDAO.findById(id);
    }

    public int add(HanhKhachDTO hk) {
        if (hk.getHoTen() == null || hk.getHoTen().trim().isEmpty()) {
            throw new RuntimeException("Họ tên không được để trống");
        }
        return hanhKhachDAO.insert(hk);
    }

    public void update(HanhKhachDTO hk) {
        if (hk.getHoTen() == null || hk.getHoTen().trim().isEmpty()) {
            throw new RuntimeException("Họ tên không hợp lệ");
        }
        hanhKhachDAO.update(hk);
    }

    public void delete(int id) {
        if (id <= 0) {
            throw new RuntimeException("ID hành khách không hợp lệ");
        }
        hanhKhachDAO.delete(id);
    }

    public List<HanhKhachDTO> getByTaiKhoan(int taiKhoanId) {
        return hanhKhachDAO.findByTaiKhoan(taiKhoanId);
    }
}