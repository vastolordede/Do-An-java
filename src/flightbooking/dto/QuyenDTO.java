package flightbooking.dto;

public class QuyenDTO {
    private int quyenId;
    private String tenQuyen;
    private Integer parentId; // null = quyền cha

    public int getQuyenId() { return quyenId; }
    public void setQuyenId(int id) { this.quyenId = id; }

    public String getTenQuyen() { return tenQuyen; }
    public void setTenQuyen(String ten) { this.tenQuyen = ten; }

    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }

    public boolean isParent() { return parentId == null; }
}