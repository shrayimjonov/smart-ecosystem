package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.receipt.Receipt;
import uz.uzkassa.horeca.dto.receipt.ReceiptStatisticDTO;
import uz.uzkassa.horeca.enums.ReceiptPaymentType;
import uz.uzkassa.horeca.enums.ReceiptStatus;
import uz.uzkassa.horeca.filters.ReceiptFilter;
import uz.uzkassa.horeca.repository.ReceiptRepositoryCustom;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;


@SuppressWarnings("unused")
public class ReceiptRepositoryImpl extends BaseRepositoryImpl implements ReceiptRepositoryCustom {

    @Override
    public Page<Receipt> getResultList(ReceiptFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct r from Receipt r ");
        if (filter.getPaymentType() != null) {
            sql.append(" join r.payments pt ");
        }
        sql.append(" where r.deleted is not true");
        if (filter.getBranchId() != null) {
            sql.append(" and r.branchId =:branchId");
        }
        if (filter.getTerminalId() != null) {
            sql.append(" and r.terminalId =:terminalId");
        }
        if (filter.getUserId() != null) {
            sql.append(" and r.creatorId =:creatorId");
        }
        if (filter.getShiftNumber() != null) {
            sql.append(" and r.shiftNumber =:shiftNumber");
        }
        if (filter.getIsFiscal() != null) {
            if (filter.getIsFiscal()) {
                sql.append(" and r.fiscalSign is not null and r.fiscalUrl is not null");
            } else {
                sql.append(" and r.fiscalSign is null and r.fiscalUrl is null");
            }
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and r.status=:status ");
        }
        if (filter.getItemsCount() != null) {
            sql.append(" and r.itemsCount =:itemsCount");
        }
        if (filter.getPaymentType() != null) {
            sql.append(" and pt.paymentType =:paymentType");
        }
        if (filter.getFrom() != null) {
            sql.append(" and r.receiptDate >=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and r.receiptDate <=:toDate ");
        }
        if (filter.getFromTotalPrice() != null) {
            sql.append(" and r.totalCost >=:fromTotalCost");
        }
        if (filter.getToTotalPrice() != null) {
            sql.append(" and r.totalCost <=:toTotalCost");
        }
        if (filter.hasSearch()) {
            sql.append(" and (");
            sql.append(" lower(r.uid)").append(filter.getLikeSearchKey());
            sql.append(" or lower(r.originUid)").append(filter.getLikeSearchKey());
            sql.append(")");
        }

        String countSql = sql.toString().replaceFirst("select distinct r ", "select count(r.id)");
        sql.append(" order by r.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Receipt> query = entityManager
                .createQuery(sql.toString(), Receipt.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getTerminalId() != null) {
            query.setParameter("terminalId", filter.getTerminalId());
            countQuery.setParameter("terminalId", filter.getTerminalId());
        }
        if (filter.getUserId() != null) {
            query.setParameter("creatorId", filter.getUserId());
            countQuery.setParameter("creatorId", filter.getUserId());
        }
        if (filter.getShiftNumber() != null) {
            query.setParameter("shiftNumber", filter.getShiftNumber());
            countQuery.setParameter("shiftNumber", filter.getShiftNumber());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", ReceiptStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", ReceiptStatus.valueOf(filter.getStatus()));
        }
        if (filter.getItemsCount() != null) {
            query.setParameter("itemsCount", filter.getItemsCount());
            countQuery.setParameter("itemsCount", filter.getItemsCount());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }
        if (filter.getFromTotalPrice() != null) {
            query.setParameter("fromTotalCost", filter.getFromTotalPrice());
            countQuery.setParameter("fromTotalCost", filter.getFromTotalPrice());
        }
        if (filter.getToTotalPrice() != null) {
            query.setParameter("toTotalCost", filter.getToTotalPrice());
            countQuery.setParameter("toTotalCost", filter.getToTotalPrice());
        }
        if (filter.getPaymentType() != null) {
            query.setParameter("paymentType", filter.getPaymentType());
            countQuery.setParameter("paymentType", filter.getPaymentType());
        }
        if (filter.hasSearch()) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            return new PageImpl<>(query.getResultList(), filter.getSortedPageable(), count);
        }
        return new PageImpl<>(Collections.emptyList(), filter.getSortedPageable(), count);
    }

    private StringBuilder getBaseSql(ReceiptFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from Receipt r ");
        sql.append(" join r.company c ");
        sql.append(" join r.payments p ");
        sql.append(" where r.deleted = false ");
        sql.append(" and c.id =:companyId");
        if (filter.getBranchId() != null) {
            sql.append(" and r.branchId =:branchId");
        }
        if (filter.getTerminalId() != null) {
            sql.append(" and r.terminalId =:terminalId");
        }
        if (filter.getUserId() != null) {
            sql.append(" and r.creatorId =:creatorId");
        }
        if (filter.getShiftNumber() != null) {
            sql.append(" and r.shiftNumber =:shiftNumber");
        }
        if (filter.getIsFiscal() != null) {
            if (filter.getIsFiscal()) {
                sql.append(" and r.fiscalSign is not null and r.fiscalUrl is not null");
            } else {
                sql.append(" and r.fiscalSign is null and r.fiscalUrl is null");
            }
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and r.status=:status ");
        }
        if (filter.getItemsCount() != null) {
            sql.append(" and r.positionCount =:itemsCount");
        }
        if (filter.getFrom() != null) {
            sql.append(" and r.receiptDate >=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and r.receiptDate <=:toDate ");
        }
        if (filter.getFromTotalPrice() != null) {
            sql.append(" and r.totalCost >=:fromTotalCost");
        }
        if (filter.getToTotalPrice() != null) {
            sql.append(" and r.totalCost <=:toTotalCost");
        }
        if (!CollectionUtils.isEmpty(filter.getPaymentTypes())) {
            sql.append(" and p.paymentType in (:paymentTypes)");
        }

        if (filter.hasSearch()) {
            sql.append(" and (");
            sql.append(" lower(r.uid)").append(filter.getLikeSearchKey());
            sql.append(" or lower(r.originUid)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.tin)").append(filter.getLikeSearchKey());
            sql.append(")");
        }
        return sql;
    }

    private StringBuilder getBaseSqlForStatistics(ReceiptFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from receipt r ");
        if (filter.getPaymentType() != null) {
            sql.append(" inner join receipt_payment rp on r.id=rp.receipt_id ");
        }
        sql.append(" inner join company c on r.company_id=c.id ");
        sql.append(" where r.deleted = false ");
        sql.append(" and c.id =").append(filter.getCompanyId());
        if (filter.getBranchId() != null) {
            sql.append(" and r.branch_id =").append(filter.getBranchId());
        }
        if (filter.getTerminalId() != null) {
            sql.append(" and r.terminal_id =").append(filter.getTerminalId());
        }
        if (filter.getItemsCount() != null) {
            sql.append(" and r.items_count =").append(filter.getItemsCount());
        }
        if (filter.getUserId() != null) {
            sql.append(" and r.creator_id =").append(filter.getUserId());
        }
        if (filter.getShiftNumber() != null) {
            sql.append(" and r.shift_number =").append(filter.getShiftNumber());
        }
        if (filter.getIsFiscal() != null) {
            if (filter.getIsFiscal()) {
                sql.append(" and r.fiscal_sign is not null and r.fiscal_url is not null");
            } else {
                sql.append(" and r.fiscal_sign is null and r.fiscal_url is null");
            }
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and r.status='").append(filter.getStatus()).append("'");
        }
        if (filter.getFrom() != null) {
            sql.append(" and r.receipt_date >='").append(filter.getFrom()).append("'");
        }
        if (filter.getTo() != null) {
            sql.append(" and r.receipt_date <='").append(filter.getTo()).append("'");
        }
        if (filter.getFromTotalPrice() != null) {
            sql.append(" and r.total_cost >=").append(filter.getFromTotalPrice());
        }
        if (filter.getToTotalPrice() != null) {
            sql.append(" and r.total_cost <=").append(filter.getToTotalPrice());
        }
        if (filter.getPaymentType() != null) {
            sql.append(" and rp.payment_type ='").append(filter.getPaymentType().name()).append("'");
        }

        if (filter.hasSearch()) {
            sql.append(" and (");
            sql.append(" lower(r.uid) like '").append(filter.getSearchForQuery()).append("'");
            sql.append(" or lower(r.origin_uid) like '").append(filter.getSearchForQuery()).append("'");
            sql.append(" or lower(c.tin) like '").append(filter.getSearchForQuery()).append("'");
            sql.append(")");
        }
        return sql;
    }

    @Override
    public ReceiptStatisticDTO getStatistics(ReceiptFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select coalesce(count(distinct r.shift_number), 0) as shifts ");
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(count(distinct r.id), 0) as receipts");
        filter.setStatus(ReceiptStatus.PAID.getCode());
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(count(distinct r.id), 0) as returnedReceipts");
        filter.setStatus(ReceiptStatus.RETURNED.getCode());
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(r.total_paid), 0) as revenue");
        filter.setStatus(ReceiptStatus.PAID.getCode());
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(r.total_paid), 0) as totalReturnedRevenue");
        filter.setStatus(ReceiptStatus.RETURNED.getCode());
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as cash");
        filter.setStatus(ReceiptStatus.PAID.getCode());
        filter.setPaymentType(ReceiptPaymentType.CASH);
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as returnedCash");
        filter.setStatus(ReceiptStatus.RETURNED.getCode());
        filter.setPaymentType(ReceiptPaymentType.CASH);
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as card");
        filter.setStatus(ReceiptStatus.PAID.getCode());
        filter.setPaymentType(ReceiptPaymentType.CARD);
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as returnedCard");
        filter.setStatus(ReceiptStatus.RETURNED.getCode());
        filter.setPaymentType(ReceiptPaymentType.CARD);
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as cardUzCard");
        filter.setStatus(ReceiptStatus.PAID.getCode());
        filter.setPaymentType(ReceiptPaymentType.UZCARD);
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as returnedUZCARD");
        filter.setStatus(ReceiptStatus.RETURNED.getCode());
        filter.setPaymentType(ReceiptPaymentType.UZCARD);
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as cardHumo");
        filter.setStatus(ReceiptStatus.PAID.getCode());
        filter.setPaymentType(ReceiptPaymentType.HUMO);
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as returnedHUMO");
        filter.setStatus(ReceiptStatus.RETURNED.getCode());
        filter.setPaymentType(ReceiptPaymentType.HUMO);
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as cardLoyalty");
        filter.setStatus(ReceiptStatus.PAID.getCode());
        filter.setPaymentType(ReceiptPaymentType.LOYALTY_CARD);
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as returnedLoyalty");
        filter.setStatus(ReceiptStatus.RETURNED.getCode());
        filter.setPaymentType(ReceiptPaymentType.LOYALTY_CARD);
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as cardOther");
        filter.setStatus(ReceiptStatus.PAID.getCode());
        filter.setPaymentType(ReceiptPaymentType.OTHER);
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as returnedOther");
        filter.setStatus(ReceiptStatus.RETURNED.getCode());
        filter.setPaymentType(ReceiptPaymentType.OTHER);
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as nds");
        filter.setStatus(ReceiptStatus.PAID.getCode());
        filter.setPaymentType(ReceiptPaymentType.NDS);
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as returnedNDS");
        filter.setStatus(ReceiptStatus.RETURNED.getCode());
        filter.setPaymentType(ReceiptPaymentType.NDS);
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(r.items_count),0) as positionsInReceipts");
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as discount");
        filter.setStatus(ReceiptStatus.PAID.getCode());
        filter.setPaymentType(ReceiptPaymentType.DISCOUNT);
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as returnedDiscount");
        filter.setStatus(ReceiptStatus.RETURNED.getCode());
        filter.setPaymentType(ReceiptPaymentType.DISCOUNT);
        sql.append(getBaseSqlForStatistics(filter));

        Query query = entityManager.createNativeQuery(sql.toString(), Object.class);

        List<Object> result = query.getResultList();

        ReceiptStatisticDTO receiptStatisticDTO = new ReceiptStatisticDTO();
        if (!CollectionUtils.isEmpty(result)) {
            receiptStatisticDTO.setShiftCount(((BigDecimal) result.get(0)).longValue());
            receiptStatisticDTO.setReceipts(((BigDecimal) result.get(1)).longValue());
            receiptStatisticDTO.setReturnedReceipts(((BigDecimal) result.get(2)).longValue());
            receiptStatisticDTO.setRevenue((BigDecimal) result.get(3));
            receiptStatisticDTO.setTotalReturnedRevenue((BigDecimal) result.get(4));
            receiptStatisticDTO.setCash((BigDecimal) result.get(5));
            receiptStatisticDTO.setReturnedCash((BigDecimal) result.get(6));
            receiptStatisticDTO.setCard((BigDecimal) result.get(7));
            receiptStatisticDTO.setReturnedCard((BigDecimal) result.get(8));
            receiptStatisticDTO.setCardUzCard((BigDecimal) result.get(9));
            receiptStatisticDTO.setReturnedUZCARD((BigDecimal) result.get(10));
            receiptStatisticDTO.setCardHumo((BigDecimal) result.get(11));
            receiptStatisticDTO.setReturnedHUMO((BigDecimal) result.get(12));
            receiptStatisticDTO.setCardLoyalty((BigDecimal) result.get(13));
            receiptStatisticDTO.setReturnedLoyalty((BigDecimal) result.get(14));
            receiptStatisticDTO.setCardOther((BigDecimal) result.get(15));
            receiptStatisticDTO.setReturnedOther((BigDecimal) result.get(16));
            receiptStatisticDTO.setNds((BigDecimal) result.get(17));
            receiptStatisticDTO.setReturnedNDS((BigDecimal) result.get(18));
            receiptStatisticDTO.setPositionsInReceipts(((BigDecimal) result.get(19)).longValue());
            receiptStatisticDTO.setDiscount((BigDecimal) result.get(20));
            receiptStatisticDTO.setReturnedDiscount((BigDecimal) result.get(21));
        }
        return receiptStatisticDTO;
    }
}
