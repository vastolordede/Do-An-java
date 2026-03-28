package flightbooking.gui.user.pnl;

import flightbooking.bus.DatVeBUS;
import flightbooking.dto.GheDTO;
import flightbooking.dto.GiaGheOverrideDTO;
import flightbooking.gui.common.SeatMapRenderUtil;
import flightbooking.gui.user.common.AppNavigator;
import flightbooking.gui.user.common.TempVeStore;
import flightbooking.gui.user.theme.UserTheme;
import flightbooking.dao.GiaGheOverrideDAO;
import flightbooking.dao.GiaHangChuyenBayDAO;
import flightbooking.dto.GiaHangChuyenBayDTO;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PnlChonGhe extends JPanel {

    private final AppNavigator nav;
    private final DatVeBUS datVeBUS = new DatVeBUS();
    private final JLabel lblInfo = new JLabel("Chọn ghế");
    private final Map<Integer, Long> basePriceCache = new HashMap<>();
    private final GiaHangChuyenBayDAO giaHangDAO = new GiaHangChuyenBayDAO();
    private JComponent seatArea;

    // Thêm field
    private final GiaGheOverrideDAO overrideDAO = new GiaGheOverrideDAO();
    private final Map<Integer, Long> overridePriceCache = new HashMap<>();

    public static Integer GHE_ID_DANG_CHON = null;
    public static String GHE_TEXT_DA_CHON = "";

    public PnlChonGhe(AppNavigator nav) {
        // loadPriceCache(PnlKetQuaChuyenBay.CHUYEN_BAY_ID_CHON);
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

        loadPriceCache(chuyenBayId);
        loadOverrideCache(chuyenBayId);

        final List<GheDTO> ds = datVeBUS.dsGheCuaChuyen(chuyenBayId);

        if (ds == null || ds.isEmpty()) {
            JPanel empty = new JPanel();
            empty.add(new JLabel("Không có ghế"));
            return empty;
        }

        final List<Integer> gheDaChon = new ArrayList<Integer>();
        for (int i = 0; i < TempVeStore.getAll().size(); i++) {
            if (i == TempVeStore.CURRENT_INDEX) {
                continue;
            }
            gheDaChon.add(TempVeStore.getAll().get(i).getGheId());
        }

        return SeatMapRenderUtil.buildSeatMap(ds, new SeatMapRenderUtil.SeatComponentFactory() {
            @Override
            public JComponent createSeat(final GheDTO ghe) {
                long price = getSeatPrice(ghe);

                JButton b = new JButton(
                    "<html><center>" + ghe.getTenGhe() + "<br>" + formatPrice(price) + "</center></html>"
                );
                b.setPreferredSize(new Dimension(56, 40));
                b.setFocusPainted(false);
                b.setContentAreaFilled(true);
                b.setBorderPainted(true);

                boolean unavailable = ghe.isDaDat()
                        || (ghe.getTrangThai() != null && ghe.getTrangThai().intValue() == 0)
                        || gheDaChon.contains(ghe.getGheId());

                if (unavailable) {
                    b.setEnabled(false);
                    b.setBackground(new Color(70, 70, 70));
                    b.setForeground(Color.LIGHT_GRAY);
                } else {
                    SeatMapRenderUtil.colorSeat(b, ghe.getHangGheId());
                    b.setForeground(Color.BLACK);

                    b.addActionListener(e -> {
                        GHE_ID_DANG_CHON = ghe.getGheId();
                        GHE_TEXT_DA_CHON = "Ghế " + ghe.getTenGhe();
                        lblInfo.setText("Đã chọn: " + ghe.getTenGhe());

                        remove(seatArea);
                        seatArea = buildSeatMap();
                        add(seatArea, BorderLayout.CENTER);

                        revalidate();
                        repaint();
                    });
                }

                if (GHE_ID_DANG_CHON != null && ghe.getGheId() == GHE_ID_DANG_CHON.intValue()) {
                    b.setBackground(Color.GREEN);
                }

                return b;
            }
        });
    }

    private JComponent buildActions() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setOpaque(false);

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

    private void loadPriceCache(int chuyenBayId) {
        try {
            List<GiaHangChuyenBayDTO> list = giaHangDAO.findByChuyenBay(chuyenBayId);
            for (GiaHangChuyenBayDTO g : list) {
                long giaCoBan = g.getGiaCoBan().longValue();
                long thue = g.getThuePhi() != null ? g.getThuePhi().longValue() : 0L;

                long giaSau = giaCoBan + (giaCoBan * thue / 100);
                basePriceCache.put(g.getHangGheId(), giaSau);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long getSeatPrice(GheDTO ghe) {
        Long overridePrice = overridePriceCache.get(ghe.getGheId());
        if (overridePrice != null) return overridePrice;
        
        Long price = basePriceCache.get(ghe.getHangGheId());
        return price != null ? price : 0L;
    }

    private String formatPrice(long price) {
    if (price >= 1_000_000) {
        return String.format("%.1fM", price / 1_000_000.0);
    } else if (price >= 1_000) {
        return (price / 1_000) + "K";
    }
    return price + "đ";
}

// Thêm method
private void loadOverrideCache(int chuyenBayId) {
    try {
        List<GiaGheOverrideDTO> list = overrideDAO.findByChuyenBay(chuyenBayId);
        for (GiaGheOverrideDTO o : list) {
            if (o.getGheId() != null && o.getGiaOverride() != null) {
                overridePriceCache.put(o.getGheId(), o.getGiaOverride().longValue());
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}