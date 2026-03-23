package flightbooking.bus;

import flightbooking.dao.SuatAnDAO;
import flightbooking.dto.SuatAnDTO;
import java.util.List;

public class SuatAnBUS {

    private final SuatAnDAO dao = new SuatAnDAO();

    public List<SuatAnDTO> dsSuatAn() {
        return dao.findAll();
    }
}
