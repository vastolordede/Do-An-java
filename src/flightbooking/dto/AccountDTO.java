package flightbooking.dto;

public class AccountDTO {
    private String tenDangNhap;    // Thay cho username
    private String matKhauMaHoa;   // Thay cho password (theo cột MatKhauMaHoa)
    private int nhomQuyenId;       // Thay cho role (để khớp với khóa ngoại NhomQuyenId)
    private boolean dangHoatDong;  // Cột DangHoatDong trong ERD (để check tài khoản còn dùng được không)
    

    // Hàm khởi tạo không tham số
    public AccountDTO() {}

    // Hàm khởi tạo có tham số
    public AccountDTO(String tenDangNhap, String matKhauMaHoa, int nhomQuyenId, boolean dangHoatDong) {
        this.tenDangNhap = tenDangNhap;
        this.matKhauMaHoa = matKhauMaHoa;
        this.nhomQuyenId = nhomQuyenId;
        this.dangHoatDong = dangHoatDong;
    }

    private int taiKhoanId;   // 🔥 ID tài khoản
private int nhanVienId;   // 🔥 liên kết nhân viên
public int getTaiKhoanId() { return taiKhoanId; }
public void setTaiKhoanId(int taiKhoanId) { this.taiKhoanId = taiKhoanId; }

public int getNhanVienId() { return nhanVienId; }
public void setNhanVienId(int nhanVienId) { this.nhanVienId = nhanVienId; }
    // Các hàm Getter và Setter
    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

    public String getMatKhauMaHoa() { return matKhauMaHoa; }
    public void setMatKhauMaHoa(String matKhauMaHoa) { this.matKhauMaHoa = matKhauMaHoa; }

    public int getNhomQuyenId() { return nhomQuyenId; }
    public void setNhomQuyenId(int nhomQuyenId) { this.nhomQuyenId = nhomQuyenId; }

    public boolean isDangHoatDong() { return dangHoatDong; }
    public void setDangHoatDong(boolean dangHoatDong) { this.dangHoatDong = dangHoatDong; }
}