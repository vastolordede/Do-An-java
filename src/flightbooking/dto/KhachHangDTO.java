package flightbooking.dto;

public class KhachHangDTO {

    private int khachHangId;        // ✅ ID khách hàng (account)
    private String hoTen;
    private String dienThoai;
    private String tenDangNhap;
    private String matKhauMaHoa;
    private boolean dangHoatDong;
    private int taiKhoanKhachHangId;

    public KhachHangDTO() {}

    public KhachHangDTO(int khachHangId, String hoTen, String dienThoai,
                        String tenDangNhap, String matKhauMaHoa, boolean dangHoatDong) {
        this.khachHangId = khachHangId;
        this.hoTen = hoTen;
        this.dienThoai = dienThoai;
        this.tenDangNhap = tenDangNhap;
        this.matKhauMaHoa = matKhauMaHoa;
        this.dangHoatDong = dangHoatDong;
    }

    // ===== GETTER / SETTER =====

    public int getKhachHangId() {
        return khachHangId;
    }

    public void setKhachHangId(int khachHangId) {
        this.khachHangId = khachHangId;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getDienThoai() {
        return dienThoai;
    }

    public void setDienThoai(String dienThoai) {
        this.dienThoai = dienThoai;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhauMaHoa() {
        return matKhauMaHoa;
    }

    public void setMatKhauMaHoa(String matKhauMaHoa) {
        this.matKhauMaHoa = matKhauMaHoa;
    }

    public boolean isDangHoatDong() {
        return dangHoatDong;
    }

    public void setDangHoatDong(boolean dangHoatDong) {
        this.dangHoatDong = dangHoatDong;
    }

    public int getTaiKhoanKhachHangId() { return taiKhoanKhachHangId; }
    
    public void setTaiKhoanKhachHangId(int id) { this.taiKhoanKhachHangId = id; }
    private int diemTichLuy;

public int getDiemTichLuy() {
    return diemTichLuy;
}

public void setDiemTichLuy(int diemTichLuy) {
    this.diemTichLuy = diemTichLuy;
}
}