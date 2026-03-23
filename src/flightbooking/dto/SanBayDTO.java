package flightbooking.dto;

public class SanBayDTO {
    private int sanBayId;
    private String tenSanBay;  // tensanbay
    private String thanhPho;   // thanhpho
    private String quocGia;    // quocgia

    public SanBayDTO() {
    }

    public int getSanBayId() {
        return sanBayId;
    }

    public void setSanBayId(int sanBayId) {
        this.sanBayId = sanBayId;
    }

    public String getTenSanBay() {
        return tenSanBay;
    }

    public void setTenSanBay(String tenSanBay) {
        this.tenSanBay = tenSanBay;
    }

    public String getThanhPho() {
        return thanhPho;
    }

    public void setThanhPho(String thanhPho) {
        this.thanhPho = thanhPho;
    }

    public String getQuocGia() {
        return quocGia;
    }

    public void setQuocGia(String quocGia) {
        this.quocGia = quocGia;
    }
}
