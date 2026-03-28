package flightbooking.gui.admin.pnl;

import flightbooking.bus.SanBayBUS;
import flightbooking.dto.SanBayDTO;
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
            new Object[]{"ID", "Tên sân bay", "Thành phố", "Quốc gia"}, 0
    );
    private final JTable table = new JTable(model);

    private final JTextField txtTen = new JTextField();
    private final JTextField txtThanhPho = new JTextField();
    private final JTextField txtQuocGia = new JTextField();

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnExport;
    private JButton btnImport;
    private JButton btnReload;

    public PnlQuanLySanBay() {
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        add(buildForm(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> fillForm());
        reload();
    }

    private JPanel buildForm() {
    JPanel form = new JPanel(new GridBagLayout());
    GridBagConstraints lc = makeLc(); GridBagConstraints fc = makeFc();

    lc.gridx=0; lc.gridy=0; form.add(makeLabel("Tên sân bay"), lc);
    fc.gridx=1; fc.gridy=0; styleField(txtTen); form.add(txtTen, fc);

    lc.gridx=2; lc.gridy=0; form.add(makeLabel("Thành phố"), lc);
    fc.gridx=3; fc.gridy=0; styleField(txtThanhPho); form.add(txtThanhPho, fc);

    lc.gridx=4; lc.gridy=0; form.add(makeLabel("Quốc gia"), lc);
    fc.gridx=5; fc.gridy=0; styleField(txtQuocGia); form.add(txtQuocGia, fc);

    btnAdd = new JButton("Thêm"); btnUpdate = new JButton("Sửa"); btnDelete = new JButton("Xóa");
    btnReload = new JButton("Làm mới");
btnReload.addActionListener(e -> reloadData());
    btnAdd.addActionListener(e -> add());
    btnUpdate.addActionListener(e -> update());
    btnDelete.addActionListener(e -> delete());

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
        List<SanBayDTO> list = bus.dsSanBay();
        for (SanBayDTO s : list) {
            model.addRow(new Object[]{
                    s.getSanBayId(),
                    s.getTenSanBay(),
                    s.getThanhPho(),
                    s.getQuocGia()
            });
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
        String ten = txtTen.getText().trim();
        String tp = txtThanhPho.getText().trim();
        String qg = txtQuocGia.getText().trim();

        if (ten.isEmpty() || tp.isEmpty() || qg.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không được để trống.");
            return;
        }

        if (!flightbooking.util.Validator.isValidName(ten)) {
            JOptionPane.showMessageDialog(this, "Tên sân bay không hợp lệ.");
            return;
        }

        if (!flightbooking.util.Validator.isValidName(tp)) {
            JOptionPane.showMessageDialog(this, "Thành phố không hợp lệ.");
            return;
        }

        if (!flightbooking.util.Validator.isValidName(qg)) {
            JOptionPane.showMessageDialog(this, "Quốc gia không hợp lệ.");
            return;
        }

        SanBayDTO s = new SanBayDTO();
        s.setTenSanBay(txtTen.getText().trim());
        s.setThanhPho(txtThanhPho.getText().trim());
        s.setQuocGia(txtQuocGia.getText().trim());

        bus.themSanBay(s);
        reload();
    }

    private void update() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        String ten = txtTen.getText().trim();
        String tp = txtThanhPho.getText().trim();
        String qg = txtQuocGia.getText().trim();

        if (ten.isEmpty() || tp.isEmpty() || qg.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không được để trống.");
            return;
        }

        if (!flightbooking.util.Validator.isValidName(ten)) {
            JOptionPane.showMessageDialog(this, "Tên sân bay không hợp lệ.");
            return;
        }

        if (!flightbooking.util.Validator.isValidName(tp)) {
            JOptionPane.showMessageDialog(this, "Thành phố không hợp lệ.");
            return;
        }

        if (!flightbooking.util.Validator.isValidName(qg)) {
            JOptionPane.showMessageDialog(this, "Quốc gia không hợp lệ.");
            return;
        }

        int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));

        SanBayDTO s = new SanBayDTO();
        s.setSanBayId(id);
        s.setTenSanBay(txtTen.getText().trim());
        s.setThanhPho(txtThanhPho.getText().trim());
        s.setQuocGia(txtQuocGia.getText().trim());

        bus.capNhatSanBay(s);
        reload();
    }

    private void delete() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
        bus.xoaSanBay(id);
        reload();
    }

    // Gọi hàm này sau khi load quyền
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
    table.clearSelection();

    txtTen.setText("");
    txtThanhPho.setText("");
    txtQuocGia.setText("");

    revalidate();
    repaint();
}
}
