package flightbooking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VeDTO {
    private int veId;
    private Integer chuyenBayId;
    private Integer gheId;
    private Integer hanhKhachId;
    private Integer taiKhoanNhanVienId;
    private BigDecimal giaChot;
    private BigDecimal thueChot;
    private Integer trangThai;
    private LocalDateTime thoiDiemTao;
    private Integer taiKhoanKhachHangId;
private String emailKhachHang;

    public VeDTO() {
    }

    public int getVeId() {
        return veId;
    }

    public void setVeId(int veId) {
        this.veId = veId;
    }

    public Integer getChuyenBayId() {
        return chuyenBayId;
    }

    public void setChuyenBayId(Integer chuyenBayId) {
        this.chuyenBayId = chuyenBayId;
    }

    public Integer getGheId() {
        return gheId;
    }

    public void setGheId(Integer gheId) {
        this.gheId = gheId;
    }

    public Integer getHanhKhachId() {
        return hanhKhachId;
    }

    public void setHanhKhachId(Integer hanhKhachId) {
        this.hanhKhachId = hanhKhachId;
    }

    public Integer getTaiKhoanNhanVienId() {
        return taiKhoanNhanVienId;
    }

    public void setTaiKhoanNhanVienId(Integer taiKhoanNhanVienId) {
        this.taiKhoanNhanVienId = taiKhoanNhanVienId;
    }

    public BigDecimal getGiaChot() {
        return giaChot;
    }

    public void setGiaChot(BigDecimal giaChot) {
        this.giaChot = giaChot;
    }

    public BigDecimal getThueChot() {
        return thueChot;
    }

    public void setThueChot(BigDecimal thueChot) {
        this.thueChot = thueChot;
    }

    public Integer getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Integer trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getThoiDiemTao() {
        return thoiDiemTao;
    }

    public void setThoiDiemTao(LocalDateTime thoiDiemTao) {
        this.thoiDiemTao = thoiDiemTao;
    }
    private String tenNhanVien;

public String getTenNhanVien() {
    return tenNhanVien;
}

public void setTenNhanVien(String tenNhanVien) {
    this.tenNhanVien = tenNhanVien;
}
public Integer getTaiKhoanKhachHangId() { return taiKhoanKhachHangId; }
public void setTaiKhoanKhachHangId(Integer id) { this.taiKhoanKhachHangId = id; }

public String getEmailKhachHang() { return emailKhachHang; }
public void setEmailKhachHang(String s) { this.emailKhachHang = s; }
}
