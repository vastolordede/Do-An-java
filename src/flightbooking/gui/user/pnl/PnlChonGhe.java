package flightbooking.gui.user.pnl;

import flightbooking.bus.DatVeBUS;
import flightbooking.dto.GheDTO;
import flightbooking.gui.user.common.AppNavigator;
import flightbooking.gui.user.common.TempVeStore;
import flightbooking.gui.user.theme.UserTheme;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class PnlChonGhe extends JPanel {

    private final AppNavigator nav;
    private final DatVeBUS datVeBUS = new DatVeBUS();
    private final JLabel lblInfo = new JLabel("Chọn ghế");
    private JComponent seatArea;

    public static Integer GHE_ID_DANG_CHON = null;
    public static String GHE_TEXT_DA_CHON = "";

    public PnlChonGhe(AppNavigator nav) {
        this.nav = nav;

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(UserTheme.BG);

        lblInfo.setForeground(UserTheme.ACCENT);
        add(lblInfo, BorderLayout.NORTH);

        seatArea = new JPanel();
        add(seatArea, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(buildLegend(), BorderLayout.CENTER);
        south.add(buildActions(), BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);
    }

    private JComponent buildSeatMap() {
        int chuyenBayId = PnlKetQuaChuyenBay.CHUYEN_BAY_ID_CHON;
        List<GheDTO> ds = datVeBUS.dsGheCuaChuyen(chuyenBayId);

        if (ds.isEmpty()) {
            JPanel empty = new JPanel();
            empty.add(new JLabel("Không có ghế"));
            return empty;
        }

        Map<Integer, Map<Integer, GheDTO>> map = new HashMap<>();
        TreeSet<Integer> rows = new TreeSet<>();
        int maxCol = 0;

        for (GheDTO g : ds) {
            rows.add(g.getRowIndex());
            if (g.getColIndex() > maxCol) maxCol = g.getColIndex();
            map.computeIfAbsent(g.getRowIndex(), k -> new HashMap<>())
                    .put(g.getColIndex(), g);
        }

        int aisle = maxCol / 2;

        JPanel grid = new JPanel(new GridLayout(rows.size() + 1, maxCol + 2, 5, 5));
        grid.setBackground(UserTheme.CARD);
        grid.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Header
        grid.add(new JLabel(""));
        for (int c = 1; c <= maxCol; c++) {
            char colLetter = (char) ('A' + c - 1);
            grid.add(new JLabel(String.valueOf(colLetter), SwingConstants.CENTER));
            if (c == aisle) grid.add(new JLabel(""));
        }

        List<Integer> gheDaChon = new ArrayList<>();
        for (int i = 0; i < TempVeStore.getAll().size(); i++) {
            if (i == TempVeStore.CURRENT_INDEX) continue;
            gheDaChon.add(TempVeStore.getAll().get(i).getGheId());
        }

        for (int r : rows) {
            grid.add(new JLabel(String.valueOf(r), SwingConstants.CENTER));
            Map<Integer, GheDTO> colMap = map.get(r);

            for (int c = 1; c <= maxCol; c++) {
                GheDTO g = (colMap != null) ? colMap.get(c) : null;

                if (g == null) {
                    grid.add(new JLabel(""));
                } else {
                    // ✅ Ghế dùng JButton thường vì cần màu động theo trạng thái
                    JButton b = new JButton(g.getTenGhe());
                    b.setFocusPainted(false);
                    b.setContentAreaFilled(true); // ghế cần fill màu theo trạng thái
                    b.setBorderPainted(true);

                    if (g.isDaDat()
                            || (g.getTrangThai() != null && g.getTrangThai() == 0)
                            || gheDaChon.contains(g.getGheId())) {

                        b.setEnabled(false);
                        b.setBackground(new Color(70, 70, 70));
                        b.setForeground(Color.LIGHT_GRAY);

                    } else {
                        colorSeat(b, g.getHangGheId());
                        b.setForeground(Color.BLACK);

                        b.addActionListener(e -> {
                            // Reset màu tất cả ghế
                            for (Component comp : grid.getComponents()) {
                                if (comp instanceof JButton) {
                                    JButton btn = (JButton) comp;
                                    for (GheDTO seat : ds) {
                                        if (seat.getTenGhe().equals(btn.getText())) {
                                            colorSeat(btn, seat.getHangGheId());
                                            break;
                                        }
                                    }
                                }
                            }

                            GHE_ID_DANG_CHON = g.getGheId();
                            GHE_TEXT_DA_CHON = "Ghế " + g.getTenGhe();
                            b.setBackground(Color.GREEN);
                            lblInfo.setText("Đã chọn: " + g.getTenGhe());
                        });
                    }

                    if (GHE_ID_DANG_CHON != null && g.getGheId() == GHE_ID_DANG_CHON) {
                        b.setBackground(Color.GREEN);
                    }

                    grid.add(b);
                }

                if (c == aisle) grid.add(new JLabel(""));
            }
        }

        return new JScrollPane(grid);
    }

    private void colorSeat(JButton btn, Integer hangGheId) {
        if (hangGheId == null) return;
        switch (hangGheId) {
            case 3: btn.setBackground(new Color(255, 180, 180)); break;
            case 2: btn.setBackground(new Color(255, 220, 150)); break;
            case 4: btn.setBackground(new Color(210, 255, 210)); break;
            case 1: btn.setBackground(new Color(200, 230, 255)); break;
            default: btn.setBackground(Color.WHITE);
        }
    }

    private JComponent buildActions() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setOpaque(false);

        // ✅ Dùng UserTheme thay vì primaryButton/ghostButton cũ
        JButton back = UserTheme.createOutlineButton("← Quay lại");
        JButton next = UserTheme.createButton("Tiếp tục →");

        back.addActionListener(e -> nav.show("KQ_CHUYEN"));

        next.addActionListener(e -> {
            if (GHE_ID_DANG_CHON == null) {
                JOptionPane.showMessageDialog(this, "Bạn chưa chọn ghế.");
                return;
            }
            nav.show("HANH_KHACH");
            Component comp = nav.get("HANH_KHACH");
            if (comp instanceof PnlThongTinHanhKhach) {
                ((PnlThongTinHanhKhach) comp).reload();
            }
        });

        p.add(back);
        p.add(next);
        return p;
    }

    public void reload() {
        DatVeBUS.ThongTinHanhKhachVaGhe current = TempVeStore.getCurrent();

        if (current != null) {
            GHE_ID_DANG_CHON = current.getGheId();
            lblInfo.setText("Đã chọn: " + current.getGheId());
        } else {
            GHE_ID_DANG_CHON = null;
            lblInfo.setText("Chọn ghế");
        }

        remove(seatArea);
        seatArea = buildSeatMap();
        add(seatArea, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JPanel buildLegend() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setBorder(BorderFactory.createTitledBorder("Chú thích hạng ghế"));
        p.setBackground(UserTheme.BG);

        p.add(colorBox(new Color(255, 180, 180), "First Class"));
        p.add(colorBox(new Color(255, 220, 150), "Business"));
        p.add(colorBox(new Color(210, 255, 210), "Premium Economy"));
        p.add(colorBox(new Color(200, 230, 255), "Economy"));
        p.add(colorBox(new Color(70, 70, 70), "Đã đặt / Không khả dụng"));
        p.add(colorBox(Color.GREEN, "Đang chọn"));

        return p;
    }

    private JPanel colorBox(Color c, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setOpaque(false);

        JLabel box = new JLabel();
        box.setOpaque(true);
        box.setBackground(c);
        box.setPreferredSize(new Dimension(20, 20));
        box.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        p.add(box);
        p.add(new JLabel(text));
        return p;
    }
}