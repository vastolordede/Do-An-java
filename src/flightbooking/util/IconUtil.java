package flightbooking.util;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class IconUtil {

    // cache để không load lại nhiều lần
    private static final Map<String, ImageIcon> CACHE = new HashMap<>();

    // ===== LOAD ICON =====
    public static ImageIcon getIcon(String path, int w, int h) {
        String key = path + "_" + w + "_" + h;

        if (CACHE.containsKey(key)) return CACHE.get(key);

        URL url = IconUtil.class.getResource("/" + path);
        if (url == null) {
            System.err.println("Không tìm thấy icon: " + path);
            return null;
        }

        Image img = new ImageIcon(url).getImage();
        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaled);

        CACHE.put(key, icon);
        return icon;
    }

    // ===== GẮN ICON CHO BUTTON =====
    public static void setIcon(JButton btn, String path, int size) {
        btn.setIcon(getIcon(path, size, size));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // ===== GẮN ICON CHO LABEL =====
    public static void setIcon(JLabel lbl, String path, int size) {
        lbl.setIcon(getIcon(path, size, size));
    }

    // ===== BUTTON ICON ONLY =====
    public static JButton createIconButton(String path, int size) {
        JButton btn = new JButton();
        setIcon(btn, path, size);
        return btn;
    }
}