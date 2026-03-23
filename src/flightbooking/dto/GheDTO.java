package flightbooking.dto;

public class GheDTO {
    private int gheId;
    private Integer mayBayId;
    private Integer hangGheId;
    private Integer tang;     // ✅ NEW
    private String tenGhe;
    private Integer trangThai;

    private boolean daDat;
    private Integer rowIndex;
private Integer colIndex;

    public int getGheId() { return gheId; }
    public void setGheId(int gheId) { this.gheId = gheId; }

    public Integer getMayBayId() { return mayBayId; }
    public void setMayBayId(Integer mayBayId) { this.mayBayId = mayBayId; }

    public Integer getHangGheId() { return hangGheId; }
    public void setHangGheId(Integer hangGheId) { this.hangGheId = hangGheId; }

    public Integer getTang() { return tang; }          // ✅ NEW
    public void setTang(Integer tang) { this.tang = tang; }

    public String getTenGhe() { return tenGhe; }
    public void setTenGhe(String tenGhe) { this.tenGhe = tenGhe; }

    public Integer getTrangThai() { return trangThai; }
    public void setTrangThai(Integer trangThai) { this.trangThai = trangThai; }

    public boolean isDaDat() { return daDat; }
    public void setDaDat(boolean daDat) { this.daDat = daDat; }
    public Integer getRowIndex() { return rowIndex; }
public void setRowIndex(Integer rowIndex) { this.rowIndex = rowIndex; }

public Integer getColIndex() { return colIndex; }
public void setColIndex(Integer colIndex) { this.colIndex = colIndex; }
}
