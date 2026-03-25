package flightbooking.dto;

import java.math.BigDecimal;

public class ThongKeHangGheDTO {
    private String tenHangGhe;
    private int soVe;
    private BigDecimal doanhThu;

    public String getTenHangGhe() {
        return tenHangGhe;
    }

    public void setTenHangGhe(String tenHangGhe) {
        this.tenHangGhe = tenHangGhe;
    }

    public int getSoVe() {
        return soVe;
    }

    public void setSoVe(int soVe) {
        this.soVe = soVe;
    }

    public BigDecimal getDoanhThu() {
        return doanhThu;
    }

    public void setDoanhThu(BigDecimal doanhThu) {
        this.doanhThu = doanhThu;
    }
}