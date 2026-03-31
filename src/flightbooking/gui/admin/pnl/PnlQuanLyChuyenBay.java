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
import flightbooking.gui.admin.theme.AdminTheme;
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
    private final HangHangKhongBUS hangBUS = new HangHangKhongBUS();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "TuyenBayId", "Tuyến", "HHK_ID", "Máy bay", "Giờ KH", "Giờ Đến", "Trạng thái"}, 0
    ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

    private final JTable table = new JTable(model);

    private final JComboBox<Item> cbTuyenBay = new JComboBox<>();
    private final JComboBox<HHKItem> cbHangHK = new JComboBox<>();
    private final JComboBox<MayBayItem> cbMayBay = new JComboBox<>();

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnGiaHang;
    private JButton btnSeatMap;
    private JButton btnReload;
    private JButton btnExport;
    private JButton btnImport;

    private static class HHKItem {
        final int id; final String text;
        HHKItem(int id, String text) { this.id = id; this.text = text; }
        @Override public String toString() { return text; }
    }

    private final JSpinner spGioKhoiHanh = new JSpinner(new SpinnerDateModel());
    private final JSpinner spGioDen = new JSpinner(new SpinnerDateModel());
    private final JComboBox<String> cbTrangThai = new JComboBox<>(new String[]{"1 - Đang mở", "0 - Hủy/Đóng"});

    public PnlQuanLyChuyenBay() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildForm(), BorderLayout.NORTH);
        AdminTheme.styleTable(table, false);
        add(AdminTheme.wrapTable(table), BorderLayout.CENTER);

        loadTuyenBayToCombo();
        loadMayBayToCombo();
        loadHangHKToCombo();
        reload();

        table.getSelectionModel().addListSelectionListener(e -> fillFormFromSelectedRow());

        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);

        // --- THÊM ĐOẠN NÀY VÀO CUỐI CONSTRUCTOR ---
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                // Tự động load lại mọi thứ khi panel được hiển thị
                loadTuyenBayToCombo();
                loadMayBayToCombo();
                loadHangHKToCombo();
                reload();
                // System.out.println("PnlQuanLyChuyenBay: Data auto-reloaded."); 
            }
        });
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridLayout(3, 4, 10, 10));
        form.setOpaque(false);
