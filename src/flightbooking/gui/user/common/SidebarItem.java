package flightbooking.gui.user.common;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class SidebarItem extends JButton {

    private static final Color BG_NORMAL = Color.WHITE;
    private static final Color BG_HOVER  = new Color(255, 235, 235);
    private static final Color BG_ACTIVE = new Color(255, 235, 235);
    private static final Color FG_NORMAL = new Color(50, 50, 50);
    private static final Color FG_ACTIVE = new Color(200, 0, 0);

    private boolean active = false;
    private boolean hovering = false;

    public SidebarItem(String text, Runnable action) {
        setText(text);
        setUI(new BasicButtonUI()); // 🔥 chặn L&F override
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);          // 🔥 để paintComponent tự xử lý
        setContentAreaFilled(false);
        setHorizontalAlignment(SwingConstants.LEFT);
        setFont(getFont().deriveFont(Font.PLAIN, 13f));
        setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 10));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setForeground(FG_NORMAL);

        addActionListener(e -> action.run());

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!active) {
                    hovering = true;
                    setForeground(FG_ACTIVE);
                    repaint();
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!active) {
                    hovering = false;
                    setForeground(FG_NORMAL);
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 🔥 Tự vẽ background, không phụ thuộc Swing L&F
        Color bg;
        if (active)        bg = BG_ACTIVE;
        else if (hovering) bg = BG_HOVER;
        else               bg = BG_NORMAL;

        g.setColor(bg);
        g.fillRect(0, 0, getWidth(), getHeight());

        super.paintComponent(g);
    }

    public void setActive(boolean active) {
        this.active = active;
        hovering = false;
        setForeground(active ? FG_ACTIVE : FG_NORMAL);
        setFont(getFont().deriveFont(active ? Font.BOLD : Font.PLAIN, 13f));
        repaint();
    }
}