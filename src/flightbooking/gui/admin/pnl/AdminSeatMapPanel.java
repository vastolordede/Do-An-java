package flightbooking.gui.admin.pnl;

import flightbooking.bus.DatVeBUS;
import flightbooking.dto.GheDTO;
import flightbooking.gui.common.AircraftLayoutPanel;
import flightbooking.gui.common.SeatMapRenderUtil;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminSeatMapPanel extends JPanel {

    private final DatVeBUS datVeBUS = new DatVeBUS();

    public Integer gheDangChon = null;
    public String gheText = "";

    private final Map<Integer, JButton> seatButtonMap = new HashMap<Integer, JButton>();

    public AdminSeatMapPanel(int chuyenBayId) {
        setLayout(new BorderLayout());

        final List<GheDTO> ds = datVeBUS.dsGheCuaChuyen(chuyenBayId);
        if (ds == null || ds.isEmpty()) {
            add(new JLabel("Không có dữ liệu ghế", SwingConstants.CENTER), BorderLayout.CENTER);
            return;
        }

        JComponent seatMap = SeatMapRenderUtil.buildSeatMap(ds, new SeatMapRenderUtil.SeatComponentFactory() {
            @Override
            public JComponent createSeat(final GheDTO ghe) {
                JButton btn = new JButton("<html><center><b>" + ghe.getTenGhe() + "</b></center></html>");
                btn.setPreferredSize(new Dimension(58, 46));
                btn.setMargin(new Insets(2, 2, 2, 2));
                btn.setFont(new Font("Arial", Font.PLAIN, 11));
                btn.setFocusPainted(false);
                btn.setOpaque(true);
                btn.setContentAreaFilled(true);
                btn.setBorderPainted(true);

                seatButtonMap.put(ghe.getGheId(), btn);

                boolean unavailable = ghe.isDaDat()
                        || (ghe.getTrangThai() != null && ghe.getTrangThai().intValue() == 0);

                if (unavailable) {
                    btn.setEnabled(false);
                    btn.setBackground(new Color(90, 90, 90));
                    btn.setForeground(Color.WHITE);
                } else {
                    SeatMapRenderUtil.colorSeat(btn, ghe.getHangGheId());
                    btn.setForeground(Color.BLACK);

                    btn.addActionListener(e -> {
                        gheDangChon = ghe.getGheId();
                        gheText = ghe.getTenGhe();
                        resetAllSeatColors(ds);
                        btn.setBackground(Color.GREEN);
                    });
                }

                return btn;
            }
        });

        JComponent aircraftPanel = new AircraftLayoutPanel(seatMap);

        add(aircraftPanel, BorderLayout.CENTER);
        add(buildLegend(), BorderLayout.SOUTH);
    }

    private void resetAllSeatColors(List<GheDTO> ds) {
        for (GheDTO seat : ds) {
            JButton b = seatButtonMap.get(seat.getGheId());
            if (b == null) continue;

            boolean unavailable = seat.isDaDat()
                    || (seat.getTrangThai() != null && seat.getTrangThai().intValue() == 0);

            if (unavailable) {
                b.setEnabled(false);
                b.setBackground(new Color(90, 90, 90));
                b.setForeground(Color.WHITE);
            } else {
                b.setEnabled(true);
                b.setForeground(Color.BLACK);
                SeatMapRenderUtil.colorSeat(b, seat.getHangGheId());
            }
        }
    }

    private JPanel buildLegend() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setBorder(BorderFactory.createTitledBorder("Chú thích"));

        p.add(colorBox(new Color(255, 180, 180), "First Class"));
        p.add(colorBox(new Color(255, 220, 150), "Business"));
        p.add(colorBox(new Color(210, 255, 210), "Premium Economy"));
        p.add(colorBox(new Color(200, 230, 255), "Economy"));
        p.add(colorBox(new Color(90, 90, 90), "Đã đặt / Không khả dụng"));
        p.add(colorBox(Color.GREEN, "Đang chọn"));

        return p;
    }

    private JPanel colorBox(Color c, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));

        JLabel box = new JLabel();
        box.setOpaque(true);
        box.setBackground(c);
        box.setPreferredSize(new Dimension(18, 18));
        box.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        p.add(box);
        p.add(new JLabel(text));
        return p;
    }
}