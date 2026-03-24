package flightbooking.gui.admin.pnl;

import flightbooking.bus.QuanLyVeBUS;
import flightbooking.dto.VeDTO;
import flightbooking.util.ActionConstants;
import flightbooking.util.ExcelExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PnlQuanLyVe extends JPanel {

    private final QuanLyVeBUS bus = new QuanLyVeBUS();
    private PnlDatVeAdmin pnlDatVeAdmin;

    private final JTextField txtChuyenBayId = new JTextField();
    private final JTextField txtHoTen = new JTextField();
    private final JTextField txtSoGiayTo = new JTextField();

    private final JComboBox<String> cbTrangThai =
            new JComboBox<>(new String[]{"Tất cả", "Đang hiệu lực", "Đã hủy"});

    private JButton btnLoc;
    private JButton btnLamMoi;
    private JButton btnHuyVe;
    private JButton btnExport;

    private JTable table;
    private DefaultTableModel model;

    private List<VeDTO> currentData = new ArrayList<>();

    public PnlQuanLyVe() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildTop(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);

        loadData();
    }

    private JComponent buildTop() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints lc = makeLc();
        GridBagConstraints fc = makeFc();

        lc.gridx = 0; lc.gridy = 0;
        form.add(makeLabel("Chuyến bay ID"), lc);
        fc.gridx = 1; fc.gridy = 0;
        styleField(txtChuyenBayId);
        form.add(txtChuyenBayId, fc);

        lc.gridx = 2; lc.gridy = 0;
        form.add(makeLabel("Họ tên"), lc);
        fc.gridx = 3; fc.gridy = 0;
        styleField(txtHoTen);
        form.add(txtHoTen, fc);

        lc.gridx = 4; lc.gridy = 0;
        form.add(makeLabel("Số giấy tờ"), lc);
        fc.gridx = 5; fc.gridy = 0;
        styleField(txtSoGiayTo);
        form.add(txtSoGiayTo, fc);

        lc.gridx = 0; lc.gridy = 1;
        form.add(makeLabel("Trạng thái"), lc);
        fc.gridx = 1; fc.gridy = 1;
        form.add(cbTrangThai, fc);

        btnLoc = new JButton("Lọc");
        btnLoc.addActionListener(e -> loadData());

        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.addActionListener(e -> lamMoi());

        btnHuyVe = new JButton("Hủy vé");
        btnHuyVe.addActionListener(e -> huyVeSelected());

        btnExport = new JButton("Xuất Excel");
        btnExport.addActionListener(e -> ExcelExporter.export(table, this));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        actions.add(btnLoc);
        actions.add(btnLamMoi);
        actions.add(btnHuyVe);
        actions.add(btnExport);

        JPanel wrap = new JPanel(new BorderLayout(0, 8));
        wrap.add(form, BorderLayout.CENTER);
        wrap.add(actions, BorderLayout.SOUTH);

        return wrap;
    }

    private JComponent buildCenter() {
        model = new DefaultTableModel(
                new Object[]{
                        "Vé ID", "Chuyến bay", "Hành khách", "Số giấy tờ",
                        "Ghế", "Hạng ghế", "Giá", "Email KH", "Nhân viên",
                        "Ngày tạo", "Trạng thái"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column
            ) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column
                );

                if (row >= 0 && row < currentData.size()) {
                    VeDTO v = currentData.get(row);

                    if (v.isDaHuy()) {
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

        return new JScrollPane(table);
    }

    private void loadData() {
        try {
            Integer chuyenBayId = null;
            String s = txtChuyenBayId.getText().trim();
            if (!s.isEmpty()) {
                chuyenBayId = Integer.parseInt(s);
            }

            String hoTen = txtHoTen.getText().trim();
            String soGiayTo = txtSoGiayTo.getText().trim();

            Integer trangThai = -1;
            if (cbTrangThai.getSelectedIndex() == 1) trangThai = 1;
            else if (cbTrangThai.getSelectedIndex() == 2) trangThai = 0;

            currentData = bus.timKiem(chuyenBayId, hoTen, soGiayTo, trangThai);

            model.setRowCount(0);
            DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (VeDTO v : currentData) {
                model.addRow(new Object[]{
                        v.getVeId(),
                        v.getChuyenBayId(),
                        v.getHoTenHanhKhach(),
                        v.getSoGiayTo(),
                        v.getTenGhe(),
                        v.getTenHangGhe(),
                        v.getGiaChot(),
                        v.getEmailKhachHang(),
                        v.getTenNhanVien(),
                        v.getThoiDiemTao() != null ? v.getThoiDiemTao().format(f) : "",
                        v.getTrangThaiText()
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi load vé: " + ex.getMessage());
        }
    }

    private void lamMoi() {
        txtChuyenBayId.setText("");
        txtHoTen.setText("");
        txtSoGiayTo.setText("");
        cbTrangThai.setSelectedIndex(0);
        loadData();
    }

    private void huyVeSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn 1 vé để hủy.");
            return;
        }

        VeDTO v = currentData.get(row);
        if (v.isDaHuy()) {
            JOptionPane.showMessageDialog(this, "Vé này đã hủy rồi.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn chắc chắn muốn hủy vé ID " + v.getVeId() + "?",
                "Xác nhận hủy vé",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            bus.huyVe(v.getVeId());
            JOptionPane.showMessageDialog(this, "Hủy vé thành công.");
            loadData();
            if (pnlDatVeAdmin != null) {
    pnlDatVeAdmin.reloadData();
}
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Hủy vé thất bại: " + ex.getMessage());
        }
    }

    public void applyPermissions(List<Integer> actionIds) {
        btnExport.setVisible(actionIds.contains(ActionConstants.XUAT_EXCEL));
        btnHuyVe.setVisible(actionIds.contains(ActionConstants.HUY_VE));
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
    public void setPnlDatVeAdmin(PnlDatVeAdmin pnlDatVeAdmin) {
    this.pnlDatVeAdmin = pnlDatVeAdmin;
}
}