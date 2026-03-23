package flightbooking.gui.user;

import flightbooking.bus.ThongTinVeBUS;
import flightbooking.dto.ThongTinVeDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class FrmMyTickets extends JFrame {

    private DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Tuyến", "Ngày", "Ghế"}, 0
    );

    private JTable table = new JTable(model);

    private List<ThongTinVeDTO> data; // 🔥 giữ lại để dùng

    public FrmMyTickets(int khachHangId) {
table.addMouseListener(new java.awt.event.MouseAdapter() {
    public void mouseClicked(java.awt.event.MouseEvent e) {
        if (e.getClickCount() == 2) {
            showDetail();
        }
    }
});table.setDefaultEditor(Object.class, null);
        setTitle("Vé của tôi");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildActions(), BorderLayout.SOUTH);

        loadData(khachHangId);
    }

    private void loadData(int khachHangId) {

        ThongTinVeBUS bus = new ThongTinVeBUS();
        data = bus.getSimpleByKhachHang(khachHangId);

        model.setRowCount(0);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

for (ThongTinVeDTO t : data) {

    model.addRow(new Object[]{
            t.getSanBayDi() + " → " + t.getSanBayDen(),
            t.getGioKhoiHanh().format(fmt),
            t.getTenGhe()
    });
}
    }

    private JPanel buildActions() {

        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnDetail = new JButton("Xem chi tiết");

        btnDetail.addActionListener(e -> showDetail());

        p.add(btnDetail);
        return p;
    }
private String formatTien(BigDecimal v) {
    if (v == null) return "0";
    return String.format("%,d", v.longValue());
}
    private void showDetail() {

        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn 1 vé trước");
            return;
        }

        ThongTinVeDTO t = data.get(row);

        // 🔥 popup detail
        JOptionPane.showMessageDialog(this,
                "===== CHI TIẾT VÉ =====\n\n" +
                "Hành khách: " + t.getHoTen() + "\n" +
                "Giấy tờ: " + t.getSoGiayTo() + "\n\n" +
                "Tuyến: " + t.getSanBayDi() + " → " + t.getSanBayDen() + "\n" +
                "Khởi hành: " + t.getGioKhoiHanh() + "\n\n" +
                
                "Ghế: " + t.getTenGhe() + "\n" +
                "Hạng: " + t.getHangGhe() +
                "💰 Giá: " + formatTien(t.getGia()) + " VND"
        );
    }
}