spGioKhoiHanh.setEditor(new JSpinner.DateEditor(spGioKhoiHanh, "yyyy-MM-dd HH:mm"));
spGioDen.setEditor(new JSpinner.DateEditor(spGioDen, "yyyy-MM-dd HH:mm"));
        AdminTheme.styleSoftComboBox(cbTuyenBay);
        AdminTheme.styleSoftComboBox(cbHangHK);
        AdminTheme.styleSoftComboBox(cbMayBay);
        AdminTheme.styleSoftSpinner(spGioKhoiHanh);
        AdminTheme.styleSoftSpinner(spGioDen);
        AdminTheme.styleSoftComboBox(cbTrangThai);

        form.add(new JLabel("Tuyến bay")); form.add(cbTuyenBay);
        form.add(new JLabel("Hãng HK ID")); form.add(cbHangHK);
        form.add(new JLabel("Máy bay")); form.add(cbMayBay);
        form.add(new JLabel("Giờ khởi hành (yyyy-MM-dd HH:mm)")); form.add(spGioKhoiHanh);
        form.add(new JLabel("Giờ đến (yyyy-MM-dd HH:mm)")); form.add(spGioDen);
        form.add(new JLabel("Trạng thái")); form.add(cbTrangThai);

        // ✅ Dùng createActionButton
        btnReload   = AdminTheme.createActionButton("Làm mới",       AdminTheme.ButtonRole.NEUTRAL);
        btnAdd      = AdminTheme.createActionButton("Thêm",          AdminTheme.ButtonRole.ADD);
        btnUpdate   = AdminTheme.createActionButton("Sửa",           AdminTheme.ButtonRole.EDIT  );
        btnDelete   = AdminTheme.createActionButton("Xóa",           AdminTheme.ButtonRole.DELETE);
        btnGiaHang  = AdminTheme.createActionButton("Giá hạng ghế",  AdminTheme.ButtonRole.NEUTRAL);
        btnSeatMap  = AdminTheme.createActionButton("Sơ đồ ghế",     AdminTheme.ButtonRole.NEUTRAL);
        btnExport   = AdminTheme.createActionButton("Xuất Excel",    AdminTheme.ButtonRole.NEUTRAL);
        btnImport   = AdminTheme.createActionButton("Nhập Excel",    AdminTheme.ButtonRole.NEUTRAL);

        btnAdd.addActionListener(e -> add());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());
        btnSeatMap.addActionListener(e -> openSeatMap());
        btnGiaHang.addActionListener(e -> openGiaHangDialog());
        btnExport.addActionListener(e -> ExcelExporter.export(table, this));
        btnImport.addActionListener(e -> ExcelImporter.importToTable(table, this));
        btnReload.addActionListener(e -> {
            loadTuyenBayToCombo();
            loadMayBayToCombo();
            reload();
            JOptionPane.showMessageDialog(this, "Dữ liệu đã được cập nhật mới nhất!");
        });

        return AdminTheme.wrapFormCard(form,
                btnReload, btnAdd, btnUpdate, btnDelete,
                btnGiaHang, btnSeatMap, btnExport, btnImport);
    }

    private void loadTuyenBayToCombo() {
        cbTuyenBay.removeAllItems();
        List<TuyenBayDTO> list = tuyenBayBUS.dsTuyenBay();
        for (TuyenBayDTO t : list) {
            String di = t.getSanBayDiTen() != null ? t.getSanBayDiTen() : ("SB#" + t.getSanBayDiId());
            String den = t.getSanBayDenTen() != null ? t.getSanBayDenTen() : ("SB#" + t.getSanBayDenId());
            cbTuyenBay.addItem(new Item(t.getTuyenBayId(),
                    "ID=" + t.getTuyenBayId() + " | " + di + " → " + den));
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

    private void loadHangHKToCombo() {
        cbHangHK.removeAllItems();
        List<HangHangKhongDTO> list = hangBUS.getDsHHK();
        for (HangHangKhongDTO h : list) {
            cbHangHK.addItem(new HHKItem(h.getHangHangKhongId(),
                    "ID=" + h.getHangHangKhongId() + " | " + h.getTenHang()));
        }
    }

    private void reload() {
        model.setRowCount(0);
        List<ChuyenBayDTO> list = chuyenBayBUS.dsChuyenBay();
        for (ChuyenBayDTO c : list) {
            String tuyenTxt = c.getSanBayDiTen() + " → " + c.getSanBayDenTen();
            String mbTxt = (c.getMayBayId() != null) ? ("MB#" + c.getMayBayId()) : "(null)";
            model.addRow(new Object[]{
                    c.getChuyenBayId(), c.getTuyenBayId(), tuyenTxt,
                    c.getHangHangKhongId(), mbTxt,
                    c.getGioKhoiHanh(), c.getGioDen(), c.getTrangThai()
            });
        }
    }

    private void fillFormFromSelectedRow() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        Integer tuyenBayId = (Integer) model.getValueAt(row, 1);
        Integer hhk = (Integer) model.getValueAt(row, 3);

        int cbId = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
        ChuyenBayDTO dto = chuyenBayBUS.findById(cbId);
        Integer mbId = dto != null ? dto.getMayBayId() : null;

        Object gkh = model.getValueAt(row, 5);
        Object gd = model.getValueAt(row, 6);
        Integer tt = (Integer) model.getValueAt(row, 7);

        selectComboById(cbTuyenBay, tuyenBayId != null ? tuyenBayId : -1);
        selectComboHHKById(hhk != null ? hhk : -1);
        selectComboMayBayById(mbId != null ? mbId : -1);

        if (gkh != null && dto != null) {
            spGioKhoiHanh.setValue(java.util.Date.from(
                dto.getGioKhoiHanh().atZone(java.time.ZoneId.systemDefault()).toInstant()));
        }
        if (gd != null && dto != null) {
            spGioDen.setValue(java.util.Date.from(
                dto.getGioDen().atZone(java.time.ZoneId.systemDefault()).toInstant()));
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

    private void selectComboHHKById(int id) {
        for (int i = 0; i < cbHangHK.getItemCount(); i++) {
            HHKItem it = cbHangHK.getItemAt(i);
            if (it != null && it.id == id) { cbHangHK.setSelectedIndex(i); return; }
        }
    }

    private void add() {
        Item tuyen = (Item) cbTuyenBay.getSelectedItem();
        MayBayItem mb = (MayBayItem) cbMayBay.getSelectedItem();
        HHKItem hhk = (HHKItem) cbHangHK.getSelectedItem();
        if (tuyen == null || mb == null || hhk == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ thông tin."); return;
        }

        LocalDateTime gkh = ((java.util.Date) spGioKhoiHanh.getValue())
                .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime gd = ((java.util.Date) spGioDen.getValue())
                .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

        if (gkh.isAfter(gd)) { JOptionPane.showMessageDialog(this, "Giờ khởi hành phải trước giờ đến."); return; }
        if (gkh.isEqual(gd)) { JOptionPane.showMessageDialog(this, "Giờ khởi hành và giờ đến không được trùng nhau."); return; }

        ChuyenBayDTO c = new ChuyenBayDTO();
        c.setTuyenBayId(tuyen.id);
        c.setHangHangKhongId(hhk.id);
        c.setMayBayId(mb.id);
        c.setGioKhoiHanh(gkh);
        c.setGioDen(gd);
        c.setTrangThai(cbTrangThai.getSelectedIndex() == 0 ? 1 : 0);

        try { chuyenBayBUS.themChuyenBay(c); reload(); }
        catch (RuntimeException ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
    }

    private void update() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        Item tuyen = (Item) cbTuyenBay.getSelectedItem();
        MayBayItem mb = (MayBayItem) cbMayBay.getSelectedItem();
        HHKItem hhk = (HHKItem) cbHangHK.getSelectedItem();
        if (tuyen == null || mb == null || hhk == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ thông tin."); return;
        }

        int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
        LocalDateTime gkh = ((java.util.Date) spGioKhoiHanh.getValue())
                .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime gd = ((java.util.Date) spGioDen.getValue())
                .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

        ChuyenBayDTO c = new ChuyenBayDTO();
        c.setChuyenBayId(id);
        c.setTuyenBayId(tuyen.id);
        c.setHangHangKhongId(hhk.id);
        c.setMayBayId(mb.id);
        c.setGioKhoiHanh(gkh);
        c.setGioDen(gd);
        c.setTrangThai(cbTrangThai.getSelectedIndex() == 0 ? 1 : 0);

        try { chuyenBayBUS.capNhatChuyenBay(c); reload(); }
        catch (RuntimeException ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
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
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn chuyến bay trước."); return; }
        int chuyenBayId = (Integer) model.getValueAt(row, 0);

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
                "Thiết lập giá hạng ghế - CB#" + chuyenBayId, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        DefaultTableModel m = new DefaultTableModel(new Object[]{"ID", "Tên Hạng Ghế", "Giá cơ bản"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 2; }
        };
        JTable tbl = new JTable(m);

        Object[][] danhSachHangGhe = {
            {3, "First Class", 4500000}, {2, "Business", 2500000},
            {4, "Premium Economy", 1800000}, {1, "Economy", 1200000}
        };

        GiaHangChuyenBayDAO dao = new GiaHangChuyenBayDAO();

        try {
            List<GiaHangChuyenBayDTO> giaDaLuu = dao.findByChuyenBay(chuyenBayId);
            for (Object[] hg : danhSachHangGhe) {
                int hangGheId = (Integer) hg[0];
                String tenHang = (String) hg[1];
                long giaHienTai = ((Number) hg[2]).longValue();
                if (giaDaLuu != null) {
                    for (GiaHangChuyenBayDTO daLuu : giaDaLuu) {
                        if (daLuu.getHangGheId() == hangGheId) {
                            giaHienTai = daLuu.getGiaCoBan().longValue(); break;
                        }
                    }
                }
                m.addRow(new Object[]{hangGheId, tenHang, giaHienTai});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không thể tải giá cũ: " + e.getMessage()); return;
        }

        JButton btnSave = AdminTheme.createActionButton("Lưu Thiết Lập Giá", AdminTheme.ButtonRole.NEUTRAL);
        btnSave.addActionListener(e -> {
            if (tbl.isEditing()) tbl.getCellEditor().stopCellEditing();
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
                    if (old == null) dao.insert(d); else dao.update(d);
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
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn chuyến bay trước."); return; }

        int chuyenBayId = (Integer) model.getValueAt(row, 0);
        ChuyenBayDTO dto = chuyenBayBUS.findById(chuyenBayId);

        if (dto == null || dto.getMayBayId() == null) {
            JOptionPane.showMessageDialog(this, "Chuyến bay chưa gán máy bay."); return;
        }

        int mayBayId = dto.getMayBayId();
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this),
                "Sơ đồ ghế - Chuyến bay #" + chuyenBayId, Dialog.ModalityType.APPLICATION_MODAL);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        d.setSize(screenSize.width, screenSize.height);
        d.setLocation(0, 0);
        d.setResizable(true);
        d.setLayout(new BorderLayout());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(new SeatMapPanel(chuyenBayId, mayBayId), BorderLayout.CENTER);

        JButton btnReloadSeat = AdminTheme.createActionButton("🔄 Reload sơ đồ ghế", AdminTheme.ButtonRole.NEUTRAL);
        btnReloadSeat.addActionListener(e -> {
            wrapper.removeAll();
            wrapper.add(new SeatMapPanel(chuyenBayId, mayBayId), BorderLayout.CENTER);
            wrapper.revalidate();
            wrapper.repaint();
        });

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnReloadSeat);

        d.add(wrapper, BorderLayout.CENTER);
        d.add(south, BorderLayout.SOUTH);
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
}