package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.domain.CustomerBranchApplication;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.promomanagardashboard.CountByDateAndRegionDTO;
import uz.uzkassa.cts.dto.promomanagardashboard.TopRegionsByCountDTO;
import uz.uzkassa.cts.enums.CustomerBranchApplicationStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.CustomerBranchApplicationRepositoryCustom;
import uz.uzkassa.cts.security.SecurityUtils;

import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("unused")
public class CustomerBranchApplicationRepositoryImpl extends BaseRepositoryImpl implements CustomerBranchApplicationRepositoryCustom {

    @Override
    public ResultList<CustomerBranchApplication> getResultList(BaseFilter filter) {
        ResultList<CustomerBranchApplication> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select cpa from CustomerBranchApplication cpa ");
        sql.append(" join cpa.customerBranch cb ");
        sql.append(" join cpa.customerBranch.customer c ");
        sql.append(" where cpa.deleted = false ");
        if (filter.getManagerId() != null) {
            sql.append(" and cpa.managerId =:managerId ");
        }
        if (filter.getCustomerBranchId() != null) {
            sql.append(" and cpa.customerBranchId =:customerBranchId ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and cpa.createdDate>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and cpa.createdDate<=:toDate");
        }
        if (filter.getRegionId() != null) {
            sql.append(" and cb.address.regionId =:regionId ");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and cpa.status =:status ");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(c.tin)").append(filter.getLikeSearch());
            sql.append(" or lower(c.companyName) ").append(filter.getLikeSearch());
            sql.append(" or lower(cb.name)").append(filter.getLikeSearch());
            sql.append(")");
        }

        String countSql = sql.toString().replaceFirst("select cpa", "select count(cpa.id)");
        sql.append(" order by cpa.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<CustomerBranchApplication> query = entityManager
                .createQuery(sql.toString(), CustomerBranchApplication.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFromStartOfDay());
            countQuery.setParameter("fromDate", filter.getFromStartOfDay());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getToEndOfDay());
            countQuery.setParameter("toDate", filter.getToEndOfDay());
        }
        if (filter.isSearchNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchFor());
            countQuery.setParameter("searchKey", filter.getSearchFor());
        }
        if (filter.getManagerId() != null) {
            query.setParameter("managerId", filter.getManagerId());
            countQuery.setParameter("managerId", filter.getManagerId());
        }
        if (filter.getCustomerBranchId() != null) {
            query.setParameter("customerBranchId", filter.getCustomerBranchId());
            countQuery.setParameter("customerBranchId", filter.getCustomerBranchId());
        }
        if (filter.getRegionId() != null) {
            query.setParameter("regionId", filter.getRegionId());
            countQuery.setParameter("regionId", filter.getRegionId());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CustomerBranchApplicationStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CustomerBranchApplicationStatus.valueOf(filter.getStatus()));
        }

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }

        return resultList;
    }

    @Override
    public List<CountByDateAndRegionDTO> getCustomerApplicationCountByDate(LocalDateTime from, LocalDateTime to) {
        StringBuilder sql = new StringBuilder();
        String name = getLocalizedName();
        sql.append("select ");
        sql.append(" DATE(cba.created_date) as date , ");
        sql.append(" r.").append(name).append(" as region, ");
        sql.append(" count(cba.id) as count ");
        sql.append(" from customer_branch_application cba ");
        sql.append(" inner join customer_branch cb on cb.id = cba.customer_branch_id ");
        sql.append(" inner join region r on r.id = cb.region_id ");
        sql.append(" where cba.created_date >= '").append(from).append("' ");
        sql.append(" and cba.created_date <= '").append(to).append("' ");
        sql.append(" group by DATE(cba.created_date), r.id ");
        sql.append(" order by date, r.").append(name);

        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(CountByDateAndRegionDTO.class));
    }

    @Override
    public List<TopRegionsByCountDTO> getTopRegionsByApplications(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        String name = getLocalizedName();
        sql.append("select ");
        sql.append(" r.").append(name).append(" as region, ");
        sql.append(" coalesce(count(cba.id), 0) as count ");
        sql.append(" from customer_branch_application cba ");
        sql.append(" inner join customer_branch cb on cb.id = cba.customer_branch_id ");
        sql.append(" inner join region r on r.id = cb.region_id ");
        sql.append(" where 1 = 1 ");
        if (filter.getFrom() != null) {
            sql.append(" and f.created_date >= '").append(filter.getFromStartOfDay()).append("' ");
        }
        if (filter.getTo() != null) {
            sql.append(" and f.created_date <= '").append(filter.getToEndOfDay()).append("' ");
        }
        sql.append(" group by r.id, r.").append(name);
        sql.append(" order by r.").append(name);
        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(TopRegionsByCountDTO.class));
    }

    private String getLocalizedName() {
        String name;
        switch (SecurityUtils.getCurrentRequestLanguageEnum()) {
            case uz_latin -> name = "name_uz_latin";
            case uz_cyrillic -> name = "name_uz_cyrillic";
            default -> name = "name_ru";
        }
        return name;
    }
}
