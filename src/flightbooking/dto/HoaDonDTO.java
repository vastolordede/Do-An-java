package flightbooking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HoaDonDTO {
    private int hoaDonId;
    private Integer taiKhoanKhachHangId;
    private Integer taiKhoanNhanVienId;
    private LocalDateTime ngayTao;
    private BigDecimal tongTien;
    private Integer trangThai;

    public HoaDonDTO() {
    }

    public int getHoaDonId() {
        return hoaDonId;
    }

    public void setHoaDonId(int hoaDonId) {
        this.hoaDonId = hoaDonId;
    }

    public Integer getTaiKhoanKhachHangId() {
        return taiKhoanKhachHangId;
    }

    public void setTaiKhoanKhachHangId(Integer taiKhoanKhachHangId) {
        this.taiKhoanKhachHangId = taiKhoanKhachHangId;
    }

    public Integer getTaiKhoanNhanVienId() {
        return taiKhoanNhanVienId;
    }

    public void setTaiKhoanNhanVienId(Integer taiKhoanNhanVienId) {
        this.taiKhoanNhanVienId = taiKhoanNhanVienId;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public Integer getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Integer trangThai) {
        this.trangThai = trangThai;
    }
}
