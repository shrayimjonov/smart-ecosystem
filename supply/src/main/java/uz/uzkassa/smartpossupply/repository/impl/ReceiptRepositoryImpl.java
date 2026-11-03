package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.receipt.Receipt;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.receipt.ReceiptStatisticDTO;
import uz.uzkassa.smartpossupply.dto.receipt.TerminalDashboardStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.ReceiptPaymentType;
import uz.uzkassa.smartpossupply.enums.ReceiptStatus;
import uz.uzkassa.smartpossupply.enums.Source;
import uz.uzkassa.smartpossupply.filters.ReceiptFilter;
import uz.uzkassa.smartpossupply.repository.ReceiptRepositoryCustom;

import java.math.BigDecimal;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 */
public class ReceiptRepositoryImpl extends BaseRepositoryImpl implements ReceiptRepositoryCustom {

    @Override
    public ResultList<Receipt> getResultList(ReceiptFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
        ResultList<Receipt> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select r ");
        sql.append(getBaseSql(filter));

        String countSql = sql.toString().replace("select r", "select count(distinct r.id) ");
        sql.append(" group by r.id ");
        sql.append(" order by r.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Receipt> query = entityManager
                .createQuery(sql.toString(), Receipt.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("companyId", filter.getCompanyId());

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }

        if (filter.getShiftNumber() != null) {
            query.setParameter("shiftNumber", filter.getShiftNumber());
            countQuery.setParameter("shiftNumber", filter.getShiftNumber());
        }
        if (filter.getCreatorId() != null) {
            query.setParameter("creatorId", filter.getCreatorId());
            countQuery.setParameter("creatorId", filter.getCreatorId());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", ReceiptStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", ReceiptStatus.valueOf(filter.getStatus()));
        }
        if (StringUtils.isNotEmpty(filter.getSource())) {
            query.setParameter("source", filter.getSource());
            countQuery.setParameter("source", filter.getSource());
        }
        if (filter.getPositionCount() != null) {
            query.setParameter("positionCount", filter.getPositionCount());
            countQuery.setParameter("positionCount", filter.getPositionCount());
        }
        if (!CollectionUtils.isEmpty(filter.getPaymentTypes())) {
            query.setParameter("paymentTypes", filter.getPaymentTypes().stream().map(ReceiptPaymentType::valueOf).toList());
            countQuery.setParameter("paymentTypes", filter.getPaymentTypes().stream().map(ReceiptPaymentType::valueOf).toList());
        }
        if (filter.getFromTotalPrice() != null) {
            query.setParameter("fromTotalPrice", filter.getFromTotalPrice());
            countQuery.setParameter("fromTotalPrice", filter.getFromTotalPrice());
        }
        if (filter.getToTotalPrice() != null) {
            query.setParameter("toTotalPrice", filter.getToTotalPrice());
            countQuery.setParameter("toTotalPrice", filter.getToTotalPrice());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }
        if (StringUtils.isNotEmpty(filter.getPaymentType())) {
            query.setParameter("paymentType", ReceiptPaymentType.valueOf(filter.getPaymentType()));
            countQuery.setParameter("paymentType", ReceiptPaymentType.valueOf(filter.getPaymentType()));
        }
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
            countQuery.setParameter("searchKey", filter.getSearchValue());
        }
        long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    public TerminalDashboardStatisticsDTO getTerminalDashboardStatistics(ReceiptFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
        StringBuilder sql = new StringBuilder();
        sql.append(" select ");

        sql.append(" coalesce(count (distinct r.id),0) ");
        sql.append(" as receiptCount, ");

        sql.append(" coalesce(count (distinct r.shiftNumber),0) ");
        sql.append(" as shiftCount, ");

        sql.append(" coalesce(sum(r.totalPaid),0) ");
        sql.append(" as totalSales, ");

        sql.append(" coalesce(sum(r.totalCash),0) ");
        sql.append(" as totalCash, ");

        sql.append(" coalesce(sum(r.totalCard),0) ");
        sql.append(" as totalCard, ");

        sql.append(" coalesce(count(distinct s.id),0) ");
        sql.append(" as stockOutCount, ");

        sql.append(" coalesce(count(distinct o.id),0) ");
        sql.append(" as orderCount, ");

        sql.append(" coalesce(sum(r.totalQty),0) ");
        sql.append(" as productCount ");

        sql.append(getBaseSql(filter));

        TypedQuery<TerminalDashboardStatisticsDTO> query = entityManager.createQuery(sql.toString(), TerminalDashboardStatisticsDTO.class);

        query.setParameter("companyId", filter.getCompanyId());
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
        }

