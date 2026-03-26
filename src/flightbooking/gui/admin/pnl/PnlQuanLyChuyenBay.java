package flightbooking.gui.admin.pnl;

import flightbooking.bus.ChuyenBayBUS;
import flightbooking.bus.HangHangKhongBUS;
import flightbooking.bus.MayBayBUS;
import flightbooking.bus.TuyenBayBUS;
import flightbooking.dao.GiaHangChuyenBayDAO;
import flightbooking.dto.ChuyenBayDTO;
import flightbooking.dto.GiaHangChuyenBayDTO;
import flightbooking.dto.HangHangKhongDTO;
import flightbooking.dto.MayBayDTO;
import flightbooking.dto.TuyenBayDTO;
import flightbooking.util.ActionConstants;
import flightbooking.util.ExcelExporter;
import flightbooking.util.ExcelImporter;
import flightbooking.util.ValidationUtil;   

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;

import java.util.List;

public class PnlQuanLyChuyenBay extends JPanel {

    private final ChuyenBayBUS chuyenBayBUS = new ChuyenBayBUS();
    private final TuyenBayBUS tuyenBayBUS = new TuyenBayBUS();
    private final MayBayBUS mayBayBUS = new MayBayBUS();
    private JButton btnExport;

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "TuyenBayId", "Tuyến", "HHK_ID", "Máy bay", "Giờ KH", "Giờ Đến", "Trạng thái"}, 0
    ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

    private final JTable table = new JTable(model);

    private final JComboBox<Item> cbTuyenBay = new JComboBox<>();
    private final JComboBox<HHKItem> cbHangHK = new JComboBox<>();
private final HangHangKhongBUS hangBUS = new HangHangKhongBUS();

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnGiaHang;
    private JButton btnSeatMap;
    private JButton btnReload;
    private JButton btnImport;

    private static class HHKItem {
    final int id;
    final String text;

    HHKItem(int id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
    // ✅ đổi sang combo máy bay
    private final JComboBox<MayBayItem> cbMayBay = new JComboBox<>();

    private final JSpinner spGioKhoiHanh = new JSpinner(new SpinnerDateModel());
private final JSpinner spGioDen = new JSpinner(new SpinnerDateModel());
    private final JComboBox<String> cbTrangThai = new JComboBox<>(new String[]{"1 - Đang mở", "0 - Hủy/Đóng"});

    

    public PnlQuanLyChuyenBay() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildForm(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadTuyenBayToCombo();
        loadMayBayToCombo();
        loadHangHKToCombo();
        reload();

        table.getSelectionModel().addListSelectionListener(e -> fillFormFromSelectedRow());

        // Ẩn cột TuyenBayId (index=1)
        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridLayout(3, 4, 10, 10));

        form.add(new JLabel("Tuyến bay"));
        form.add(cbTuyenBay);

        form.add(new JLabel("Hãng HK ID"));
        form.add(cbHangHK);

        form.add(new JLabel("Máy bay"));
        form.add(cbMayBay);

        form.add(new JLabel("Giờ khởi hành (yyyy-MM-dd HH:mm)"));
        form.add(spGioKhoiHanh);

        form.add(new JLabel("Giờ đến (yyyy-MM-dd HH:mm)"));
        form.add(spGioDen);

        form.add(new JLabel("Trạng thái"));
        form.add(cbTrangThai);

        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnSeatMap = new JButton("Sơ đồ ghế");
        btnReload = new JButton("Làm mới");
        btnExport = new JButton("Xuất Excel");
        
btnImport = new JButton("Nhập Excel");
btnImport.addActionListener(e -> {
    ExcelImporter.importToTable(table, this);
});


        btnAdd.addActionListener(e -> add());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());
        btnSeatMap.addActionListener(e -> openSeatMap());

        // Gán sự kiện cho nút Reload
        btnReload.addActionListener(e -> {
            loadTuyenBayToCombo(); // Cập nhật lại danh sách tuyến bay mới nhất
            loadMayBayToCombo();   // Cập nhật lại danh sách máy bay mới nhất
            reload();              // Cập nhật lại bảng chuyến bay
            JOptionPane.showMessageDialog(this, "Dữ liệu đã được cập nhật mới nhất!");
        });

        btnGiaHang = new JButton("Giá hạng ghế");

btnGiaHang.addActionListener(e -> openGiaHangDialog());

        JPanel wrap = new JPanel(new BorderLayout(10, 10));
        wrap.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnReload);
        actions.add(btnAdd);
        actions.add(btnUpdate);
        actions.add(btnDelete);
        actions.add(btnGiaHang);
        actions.add(btnSeatMap);
        
        btnExport = new JButton("Xuất Excel");
btnExport.addActionListener(e -> {
    ExcelExporter.export(table, this);
});

