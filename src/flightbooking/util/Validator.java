package flightbooking.util;

public class Validator {

    // Họ tên: chỉ chữ + khoảng trắng (không số, không ký tự đặc biệt)
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) return false;

        return name.matches("^[\\p{L} ]+$");
        // \p{L} = tất cả ký tự chữ (có hỗ trợ tiếng Việt)
    }

    // Số giấy tờ: đúng 12 chữ số
    public static boolean isValidSoGiayTo(String s) {
        if (s == null) return false;

        return s.matches("^\\d{12}$");
    }

}