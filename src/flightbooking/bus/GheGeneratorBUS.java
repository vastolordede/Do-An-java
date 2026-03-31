package flightbooking.bus;

import flightbooking.dao.GheDAO;
import flightbooking.dao.GiaGheOverrideDAO;
import flightbooking.dto.CauHinhKhoangGheDTO;
import flightbooking.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class GheGeneratorBUS {

    private final GheDAO gheDAO = new GheDAO();
    private final GiaGheOverrideDAO giaGheOverrideDAO = new GiaGheOverrideDAO();

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

            int tongGhe = tinhTongSoGhe(dsHang);
            if (tongGhe <= 0) {
                throw new RuntimeException("Tổng số ghế phải lớn hơn 0.");
            }

            int tongHangThucTe = tongHang > 0
                    ? tongHang
                    : tuDongTinhTongHang(dsHang, sucChuaMoiHang);

            int currentRow = 1;

            for (CauHinhKhoangGheDTO h : dsHang) {
                int tongGheHang = h.getTongSoGhe();
                if (tongGheHang <= 0) continue;

                int soHangCanDung = (int) Math.ceil((double) tongGheHang / sucChuaMoiHang);
                int tongSlotBlock = soHangCanDung * sucChuaMoiHang;

                if (currentRow + soHangCanDung - 1 > tongHangThucTe) {
                    throw new RuntimeException(
                            "Không đủ số hàng để xếp hạng " + h.getTenHang()
                                    + ". Cần ít nhất " + (currentRow + soHangCanDung - 1)
                                    + " hàng, nhưng chỉ có " + tongHangThucTe + "."
                    );
                }

                for (int slot = 0; slot < tongSlotBlock; slot++) {
                    int localRow = slot / sucChuaMoiHang;
                    int localPos = slot % sucChuaMoiHang;

                    int rowIndex = currentRow + localRow;
                    int colIndex = tinhVisualCol(localPos, gheTrai, ghePhai);

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

    public int capNhatSoLuongGheTheoHang(
            int mayBayId,
            int gheTrai,
            int ghePhai,
            List<CauHinhKhoangGheDTO> dsHangMoi
    ) {
        if (gheDAO.existsUsedInVeByMayBay(mayBayId)) {
            throw new RuntimeException("Máy bay đã phát sinh vé, không thể chỉnh sửa cấu hình ghế.");
        }

        giaGheOverrideDAO.deleteByMayBay(mayBayId);
        gheDAO.deleteByMayBay(mayBayId);

        return taoGheTheoSoDoMoi(mayBayId, 0, gheTrai, ghePhai, dsHangMoi);
    }

    private int tinhTongSoGhe(List<CauHinhKhoangGheDTO> dsHang) {
        int tong = 0;
        if (dsHang == null) return 0;
        for (CauHinhKhoangGheDTO h : dsHang) {
            if (h != null && h.getTongSoGhe() > 0) {
                tong += h.getTongSoGhe();
            }
        }
        return tong;
    }

    private int tuDongTinhTongHang(List<CauHinhKhoangGheDTO> dsHang, int sucChuaMoiHang) {
        if (sucChuaMoiHang <= 0) {
            throw new RuntimeException("Sức chứa mỗi hàng không hợp lệ.");
        }

        int tongGhe = tinhTongSoGhe(dsHang);
        if (tongGhe <= 0) {
            throw new RuntimeException("Tổng số ghế phải lớn hơn 0.");
        }

        int minRows = tinhSoHangToiThieuTheoBlock(dsHang, sucChuaMoiHang);

        int bestRows = minRows;
        int bestExtra = minRows * sucChuaMoiHang - tongGhe;

        int maxRows = minRows + (int) Math.ceil(60.0 / sucChuaMoiHang);

        for (int rows = minRows; rows <= maxRows; rows++) {
            int totalSlots = rows * sucChuaMoiHang;
            int extra = totalSlots - tongGhe;

            if (extra < 0 || extra > 60) continue;

            if (totalSlots % 10 == 0) {
                return rows;
            }

            if (extra < bestExtra) {
                bestExtra = extra;
                bestRows = rows;
            }
        }

        return bestRows;
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
        ps.setInt(4, 1);
        ps.setInt(5, 1);
        ps.setInt(6, rowIndex);
        ps.setInt(7, colIndex);
        ps.addBatch();
    }

    private String taoTenGhe(int rowIndex, int colIndex) {
        return rowIndex + cotHienThi(colIndex);
    }

    private String cotHienThi(int colIndex) {
        char ch = (char) ('A' + colIndex - 1);
        return String.valueOf(ch);
    }

    private int tinhVisualCol(int localPos, int gheTrai, int ghePhai) {
        if (localPos < gheTrai) {
            return localPos + 1;
        }
        return localPos + 2;
    }

    private int tinhSoHangToiThieuTheoBlock(List<CauHinhKhoangGheDTO> dsHang, int sucChuaMoiHang) {
        int tongHang = 0;
        if (dsHang == null) return 0;

        for (CauHinhKhoangGheDTO h : dsHang) {
            if (h == null) continue;
            int tongGheHang = h.getTongSoGhe();
            if (tongGheHang <= 0) continue;

            tongHang += (int) Math.ceil((double) tongGheHang / sucChuaMoiHang);
        }
        return tongHang;
    }
}