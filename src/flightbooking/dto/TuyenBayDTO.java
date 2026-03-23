package flightbooking.dto;

public class TuyenBayDTO {
    private int tuyenBayId;
    private Integer sanBayDiId;
    private Integer sanBayDenId;
    private int soDam;

    // ===== field để UI hiển thị =====
    private String sanBayDiTen;
    private String sanBayDenTen;

    public TuyenBayDTO() {}

    public int getTuyenBayId() { return tuyenBayId; }
    public void setTuyenBayId(int tuyenBayId) { this.tuyenBayId = tuyenBayId; }

    public Integer getSanBayDiId() { return sanBayDiId; }
    public void setSanBayDiId(Integer sanBayDiId) { this.sanBayDiId = sanBayDiId; }

    public Integer getSanBayDenId() { return sanBayDenId; }
    public void setSanBayDenId(Integer sanBayDenId) { this.sanBayDenId = sanBayDenId; }

    public int getSoDam() { return soDam; }
    public void setSoDam(int soDam) { this.soDam = soDam; }

    public String getSanBayDiTen() { return sanBayDiTen; }
    public void setSanBayDiTen(String sanBayDiTen) { this.sanBayDiTen = sanBayDiTen; }

    public String getSanBayDenTen() { return sanBayDenTen; }
    public void setSanBayDenTen(String sanBayDenTen) { this.sanBayDenTen = sanBayDenTen; }
}
