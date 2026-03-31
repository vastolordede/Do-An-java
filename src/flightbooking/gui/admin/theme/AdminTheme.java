package flightbooking.gui.admin.theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Enumeration;

public class AdminTheme {

    // ===== CORE =====
    public static final Color PRIMARY        = Color.decode("#D31232");
    public static final Color PRIMARY_DARK   = Color.decode("#B10F2A");

    public static final Color BG             = Color.decode("#ECECEC");
    public static final Color SIDEBAR_BG     = Color.decode("#D31232");
    public static final Color HEADER_BG      = Color.decode("#C9142F");
    public static final Color CONTENT_BG     = Color.decode("#ECECEC");

    public static final Color TEXT_DARK      = Color.BLACK;
    public static final Color BORDER_BLACK   = Color.BLACK;
    public static final Color BG_WHITE       = Color.WHITE;

    public static final Color CARD           = Color.WHITE;
    public static final Color TEXT_MUTED     = Color.decode("#410007");
    public static final Color TEXT_LIGHT     = Color.WHITE;
    public static final Color BORDER         = Color.decode("#E4E4E4");

    // ===== ACTION COLORS =====
    public static final Color SUCCESS        = Color.decode("#0F9D58");
    public static final Color SUCCESS_DARK   = Color.decode("#0B7D46");

    public static final Color WARNING        = Color.decode("#F4B400");
    public static final Color WARNING_DARK   = Color.decode("#D39A00");

    public static final Color DANGER         = Color.decode("#DB4437");
    public static final Color DANGER_DARK    = Color.decode("#B83228");

    public static final Color NEUTRAL_BTN    = Color.WHITE;
    public static final Color NEUTRAL_BORDER = Color.decode("#DADADA");
    public static final Color TABLE_HEADER   = Color.decode("#FFDAD8");

    public static final Color TABLE_ROW_1       = Color.WHITE;
    public static final Color TABLE_ROW_2       = Color.decode("#F7F7F7");
    public static final Color TABLE_SELECT      = Color.decode("#D8F6FF");
    public static final Color TABLE_HEADER_TEXT = Color.decode("#410007");
    public static final Color TABLE_BODY_TEXT   = Color.decode("#410007");
    public static final Color TEXT_STRONG       = Color.BLACK;

    public enum ButtonRole {
        ADD, EDIT, DELETE, NEUTRAL
    }

    public static void apply() {
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ignored) {}

    Font f = new Font("Segoe UI", Font.PLAIN, 13);
    for (Enumeration<Object> e = UIManager.getDefaults().keys(); e.hasMoreElements();) {
        Object key = e.nextElement();
        Object val = UIManager.get(key);
        if (val instanceof FontUIResource) {
            UIManager.put(key, new FontUIResource(f));
        }
    }

