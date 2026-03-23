package flightbooking.bus;

import flightbooking.dto.AccountDTO;
import flightbooking.dao.AccountDAO;

public class AccountBUS {
    private final AccountDAO accountDao = new AccountDAO();

    /**
     * Kiểm tra tính hợp lệ về mặt hình thức (Validation)
     */
    public String validateLogin(String tenDangNhap, String matKhau) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            return "Tên đăng nhập không được để trống.";
        }
        if (matKhau == null || matKhau.isEmpty()) {
            return "Mật khẩu không được để trống.";
        }
        if (matKhau.length() < 3) {
            return "Mật khẩu phải có ít nhất 3 ký tự.";
        }
        return null;
    }

    /**
     * Kiểm tra đăng nhập thật sự từ Database
     */
    public AccountDTO checkLogin(String tenDangNhap, String matKhau) {

    AccountDTO account = accountDao.getAccountByUsername(tenDangNhap);

    if (account == null || !account.isDangHoatDong()) {
        return null;
    }

    String stored = account.getMatKhauMaHoa();
    String hashedInput = flightbooking.util.PasswordUtil.hash(matKhau);

    // 1. Check hash (chuẩn)
    if (stored.equals(hashedInput)) {
        return account;
    }

    // 2. Check plain (account cũ)
    if (stored.equals(matKhau)) {

        // 🔥 BONUS: tự động upgrade lên hash luôn
        String newHash = hashedInput;
        accountDao.updatePassword(account.getTaiKhoanId(), newHash);

        return account;
    }

    return null;
}
    
    public AccountDTO checkLoginAdmin(String u, String p) {
    AccountDTO acc = checkLogin(u, p);
    // Giả sử NhomQuyenId = 1 là Admin/NhanVien
    if (acc != null && acc.getNhomQuyenId() == 1) {
        return acc;
    }
    return null; // Trả về null nếu là khách hàng hoặc sai pass
}
    
    
    public String registerAccount(AccountDTO account, String confirmPass) {
    // 1. Kiểm tra mật khẩu nhập lại
    if (!account.getMatKhauMaHoa().equals(confirmPass)) {
        return "Mật khẩu xác nhận không khớp!";
    }
    
    // 2. Kiểm tra tên đăng nhập đã tồn tại trong DB chưa
    if (accountDao.getAccountByUsername(account.getTenDangNhap()) != null) {
        return "Tên đăng nhập này đã tồn tại, vui lòng chọn tên khác!";
    }
    
    // 3. Gọi DAO để lưu
    boolean success = accountDao.insertAccount(account);
    return success ? null : "Lỗi hệ thống: Không thể tạo tài khoản lúc này.";
}
public String changePassword(int taiKhoanId, String oldPass, String newPass) {

    AccountDTO acc = accountDao.getAccountByUsername(
        flightbooking.util.SessionContext.getAdminUsername()
    );

    if (acc == null) return "Không tìm thấy tài khoản";

    if (!acc.getMatKhauMaHoa()
            .equals(flightbooking.util.PasswordUtil.hash(oldPass))) {
        return "Mật khẩu cũ sai";
    }

    if (newPass.length() < 3) {
        return "Mật khẩu mới quá yếu";
    }

    boolean ok = accountDao.updatePassword(
        taiKhoanId,
        flightbooking.util.PasswordUtil.hash(newPass)
    );

    return ok ? null : "Lỗi hệ thống";
}
}