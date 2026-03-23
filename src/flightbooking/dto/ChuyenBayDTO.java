package flightbooking.dto;

import java.time.LocalDateTime;

public class ChuyenBayDTO {
    private int chuyenBayId;
    private Integer tuyenBayId;
    private Integer hangHangKhongId;
    private Integer mayBayId;
    private LocalDateTime gioKhoiHanh;
    private LocalDateTime gioDen;
    private Integer trangThai;

    // ===== field để UI hiển thị (enrich từ TuyenBay) =====
    private String sanBayDiTen;
    private String sanBayDenTen;
    private Integer soDam;

    public ChuyenBayDTO() {}

    public int getChuyenBayId() { return chuyenBayId; }
    public void setChuyenBayId(int chuyenBayId) { this.chuyenBayId = chuyenBayId; }

    public Integer getTuyenBayId() { return tuyenBayId; }
    public void setTuyenBayId(Integer tuyenBayId) { this.tuyenBayId = tuyenBayId; }

    public Integer getHangHangKhongId() { return hangHangKhongId; }
    public void setHangHangKhongId(Integer hangHangKhongId) { this.hangHangKhongId = hangHangKhongId; }

    public Integer getMayBayId() { return mayBayId; }
    public void setMayBayId(Integer mayBayId) { this.mayBayId = mayBayId; }

    public LocalDateTime getGioKhoiHanh() { return gioKhoiHanh; }
    public void setGioKhoiHanh(LocalDateTime gioKhoiHanh) { this.gioKhoiHanh = gioKhoiHanh; }

    public LocalDateTime getGioDen() { return gioDen; }
    public void setGioDen(LocalDateTime gioDen) { this.gioDen = gioDen; }

    public Integer getTrangThai() { return trangThai; }
    public void setTrangThai(Integer trangThai) { this.trangThai = trangThai; }

    public String getSanBayDiTen() { return sanBayDiTen; }
    public void setSanBayDiTen(String sanBayDiTen) { this.sanBayDiTen = sanBayDiTen; }

    public String getSanBayDenTen() { return sanBayDenTen; }
    public void setSanBayDenTen(String sanBayDenTen) { this.sanBayDenTen = sanBayDenTen; }

    public Integer getSoDam() { return soDam; }
    public void setSoDam(Integer soDam) { this.soDam = soDam; }
}
