package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.horeca.domain.stockadjustment.StockAdjustment;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.enums.StockAdjustmentStatus;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.repository.StockAdjustmentRepositoryCustom;

public class StockAdjustmentRepositoryImpl extends BaseRepositoryImpl implements StockAdjustmentRepositoryCustom {

    @Override
    public ResultList<StockAdjustment> getResultList(BaseFilter filter) {
        ResultList<StockAdjustment> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" select sa ");
        sql.append(" from StockAdjustment sa ");
        sql.append(" where sa.deleted =false ");
        sql.append(" and sa.companyId=:companyId ");

        if (filter.getBranchId() != null) {
            sql.append(" and sa.branchId=:branchId ");
        }
        if (filter.getWarehouseId() != null) {
            sql.append(" and sa.warehouseId=:warehouseId ");
        }
        if (filter.getStatus() != null) {
            sql.append(" and sa.status=:status ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and sa.adjustmentDate>=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and sa.adjustmentDate<=:toDate ");
        }
        if (filter.hasSearch()) {
            sql.append(" and (");
            sql.append(" lower(sa.adjustmentNumber) ").append(filter.getSearchForQuery());
            sql.append(" or lower(sa.description) ").append(filter.getSearchForQuery());
            sql.append(") ");
        }

        String countSql = sql.toString().replaceFirst("select sa", "select count(sa.id)");

        sql.append(" order by sa.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<StockAdjustment> query = entityManager
                .createQuery(sql.toString(), StockAdjustment.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("companyId", filter.getCompanyId());

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getWarehouseId() != null) {
            query.setParameter("warehouseId", filter.getWarehouseId());
            countQuery.setParameter("warehouseId", filter.getWarehouseId());
        }
        if (filter.getStatus() != null) {
            query.setParameter("status", StockAdjustmentStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", StockAdjustmentStatus.valueOf(filter.getStatus()));
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }

        if (filter.hasSearch()) {
            query.setParameter("searchKey", filter.getLikeSearchKey());
            countQuery.setParameter("searchKey", filter.getLikeSearchKey());
        }

        Long listCount = countQuery.getSingleResult();
        if (listCount > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(listCount);

        }

        return resultList;
    }
}
