package flightbooking.gui.common;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

public class AircraftLayoutPanel extends JPanel {

    private final JComponent seatMapComponent;

    public AircraftLayoutPanel(JComponent seatMapComponent) {
        this.seatMapComponent = seatMapComponent;

        setLayout(new GridBagLayout());
        setBackground(new Color(235, 235, 235));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;

        if (seatMapComponent instanceof JScrollPane) {
            JScrollPane scroll = (JScrollPane) seatMapComponent;
            scroll.setBorder(null);
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);
        } else {
            seatMapComponent.setOpaque(false);
        }

        add(seatMapComponent, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int panelW = getWidth();
            int panelH = getHeight();

            g2.setColor(new Color(238, 238, 238));
            g2.fillRect(0, 0, panelW, panelH);

            Dimension contentSize = resolveSeatContentSize();
            int seatW = Math.max(500, contentSize.width);
            int seatH = Math.max(260, contentSize.height);

            int planeH = Math.max(320, seatH + 90);

            int planeX = 10;
            int planeY = Math.max(18, (panelH - planeH) / 2);

            int noseW = 110;
            int backPadding = 40;
            int bodyW = seatW + 120;

            int maxPlaneW = panelW - 20;
            int planeW = noseW + bodyW + backPadding;
            if (planeW > maxPlaneW) {
                planeW = maxPlaneW;
                bodyW = planeW - noseW - backPadding;
            }

            int bodyX = planeX + noseW - 18;
            int bodyY = planeY;
            int bodyH = planeH;
            int tailRadius = 78;

            Path2D.Double planeShape = new Path2D.Double();
            planeShape.moveTo(bodyX + 12, bodyY);

            // cạnh trên
            planeShape.lineTo(bodyX + bodyW - tailRadius, bodyY);
            planeShape.quadTo(bodyX + bodyW, bodyY, bodyX + bodyW, bodyY + tailRadius);

            // cạnh phải
            planeShape.lineTo(bodyX + bodyW, bodyY + bodyH - tailRadius);
            planeShape.quadTo(bodyX + bodyW, bodyY + bodyH, bodyX + bodyW - tailRadius, bodyY + bodyH);

            // cạnh dưới
            planeShape.lineTo(bodyX + 12, bodyY + bodyH);

            // đầu trái bo tròn nhưng vẫn nhọn
            planeShape.quadTo(planeX + 8, bodyY + bodyH * 0.82, planeX + 2, bodyY + bodyH / 2.0);
            planeShape.quadTo(planeX + 8, bodyY + bodyH * 0.18, bodyX + 12, bodyY);

            planeShape.closePath();

            g2.setColor(Color.WHITE);
            g2.fill(planeShape);

            g2.setColor(new Color(120, 120, 120));
            g2.setStroke(new BasicStroke(3f));
            g2.draw(planeShape);

            // 2 khối gần đầu máy bay
            int block1W = 30;
int block1H = 96;
int block2W = 28;
int block2H = 60;

int leftBlockX1 = bodyX + 14;
int leftBlockY1 = bodyY + 118;

int leftBlockX2 = bodyX + 10;
int leftBlockY2 = bodyY + bodyH - 150;

drawCabinBlock(g2, leftBlockX1, leftBlockY1, block1W, block1H);
drawCabinBlock(g2, leftBlockX2, leftBlockY2, block2W, block2H);

            // cửa
            drawDoor(g2, bodyX + 32, bodyY + bodyH - 118, "DOOR");

            // WC
            drawWc(g2, bodyX + 50, bodyY + 55);
            drawWc(g2, bodyX + bodyW - 85, bodyY + 55);
            drawWc(g2, bodyX + 50, bodyY + bodyH - 95);
            drawWc(g2, bodyX + bodyW - 85, bodyY + bodyH - 95);

            // EXIT dưới
            drawExitBottom(g2, bodyX + bodyW / 2 - 60, bodyY + bodyH - 8);

        } finally {
            g2.dispose();
        }
    }

    private Dimension resolveSeatContentSize() {
        if (seatMapComponent instanceof JScrollPane) {
            JScrollPane scroll = (JScrollPane) seatMapComponent;
            JViewport vp = scroll.getViewport();
            if (vp != null && vp.getView() != null) {
                Dimension d = vp.getView().getPreferredSize();
                if (d != null) return d;
            }
            return scroll.getPreferredSize();
        }
        return seatMapComponent.getPreferredSize();
    }

    private void drawExitBottom(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(220, 30, 30));
        g2.setStroke(new BasicStroke(4f));
        g2.drawLine(x, y, x + 120, y);

        Polygon arrow = new Polygon();
        arrow.addPoint(x + 60, y + 12);
        arrow.addPoint(x + 52, y);
        arrow.addPoint(x + 68, y);
        g2.fillPolygon(arrow);

        g2.setFont(new Font("Arial", Font.BOLD, 11));
        g2.drawString("EXIT", x + 38, y + 26);
    }

    private void drawDoor(Graphics2D g2, int x, int y, String text) {
        g2.setColor(new Color(250, 250, 250));
        g2.fillRoundRect(x, y, 34, 54, 10, 10);
        g2.setColor(new Color(110, 110, 110));
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawRoundRect(x, y, 34, 54, 10, 10);

        g2.setFont(new Font("Arial", Font.BOLD, 9));
        FontMetrics fm = g2.getFontMetrics();
        int tx = x + (34 - fm.stringWidth(text)) / 2;
        g2.drawString(text, tx, y + 68);
    }

    private void drawWc(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(245, 245, 245));
        g2.fillRoundRect(x, y, 42, 28, 10, 10);
        g2.setColor(new Color(100, 100, 100));
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawRoundRect(x, y, 42, 28, 10, 10);

        g2.setFont(new Font("Arial", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();
        String text = "WC";
        int tx = x + (42 - fm.stringWidth(text)) / 2;
        int ty = y + ((28 - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(text, tx, ty);
    }

    private void drawCabinBlock(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(new Color(245, 245, 245));
        g2.fillRoundRect(x, y, w, h, 14, 14);
        g2.setColor(new Color(180, 180, 180));
        g2.setStroke(new BasicStroke(1.8f));
        g2.drawRoundRect(x, y, w, h, 14, 14);
    }
}