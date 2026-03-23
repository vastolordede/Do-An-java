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
    private String tenNhanVien;

    // ===== field phục vụ màn quản lý vé =====
    private String hoTenHanhKhach;
    private String soGiayTo;
    private String tenGhe;
    private String tenHangGhe;
    private LocalDateTime ngayTaoHoaDon;
    private BigDecimal tongTienHoaDon;

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

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }

    public Integer getTaiKhoanKhachHangId() {
        return taiKhoanKhachHangId;
    }

    public void setTaiKhoanKhachHangId(Integer id) {
        this.taiKhoanKhachHangId = id;
    }

    public String getEmailKhachHang() {
        return emailKhachHang;
    }

    public void setEmailKhachHang(String s) {
        this.emailKhachHang = s;
    }

    public String getHoTenHanhKhach() {
        return hoTenHanhKhach;
    }

    public void setHoTenHanhKhach(String hoTenHanhKhach) {
        this.hoTenHanhKhach = hoTenHanhKhach;
    }

    public String getSoGiayTo() {
        return soGiayTo;
    }

    public void setSoGiayTo(String soGiayTo) {
        this.soGiayTo = soGiayTo;
    }

    public String getTenGhe() {
        return tenGhe;
    }

    public void setTenGhe(String tenGhe) {
        this.tenGhe = tenGhe;
    }

    public String getTenHangGhe() {
        return tenHangGhe;
    }

    public void setTenHangGhe(String tenHangGhe) {
        this.tenHangGhe = tenHangGhe;
    }

    public LocalDateTime getNgayTaoHoaDon() {
        return ngayTaoHoaDon;
    }

    public void setNgayTaoHoaDon(LocalDateTime ngayTaoHoaDon) {
        this.ngayTaoHoaDon = ngayTaoHoaDon;
    }

    public BigDecimal getTongTienHoaDon() {
        return tongTienHoaDon;
    }

    public void setTongTienHoaDon(BigDecimal tongTienHoaDon) {
        this.tongTienHoaDon = tongTienHoaDon;
    }

    public boolean isDaHuy() {
        return trangThai != null && trangThai == 0;
    }

    public String getTrangThaiText() {
        return isDaHuy() ? "Đã hủy" : "Đang hiệu lực";
    }
}