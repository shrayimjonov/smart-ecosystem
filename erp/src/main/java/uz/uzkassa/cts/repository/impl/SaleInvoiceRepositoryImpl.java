package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.SaleInvoice;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;
import uz.uzkassa.cts.repository.custom.SaleInvoiceRepositoryCustom;

@SuppressWarnings("unused")
public class SaleInvoiceRepositoryImpl extends BaseRepositoryImpl implements SaleInvoiceRepositoryCustom {

    @Override
    public ResultList<SaleInvoice> getResultList(TradeInInvoiceFilter filter) {
        ResultList<SaleInvoice> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select s from SaleInvoice s");
        sql.append(" join s.saleContract con");
        sql.append(" join con.customer cus");
        sql.append(" where s.deleted = false");

        if (filter.getId() != null) {
            sql.append(" and s.id = :id");
        }
        if (filter.getFrom() != null) {
            sql.append(" and s.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and s.createdDate <= :toDate");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and s.status = :status");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(cus.name)").append(filter.getLikeSearch());
            sql.append(" or lower(cus.tin)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select s", " select count(s.id)");
        sql.append(" order by s.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<SaleInvoice> query = entityManager
                .createQuery(sql.toString(), SaleInvoice.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getId() != null) {
            query.setParameter("id", filter.getId());
            countQuery.setParameter("id", filter.getId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFromStartOfDay());
            countQuery.setParameter("fromDate", filter.getFromStartOfDay());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getToEndOfDay());
            countQuery.setParameter("toDate", filter.getToEndOfDay());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", ContractStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", ContractStatus.valueOf(filter.getStatus()));
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
