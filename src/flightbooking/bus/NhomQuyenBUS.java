package flightbooking.bus;

import flightbooking.dao.NhomQuyenDAO;
import java.util.List;
import java.util.Map;

public class NhomQuyenBUS {

    private final NhomQuyenDAO dao = new NhomQuyenDAO();

    public boolean hasPermission(int nhomId, int quyenId) {
        return dao.hasPermission(nhomId, quyenId);
    }

    public void createNhomQuyen(String ten, List<Integer> quyenIds, Map<Integer, List<Integer>> actionMap) {
        if (ten == null || ten.trim().isEmpty()) {
            throw new RuntimeException("Tên nhóm không được rỗng");
        }

        int id = dao.insertNhomQuyen(ten);

        if (id <= 0) {
            throw new RuntimeException("Tạo nhóm thất bại");
        }

        dao.insertQuyen(id, quyenIds);

        for (Map.Entry<Integer, List<Integer>> entry : actionMap.entrySet()) {
            dao.insertActions(id, entry.getKey(), entry.getValue());
        }
    }

    public void updateNhomQuyen(int id, String ten, List<Integer> quyenIds, Map<Integer, List<Integer>> actionMap) {
        if (ten == null || ten.trim().isEmpty()) {
            throw new RuntimeException("Tên không hợp lệ");
        }

        dao.updateNhomQuyen(id, ten);

        dao.deleteAllActions(id);
        dao.deleteAllQuyen(id);

        dao.insertQuyen(id, quyenIds);

        for (Map.Entry<Integer, List<Integer>> entry : actionMap.entrySet()) {
            dao.insertActions(id, entry.getKey(), entry.getValue());
        }
    }

    public void deleteNhomQuyen(int id) {
        dao.deleteAllActions(id);
        dao.deleteAllQuyen(id);
        dao.deleteNhomQuyen(id);
    }
}