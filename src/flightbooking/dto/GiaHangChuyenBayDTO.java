package flightbooking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GiaHangChuyenBayDTO {
    private int giaHChuyenBayId;      // giahchuyenbay_id
    private Integer chuyenBayId;
    private Integer hangGheId;
    private BigDecimal giaCoBan;      // giacoban
    private BigDecimal thuePhi;       // thuephi
    private LocalDateTime capNhatLuc; // capnhatluc

    public GiaHangChuyenBayDTO() {
    }

    public int getGiaHChuyenBayId() {
        return giaHChuyenBayId;
    }

    public void setGiaHChuyenBayId(int giaHChuyenBayId) {
        this.giaHChuyenBayId = giaHChuyenBayId;
    }

    public Integer getChuyenBayId() {
        return chuyenBayId;
    }

    public void setChuyenBayId(Integer chuyenBayId) {
        this.chuyenBayId = chuyenBayId;
    }

    public Integer getHangGheId() {
        return hangGheId;
    }

    public void setHangGheId(Integer hangGheId) {
        this.hangGheId = hangGheId;
    }

    public BigDecimal getGiaCoBan() {
        return giaCoBan;
    }

    public void setGiaCoBan(BigDecimal giaCoBan) {
        this.giaCoBan = giaCoBan;
    }

    public BigDecimal getThuePhi() {
        return thuePhi;
    }

    public void setThuePhi(BigDecimal thuePhi) {
        this.thuePhi = thuePhi;
    }

    public LocalDateTime getCapNhatLuc() {
        return capNhatLuc;
    }

    public void setCapNhatLuc(LocalDateTime capNhatLuc) {
        this.capNhatLuc = capNhatLuc;
    }
}
