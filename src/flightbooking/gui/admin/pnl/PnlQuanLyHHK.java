package flightbooking.gui.admin.pnl;

import flightbooking.bus.HangHangKhongBUS;
import flightbooking.dto.HangHangKhongDTO;
import flightbooking.gui.admin.theme.AdminTheme;
import flightbooking.util.ActionConstants;
import flightbooking.util.ExcelExporter;
import flightbooking.util.ExcelImporter;
import flightbooking.util.Validator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PnlQuanLyHHK extends JPanel {
    private final HangHangKhongBUS hhkBUS = new HangHangKhongBUS();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Tên hãng"}, 0
    ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

    private final JTable table = new JTable(model);
    private final JTextField txtTen = new JTextField();

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnImport;
    // private JButton btnReload;
    private JButton btnExport;

    public PnlQuanLyHHK() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildTop(), BorderLayout.NORTH);
        AdminTheme.styleTable(table, false);
        add(AdminTheme.wrapTable(table), BorderLayout.CENTER);
        table.getSelectionModel().addListSelectionListener(e -> fillForm());
        reload();
        // Thêm Listener này vào Constructor của PnlQuanLyHHK
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                reload(); // Tự động load lại mỗi khi Panel này được hiển thị
            }
        });
    }

    private JComponent buildTop() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints lc = makeLc(); GridBagConstraints fc = makeFc();

        lc.gridx = 0; lc.gridy = 0; form.add(makeLabel("Tên hãng hàng không"), lc);
        fc.gridx = 1; fc.gridy = 0; AdminTheme.styleSoftTextField(txtTen); form.add(txtTen, fc);

        // ✅ Dùng createActionButton
        // btnReload = AdminTheme.createActionButton("Làm mới",    AdminTheme.ButtonRole.NEUTRAL);
        btnAdd    = AdminTheme.createActionButton("Thêm",       AdminTheme.ButtonRole.ADD);
        btnUpdate = AdminTheme.createActionButton("Sửa",        AdminTheme.ButtonRole.EDIT  );
        btnDelete = AdminTheme.createActionButton("Xóa",        AdminTheme.ButtonRole.DELETE);
        btnExport = AdminTheme.createActionButton("Xuất Excel", AdminTheme.ButtonRole.NEUTRAL);
        btnImport = AdminTheme.createActionButton("Nhập Excel", AdminTheme.ButtonRole.NEUTRAL);

        btnAdd.addActionListener(e -> add());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());
        // btnReload.addActionListener(e -> reloadData());
        // btnExport.addActionListener(e -> ExcelExporter.export(table, this));
        btnImport.addActionListener(e -> ExcelImporter.importToTable(table, this));

        return AdminTheme.wrapFormCard(form, btnAdd, btnUpdate, btnDelete, btnExport, btnImport);
    }

    private void reload() {
        model.setRowCount(0);
        try {
            for (HangHangKhongDTO h : hhkBUS.getDsHHK()) {
                model.addRow(new Object[]{ h.getHangHangKhongId(), h.getTenHang() });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row >= 0) txtTen.setText(String.valueOf(model.getValueAt(row, 1)));
    }

    private void add() {
        String ten = txtTen.getText().trim();
        if (ten.isEmpty()) { JOptionPane.showMessageDialog(this, "Tên hãng không được để trống."); return; }
        if (!Validator.isValidName(ten)) { JOptionPane.showMessageDialog(this, "Tên hãng chỉ được chứa chữ cái và khoảng trắng."); return; }
        HangHangKhongDTO h = new HangHangKhongDTO();
        h.setTenHang(ten);
        hhkBUS.themHHK(h);
        reload();
        txtTen.setText("");
    }

    private void update() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        String ten = txtTen.getText().trim();
        if (ten.isEmpty()) { JOptionPane.showMessageDialog(this, "Tên hãng không được để trống."); return; }
        if (!Validator.isValidName(ten)) { JOptionPane.showMessageDialog(this, "Tên hãng chỉ được chứa chữ cái và khoảng trắng."); return; }
        HangHangKhongDTO h = new HangHangKhongDTO();
        h.setHangHangKhongId((int) model.getValueAt(row, 0));
        h.setTenHang(ten);
        hhkBUS.capNhatHHK(h);
        reload();
    }

    private void delete() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        hhkBUS.xoaHHK((int) model.getValueAt(row, 0));
        reload();
        txtTen.setText("");
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
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(6, 4, 6, 6);
        return lc;
    }

    private GridBagConstraints makeFc() {
        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;
        fc.insets = new Insets(6, 0, 6, 12);
        return fc;
    }

    private JLabel makeLabel(String text) {
        JLabel lb = new JLabel(text);
        lb.setFont(lb.getFont().deriveFont(Font.PLAIN, 13f));
        return lb;
    }

    public void reloadData() {
        reload();
        txtTen.setText("");
        table.clearSelection();
        revalidate(); repaint();
    }
}