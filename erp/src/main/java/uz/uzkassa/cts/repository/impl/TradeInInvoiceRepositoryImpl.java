package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.TradeInInvoice;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.XfileStatus;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;
import uz.uzkassa.cts.repository.custom.TradeInInvoiceRepositoryCustom;

import java.time.LocalDate;
import java.util.List;

@SuppressWarnings("unused")
public class TradeInInvoiceRepositoryImpl extends BaseRepositoryImpl implements TradeInInvoiceRepositoryCustom {

    @Override
    public List<TradeInInvoice> updateInvoiceXfileStatuses() {
        Query query = entityManager.createQuery(
                "SELECT e FROM TradeInInvoice e " +
                        " WHERE e.invoiceDate >= :date " +
                        " order by e.id");
        query.setParameter("date", LocalDate.of(2021, 1, 1).atStartOfDay());

        return (List<TradeInInvoice>) query.getResultList();
    }

    @Override
    public ResultList<TradeInInvoice> getResultList(TradeInInvoiceFilter filter) {
        ResultList<TradeInInvoice> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select t from TradeInInvoice t");
        sql.append(" join t.contract con");
        sql.append(" join con.customer cus");
        sql.append(" where t.deleted = false");

        if (filter.getId() != null) {
            sql.append(" and t.id = :id");
        }
        if (filter.getFrom() != null) {
            sql.append(" and t.invoiceDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and t.invoiceDate <= :toDate");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and t.status = :status");
        }
        if (filter.getXfileStatus() != null) {
            if (XfileStatus.NOT_SEND == filter.getXfileStatus()) {
                sql.append(" and t.xfileId is null");
            } else {
                sql.append(" and t.xfileStatus = :xfileStatus");
            }
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(cus.name) ").append(filter.getLikeSearch());
            sql.append(" or lower(cus.tin) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select t", " select count(t.id)");
        sql.append(" order by t.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<TradeInInvoice> query = entityManager
                .createQuery(sql.toString(), TradeInInvoice.class)
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
        if (filter.getXfileStatus() != null && XfileStatus.NOT_SEND != filter.getXfileStatus()) {
            query.setParameter("xfileStatus", filter.getXfileStatus());
            countQuery.setParameter("xfileStatus", filter.getXfileStatus());
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
