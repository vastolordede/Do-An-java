package flightbooking.dto;

public class PhongBanDTO {

    private int phongBanId;     
    private String tenPhongBan;

    public PhongBanDTO() {
    }

    public int getPhongBanId() {
        return phongBanId;
    }

    public void setPhongBanId(int phongBanId) {
        this.phongBanId = phongBanId;
    }

    public String getTenPhongBan() {
        return tenPhongBan;
    }

    public void setTenPhongBan(String tenPhongBan) {
        this.tenPhongBan = tenPhongBan;
    }

}