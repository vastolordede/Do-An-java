package flightbooking.bus;

import flightbooking.dto.CauHinhKhoangGheDTO;
import flightbooking.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class GheGeneratorBUS {

    public int taoGheTheoSoDoMoi(
        int mayBayId,
        int tongHang,
        int gheTrai,
        int ghePhai,
        List<CauHinhKhoangGheDTO> dsHang
) {
    Connection conn = null;
    int tongDaTao = 0;

    String sql = "INSERT INTO ghe (maybay_id, hangghe_id, tenghe, trangthai, tang, row_index, col_index) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?)";

    try {
        conn = DBConnection.getConnection();
        conn.setAutoCommit(false);

        PreparedStatement ps = conn.prepareStatement(sql);

        int sucChuaMoiHang = gheTrai + ghePhai;
        if (sucChuaMoiHang <= 0) {
            throw new RuntimeException("Số ghế mỗi hàng không hợp lệ.");
        }

        int currentRow = 1;

        for (CauHinhKhoangGheDTO h : dsHang) {
            int tongGheHang = h.getTongSoGhe();
            if (tongGheHang <= 0) continue;

            int soHangCanDung = (int) Math.ceil((double) tongGheHang / sucChuaMoiHang);
            int tongSlotBlock = soHangCanDung * sucChuaMoiHang;

            if (currentRow + soHangCanDung - 1 > tongHang) {
                throw new RuntimeException(
                        "Không đủ số hàng để xếp hạng " + h.getTenHang()
                                + ". Cần ít nhất " + (currentRow + soHangCanDung - 1)
                                + " hàng, nhưng chỉ có " + tongHang + "."
                );
            }

            for (int slot = 0; slot < tongSlotBlock; slot++) {
                int localRow = slot / sucChuaMoiHang;      // hàng trong block
                int localPos = slot % sucChuaMoiHang;      // vị trí trong hàng

                int rowIndex = currentRow + localRow;
                int colIndex = tinhVisualCol(localPos, gheTrai, ghePhai);

                // chỉ insert ghế thật, slot dư thì bỏ qua để render thành ô trống
                if (slot < tongGheHang) {
                    String tenGhe = taoTenGhe(rowIndex, colIndex);
                    addSeatBatch(ps, mayBayId, h.getHangGheId(), tenGhe, rowIndex, colIndex);
                    tongDaTao++;
                }
            }

            currentRow += soHangCanDung;
        }

        ps.executeBatch();
        conn.commit();
        return tongDaTao;

    } catch (Exception e) {
        e.printStackTrace();
        try {
            if (conn != null) conn.rollback();
        } catch (Exception ignored) {
        }
        throw new RuntimeException("Lỗi tạo sơ đồ ghế", e);
    } finally {
        try {
            if (conn != null) conn.close();
        } catch (Exception ignored) {
        }
    }
}

    private void addSeatBatch(PreparedStatement ps,
                              int mayBayId,
                              int hangGheId,
                              String tenGhe,
                              int rowIndex,
                              int colIndex) throws Exception {

        ps.setInt(1, mayBayId);
        ps.setInt(2, hangGheId);
        ps.setString(3, tenGhe);
        ps.setInt(4, 1); // trangthai
        ps.setInt(5, 1); // tang
        ps.setInt(6, rowIndex);
        ps.setInt(7, colIndex);
        ps.addBatch();
    }

    private String taoTenGhe(int rowIndex, int colIndex) {
        return rowIndex + cotHienThi(colIndex);
    }

    /**
     * Dùng đúng logic visual column.
     * Ví dụ:
     * col 1 -> A
     * col 2 -> B
     * col 3 -> C (thường là cột trống nếu layout 2-2)
     * col 4 -> D
     * col 5 -> E
     *
     * Nghĩa là nếu bạn tạo 2-2 thì ghế sẽ là:
     * 1A 1B [trống C] 1D 1E
     */
    private String cotHienThi(int colIndex) {
        char ch = (char) ('A' + colIndex - 1);
        return String.valueOf(ch);
    }
    private int tinhVisualCol(int localPos, int gheTrai, int ghePhai) {
    // localPos chạy từ 0 .. (gheTrai + ghePhai - 1)
    // ví dụ layout 2-2:
    // pos 0 -> col 1 (A)
    // pos 1 -> col 2 (B)
    // pos 2 -> col 4 (D)
    // pos 3 -> col 5 (E)
    if (localPos < gheTrai) {
        return localPos + 1;
    }
    return localPos + 2; // +1 vì index từ 0, +1 nữa để chừa cột aisle
}
}