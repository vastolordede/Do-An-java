package flightbooking.util;

import flightbooking.dto.KhachHangDTO;

/**
 * Lớp quản lý session cho Admin và User
 * - Admin/Nhân viên: Dùng setAdminSession() / getAdminUsername()
 * - User/Khách hàng: Dùng setCurrentUser() / getCurrentUser()
 */
public class SessionContext {

    // ================= ADMIN/NHÂN VIÊN SESSION =================
    private static String adminUsername;      // Tên đăng nhập admin
    private static int adminNhomQuyenId;      // Nhóm quyền admin
    private static int adminTaiKhoanId;       // ID tài khoản admin

    // ================= USER/KHÁCH HÀNG SESSION =================
    private static KhachHangDTO currentUser;  // Thông tin khách hàng đăng nhập

    // ================== ADMIN SESSION METHODS ==================

    /**
     * Lưu thông tin session cho admin/nhân viên
     */
    public static void setAdminSession(String username, int nhomQuyenId) {
        adminUsername = username;
        adminNhomQuyenId = nhomQuyenId;
        
    }

    /**
     * Lưu thông tin session cho admin/nhân viên (với ID tài khoản)
     */
    public static void setAdminSession(String username, int nhomQuyenId, int taiKhoanId) {
        adminUsername = username;
        adminNhomQuyenId = nhomQuyenId;
        adminTaiKhoanId = taiKhoanId;
    }

    /**
     * Lấy tên đăng nhập admin
     */
    public static String getAdminUsername() {
        return adminUsername;
    }

    /**
     * Lấy nhóm quyền admin
     */
    public static int getAdminNhomQuyenId() {
        return adminNhomQuyenId;
    }

    /**
     * Lấy ID tài khoản admin
     */
    public static int getAdminTaiKhoanId() {
        return adminTaiKhoanId;
    }

    /**
     * Kiểm tra admin đã đăng nhập chưa
     */
    public static boolean isAdminLoggedIn() {
        return adminUsername != null && !adminUsername.isEmpty();
    }

    /**
     * Xóa session admin
     */
    public static void clearAdminSession() {
        adminUsername = null;
        adminNhomQuyenId = 0;
        adminTaiKhoanId = 0;
    }

    // ================== USER SESSION METHODS ==================

    /**
     * Lưu thông tin session cho khách hàng
     */
    public static void setCurrentUser(KhachHangDTO user) {
        currentUser = user;
    }

    /**
     * Lấy thông tin khách hàng đang đăng nhập
     */
    public static KhachHangDTO getCurrentUser() {
        return currentUser;
    }

    /**
     * Lấy ID khách hàng
     */
   // ===== USER/KHÁCH HÀNG =====

public static Integer getCurrentUserId() {
    return currentUser != null ? currentUser.getTaiKhoanKhachHangId() : null;
}

public static String getCurrentUsername() {
    return currentUser != null ? currentUser.getTenDangNhap() : null;
}

    /**
     * Kiểm tra khách hàng đã đăng nhập chưa
     */
    public static boolean isUserLoggedIn() {
        return currentUser != null;
    }

    /**
     * Xóa session khách hàng
     */
    public static void clearUserSession() {
        currentUser = null;
    }

    // ================== GENERAL METHODS ==================

    /**
     * Xóa tất cả session (Admin + User)
     */
    public static void clearAll() {
        clearAdminSession();
        clearUserSession();
    }

    /**
     * Kiểm tra có ai đó đang đăng nhập (Admin hoặc User)
     */
    public static boolean isLoggedIn() {
        return isAdminLoggedIn() || isUserLoggedIn();
    }

    /**
     * In ra thông tin session (dùng để debug)
     */
    public static void printSessionInfo() {
        System.out.println("===== SESSION INFO =====");
        System.out.println("Admin: " + (isAdminLoggedIn() ? adminUsername : "Not logged in"));
        System.out.println("User: " + (isUserLoggedIn() ? getCurrentUsername() : "Not logged in"));
        System.out.println("========================");
    }
    // thêm
private static java.util.Set<Integer> permissions = new java.util.HashSet<>();
public static java.util.Set<Integer> getPermissions() {
    return permissions;
}
public static void setPermissions(java.util.Set<Integer> perms) {
    if (perms == null) {
        permissions = new java.util.HashSet<>();
    } else {
        permissions = perms;
    }
}

public static boolean hasAnyPermission() {
    return permissions != null && !permissions.isEmpty();
}
}