        if (filter.getShiftNumber() != null) {
            query.setParameter("shiftNumber", filter.getShiftNumber());
        }
        if (filter.getCreatorId() != null) {
            query.setParameter("creatorId", filter.getCreatorId());
        }
        if (filter.getStatus() != null) {
            query.setParameter("status", ReceiptStatus.valueOf(filter.getStatus()));
        }
        if (!CollectionUtils.isEmpty(filter.getPaymentTypes())) {
            query.setParameter("paymentTypes", filter.getPaymentTypes().stream().map(ReceiptPaymentType::valueOf).toList());
        }
        if (StringUtils.isNotEmpty(filter.getSource())) {
            query.setParameter("source", Source.valueOf(filter.getSource()));
        }
        if (filter.getPositionCount() != null) {
            query.setParameter("positionCount", filter.getPositionCount());
        }
        if (filter.getFrom() != null) {
            query.setParameter("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("to", filter.getTo());
        }
        if (filter.getFromTotalPrice() != null) {
            query.setParameter("fromTotalPrice", filter.getFromTotalPrice());
        }
        if (filter.getToTotalPrice() != null) {
            query.setParameter("toTotalPrice", filter.getToTotalPrice());
        }
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
        return query.getSingleResult();
    }

    private StringBuilder getBaseSql(ReceiptFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from Receipt r");
        sql.append(" join r.payments as p");
        sql.append(" join r.stockOut as s");
        sql.append(" join r.branch as b");
        sql.append(" join r.customer as c");
        sql.append(" left join s.order as o");
        sql.append(" where r.deleted = false ");
        sql.append(" and b.companyId=:companyId");
        if (filter.getBranchId() != null) {
            sql.append(" and r.branchId=:branchId ");
        }
        if (filter.getCreatorId() != null) {
            sql.append(" and r.creatorId=:creatorId ");
        }
        if (filter.getShiftNumber() != null) {
            sql.append(" and r.shiftNumber=:shiftNumber ");
        }
        if (filter.getPositionCount() != null) {
            sql.append(" and r.positionCount=:positionCount ");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and r.status=:status ");
        }
        if (!CollectionUtils.isEmpty(filter.getPaymentTypes())) {
            sql.append(" and p.paymentType in (:paymentTypes) ");
        }
        if (StringUtils.isNotEmpty(filter.getPaymentType())) {
            sql.append(" and p.paymentType=:paymentType ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and r.createdDate>=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and r.createdDate<=:toDate ");
        }
        if (filter.getFromTotalPrice() != null) {
            sql.append(" and r.totalPrice>=:fromTotalPrice ");
        }
        if (filter.getToTotalPrice() != null) {
            sql.append(" and r.totalPrice<=:toTotalPrice ");
        }
        if (filter.isNotEmpty()) {
            sql.append(" and ( ");
            sql.append(" lower(r.uid) ").append(filter.getSearchKey());
            sql.append(" or lower(r.terminalSerialNumber) ").append(filter.getSearchKey());
            sql.append(" or lower(c.name) ").append(filter.getSearchKey());
            sql.append(" or lower(c.tin) ").append(filter.getSearchKey());
            sql.append(" or lower(c.phone) ").append(filter.getSearchKey());
            sql.append(") ");
        }
        return sql;
    }

