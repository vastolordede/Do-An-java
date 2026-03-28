package flightbooking.gui.admin.pnl;

import flightbooking.bus.SanBayBUS;
import flightbooking.bus.TuyenBayBUS;
import flightbooking.dto.SanBayDTO;
import flightbooking.dto.TuyenBayDTO;
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
            new Object[]{"ID", "Sân bay đi", "Sân bay đến", "Số dặm"}, 0
    );
    private final JTable table = new JTable(model);

    private final JComboBox<Item> cbSanBayDi = new JComboBox<>();
    private final JComboBox<Item> cbSanBayDen = new JComboBox<>();
    private final JSpinner spSoDam = new JSpinner(new SpinnerNumberModel(0, 0, 100000, 10));

    public PnlQuanLyTuyenBay() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildForm(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadSanBayToCombo();
        reload();

        table.getSelectionModel().addListSelectionListener(e -> fillFormFromSelectedRow());
    }

    private JPanel buildForm() {
    JPanel form = new JPanel(new GridBagLayout());
    GridBagConstraints lc = makeLc(); GridBagConstraints fc = makeFc();

    lc.gridx=0; lc.gridy=0; form.add(makeLabel("Sân bay đi"), lc);
    fc.gridx=1; fc.gridy=0; form.add(cbSanBayDi, fc);

    lc.gridx=2; lc.gridy=0; form.add(makeLabel("Sân bay đến"), lc);
    fc.gridx=3; fc.gridy=0; form.add(cbSanBayDen, fc);

    lc.gridx=4; lc.gridy=0; form.add(makeLabel("Số dặm"), lc);
    fc.gridx=5; fc.gridy=0; form.add(spSoDam, fc);

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

    private void loadSanBayToCombo() {
        cbSanBayDi.removeAllItems();
        cbSanBayDen.removeAllItems();
        List<SanBayDTO> list = sanBayBUS.dsSanBay();
        for (SanBayDTO s : list) {
            cbSanBayDi.addItem(new Item(s.getSanBayId(), s.getTenSanBay()));
            cbSanBayDen.addItem(new Item(s.getSanBayId(), s.getTenSanBay()));
        }
    }

    private void reload() {
        model.setRowCount(0);
        List<TuyenBayDTO> list = tuyenBayBUS.dsTuyenBay();
        for (TuyenBayDTO t : list) {
            String tenDi = findTenSanBay(t.getSanBayDiId());
            String tenDen = findTenSanBay(t.getSanBayDenId());
            model.addRow(new Object[]{t.getTuyenBayId(), tenDi, tenDen, t.getSoDam()});
        }
    }

    private String findTenSanBay(Integer id) {
        if (id == null) return "N/A";
        for (SanBayDTO s : sanBayBUS.dsSanBay()) {
            if (s.getSanBayId() == id) return s.getTenSanBay();
        }
        return "ID=" + id;
    }

    private void fillFormFromSelectedRow() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        String tenDi = String.valueOf(model.getValueAt(row, 1));
        String tenDen = String.valueOf(model.getValueAt(row, 2));
        int soDam = Integer.parseInt(String.valueOf(model.getValueAt(row, 3)));

        selectComboByText(cbSanBayDi, tenDi);
        selectComboByText(cbSanBayDen, tenDen);
        spSoDam.setValue(soDam);
    }

    private void selectComboByText(JComboBox<Item> cb, String text) {
        for (int i = 0; i < cb.getItemCount(); i++) {
            Item it = cb.getItemAt(i);
            if (it != null && it.text.equals(text)) {
                cb.setSelectedIndex(i);
                return;
            }
        }
    }

    private void add() {
        Item di = (Item) cbSanBayDi.getSelectedItem();
        Item den = (Item) cbSanBayDen.getSelectedItem();
        if (di == null || den == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sân bay.");
            return;
        }

        if (di.id == den.id) {
            JOptionPane.showMessageDialog(this, "Sân bay đi và đến không được trùng nhau.");
            return;
        }

        int soDam = (int) spSoDam.getValue();
        if (soDam <= 0) {
            JOptionPane.showMessageDialog(this, "Số dặm phải lớn hơn 0.");
            return;
        }

        TuyenBayDTO t = new TuyenBayDTO();
        t.setSanBayDiId(di.id);
        t.setSanBayDenId(den.id);
        t.setSoDam((int) spSoDam.getValue());

        try {
            tuyenBayBUS.themTuyenBay(t);
            reload();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void update() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        Item di = (Item) cbSanBayDi.getSelectedItem();
        Item den = (Item) cbSanBayDen.getSelectedItem();
        if (di == null || den == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sân bay.");
            return;
        }

        int soDam = (int) spSoDam.getValue();
        if (soDam <= 0) {
            JOptionPane.showMessageDialog(this, "Số dặm phải lớn hơn 0.");
            return;
        }

        int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));

        TuyenBayDTO t = new TuyenBayDTO();
        t.setTuyenBayId(id);
        t.setSanBayDiId(di.id);
        t.setSanBayDenId(den.id);
        t.setSoDam((int) spSoDam.getValue());

        try {
            tuyenBayBUS.capNhatTuyenBay(t);
            reload();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void delete() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
        tuyenBayBUS.xoaTuyenBay(id);
        reload();
    }

    private static class Item {
        final int id;
        final String text;

        Item(int id, String text) {
            this.id = id;
            this.text = text;
        }

        @Override
        public String toString() { return text; }
    }

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
    loadSanBayToCombo();
    reload();
    table.clearSelection();

    if (cbSanBayDi.getItemCount() > 0) cbSanBayDi.setSelectedIndex(0);
    if (cbSanBayDen.getItemCount() > 0) cbSanBayDen.setSelectedIndex(0);

    revalidate();
    repaint();
}
}
