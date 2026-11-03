package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.domain.OneTimeOrder;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.serviceorder.ServiceOrderListDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.filter.EmployeeReportFilter;
import uz.uzkassa.cts.filter.OneTimeOrderFilter;
import uz.uzkassa.cts.repository.custom.OneTimeOrderRepositoryCustom;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.utils.DateUtil;
import uz.uzkassa.cts.utils.QueryUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("unused")
public class OneTimeOrderRepositoryImpl extends BaseRepositoryImpl implements OneTimeOrderRepositoryCustom {

    @Override
    public ResultList<ServiceOrderListDTO> getListByEmployee(EmployeeReportFilter filter) {
        ResultList<ServiceOrderListDTO> resultList = new ResultList<>();
        String sql = "select oto.*, \n" +
                "s.name AS name, s.bonus AS bonus, s.price AS price,\n" +
                "c.id AS cusId, c.name AS cusName, c.tin AS cusTin \n" +
                getBaseSql(filter) +
                " order by oto." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<ServiceOrderListDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(ServiceOrderListDTO.class));

        String countSql = "select count(oto.id) \n" +
                getBaseSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    public BigDecimal totalBonusListByEmployee(EmployeeReportFilter filter) {
        String sql = "select sum(coalesce(s.bonus,0)) as totalBonus \n" +
                getBaseSql(filter);
        Query query = entityManager.createNativeQuery(sql);

        return ((BigDecimal) (query.getSingleResult()));
    }

    @Override
    public ResultList<OneTimeOrder> getResultList(OneTimeOrderFilter filter) {
        ResultList<OneTimeOrder> resultList = new ResultList<>();
        boolean isNotAdmin = !SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode());
        StringBuilder sql = new StringBuilder();
        sql.append("select oto from OneTimeOrder oto ");
        sql.append(" join oto.customer c ");
        if (isNotAdmin) {
            sql.append(" join c.branches cb ");
        }
        sql.append(" join c.address a ");
        sql.append(" where oto.deleted = false ");

        if (isNotAdmin) {
            sql.append(" and cb.id =:branchId ");
        }

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and oto.status =:status ");
        }

        if (filter.getFrom() != null) {
            sql.append(" and oto.createdDate >=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and oto.createdDate <=:toDate");
        }

        if (filter.getCreatorId() != null) {
            sql.append(" and oto.creatorId = :creatorId");
        }

        if (filter.getRegionId() != null) {
            sql.append(" and a.regionId = :regionId");
        }

        if (filter.getDistrictId() != null) {
            sql.append(" and a.districtId = :districtId");
        }

        if (CollectionUtils.isNotEmpty(filter.getServiceIds())) {
            sql.append(" and oto.serviceId in (:serviceIds)");
        }

        if (filter.getModifiedFrom() != null && filter.getModifiedTo() != null) {
            sql.append(" and (oto.lastModifiedDate >=:modifiedDateFrom and oto.lastModifiedDate <=:modifiedDateTo) ");
        }

        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(c.name) ").append(filter.getLikeSearch());
            sql.append(" or lower(c.companyName) ").append(filter.getLikeSearch());
            sql.append(" or lower(c.tin) ").append(filter.getLikeSearch());
            sql.append(" or lower(oto.serialNumberKKM) ").append(filter.getLikeSearch());
            if (StringUtils.isNumeric(filter.getSearch())) {
                sql.append(" or oto.id = ").append(filter.getSearch());
            }
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select oto", " select count(oto.id)");
        sql.append(" order by oto.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<OneTimeOrder> query = entityManager
                .createQuery(sql.toString(), OneTimeOrder.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (isNotAdmin) {
            query.setParameter("branchId", SecurityUtils.getBranchId());
            countQuery.setParameter("branchId", SecurityUtils.getBranchId());
        }

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CommonStatus.get(filter.getStatus()));
            countQuery.setParameter("status", CommonStatus.get(filter.getStatus()));
        }
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
        if (filter.getCreatorId() != null) {
            query.setParameter("creatorId", filter.getCreatorId() );
            countQuery.setParameter("creatorId", filter.getCreatorId() );
        }

        if (filter.getRegionId() != null) {
            query.setParameter("regionId", filter.getRegionId() );
            countQuery.setParameter("regionId", filter.getRegionId() );
        }

        if (filter.getDistrictId() != null) {
            query.setParameter("districtId", filter.getDistrictId() );
            countQuery.setParameter("districtId", filter.getDistrictId() );
        }

        if (CollectionUtils.isNotEmpty(filter.getServiceIds())) {
            sql.append(" and oto.serviceId in (:serviceIds)");
            query.setParameter("serviceIds", filter.getServiceIds() );
            countQuery.setParameter("serviceIds", filter.getServiceIds() );
        }

        if (filter.getModifiedFrom() != null && filter.getModifiedTo() != null) {
            query.setParameter("modifiedDateFrom", filter.getModifiedFrom());
            countQuery.setParameter("modifiedDateFrom", filter.getModifiedFrom());
            query.setParameter("modifiedDateTo", filter.getModifiedTo());
            countQuery.setParameter("modifiedDateTo", filter.getModifiedTo());
        }
        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getBaseSql(EmployeeReportFilter filter) {
        LocalDateTime fromDate = DateUtil.getStartDate(filter.getFromDate());
        LocalDateTime toDate = DateUtil.getEndDate(filter.getToDate());

        StringBuilder sql = new StringBuilder();
        sql.append(" from one_time_order oto \n");
        sql.append(" inner join cts_service s on s.id = oto.service_id \n");
        sql.append(" inner join customer c on c.id = oto.customer_id \n");
        sql.append(" where oto.deleted = false \n");
        sql.append(" and s.deleted = false \n");

        if (filter.getEmployeeId() != null) {
            sql.append(" and oto.creator_id=").append(filter.getEmployeeId());
        }
        if (toDate != null && fromDate != null) {
            sql.append(" and (oto.created_date between '").append(fromDate).append("' and '").append(toDate).append("') \n");
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(s.name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.name)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }
}
