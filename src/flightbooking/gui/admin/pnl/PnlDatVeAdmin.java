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
import java.time.LocalDateTime;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import flightbooking.util.ValidationUtil;
import flightbooking.gui.admin.theme.AdminTheme;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class PnlDatVeAdmin extends JPanel {

    private final DatVeBUS datVeBUS = new DatVeBUS();
    private final ChuyenBayBUS chuyenBayBUS = new ChuyenBayBUS();
    private final ThongTinVeBUS thongTinVeBUS = new ThongTinVeBUS();

    // ✅ Dùng createActionButton thay vì new JButton + styleActionButton
    private JButton btnTaoVe;
    private JButton btnExport;
    private JButton btnImport;
    private JButton btnReload;
    private JButton btnChonGhe;

    private final JComboBox<ChuyenItem> cbChuyen = new JComboBox<>();
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
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

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

        AdminTheme.styleTable(tableVe, true);
        JScrollPane sp = AdminTheme.wrapTable(tableVe);
        loadVeNhanVien();
        return sp;
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
        form.setOpaque(false);

        // ✅ Tạo nút bằng createActionButton — paintComponent không bị LAF override
        btnReload  = AdminTheme.createActionButton("Làm mới",    AdminTheme.ButtonRole.NEUTRAL);
        btnTaoVe   = AdminTheme.createActionButton("Tạo vé",     AdminTheme.ButtonRole.NEUTRAL);
        btnExport  = AdminTheme.createActionButton("Xuất Excel", AdminTheme.ButtonRole.NEUTRAL);
        btnImport  = AdminTheme.createActionButton("Nhập Excel", AdminTheme.ButtonRole.NEUTRAL);
        btnChonGhe = AdminTheme.createActionButton("Chọn ghế",   AdminTheme.ButtonRole.NEUTRAL);

        AdminTheme.styleSoftComboBox(cbChuyen);
        AdminTheme.styleSoftComboBox(cbPay);
        AdminTheme.styleSoftTextField(txtHoTen);
        AdminTheme.styleSoftTextField(txtSoGiayTo);

        GridBagConstraints lc = makeLc();
        GridBagConstraints fc = makeFc();

        lc.gridx = 0; lc.gridy = 0; form.add(makeLabel("Chuyến bay"), lc);
        fc.gridx = 1; fc.gridy = 0; form.add(cbChuyen, fc);

        lc.gridx = 2; lc.gridy = 0; form.add(makeLabel("Ghế"), lc);

        btnChonGhe.addActionListener(e -> openSeatMapPopup());

        JPanel ghePanel = new JPanel(new BorderLayout(8, 0));
        ghePanel.setOpaque(false);
        ghePanel.add(btnChonGhe, BorderLayout.WEST);
        ghePanel.add(lblGheDaChon, BorderLayout.CENTER);

        fc.gridx = 3; fc.gridy = 0; form.add(ghePanel, fc);

        lc.gridx = 0; lc.gridy = 1; form.add(makeLabel("Họ tên hành khách"), lc);
        fc.gridx = 1; fc.gridy = 1; form.add(txtHoTen, fc);

        lc.gridx = 2; lc.gridy = 1; form.add(makeLabel("Số giấy tờ"), lc);
        fc.gridx = 3; fc.gridy = 1; form.add(txtSoGiayTo, fc);

        lc.gridx = 4; lc.gridy = 1; form.add(makeLabel("Thanh toán"), lc);
        fc.gridx = 5; fc.gridy = 1; form.add(cbPay, fc);

        btnReload.addActionListener(e -> reloadData());
        btnExport.addActionListener(e -> ExcelExporter.export(tableVe, this));
        btnImport.addActionListener(e -> ExcelImporter.importToTable(tableVe, this));
        btnTaoVe.addActionListener(e -> taoVe());

        return AdminTheme.wrapFormCard(form, btnReload, btnTaoVe, btnExport, btnImport);
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
        if (isChuyenBayDaKhoiHanh()) {
            JOptionPane.showMessageDialog(this, "Chuyến bay đã khởi hành.");
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

        JButton btnOK = AdminTheme.createActionButton("Xác nhận", AdminTheme.ButtonRole.NEUTRAL);
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

        if (cb == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến bay.");
            return;
        }
        if (isChuyenBayDaKhoiHanh()) {
            JOptionPane.showMessageDialog(this, "Chuyến bay đã khởi hành.");
            return;
        }
        if (gheIdDaChon == null) {
            JOptionPane.showMessageDialog(this, "Chưa chọn ghế.");
            return;
        }

        int gheId = gheIdDaChon;
        String ten = txtHoTen.getText().trim();
        String giayto = txtSoGiayTo.getText().trim();

        try {
            ValidationUtil.validateName(ten, "Họ tên hành khách");
            if (giayto == null || giayto.trim().isEmpty())
                throw new RuntimeException("Giấy tờ không được để trống.");
            if (!giayto.matches("^(\\d{9}|\\d{12})$"))
                throw new RuntimeException("CMND/CCCD phải gồm 9 hoặc 12 chữ số.");

            int chuyenBayId = cb.id;

            DatVeBUS.ThongTinHanhKhachVaGhe item = new DatVeBUS.ThongTinHanhKhachVaGhe();
            HanhKhachDTO hk = new HanhKhachDTO();
            hk.setHoTen(ten);
            hk.setSoGiayTo(giayto);
            item.setHanhKhach(hk);
            item.setGheId(gheId);

            List<DatVeBUS.ThongTinHanhKhachVaGhe> items = new ArrayList<>();
            items.add(item);

            Integer taiKhoanNhanVienId = SessionContext.getAdminTaiKhoanId();
            if (taiKhoanNhanVienId == null || taiKhoanNhanVienId == 0) {
                throw new RuntimeException("Admin chưa đăng nhập.");
            }

            datVeBUS.datVe(
                    null,
                    taiKhoanNhanVienId,
                    chuyenBayId,
                    items,
                    (String) cbPay.getSelectedItem(),
                    0
            );

            JOptionPane.showMessageDialog(this, "✓ Tạo vé thành công!");
            clearForm();
            loadVeNhanVien();
            repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Không tạo được vé: " + ex.getMessage());
        }
    }

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
        btnTaoVe.setVisible(actionIds.contains(ActionConstants.TAO_VE));
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

    public void reloadData() {
        loadChuyenBay();
        loadVeNhanVien();
        clearForm();
        tableVe.clearSelection();
        revalidate();
        repaint();
    }

    private ChuyenBayDTO getSelectedChuyenBay() {
        ChuyenItem cb = (ChuyenItem) cbChuyen.getSelectedItem();
        if (cb == null) return null;
        List<ChuyenBayDTO> list = chuyenBayBUS.dsChuyenBay();
        for (ChuyenBayDTO c : list) {
            if (c.getChuyenBayId() == cb.id) return c;
        }
        return null;
    }

    private boolean isChuyenBayDaKhoiHanh() {
        ChuyenBayDTO c = getSelectedChuyenBay();
        if (c == null || c.getGioKhoiHanh() == null) return false;
        return !c.getGioKhoiHanh().isAfter(LocalDateTime.now());
    }
}