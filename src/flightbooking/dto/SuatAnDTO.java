package flightbooking.dto;

import java.math.BigDecimal;

public class SuatAnDTO {
    private int suatAnId;
    private String tenSuatAn;
    private BigDecimal gia;

    public int getSuatAnId() { return suatAnId; }
    public void setSuatAnId(int suatAnId) { this.suatAnId = suatAnId; }

    public String getTenSuatAn() { return tenSuatAn; }
    public void setTenSuatAn(String tenSuatAn) { this.tenSuatAn = tenSuatAn; }

    public BigDecimal getGia() { return gia; }
    public void setGia(BigDecimal gia) { this.gia = gia; }
}
