package flightbooking.gui.admin.pnl;

import flightbooking.bus.HangHangKhongBUS;
import flightbooking.dto.HangHangKhongDTO;
import flightbooking.util.ActionConstants;
import flightbooking.util.ExcelExporter;
import flightbooking.util.ExcelImporter;

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
    private JButton btnExport;

    // Thêm field
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnImport;
    private JButton btnReload;

    public PnlQuanLyHHK() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildTop(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        table.getSelectionModel().addListSelectionListener(e -> fillForm());
        reload();
    }

    private JComponent buildTop() {
    JPanel form = new JPanel(new GridBagLayout());
    GridBagConstraints lc = makeLc(); GridBagConstraints fc = makeFc();

    lc.gridx=0; lc.gridy=0; form.add(makeLabel("Tên hãng hàng không"), lc);
    fc.gridx=1; fc.gridy=0; styleField(txtTen); form.add(txtTen, fc);

    btnAdd = new JButton("Thêm"); btnUpdate = new JButton("Sửa"); btnDelete = new JButton("Xóa");
    btnReload = new JButton("Làm mới");
    btnAdd.addActionListener(e -> add());
    btnUpdate.addActionListener(e -> update());
    btnDelete.addActionListener(e -> delete());
    btnReload.addActionListener(e -> reloadData());
    btnExport = new JButton("Xuất Excel");
btnExport.addActionListener(e -> {
    ExcelExporter.export(table, this);
});

    btnImport = new JButton("Nhập Excel");
    btnImport.addActionListener(e -> {
        ExcelImporter.importToTable(table, this);
    });
    return wrapWithActions(form, btnReload, btnAdd, btnUpdate, btnDelete, btnExport, btnImport);
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
        HangHangKhongDTO h = new HangHangKhongDTO();
        h.setTenHang(txtTen.getText().trim());
        hhkBUS.themHHK(h);
        reload();
        txtTen.setText("");
    }

    private void update() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        HangHangKhongDTO h = new HangHangKhongDTO();
        h.setHangHangKhongId((int) model.getValueAt(row, 0));
        h.setTenHang(txtTen.getText().trim());
        hhkBUS.capNhatHHK(h);
        reload();
    }

    private void delete() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) model.getValueAt(row, 0);
        hhkBUS.xoaHHK(id);
        reload();
        txtTen.setText("");
    }
    // Thêm method
    public void applyPermissions(List<Integer> actionIds) {
    btnAdd.setVisible(actionIds.contains(ActionConstants.THEM));
    btnUpdate.setVisible(actionIds.contains(ActionConstants.SUA));
    btnDelete.setVisible(actionIds.contains(ActionConstants.XOA));
    btnExport.setVisible(actionIds.contains(ActionConstants.XUAT_EXCEL));
    btnImport.setVisible(actionIds.contains(ActionConstants.NHAP_EXCEL));
    revalidate();
    repaint();
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

private void styleField(JTextField field) {
    field.setPreferredSize(new Dimension(160, 30));
    field.setFont(field.getFont().deriveFont(13f));
    field.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(3, 8, 3, 8)
    ));
}

private JPanel wrapWithActions(JPanel form, JButton... buttons) {
    JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
    for (JButton b : buttons) actions.add(b);
    JPanel wrap = new JPanel(new BorderLayout(0, 8));
    wrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
    wrap.add(form, BorderLayout.CENTER);
    wrap.add(actions, BorderLayout.SOUTH);
    return wrap;
}
public void reloadData() {
    reload();
    txtTen.setText("");
    table.clearSelection();
    revalidate();
    repaint();
}
}