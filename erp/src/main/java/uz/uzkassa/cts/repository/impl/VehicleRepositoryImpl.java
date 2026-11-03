package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.Vehicle;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.VehicleRepositoryCustom;

@Component
public class VehicleRepositoryImpl extends BaseRepositoryImpl implements VehicleRepositoryCustom {

    @Override
    public ResultList<Vehicle> getResultList(BaseFilter filter) {
        ResultList<Vehicle> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select v from Vehicle v ");
        sql.append(" where v.deleted = false ");

        if (filter.getEmployeeId() != null) {
            sql.append(" and v.employeeId =:employeeId ");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and v.status =:status ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and v.createdDate >=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and v.createdDate <=:toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(v.numberPlate) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select v", " select count(v.id)");
        sql.append(" order by v.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Vehicle> query = entityManager
                .createQuery(sql.toString(), Vehicle.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getEmployeeId() != null) {
            query.setParameter("employeeId", filter.getEmployeeId());
            countQuery.setParameter("employeeId", filter.getEmployeeId());
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
        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }
}
