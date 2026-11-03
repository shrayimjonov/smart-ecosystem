package uz.uzkassa.cts.repository.impl;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.dto.tariff.TariffTotalDTO;
import uz.uzkassa.cts.filter.EmployeeReportFilter;
import uz.uzkassa.cts.repository.custom.TariffRepositoryCustom;
import uz.uzkassa.cts.utils.DateUtil;

import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("unused")
public class TariffRepositoryImpl extends BaseRepositoryImpl implements TariffRepositoryCustom {

    @Override
    public List<TariffTotalDTO> listByEmployee(EmployeeReportFilter filter) {
        LocalDateTime fromDate = DateUtil.getStartDate(filter.getFromDate());
        LocalDateTime toDate = DateUtil.getEndDate(filter.getToDate());

        StringBuilder sql = new StringBuilder();
        sql.append("select t.id id, t.name as name, count(sp.id) total, t.price price, coalesce(t.bonus,0) * count(sp.id) as bonus \n");
        sql.append(" from public.cts_service_order_specification sp \n");
        sql.append(" inner join public.cts_service_order so on so.id = sp.order_id \n");
        sql.append(" inner join public.order_tariff ot on ot.service_order_id = so.id \n");
        sql.append(" inner join public.device_tariff dt on dt.order_tariff_id = ot.id \n");
        sql.append(" inner join public.tariff t on t.id = ot.tariff_id \n");
        sql.append(" where sp.deleted = false ");
        sql.append(" and t.deleted = false ");

        if (filter.getEmployeeId() != null) {
            sql.append(" and ot.creator_id=").append(filter.getEmployeeId());
        }
        if (toDate != null && fromDate != null) {
            sql.append(" and (sp.created_date between '").append(fromDate).append("' and '").append(toDate).append("') \n");
        }

        sql.append(" group by t.id ");

        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(TariffTotalDTO.class));
    }
}
