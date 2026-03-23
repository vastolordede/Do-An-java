package flightbooking.gui.user.theme;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Enumeration;

public class UserTheme {
    public static final Color PRIMARY = Color.decode("#E51937");
    public static final Color ACCENT  = Color.decode("#102469");
    public static final Color BG      = Color.decode("#F7F8FB");
    public static final Color CARD    = Color.WHITE;

    public static void apply() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Font chung
        Font f = new Font("Segoe UI", Font.PLAIN, 13);
        for (Enumeration<Object> e = UIManager.getDefaults().keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            Object val = UIManager.get(key);
            if (val instanceof FontUIResource) UIManager.put(key, new FontUIResource(f));
        }

        UIManager.put("Panel.background", BG);

        // ❌ KHÔNG dùng UIManager cho Button — Windows LAF sẽ ignore hoặc đè trắng
        // Thay vào đó dùng createButton() factory bên dưới

        UIManager.put("Table.selectionBackground", PRIMARY);
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("ComboBox.selectionBackground", PRIMARY);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
    }

    /**
     * Tạo nút PRIMARY đỏ — vẽ thủ công để tránh Windows LAF override màu trắng.
     * Dùng hàm này thay cho "new JButton(...)" ở mọi nơi trong app.
     */
    public static JButton createButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Nền đỏ PRIMARY (hover thì tối hơn)
                Color bg = getModel().isPressed()
                        ? PRIMARY.darker()
                        : getModel().isRollover()
                            ? PRIMARY.brighter()
                            : PRIMARY;

                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));

                // Chữ trắng, căn giữa
                g2.setColor(Color.WHITE);
                g2.setFont(getFont().deriveFont(Font.BOLD, 13f));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()  - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                // Không vẽ border mặc định (tránh viền xấu của Windows LAF)
            }
        };

        btn.setContentAreaFilled(false); // Tắt fill mặc định của Swing
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);       // fallback nếu paintComponent bị skip
        btn.setPreferredSize(new Dimension(140, 36));
        return btn;
    }

    /**
     * Nút outline (viền đỏ, nền trong suốt) — dùng cho hành động phụ.
     */
    public static JButton createOutlineButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isRollover()) {
                    g2.setColor(new Color(229, 25, 55, 20));
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                }

                // Viền đỏ
                g2.setColor(PRIMARY);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 8, 8));

                // Chữ đỏ
                g2.setFont(getFont().deriveFont(Font.BOLD, 13f));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()  - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.setColor(PRIMARY);
                g2.drawString(getText(), x, y);
                g2.dispose();
            }

            @Override protected void paintBorder(Graphics g) {}
        };

        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(140, 36));
        return btn;
    }
}