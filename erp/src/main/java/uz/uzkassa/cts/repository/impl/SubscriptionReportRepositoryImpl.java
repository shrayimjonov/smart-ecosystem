package uz.uzkassa.cts.repository.impl;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.dto.report.ReportTotalDTO;
import uz.uzkassa.cts.dto.subscriptionreport.SubscriptionReportServiceDTO;
import uz.uzkassa.cts.filter.SubscriptionReportFilter;
import uz.uzkassa.cts.repository.custom.SubscriptionReportRepositoryCustom;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class SubscriptionReportRepositoryImpl extends BaseRepositoryImpl implements SubscriptionReportRepositoryCustom {

    @Override
    public Map<String, HashMap<Long, BigInteger>> getTotalsByEmployees(SubscriptionReportFilter filter) {
        Map<String, HashMap<Long, BigInteger>> resultMap = new HashMap<>();

        StringBuilder sql = new StringBuilder();
        sql.append("select u.id, count(sr.id) total \n");
        sql.append("from subscription_report sr \n");
        sql.append("inner join jhi_user u on u.id = sr.employee_id \n");
        sql.append("where u.deleted = false \n");
        if (filter.getFrom() != null && filter.getTo() != null) {
            sql.append(" and sr.subscription_date between '").append(filter.getFrom().atStartOfDay()).append("' and '").append(filter.getTo().atTime(LocalTime.MAX)).append("' \n");
        }
        if (filter.getServiceId() != null) {
            sql.append(" and sr.cts_service_id = ").append(filter.getServiceId()).append("\n");
        }
        if (filter.getEmployeeId() != null) {
            sql.append(" and sr.employee_id = ").append(filter.getEmployeeId()).append("\n");
        }
        sql.append(" group by u.id \n");
        List<ReportTotalDTO> resultList = jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(ReportTotalDTO.class));
        HashMap<Long, BigInteger> customerMap = resultList.stream().collect(Collectors.toMap(ReportTotalDTO::getId, ReportTotalDTO::getTotal, (a, b) -> b, HashMap::new));
        resultMap.put("customerMap", customerMap);

        return resultMap;
    }

    @Override
    public List<SubscriptionReportServiceDTO> getBonusesByEmployeesList(SubscriptionReportFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select u.id employeeId, sr.cts_service_id as serviceId, count(sr.id) qty, \n");
        sql.append(" t.price, ((count(sr.id) * t.price) / t.bonus_percentage) bonus \n");
        sql.append("from subscription_report sr \n");
        sql.append("inner join jhi_user u on u.id = sr.employee_id\n");
        sql.append("""
                inner join (select ssr.cts_service_id, ssr.price, ssr.bonus_percentage
                                     from subscription_report ssr
                                     group by ssr.cts_service_id, ssr.price, ssr.bonus_percentage) t on t.cts_service_id = sr.cts_service_id
                """);
        sql.append(" where u.deleted = false \n");
        if (filter.getFrom() != null && filter.getTo() != null) {
            sql.append(" and sr.subscription_date between '").append(filter.getFrom().atStartOfDay()).append("' and '").append(filter.getTo().atTime(LocalTime.MAX)).append("' \n");
        }
        if (filter.getServiceId() != null) {
            sql.append(" and sr.cts_service_id = ").append(filter.getServiceId()).append("\n");
        }
        if (filter.getEmployeeId() != null) {
            sql.append(" and sr.employee_id = ").append(filter.getEmployeeId()).append("\n");
        }
        sql.append("group by u.id, sr.cts_service_id, t.price, t.bonus_percentage \n");
        sql.append("order by u.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(SubscriptionReportServiceDTO.class));
    }
}
