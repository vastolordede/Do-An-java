package flightbooking.gui.admin.pnl;

import flightbooking.bus.SanBayBUS;
import flightbooking.bus.TuyenBayBUS;
import flightbooking.dto.SanBayDTO;
import flightbooking.dto.TuyenBayDTO;
import flightbooking.gui.admin.theme.AdminTheme;
import flightbooking.util.ActionConstants;
import flightbooking.util.ExcelExporter;
import flightbooking.util.ExcelImporter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PnlQuanLyTuyenBay extends JPanel {

    private final TuyenBayBUS tuyenBayBUS = new TuyenBayBUS();
    private final SanBayBUS sanBayBUS = new SanBayBUS();

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnExport;
    private JButton btnImport;
    private JButton btnReload;

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Sân bay đi", "Sân bay đến", "Số dặm"}, 0);
    private final JTable table = new JTable(model);

    private final JComboBox<Item> cbSanBayDi = new JComboBox<>();
    private final JComboBox<Item> cbSanBayDen = new JComboBox<>();
    private final JSpinner spSoDam = new JSpinner(new SpinnerNumberModel(0, 0, 100000, 10));

    public PnlQuanLyTuyenBay() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildForm(), BorderLayout.NORTH);
        AdminTheme.styleTable(table, false);
        add(AdminTheme.wrapTable(table), BorderLayout.CENTER);
        loadSanBayToCombo();
        reload();
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromSelectedRow());
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        AdminTheme.styleSoftComboBox(cbSanBayDi);
        AdminTheme.styleSoftComboBox(cbSanBayDen);
        AdminTheme.styleSoftSpinner(spSoDam);
        GridBagConstraints lc = makeLc(); GridBagConstraints fc = makeFc();

        lc.gridx = 0; lc.gridy = 0; form.add(makeLabel("Sân bay đi"), lc);
        fc.gridx = 1; fc.gridy = 0; form.add(cbSanBayDi, fc);
        lc.gridx = 2; lc.gridy = 0; form.add(makeLabel("Sân bay đến"), lc);
        fc.gridx = 3; fc.gridy = 0; form.add(cbSanBayDen, fc);
        lc.gridx = 4; lc.gridy = 0; form.add(makeLabel("Số dặm"), lc);
        fc.gridx = 5; fc.gridy = 0; form.add(spSoDam, fc);

        // ✅ Dùng createActionButton
        btnReload = AdminTheme.createActionButton("Làm mới",    AdminTheme.ButtonRole.NEUTRAL);
        btnAdd    = AdminTheme.createActionButton("Thêm",       AdminTheme.ButtonRole.ADD);
        btnUpdate = AdminTheme.createActionButton("Sửa",        AdminTheme.ButtonRole.EDIT  );
        btnDelete = AdminTheme.createActionButton("Xóa",        AdminTheme.ButtonRole.DELETE);
        btnExport = AdminTheme.createActionButton("Xuất Excel", AdminTheme.ButtonRole.NEUTRAL);
        btnImport = AdminTheme.createActionButton("Nhập Excel", AdminTheme.ButtonRole.NEUTRAL);

        btnReload.addActionListener(e -> reloadData());
        btnAdd.addActionListener(e -> add());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());
        btnExport.addActionListener(e -> ExcelExporter.export(table, this));
        btnImport.addActionListener(e -> ExcelImporter.importToTable(table, this));

        return AdminTheme.wrapFormCard(form, btnReload, btnAdd, btnUpdate, btnDelete, btnExport, btnImport);
    }

    private void loadSanBayToCombo() {
        cbSanBayDi.removeAllItems(); cbSanBayDen.removeAllItems();
        for (SanBayDTO s : sanBayBUS.dsSanBay()) {
            cbSanBayDi.addItem(new Item(s.getSanBayId(), s.getTenSanBay()));
            cbSanBayDen.addItem(new Item(s.getSanBayId(), s.getTenSanBay()));
        }
    }

    private void reload() {
        model.setRowCount(0);
        for (TuyenBayDTO t : tuyenBayBUS.dsTuyenBay()) {
            model.addRow(new Object[]{ t.getTuyenBayId(), findTenSanBay(t.getSanBayDiId()), findTenSanBay(t.getSanBayDenId()), t.getSoDam() });
        }
    }

    private String findTenSanBay(Integer id) {
        if (id == null) return "N/A";
        for (SanBayDTO s : sanBayBUS.dsSanBay()) { if (s.getSanBayId() == id) return s.getTenSanBay(); }
        return "ID=" + id;
    }

    private void fillFormFromSelectedRow() {
        int row = table.getSelectedRow(); if (row < 0) return;
        selectComboByText(cbSanBayDi, String.valueOf(model.getValueAt(row, 1)));
        selectComboByText(cbSanBayDen, String.valueOf(model.getValueAt(row, 2)));
        spSoDam.setValue(Integer.parseInt(String.valueOf(model.getValueAt(row, 3))));
    }

    private void selectComboByText(JComboBox<Item> cb, String text) {
        for (int i = 0; i < cb.getItemCount(); i++) {
            Item it = cb.getItemAt(i);
            if (it != null && it.text.equals(text)) { cb.setSelectedIndex(i); return; }
        }
    }

    private void add() {
        Item di = (Item) cbSanBayDi.getSelectedItem(); Item den = (Item) cbSanBayDen.getSelectedItem();
        if (di == null || den == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn sân bay."); return; }
        if (di.id == den.id) { JOptionPane.showMessageDialog(this, "Sân bay đi và đến không được trùng nhau."); return; }
        int soDam = (int) spSoDam.getValue();
        if (soDam <= 0) { JOptionPane.showMessageDialog(this, "Số dặm phải lớn hơn 0."); return; }
        TuyenBayDTO t = new TuyenBayDTO(); t.setSanBayDiId(di.id); t.setSanBayDenId(den.id); t.setSoDam(soDam);
        try { tuyenBayBUS.themTuyenBay(t); reload(); }
        catch (RuntimeException ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
    }

    private void update() {
        int row = table.getSelectedRow(); if (row < 0) return;
        Item di = (Item) cbSanBayDi.getSelectedItem(); Item den = (Item) cbSanBayDen.getSelectedItem();
        if (di == null || den == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn sân bay."); return; }
        int soDam = (int) spSoDam.getValue();
        if (soDam <= 0) { JOptionPane.showMessageDialog(this, "Số dặm phải lớn hơn 0."); return; }
        int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
        TuyenBayDTO t = new TuyenBayDTO(); t.setTuyenBayId(id); t.setSanBayDiId(di.id); t.setSanBayDenId(den.id); t.setSoDam(soDam);
        try { tuyenBayBUS.capNhatTuyenBay(t); reload(); }
        catch (RuntimeException ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
    }

    private void delete() {
        int row = table.getSelectedRow(); if (row < 0) return;
        tuyenBayBUS.xoaTuyenBay(Integer.parseInt(String.valueOf(model.getValueAt(row, 0)))); reload();
    }

    private static class Item {
        final int id; final String text;
        Item(int id, String text) { this.id = id; this.text = text; }
        @Override public String toString() { return text; }
    }

    public void applyPermissions(List<Integer> actionIds) {
        btnAdd.setVisible(actionIds.contains(ActionConstants.THEM));
        btnUpdate.setVisible(actionIds.contains(ActionConstants.SUA));
        btnDelete.setVisible(actionIds.contains(ActionConstants.XOA));
        btnExport.setVisible(actionIds.contains(ActionConstants.XUAT_EXCEL));
        btnImport.setVisible(actionIds.contains(ActionConstants.NHAP_EXCEL));
        revalidate(); repaint();
    }

    private GridBagConstraints makeLc() {
        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST; lc.insets = new Insets(6, 4, 6, 6); return lc;
    }

    private GridBagConstraints makeFc() {
        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.HORIZONTAL; fc.weightx = 1.0; fc.insets = new Insets(6, 0, 6, 12); return fc;
    }

    private JLabel makeLabel(String text) {
        JLabel lb = new JLabel(text); lb.setFont(lb.getFont().deriveFont(Font.PLAIN, 13f)); return lb;
    }

    public void reloadData() {
        loadSanBayToCombo(); reload(); table.clearSelection();
        if (cbSanBayDi.getItemCount() > 0) cbSanBayDi.setSelectedIndex(0);
        if (cbSanBayDen.getItemCount() > 0) cbSanBayDen.setSelectedIndex(0);
        revalidate(); repaint();
    }
}