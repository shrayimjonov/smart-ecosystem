package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.domain.customer.Feedback;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.promomanagardashboard.*;
import uz.uzkassa.cts.enums.FeedBackStatus;
import uz.uzkassa.cts.enums.ReferenceType;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.FeedbackFilter;
import uz.uzkassa.cts.repository.custom.FeedbackRepositoryCustom;
import uz.uzkassa.cts.security.SecurityUtils;

import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("unused")
public class FeedbackRepositoryImpl extends BaseRepositoryImpl implements FeedbackRepositoryCustom {

    @Override
    public ResultList<Feedback> getResultList(FeedbackFilter filter) {
        ResultList<Feedback> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select f from Feedback f ");
        sql.append(" where f.deleted = false ");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and f.status =:status ");
        }
        if (filter.getCustomerId() != null) {
            sql.append(" and f.customerId =:customerId ");
        }
        if (filter.getCustomerBranchId() != null) {
            sql.append(" and f.customerBranchId =:customerBranchId ");
        }
        if (filter.getTypeId() != null) {
            sql.append(" and f.typeId =:typeId ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and f.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and f.createdDate <= :toDate");
        }
        if (filter.getCustomerBranchStatus() != null) {
            sql.append(" and f.customerBranch.status = :customerBranchStatus");
        }
        if (filter.getRegionId() != null) {
            sql.append(" and f.customerBranch.address.regionId = :regionId");
        }
        if (filter.getRate() != null) {
            sql.append(" and f.rate = :rate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(f.description) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select f", " select count(f.id)");
        sql.append(" order by f.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Feedback> query = entityManager
                .createQuery(sql.toString(), Feedback.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", FeedBackStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", FeedBackStatus.valueOf(filter.getStatus()));
        }
        if (filter.getCustomerId() != null) {
            query.setParameter("customerId", filter.getCustomerId());
            countQuery.setParameter("customerId", filter.getCustomerId());
        }
        if (filter.getCustomerBranchId() != null) {
            query.setParameter("customerBranchId", filter.getCustomerBranchId());
            countQuery.setParameter("customerBranchId", filter.getCustomerBranchId());
        }
        if (filter.getTypeId() != null) {
            query.setParameter("typeId", filter.getTypeId());
            countQuery.setParameter("typeId", filter.getTypeId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFromStartOfDay());
            countQuery.setParameter("fromDate", filter.getFromStartOfDay());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getToEndOfDay());
            countQuery.setParameter("toDate", filter.getToEndOfDay());
        }
        if (filter.getCustomerBranchStatus() != null) {
            query.setParameter("customerBranchStatus", filter.getCustomerBranchStatus());
            countQuery.setParameter("customerBranchStatus", filter.getCustomerBranchStatus());
        }
        if (filter.getRegionId() != null) {
            query.setParameter("regionId", filter.getRegionId());
            countQuery.setParameter("regionId", filter.getRegionId());
        }
        if (filter.getRate() != null) {
            query.setParameter("rate", filter.getRate());
            countQuery.setParameter("rate", filter.getRate());
        }
        if (filter.isSearchNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchFor());
            countQuery.setParameter("searchKey", filter.getSearchFor());
        }
        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    public List<FeedbackCountByRegionTypeDTO> getFeedbackCountByRegionAndType(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        String name = getLocalizedName();
        sql.append("select ");
        sql.append(" r.").append(name).append(" as region, ");
        sql.append(" t.").append(name).append(" as type, ");
        sql.append(" count(f.id) count ");
        sql.append(getBaseQuery());
        sql.append(" inner join reference t on t.id = f.type_id and t.type='").append(ReferenceType.FEEDBACK_TYPE.name()).append("'");
        sql.append(getFilter(filter));
        sql.append(" group by r.id, t.id ");
        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(FeedbackCountByRegionTypeDTO.class));
    }

    @Override
    public List<FeedbackCountByRegionAndStatusDTO> getFeedbackCountByRegionAndStatus(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        String name = getLocalizedName();
        sql.append("select ");
        sql.append(" r.").append(name).append(" as region, ");
        sql.append(" f.status as status, ");
        sql.append(" count(f.id) count ");
        sql.append(getBaseQuery());
        sql.append(getFilter(filter));
        sql.append(" group by r.id, f.status ");
        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(FeedbackCountByRegionAndStatusDTO.class));
    }

    @Override
    public List<CountByDateAndRegionDTO> getFeedbackCountByDate(LocalDateTime from, LocalDateTime to) {
        StringBuilder sql = new StringBuilder();
        String name = getLocalizedName();
        sql.append("select ");
        sql.append(" DATE(f.created_date) as date , ");
        sql.append(" r.").append(name).append(" as region, ");
        sql.append(" count(f.id) as count ");
        sql.append(getBaseQuery());
        sql.append(" where f.created_date >= '").append(from).append("' ");
        sql.append(" and f.created_date <= '").append(to).append("' ");
        sql.append(" group by DATE(f.created_date), r.id ");
        sql.append(" order by date, r.").append(name);

        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(CountByDateAndRegionDTO.class));
    }

    @Override
    public List<RegionRateDTO> getRegionAverageRateByFeedback(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        String name = getLocalizedName();
        sql.append("select ");
        sql.append(" r.").append(name).append(" as region, ");
        sql.append(" coalesce(round(avg(f.rate), 1), 0) as rate ");
        sql.append(getBaseQuery());
        sql.append(getFilter(filter));
        sql.append(" group by r.id, r.").append(name);
        sql.append(" order by r.").append(name);
        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(RegionRateDTO.class));
    }

    @Override
    public List<TopRegionsByCountDTO> getTopRegionsByFeedback(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        String name = getLocalizedName();
        sql.append(" select ");
        sql.append(" r.").append(name).append(" as region, ");
        sql.append(" coalesce(count(f.id), 0) as count ");
        sql.append(getBaseQuery());
        sql.append(getFilter(filter));
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

    private StringBuilder getBaseQuery() {
        StringBuilder sql = new StringBuilder();
        sql.append(" from feedback f");
        sql.append(" inner join customer_branch cb on cb.id = f.customer_branch_id ");
        sql.append(" inner join region r on r.id = cb.region_id ");
        return sql;
    }

    private StringBuilder getFilter(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" where 1 = 1 ");
        if (filter.getFrom() != null) {
            sql.append(" and f.created_date >= '").append(filter.getFromStartOfDay()).append("' ");
        }
        if (filter.getTo() != null) {
            sql.append(" and f.created_date <= '").append(filter.getToEndOfDay()).append("' ");
        }
        return sql;
    }
}
