package flightbooking.gui.admin.pnl;

import flightbooking.bus.ChuyenBayBUS;
import flightbooking.bus.DatVeBUS;
import flightbooking.bus.ThongTinVeBUS;
import flightbooking.dto.ChuyenBayDTO;

import flightbooking.dto.HanhKhachDTO;
import flightbooking.dto.ThongTinVeDTO;
import flightbooking.util.ActionConstants;
import flightbooking.util.ExcelExporter;
import flightbooking.util.ExcelImporter;
import flightbooking.util.SessionContext;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class PnlDatVeAdmin extends JPanel {

    private final DatVeBUS datVeBUS = new DatVeBUS();
    private final ChuyenBayBUS chuyenBayBUS = new ChuyenBayBUS();
    private final ThongTinVeBUS thongTinVeBUS = new ThongTinVeBUS();

    private JButton btnTaoVe;
    private JButton btnExport;
    private JButton btnImport;

    // ✅ combo thay vì nhập ID
    private final JComboBox<ChuyenItem> cbChuyen = new JComboBox<>();
    private JButton btnChonGhe;
private JLabel lblGheDaChon = new JLabel("Chưa chọn ghế");
private Integer gheIdDaChon = null;
private JTable tableVe;
private DefaultTableModel modelVe;

    private final JTextField txtHoTen = new JTextField();
    private final JTextField txtSoGiayTo = new JTextField();

    private final JComboBox<String> cbPay = new JComboBox<>(new String[]{"cash", "card"});
    private List<ThongTinVeDTO> currentVeData = new ArrayList<>();

    public PnlDatVeAdmin() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildForm(), BorderLayout.NORTH);
        add(buildTableVe(), BorderLayout.CENTER);

        loadChuyenBay();
    }

    private JComponent buildTableVe() {
    modelVe = new DefaultTableModel(
        new Object[]{"Chuyến", "Hành khách", "Giấy tờ", "Tuyến", "Ghế", "Hạng", "Giá"}, 0
    ) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };

    tableVe = new JTable(modelVe);
    tableVe.setRowHeight(28);
    tableVe.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    tableVe.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column
        ) {
            Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column
            );

            if (row >= 0 && row < currentVeData.size()) {
                ThongTinVeDTO t = currentVeData.get(row);

                if (t.isDaHuy()) {
                    if (isSelected) {
                        c.setBackground(new Color(80, 80, 80));
                        c.setForeground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(45, 45, 45));
                        c.setForeground(Color.WHITE);
                    }
                } else {
                    if (isSelected) {
                        c.setBackground(table.getSelectionBackground());
                        c.setForeground(table.getSelectionForeground());
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                }
            }

            return c;
        }
    });

    loadVeNhanVien();

    return new JScrollPane(tableVe);
}

