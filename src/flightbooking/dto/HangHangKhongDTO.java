package flightbooking.dto;

public class HangHangKhongDTO {
    private int hangHangKhongId;
    private String tenHang;

    public HangHangKhongDTO() {}

    public HangHangKhongDTO(int hangHangKhongId, String tenHang) {
        this.hangHangKhongId = hangHangKhongId;
        this.tenHang = tenHang;
    }

    public int getHangHangKhongId() { return hangHangKhongId; }
    public void setHangHangKhongId(int hangHangKhongId) { this.hangHangKhongId = hangHangKhongId; }

    public String getTenHang() { return tenHang; }
    public void setTenHang(String tenHang) { this.tenHang = tenHang; }
}