    UIManager.put("Panel.background", CONTENT_BG);
UIManager.put("OptionPane.background", CONTENT_BG);
UIManager.put("Table.selectionBackground", TABLE_SELECT);
UIManager.put("Table.selectionForeground", TEXT_DARK);
UIManager.put("ComboBox.selectionBackground", TABLE_SELECT);
UIManager.put("ComboBox.selectionForeground", Color.WHITE);
UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
UIManager.put("Table.scrollPaneBorder", BorderFactory.createEmptyBorder());

UIManager.put("Spinner.background", Color.WHITE);
UIManager.put("Spinner.foreground", TEXT_DARK);
UIManager.put("Spinner.border", BorderFactory.createEmptyBorder());
UIManager.put("Spinner.arrowButtonBorder", BorderFactory.createEmptyBorder());
}

    // =========================================================
    // SIDEBAR
    // =========================================================
    public static JButton createSidebarButton(String text, String iconPath) {

    final ImageIcon icon;
    if (iconPath != null) {
        java.net.URL url = AdminTheme.class.getResource(iconPath);
        if (url != null) {
            Image img = new ImageIcon(url).getImage()
                    .getScaledInstance(18, 18, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);
        } else {
            icon = null;
        }
    } else {
        icon = null;
    }

    JButton btn = new JButton(text) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color bg;
            if (getModel().isPressed()) {
                bg = PRIMARY_DARK;
            } else if (getModel().isRollover()) {
                bg = new Color(255, 255, 255, 35);
            } else {
                bg = new Color(255, 255, 255, 18);
            }

            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));

            g2.setColor(new Color(255, 255, 255, 190));
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new RoundRectangle2D.Float(0.6f, 0.6f, getWidth() - 1.2f, getHeight() - 1.2f, 12, 12));

            g2.setColor(TEXT_LIGHT);
            g2.setFont(getFont().deriveFont(Font.BOLD, 13f));
            FontMetrics fm = g2.getFontMetrics();
            int x = 14;

            if (icon != null) {
                int iconY = (getHeight() - icon.getIconHeight()) / 2;
                g2.drawImage(icon.getImage(), x, iconY, null);
                x += icon.getIconWidth() + 10;
            }

            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(getText(), x, y);

            g2.dispose();
        }
    };

    btn.setContentAreaFilled(false);
    btn.setBorderPainted(false);
    btn.setFocusPainted(false);
    btn.setOpaque(false);
    btn.setForeground(TEXT_LIGHT);
    btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
    btn.setHorizontalAlignment(SwingConstants.LEFT);
    btn.setPreferredSize(new Dimension(210, 42));
    btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

    return btn;
}

    // =========================================================
    // HEADER
    // =========================================================
    public static JButton createHeaderButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bg = getModel().isPressed()
                        ? PRIMARY_DARK
                        : getModel().isRollover()
                            ? new Color(255, 255, 255, 45)
                            : new Color(255, 255, 255, 25);

                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));

                g2.setColor(Color.WHITE);
                g2.setFont(getFont().deriveFont(Font.BOLD, 13f));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);

                g2.dispose();
            }
        };

        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setForeground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(130, 36));
        return btn;
    }

    // =========================================================
    // ACTION BUTTONS — factory method, đảm bảo paintComponent không bị LAF override
    // =========================================================
    public static JButton createActionButton(String text, ButtonRole role) {
    JButton btn = new JButton(text) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color fill;
            Color border;
            Color textColor = Color.WHITE;

            switch (role) {
                case ADD:
                    fill = new Color(33, 150, 243);      // xanh dương
                    border = new Color(25, 118, 210);
                    break;
                case EDIT:
                    fill = new Color(255, 193, 7);       // vàng
                    border = new Color(230, 170, 0);
                    textColor = Color.BLACK;
                    break;
                case DELETE:
                    fill = new Color(244, 67, 54);       // đỏ
                    border = new Color(211, 47, 47);
                    break;
                default:
                    fill = Color.WHITE;
                    border = BORDER_BLACK;
                    textColor = TEXT_DARK;
                    break;
            }

            if (getModel().isPressed()) {
                fill = fill.darker();
            } else if (getModel().isRollover()) {
                fill = role == ButtonRole.NEUTRAL ? new Color(245, 245, 245) : fill.brighter();
            }

            g2.setColor(fill);
            g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 16, 16);

            g2.setColor(border);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 16, 16);

            g2.setColor(textColor);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(getText(), x, y);

            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
        }
    };

    btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
    btn.setForeground(TEXT_DARK);
    btn.setContentAreaFilled(false);
    btn.setBorderPainted(false);
    btn.setFocusPainted(false);
    btn.setOpaque(false);
    btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btn.setPreferredSize(new Dimension(125, 38));
    btn.setBorder(new EmptyBorder(0, 14, 0, 14));
    return btn;
}

    // Giữ lại styleActionButton để không break code cũ ở các panel khác,
    // nhưng bên trong gọi createActionButton pattern
    public static void styleActionButton(JButton btn, ButtonRole role) {
        // Không dùng nữa — hãy dùng createActionButton thay thế
        // Giữ để tránh compile error ở các file chưa migrate
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(TEXT_DARK);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 36));
    }

    public static void styleActionButtonsRecursively(Container root) {
        for (Component comp : root.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                if (!(btn.getUI() instanceof RoundedButtonUI)) {
                    ButtonRole role = detectRole(btn.getText());
                    styleActionButton(btn, role);
                }
            }
            if (comp instanceof JTable) {
                styleTable((JTable) comp, false);
            }
            if (comp instanceof Container) {
                styleActionButtonsRecursively((Container) comp);
            }
        }
    }

    private static ButtonRole detectRole(String text) {
        if (text == null) return ButtonRole.NEUTRAL;
        String t = text.trim().toLowerCase();
        if (t.equals("thêm") || t.equals("tạo") || t.equals("tạo vé") || t.equals("lưu")) return ButtonRole.ADD;
        if (t.equals("sửa") || t.equals("cập nhật")) return ButtonRole.EDIT;
        if (t.equals("xóa") || t.equals("hủy vé") || t.equals("đăng xuất")) return ButtonRole.DELETE;
        return ButtonRole.NEUTRAL;
    }

    // Giữ lại để không break compile
    private static class RoundedButtonUI extends BasicButtonUI {
        private final ButtonRole role;
        RoundedButtonUI(ButtonRole role) { this.role = role; }
    }

    // =========================================================
    // TABLE
    // =========================================================
    public static void styleTable(JTable table, boolean keepRenderer) {
        table.setRowHeight(44);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT_DARK);
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(TABLE_SELECT);
        table.setSelectionForeground(TEXT_DARK);
        table.setGridColor(BORDER);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setBorder(null);

        JTableHeader header = table.getTableHeader();
        if (header != null) {
            header.setBackground(TABLE_HEADER);
            header.setForeground(TABLE_HEADER_TEXT);
            header.setFont(new Font("Segoe UI", Font.BOLD, 12));
            header.setPreferredSize(new Dimension(0, 42));
            header.setReorderingAllowed(false);
            header.setBorder(BorderFactory.createEmptyBorder());
            header.setOpaque(true);

            header.setDefaultRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                        JTable table, Object value, boolean isSelected,
                        boolean hasFocus, int row, int column) {
                    JLabel c = (JLabel) super.getTableCellRendererComponent(
                            table, value, isSelected, hasFocus, row, column);
                    c.setBackground(TABLE_HEADER);
                    c.setForeground(TABLE_HEADER_TEXT);
                    c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    c.setHorizontalAlignment(SwingConstants.LEFT);
                    c.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                    c.setOpaque(true);
                    return c;
                }
            });
        }

        if (!keepRenderer) {
            table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                        JTable table, Object value, boolean isSelected,
                        boolean hasFocus, int row, int column) {
                    JLabel c = (JLabel) super.getTableCellRendererComponent(
                            table, value, isSelected, hasFocus, row, column);
                    c.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                    c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    c.setForeground(TABLE_BODY_TEXT);

                    String colName = table.getColumnName(column).toLowerCase();
                    if (colName.contains("tên") || colName.contains("email")
                            || colName.contains("giá") || colName.contains("lương")) {
                        c.setForeground(TEXT_STRONG);
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    }

                    if (isSelected) {
                        c.setBackground(TABLE_SELECT);
                        c.setForeground(Color.BLACK);
                    } else {
                        c.setBackground(row % 2 == 0 ? TABLE_ROW_1 : TABLE_ROW_2);
                    }
                    return c;
                }
            });
        }
    }

    public static JScrollPane wrapTable(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(null);
        sp.setViewportBorder(null);
        sp.setUI(new javax.swing.plaf.basic.BasicScrollPaneUI());

        JPanel emptyCorner = new JPanel();
        emptyCorner.setBackground(Color.WHITE);
        sp.setCorner(JScrollPane.UPPER_RIGHT_CORNER, emptyCorner);
        sp.setCorner(JScrollPane.UPPER_LEFT_CORNER, emptyCorner);
        sp.setCorner(JScrollPane.LOWER_RIGHT_CORNER, emptyCorner);
        sp.setCorner(JScrollPane.LOWER_LEFT_CORNER, emptyCorner);
        sp.getViewport().setBackground(Color.WHITE);
        sp.getViewport().setBorder(null);
        return sp;
    }

    public class ShadowPanel extends JPanel {
        public ShadowPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            int shadowSize = 8;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(shadowSize, shadowSize, getWidth() - shadowSize, getHeight() - shadowSize, 16, 16);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - shadowSize, getHeight() - shadowSize, 16, 16);
            g2.dispose();
        }
    }

    public static JPanel createFormCard(LayoutManager layout) {
        JPanel card = new JPanel(layout);
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        return card;
    }

    public static void styleSoftTextField(JTextField field) {
    field.setPreferredSize(new Dimension(190, 38));
    field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    field.setBackground(Color.WHITE);
    field.setForeground(TEXT_DARK);
    field.setCaretColor(TEXT_DARK);
    field.setOpaque(true);

    field.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(18, new Color(35, 35, 35), 1.8f),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
    ));
}

    public static void styleSoftComboBox(JComboBox<?> cb) {
    cb.setPreferredSize(new Dimension(190, 38));
    cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    cb.setBackground(Color.decode("#D8F6FF"));
    cb.setForeground(TEXT_DARK);
    cb.setOpaque(false);
    cb.setFocusable(false);

    cb.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(18, new Color(35, 35, 35), 1.8f),
            BorderFactory.createEmptyBorder(4, 10, 4, 8)
    ));

    cb.setRenderer(new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            JLabel lb = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            lb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lb.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

            if (isSelected) {
                lb.setBackground(TABLE_SELECT);
                lb.setForeground(TEXT_DARK);
            } else {
                lb.setBackground(Color.WHITE);
                lb.setForeground(TEXT_DARK);
            }
            lb.setOpaque(true);
            return lb;
        }
    });

    cb.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
        @Override
        protected JButton createArrowButton() {
            JButton btn = new JButton("▼");
            btn.setBorder(BorderFactory.createEmptyBorder());
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setOpaque(false);
            btn.setForeground(TEXT_DARK);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            btn.setBackground(new Color(0, 0, 0, 0));
            return btn;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.decode("#D8F6FF"));
            g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 16, 16);
            g2.dispose();
        }
    });
}

    public static class RoundedBorder implements javax.swing.border.Border {
    private final int radius;
    private final Color color;
    private final float thickness;

    public RoundedBorder(int radius, Color color) {
        this(radius, color, 1.6f);
    }

    public RoundedBorder(int radius, Color color, float thickness) {
        this.radius = radius;
        this.color = color;
        this.thickness = thickness;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(6, 10, 6, 10);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(thickness));
        g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, radius, radius);
        g2.dispose();
    }
}

    public static void styleSoftSpinner(JSpinner sp) {
    sp.setPreferredSize(new Dimension(190, 38));
    sp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    sp.setBackground(Color.WHITE);
    sp.setForeground(TEXT_DARK);
    sp.setOpaque(true);

    sp.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(18, new Color(35, 35, 35), 1.8f),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
    ));

    JComponent editor = sp.getEditor();
    if (editor instanceof JSpinner.DefaultEditor) {
        JSpinner.DefaultEditor de = (JSpinner.DefaultEditor) editor;
        JTextField tf = de.getTextField();

        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setForeground(TEXT_DARK);
        tf.setCaretColor(TEXT_DARK);
        tf.setBackground(Color.WHITE);
        tf.setOpaque(true);
        tf.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
        tf.setDisabledTextColor(TEXT_DARK);
        tf.setSelectionColor(TABLE_SELECT);
        tf.setSelectedTextColor(TEXT_DARK);
        tf.setHorizontalAlignment(JTextField.RIGHT);
    }

    for (Component c : sp.getComponents()) {
        if (c instanceof JButton) {
            JButton btn = (JButton) c;
            btn.setBorder(BorderFactory.createEmptyBorder());
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setOpaque(false);
            btn.setBackground(new Color(0, 0, 0, 0));
            btn.setMargin(new Insets(0, 0, 0, 0));
        } else if (c instanceof JComponent) {
            ((JComponent) c).setBorder(BorderFactory.createEmptyBorder());
        }
    }

    sp.revalidate();
    sp.repaint();
}

    public static JPanel wrapFormCard(JComponent form, JButton... buttons) {
    JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
    actions.setOpaque(true);
    actions.setBackground(Color.WHITE);

    for (JButton b : buttons) {
        actions.add(b);
    }

    JPanel card = createFormCard(new BorderLayout(0, 10));
    card.setOpaque(true);
    card.setBackground(Color.WHITE);

    form.setOpaque(true);
    form.setBackground(Color.WHITE);

    card.add(form, BorderLayout.CENTER);
    card.add(actions, BorderLayout.SOUTH);
    return card;
}

    public static class RoundedTextField extends JTextField {
        private final int radius = 18;

        public RoundedTextField() {
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
            setBackground(Color.decode("#D8F6FF"));
            setForeground(TEXT_DARK);
            setCaretColor(TEXT_DARK);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {}

        @Override
        public boolean contains(int x, int y) {
            Shape s = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius);
            return s.contains(x, y);
        }
    }

    public static class RoundedComboBox<E> extends JComboBox<E> {
        private final int radius = 18;

        public RoundedComboBox() {
            setOpaque(false);
            setBackground(Color.decode("#D8F6FF"));
            setForeground(TEXT_DARK);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setBorder(BorderFactory.createEmptyBorder(2, 12, 2, 12));

            setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {
                    JLabel lb = (JLabel) super.getListCellRendererComponent(
                            list, value, index, isSelected, cellHasFocus);
                    lb.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                    lb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    return lb;
                }
            });

            setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
                @Override
                protected JButton createArrowButton() {
                    JButton btn = new JButton("▼");
                    btn.setBorder(BorderFactory.createEmptyBorder());
                    btn.setContentAreaFilled(false);
                    btn.setFocusPainted(false);
                    btn.setOpaque(false);
                    btn.setForeground(TEXT_DARK);
                    btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    return btn;
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {}

        @Override
        public boolean contains(int x, int y) {
            Shape s = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius);
            return s.contains(x, y);
        }
    }

    private ImageIcon getIcon(String name) {
    // Giả sử icon để trong src/resources/icons/
    String path = "/icons/" + name;
    try {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            // Resize về kích thước chuẩn (ví dụ 20x20)
            Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
    } catch (Exception e) {
        System.err.println("Không tìm thấy icon: " + path);
    }
    return null;
}
}