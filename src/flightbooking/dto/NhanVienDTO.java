package flightbooking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class NhanVienDTO {
    private int nhanVienId;
    private String hoTen;
    private String dienThoai;
    private String email;
    private Integer phongBanId;
    private Integer chucVuId;
    private LocalDate ngayVaoLam;
    private int trangThai;
    private LocalDate ngayNghi;
    private BigDecimal luongCoBan;

    // ===== field để UI hiển thị (enrich từ JOIN) =====
    private String tenPhongBan;
    private String tenChucVu;

    public NhanVienDTO() {}

    public int getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(int nhanVienId) { this.nhanVienId = nhanVienId; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getDienThoai() { return dienThoai; }
    public void setDienThoai(String dienThoai) { this.dienThoai = dienThoai; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getPhongBanId() { return phongBanId; }
    public void setPhongBanId(Integer phongBanId) { this.phongBanId = phongBanId; }

    public Integer getChucVuId() { return chucVuId; }
    public void setChucVuId(Integer chucVuId) { this.chucVuId = chucVuId; }

    public LocalDate getNgayVaoLam() { return ngayVaoLam; }
    public void setNgayVaoLam(LocalDate ngayVaoLam) { this.ngayVaoLam = ngayVaoLam; }

    public int getTrangThai() { return trangThai; }
    public void setTrangThai(int trangThai) { this.trangThai = trangThai; }

    public LocalDate getNgayNghi() { return ngayNghi; }
    public void setNgayNghi(LocalDate ngayNghi) { this.ngayNghi = ngayNghi; }

    public BigDecimal getLuongCoBan() { return luongCoBan; }
    public void setLuongCoBan(BigDecimal luongCoBan) { this.luongCoBan = luongCoBan; }

    public String getTenPhongBan() { return tenPhongBan; }
    public void setTenPhongBan(String tenPhongBan) { this.tenPhongBan = tenPhongBan; }

    public String getTenChucVu() { return tenChucVu; }
    public void setTenChucVu(String tenChucVu) { this.tenChucVu = tenChucVu; }
}