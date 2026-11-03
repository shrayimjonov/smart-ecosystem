package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.domain.Bonus;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.bonus.BonusListDTO;
import uz.uzkassa.cts.dto.bonus.BonusListDetailByEmployeeDTO;
import uz.uzkassa.cts.dto.bonus.BonusStatisticsDTO;
import uz.uzkassa.cts.dto.bonus.BonusSummaryExcelExportDTO;
import uz.uzkassa.cts.enums.EventStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.BonusRepositoryCustom;
import uz.uzkassa.cts.utils.QueryUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class BonusRepositoryImpl extends BaseRepositoryImpl implements BonusRepositoryCustom {

    @Override
    public ResultList<BonusListDTO> getResultList(BaseFilter filter) {
        ResultList<BonusListDTO> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append("b.employee_id as employeeId, br.name as branch,\n");
        sql.append("concat(e.last_name, ' ', e.first_name, ' ', e.patronymic) as employee, \n");
        sql.append("sum(b.amount) as amount, sum(b.bonus_amount) as bonusAmount,\n");
        sql.append("c.name as company,\n");
        sql.append("count(b.employee_id) count \n");
        sql.append(getSQL(filter));
        sql.append("group by e.id, b.employee_id, br.id, c.id \n");
        sql.append("order by ").append(filter.getOrderBy().equals("id") ? "employeeId" : filter.getOrderBy()).append(" ").append(filter.getSortOrder());
        sql.append(QueryUtils.getOffsetLimit(filter));

        List<BonusListDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(),
                BeanPropertyRowMapper.newInstance(BonusListDTO.class));
        StringBuilder countSql = new StringBuilder();
        countSql.append("select count(t.count) from (select count(e.id) \n");
        countSql.append(getSQL(filter));
        countSql.append("group by e.id, b.employee_id, br.id \n");
        countSql.append(") t ");
        Query countQuery = entityManager.createNativeQuery(countSql.toString());

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    public ResultList<BonusListDetailByEmployeeDTO> getResultListByEmployee(BaseFilter filter) {
        ResultList<BonusListDetailByEmployeeDTO> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append("b.employee_id as employeeId, br.name as branch,\n");
        sql.append("concat(e.last_name, ' ', e.first_name, ' ', e.patronymic) as employee, \n");
        sql.append("sum(b.amount) as amount, sum(b.bonus_amount) as bonusAmount,\n");
        sql.append("count(b.employee_id) count, b.tariff_type as tariff \n");
        sql.append(getSQL(filter));
        sql.append("group by e.id, b.employee_id, br.id, b.tariff_type \n");
        sql.append("order by ").append(filter.getOrderBy().equals("id") ? "employeeId" : filter.getOrderBy()).append(" ").append(filter.getSortOrder());
        sql.append(QueryUtils.getOffsetLimit(filter));

        List<BonusListDetailByEmployeeDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(),
                BeanPropertyRowMapper.newInstance(BonusListDetailByEmployeeDTO.class));
        StringBuilder countSql = new StringBuilder();
        countSql.append("select count(t.count) from (select count(e.id) \n");
        countSql.append(getSQL(filter));
        countSql.append("group by e.id, b.employee_id, br.id, b.tariff_type \n");
        countSql.append(") t ");
        Query countQuery = entityManager.createNativeQuery(countSql.toString());

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    public Optional<BonusStatisticsDTO> getStatistic(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select sum(b.amount) as totalAmount,");
        sql.append("sum(b.bonus_amount) as totalBonusAmount \n");
        sql.append(getSQL(filter));
        return jdbcSpringManager
                .getSimpleJdbcTemplate()
                .query(sql.toString(), BeanPropertyRowMapper.newInstance(BonusStatisticsDTO.class))
                .stream()
                .findFirst();
    }

    @Override
    public List<BonusSummaryExcelExportDTO> getBonusSummary(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select r.name_uz_latin  as region, \n");
        sql.append("concat(e.last_name, ' ', e.first_name, ' ', e.patronymic) as employee,\n");
        sql.append("c.name as company, count(b.tariff_type) qtyServices,");
        sql.append("sum(coalesce(b.amount, 0)) as totalAmount, \n");
        sql.append("sum(coalesce(b.bonus_amount, 0)) as totalBonusAmount \n");
        sql.append("from bonus b \n");
        sql.append("left join jhi_user e on e.id = b.employee_id \n");
        sql.append("left join company c on b.company_id = c.id \n");
        sql.append("left join region r on e.region_id = r.id \n");
        sql.append("where b.deleted is false \n");
        if (filter.getEmployeeId() != null) {
            sql.append("and b.employee_id = ").append(filter.getEmployeeId()).append("\n");
        }
        if (filter.getBranchId() != null) {
            sql.append("and e.branch_id = ").append(filter.getBranchId()).append("\n");
        }
        if (filter.getFrom() != null && filter.getTo() != null) {
            sql.append("and b.created_date between '").append(filter.getFromStartOfDay()).append("' and '").append(filter.getToEndOfDay()).append("' \n");
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(e.first_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(e.last_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(e.patronymic)").append(filter.getLikeSearchKey());
            sql.append(" or lower(e.login)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        sql.append("group by e.id, c.id, r.id \n");
        sql.append("order by r.id \n");
        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(),
                BeanPropertyRowMapper.newInstance(BonusSummaryExcelExportDTO.class));
    }

    @Override
    public List<String> getNotExistBillingIds(String billingIds) {
        StringBuilder sql = new StringBuilder();
        sql.append("select tb.billingId \n");
        sql.append("from (values ");
        sql.append(billingIds);
        sql.append(") as tb(billingId) \n");
        sql.append("left join bonus b ON tb.billingId = b.billing_id \n");
        sql.append("where b.billing_id is null \n");

        Query query = entityManager.createNativeQuery(sql.toString(), String.class);
        return query.getResultList();
    }

    private String getSQL(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("from bonus b \n");
        sql.append("left join jhi_user e on b.employee_id = e.id \n");
        sql.append("left join branch br on e.branch_id = br.id \n");
        sql.append("left join company c on b.company_id = c.id \n");
        sql.append("where b.deleted is false \n");
        if (filter.getEmployeeId() != null) {
            sql.append("and b.employee_id = ").append(filter.getEmployeeId()).append("\n");
        }
        if (filter.getBranchId() != null) {
            sql.append("and e.branch_id = ").append(filter.getBranchId()).append("\n");
        }
        if (filter.getFrom() != null && filter.getTo() != null) {
            sql.append("and b.created_date between '").append(filter.getFromStartOfDay()).append("' and '").append(filter.getToEndOfDay()).append("' \n");
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(e.first_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(e.last_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(e.patronymic)").append(filter.getLikeSearchKey());
            sql.append(" or lower(e.login)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql.toString();
    }

    @Override
    public List<Bonus> findAll(BaseFilter filter) {
        List<Bonus> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select b from Bonus b");
        sql.append(" left join b.employee e");
        sql.append(" where b.deleted = false");

        if (filter.getEmployeeId() != null) {
            sql.append(" and b.employeeId = :employeeId");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and e.branchId = :branchId");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and b.tariffType = :tariffType");
        }
        if (filter.getFrom() != null) {
            sql.append(" and b.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and b.createdDate <= :toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(e.firstName)").append(filter.getLikeSearch());
            sql.append(" or lower(e.lastName)").append(filter.getLikeSearch());
            sql.append(" or lower(e.patronymic)").append(filter.getLikeSearch());
            sql.append(" or lower(e.login)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        TypedQuery<Bonus> query = entityManager.createQuery(sql.toString(), Bonus.class);

        if (filter.getEmployeeId() != null) {
            query.setParameter("employeeId", filter.getEmployeeId());
        }
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("tariffType", EventStatus.valueOf(filter.getStatus()));
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFromStartOfDay());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getToEndOfDay());
        }
        if (filter.isSearchNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchFor());
        }

        return query.getResultList();
    }
}
