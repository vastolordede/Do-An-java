package flightbooking.gui.common;

import flightbooking.dto.GheDTO;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class SeatMapRenderUtil {

    public interface SeatComponentFactory {
        JComponent createSeat(GheDTO ghe);
    }

    private static final int HEADER_W = 42;
    private static final int HEADER_H = 42;
    private static final int CELL_W = 64;
    private static final int CELL_H = 46;
    private static final int GAP = 8;
    private static final int PADDING = 12;

    public static JComponent buildSeatMap(List<GheDTO> seats, SeatComponentFactory factory) {
        if (seats == null || seats.isEmpty()) {
            return new JLabel("Không có dữ liệu ghế", SwingConstants.CENTER);
        }

        int maxRow = 0;
        SortedSet<Integer> usedCols = new TreeSet<>();
        Map<String, GheDTO> seatMap = new HashMap<>();

        for (GheDTO g : seats) {
            if (g == null) continue;

            int row = safeInt(g.getRowIndex());
            int col = safeInt(g.getColIndex());

            if (row <= 0 || col <= 0) continue;

            maxRow = Math.max(maxRow, row);
            usedCols.add(col);
            seatMap.put(key(row, col), g);
        }

        if (maxRow <= 0 || usedCols.isEmpty()) {
            return new JLabel("Không có dữ liệu ghế hợp lệ", SwingConstants.CENTER);
        }

        int minCol = usedCols.first();
        int maxCol = usedCols.last();

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
        grid.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(GAP / 2, GAP / 2, GAP / 2, GAP / 2);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        // Góc trái trên
        gbc.gridx = 0;
        gbc.gridy = 0;
        grid.add(createCornerCell(), gbc);

        // Header ngang: 1 2 3 4 ... (row_index)
        for (int row = 1; row <= maxRow; row++) {
            gbc.gridx = row;
            gbc.gridy = 0;
            grid.add(createHeaderCell(String.valueOf(row)), gbc);
        }

        // Body:
        // Cột trái = A B [trống] D E
        // Trong thân = ghế theo (row, col)
        int visualY = 1;
        for (int col = minCol; col <= maxCol; col++) {
            gbc.gridx = 0;
            gbc.gridy = visualY;

            if (usedCols.contains(col)) {
                grid.add(createHeaderCell(colToLetter(col)), gbc);
            } else {
                grid.add(createEmptyHeaderCell(), gbc);
            }

            for (int row = 1; row <= maxRow; row++) {
                gbc.gridx = row;
                gbc.gridy = visualY;

                GheDTO ghe = seatMap.get(key(row, col));
                if (ghe != null) {
                    grid.add(factory.createSeat(ghe), gbc);
                } else {
                    grid.add(createEmptyCell(), gbc);
                }
            }

            visualY++;
        }

        int visualColCount = maxCol - minCol + 1;
        int gridWidth = PADDING * 2 + HEADER_W + maxRow * CELL_W + (maxRow + 1) * GAP;
        int gridHeight = PADDING * 2 + HEADER_H + visualColCount * CELL_H + (visualColCount + 1) * GAP;
        grid.setPreferredSize(new Dimension(gridWidth, gridHeight));

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(grid);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getHorizontalScrollBar().setUnitIncrement(16);

        int viewportW = Math.min(gridWidth + 10, 1200);
        int viewportH = Math.min(gridHeight + 10, 420);
        scroll.setPreferredSize(new Dimension(viewportW, viewportH));

        return scroll;
    }

    private static String key(int row, int col) {
        return row + "_" + col;
    }

    private static int safeInt(Integer v) {
        return v == null ? 0 : v;
    }

    private static JComponent createCornerCell() {
        JLabel lb = new JLabel("", SwingConstants.CENTER);
        lb.setPreferredSize(new Dimension(HEADER_W, HEADER_H));
        lb.setOpaque(false);
        return lb;
    }

    private static JComponent createHeaderCell(String text) {
        JLabel lb = new JLabel(text, SwingConstants.CENTER);
        lb.setPreferredSize(new Dimension(HEADER_W, HEADER_H));
        lb.setFont(new Font("Arial", Font.BOLD, 13));
        lb.setForeground(new Color(60, 60, 60));
        lb.setOpaque(false);
        return lb;
    }

    private static JComponent createEmptyHeaderCell() {
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(HEADER_W, HEADER_H));
        p.setOpaque(false);
        return p;
    }

    private static JComponent createEmptyCell() {
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(CELL_W, CELL_H));
        p.setOpaque(false);
        return p;
    }

    private static String colToLetter(int col) {
        return String.valueOf((char) ('A' + col - 1));
    }

    public static void colorSeat(JButton btn, int hangGheId) {
        switch (hangGheId) {
            case 3:
                btn.setBackground(new Color(255, 180, 180));
                break;
            case 2:
                btn.setBackground(new Color(255, 220, 150));
                break;
            case 4:
                btn.setBackground(new Color(210, 255, 210));
                break;
            case 1:
                btn.setBackground(new Color(200, 230, 255));
                break;
            default:
                btn.setBackground(Color.LIGHT_GRAY);
                break;
        }

        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(true);
    }
}