package flightbooking.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Dùng chung để validate từng dòng khi import Excel.
 * Mỗi panel tự build RowValidator bằng cách chain các rule.
 *
 * Ví dụ dùng:
 *   RowValidator v = ImportValidator.forRow()
 *       .requireNotBlank(1, "Tên sân bay")
 *       .requireNotBlank(2, "Thành phố")
 *       .requireName(1, "Tên sân bay")
 *       .build();
 */
public final class ImportValidator {

    private ImportValidator() {}

    // ----------------------------------------------------------------
    // Interface callback – truyền vào ExcelImporter
    // ----------------------------------------------------------------
    @FunctionalInterface
    public interface RowValidator {
        /** Trả về list lỗi của dòng đó, rỗng = hợp lệ. */
        List<String> validate(int rowIndex, Object[] row);
    }

    // ----------------------------------------------------------------
    // Builder
    // ----------------------------------------------------------------
    public static Builder forRow() {
        return new Builder();
    }

    public static class Builder {

        private final List<RowRule> rules = new ArrayList<>();

        /** Cột không được null/rỗng */
        public Builder requireNotBlank(int colIndex, String fieldName) {
            rules.add((rowIndex, row) -> {
                String val = getString(row, colIndex);
                if (val.isEmpty()) {
                    return "Dòng " + rowIndex + ": [" + fieldName + "] không được để trống.";
                }
                return null;
            });
            return this;
        }

        /** Cột phải là tên hợp lệ (chữ + khoảng trắng, ≥ 2 ký tự) */
        public Builder requireName(int colIndex, String fieldName) {
            rules.add((rowIndex, row) -> {
                String val = getString(row, colIndex);
                if (val.isEmpty()) return null; // đã check bởi requireNotBlank
                if (val.length() < 2) {
                    return "Dòng " + rowIndex + ": [" + fieldName + "] phải có ít nhất 2 ký tự.";
                }
                if (!val.matches("^[\\p{L} ]+$")) {
                    return "Dòng " + rowIndex + ": [" + fieldName + "] không được chứa số hoặc ký tự đặc biệt.";
                }
                return null;
            });
            return this;
        }

        /** Cột phải là số nguyên dương */
        public Builder requirePositiveInt(int colIndex, String fieldName) {
            rules.add((rowIndex, row) -> {
                String val = getString(row, colIndex);
                if (val.isEmpty()) return null;
                try {
                    int n = Integer.parseInt(val);
                    if (n <= 0) return "Dòng " + rowIndex + ": [" + fieldName + "] phải lớn hơn 0.";
                } catch (NumberFormatException e) {
                    return "Dòng " + rowIndex + ": [" + fieldName + "] phải là số nguyên.";
                }
                return null;
            });
            return this;
        }

        /** Cột phải là số thực không âm */
        public Builder requireNonNegativeDecimal(int colIndex, String fieldName) {
            rules.add((rowIndex, row) -> {
                String val = getString(row, colIndex);
                if (val.isEmpty()) return null;
                try {
                    double d = Double.parseDouble(val);
                    if (d < 0) return "Dòng " + rowIndex + ": [" + fieldName + "] không được âm.";
                } catch (NumberFormatException e) {
                    return "Dòng " + rowIndex + ": [" + fieldName + "] phải là số.";
                }
                return null;
            });
            return this;
        }

        /** Cột phải là email hợp lệ */
        public Builder requireEmail(int colIndex) {
            rules.add((rowIndex, row) -> {
                String val = getString(row, colIndex);
                if (val.isEmpty()) return null;
                if (!val.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                    return "Dòng " + rowIndex + ": [Email] không hợp lệ (" + val + ").";
                }
                return null;
            });
            return this;
        }

        /** Cột phải là số điện thoại 10 số bắt đầu bằng 0 */
        public Builder requirePhone(int colIndex) {
            rules.add((rowIndex, row) -> {
                String val = getString(row, colIndex);
                if (val.isEmpty()) return null;
                if (!val.matches("^0\\d{9}$")) {
                    return "Dòng " + rowIndex + ": [Số điện thoại] không hợp lệ (" + val + ").";
                }
                return null;
            });
            return this;
        }

        /** Số cột tối thiểu của mỗi dòng */
        public Builder requireMinColumns(int minCols) {
            rules.add((rowIndex, row) -> {
                if (row == null || row.length < minCols) {
                    return "Dòng " + rowIndex + ": thiếu cột (cần ít nhất " + minCols + " cột).";
                }
                return null;
            });
            return this;
        }

        public RowValidator build() {
            List<RowRule> snapshot = new ArrayList<>(rules);
            return (rowIndex, row) -> {
                List<String> errors = new ArrayList<>();
                for (RowRule rule : snapshot) {
                    String err = rule.check(rowIndex, row);
                    if (err != null) errors.add(err);
                }
                return errors;
            };
        }
    }

    // ----------------------------------------------------------------
    // Internal
    // ----------------------------------------------------------------
    @FunctionalInterface
    private interface RowRule {
        String check(int rowIndex, Object[] row);
    }

    private static String getString(Object[] row, int colIndex) {
        if (row == null || colIndex >= row.length || row[colIndex] == null) return "";
        return row[colIndex].toString().trim();
    }
}