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

    public static final Color CARD           = Color.WHITE;
    public static final Color TEXT_DARK      = Color.decode("#410007");
public static final Color TEXT_MUTED     = Color.decode("#410007");
    public static final Color TEXT_LIGHT     = Color.WHITE;
    public static final Color BORDER         = Color.decode("#E4E4E4");

    // ===== ACTION COLORS =====
    public static final Color SUCCESS        = Color.decode("#0F9D58"); // thêm
    public static final Color SUCCESS_DARK   = Color.decode("#0B7D46");

    public static final Color WARNING        = Color.decode("#F4B400"); // sửa
    public static final Color WARNING_DARK   = Color.decode("#D39A00");

    public static final Color DANGER         = Color.decode("#DB4437"); // xóa
    public static final Color DANGER_DARK    = Color.decode("#B83228");

    public static final Color NEUTRAL_BTN    = Color.WHITE;
    public static final Color NEUTRAL_BORDER = Color.decode("#DADADA");
    public static final Color TABLE_HEADER       = Color.decode("#FFDAD8");

public static final Color TABLE_ROW_1        = Color.WHITE;
public static final Color TABLE_ROW_2        = Color.decode("#F7F7F7");

public static final Color TABLE_SELECT = Color.decode("#D8F6FF");// 👈 màu bạn yêu cầu

public static final Color TABLE_HEADER_TEXT  = Color.decode("#410007"); // 👈 header
public static final Color TABLE_BODY_TEXT    = Color.decode("#410007"); // 👈 chữ thường
public static final Color TEXT_STRONG        = Color.BLACK;             // 👈 tên/email/giá

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

// thêm 2 dòng này ở CUỐI hàm apply
UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
UIManager.put("Table.scrollPaneBorder", BorderFactory.createEmptyBorder());
    }

    // =========================================================
    // SIDEBAR
    // =========================================================
    public static JButton createSidebarButton(String text) {
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
    // ACTION BUTTONS
    // =========================================================
    public static void styleActionButton(JButton btn, ButtonRole role) {
        btn.setUI(new RoundedButtonUI(role));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(TEXT_DARK);
        btn.setPreferredSize(new Dimension(
                Math.max(btn.getPreferredSize().width, 100),
                36
        ));
    }

    public static void styleActionButtonsRecursively(Container root) {
        for (Component comp : root.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;

                // bỏ qua 2 loại custom riêng
                if (btn.getParent() != null && btn.getParent().getBackground() != null) {
                    // vẫn cho đi tiếp, nhưng không style sidebar/header nếu đã custom tay
                }

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

        if (t.equals("thêm") || t.equals("tạo") || t.equals("tạo vé") || t.equals("lưu")) {
            return ButtonRole.ADD;
        }

        if (t.equals("sửa") || t.equals("cập nhật")) {
            return ButtonRole.EDIT;
        }

        if (t.equals("xóa") || t.equals("hủy vé") || t.equals("đăng xuất")) {
            return ButtonRole.DELETE;
        }

        return ButtonRole.NEUTRAL;
    }

    private static class RoundedButtonUI extends BasicButtonUI {
        private final ButtonRole role;

        RoundedButtonUI(ButtonRole role) {
            this.role = role;
        }

        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            c.setBorder(new EmptyBorder(0, 14, 0, 14));
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            JButton b = (JButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color fill;
            Color border;
            Color text;

            switch (role) {
                case ADD:
                    fill   = b.getModel().isPressed() ? SUCCESS_DARK : SUCCESS;
                    border = fill;
                    text   = Color.WHITE;
                    break;
                case EDIT:
                    fill   = b.getModel().isPressed() ? WARNING_DARK : WARNING;
                    border = fill;
                    text   = Color.WHITE;
                    break;
                case DELETE:
                    fill   = b.getModel().isPressed() ? DANGER_DARK : DANGER;
                    border = fill;
                    text   = Color.WHITE;
                    break;
                default:
                    fill   = b.getModel().isPressed() ? new Color(235, 235, 235)
                            : b.getModel().isRollover() ? new Color(248, 248, 248)
                            : NEUTRAL_BTN;
                    border = NEUTRAL_BORDER;
                    text   = TEXT_DARK;
                    break;
            }

            g2.setColor(fill);
            g2.fill(new RoundRectangle2D.Float(0, 0, c.getWidth(), c.getHeight(), 14, 14));

            g2.setColor(border);
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new RoundRectangle2D.Float(0.6f, 0.6f, c.getWidth() - 1.2f, c.getHeight() - 1.2f, 14, 14));

            g2.setColor(text);
            g2.setFont(b.getFont());
            FontMetrics fm = g2.getFontMetrics();
            int x = (c.getWidth() - fm.stringWidth(b.getText())) / 2;
            int y = (c.getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(b.getText(), x, y);

            g2.dispose();
        }
    }

    // =========================================================
    // TABLE
    // keepRenderer = true: chỉ style khung/header/height, không đè renderer cũ
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
                boolean hasFocus, int row, int column
        ) {
            JLabel c = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column
            );

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
                boolean hasFocus, int row, int column
        ) {
            JLabel c = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column
            );

            c.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            c.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            // ===== MẶC ĐỊNH =====
            c.setForeground(TABLE_BODY_TEXT);

            // ===== CÁC CỘT NHẤN (Tên / Email / Giá / Lương) =====
            String colName = table.getColumnName(column).toLowerCase();

            if (colName.contains("tên")
                    || colName.contains("email")
                    || colName.contains("giá")
                    || colName.contains("lương")) {

                c.setForeground(TEXT_STRONG);
                c.setFont(c.getFont().deriveFont(Font.BOLD));
            }

            // ===== SELECT =====
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
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        // ===== SHADOW =====
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillRoundRect(
                shadowSize, shadowSize,
                getWidth() - shadowSize,
                getHeight() - shadowSize,
                16, 16
        );

        // ===== CARD =====
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(
                0, 0,
                getWidth() - shadowSize,
                getHeight() - shadowSize,
                16, 16
        );

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
    field.setPreferredSize(new Dimension(180, 38));
    field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    field.setBackground(Color.decode("#D8F6FF"));
    field.setForeground(TEXT_DARK);
    field.setCaretColor(TEXT_DARK);
    field.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
    field.setOpaque(false);
}