actions.add(btnExport);
actions.add(btnImport);

        wrap.add(actions, BorderLayout.SOUTH);
        return wrap;
    }
private void validateChuyenBayTime(LocalDateTime gkh, LocalDateTime gd) {
    ValidationUtil.validateDateTimeOrder(gkh, gd, "Giờ khởi hành", "Giờ đến");
}
    private void loadTuyenBayToCombo() {
        cbTuyenBay.removeAllItems();
        List<TuyenBayDTO> list = tuyenBayBUS.dsTuyenBay();
        for (TuyenBayDTO t : list) {
            String di = t.getSanBayDiTen() != null ? t.getSanBayDiTen() : ("SB#" + t.getSanBayDiId());
            String den = t.getSanBayDenTen() != null ? t.getSanBayDenTen() : ("SB#" + t.getSanBayDenId());
            cbTuyenBay.addItem(new Item(
    t.getTuyenBayId(),
    "ID=" + t.getTuyenBayId() + " | " + di + " → " + den
));
        }
    }

    private void loadMayBayToCombo() {
        cbMayBay.removeAllItems();
        List<MayBayDTO> list = mayBayBUS.dsMayBay();
        for (MayBayDTO m : list) {
            String text = m.getTenMayBay() + " • " + m.getKieuMayBay()
                    + " • tầng=" + (m.getSoTang() == null ? 1 : m.getSoTang())
                    + " • ID=" + m.getMayBayId();
            cbMayBay.addItem(new MayBayItem(m.getMayBayId(), text));
        }
    }

    private void reload() {
        model.setRowCount(0);
        List<ChuyenBayDTO> list = chuyenBayBUS.dsChuyenBay();

        for (ChuyenBayDTO c : list) {
            String tuyenTxt = c.getSanBayDiTen() + " → " + c.getSanBayDenTen();
            String mbTxt = (c.getMayBayId() != null) ? ("MB#" + c.getMayBayId()) : "(null)";

            model.addRow(new Object[]{
                    c.getChuyenBayId(),
                    c.getTuyenBayId(),
                    tuyenTxt,
                    c.getHangHangKhongId(),
                    mbTxt,
                    c.getGioKhoiHanh(),
                    c.getGioDen(),
                    c.getTrangThai()
            });
        }
    }

    private void fillFormFromSelectedRow() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        Integer tuyenBayId = (Integer) model.getValueAt(row, 1);
        Integer hhk = (Integer) model.getValueAt(row, 3);

        // vì bảng đang hiển thị "MB#id" nên ta lấy lại từ BUS theo id ở DTO tốt nhất,
        // nhưng hiện model không lưu mayBayId riêng -> dùng lại dsChuyenBay để map theo id nhanh nhất.
        int cbId = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
        ChuyenBayDTO dto = chuyenBayBUS.findById(cbId); // ✅ nếu bạn có. Nếu chưa có thì bảo mình, mình viết luôn.
        Integer mbId = dto != null ? dto.getMayBayId() : null;

        Object gkh = model.getValueAt(row, 5);
        Object gd = model.getValueAt(row, 6);
        Integer tt = (Integer) model.getValueAt(row, 7);

        selectComboById(cbTuyenBay, tuyenBayId != null ? tuyenBayId : -1);
        selectComboHHKById(hhk != null ? hhk : -1);
        selectComboMayBayById(mbId != null ? mbId : -1);

        if (gkh != null) {
    spGioKhoiHanh.setValue(java.util.Date.from(
        ((LocalDateTime) dto.getGioKhoiHanh())
        .atZone(java.time.ZoneId.systemDefault()).toInstant()
    ));
}

