package flightbooking.gui.admin.pnl;

import flightbooking.bus.QuanLyVeBUS;
import flightbooking.dto.VeDTO;
import flightbooking.gui.admin.theme.AdminTheme;
import flightbooking.util.ActionConstants;
import flightbooking.util.ExcelExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import flightbooking.util.ValidationUtil;
import java.time.LocalDate;

public class PnlQuanLyVe extends JPanel {

    private final QuanLyVeBUS bus = new QuanLyVeBUS();
    private PnlDatVeAdmin pnlDatVeAdmin;

    private final JTextField txtChuyenBayId = new JTextField();
    private final JTextField txtHoTen = new JTextField();
    private final JTextField txtSoGiayTo = new JTextField();
    private final JComboBox<String> cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đang hiệu lực", "Đã hủy"});

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
        form.setOpaque(false);

        AdminTheme.styleSoftTextField(txtChuyenBayId);
        AdminTheme.styleSoftTextField(txtHoTen);
        AdminTheme.styleSoftTextField(txtSoGiayTo);
        AdminTheme.styleSoftComboBox(cbTrangThai);

        GridBagConstraints lc = makeLc(); GridBagConstraints fc = makeFc();

        lc.gridx = 0; lc.gridy = 0; form.add(makeLabel("Chuyến bay ID"), lc);
        fc.gridx = 1; fc.gridy = 0; form.add(txtChuyenBayId, fc);
        lc.gridx = 2; lc.gridy = 0; form.add(makeLabel("Họ tên"), lc);
        fc.gridx = 3; fc.gridy = 0; form.add(txtHoTen, fc);
        lc.gridx = 4; lc.gridy = 0; form.add(makeLabel("Số giấy tờ"), lc);
        fc.gridx = 5; fc.gridy = 0; form.add(txtSoGiayTo, fc);
        lc.gridx = 0; lc.gridy = 1; form.add(makeLabel("Trạng thái"), lc);
        fc.gridx = 1; fc.gridy = 1; form.add(cbTrangThai, fc);

        // ✅ Dùng createActionButton
        btnLoc    = AdminTheme.createActionButton("Lọc",        AdminTheme.ButtonRole.NEUTRAL);
        btnLamMoi = AdminTheme.createActionButton("Làm mới",    AdminTheme.ButtonRole.NEUTRAL);
        btnHuyVe  = AdminTheme.createActionButton("Hủy vé",     AdminTheme.ButtonRole.NEUTRAL);
        btnExport = AdminTheme.createActionButton("Xuất Excel", AdminTheme.ButtonRole.NEUTRAL);

        btnLoc.addActionListener(e -> loadData());
        btnLamMoi.addActionListener(e -> lamMoi());
        btnHuyVe.addActionListener(e -> huyVeSelected());
        btnExport.addActionListener(e -> ExcelExporter.export(table, this));

        return AdminTheme.wrapFormCard(form, btnLoc, btnLamMoi, btnHuyVe, btnExport);
    }

    private JComponent buildCenter() {
        model = new DefaultTableModel(
                new Object[]{"Vé ID", "Chuyến bay", "Hành khách", "Số giấy tờ",
                        "Ghế", "Hạng ghế", "Giá", "Email KH", "Nhân viên", "Ngày tạo", "Trạng thái"}, 0
        ) { @Override public boolean isCellEditable(int row, int column) { return false; } };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row >= 0 && row < currentData.size()) {
                    VeDTO v = currentData.get(row);
                    if (v.isDaHuy()) {
                        c.setBackground(isSelected ? new Color(80, 80, 80) : new Color(45, 45, 45));
                        c.setForeground(Color.WHITE);
                    } else {
                        if (isSelected) { c.setBackground(table.getSelectionBackground()); c.setForeground(table.getSelectionForeground()); }
                        else { c.setBackground(Color.WHITE); c.setForeground(Color.BLACK); }
                    }
                }
                return c;
            }
        });

        AdminTheme.styleTable(table, true);
        return AdminTheme.wrapTable(table);
    }

    private void loadData() {
        try {
            Integer chuyenBayId = null;
            String s = txtChuyenBayId.getText().trim();
            if (!s.isEmpty()) { chuyenBayId = Integer.parseInt(s); ValidationUtil.validatePositiveInt(chuyenBayId, "Chuyến bay ID"); }
            String hoTen = txtHoTen.getText().trim(); String soGiayTo = txtSoGiayTo.getText().trim();
            if (!hoTen.isEmpty()) ValidationUtil.validateName(hoTen, "Họ tên");
            if (!soGiayTo.isEmpty()) ValidationUtil.validateDocument(soGiayTo);
            Integer trangThai = -1;
            if (cbTrangThai.getSelectedIndex() == 1) trangThai = 1;
            else if (cbTrangThai.getSelectedIndex() == 2) trangThai = 0;
            currentData = bus.timKiem(chuyenBayId, hoTen, soGiayTo, trangThai);
            model.setRowCount(0);
            DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (VeDTO v : currentData) {
                model.addRow(new Object[]{ v.getVeId(), v.getChuyenBayId(), v.getHoTenHanhKhach(), v.getSoGiayTo(),
                        v.getTenGhe(), v.getTenHangGhe(), v.getGiaChot(), v.getEmailKhachHang(),
                        v.getTenNhanVien(), v.getThoiDiemTao() != null ? v.getThoiDiemTao().format(f) : "", v.getTrangThaiText() });
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi load vé: " + ex.getMessage()); }
    }

    private void lamMoi() {
        txtChuyenBayId.setText(""); txtHoTen.setText(""); txtSoGiayTo.setText("");
        cbTrangThai.setSelectedIndex(0); loadData();
    }

    private void huyVeSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn 1 vé để hủy."); return; }
        VeDTO v = currentData.get(row);
        if (v.isDaHuy()) { JOptionPane.showMessageDialog(this, "Vé này đã hủy rồi."); return; }
        try {
            if (v.getThoiDiemTao() != null) {
                LocalDate ngayTao = v.getThoiDiemTao().toLocalDate();
                if (ngayTao.isBefore(LocalDate.now())) throw new RuntimeException("Không thể hủy vé cũ hơn ngày hôm nay.");
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn chắc chắn muốn hủy vé ID " + v.getVeId() + "?", "Xác nhận hủy vé", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            bus.huyVe(v.getVeId());
            JOptionPane.showMessageDialog(this, "Hủy vé thành công.");
            loadData();
            if (pnlDatVeAdmin != null) pnlDatVeAdmin.reloadData();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Hủy vé thất bại: " + ex.getMessage()); }
    }

    public void applyPermissions(List<Integer> actionIds) {
        btnHuyVe.setVisible(actionIds.contains(ActionConstants.HUY_VE));
        btnExport.setVisible(actionIds.contains(ActionConstants.XUAT_EXCEL));
        revalidate(); repaint();
    }

    public void setPnlDatVeAdmin(PnlDatVeAdmin pnlDatVeAdmin) { this.pnlDatVeAdmin = pnlDatVeAdmin; }

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
}