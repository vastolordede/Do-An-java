package flightbooking.gui.user.pnl;

import flightbooking.bus.SanBayBUS;
import flightbooking.dto.SanBayDTO;
import flightbooking.gui.user.common.AppNavigator;
import flightbooking.gui.user.theme.UserTheme;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PnlTimChuyenBay extends JPanel {

    private final AppNavigator nav;
    private final SanBayBUS sanBayBUS = new SanBayBUS();

    private final JComboBox<Item> cbDi = new JComboBox<>();
    private final JComboBox<Item> cbDen = new JComboBox<>();
    private final JSpinner spNgay = new JSpinner(
        new SpinnerDateModel()
);

    public static int SANBAY_DI_ID = -1;
    public static int SANBAY_DEN_ID = -1;
    public static String NGAY = "";

    public PnlTimChuyenBay(AppNavigator nav) {
        this.nav = nav;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(UserTheme.BG);
JSpinner.DateEditor editor = new JSpinner.DateEditor(spNgay, "yyyy-MM-dd");
spNgay.setEditor(editor);
        add(buildCard(), BorderLayout.NORTH);
        add(buildHero(), BorderLayout.CENTER);

        loadSanBay();
    }

    private JComponent buildHero() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel title = new JLabel("Tìm chuyến bay");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        title.setForeground(UserTheme.PRIMARY);

        JLabel sub = new JLabel("Chọn điểm đi, điểm đến và ngày bay");
        sub.setForeground(UserTheme.ACCENT);

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setOpaque(false);
        top.add(title);
        top.add(sub);

        p.add(top, BorderLayout.NORTH);
        return p;
    }

    private JPanel buildCard() {
        JPanel card = new JPanel(new GridLayout(3, 2, 12, 12));
        card.setBackground(UserTheme.CARD);
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        card.add(label("Sân bay đi"));
        card.add(cbDi);

        card.add(label("Sân bay đến"));
        card.add(cbDen);

        card.add(label("Ngày bay (yyyy-MM-dd)"));
        card.add(spNgay);

        // ✅ Dùng UserTheme.createButton thay vì primaryButton() cũ
        JButton btn = UserTheme.createButton("Tìm chuyến");
        btn.addActionListener(e -> timChuyen());

        JPanel wrap = new JPanel(new BorderLayout(12, 12));
        wrap.setOpaque(false);
        wrap.add(card, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        actions.add(btn);

        wrap.add(actions, BorderLayout.SOUTH);
        return wrap;
    }

    private JLabel label(String s) {
        JLabel lb = new JLabel(s);
        lb.setForeground(UserTheme.ACCENT);
        return lb;
    }

    // ❌ Đã xóa primaryButton() cũ — không dùng nữa

    private void loadSanBay() {
        cbDi.removeAllItems();
        cbDen.removeAllItems();
        List<SanBayDTO> list = sanBayBUS.dsSanBay();
        for (SanBayDTO s : list) {
            Item it = new Item(s.getSanBayId(), s.getTenSanBay());
            cbDi.addItem(it);
            cbDen.addItem(it);
        }
    }

    private void timChuyen() {
        Item di = (Item) cbDi.getSelectedItem();
        Item den = (Item) cbDen.getSelectedItem();
        java.util.Date date = (java.util.Date) spNgay.getValue();

String ngay = date.toInstant()
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalDate()
        .toString(); // vẫn giữ String cho code cũ

try {
    LocalDate.parse(ngay); // test format
} catch (Exception e) {
    JOptionPane.showMessageDialog(this, "Ngày không hợp lệ (yyyy-MM-dd)");
    return;
}

        if (di == null || den == null || ngay.isEmpty()) return;
        if (di.id == den.id) {
            JOptionPane.showMessageDialog(this, "Điểm đi và đến không được trùng nhau.");
            return;
        }

        SANBAY_DI_ID = di.id;
        SANBAY_DEN_ID = den.id;
        NGAY = ngay;

        nav.show("KQ_CHUYEN");

        Component comp = nav.get("KQ_CHUYEN");
        if (comp instanceof PnlKetQuaChuyenBay) {
            ((PnlKetQuaChuyenBay) comp).reload();
        }
    }

    private static class Item {
        final int id;
        final String text;

        Item(int id, String text) { this.id = id; this.text = text; }
        @Override public String toString() { return text; }
    }
}