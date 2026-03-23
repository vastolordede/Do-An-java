package flightbooking.dto;

import java.time.LocalDate;

public class HanhKhachDTO {
    private int hanhKhachId;
    private String hoTen;
    private LocalDate ngaySinh;
    private String soGiayTo;
    private String quocTich;

    public HanhKhachDTO() {
    }

    public int getHanhKhachId() {
        return hanhKhachId;
    }

    public void setHanhKhachId(int hanhKhachId) {
        this.hanhKhachId = hanhKhachId;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getSoGiayTo() {
        return soGiayTo;
    }

    public void setSoGiayTo(String soGiayTo) {
        this.soGiayTo = soGiayTo;
    }

    public String getQuocTich() {
        return quocTich;
    }

    public void setQuocTich(String quocTich) {
        this.quocTich = quocTich;
    }
}
