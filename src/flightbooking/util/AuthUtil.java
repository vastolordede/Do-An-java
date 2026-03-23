package flightbooking.util;

public class AuthUtil {

    public static boolean hasPermission(int quyenId) {

        int nhomId = SessionContext.getAdminNhomQuyenId();

        // 🔥 ADMIN FULL
        if (nhomId == 1) return true;

        // 🔥 tránh null
        if (SessionContext.getPermissions() == null) return false;

        return SessionContext.getPermissions().contains(quyenId);
    }
}