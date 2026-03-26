package flightbooking.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class ValidationUtil {

    private ValidationUtil() {}

    // Tên người: chỉ chữ và khoảng trắng
    private static final String NAME_REGEX = "^[\\p{L} ]+$";

    private static final String PHONE_REGEX = "^0\\d{9}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public static void requireNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(fieldName + " không được để trống.");
        }
    }

    public static void validateName(String value, String fieldName) {
        requireNotBlank(value, fieldName);

        String s = value.trim().replaceAll("\\s+", " ");
        if (s.length() < 2) {
            throw new RuntimeException(fieldName + " phải có ít nhất 2 ký tự.");
        }
        if (!s.matches(NAME_REGEX)) {
            throw new RuntimeException(fieldName + " không được chứa số hoặc ký tự đặc biệt.");
        }
    }

    // Text thường: chỉ cần không rỗng, cho nhập thoải mái
    public static void validateCommonText(String value, String fieldName) {
        requireNotBlank(value, fieldName);
    }

    public static void validatePhone10Digits(String value) {
        requireNotBlank(value, "Số điện thoại");
        if (!value.trim().matches(PHONE_REGEX)) {
            throw new RuntimeException("Số điện thoại phải gồm đúng 10 số và bắt đầu bằng 0.");
        }
    }

    public static void validateEmail(String value) {
        requireNotBlank(value, "Email");
        if (!value.trim().matches(EMAIL_REGEX)) {
            throw new RuntimeException("Email không hợp lệ.");
        }
    }

    // Mã / số giấy tờ: chỉ cần không rỗng, cho tự do
    public static void validateDocument(String value) {
        requireNotBlank(value, "Số giấy tờ");
    }

    public static void validateNonNegative(BigDecimal value, String fieldName) {
        if (value == null) {
            throw new RuntimeException(fieldName + " không được để trống.");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException(fieldName + " không được âm.");
        }
    }

    public static void validateNonNegativeInt(int value, String fieldName) {
        if (value < 0) {
            throw new RuntimeException(fieldName + " không được âm.");
        }
    }

    public static void validatePositiveInt(int value, String fieldName) {
        if (value <= 0) {
            throw new RuntimeException(fieldName + " phải lớn hơn 0.");
        }
    }

    public static void validateDateOrder(LocalDate from, LocalDate to,
                                         String fromName, String toName) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new RuntimeException(fromName + " không được sau " + toName + ".");
        }
    }

    public static void validateDateTimeOrder(LocalDateTime from, LocalDateTime to,
                                             String fromName, String toName) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new RuntimeException(fromName + " không được sau " + toName + ".");
        }
    }

    public static void validateNotPastForCancel(LocalDate flightDate) {
        if (flightDate != null && flightDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("Không thể hủy vé của chuyến bay cũ hơn ngày hôm nay.");
        }
    }
}