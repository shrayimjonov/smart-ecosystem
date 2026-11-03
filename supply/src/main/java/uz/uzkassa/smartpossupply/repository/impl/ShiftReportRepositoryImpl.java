package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpossupply.domain.shift.ShiftReport;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.shift.RegistrationReportDTO;
import uz.uzkassa.smartpossupply.dto.shift.ShiftReportStatisticDTO;
import uz.uzkassa.smartpossupply.dto.shift.XReportDTO;
import uz.uzkassa.smartpossupply.dto.shift.ZReportStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.ReceiptStatus;
import uz.uzkassa.smartpossupply.filters.ShiftReportFilter;
import uz.uzkassa.smartpossupply.repository.ShiftReportRepositoryCustom;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 28.12.2022 13:35
 */
public class ShiftReportRepositoryImpl extends BaseRepositoryImpl implements ShiftReportRepositoryCustom {

    @Override
    public XReportDTO xReport(Long branchId, Long shiftId) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select ");
        sql.append(" count( distinct r.id) filter( where r.status='PAID') ");
        sql.append(" as receiptCount, ");

        sql.append(" count( distinct r.id) filter( where r.status='RETURNED') ");
        sql.append(" as returnedReceiptCount, ");

        sql.append(" sum(rp.amount) filter( where r.status='PAID' and rp.payments_key='CARD') ");
        sql.append(" as totalCard, ");

        sql.append(" sum(rp.amount) filter (where r.status='RETURNED' and rp.payments_key='CARD') ");
        sql.append(" as totalReturnedCard, ");

        sql.append(" sum(rp.amount) filter (where r.status='PAID' and rp.payments_key='VAT') ");
        sql.append(" as totalVat, ");

        sql.append(" sum(rp.amount) filter (where r.status='RETURNED' and rp.payments_key='VAT') ");
        sql.append(" as totalReturnedVat, ");

        sql.append(" sum(rp.amount) filter (where r.status='PAID' and rp.payments_key='CASH') ");
        sql.append(" as totalCash, ");

        sql.append(" sum(rp.amount) filter (where r.status='RETURNED' and rp.payments_key='CASH') ");
        sql.append(" as totalReturnedCash ");

        sql.append(" from receipt r ");
        sql.append(" inner join receipt_payments rp on r.id = rp.receipt_id ");
        sql.append(" where r.shift_id =:shiftId ");
        sql.append(" and r.branch_id =:branchId ");
        sql.append(" and r.status in ('PAID','RETURNED')");
        Query query = entityManager.createNativeQuery(sql.toString(), "X-ReportMapper");
        query.setParameter("shiftId", shiftId);
        query.setParameter("branchId", branchId);
        return (XReportDTO) query.getSingleResult();
    }

    @Override
    public ZReportStatisticsDTO getZReportStatistics(Long branchId, Long shiftNumber) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select ");

        sql.append(" sum(rp.amount) filter( where r.status='PAID' and rp.payments_key='CARD') ");
        sql.append(" as totalCard, ");

        sql.append(" sum(rp.amount) filter (where r.status='PAID' and rp.payments_key='CASH') ");
        sql.append(" as totalCash, ");

        sql.append(" from receipt r ");
        sql.append(" inner join receipt_payments rp on r.id = rp.receipt_id ");
        sql.append(" inner join shift sh on sh.id = r.shift_id ");
        sql.append(" where sh.shift_number <=").append(shiftNumber);
        sql.append(" and r.branch_id =:branchId");
        sql.append(" and r.status =:status");
        Query query = entityManager.createNativeQuery(sql.toString(), "Z-ReportStatisticMapper");
        query.setParameter("branchId", branchId);
        query.setParameter("status", ReceiptStatus.PAID);
        return (ZReportStatisticsDTO) query.getSingleResult();
    }

    @Override
    public ResultList<ShiftReport> zReport(ShiftReportFilter filter) {
        ResultList<ShiftReport> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" select r from ShiftReport r ");
        sql.append(" where r.branchId =:branchId ");

        if (filter.getFrom() != null) {
            sql.append(" and r.endDate >=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and r.endDate <=:toDate ");
        }
        if (filter.getCashierId() != null) {
            sql.append(" and r.cashierId =:cashierId");
        }

        String countSql = sql.toString().replaceFirst("select r", "select count(r.id) ");
        sql.append(" order by r.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<ShiftReport> query = entityManager
            .createQuery(sql.toString(), ShiftReport.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);
        query.setParameter("branchId", filter.getBranchId());
        countQuery.setParameter("branchId", filter.getBranchId());

        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }
        if (filter.getCashierId() != null) {
            query.setParameter("cashierId", filter.getCashierId());
            countQuery.setParameter("cashierId", filter.getCashierId());
        }
        Long count = countQuery.getSingleResult();
        if (count >= 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    public ShiftReportStatisticDTO getStatistics(ShiftReportFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select ");
        sql.append(" sum(r.total_cost) as totalCost,");
        sql.append(" sum(r.total_cash) as totalCash,");
        sql.append(" sum(r.total_card) as totalCard,");
        sql.append(" sum(r.total_loyalty_card) as totalLoyaltyCard,");
        sql.append(" sum(r.total_discount) as totalDiscount,");
        sql.append(" sum(r.total_excise) as totalExcise,");
        sql.append(" sum(r.total_paid) as totalPaid,");
        sql.append(" sum(r.total_vat) as totalVat,");
        sql.append(" count (r.id) as receiptCount,");
        sql.append(" count (r.id) filter(where r.status='DRAFT') as draftCount,");
        sql.append(" count (r.id) filter(where r.status='RETURNED') as receiptReturnedCount,");
        sql.append(" sum(r.total_cash) filter(where r.status='RETURNED') as totalCashReturned,");
        sql.append(" sum(r.total_card) filter(where r.status='RETURNED') as totalCardReturned,");
        sql.append(" sum(r.total_vat) filter(where r.status='RETURNED') as totalVatReturned,");
        sql.append(" count (r.shift_id) as shiftCount");
        sql.append(" from receipt r");
        sql.append(" where r.deleted = false ");
        sql.append(" and r.branch_id=:branchId ");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and r.status=:status ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and r.created_date>=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and r.created_date<=:toDate ");
        }
        if (filter.isNotEmpty()) {
            sql.append(" and ( ");
            sql.append(" lower(r.uid) ").append(filter.getSearchKey());
            sql.append(") ");
        }

        Query query = entityManager.createNativeQuery(sql.toString(), "ShiftReportStatisticsMapper");
        query.setParameter("branchId", filter.getBranchId());

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", ReceiptStatus.valueOf(filter.getStatus()));
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
        }
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }

        return (ShiftReportStatisticDTO) query.getSingleResult();
    }

    @Override
    public RegistrationReportDTO getRegistrationReport(Long branchId) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select ");
        sql.append(" sum(sr.receipt_count) as receiptCount, ");
        sql.append(" count(sr.shift_id) as shiftCount, ");
        sql.append(" sum(sr.total_cash) as totalCash, ");
        sql.append(" sum(sr.total_card) as totalCard, ");
        sql.append(" sum(sr.total_vat) as totalVat ");
        sql.append(" from shift_report sr ");
        sql.append(" where sr.branch_id=:branchId ");
        Query query = entityManager.createNativeQuery(sql.toString(), "RegistrationReportMapper");
        query.setParameter("branchId", branchId);
        return (RegistrationReportDTO) query.getSingleResult();
    }
}
