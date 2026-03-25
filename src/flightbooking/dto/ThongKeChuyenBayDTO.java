package flightbooking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ThongKeChuyenBayDTO {
    private int chuyenBayId;
    private String sanBayDi;
    private String sanBayDen;
    private LocalDateTime gioKhoiHanh;
    private int veHieuLuc;
    private int veHuy;
    private BigDecimal doanhThu;

    public int getChuyenBayId() {
        return chuyenBayId;
    }

    public void setChuyenBayId(int chuyenBayId) {
        this.chuyenBayId = chuyenBayId;
    }

    public String getSanBayDi() {
        return sanBayDi;
    }

    public void setSanBayDi(String sanBayDi) {
        this.sanBayDi = sanBayDi;
    }

    public String getSanBayDen() {
        return sanBayDen;
    }

    public void setSanBayDen(String sanBayDen) {
        this.sanBayDen = sanBayDen;
    }

    public LocalDateTime getGioKhoiHanh() {
        return gioKhoiHanh;
    }

    public void setGioKhoiHanh(LocalDateTime gioKhoiHanh) {
        this.gioKhoiHanh = gioKhoiHanh;
    }

    public int getVeHieuLuc() {
        return veHieuLuc;
    }

    public void setVeHieuLuc(int veHieuLuc) {
        this.veHieuLuc = veHieuLuc;
    }

    public int getVeHuy() {
        return veHuy;
    }

    public void setVeHuy(int veHuy) {
        this.veHuy = veHuy;
    }

    public BigDecimal getDoanhThu() {
        return doanhThu;
    }

    public void setDoanhThu(BigDecimal doanhThu) {
        this.doanhThu = doanhThu;
    }

    public String getTuyenBayText() {
        return sanBayDi + " → " + sanBayDen;
    }

    public double getTyLeHuy() {
        int tong = veHieuLuc + veHuy;
        if (tong == 0) {
            return 0;
        }
        return (veHuy * 100.0) / tong;
    }
}