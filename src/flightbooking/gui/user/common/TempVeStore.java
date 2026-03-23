package flightbooking.gui.user.common;

import flightbooking.bus.DatVeBUS;
import java.util.ArrayList;
import java.util.List;

public class TempVeStore {

    public static List<DatVeBUS.ThongTinHanhKhachVaGhe> DS = new ArrayList<>();
    public static int CURRENT_INDEX = -1;

    public static void clear() {
        DS.clear();
        CURRENT_INDEX = -1;
    }

    public static List<DatVeBUS.ThongTinHanhKhachVaGhe> getAll() {
        return DS;
    }

    public static void setCurrentIndex(int i) {
        CURRENT_INDEX = i;
    }

    public static DatVeBUS.ThongTinHanhKhachVaGhe getCurrent() {
        if (CURRENT_INDEX >= 0 && CURRENT_INDEX < DS.size()) {
            return DS.get(CURRENT_INDEX);
        }
        return null;
    }

    public static void upsertCurrent(DatVeBUS.ThongTinHanhKhachVaGhe item) {
        if (CURRENT_INDEX >= 0 && CURRENT_INDEX < DS.size()) {
            DS.set(CURRENT_INDEX, item); // update
        } else {
            DS.add(item); // new
            CURRENT_INDEX = DS.size() - 1;
        }
    }
}