if (gd != null) {
    spGioDen.setValue(java.util.Date.from(
        ((LocalDateTime) dto.getGioDen())
        .atZone(java.time.ZoneId.systemDefault()).toInstant()
    ));
}

        cbTrangThai.setSelectedIndex(tt != null && tt == 1 ? 0 : 1);
    }

    private void selectComboById(JComboBox<Item> cb, int id) {
        for (int i = 0; i < cb.getItemCount(); i++) {
            Item it = cb.getItemAt(i);
            if (it != null && it.id == id) { cb.setSelectedIndex(i); return; }
        }
    }

    private void selectComboMayBayById(int id) {
        for (int i = 0; i < cbMayBay.getItemCount(); i++) {
            MayBayItem it = cbMayBay.getItemAt(i);
            if (it != null && it.id == id) { cbMayBay.setSelectedIndex(i); return; }
        }
    }

    private void add() {
    Item tuyen = (Item) cbTuyenBay.getSelectedItem();
    MayBayItem mb = (MayBayItem) cbMayBay.getSelectedItem();
    if (tuyen == null || mb == null) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn tuyến bay và máy bay.");
        return;
    }

    ChuyenBayDTO c = new ChuyenBayDTO();
    c.setTuyenBayId(tuyen.id);

    HHKItem hhk = (HHKItem) cbHangHK.getSelectedItem();
    if (hhk == null) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn hãng hàng không.");
        return;
    }

    c.setHangHangKhongId(hhk.id);
    c.setMayBayId(mb.id);

    LocalDateTime gkh = ((java.util.Date) spGioKhoiHanh.getValue())
            .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

    LocalDateTime gd = ((java.util.Date) spGioDen.getValue())
            .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

    try {
        validateChuyenBayTime(gkh, gd);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage());
        return;
    }

    c.setGioKhoiHanh(gkh);
    c.setGioDen(gd);
    c.setTrangThai(cbTrangThai.getSelectedIndex() == 0 ? 1 : 0);

    chuyenBayBUS.themChuyenBay(c);
    reload();
}

    private void update() {
    int row = table.getSelectedRow();
    if (row < 0) {
        JOptionPane.showMessageDialog(this, "Chọn 1 chuyến bay để sửa.");
        return;
    }

    Item tuyen = (Item) cbTuyenBay.getSelectedItem();
    MayBayItem mb = (MayBayItem) cbMayBay.getSelectedItem();
    if (tuyen == null || mb == null) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn tuyến bay và máy bay.");
        return;
    }

    int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));

    ChuyenBayDTO c = new ChuyenBayDTO();
    c.setChuyenBayId(id);
    c.setTuyenBayId(tuyen.id);

    HHKItem hhk = (HHKItem) cbHangHK.getSelectedItem();
    if (hhk == null) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn hãng hàng không.");
        return;
    }

    c.setHangHangKhongId(hhk.id);
    c.setMayBayId(mb.id);

    LocalDateTime gkh = ((java.util.Date) spGioKhoiHanh.getValue())
            .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

    LocalDateTime gd = ((java.util.Date) spGioDen.getValue())
            .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

    try {
        validateChuyenBayTime(gkh, gd);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage());
        return;
    }

    c.setTrangThai(cbTrangThai.getSelectedIndex() == 0 ? 1 : 0);
    c.setGioKhoiHanh(gkh);
    c.setGioDen(gd);

    chuyenBayBUS.capNhatChuyenBay(c);
    reload();
}

    private void delete() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
        chuyenBayBUS.xoaChuyenBay(id);
        reload();
    }

    

    private static class Item {
        final int id; final String text;
        Item(int id, String text) { this.id = id; this.text = text; }
        @Override public String toString() { return text + " (ID=" + id + ")"; }
    }

    private static class MayBayItem {
        final int id; final String text;
        MayBayItem(int id, String text) { this.id = id; this.text = text; }
        @Override public String toString() { return text; }
    }
    private void openGiaHangDialog() {
    int row = table.getSelectedRow();
    if (row < 0) {
        JOptionPane.showMessageDialog(this, "Chọn chuyến bay trước.");
        return;
    }
    int chuyenBayId = (Integer) model.getValueAt(row, 0);

    JDialog dialog = new JDialog(
            SwingUtilities.getWindowAncestor(this),
            "Thiết lập giá hạng ghế - CB#" + chuyenBayId,
            Dialog.ModalityType.APPLICATION_MODAL
    );
    dialog.setSize(500, 300);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout(10, 10));

    // Thêm cột Tên Hạng Ghế (Cột 0 và 1 sẽ khóa không cho sửa)
    DefaultTableModel m = new DefaultTableModel(
            new Object[]{"ID", "Tên Hạng Ghế", "Giá cơ bản"}, 0
    ) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return c == 2; // Chỉ cho sửa cột Giá và Thuế
        }
    };
    JTable tbl = new JTable(m);

    // Mảng chứa các hạng ghế theo thứ tự ID
    Object[][] danhSachHangGhe = {
        {3, "First Class", 4500000},
        {2, "Business", 2500000},
        {4, "Premium Economy", 1800000},
        {1, "Economy", 1200000}
    };

    GiaHangChuyenBayDAO dao = new GiaHangChuyenBayDAO();

    // KIỂM TRA DỮ LIỆU CŨ TRONG DATABASE: Nếu đã có, lấy giá cũ lên
    try {
        List<GiaHangChuyenBayDTO> giaDaLuu = dao.findByChuyenBay(chuyenBayId);
        
        for (Object[] hg : danhSachHangGhe) {
            int hangGheId = (Integer) hg[0];
            String tenHang = (String) hg[1];
            long giaHienTai = ((Number) hg[2]).longValue(); // Giá mặc định ban đầu
             // Thuế mặc định ban đầu
            
            // Tìm trong list xem đã lưu chưa
            if (giaDaLuu != null) {
                for (GiaHangChuyenBayDTO daLuu : giaDaLuu) {
                    if (daLuu.getHangGheId() == hangGheId) {
                        giaHienTai = daLuu.getGiaCoBan().longValue();
                        
                        break;
                    }
                }
            }
            m.addRow(new Object[]{hangGheId, tenHang, giaHienTai});
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Không thể tải giá cũ: " + e.getMessage());
        return; // Dừng luôn không mở Dialog
    }

    JButton btnSave = new JButton("Lưu Thiết Lập Giá");

    btnSave.addActionListener(e -> {
    if (tbl.isEditing()) {
        tbl.getCellEditor().stopCellEditing();
    }

    try {
        for (int i = 0; i < m.getRowCount(); i++) {
            int hangGheId = Integer.parseInt(m.getValueAt(i, 0).toString());

            String strGia = m.getValueAt(i, 2) == null ? "0" : m.getValueAt(i, 2).toString().trim();
            if (strGia.isEmpty()) strGia = "0";

            java.math.BigDecimal gia = new java.math.BigDecimal(strGia);
            ValidationUtil.validateNonNegative(gia, "Giá cơ bản");

            GiaHangChuyenBayDTO d = new GiaHangChuyenBayDTO();
            d.setChuyenBayId(chuyenBayId);
            d.setHangGheId(hangGheId);
            d.setGiaCoBan(gia);

            GiaHangChuyenBayDTO old = dao.findByChuyenBayAndHangGhe(chuyenBayId, hangGheId);
            if (old == null) dao.insert(d);
            else dao.update(d);
        }

        JOptionPane.showMessageDialog(dialog, "Đã lưu giá thành công cho chuyến bay " + chuyenBayId);
        dialog.dispose();

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(dialog, "Lỗi nhập liệu: Giá phải là số hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(dialog, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
});

    dialog.add(new JScrollPane(tbl), BorderLayout.CENTER);
    dialog.add(btnSave, BorderLayout.SOUTH);
    dialog.setVisible(true);
}
private void openSeatMap() {
    int row = table.getSelectedRow();

    if (row < 0) {
        JOptionPane.showMessageDialog(this, "Chọn chuyến bay trước.");
        return;
    }

    int chuyenBayId = (Integer) model.getValueAt(row, 0);
    ChuyenBayDTO dto = chuyenBayBUS.findById(chuyenBayId);
    
    if (dto == null || dto.getMayBayId() == null) {
        JOptionPane.showMessageDialog(this, "Chuyến bay chưa gán máy bay.");
        return;
    }

    int mayBayId = dto.getMayBayId();

    JDialog d = new JDialog(
            SwingUtilities.getWindowAncestor(this),
            "Sơ đồ ghế - Chuyến bay #" + chuyenBayId,
            Dialog.ModalityType.APPLICATION_MODAL
    );

    // --- PHẦN CHỈNH SỬA ĐỂ FULL MÀN HÌNH ---
    // Lấy kích thước màn hình (trừ đi thanh Taskbar)
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    d.setSize(screenSize.width, screenSize.height);
    
    // Đặt vị trí ở góc trên cùng bên trái
    d.setLocation(0, 0);
    
    // Cho phép người dùng thay đổi kích thước/thu nhỏ nếu muốn
    d.setResizable(true); 
    // ---------------------------------------

    d.add(new SeatMapPanel(chuyenBayId, mayBayId));

    d.setVisible(true);
}

public void applyPermissions(List<Integer> actionIds) {
    btnAdd.setVisible(actionIds.contains(ActionConstants.THEM));
    btnUpdate.setVisible(actionIds.contains(ActionConstants.SUA));
    btnDelete.setVisible(actionIds.contains(ActionConstants.XOA));
    btnGiaHang.setVisible(actionIds.contains(ActionConstants.GIA_HANG_GHE));
    btnSeatMap.setVisible(actionIds.contains(ActionConstants.SO_DO_GHE));
    btnExport.setVisible(actionIds.contains(ActionConstants.XUAT_EXCEL));
    btnImport.setVisible(actionIds.contains(ActionConstants.NHAP_EXCEL));
    revalidate();
    repaint();
}
private void loadHangHKToCombo() {
    cbHangHK.removeAllItems();
    List<HangHangKhongDTO> list = hangBUS.getDsHHK(); // 🔥 đúng BUS của bạn

    for (HangHangKhongDTO h : list) {
        cbHangHK.addItem(new HHKItem(
            h.getHangHangKhongId(),
            "ID=" + h.getHangHangKhongId() + " | " + h.getTenHang()
        ));
    }
}
private void selectComboHHKById(int id) {
    for (int i = 0; i < cbHangHK.getItemCount(); i++) {
        HHKItem it = cbHangHK.getItemAt(i);
        if (it != null && it.id == id) {
            cbHangHK.setSelectedIndex(i);
            return;
        }
    }
}
}
