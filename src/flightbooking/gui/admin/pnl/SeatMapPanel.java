package flightbooking.gui.admin.pnl;

import flightbooking.dao.GheDAO;
import flightbooking.dao.GiaGheOverrideDAO;
import flightbooking.dao.GiaHangChuyenBayDAO;
import flightbooking.dto.GheDTO;
import flightbooking.dto.GiaGheOverrideDTO;
import flightbooking.dto.GiaHangChuyenBayDTO;
import flightbooking.gui.common.AircraftLayoutPanel;
import flightbooking.gui.common.SeatMapRenderUtil;
import flightbooking.util.PriceFormatter;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeatMapPanel extends JPanel {

    private final GheDAO gheDAO = new GheDAO();
    private final GiaHangChuyenBayDAO giaHangDAO = new GiaHangChuyenBayDAO();
    private final GiaGheOverrideDAO overrideDAO = new GiaGheOverrideDAO();

    private final Map<Integer, Long> basePriceCache = new HashMap<>();
    private final Map<Integer, Long> overridePriceCache = new HashMap<>(); // load 1 lần, không query từng ghế

    private final int chuyenBayId;

    public SeatMapPanel(final int chuyenBayId, int mayBayId) {
        this.chuyenBayId = chuyenBayId;
        setLayout(new BorderLayout());

        loadPriceCache(chuyenBayId);
        loadOverrideCache(chuyenBayId); // 1 query duy nhất thay vì N query

        final List<GheDTO> seats = gheDAO.findByMayBay(mayBayId);
        if (seats == null || seats.isEmpty()) {
            add(new JLabel("Máy bay chưa có ghế", SwingConstants.CENTER), BorderLayout.CENTER);
            return;
        }

        JComponent seatMap = SeatMapRenderUtil.buildSeatMap(seats, new SeatMapRenderUtil.SeatComponentFactory() {
            @Override
            public JComponent createSeat(final GheDTO ghe) {
                long price = getSeatPrice(ghe);

                String label = "<html><center><b>" + ghe.getTenGhe() + "</b><br>"
                        + PriceFormatter.formatSeatPrice(price) + "</center></html>";

                JButton btn = new JButton(label);
                btn.setPreferredSize(new Dimension(64, 46));
                btn.setMargin(new Insets(2, 2, 2, 2));
                btn.setFont(new Font("Arial", Font.PLAIN, 10));
                btn.setFocusPainted(false);
                btn.setOpaque(true);
                btn.setContentAreaFilled(true);
                btn.setBorderPainted(true);

                SeatMapRenderUtil.colorSeat(btn, ghe.getHangGheId());

                btn.addActionListener(e -> overrideSeatPrice(chuyenBayId, ghe, btn));

                return btn;
            }
        });

        JComponent aircraftPanel = new AircraftLayoutPanel(seatMap);

        add(aircraftPanel, BorderLayout.CENTER);
        add(buildLegend(), BorderLayout.SOUTH);
    }

    private void loadPriceCache(int chuyenBayId) {
        try {
            List<GiaHangChuyenBayDTO> list = giaHangDAO.findByChuyenBay(chuyenBayId);
            for (GiaHangChuyenBayDTO g : list) {
                long giaCoBan = g.getGiaCoBan().longValue();
                long thue = g.getThuePhi() != null ? g.getThuePhi().longValue() : 0L;
                long giaSauLai = giaCoBan + (giaCoBan * thue / 100);
                basePriceCache.put(g.getHangGheId(), giaSauLai);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    private long getSeatPrice(GheDTO ghe) {
        // Ưu tiên override từ cache — không query DB
        Long overridePrice = overridePriceCache.get(ghe.getGheId());
        if (overridePrice != null) return overridePrice;

        Long price = basePriceCache.get(ghe.getHangGheId());
        return price != null ? price : 0L;
    }

    private void overrideSeatPrice(int chuyenBayId, GheDTO ghe, JButton btn) {
        String s = JOptionPane.showInputDialog(this, "Nhập giá mới cho ghế " + ghe.getTenGhe());

        if (s == null || s.trim().isEmpty()) {
            return;
        }

        try {
            BigDecimal newPrice = new BigDecimal(s.trim());
            overrideDAO.insert(chuyenBayId, ghe.getGheId(), newPrice);

            // Cập nhật cache luôn, không cần reload toàn bộ
            overridePriceCache.put(ghe.getGheId(), newPrice.longValue());

            JOptionPane.showMessageDialog(this, "Đã cập nhật giá thành công!");

            btn.setText("<html><center><b>" + ghe.getTenGhe() + "</b><br>"
                    + PriceFormatter.formatSeatPrice(newPrice.longValue()) + "</center></html>");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá nhập vào không hợp lệ (phải là số).");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + ex.getMessage());
        }
    }

    private JPanel buildLegend() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setBorder(BorderFactory.createTitledBorder("Chú thích hạng ghế"));

        p.add(colorBox(new Color(255, 180, 180), "First Class"));
        p.add(colorBox(new Color(255, 220, 150), "Business"));
        p.add(colorBox(new Color(210, 255, 210), "Premium Economy"));
        p.add(colorBox(new Color(200, 230, 255), "Economy"));

        return p;
    }

    private JPanel colorBox(Color c, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
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