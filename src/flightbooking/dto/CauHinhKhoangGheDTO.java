package flightbooking.dto;

public class CauHinhKhoangGheDTO {

    private int hangGheId;
    private String tenHang;
    private int tongSoGhe;

    public CauHinhKhoangGheDTO() {}

    public CauHinhKhoangGheDTO(int hangGheId, String tenHang, int tongSoGhe) {
        this.hangGheId = hangGheId;
        this.tenHang = tenHang;
        this.tongSoGhe = tongSoGhe;
    }

    public int getHangGheId() {
        return hangGheId;
    }

    public void setHangGheId(int hangGheId) {
        this.hangGheId = hangGheId;
    }

    public String getTenHang() {
        return tenHang;
    }

    public void setTenHang(String tenHang) {
        this.tenHang = tenHang;
    }

    public int getTongSoGhe() {
        return tongSoGhe;
    }

    public void setTongSoGhe(int tongSoGhe) {
        this.tongSoGhe = tongSoGhe;
    }
}