public static void styleSoftComboBox(JComboBox<?> cb) {
    UIManager.put("ComboBox.background", Color.decode("#D8F6FF"));
UIManager.put("ComboBox.foreground", TEXT_DARK);
UIManager.put("ComboBox.selectionBackground", Color.decode("#D8F6FF"));
    cb.setPreferredSize(new Dimension(180, 38));
    cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    cb.setBackground(Color.decode("#D8F6FF"));
    cb.setForeground(TEXT_DARK);
    cb.setBorder(BorderFactory.createEmptyBorder(2, 12, 2, 12));
    cb.setOpaque(false);
}

public static void styleSoftSpinner(JSpinner sp) {
    sp.setPreferredSize(new Dimension(180, 38));
    sp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    sp.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));

    JComponent editor = sp.getEditor();
    if (editor instanceof JSpinner.DefaultEditor) {
        JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
        tf.setBackground(Color.decode("#D8F6FF"));
        tf.setForeground(TEXT_DARK);
        tf.setBorder(null);
    }

    sp.setOpaque(true);
    sp.setBackground(Color.decode("#D8F6FF"));
}

public static JPanel wrapFormCard(JComponent form, JButton... buttons) {
    JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
    actions.setOpaque(false);
    for (JButton b : buttons) {
        actions.add(b);
    }

    JPanel card = createFormCard(new BorderLayout(0, 10));
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
    protected void paintBorder(Graphics g) {
        // bỏ border
    }

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
                    boolean isSelected, boolean cellHasFocus
            ) {
                JLabel lb = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus
                );
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
    protected void paintBorder(Graphics g) {
        // bỏ border
    }

    @Override
    public boolean contains(int x, int y) {
        Shape s = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius);
        return s.contains(x, y);
    }
}
}