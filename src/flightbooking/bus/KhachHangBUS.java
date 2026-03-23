package flightbooking.bus;

import flightbooking.dao.KhachHangDAO;
import flightbooking.dto.KhachHangDTO;
import flightbooking.util.UiUtil;
import java.awt.Component;

public class KhachHangBUS {
    
    private final KhachHangDAO khDAO = new KhachHangDAO();

    /**
     * Lấy thông tin khách hàng từ username
     */
    public KhachHangDTO getByUsername(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            return null;
        }
        return khDAO.findByUsername(tenDangNhap.trim());
    }

    /**
     * Lấy thông tin khách hàng từ ID
     */
    public KhachHangDTO getById(int hanhkhachId) {
        if (hanhkhachId <= 0) {
            throw new RuntimeException("ID khách hàng không hợp lệ");
        }
        return khDAO.findById(hanhkhachId);
    }

    /**
     * Xử lý logic Đăng ký tài khoản khách hàng
     */
    public boolean dangKy(Component parent, String hoTen, String sdt, String user, String pass, String rePass) {
        // Kiểm tra rỗng
        if (hoTen == null || hoTen.trim().isEmpty() ||
            sdt == null || sdt.trim().isEmpty() ||
            user == null || user.trim().isEmpty() ||
            pass == null || pass.isEmpty()) {
            UiUtil.showInfo(parent, "Vui lòng nhập đầy đủ tất cả thông tin!");
            return false;
        }

        // Kiểm tra độ dài SĐT (nên từ 9-11 chữ số)
        if (!sdt.trim().matches("\\d{9,11}")) {
            UiUtil.showInfo(parent, "Số điện thoại phải từ 9 đến 11 chữ số!");
            return false;
        }

        // Kiểm tra khớp mật khẩu
        if (!pass.equals(rePass)) {
            UiUtil.showInfo(parent, "Mật khẩu xác nhận không trùng khớp!");
            return false;
        }

        // Kiểm tra độ dài mật khẩu
        if (pass.length() < 3) {
            UiUtil.showInfo(parent, "Mật khẩu phải có ít nhất 3 ký tự!");
            return false;
        }

        // Kiểm tra username không chứa ký tự đặc biệt (tùy chọn)
        if (!user.trim().matches("[a-zA-Z0-9_]{3,20}")) {
            UiUtil.showInfo(parent, "Tên đăng nhập chỉ chứa chữ, số, _ và từ 3-20 ký tự!");
            return false;
        }

        // Gọi xuống DAO để thực hiện Insert vào Neon
        try {
            String hashed = flightbooking.util.PasswordUtil.hash(pass);

if (khDAO.register(hoTen.trim(), sdt.trim(), user.trim(), hashed)) {
                UiUtil.showInfo(parent, "✓ Chúc mừng! Bạn đã đăng ký tài khoản thành công.");
                return true;
            } else {
                UiUtil.showInfo(parent, "✗ Đăng ký thất bại! Tên đăng nhập có thể đã tồn tại.");
                return false;
            }
        } catch (Exception ex) {
            UiUtil.showInfo(parent, "✗ Lỗi hệ thống: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Xử lý logic Đăng nhập khách hàng
     */
    public boolean dangNhap(Component parent, String user, String pass) {
    if (user == null || user.trim().isEmpty() ||
        pass == null || pass.isEmpty()) {
        UiUtil.showInfo(parent, "Tên đăng nhập và mật khẩu không được để trống!");
        return false;
    }

    try {
        KhachHangDTO kh = khDAO.findByUsername(user.trim());

        // ❌ không tồn tại
        if (kh == null || !kh.isDangHoatDong()) {
            UiUtil.showInfo(parent, "✗ Tài khoản hoặc mật khẩu không chính xác!");
            return false;
        }

        String stored = kh.getMatKhauMaHoa(); // ✅ đúng field của bạn
        String hashedInput = flightbooking.util.PasswordUtil.hash(pass);

        // ✅ 1. Check hash (chuẩn)
        if (stored.equals(hashedInput)) {
            return true;
        }

        // ✅ 2. Check plain (account cũ)
        if (stored.equals(pass)) {

            // 🔥 upgrade lên hash
            khDAO.updatePassword(kh.getTaiKhoanKhachHangId(), hashedInput);

            return true;
        }

        // ❌ sai mật khẩu
        UiUtil.showInfo(parent, "✗ Tài khoản hoặc mật khẩu không chính xác!");
        return false;

    } catch (Exception ex) {
        UiUtil.showInfo(parent, "✗ Lỗi hệ thống: " + ex.getMessage());
        return false;
    }
}

    /**
     * Kiểm tra tài khoản có tồn tại không
     */
    public boolean userExists(String tenDangNhap) {
        return khDAO.existsByUsername(tenDangNhap);
    }

    /**
     * Lấy danh sách khách hàng (admin)
     */
    public java.util.List<KhachHangDTO> getAll() {
        return khDAO.findAll();
    }
    public int getDiem(int id) {
    return khDAO.getDiem(id);
}

}