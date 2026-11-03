package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.KapitalbankPayment;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.SortType;
import uz.uzkassa.cts.filter.KapitalBankFilter;
import uz.uzkassa.cts.repository.custom.KapitalbankPaymentRepositoryCustom;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@SuppressWarnings("unused")
public class KapitalbankPaymentRepositoryImpl extends BaseRepositoryImpl implements KapitalbankPaymentRepositoryCustom {

    @Override
    public ResultList<KapitalbankPayment> getList(KapitalBankFilter filter) {
        ResultList<KapitalbankPayment> resultList = new ResultList<>();
        Query query = entityManager.createNativeQuery(getQuery(filter, false, false).toString(), KapitalbankPayment.class);
        query.setFirstResult(filter.getStart()).setMaxResults(filter.getSize());
        List<KapitalbankPayment> list = query.getResultList();

        StringBuilder sqlCount = getQuery(filter, true, false);
        Query countQuery = entityManager.createNativeQuery(sqlCount.toString());

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    public List<KapitalbankPayment> getDownloadUrl(KapitalBankFilter filter) {
        Query query = entityManager.createNativeQuery(getQuery(filter, false, false).toString(), KapitalbankPayment.class);

        return (List<KapitalbankPayment>) query.getResultList();
    }

    @Override
    public BigDecimal getConfirmedAmount(KapitalBankFilter filter) {
        Query query = entityManager.createNativeQuery(getQuery(filter, false, true).toString());
        return (BigDecimal) query.getSingleResult();
    }

    @Override
    public ResultList<KapitalbankPayment> getList1c(KapitalBankFilter filter) {
        ResultList<KapitalbankPayment> resultList = new ResultList<>();
        Query query = entityManager.createNativeQuery(getQuery(filter, false, false).toString(), KapitalbankPayment.class);
        query.setFirstResult(filter.getStart()).setMaxResults(filter.getSize());
        List<KapitalbankPayment> list = query.getResultList();

        StringBuilder sqlCount = getQuery(filter, true, false);
        Query countQuery = entityManager.createNativeQuery(sqlCount.toString());

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getQuery(KapitalBankFilter filter, boolean isCount, boolean calculateSum) {
        StringBuilder sql = new StringBuilder();
        if (calculateSum)
            sql.append(" SELECT sum(amount) FROM kapitalbank_payments \n");
        else if (isCount)
            sql.append(" SELECT count(*) FROM kapitalbank_payments \n");
        else
            sql.append(" SELECT * FROM kapitalbank_payments \n");
        sql.append(" where 1=1 ");
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            String search = filter.getSearch().toUpperCase();
            sql.append(" and (masked_phone_number ilike '%").append(search).append("%'");
            sql.append(" or company_name ilike '%").append(search).append("%'");
            sql.append(" or pan ilike '%").append(search).append("%'");
            sql.append(" or tin ilike '%").append(search).append("%' )");
        }
        if (filter.getFrom() != null && filter.getTo() != null) {
            sql.append(" and pay_timestamp > '")
                    .append(filter.getFrom().atStartOfDay())
                    .append("' and ")
                    .append(" pay_timestamp < '")
                    .append(filter.getTo().atTime(LocalTime.MAX))
                    .append("' ");
        }
        if (filter.getKapitalbankPaymentStatus() != null)
            sql.append(" and kapitalbank_payment_status = ").append(filter.getKapitalbankPaymentStatus().ordinal());

        if (filter.getTransactionId() != null)
            sql.append(" and transaction_id = ").append(filter.getTransactionId());

        if (filter.getAmount() != null)
            sql.append(" and amount = ").append(filter.getAmount());

//        if (isCount || calculateSum) {
//            sql.append(" group by id");
//        }
        if (!isCount && !calculateSum) {
            sql.append(" order by ").append(filter.getOrderBy());
            if (SortType.asc.equals(filter.getSortOrder())) {
                sql.append(" asc");
            } else {
                sql.append(" desc");
            }
        }
        return sql;
    }
}
