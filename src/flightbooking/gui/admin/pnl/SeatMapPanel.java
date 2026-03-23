package flightbooking.gui.admin.pnl;

import flightbooking.dao.GheDAO;
import flightbooking.dao.GiaGheOverrideDAO;
import flightbooking.dao.GiaHangChuyenBayDAO;
import flightbooking.dto.GheDTO;
import flightbooking.dto.GiaHangChuyenBayDTO;
import flightbooking.util.PriceFormatter;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeatMapPanel extends JPanel {

    private GheDAO gheDAO = new GheDAO();
    private GiaHangChuyenBayDAO giaHangDAO = new GiaHangChuyenBayDAO();
    private GiaGheOverrideDAO overrideDAO = new GiaGheOverrideDAO();

    // Cache giá theo hạng ghế
    private Map<Integer, Long> basePriceCache = new HashMap<>();

    public SeatMapPanel(int chuyenBayId, int mayBayId) {
        setLayout(new BorderLayout());
        loadPriceCache(chuyenBayId);

        List<GheDTO> seats = gheDAO.findByMayBay(mayBayId);
        if (seats.isEmpty()) {
            add(new JLabel("Máy bay chưa có ghế"), BorderLayout.CENTER);
            return;
        }

        // 1. XỬ LÝ DỮ LIỆU: Lọc ra các hàng và cột thực tế có tồn tại
        Map<Integer, Map<Integer, GheDTO>> structuredMap = new HashMap<>();
        java.util.TreeSet<Integer> existingRows = new java.util.TreeSet<>();
        int maxCol = 0;

        for (GheDTO g : seats) {
            existingRows.add(g.getRowIndex()); // Chỉ lưu những rowIndex thực sự có trong DB
            if (g.getColIndex() > maxCol) maxCol = g.getColIndex();
            
            structuredMap.computeIfAbsent(g.getRowIndex(), k -> new HashMap<>())
                         .put(g.getColIndex(), g);
        }

        int aisle = maxCol / 2;
        int totalRowsExist = existingRows.size(); // Số lượng hàng thực tế sẽ vẽ

        // 2. TẠO LƯỚI (Dùng totalRowsExist thay vì maxRow)
        JPanel grid = new JPanel(new GridLayout(totalRowsExist + 1, maxCol + 2, 5, 5));

        // Header Cột (A, B, C...)
        grid.add(new JLabel("")); 
        for (int c = 1; c <= maxCol; c++) {
            char colLetter = (char) ('A' + c - 1);
            grid.add(new JLabel(String.valueOf(colLetter), SwingConstants.CENTER));
            if (c == aisle) grid.add(new JLabel(""));
        }

        // 3. VẼ CÁC HÀNG (Duyệt qua danh sách hàng thực tế)
        for (int rowIndex : existingRows) {
            // Hiển thị nhãn hàng dựa trên rowIndex gốc trong DB
            grid.add(new JLabel(String.valueOf(rowIndex), SwingConstants.CENTER));

            Map<Integer, GheDTO> colMap = structuredMap.get(rowIndex);

            for (int c = 1; c <= maxCol; c++) {
                GheDTO seat = (colMap != null) ? colMap.get(c) : null;

                if (seat == null) {
                    grid.add(new JLabel("")); 
                } else {
                    // Truyền rowIndex gốc để nhãn ghế (1A, 2A...) vẫn chính xác
                    JButton btn = createSeatButton(chuyenBayId, seat, rowIndex, c);
                    grid.add(btn);
                }

                if (c == aisle) grid.add(new JLabel(""));
            }
        }

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));
        wrapper.add(grid);
        add(new JScrollPane(wrapper), BorderLayout.CENTER);
        add(buildLegend(), BorderLayout.SOUTH);
    
    }

    private void loadPriceCache(int chuyenBayId) {
    try {
        List<GiaHangChuyenBayDTO> list = giaHangDAO.findByChuyenBay(chuyenBayId);
        for (GiaHangChuyenBayDTO g : list) {
            long giaCoBan = g.getGiaCoBan().longValue();
            long thue = g.getThuePhi() != null ? g.getThuePhi().longValue() : 0;

            // Nếu thuế là % (ví dụ: 10 = 10%)
            long giaSauLai = giaCoBan + (giaCoBan * thue / 100);

            // Nếu thuế là số tiền cố định thì dùng dòng này thay thế:
            // long giaSauLai = giaCoBan + thue;

            basePriceCache.put(g.getHangGheId(), giaSauLai);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    private JButton createSeatButton(int chuyenBayId, GheDTO ghe, int r, int c) {
        long price = getSeatPrice(ghe);
        
        // Tạo nhãn ghế: Số hàng + Chữ cột (Ví dụ: 1A, 1B...)
        char colLetter = (char) ('A' + c - 1);
        String seatName = r + String.valueOf(colLetter);

        String label = "<html><center><b>" + seatName + "</b><br>" 
                     + PriceFormatter.formatSeatPrice(price) + "</center></html>";

        JButton btn = new JButton(label);
        btn.setPreferredSize(new Dimension(55, 45));
        btn.setMargin(new Insets(2, 2, 2, 2));
        btn.setFont(new Font("Arial", Font.PLAIN, 10));

        colorSeat(btn, ghe.getHangGheId());

        btn.addActionListener(e -> overrideSeatPrice(chuyenBayId, ghe, btn, r, c));

        return btn;
    }

    private long getSeatPrice(GheDTO ghe) {
        Long price = basePriceCache.get(ghe.getHangGheId());
        return (price != null) ? price : 0;
    }

    private void overrideSeatPrice(int chuyenBayId, GheDTO ghe, JButton btn, int r, int c) {
        String s = JOptionPane.showInputDialog(this, "Nhập giá mới cho ghế " + r + (char)('A'+c-1));

        if (s == null || s.trim().isEmpty()) return;

        try {
            BigDecimal newPrice = new BigDecimal(s);
            overrideDAO.insert(chuyenBayId, ghe.getGheId(), newPrice);
            JOptionPane.showMessageDialog(this, "Đã cập nhật giá thành công!");

            // Cập nhật lại giao diện nút mà không đóng cửa sổ
            char colLetter = (char) ('A' + c - 1);
            String seatName = r + String.valueOf(colLetter);
            btn.setText("<html><center><b>" + seatName + "</b><br>" 
                        + PriceFormatter.formatSeatPrice(newPrice.longValue()) + "</center></html>");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá nhập vào không hợp lệ (Phải là số).");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + ex.getMessage());
        }
    }

    private void colorSeat(JButton btn, Integer hangGheId) {
        if (hangGheId == null) return;
        switch (hangGheId) {
            case 3: // First Class
                btn.setBackground(new Color(255, 180, 180)); break;
            case 2: // Business
                btn.setBackground(new Color(255, 220, 150)); break;
            case 4: // Premium Economy
                btn.setBackground(new Color(210, 255, 210)); break;
            case 1: // Economy
                btn.setBackground(new Color(200, 230, 255)); break;
        }
    }

    private JPanel buildLegend() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setBorder(BorderFactory.createTitledBorder("Chú thích hạng ghế"));

        // Thứ tự từ cao cấp nhất đến bình dân
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