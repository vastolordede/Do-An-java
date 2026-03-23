package flightbooking.gui.user.pnl;

import flightbooking.bus.ChuyenBayBUS;
import flightbooking.dto.ChuyenBayDTO;
import flightbooking.gui.user.common.AppNavigator;
import flightbooking.gui.user.theme.UserTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PnlKetQuaChuyenBay extends JPanel {

    private final AppNavigator nav;
    private final ChuyenBayBUS chuyenBayBUS = new ChuyenBayBUS();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Chuyến bay ID", "Tuyến", "Giờ KH", "Giờ Đến", "Trạng thái"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };

    private final JTable table = new JTable(model);

    public static int CHUYEN_BAY_ID_CHON = -1;

    public PnlKetQuaChuyenBay(AppNavigator nav) {
        this.nav = nav;

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(UserTheme.BG);

        add(buildHeader(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildActions(), BorderLayout.SOUTH);

        reload();
    }

    private JComponent buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel title = new JLabel("Kết quả chuyến bay");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setForeground(UserTheme.PRIMARY);

        JLabel sub = new JLabel("Chọn 1 chuyến rồi bấm 'Chọn ghế'");
        sub.setForeground(UserTheme.ACCENT);

        JPanel g = new JPanel(new GridLayout(2, 1));
        g.setOpaque(false);
        g.add(title);
        g.add(sub);

        p.add(g, BorderLayout.WEST);
        return p;
    }

    private JComponent buildActions() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setOpaque(false);

        // ✅ Dùng UserTheme thay vì private methods cũ
        JButton back = UserTheme.createOutlineButton("← Quay lại");
        JButton next = UserTheme.createButton("Chọn ghế →");

        back.addActionListener(e -> nav.show("TIM_CHUYEN"));
        next.addActionListener(e -> chonChuyen());

        p.add(back);
        p.add(next);
        return p;
    }

    public void reload() {
        model.setRowCount(0);

        int di = PnlTimChuyenBay.SANBAY_DI_ID;
        int den = PnlTimChuyenBay.SANBAY_DEN_ID;
        String ngayStr = PnlTimChuyenBay.NGAY;

        if (di <= 0 || den <= 0 || ngayStr == null || ngayStr.isBlank()) return;

        
LocalDate ngay = LocalDate.parse(ngayStr);

// ✅ dùng hàm mới
List<ChuyenBayDTO> list = chuyenBayBUS.timChuyenTheoNgay(di, den, ngay);

        for (ChuyenBayDTO c : list) {
            String tuyen =
                    (c.getSanBayDiTen() != null && c.getSanBayDenTen() != null)
                            ? (c.getSanBayDiTen() + " → " + c.getSanBayDenTen())
                            : ("Tuyến #" + c.getTuyenBayId());

            model.addRow(new Object[]{
                    c.getChuyenBayId(),
                    tuyen,
                    c.getGioKhoiHanh(),
                    c.getGioDen(),
                    c.getTrangThai()
            });
        }
    }

    private void chonChuyen() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Bạn hãy chọn 1 chuyến bay.");
            return;
        }
        CHUYEN_BAY_ID_CHON = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
        nav.show("CHON_GHE");
        Component comp = nav.get("CHON_GHE");
        if (comp instanceof PnlChonGhe) {
            ((PnlChonGhe) comp).reload();
        }
    }
}