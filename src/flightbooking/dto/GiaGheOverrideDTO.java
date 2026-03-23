package flightbooking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GiaGheOverrideDTO {
    private int giaGheOverrideId;          // giagheoverride_id
    private Integer chuyenBayId;
    private Integer gheId;
    private BigDecimal giaOverride;
    private BigDecimal thuePhiOverride;    // thuephioverride
    private String lyDo;
    private LocalDateTime capNhatLuc;

    public GiaGheOverrideDTO() {
    }

    public int getGiaGheOverrideId() {
        return giaGheOverrideId;
    }

    public void setGiaGheOverrideId(int giaGheOverrideId) {
        this.giaGheOverrideId = giaGheOverrideId;
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

    public BigDecimal getGiaOverride() {
        return giaOverride;
    }

    public void setGiaOverride(BigDecimal giaOverride) {
        this.giaOverride = giaOverride;
    }

    public BigDecimal getThuePhiOverride() {
        return thuePhiOverride;
    }

    public void setThuePhiOverride(BigDecimal thuePhiOverride) {
        this.thuePhiOverride = thuePhiOverride;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public LocalDateTime getCapNhatLuc() {
        return capNhatLuc;
    }

    public void setCapNhatLuc(LocalDateTime capNhatLuc) {
        this.capNhatLuc = capNhatLuc;
    }
}
