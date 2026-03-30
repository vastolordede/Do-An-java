package flightbooking.gui.admin.pnl;

import flightbooking.bus.NhomQuyenBUS;
import flightbooking.dao.NhomQuyenDAO;
import flightbooking.dao.QuyenDAO;
import flightbooking.dao.QuyenActionDAO;
import flightbooking.dto.NhomQuyenDTO;
import flightbooking.dto.QuyenDTO;
import flightbooking.gui.admin.theme.AdminTheme;
import flightbooking.util.ActionConstants;
import flightbooking.dto.QuyenActionDTO;
import flightbooking.util.Validator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class PnlNhomQuyen extends JPanel {

    private final JTextField txtTen = new JTextField();
    private final JPanel pnlCheck = new JPanel();

    private final List<JCheckBox> checkBoxes = new ArrayList<>();
    private final Map<Integer, JPanel> mapPanelCon = new LinkedHashMap<>();
    private final Map<String, Integer> actionMap = new HashMap<>();

    private final NhomQuyenBUS bus = new NhomQuyenBUS();

    private JTable tbl;
    private DefaultTableModel model;

    private JButton btnSave;
    private JButton btnUpdate;
    private JButton btnDelete;

    private static final Map<String, String[]> ACTION_MAP = new HashMap<>();
    static {
        ACTION_MAP.put("quản lý sân bay", new String[]{"Thêm", "Sửa", "Xóa", "Xuất Excel", "Nhập Excel"});
        ACTION_MAP.put("quản lý tuyến bay", new String[]{"Thêm", "Sửa", "Xóa", "Xuất Excel", "Nhập Excel"});
        ACTION_MAP.put("quản lý chuyến bay", new String[]{"Thêm", "Sửa", "Xóa", "Giá hạng ghế", "Sơ đồ ghế", "Xuất Excel", "Nhập Excel"});
        ACTION_MAP.put("đặt vé (quầy)", new String[]{"Tạo vé", "Xuất Excel", "Nhập Excel"});
        ACTION_MAP.put("quản lý vé", new String[]{"Hủy vé", "Xuất Excel", "Nhập Excel"});
        ACTION_MAP.put("quản lý hãng hàng không", new String[]{"Thêm", "Sửa", "Xóa", "Xuất Excel", "Nhập Excel"});
        ACTION_MAP.put("quản lý máy bay", new String[]{"Thêm", "Sửa", "Xóa", "Tạo ghế", "Xuất Excel", "Nhập Excel"});
        ACTION_MAP.put("quản lý nhân viên", new String[]{"Thêm", "Sửa", "Xóa", "Phân quyền", "Xuất Excel", "Nhập Excel"});
        ACTION_MAP.put("quản lý nhóm quyền", new String[]{"Thêm", "Sửa", "Xóa", "Xuất Excel", "Nhập Excel"});
        ACTION_MAP.put("thống kê", new String[]{"Xuất Excel"});
    }

    private static final String[] DEFAULT_ACTIONS = {"Thêm", "Sửa", "Xóa"};

    public PnlNhomQuyen() {
        setLayout(new BorderLayout(10, 10));
        add(buildTop(), BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ID", "Tên"}, 0);
        tbl = new JTable(model);
        tbl.setPreferredScrollableViewportSize(new Dimension(250, 0));
        AdminTheme.styleTable(tbl, false);
        add(AdminTheme.wrapTable(tbl), BorderLayout.WEST);

        pnlCheck.setLayout(new BoxLayout(pnlCheck, BoxLayout.Y_AXIS));
        JScrollPane spCheck = new JScrollPane(pnlCheck);
        spCheck.setBorder(null);
        spCheck.setViewportBorder(null);
        add(spCheck, BorderLayout.CENTER);

        loadActionMap();
        loadQuyen();
        loadTable();
        bindTable();
    }

    private JPanel buildTop() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints lc = makeLc();
        GridBagConstraints fc = makeFc();

        lc.gridx = 0; lc.gridy = 0; form.add(makeLabel("Tên nhóm quyền"), lc);
        fc.gridx = 1; fc.gridy = 0; fc.gridwidth = 3;
        AdminTheme.styleSoftTextField(txtTen);
        form.add(txtTen, fc);
        fc.gridwidth = 1;

        // ✅ Dùng createActionButton
        btnSave   = AdminTheme.createActionButton("Tạo",       AdminTheme.ButtonRole.NEUTRAL);
        btnUpdate = AdminTheme.createActionButton("Cập nhật",  AdminTheme.ButtonRole.NEUTRAL);
        btnDelete = AdminTheme.createActionButton("Xóa",       AdminTheme.ButtonRole.NEUTRAL);

        btnSave.addActionListener(e -> save());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());

        return AdminTheme.wrapFormCard(form, btnSave, btnUpdate, btnDelete);
    }

    private GridBagConstraints makeLc() {
        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(6, 4, 6, 6);
        return lc;
    }

    private GridBagConstraints makeFc() {
        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;
        fc.insets = new Insets(6, 0, 6, 12);
        return fc;
    }

    private JLabel makeLabel(String text) {
        JLabel lb = new JLabel(text);
        lb.setFont(lb.getFont().deriveFont(Font.PLAIN, 13f));
        return lb;
    }

    private void loadActionMap() {
        List<QuyenActionDTO> list = new QuyenActionDAO().findAll();
        for (QuyenActionDTO a : list) {
            actionMap.put(a.getTenquyen().toLowerCase(), a.getId());
        }
    }

    private void loadQuyen() {
    List<QuyenDTO> list = new QuyenDAO().findAll();

    for (QuyenDTO q : list) {
        int qid = q.getQuyenId();
        String tenQuyen = q.getTenQuyen();

        JCheckBox cbCha = new JCheckBox(tenQuyen);
        cbCha.setFont(cbCha.getFont().deriveFont(Font.BOLD));
        cbCha.putClientProperty("id", qid);
        checkBoxes.add(cbCha);

        String[] actions = ACTION_MAP.getOrDefault(
                tenQuyen.toLowerCase().trim(), DEFAULT_ACTIONS);

        JPanel panelCon = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCon.setBorder(BorderFactory.createEmptyBorder(0, 24, 4, 0));
        
        // --- CHỈNH SỬA Ở ĐÂY: Luôn hiện panel con ---
        panelCon.setVisible(true); 

        for (String action : actions) {
            JCheckBox cbCon = new JCheckBox(action);
            Integer actionId = actionMap.get(action.toLowerCase());
            if (actionId != null) cbCon.putClientProperty("id", actionId);
            panelCon.add(cbCon);
        }

        mapPanelCon.put(qid, panelCon);

        // --- CẬP NHẬT LOGIC CLICK: Chỉ xử lý việc tick, không ẩn/hiện nữa ---
        cbCha.addActionListener(e -> {
            boolean checked = cbCha.isSelected();
            if (checked) {
                // Kiểm tra xem đã có hành động nào được tick chưa (trường hợp load từ DB)
                boolean hasAnyChecked = false;
                for (Component c : panelCon.getComponents()) {
                    if (c instanceof JCheckBox && ((JCheckBox) c).isSelected()) {
                        hasAnyChecked = true;
                        break;
                    }
                }
                // Nếu chưa có cái nào được tick (tạo mới hoàn toàn), thì tự động tick hết
                if (!hasAnyChecked) {
                    for (Component c : panelCon.getComponents()) {
                        if (c instanceof JCheckBox) ((JCheckBox) c).setSelected(true);
                    }
                }
            } else {
                // Nếu bỏ chọn quyền cha, hủy sạch quyền con
                for (Component c : panelCon.getComponents()) {
                    if (c instanceof JCheckBox) ((JCheckBox) c).setSelected(false);
                }
            }
        });

        JPanel row = new JPanel(new BorderLayout());
        row.add(cbCha, BorderLayout.NORTH);
        row.add(panelCon, BorderLayout.CENTER);
        pnlCheck.add(row);
    }
}

    private void loadTable() {
        model.setRowCount(0);
        List<NhomQuyenDTO> list = new NhomQuyenDAO().findAll();
        for (NhomQuyenDTO n : list) {
            model.addRow(new Object[]{n.getNhomQuyenId(), n.getTenNhomQuyen()});
        }
    }

    private void bindTable() {
    tbl.getSelectionModel().addListSelectionListener(e -> {
        int row = tbl.getSelectedRow();
        if (row < 0) return;

        int id = (int) model.getValueAt(row, 0);
        txtTen.setText(model.getValueAt(row, 1).toString());

        List<Integer> perms = new NhomQuyenDAO().getPermissionsByNhom(id);

        for (JCheckBox cb : checkBoxes) {
            int qid = (int) cb.getClientProperty("id");
            boolean has = perms.contains(qid);
            cb.setSelected(has);

            JPanel panelCon = mapPanelCon.get(qid);
            if (panelCon != null) {
                // --- CHỈNH SỬA Ở ĐÂY: Xóa hoặc comment dòng này ---
                // panelCon.setVisible(has); 
                
                // Luôn đảm bảo nó hiện ra
                panelCon.setVisible(true);

                List<Integer> actionIds = new NhomQuyenDAO().getActionByNhomAndQuyen(id, qid);
                for (Component c : panelCon.getComponents()) {
                    if (c instanceof JCheckBox) {
                        JCheckBox cbCon = (JCheckBox) c;
                        Integer aid = (Integer) cbCon.getClientProperty("id");
                        if (aid != null) cbCon.setSelected(actionIds.contains(aid));
                    }
                }
            }
        }
        pnlCheck.revalidate();
        pnlCheck.repaint();
    });
}

    private List<Integer> getSelected() {
        List<Integer> list = new ArrayList<>();
        for (JCheckBox cb : checkBoxes) {
            if (cb.isSelected()) list.add((Integer) cb.getClientProperty("id"));
        }
        return list;
    }

    private Map<Integer, List<Integer>> getSelectedActions() {
        Map<Integer, List<Integer>> result = new HashMap<>();
        for (JCheckBox cbCha : checkBoxes) {
            int qid = (Integer) cbCha.getClientProperty("id");
            if (!cbCha.isSelected()) continue;
            JPanel panelCon = mapPanelCon.get(qid);
            List<Integer> actionIds = new ArrayList<>();
            if (panelCon != null) {
                for (Component c : panelCon.getComponents()) {
                    if (c instanceof JCheckBox) {
                        JCheckBox cbCon = (JCheckBox) c;
                        if (cbCon.isSelected()) {
                            Integer aid = (Integer) cbCon.getClientProperty("id");
                            if (aid != null) actionIds.add(aid);
                        }
                    }
                }
            }
            result.put(qid, actionIds);
        }
        return result;
    }

    private void save() {
        try {
            String ten = txtTen.getText().trim();
            if (ten.isEmpty()) throw new RuntimeException("Tên nhóm quyền không được để trống.");
            if (!Validator.isValidName(ten)) throw new RuntimeException("Tên nhóm quyền chỉ được chứa chữ và khoảng trắng.");
            bus.createNhomQuyen(txtTen.getText(), getSelected(), getSelectedActions());
            loadTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Tạo thành công!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void update() {
        int row = tbl.getSelectedRow();
        if (row < 0) return;
        int id = (int) model.getValueAt(row, 0);
        try {
            bus.updateNhomQuyen(id, txtTen.getText(), getSelected(), getSelectedActions());
            loadTable();
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void delete() {
        int row = tbl.getSelectedRow();
        if (row < 0) return;
        int id = (int) model.getValueAt(row, 0);
        try {
            bus.deleteNhomQuyen(id);
            loadTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void clearForm() {
        txtTen.setText("");
        for (JCheckBox cb : checkBoxes) cb.setSelected(false);
        for (JPanel p : mapPanelCon.values()) p.setVisible(false);
        pnlCheck.revalidate();
        pnlCheck.repaint();
    }

    public void applyPermissions(List<Integer> actionIds) {
        btnSave.setVisible(actionIds.contains(ActionConstants.THEM));
        btnUpdate.setVisible(actionIds.contains(ActionConstants.SUA));
        btnDelete.setVisible(actionIds.contains(ActionConstants.XOA));
        revalidate();
        repaint();
    }
}