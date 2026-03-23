package flightbooking.gui.user.pnl;

import flightbooking.bus.DatVeBUS;
import flightbooking.bus.KhachHangBUS;
import flightbooking.bus.ThongTinVeBUS;
import flightbooking.dto.KhachHangDTO;
import flightbooking.dto.ThongTinVeDTO;
import flightbooking.gui.user.common.AppNavigator;
import flightbooking.gui.user.common.TempVeStore;
import flightbooking.gui.user.theme.UserTheme;
import flightbooking.util.SessionContext;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class PnlXacNhanDatVe extends JPanel {

    private final AppNavigator nav;
    private final DatVeBUS datVeBUS = new DatVeBUS();

    private final JTextArea summary = new JTextArea();
    private final JComboBox<String> cbHanhKhach = new JComboBox<>();
    private JButton btnDone;

    private JCheckBox chkUsePoint = new JCheckBox("Dùng điểm");
    private JTextField txtDiem = new JTextField("0");

    public PnlXacNhanDatVe(AppNavigator nav) {
        this.nav = nav;

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(UserTheme.BG);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildSummary(), BorderLayout.CENTER);
        add(buildActions(), BorderLayout.SOUTH);

        txtDiem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                updateTongTien();
            }
        });

        chkUsePoint.addActionListener(e -> updateTongTien());
    }

    private JComponent buildHeader() {
        JLabel lb = new JLabel("Xác nhận đặt vé");
        lb.setFont(lb.getFont().deriveFont(Font.BOLD, 22f));
        lb.setForeground(UserTheme.PRIMARY);
        return lb;
    }

    private JComponent buildSummary() {
        summary.setEditable(false);
        summary.setLineWrap(true);
        summary.setWrapStyleWord(true);
        summary.setBackground(UserTheme.CARD);
        summary.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        summary.setText("Đang tải thông tin vé...");

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UserTheme.BG);

        wrapper.add(cbHanhKhach, BorderLayout.NORTH);
        wrapper.add(new JScrollPane(summary), BorderLayout.CENTER);

        JPanel pointPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pointPanel.setBackground(UserTheme.CARD);
        pointPanel.add(chkUsePoint);
        pointPanel.add(new JLabel("Số điểm:"));
        txtDiem.setPreferredSize(new Dimension(80, 30));
        pointPanel.add(txtDiem);

        wrapper.add(pointPanel, BorderLayout.SOUTH);
        return wrapper;
    }

    private JComponent buildActions() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setOpaque(false);

        // ✅ Dùng UserTheme thay vì primaryButton/ghostButton cũ
        JButton back    = UserTheme.createOutlineButton("← Quay lại");
        JButton addMore = UserTheme.createOutlineButton("+ Thêm vé");
        btnDone         = UserTheme.createButton("Hoàn tất");

        back.addActionListener(e -> {
            nav.show("HANH_KHACH");
            Component comp = nav.get("HANH_KHACH");
            if (comp instanceof PnlThongTinHanhKhach) {
                ((PnlThongTinHanhKhach) comp).reload();
            }
        });

        addMore.addActionListener(e -> {
            TempVeStore.setCurrentIndex(-1);
            nav.show("CHON_GHE");
        });

        btnDone.addActionListener(e -> datVe());

        p.add(back);
        p.add(addMore);
        p.add(btnDone);
        return p;
    }

    private void datVe() {
        try {
            int chuyenBayId = PnlKetQuaChuyenBay.CHUYEN_BAY_ID_CHON;
            List<DatVeBUS.ThongTinHanhKhachVaGhe> items = TempVeStore.getAll();

            if (items.isEmpty()) throw new RuntimeException("Bạn chưa thêm vé nào.");

            Integer khachHangId = SessionContext.getCurrentUserId();
            if (khachHangId == null) throw new RuntimeException("Bạn chưa đăng nhập.");

            int diemSuDung = 0;
            if (chkUsePoint.isSelected()) {
                try { diemSuDung = Integer.parseInt(txtDiem.getText()); } catch (Exception e) { diemSuDung = 0; }
            }

            datVeBUS.datVe(khachHangId, null, chuyenBayId, items, "online", diemSuDung);

            JOptionPane.showMessageDialog(this, "Đặt vé thành công!");

            KhachHangBUS bus = new KhachHangBUS();
            KhachHangDTO user = bus.getByUsername(SessionContext.getCurrentUsername());
            SessionContext.setCurrentUser(user);

            TempVeStore.clear();
            PnlChonGhe.GHE_ID_DANG_CHON = null;
            PnlThongTinHanhKhach.HO_TEN = "";
            PnlThongTinHanhKhach.SO_GIAY_TO = "";

            nav.show("TIM_CHUYEN");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Đặt vé thất bại: " + ex.getMessage());
        }
    }

    public void reload() {
        int chuyenBayId = PnlKetQuaChuyenBay.CHUYEN_BAY_ID_CHON;
        List<DatVeBUS.ThongTinHanhKhachVaGhe> ds = TempVeStore.getAll();

        if (chuyenBayId <= 0 || ds.isEmpty()) {
            summary.setText("❌ Thiếu dữ liệu.");
            return;
        }

        cbHanhKhach.removeAllItems();

        int index = 1;
        for (DatVeBUS.ThongTinHanhKhachVaGhe it : ds) {
            cbHanhKhach.addItem("Hành khách " + index++);
        }

        cbHanhKhach.addActionListener(e -> {
            int selected = cbHanhKhach.getSelectedIndex();
            if (selected >= 0 && selected < ds.size()) {
                TempVeStore.setCurrentIndex(selected);
                showSingle(ds.get(selected));
            }
        });

        if (TempVeStore.CURRENT_INDEX >= 0) {
            cbHanhKhach.setSelectedIndex(TempVeStore.CURRENT_INDEX);
        } else {
            TempVeStore.setCurrentIndex(0);
            cbHanhKhach.setSelectedIndex(0);
        }

        showSingle(ds.get(cbHanhKhach.getSelectedIndex()));
        updateTongTien();
    }

    private void showSingle(DatVeBUS.ThongTinHanhKhachVaGhe it) {
        int chuyenBayId = PnlKetQuaChuyenBay.CHUYEN_BAY_ID_CHON;
        ThongTinVeBUS bus = new ThongTinVeBUS();

        ThongTinVeDTO t = bus.getFullInfo(
                chuyenBayId,
                it.getGheId(),
                it.getHanhKhach().getHoTen(),
                it.getHanhKhach().getSoGiayTo()
        );

        if (t == null) {
            summary.setText("Không có dữ liệu");
            return;
        }

        summary.setText(
                "===== VÉ =====\n\n" +
                "Hành khách: " + t.getHoTen() + "\n" +
                "Giấy tờ: " + t.getSoGiayTo() + "\n\n" +
                "Tuyến: " + t.getSanBayDi() + " → " + t.getSanBayDen() + "\n" +
                "Khởi hành: " + t.getGioKhoiHanh() + "\n\n" +
                "Máy bay: " + t.getTenMayBay() + " - " + t.getKieuMayBay() + "\n\n" +
                "Ghế: " + t.getTenGhe() + "\n" +
                "Hạng: " + t.getHangGhe()
        );
    }

    private void updateTongTien() {
        if (btnDone == null) return;

        int chuyenBayId = PnlKetQuaChuyenBay.CHUYEN_BAY_ID_CHON;
        List<DatVeBUS.ThongTinHanhKhachVaGhe> items = TempVeStore.getAll();

        if (items.isEmpty()) {
            btnDone.setText("Hoàn tất");
            return;
        }

        BigDecimal tong = BigDecimal.ZERO;
        for (DatVeBUS.ThongTinHanhKhachVaGhe it : items) {
            tong = tong.add(datVeBUS.tinhGiaGhe(chuyenBayId, it.getGheId()));
        }

        Integer userId = SessionContext.getCurrentUserId();
        if (userId != null && chkUsePoint.isSelected()) {
            int diemSuDung = 0;
            try { diemSuDung = Integer.parseInt(txtDiem.getText()); } catch (Exception e) { diemSuDung = 0; }

            int diemHienTai = new KhachHangBUS().getDiem(userId);
            if (diemSuDung > diemHienTai) diemSuDung = diemHienTai;

            BigDecimal giam = BigDecimal.valueOf(diemSuDung * 10L);
            tong = tong.subtract(giam);
            if (tong.compareTo(BigDecimal.ZERO) < 0) tong = BigDecimal.ZERO;
        }

        btnDone.setText("Hoàn tất (" + tong + " VND)");
    }
}