private void loadVeNhanVien() {
    modelVe.setRowCount(0);

    Integer adminId = SessionContext.getAdminTaiKhoanId();
    if (adminId == null || adminId == 0) {
        currentVeData = new ArrayList<>();
        return;
    }

    currentVeData = thongTinVeBUS.getByNhanVien(adminId);

    for (ThongTinVeDTO t : currentVeData) {
        String tuyen = t.getSanBayDi() + " → " + t.getSanBayDen();

        modelVe.addRow(new Object[]{
            "CB#" + t.getChuyenBayId(),
            t.getHoTen(),
            t.getSoGiayTo(),
            tuyen,
            t.getTenGhe(),
            t.getHangGhe(),
            t.getGia()
        });
    }
}

    private JPanel buildForm() {
    JPanel form = new JPanel(new GridBagLayout());
    GridBagConstraints lc = makeLc(); GridBagConstraints fc = makeFc();

    lc.gridx=0; lc.gridy=0; form.add(makeLabel("Chuyến bay"), lc);
    fc.gridx=1; fc.gridy=0; form.add(cbChuyen, fc);

    lc.gridx=2; lc.gridy=0;
form.add(makeLabel("Ghế"), lc);

btnChonGhe = new JButton("Chọn ghế");
btnChonGhe.addActionListener(e -> openSeatMapPopup());

JPanel ghePanel = new JPanel(new BorderLayout());
ghePanel.add(btnChonGhe, BorderLayout.WEST);
ghePanel.add(lblGheDaChon, BorderLayout.CENTER);

fc.gridx=3; fc.gridy=0;
form.add(ghePanel, fc);

    lc.gridx=0; lc.gridy=1; form.add(makeLabel("Họ tên hành khách"), lc);
    fc.gridx=1; fc.gridy=1; styleField(txtHoTen); form.add(txtHoTen, fc);

    lc.gridx=2; lc.gridy=1; form.add(makeLabel("Số giấy tờ"), lc);
    fc.gridx=3; fc.gridy=1; styleField(txtSoGiayTo); form.add(txtSoGiayTo, fc);

    lc.gridx=4; lc.gridy=1; form.add(makeLabel("Thanh toán"), lc);
    fc.gridx=5; fc.gridy=1; form.add(cbPay, fc);

    btnExport = new JButton("Xuất Excel");
btnExport.addActionListener(e -> {
    ExcelExporter.export(tableVe, this);
});

btnImport = new JButton("Nhập Excel");
btnImport.addActionListener(e -> {
    ExcelImporter.importToTable(tableVe, this);
});

    btnTaoVe = new JButton("Tạo vé");
    btnTaoVe.addActionListener(e -> taoVe());

    return wrapWithActions(form, btnTaoVe, btnExport, btnImport);
}

    private void loadChuyenBay() {
        cbChuyen.removeAllItems();
        List<ChuyenBayDTO> list = chuyenBayBUS.dsChuyenBay();
        for (ChuyenBayDTO c : list) {
            String tuyen =
                    (c.getSanBayDiTen() != null && c.getSanBayDenTen() != null)
                            ? (c.getSanBayDiTen() + " → " + c.getSanBayDenTen())
                            : ("Tuyến #" + c.getTuyenBayId());
            String text = "CB#" + c.getChuyenBayId() + " • " + tuyen + " • " + c.getGioKhoiHanh();
            cbChuyen.addItem(new ChuyenItem(c.getChuyenBayId(), text));
        }
        
    }

    private void openSeatMapPopup() {
    ChuyenItem cb = (ChuyenItem) cbChuyen.getSelectedItem();
    if (cb == null) {
        JOptionPane.showMessageDialog(this, "Chọn chuyến bay trước.");
        return;
    }

    int chuyenBayId = cb.id;

    JDialog dialog = new JDialog(
            SwingUtilities.getWindowAncestor(this),
            "Chọn ghế",
            Dialog.ModalityType.APPLICATION_MODAL
    );

    dialog.setSize(1000, 600);
    dialog.setLocationRelativeTo(this);

    AdminSeatMapPanel panel = new AdminSeatMapPanel(chuyenBayId);

    JButton btnOK = new JButton("Xác nhận");

    btnOK.addActionListener(e -> {
        if (panel.gheDangChon == null) {
            JOptionPane.showMessageDialog(dialog, "Chưa chọn ghế.");
            return;
        }

        gheIdDaChon = panel.gheDangChon;
        lblGheDaChon.setText("Đã chọn: " + panel.gheText);

        dialog.dispose();
    });

    dialog.setLayout(new BorderLayout());
    dialog.add(panel, BorderLayout.CENTER);

    JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    bottom.add(btnOK);

    dialog.add(bottom, BorderLayout.SOUTH);

    dialog.setVisible(true);
}

    private void taoVe() {
        ChuyenItem cb = (ChuyenItem) cbChuyen.getSelectedItem();
        if (gheIdDaChon == null) {
    JOptionPane.showMessageDialog(this, "Chưa chọn ghế.");
    return;
}
int gheId = gheIdDaChon;
        String ten = txtHoTen.getText().trim();
        String giayto = txtSoGiayTo.getText().trim();

        if (cb == null || ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến, chọn ghế, nhập họ tên.");
            return;
        }

        try {
            int chuyenBayId = cb.id;
            

            DatVeBUS.ThongTinHanhKhachVaGhe item = new DatVeBUS.ThongTinHanhKhachVaGhe();
            HanhKhachDTO hk = new HanhKhachDTO();
            hk.setHoTen(ten);
            hk.setSoGiayTo(giayto);

            item.setHanhKhach(hk);
            item.setGheId(gheId);

            List<DatVeBUS.ThongTinHanhKhachVaGhe> items = new ArrayList<>();
            items.add(item);

            // ✅ FIX: Lấy ID admin từ SessionContext (đã được set khi login)
            
            Integer taiKhoanNhanVienId = SessionContext.getAdminTaiKhoanId();
            if (taiKhoanNhanVienId == null || taiKhoanNhanVienId == 0) {
    throw new RuntimeException("Admin chưa đăng nhập.");
}
            
            // Nếu có admin session, bạn có thể lấy ID nhân viên từ username
            // Nhưng hiện tại để null (vì không có method lấy ID từ username trong SessionContext)
            

            datVeBUS.datVe(
    null,
    taiKhoanNhanVienId,  // ✔ nhân viên
    chuyenBayId,
    items,
    (String) cbPay.getSelectedItem(),
    0
);

            JOptionPane.showMessageDialog(this, "✓ Tạo vé thành công!");
            clearForm();
            repaint();
             // refresh vì ghế vừa đặt xong
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Không tạo được vé: " + ex.getMessage());
        }
    }

    // ✅ Clear form sau khi đặt vé thành công
    private void clearForm() {
        txtHoTen.setText("");
        txtSoGiayTo.setText("");
        cbPay.setSelectedIndex(0);

        gheIdDaChon = null;
    lblGheDaChon.setText("Chưa chọn ghế");
    }

    private static class ChuyenItem {
        final int id;
        final String text;

        ChuyenItem(int id, String text) {
            this.id = id;
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    
    public void applyPermissions(List<Integer> actionIds) {
    btnExport.setVisible(actionIds.contains(ActionConstants.XUAT_EXCEL));
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
    loadVeNhanVien();
    revalidate();
    repaint();
}
}