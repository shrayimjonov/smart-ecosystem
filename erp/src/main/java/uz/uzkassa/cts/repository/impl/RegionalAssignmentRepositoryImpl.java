package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.regionalassignment.RegionalAssignment;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.RegionalAssignmentFilter;
import uz.uzkassa.cts.repository.custom.RegionalAssignmentRepositoryCustom;

public class RegionalAssignmentRepositoryImpl extends BaseRepositoryImpl implements RegionalAssignmentRepositoryCustom {

    @Override
    public ResultList<RegionalAssignment> getResultList(RegionalAssignmentFilter filter) {
        ResultList<RegionalAssignment> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct ra.* from regional_assignment ra ");
        if (org.springframework.util.StringUtils.hasText(filter.getSearch())) {
            sql.append(" join jhi_user e on ra.employee_id = e.id ");
        }
        if (!CollectionUtils.isEmpty(filter.getDistrictIds())) {
            sql.append(" join regional_assignment_district rad on rad.regional_assignment_id = ra.id ");
        }
        sql.append(" where ra.deleted = false ");
        if (filter.getEmployeeId() != null) {
            sql.append(" and ra.employee_id =:employeeId ");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and ra.status =:status ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and ra.created_date >=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and ra.created_date <=:toDate");
        }
        if (filter.getRegionId() != null) {
            sql.append(" and ra.region_id =:regionId ");
        }
        if (!CollectionUtils.isEmpty(filter.getDistrictIds())) {
            sql.append(" and rad.district_id in (:districtIds) ");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(e.first_name) ").append(filter.getLikeSearch());
            sql.append(" or lower(e.last_name) ").append(filter.getLikeSearch());
            sql.append(" or lower(e.patronymic) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select distinct ra", " select count(distinct ra.id)");
        countSql = countSql.replace(".*", "");
        sql.append(" order by ra.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        Query query = entityManager
                .createNativeQuery(sql.toString(), RegionalAssignment.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        Query countQuery = entityManager.createNativeQuery(countSql, Long.class);

        if (filter.getEmployeeId() != null) {
            query.setParameter("employeeId", filter.getEmployeeId());
            countQuery.setParameter("employeeId", filter.getEmployeeId());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", filter.getStatus());
            countQuery.setParameter("status", filter.getStatus());
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
        if (filter.getRegionId() != null) {
            query.setParameter("regionId", filter.getRegionId());
            countQuery.setParameter("regionId", filter.getRegionId());
        }
        if (!CollectionUtils.isEmpty(filter.getDistrictIds())) {
            query.setParameter("districtIds", filter.getDistrictIds());
            countQuery.setParameter("districtIds", filter.getDistrictIds());
        }
        Long count = (Long) countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }
}
