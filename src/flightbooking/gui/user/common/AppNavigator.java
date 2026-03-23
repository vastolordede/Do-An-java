package flightbooking.gui.user.common;

import javax.swing.*;
import flightbooking.gui.user.pnl.PnlChonGhe;
import flightbooking.gui.user.pnl.PnlThongTinHanhKhach;
import java.awt.*;
import java.util.*;

public class AppNavigator {

    private final JPanel root = new JPanel(new BorderLayout());
    private final JPanel content = new JPanel(new CardLayout());
    private final Map<String, JComponent> screens = new HashMap<>();

    // 🔥 Dùng LinkedHashMap để giữ thứ tự
    private final Map<String, SidebarItem> sidebarItems = new LinkedHashMap<>();

    public AppNavigator() {
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(content, BorderLayout.CENTER);
    }

    public JPanel getRoot() { return root; }

    public void register(String name, JComponent comp) {
        screens.put(name, comp);
        content.add(comp, name);
    }

    public void show(String name) {
        // 🔥 Reset tất cả, active đúng cái
        sidebarItems.forEach((k, v) -> v.setActive(k.equals(name)));

        JComponent comp = screens.get(name);
        if (comp instanceof PnlChonGhe)
            ((PnlChonGhe) comp).reload();
        if (comp instanceof PnlThongTinHanhKhach)
            ((PnlThongTinHanhKhach) comp).reload();

        ((CardLayout) content.getLayout()).show(content, name);
    }

    private JComponent buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));
        sidebar.setPreferredSize(new Dimension(180, 0));

        sidebar.add(Box.createVerticalStrut(8));
        addItem(sidebar, "Tìm chuyến", "TIM_CHUYEN");
        addItem(sidebar, "Kết quả",    "KQ_CHUYEN");
        addItem(sidebar, "Chọn ghế",   "CHON_GHE");
        addItem(sidebar, "Hành khách", "HANH_KHACH");
        addItem(sidebar, "Xác nhận",   "XAC_NHAN");
        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    private void addItem(JPanel sidebar, String label, String screenName) {
        SidebarItem item = new SidebarItem(label, () -> show(screenName));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        sidebarItems.put(screenName, item);
        sidebar.add(item);
    }

    public JComponent get(String name) { return screens.get(name); }
}