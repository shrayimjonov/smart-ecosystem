package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.domain.stocktransfer.StockTransfer;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.enums.StockTransferStatus;
import uz.uzkassa.horeca.filters.StockTransferFilter;
import uz.uzkassa.horeca.repository.StockTransferRepositoryCustom;

public class StockTransferRepositoryImpl extends BaseRepositoryImpl implements StockTransferRepositoryCustom {

    public ResultList<StockTransfer> getResultList(StockTransferFilter filter) {
        ResultList<StockTransfer> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" select st ");
        sql.append(" from StockTransfer st ");
        sql.append(" where st.deleted = false ");
        sql.append(" and st.fromBranchId=:branchId");
        if (filter.getToBranchId() != null) {
            sql.append(" and st.toBranchId=:toBranchId");
        }
        if (filter.getFromWarehouseId() != null) {
            sql.append(" and st.fromWarehouseId=:fromWarehouseId");
        }
        if (filter.getToWarehouseId() != null) {
            sql.append(" and st.toWarehouseId=:toWarehouseId");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and st.status=:status");
        }
        if (filter.getFrom() != null) {
            sql.append(" and st.transferDate >=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and st.transferDate >=:toDate");
        }
        if (filter.hasSearch()) {
            sql.append(" and(");
            sql.append(" lower(st.transferNumber").append(filter.getLikeSearchKey());
            sql.append(" )");
        }
        String countSql = sql.toString().replaceFirst("select st", "select count(st.id)");

        sql.append(" order by st.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<StockTransfer> query = entityManager
            .createQuery(sql.toString(), StockTransfer.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("branchId", filter.getBranchId());
        countQuery.setParameter("branchId", filter.getBranchId());

        if (filter.getToBranchId() != null) {
            query.setParameter("toBranchId", filter.getToBranchId());
            countQuery.setParameter("toBranchId", filter.getToBranchId());
        }
        if (filter.getFromWarehouseId() != null) {
            query.setParameter("fromWarehouseId", filter.getFromWarehouseId());
            countQuery.setParameter("fromWarehouseId", filter.getFromWarehouseId());
        }
        if (filter.getToWarehouseId() != null) {
            query.setParameter("toWarehouseId", filter.getToWarehouseId());
            countQuery.setParameter("toWarehouseId", filter.getToWarehouseId());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", StockTransferStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", StockTransferStatus.valueOf(filter.getStatus()));
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

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }

        return resultList;
    }
}