    @Override
    public ReceiptStatisticDTO getStatistics(ReceiptFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
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
        filter.setPaymentType(ReceiptPaymentType.CASH.name());
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as returnedCash");
        filter.setStatus(ReceiptStatus.RETURNED.getCode());
        filter.setPaymentType(ReceiptPaymentType.CASH.name());
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as card");
        filter.setStatus(ReceiptStatus.PAID.getCode());
        filter.setPaymentType(ReceiptPaymentType.CARD.name());
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as returnedCard");
        filter.setStatus(ReceiptStatus.RETURNED.getCode());
        filter.setPaymentType(ReceiptPaymentType.CARD.name());
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as cardUzCard");
        filter.setStatus(ReceiptStatus.PAID.getCode());
        filter.setPaymentType(ReceiptPaymentType.UZCARD.name());
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as returnedUZCARD");
        filter.setStatus(ReceiptStatus.RETURNED.getCode());
        filter.setPaymentType(ReceiptPaymentType.UZCARD.name());
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as cardHumo");
        filter.setStatus(ReceiptStatus.PAID.getCode());
        filter.setPaymentType(ReceiptPaymentType.HUMO.name());
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as returnedHUMO");
        filter.setStatus(ReceiptStatus.RETURNED.getCode());
        filter.setPaymentType(ReceiptPaymentType.HUMO.name());
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as cardLoyalty");
        filter.setStatus(ReceiptStatus.PAID.getCode());
        filter.setPaymentType(ReceiptPaymentType.LOYALTY_CARD.name());
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as returnedLoyalty");
        filter.setStatus(ReceiptStatus.RETURNED.getCode());
        filter.setPaymentType(ReceiptPaymentType.LOYALTY_CARD.name());
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as cardOther");
        filter.setStatus(ReceiptStatus.PAID.getCode());
        filter.setPaymentType(ReceiptPaymentType.OTHER.name());
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as returnedOther");
        filter.setStatus(ReceiptStatus.RETURNED.getCode());
        filter.setPaymentType(ReceiptPaymentType.OTHER.name());
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as nds");
        filter.setStatus(ReceiptStatus.PAID.getCode());
        filter.setPaymentType(ReceiptPaymentType.NDS.name());
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as returnedNDS");
        filter.setStatus(ReceiptStatus.RETURNED.getCode());
        filter.setPaymentType(ReceiptPaymentType.NDS.name());
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(r.position_count),0) as positionsInReceipts");
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(r.position_count),0) as positionsInReturnedReceipts");
        filter.setStatus(ReceiptStatus.RETURNED.getCode());
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as discount");
        filter.setStatus(ReceiptStatus.PAID.getCode());
        filter.setPaymentType(ReceiptPaymentType.DISCOUNT.name());
        sql.append(getBaseSqlForStatistics(filter));
        sql.append(" union all");
        sql.append(" select coalesce(sum(rp.price),0) as returnedDiscount");
        filter.setStatus(ReceiptStatus.RETURNED.getCode());
        filter.setPaymentType(ReceiptPaymentType.DISCOUNT.name());
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
            receiptStatisticDTO.setPositionsInReturnedReceipts(((BigDecimal) result.get(20)).longValue());
            receiptStatisticDTO.setDiscount((BigDecimal) result.get(21));
            receiptStatisticDTO.setReturnedDiscount((BigDecimal) result.get(22));
        }
        return receiptStatisticDTO;
    }

    private StringBuilder getBaseSqlForStatistics(ReceiptFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from receipt r ");
        if (StringUtils.isNotEmpty(filter.getPaymentType())) {
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
        if (StringUtils.isNotEmpty(filter.getPaymentType())) {
            sql.append(" and rp.payment_type ='").append(filter.getPaymentType()).append("'");
        }

        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(r.uid) like '").append(filter.getSearchValue()).append("'");
            sql.append(" or lower(r.origin_uid) like '").append(filter.getSearchValue()).append("'");
            sql.append(" or lower(c.tin) like '").append(filter.getSearchValue()).append("'");
            sql.append(")");
        }
        return sql;
    }
}
