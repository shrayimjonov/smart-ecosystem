package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.horeca.domain.warehouse.WarehouseItem;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.repository.WarehouseItemRepositoryCustom;

import java.math.BigDecimal;

public class WarehouseItemRepositoryImpl extends BaseRepositoryImpl implements WarehouseItemRepositoryCustom {

    public ResultList<WarehouseItem> getReport(BaseFilter filter) {
        ResultList<WarehouseItem> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select wi ");
        sql.append(getBaseSql(filter));
        String countSql = sql.toString().replace("select wi ", "select count(wi.id)");

        sql.append(" order by wi.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<WarehouseItem> query = entityManager
                .createQuery(sql.toString(), WarehouseItem.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("branchId", filter.getBranchId());
        countQuery.setParameter("branchId", filter.getBranchId());

        if (filter.getDepartmentId() != null) {
            query.setParameter("departmentId", filter.getDepartmentId());
            countQuery.setParameter("departmentId", filter.getDepartmentId());
        }
        if (filter.getWarehouseId() != null) {
            query.setParameter("warehouseId", filter.getWarehouseId());
            countQuery.setParameter("warehouseId", filter.getWarehouseId());
        }
        if (filter.getCategoryId() != null) {
            query.setParameter("categoryId", filter.getCategoryId());
            countQuery.setParameter("categoryId", filter.getCategoryId());
        }
        if (filter.hasSearch()) {
            query.setParameter("searchKey", filter.getLikeSearchKey());
            countQuery.setParameter("searchKey", filter.getLikeSearchKey());
        }
        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setCount(count);
            resultList.setList(query.getResultList());
        }
        return resultList;
    }

    @Override
    public BigDecimal getReportTotalQty(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select sum(wi.qty) ");
        sql.append(getBaseSql(filter));

        TypedQuery<BigDecimal> query = entityManager
                .createQuery(sql.toString(), BigDecimal.class);

        query.setParameter("branchId", filter.getBranchId());

        if (filter.getDepartmentId() != null) {
            query.setParameter("departmentId", filter.getDepartmentId());
        }
        if (filter.getWarehouseId() != null) {
            query.setParameter("warehouseId", filter.getWarehouseId());
        }
        if (filter.getCategoryId() != null) {
            query.setParameter("categoryId", filter.getCategoryId());
        }
        if (filter.hasSearch()) {
            query.setParameter("searchKey", filter.getLikeSearchKey());
        }
        return query.getSingleResult();
    }

    private StringBuilder getBaseSql(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from WarehouseProduct wi");
        sql.append(" join wi.product p");
        sql.append(" join wi.warehouse w");
        sql.append(" where p.deleted=false");
        sql.append(" and p.branchId =:branchId");

        if (filter.getDepartmentId() != null) {
            sql.append(" and w.departmentId=:departmentId");
        }
        if (filter.getWarehouseId() != null) {
            sql.append(" and w.id=:warehouseId");
        }
        if (filter.getCategoryId() != null) {
            sql.append(" and p.categoryId=:categoryId");
        }
        if (filter.hasSearch()) {
            sql.append(" and (");
            sql.append(" lower(p.name)").append(filter.getSearchForQuery());
            sql.append(" or lower(p.barcode)").append(filter.getSearchForQuery());
            sql.append(" or lower(p.catalogCode)").append(filter.getSearchForQuery());
            sql.append(" or lower(p.catalogName)").append(filter.getSearchForQuery());
            sql.append(" or lower(p.sku)").append(filter.getSearchForQuery());
            sql.append(") ");
        }
        return sql;
    }
}
