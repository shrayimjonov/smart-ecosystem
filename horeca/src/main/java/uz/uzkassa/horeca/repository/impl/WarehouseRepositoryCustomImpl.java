package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.domain.warehouse.Warehouse;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.repository.WarehouseRepositoryCustom;

public class WarehouseRepositoryCustomImpl extends BaseRepositoryImpl implements WarehouseRepositoryCustom {

    @Override
    public ResultList<Warehouse> getResultList(BaseFilter filter) {
        ResultList<Warehouse> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select w from Warehouse w");
        sql.append(" where w.deleted = false");
        sql.append(" and w.branchId=:branchId");
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and w.status=:status");
        }
        if (filter.getDepartmentId() != null) {
            sql.append(" and w.departmentId=:departmentId");
        }
        if (filter.hasSearch()) {
            sql.append(" and (");
            sql.append(" lower(w.name)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        String countSql = sql.toString().replace("select w ", "select count(w.id)");
        sql.append(" order by w.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Warehouse> query = entityManager
                .createQuery(sql.toString(), Warehouse.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("branchId", filter.getBranchId());
        countQuery.setParameter("branchId", filter.getBranchId());

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }
        if (filter.getDepartmentId() != null) {
            query.setParameter("departmentId", filter.getDepartmentId());
            countQuery.setParameter("departmentId", filter.getDepartmentId());
        }
        if (filter.hasSearch()) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }
}
