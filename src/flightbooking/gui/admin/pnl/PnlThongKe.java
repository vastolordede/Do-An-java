package flightbooking.gui.admin.pnl;

import flightbooking.bus.ThongKeBUS;
import flightbooking.dto.ThongKeChuyenBayDTO;
import flightbooking.dto.ThongKeTongQuanDTO;
import flightbooking.gui.admin.theme.AdminTheme;
import flightbooking.util.ActionConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class PnlThongKe extends JPanel {

    private final ThongKeBUS bus = new ThongKeBUS();

    private final JSpinner spFromDate = new JSpinner(new SpinnerDateModel());
    private final JSpinner spToDate = new JSpinner(new SpinnerDateModel());

    private final JLabel lblTongDoanhThu = new JLabel("0");
    private final JLabel lblTongVeHieuLuc = new JLabel("0");
    private final JLabel lblTongVeHuy = new JLabel("0");
    private final JLabel lblDoanhThuTB = new JLabel("0");

    private final DefaultTableModel modelChuyenBay = new DefaultTableModel(
            new Object[]{"Chuyến bay", "Tuyến bay", "Giờ khởi hành", "Vé đã bán", "Vé hủy", "Doanh thu", "Tỷ lệ hủy"}, 0
    ) { @Override public boolean isCellEditable(int row, int column) { return false; } };
    private final JTable tblChuyenBay = new JTable(modelChuyenBay);

    // ✅ Dùng createActionButton
    private final JButton btnLoc      = AdminTheme.createActionButton("Lọc",        AdminTheme.ButtonRole.NEUTRAL);
    private final JButton btnLamMoi   = AdminTheme.createActionButton("Làm mới",    AdminTheme.ButtonRole.NEUTRAL);
    private final JButton btnXuatExcel = AdminTheme.createActionButton("Xuất Excel", AdminTheme.ButtonRole.NEUTRAL);

    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0");

    public PnlThongKe() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initDateSpinner();
        add(buildTop(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        btnLoc.addActionListener(e -> loadData());
        btnLamMoi.addActionListener(e -> lamMoi());
        loadData();
    }

    private void initDateSpinner() {
        spFromDate.setEditor(new JSpinner.DateEditor(spFromDate, "yyyy-MM-dd"));
        spToDate.setEditor(new JSpinner.DateEditor(spToDate, "yyyy-MM-dd"));
        spFromDate.setPreferredSize(new Dimension(120, 30));
        spToDate.setPreferredSize(new Dimension(120, 30));
        Date now = new Date();
        spFromDate.setValue(now); spToDate.setValue(now);
    }

    private JPanel buildTop() {
        JPanel root = new JPanel(new BorderLayout(0, 10));

        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filter.add(new JLabel("Từ ngày:")); filter.add(spFromDate);
        filter.add(new JLabel("Đến ngày:")); filter.add(spToDate);
        filter.add(btnLoc); filter.add(btnLamMoi); filter.add(btnXuatExcel);

        JPanel cards = new JPanel(new GridLayout(1, 4, 10, 10));
        cards.add(createCard("Tổng doanh thu", lblTongDoanhThu));
        cards.add(createCard("Vé đã bán", lblTongVeHieuLuc));
        cards.add(createCard("Vé đã hủy", lblTongVeHuy));
        cards.add(createCard("Doanh thu TB / vé", lblDoanhThuTB));

        root.add(filter, BorderLayout.NORTH);
        root.add(cards, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildCenter() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JScrollPane spMain = new JScrollPane(tblChuyenBay);
        spMain.setBorder(BorderFactory.createTitledBorder("Doanh thu theo chuyến bay"));
        panel.add(spMain, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCard(String title, JLabel valueLabel) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 14f));
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 18f));
        p.add(lblTitle, BorderLayout.NORTH);
        p.add(valueLabel, BorderLayout.CENTER);
        return p;
    }

    private void loadData() {
        try {
            LocalDate fromDate = getFromDate(); LocalDate toDate = getToDate();
            if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
                JOptionPane.showMessageDialog(this, "Từ ngày không được lớn hơn Đến ngày"); return;
            }
            setTongQuan(bus.getTongQuan(fromDate, toDate, null, null));
            fillTableChuyenBay(bus.getDoanhThuTheoChuyenBay(fromDate, toDate, null));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu thống kê:\n" + e.getMessage());
        }
    }

    private void setTongQuan(ThongKeTongQuanDTO dto) {
        if (dto == null) {
            lblTongDoanhThu.setText("0 VNĐ"); lblTongVeHieuLuc.setText("0");
            lblTongVeHuy.setText("0"); lblDoanhThuTB.setText("0 VNĐ"); return;
        }
        lblTongDoanhThu.setText(formatMoney(dto.getTongDoanhThu()) + " VNĐ");
        lblTongVeHieuLuc.setText(String.valueOf(dto.getTongVeHieuLuc()));
        lblTongVeHuy.setText(String.valueOf(dto.getTongVeHuy()));
        lblDoanhThuTB.setText(formatMoney(dto.getDoanhThuTrungBinhVe()) + " VNĐ");
    }

    private void fillTableChuyenBay(List<ThongKeChuyenBayDTO> list) {
        modelChuyenBay.setRowCount(0);
        if (list == null) return;
        for (ThongKeChuyenBayDTO x : list) {
            modelChuyenBay.addRow(new Object[]{ x.getChuyenBayId(), x.getTuyenBayText(), x.getGioKhoiHanh(),
                    x.getVeHieuLuc(), x.getVeHuy(), formatMoney(x.getDoanhThu()), String.format("%.2f%%", x.getTyLeHuy()) });
        }
    }

    private String formatMoney(BigDecimal value) { return value == null ? "0" : moneyFormat.format(value); }

    private LocalDate getFromDate() { return toLocalDate((Date) spFromDate.getValue()); }
    private LocalDate getToDate()   { return toLocalDate((Date) spToDate.getValue()); }

    private LocalDate toLocalDate(Date date) {
        if (date == null) return null;
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void lamMoi() { Date now = new Date(); spFromDate.setValue(now); spToDate.setValue(now); loadData(); }

    public void applyPermissions(List<Integer> actionIds) {
        btnXuatExcel.setVisible(actionIds.contains(ActionConstants.XUAT_EXCEL));
        revalidate(); repaint();
    }
}