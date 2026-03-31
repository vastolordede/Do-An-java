package flightbooking.gui.admin.pnl;

import flightbooking.bus.SanBayBUS;
import flightbooking.dto.SanBayDTO;
import flightbooking.gui.admin.theme.AdminTheme;
import flightbooking.util.ActionConstants;
import flightbooking.util.ExcelExporter;
import flightbooking.util.ExcelImporter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PnlQuanLySanBay extends JPanel {

    private final SanBayBUS bus = new SanBayBUS();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Tên sân bay", "Thành phố", "Quốc gia"}, 0);
    private final JTable table = new JTable(model);

    private final JTextField txtTen = new JTextField();
    private final JTextField txtThanhPho = new JTextField();
    private final JTextField txtQuocGia = new JTextField();

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnExport;
    private JButton btnImport;
    // private JButton btnReload;

    public PnlQuanLySanBay() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildForm(), BorderLayout.NORTH);
        AdminTheme.styleTable(table, false);
        add(AdminTheme.wrapTable(table), BorderLayout.CENTER);
        table.getSelectionModel().addListSelectionListener(e -> fillForm());
        reload();
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints lc = makeLc(); GridBagConstraints fc = makeFc();

        lc.gridx = 0; lc.gridy = 0; form.add(makeLabel("Tên sân bay"), lc);
        fc.gridx = 1; fc.gridy = 0; AdminTheme.styleSoftTextField(txtTen); form.add(txtTen, fc);

        lc.gridx = 2; lc.gridy = 0; form.add(makeLabel("Thành phố"), lc);
        fc.gridx = 3; fc.gridy = 0; AdminTheme.styleSoftTextField(txtThanhPho); form.add(txtThanhPho, fc);

        lc.gridx = 4; lc.gridy = 0; form.add(makeLabel("Quốc gia"), lc);
        fc.gridx = 5; fc.gridy = 0; AdminTheme.styleSoftTextField(txtQuocGia); form.add(txtQuocGia, fc);

        // ✅ Dùng createActionButton
        // btnReload = AdminTheme.createActionButton("Làm mới",    AdminTheme.ButtonRole.NEUTRAL);
        btnAdd    = AdminTheme.createActionButton("Thêm",       AdminTheme.ButtonRole.ADD);
        btnUpdate = AdminTheme.createActionButton("Sửa",        AdminTheme.ButtonRole.EDIT  );
        btnDelete = AdminTheme.createActionButton("Xóa",        AdminTheme.ButtonRole.DELETE);
        btnExport = AdminTheme.createActionButton("Xuất Excel", AdminTheme.ButtonRole.NEUTRAL);
        btnImport = AdminTheme.createActionButton("Nhập Excel", AdminTheme.ButtonRole.NEUTRAL);

        // btnReload.addActionListener(e -> reloadData());
        btnAdd.addActionListener(e -> add());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());
        btnExport.addActionListener(e -> ExcelExporter.export(table, this));
        btnImport.addActionListener(e -> ExcelImporter.importToTable(table, this));

        return AdminTheme.wrapFormCard(form, btnAdd, btnUpdate, btnDelete, btnExport, btnImport);
    }

    private void reload() {
        model.setRowCount(0);
        for (SanBayDTO s : bus.dsSanBay()) {
            model.addRow(new Object[]{ s.getSanBayId(), s.getTenSanBay(), s.getThanhPho(), s.getQuocGia() });
        }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        txtTen.setText(String.valueOf(model.getValueAt(row, 1)));
        txtThanhPho.setText(String.valueOf(model.getValueAt(row, 2)));
        txtQuocGia.setText(String.valueOf(model.getValueAt(row, 3)));
    }

    private void add() {
        String ten = txtTen.getText().trim(), tp = txtThanhPho.getText().trim(), qg = txtQuocGia.getText().trim();
        if (ten.isEmpty() || tp.isEmpty() || qg.isEmpty()) { JOptionPane.showMessageDialog(this, "Không được để trống."); return; }
        if (!flightbooking.util.Validator.isValidName(ten)) { JOptionPane.showMessageDialog(this, "Tên sân bay không hợp lệ."); return; }
        if (!flightbooking.util.Validator.isValidName(tp)) { JOptionPane.showMessageDialog(this, "Thành phố không hợp lệ."); return; }
        if (!flightbooking.util.Validator.isValidName(qg)) { JOptionPane.showMessageDialog(this, "Quốc gia không hợp lệ."); return; }
        SanBayDTO s = new SanBayDTO(); s.setTenSanBay(ten); s.setThanhPho(tp); s.setQuocGia(qg);
        bus.themSanBay(s); reload();
    }

    private void update() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        String ten = txtTen.getText().trim(), tp = txtThanhPho.getText().trim(), qg = txtQuocGia.getText().trim();
        if (ten.isEmpty() || tp.isEmpty() || qg.isEmpty()) { JOptionPane.showMessageDialog(this, "Không được để trống."); return; }
        if (!flightbooking.util.Validator.isValidName(ten)) { JOptionPane.showMessageDialog(this, "Tên sân bay không hợp lệ."); return; }
        if (!flightbooking.util.Validator.isValidName(tp)) { JOptionPane.showMessageDialog(this, "Thành phố không hợp lệ."); return; }
        if (!flightbooking.util.Validator.isValidName(qg)) { JOptionPane.showMessageDialog(this, "Quốc gia không hợp lệ."); return; }
        int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
        SanBayDTO s = new SanBayDTO(); s.setSanBayId(id); s.setTenSanBay(ten); s.setThanhPho(tp); s.setQuocGia(qg);
        bus.capNhatSanBay(s); reload();
    }

    private void delete() {
        int row = table.getSelectedRow(); if (row < 0) return;
        bus.xoaSanBay(Integer.parseInt(String.valueOf(model.getValueAt(row, 0)))); reload();
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
        reload(); table.clearSelection();
        txtTen.setText(""); txtThanhPho.setText(""); txtQuocGia.setText("");
        revalidate(); repaint();
    }
}