package flightbooking.dto;

public class MayBayDTO {
    private int mayBayId;
    private String tenMayBay;
    private String kieuMayBay;
    private Integer tongSoGhe;
    private Integer soTang;

    public int getMayBayId() { return mayBayId; }
    public void setMayBayId(int mayBayId) { this.mayBayId = mayBayId; }

    public String getTenMayBay() { return tenMayBay; }
    public void setTenMayBay(String tenMayBay) { this.tenMayBay = tenMayBay; }

    public String getKieuMayBay() { return kieuMayBay; }
    public void setKieuMayBay(String kieuMayBay) { this.kieuMayBay = kieuMayBay; }

    public Integer getTongSoGhe() { return tongSoGhe; }
    public void setTongSoGhe(Integer tongSoGhe) { this.tongSoGhe = tongSoGhe; }

    public Integer getSoTang() { return soTang; }
    public void setSoTang(Integer soTang) { this.soTang = soTang; }
}
