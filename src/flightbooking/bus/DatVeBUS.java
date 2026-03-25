package flightbooking.bus;

import flightbooking.dao.*;
import flightbooking.dto.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class DatVeBUS {

    private final TuyenBayDAO tuyenBayDAO = new TuyenBayDAO();
    private final ChuyenBayDAO chuyenBayDAO = new ChuyenBayDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final GheDAO gheDAO = new GheDAO();
    private final VeDAO veDAO = new VeDAO();

    private final GiaHangChuyenBayDAO giaHangDAO = new GiaHangChuyenBayDAO();
    private final GiaGheOverrideDAO giaOverrideDAO = new GiaGheOverrideDAO();

    private final HanhKhachDAO hanhKhachDAO = new HanhKhachDAO();

    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final HoaDonVeDAO hoaDonVeDAO = new HoaDonVeDAO();
    private final ThanhToanDAO thanhToanDAO = new ThanhToanDAO();

    // 1) TÌM CHUYẾN
    public List<ChuyenBayDTO> timChuyen(int sanBayDiId, int sanBayDenId, LocalDateTime tu, LocalDateTime den) {
        try {
            Integer tuyenBayId = tuyenBayDAO.findTuyenBayId(sanBayDiId, sanBayDenId);
            if (tuyenBayId == null) return Collections.emptyList();
            return chuyenBayDAO.findByTuyenBayAndTimeRange(tuyenBayId, tu, den);
        } catch (Exception ex) {
            throw new RuntimeException("Lỗi tìm chuyến: " + ex.getMessage(), ex);
        }
    }

    // 2) GHẾ + TRẠNG THÁI GHẾ ĐÃ ĐẶT
    public List<GheDTO> dsGheCuaChuyen(int chuyenBayId) {
        try {
            ChuyenBayDTO cb = chuyenBayDAO.findById(chuyenBayId);
            if (cb == null) return Collections.emptyList();

            Integer mayBayId = cb.getMayBayId();
            if (mayBayId == null) return Collections.emptyList();

            List<GheDTO> all = gheDAO.findByMayBay(mayBayId);
            Set<Integer> daDat = new HashSet<>(veDAO.findGheIdDaDat(chuyenBayId));

            for (GheDTO g : all) {
                g.setDaDat(daDat.contains(g.getGheId()));
            }
            return all;
        } catch (Exception ex) {
            throw new RuntimeException("Lỗi lấy danh sách ghế: " + ex.getMessage(), ex);
        }
    }

    // 3) TÍNH GIÁ 1 GHẾ
    public BigDecimal tinhGiaGhe(int chuyenBayId, int gheId) {
        GheDTO ghe = gheDAO.findById(gheId);
        if (ghe == null) throw new RuntimeException("Khong tim thay ghe_id=" + gheId);

        // override trước
        GiaGheOverrideDTO ov = giaOverrideDAO.findByChuyenBayAndGhe(chuyenBayId, gheId);
        if (ov != null) {
            BigDecimal gia = ov.getGiaOverride() != null ? ov.getGiaOverride() : BigDecimal.ZERO;
            BigDecimal thue = ov.getThuePhiOverride() != null ? ov.getThuePhiOverride() : BigDecimal.ZERO;
            return gia.add(thue);
        }

        // base theo hang ghe
        Integer hangGheId = ghe.getHangGheId();
        if (hangGheId == null) throw new RuntimeException("Ghe chua co hangghe_id, ghe_id=" + gheId);

        GiaHangChuyenBayDTO base = giaHangDAO.findByChuyenBayAndHangGhe(chuyenBayId, hangGheId);
        if (base == null) {
            throw new RuntimeException("Chua co gia base cho chuyenbay_id=" + chuyenBayId + ", hangghe_id=" + hangGheId);
        }

        BigDecimal gia = base.getGiaCoBan() != null ? base.getGiaCoBan() : BigDecimal.ZERO;
        BigDecimal thue = base.getThuePhi() != null ? base.getThuePhi() : BigDecimal.ZERO;
        return gia.add(thue);
    }

    // 4) ĐẶT VÉ
    public KetQuaDatVe datVe(
        Integer taiKhoanKhachHangId,
        Integer taiKhoanNhanVienId,
        int chuyenBayId,
        List<ThongTinHanhKhachVaGhe> items,
        String phuongThucThanhToan,
        int diemSuDung
) {
    if (items == null || items.isEmpty()) {
        throw new RuntimeException("Danh sach hanh khach/ghe rong");
    }

    if (taiKhoanKhachHangId == null && taiKhoanNhanVienId == null) {
        throw new RuntimeException("Phải có người đặt");
    }

    if (taiKhoanKhachHangId != null && taiKhoanNhanVienId != null) {
        throw new RuntimeException("Chỉ được 1 trong 2: khách hàng hoặc nhân viên");
    }

    // Chỉ user mới dùng điểm
    if (taiKhoanKhachHangId != null) {
        int diemHienTai = khachHangDAO.getDiem(taiKhoanKhachHangId);

        if (diemSuDung > diemHienTai) {
            diemSuDung = diemHienTai;
        }

        if (diemSuDung < 0) {
            diemSuDung = 0;
        }
    } else {
        diemSuDung = 0; // admin side không dùng điểm
    }

    // kiểm tra trùng ghế trong cùng request
    Set<Integer> unique = new HashSet<>();
    for (ThongTinHanhKhachVaGhe it : items) {
        if (!unique.add(it.getGheId())) {
            throw new RuntimeException("Trùng ghế: " + it.getGheId());
        }
    }

    // kiểm tra ghế đã được đặt chưa
    Set<Integer> daDat = new HashSet<>(veDAO.findGheIdDaDat(chuyenBayId));
    for (ThongTinHanhKhachVaGhe it : items) {
        if (daDat.contains(it.getGheId())) {
            throw new RuntimeException("Ghế đã đặt: " + it.getGheId());
        }
    }

    // ===== TÍNH GIÁ GỐC TỪNG VÉ TRƯỚC =====
    List<BigDecimal> dsGiaGoc = new ArrayList<>();
    BigDecimal tongGoc = BigDecimal.ZERO;

    for (ThongTinHanhKhachVaGhe it : items) {
        BigDecimal giaGoc = tinhGiaGhe(chuyenBayId, it.getGheId());
        if (giaGoc == null) giaGoc = BigDecimal.ZERO;

        dsGiaGoc.add(giaGoc);
        tongGoc = tongGoc.add(giaGoc);
    }

    // ===== TÍNH TỔNG GIẢM THEO ĐIỂM =====
    BigDecimal tongGiam = BigDecimal.ZERO;
    if (taiKhoanKhachHangId != null && diemSuDung > 0) {
        tongGiam = BigDecimal.valueOf(diemSuDung).multiply(BigDecimal.TEN);

        // không cho giảm vượt tổng tiền gốc
        if (tongGiam.compareTo(tongGoc) > 0) {
            tongGiam = tongGoc;
        }
    }

    // ===== CHIA ĐỀU GIẢM GIÁ CHO TỪNG VÉ =====
    List<BigDecimal> dsGiamMoiVe = new ArrayList<>();
    int soVe = items.size();

    if (soVe > 0) {
        BigDecimal soVeBD = BigDecimal.valueOf(soVe);
        BigDecimal giamCoBanMoiVe = tongGiam.divideToIntegralValue(soVeBD);
        BigDecimal phanDu = tongGiam.remainder(soVeBD); // phần dư tính theo VND

        for (int i = 0; i < soVe; i++) {
            BigDecimal giamVe = giamCoBanMoiVe;

            // cộng 1 VND cho các vé đầu nếu còn dư
            if (BigDecimal.valueOf(i).compareTo(phanDu) < 0) {
                giamVe = giamVe.add(BigDecimal.ONE);
            }

            dsGiamMoiVe.add(giamVe);
        }
    }

    // ===== TẠO HÓA ĐƠN =====
    HoaDonDTO hd = new HoaDonDTO();
    hd.setTaiKhoanKhachHangId(taiKhoanKhachHangId);
    hd.setTaiKhoanNhanVienId(taiKhoanNhanVienId);
    hd.setTrangThai(1);

    int hoaDonId = hoaDonDAO.insert(hd);

    BigDecimal tong = BigDecimal.ZERO;
    List<Integer> veIds = new ArrayList<>();

    // ===== INSERT TỪNG VÉ VỚI GIÁ ĐÃ CHIA GIẢM =====
    for (int i = 0; i < items.size(); i++) {
        ThongTinHanhKhachVaGhe it = items.get(i);

        int hanhKhachId = hanhKhachDAO.insert(it.getHanhKhach());

        BigDecimal giaGoc = dsGiaGoc.get(i);
        BigDecimal giamVe = dsGiamMoiVe.get(i);

        BigDecimal giaSauGiam = giaGoc.subtract(giamVe);
        if (giaSauGiam.compareTo(BigDecimal.ZERO) < 0) {
            giaSauGiam = BigDecimal.ZERO;
        }

        VeDTO ve = new VeDTO();
        ve.setChuyenBayId(chuyenBayId);
        ve.setGheId(it.getGheId());
        ve.setHanhKhachId(hanhKhachId);
        ve.setTaiKhoanNhanVienId(taiKhoanNhanVienId);
        ve.setTaiKhoanKhachHangId(taiKhoanKhachHangId);
        ve.setGiaChot(giaSauGiam);   // lưu giá sau khi đã phân bổ điểm
        ve.setThueChot(BigDecimal.ZERO);
        ve.setTrangThai(1);

        int veId = veDAO.insert(ve);
        veIds.add(veId);

        HoaDonVeDTO hdv = new HoaDonVeDTO();
        hdv.setHoaDonId(hoaDonId);
        hdv.setVeId(veId);
        hoaDonVeDAO.insert(hdv);

        tong = tong.add(giaSauGiam);
    }

    // ===== TRỪ ĐIỂM THẬT CỦA KHÁCH =====
    if (taiKhoanKhachHangId != null && diemSuDung > 0) {
        khachHangDAO.truDiem(taiKhoanKhachHangId, diemSuDung);
    }

    // ===== CẬP NHẬT HÓA ĐƠN =====
    hoaDonDAO.updateTongTien(hoaDonId, tong);

    // ===== THANH TOÁN =====
    ThanhToanDTO tt = new ThanhToanDTO();
    tt.setHoaDonId(hoaDonId);
    tt.setSoTien(tong);
    tt.setPhuongThuc(phuongThucThanhToan);
    tt.setTrangThai(1);

    int thanhToanId = thanhToanDAO.insert(tt);

    // ===== CỘNG ĐIỂM SAU KHI ĐẶT THÀNH CÔNG =====
    if (taiKhoanKhachHangId != null) {
        int soDam = getSoDam(chuyenBayId);
        int diemCong = 0;

        for (ThongTinHanhKhachVaGhe it : items) {
            GheDTO ghe = gheDAO.findById(it.getGheId());
            double mul = getMultiplier(ghe.getHangGheId());
            diemCong += (int) (soDam * mul);
        }

        khachHangDAO.congDiem(taiKhoanKhachHangId, diemCong);
    }

    KetQuaDatVe kq = new KetQuaDatVe();
    kq.setHoaDonId(hoaDonId);
    kq.setVeIds(veIds);
    kq.setThanhToanId(thanhToanId);
    kq.setTongTien(tong);

    return kq;
}

    // ====== DTO phụ cho BUS ======
    public static class ThongTinHanhKhachVaGhe {
        private HanhKhachDTO hanhKhach;
        private int gheId;

        public HanhKhachDTO getHanhKhach() { return hanhKhach; }
        public void setHanhKhach(HanhKhachDTO hanhKhach) { this.hanhKhach = hanhKhach; }

        public int getGheId() { return gheId; }
        public void setGheId(int gheId) { this.gheId = gheId; }
    }

    public static class KetQuaDatVe {
        private int hoaDonId;
        private List<Integer> veIds;
        private int thanhToanId;    
        private BigDecimal tongTien;

        public int getHoaDonId() { return hoaDonId; }
        public void setHoaDonId(int hoaDonId) { this.hoaDonId = hoaDonId; }

        public List<Integer> getVeIds() { return veIds; }
        public void setVeIds(List<Integer> veIds) { this.veIds = veIds; }

        public int getThanhToanId() { return thanhToanId; }
        public void setThanhToanId(int thanhToanId) { this.thanhToanId = thanhToanId; }

        public BigDecimal getTongTien() { return tongTien; }
        public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }
    }
    private int getSoDam(int chuyenBayId) {
    try {
        ChuyenBayDTO cb = chuyenBayDAO.findById(chuyenBayId);
        if (cb == null) throw new RuntimeException("Không tìm thấy chuyến bay");

        Integer tuyenBayId = cb.getTuyenBayId();

        TuyenBayDTO tb = tuyenBayDAO.findById(tuyenBayId);
        if (tb == null) throw new RuntimeException("Không tìm thấy tuyến bay");

        return tb.getSoDam();

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
private double getMultiplier(Integer hangGheId) {
    if (hangGheId == null) return 0;

    switch (hangGheId) {
        case 3: return 1.0;   // First
        case 2: return 0.5;   // Business
        case 4: return 0.25;  // Premium
        case 1: return 0.0;   // Economy
        default: return 0;
    }
}

}
