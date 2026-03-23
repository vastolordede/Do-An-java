package flightbooking.gui.admin.pnl;

import flightbooking.bus.DatVeBUS;
import flightbooking.dto.GheDTO;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class AdminSeatMapPanel extends JPanel {

    private final DatVeBUS datVeBUS = new DatVeBUS();

    public Integer gheDangChon = null;
    public String gheText = "";

    public AdminSeatMapPanel(int chuyenBayId) {

        setLayout(new BorderLayout());

        List<GheDTO> ds = datVeBUS.dsGheCuaChuyen(chuyenBayId);

        Map<Integer, Map<Integer, GheDTO>> map = new HashMap<>();
        TreeSet<Integer> rows = new TreeSet<>();
        int maxCol = 0;

        for (GheDTO g : ds) {
            rows.add(g.getRowIndex());
            maxCol = Math.max(maxCol, g.getColIndex());
            map.computeIfAbsent(g.getRowIndex(), k -> new HashMap<>())
                    .put(g.getColIndex(), g);
        }

        JPanel grid = new JPanel(new GridLayout(rows.size(), maxCol, 5, 5));

        for (int r : rows) {
            Map<Integer, GheDTO> colMap = map.get(r);

            for (int c = 1; c <= maxCol; c++) {
                GheDTO g = colMap.get(c);

                if (g == null) {
                    grid.add(new JLabel());
                    continue;
                }

                JButton btn = new JButton(g.getTenGhe());

                // ❌ ghế không hợp lệ
                if (g.isDaDat() || (g.getTrangThai() != null && g.getTrangThai() == 0)) {
                    btn.setEnabled(false);
                    btn.setBackground(Color.GRAY);
                } else {
                    colorSeat(btn, g.getHangGheId());

                    btn.addActionListener(e -> {
                        gheDangChon = g.getGheId();
                        gheText = g.getTenGhe();

                        // reset màu
                        for (Component comp : grid.getComponents()) {
                            if (comp instanceof JButton) {
                                JButton b = (JButton) comp;
                                for (GheDTO seat : ds) {
                                    if (seat.getTenGhe().equals(b.getText())) {
                                        colorSeat(b, seat.getHangGheId());
                                    }
                                }
                            }
                        }

                        btn.setBackground(Color.GREEN);
                    });
                }

                grid.add(btn);
            }
        }

        add(new JScrollPane(grid), BorderLayout.CENTER);
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
}