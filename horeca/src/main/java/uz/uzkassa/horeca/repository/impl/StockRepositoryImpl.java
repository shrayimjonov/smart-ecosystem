package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.domain.stock.Stock;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.enums.StockStatus;
import uz.uzkassa.horeca.enums.TransferType;
import uz.uzkassa.horeca.filters.StockFilter;
import uz.uzkassa.horeca.repository.StockRepositoryCustom;


@SuppressWarnings("unused")
public class StockRepositoryImpl extends BaseRepositoryImpl implements StockRepositoryCustom {

    @Override
    public ResultList<Stock> getResultList(StockFilter filter) {
        ResultList<Stock> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select s from Stock s ");
        sql.append(" where s.deleted = false ");
        sql.append(" and s.transferType =:transferType");
        sql.append(" and s.branchId =:branchId");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and s.status =:status ");
        }
        if (filter.getCustomerId() != null) {
            sql.append(" and s.customerId =:customerId ");
        }
        if (filter.getSupplierId() != null) {
            sql.append(" and s.supplierId =:supplierId ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and s.stockDate>=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and s.stockDate<=:toDate ");
        }
        if (filter.hasSearch()) {
            sql.append(" and (");
            sql.append(" lower(s.stockNumber)").append(filter.getLikeSearchKey());
            sql.append(" or lower(s.description)").append(filter.getLikeSearchKey());
            sql.append(")");
        }

        String countSql = sql.toString().replaceFirst("select s", "select count(s.id)");
        sql.append(" order by s.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Stock> query = entityManager
            .createQuery(sql.toString(), Stock.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("transferType", TransferType.valueOf(filter.getTransferType()));
        countQuery.setParameter("transferType", TransferType.valueOf(filter.getTransferType()));

        query.setParameter("branchId", filter.getBranchId());
        countQuery.setParameter("branchId", filter.getBranchId());

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", StockStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", StockStatus.valueOf(filter.getStatus()));
        }
        if (filter.getCustomerId() != null) {
            query.setParameter("customerId", filter.getCustomerId());
            countQuery.setParameter("customerId", filter.getCustomerId());
        }
        if (filter.getSupplierId() != null) {
            query.setParameter("supplierId", filter.getSupplierId());
            countQuery.setParameter("supplierId", filter.getSupplierId());
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
