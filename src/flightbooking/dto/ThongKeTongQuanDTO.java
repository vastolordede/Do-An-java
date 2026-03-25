package flightbooking.dto;

import java.math.BigDecimal;

public class ThongKeTongQuanDTO {
    private BigDecimal tongDoanhThu = BigDecimal.ZERO;
    private int tongVeHieuLuc;
    private int tongVeHuy;
    private BigDecimal doanhThuTrungBinhVe = BigDecimal.ZERO;

    public BigDecimal getTongDoanhThu() {
        return tongDoanhThu;
    }

    public void setTongDoanhThu(BigDecimal tongDoanhThu) {
        this.tongDoanhThu = tongDoanhThu;
    }

    public int getTongVeHieuLuc() {
        return tongVeHieuLuc;
    }

    public void setTongVeHieuLuc(int tongVeHieuLuc) {
        this.tongVeHieuLuc = tongVeHieuLuc;
    }

    public int getTongVeHuy() {
        return tongVeHuy;
    }

    public void setTongVeHuy(int tongVeHuy) {
        this.tongVeHuy = tongVeHuy;
    }

    public BigDecimal getDoanhThuTrungBinhVe() {
        return doanhThuTrungBinhVe;
    }

    public void setDoanhThuTrungBinhVe(BigDecimal doanhThuTrungBinhVe) {
        this.doanhThuTrungBinhVe = doanhThuTrungBinhVe;
    }
}