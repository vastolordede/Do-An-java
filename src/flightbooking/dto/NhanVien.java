package flightbooking.dto;

public class NhanVien {
    private String maNV;
    private String hoTen;
    private String soDienThoai;
    private double luongCoBan;
    private int nhomQuyenId;

    public NhanVien() {}

    public NhanVien(String maNV, String hoTen, String soDienThoai, double luongCoBan, int nhomQuyenId) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.luongCoBan = luongCoBan;
        this.nhomQuyenId = nhomQuyenId;
    }

    // Getter và Setter (Wind tự generate trong NetBeans cho nhanh nhé)
    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public double getLuongCoBan() { return luongCoBan; }
    public void setLuongCoBan(double luongCoBan) { this.luongCoBan = luongCoBan; }
    public int getNhomQuyenId() { return nhomQuyenId; }
    public void setNhomQuyenId(int nhomQuyenId) { this.nhomQuyenId = nhomQuyenId; }
}