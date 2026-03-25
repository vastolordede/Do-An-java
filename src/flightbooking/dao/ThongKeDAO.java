package flightbooking.dao;

import flightbooking.dto.ThongKeChuyenBayDTO;
import flightbooking.dto.ThongKeHangGheDTO;
import flightbooking.dto.ThongKeTongQuanDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ThongKeDAO extends BaseDAO {

    public ThongKeTongQuanDTO getTongQuan(LocalDate fromDate, LocalDate toDate, Integer tuyenBayId, Integer chuyenBayId) {
    StringBuilder sql = new StringBuilder();
    sql.append("select ");
    sql.append("    coalesce(sum(case when v.trangthai = 1 then coalesce(v.giachot, 0) + coalesce(v.thuechot, 0) else 0 end), 0) as tong_doanh_thu, ");
    sql.append("    coalesce(sum(case when v.trangthai = 1 then 1 else 0 end), 0) as tong_ve_hieu_luc, ");
    sql.append("    coalesce(sum(case when v.trangthai = 0 then 1 else 0 end), 0) as tong_ve_huy, ");
    sql.append("    coalesce(avg(case when v.trangthai = 1 then coalesce(v.giachot, 0) + coalesce(v.thuechot, 0) end), 0) as doanh_thu_tb_ve ");
    sql.append("from ve v ");
    sql.append("join chuyenbay cb on cb.chuyenbay_id = v.chuyenbay_id ");
    sql.append("join tuyenbay tb on tb.tuyenbay_id = cb.tuyenbay_id ");
    sql.append("where 1=1 ");

    List<Object> params = new ArrayList<>();

    if (fromDate != null) {
        sql.append(" and cb.giokhoihanh >= ? ");
        params.add(Timestamp.valueOf(fromDate.atStartOfDay()));
    }

    if (toDate != null) {
        sql.append(" and cb.giokhoihanh < ? ");
        params.add(Timestamp.valueOf(toDate.plusDays(1).atStartOfDay()));
    }

    if (tuyenBayId != null) {
        sql.append(" and tb.tuyenbay_id = ? ");
        params.add(tuyenBayId);
    }

    if (chuyenBayId != null) {
        sql.append(" and cb.chuyenbay_id = ? ");
        params.add(chuyenBayId);
    }

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql.toString())) {

        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                ThongKeTongQuanDTO dto = new ThongKeTongQuanDTO();
                dto.setTongDoanhThu(rs.getBigDecimal("tong_doanh_thu"));
                dto.setTongVeHieuLuc(rs.getInt("tong_ve_hieu_luc"));
                dto.setTongVeHuy(rs.getInt("tong_ve_huy"));
                dto.setDoanhThuTrungBinhVe(rs.getBigDecimal("doanh_thu_tb_ve"));
                return dto;
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("ThongKeDAO.getTongQuan failed", e);
    }

    return new ThongKeTongQuanDTO();
}

    public List<ThongKeChuyenBayDTO> getDoanhThuTheoChuyenBay(LocalDate fromDate, LocalDate toDate, Integer tuyenBayId) {
    StringBuilder sql = new StringBuilder();
    sql.append("select ");
    sql.append("    cb.chuyenbay_id, ");
    sql.append("    sbd.tensanbay as san_bay_di, ");
    sql.append("    sbdn.tensanbay as san_bay_den, ");
    sql.append("    cb.giokhoihanh, ");
    sql.append("    sum(case when v.trangthai = 1 then 1 else 0 end) as ve_hieu_luc, ");
    sql.append("    sum(case when v.trangthai = 0 then 1 else 0 end) as ve_huy, ");
    sql.append("    coalesce(sum(case when v.trangthai = 1 then coalesce(v.giachot, 0) + coalesce(v.thuechot, 0) else 0 end), 0) as doanh_thu ");
    sql.append("from ve v ");
    sql.append("join chuyenbay cb on cb.chuyenbay_id = v.chuyenbay_id ");
    sql.append("join tuyenbay tb on tb.tuyenbay_id = cb.tuyenbay_id ");
    sql.append("join sanbay sbd on sbd.sanbay_id = tb.sanbaydi_id ");
    sql.append("join sanbay sbdn on sbdn.sanbay_id = tb.sanbayden_id ");
    sql.append("where 1=1 ");

    List<Object> params = new ArrayList<>();

    if (fromDate != null) {
        sql.append(" and cb.giokhoihanh >= ? ");
        params.add(Timestamp.valueOf(fromDate.atStartOfDay()));
    }

    if (toDate != null) {
        sql.append(" and cb.giokhoihanh < ? ");
        params.add(Timestamp.valueOf(toDate.plusDays(1).atStartOfDay()));
    }

    if (tuyenBayId != null) {
        sql.append(" and tb.tuyenbay_id = ? ");
        params.add(tuyenBayId);
    }

    sql.append(" group by cb.chuyenbay_id, sbd.tensanbay, sbdn.tensanbay, cb.giokhoihanh ");
    sql.append(" order by doanh_thu desc, cb.giokhoihanh desc ");

    List<ThongKeChuyenBayDTO> list = new ArrayList<>();

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql.toString())) {

        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ThongKeChuyenBayDTO dto = new ThongKeChuyenBayDTO();
                dto.setChuyenBayId(rs.getInt("chuyenbay_id"));
                dto.setSanBayDi(rs.getString("san_bay_di"));
                dto.setSanBayDen(rs.getString("san_bay_den"));

                Timestamp ts = rs.getTimestamp("giokhoihanh");
                if (ts != null) {
                    dto.setGioKhoiHanh(ts.toLocalDateTime());
                }

                dto.setVeHieuLuc(rs.getInt("ve_hieu_luc"));
                dto.setVeHuy(rs.getInt("ve_huy"));
                dto.setDoanhThu(rs.getBigDecimal("doanh_thu"));
                list.add(dto);
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("ThongKeDAO.getDoanhThuTheoChuyenBay failed", e);
    }

    return list;
}

    public List<ThongKeHangGheDTO> getDoanhThuTheoHangGhe(LocalDate fromDate, LocalDate toDate, Integer tuyenBayId, Integer chuyenBayId) {
    StringBuilder sql = new StringBuilder();
    sql.append("select ");
    sql.append("    hgm.tenhangghe, ");
    sql.append("    sum(case when v.trangthai = 1 then 1 else 0 end) as so_ve, ");
    sql.append("    coalesce(sum(case when v.trangthai = 1 then coalesce(v.giachot, 0) + coalesce(v.thuechot, 0) else 0 end), 0) as doanh_thu ");
    sql.append("from ve v ");
    sql.append("join chuyenbay cb on cb.chuyenbay_id = v.chuyenbay_id ");
    sql.append("join tuyenbay tb on tb.tuyenbay_id = cb.tuyenbay_id ");
    sql.append("join ghe g on g.ghe_id = v.ghe_id ");
    sql.append("join hangghemaybay hgm on hgm.maybay_id =g.maybay_id and hgm.hangghe_id = g.hangghe_id ");
    sql.append("where 1=1 ");

    List<Object> params = new ArrayList<>();

    if (fromDate != null) {
        sql.append(" and cb.giokhoihanh >= ? ");
        params.add(Timestamp.valueOf(fromDate.atStartOfDay()));
    }

    if (toDate != null) {
        sql.append(" and cb.giokhoihanh < ? ");
        params.add(Timestamp.valueOf(toDate.plusDays(1).atStartOfDay()));
    }

    if (tuyenBayId != null) {
        sql.append(" and tb.tuyenbay_id = ? ");
        params.add(tuyenBayId);
    }

    if (chuyenBayId != null) {
        sql.append(" and cb.chuyenbay_id = ? ");
        params.add(chuyenBayId);
    }

    sql.append(" group by hgm.tenhangghe, hgm.thutuhienthi ");
    sql.append(" order by hgm.thutuhienthi asc, doanh_thu desc ");

    List<ThongKeHangGheDTO> list = new ArrayList<>();

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql.toString())) {

        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ThongKeHangGheDTO dto = new ThongKeHangGheDTO();
                dto.setTenHangGhe(rs.getString("tenhangghe"));
                dto.setSoVe(rs.getInt("so_ve"));
                dto.setDoanhThu(rs.getBigDecimal("doanh_thu"));
                list.add(dto);
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("ThongKeDAO.getDoanhThuTheoHangGhe failed", e);
    }

    return list;
}
}