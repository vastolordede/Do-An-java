package flightbooking.gui.user.pnl;

import flightbooking.bus.DatVeBUS;
import flightbooking.dto.HanhKhachDTO;
import flightbooking.gui.user.common.AppNavigator;
import flightbooking.gui.user.common.TempVeStore;
import flightbooking.gui.user.theme.UserTheme;

import javax.swing.*;
import java.awt.*;

public class PnlThongTinHanhKhach extends JPanel {

    private final AppNavigator nav;

    public static String HO_TEN = "";
    public static String SO_GIAY_TO = "";

    private final JTextField txtHoTen = new JTextField();
    private final JTextField txtSoGiayTo = new JTextField();

    public PnlThongTinHanhKhach(AppNavigator nav) {
        this.nav = nav;

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(UserTheme.BG);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCard(), BorderLayout.CENTER);
        add(buildActions(), BorderLayout.SOUTH);
    }

    private JComponent buildHeader() {
        JLabel lb = new JLabel("Thông tin hành khách");
        lb.setFont(lb.getFont().deriveFont(Font.BOLD, 22f));
        lb.setForeground(UserTheme.PRIMARY);
        return lb;
    }

    private JComponent buildCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UserTheme.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(8, 0, 8, 16);
        lc.gridx = 0;

        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;
        fc.insets = new Insets(8, 0, 8, 0);
        fc.gridx = 1;

        // Họ tên
        lc.gridy = 0; fc.gridy = 0;
        card.add(label("Họ tên"), lc);
        styleField(txtHoTen);
        card.add(txtHoTen, fc);

        // Số giấy tờ
        lc.gridy = 1; fc.gridy = 1;
        card.add(label("Số giấy tờ"), lc);
        styleField(txtSoGiayTo);
        card.add(txtSoGiayTo, fc);

        // Wrap để không bị giãn chiều dọc
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(card, BorderLayout.NORTH);
        return wrap;
    }

    private void styleField(JTextField field) {
        field.setPreferredSize(new Dimension(300, 36));
        field.setFont(field.getFont().deriveFont(13f));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
    }

    private JLabel label(String s) {
        JLabel lb = new JLabel(s);
        lb.setForeground(UserTheme.ACCENT);
        return lb;
    }

    private JComponent buildActions() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setOpaque(false);

        // ✅ Dùng UserTheme thay vì primaryButton/ghostButton cũ
        JButton back = UserTheme.createOutlineButton("← Quay lại");
        JButton next = UserTheme.createButton("Xác nhận →");

        back.addActionListener(e -> {
            nav.show("CHON_GHE");
            Component comp = nav.get("CHON_GHE");
            if (comp instanceof PnlChonGhe) {
                ((PnlChonGhe) comp).reload();
            }
        });

        next.addActionListener(e -> {
            HO_TEN = txtHoTen.getText().trim();
            SO_GIAY_TO = txtSoGiayTo.getText().trim();
            if (HO_TEN.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên.");
                return;
            }

            DatVeBUS.ThongTinHanhKhachVaGhe item = new DatVeBUS.ThongTinHanhKhachVaGhe();

            HanhKhachDTO hk = new HanhKhachDTO();
            hk.setHoTen(HO_TEN);
            hk.setSoGiayTo(SO_GIAY_TO);

            item.setHanhKhach(hk);
            item.setGheId(PnlChonGhe.GHE_ID_DANG_CHON);

            TempVeStore.upsertCurrent(item);
            PnlChonGhe.GHE_ID_DANG_CHON = null;

            nav.show("XAC_NHAN");
            SwingUtilities.invokeLater(() -> {
                Component comp = nav.get("XAC_NHAN");
                if (comp instanceof PnlXacNhanDatVe) {
                    ((PnlXacNhanDatVe) comp).reload();
                }
            });
        });

        p.add(back);
        p.add(next);
        return p;
    }

    public void reload() {
        DatVeBUS.ThongTinHanhKhachVaGhe current = TempVeStore.getCurrent();

        if (current != null && current.getHanhKhach() != null) {
            txtHoTen.setText(current.getHanhKhach().getHoTen());
            txtSoGiayTo.setText(current.getHanhKhach().getSoGiayTo());
        } else {
            txtHoTen.setText("");
            txtSoGiayTo.setText("");
        }
    }
}