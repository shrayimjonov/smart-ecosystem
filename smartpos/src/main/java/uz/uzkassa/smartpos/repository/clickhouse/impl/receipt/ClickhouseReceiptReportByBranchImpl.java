package uz.uzkassa.smartpos.repository.clickhouse.impl.receipt;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.dto.customerreceipts.BranchReceiptsDTO;
import uz.uzkassa.smartpos.dto.customerreceipts.BranchReceiptsStatDTO;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;
import uz.uzkassa.smartpos.dto.filter.ShiftFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.CustomerReceiptsFilter;
import uz.uzkassa.smartpos.dto.shift.ShiftReportStatDTO;
import uz.uzkassa.smartpos.enums.ReceiptStatus;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.filter.ShiftReportFilter;
import uz.uzkassa.smartpos.repository.clickhouse.ClickhouseUtils;
import uz.uzkassa.smartpos.repository.clickhouse.receipt.ClickhouseReceiptReportByBranch;
import uz.uzkassa.smartpos.security.SecurityUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ClickhouseReceiptReportByBranchImpl implements ClickhouseReceiptReportByBranch {
    private final JdbcClient jdbcClient;
    @Value("${spring.clickhouse.receipt}")
    private String RECEIPT;

    public ClickhouseReceiptReportByBranchImpl(@Qualifier("clickhouseJdbcClient") JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Page<BranchReceiptsDTO> getReceiptsAmountByBranch(CustomerReceiptsFilter filter) {
        StringBuilder countQuery = new StringBuilder("SELECT count() from (SELECT 1 ");
        StringBuilder selectQuery = new StringBuilder("SELECT branchId as id, sum(totalCost) AS revenue, count() AS receipts ");
        StringBuilder query = new StringBuilder(" FROM ").append(this.RECEIPT).append(" WHERE 1=1 ");
        var params = new HashMap<String, Object>();
        setFiltersForStatistics(query, filter, params);
        query.append(" GROUP BY branchId ");
        countQuery.append(query);
        countQuery.append(")");
        if (StringUtils.isNotBlank(filter.getOrderBy())) {
            SortTypeEnum sortOrder = SortTypeEnum.desc.equals(filter.getSortOrder()) ? SortTypeEnum.desc : SortTypeEnum.asc;
            if ("revenue".equalsIgnoreCase(filter.getOrderBy())) {
                query.append("ORDER BY revenue ");
                query.append(sortOrder.name());
            }
            if ("receipts".equalsIgnoreCase(filter.getOrderBy())) {
                query.append("ORDER BY receiptCount ");
                query.append(sortOrder.name());
            }
        }
        ClickhouseUtils.setLimits(query, filter.getPage(), filter.getSize());
        selectQuery.append(query);
        Long totalCount = jdbcClient.sql(countQuery.toString()).params(params).query(Long.class).single();
        return new PageImpl<>(jdbcClient.sql(selectQuery.toString()).params(params).query(BranchReceiptsDTO.class).list(), PageRequest.of(filter.getPage(), filter.getSize()), totalCount);
    }

    @Override
    public BranchReceiptsStatDTO getReceiptsAmountByBranchStat(CustomerReceiptsFilter filter) {
        StringBuilder query = new StringBuilder("SELECT uniq(companyId) AS companyCount, " +
                "uniq(branchId) AS branchCount, " +
                "count() AS receiptCount, " +
                "sum(totalCost) AS revenueCount FROM ").append(this.RECEIPT).append(" WHERE 1=1 ");
        var params = new HashMap<String, Object>();
        setFiltersForStatistics(query, filter, params);
        return jdbcClient.sql(query.toString()).params(params).query(BranchReceiptsStatDTO.class).single();
    }

    @Override
    public ShiftReportStatDTO getStatsForZReport(ShiftFilter filter) {
        StringBuilder sql = new StringBuilder("SELECT sumIf(payments['CASH'], status = 'PAID') totalCash, " +
                "sumIf(payments['CARD'], status = 'PAID') totalCard, " +
                "count(id) shifts FROM " + RECEIPT).append(" WHERE status = 'PAID' and branchId = :branchId and shiftNumber <= :shiftNumber");
        var params = new HashMap<String, Object>();
        params.put("branchId", filter.getBranchId());
        params.put("shiftNumber", filter.getShiftNumber());
        setDateTimeFilters(sql, filter, params);
        return jdbcClient.sql(sql.toString())
                .params(params).query(ShiftReportStatDTO.class)
                .single();
    }

    @Override
    public ShiftReportStatDTO getStats(ShiftFilter filter) {
        var params = new HashMap<String, Object>();
        String sql = "SELECT sumIf(payments['CASH'], status = 'PAID')                                           totalCash,\n" +
                "                       sumIf(payments['CASH'], status = 'RETURNED')                                       totalCashReturned,\n" +
                "                       sumIf(payments['CARD'], status = 'PAID')                                           totalCard,\n" +
                "                       sumIf(payments['CARD'], status = 'RETURNED')                                       totalCardReturned,\n" +
                "                       sumIf(if(totalNds > 0, totalNds, payments['NDS']), status = 'PAID')                nds,\n" +
                "                       sumIf(if(totalNds > 0, totalNds, payments['NDS']), status = 'RETURNED')            ndsReturned,\n" +
                "                       sumIf(if(totalDiscount > 0, totalDiscount, payments['DISCOUNT']), status = 'PAID') discount,\n" +
                "                       uniq(shiftNumber)                                                                  shifts,\n" +
                "                       countIf(id, status = 'PAID')                                                       receipts,\n" +
                "                       countIf(id, status = 'RETURNED')                                                   receiptsReturned\n" +
                "                FROM " + RECEIPT + " WHERE branchId = :branchId\n" +
                "                  and status in (:statuses)";
        params.put("branchId", filter.getBranchId());
        params.put("statuses", List.of(ReceiptStatus.PAID.name(), ReceiptStatus.RETURNED.name()));
        return jdbcClient.sql(sql).params(params).query(ShiftReportStatDTO.class)
                .single();
    }

    @Override
    public ShiftReportStatDTO getStats(ShiftReportFilter filter) {
        var params = new HashMap<String, Object>();
        StringBuilder sql = new StringBuilder("SELECT sumIf(payments['CASH'], status = 'PAID')                                           totalCash,\n" +
                "                       sumIf(payments['CASH'], status = 'RETURNED')                                       totalCashReturned,\n" +
                "                       sumIf(payments['CARD'], status = 'PAID')                                           totalCard,\n" +
                "                       sumIf(payments['CARD'], status = 'RETURNED')                                       totalCardReturned,\n" +
                "                       sumIf(if(totalNds > 0, totalNds, payments['NDS']), status = 'PAID')                nds,\n" +
                "                       sumIf(if(totalNds > 0, totalNds, payments['NDS']), status = 'RETURNED')            ndsReturned,\n" +
                "                       sumIf(if(totalDiscount > 0, totalDiscount, payments['DISCOUNT']), status = 'PAID') discount,\n" +
                "                       uniq(shiftNumber)                                                                  shifts,\n" +
                "                       countIf(id, status = 'PAID')                                                       receipts,\n" +
                "                       countIf(id, status = 'RETURNED')                                                   receiptsReturned\n" +
                "                FROM " + RECEIPT + " WHERE status in ('PAID', 'RETURNED')");
        if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
            sql.append(" and branchId in (:branchIds)");
        } else {
            sql.append(" and companyId = :companyId");
            params.put("companyId", SecurityUtils.getCurrentCompanyId());
        }
        params.put("branchIds", filter.getBranchIds());
        params.put("statuses", List.of(ReceiptStatus.PAID.name(), ReceiptStatus.RETURNED.name()));
        return jdbcClient.sql(sql.toString()).params(params).query(ShiftReportStatDTO.class)
                .single();
    }

    private void setFiltersForStatistics(StringBuilder query, CustomerReceiptsFilter filter, Map<String, Object> params) {
        setBaseFiltersForStatistics(query, filter, filter.getBranchIds(), params);
        setNestedFilterForStatistics(query, filter, params);
        if (filter.getIsFiscal() != null) {
            if (filter.getIsFiscal()) {
                query.append(" and isFiscal = 1 ");
            } else {
                query.append(" and isFiscal = 0 ");
            }
        }
        if (filter.getActivityTypeId() != null) {
            query.append(" and branchActivityTypeId = :activityTypeId");
            params.put("activityTypeId", filter.getActivityTypeId());
        }
    }

    private void setNestedFilterForStatistics(StringBuilder query, CustomerReceiptsFilter filter, Map<String, Object> params) {
        if (filter.getRegionId() != null) {
            query.append(" and branchRegionId = :regionId");
            params.put("regionId", filter.getRegionId());
        }
        if (filter.getCityId() != null) {
            query.append(" and branchCityId = :cityId");
            params.put("cityId", filter.getCityId());
        }

        if (filter.getPaymentTypes() != null) {
            filter.getPaymentTypes().forEach(
                    paymentType -> {
                        query.append(" and ");
                        query.append("payments['").append(paymentType.name()).append("'] > 0 ");
                    }
            );
        }
    }

    private void setBaseFiltersForStatistics(StringBuilder query, BaseFilter filter, Set<Long> branchIds, Map<String, Object> params) {
        setBranchCompanyDateTimeFilters(query, filter, branchIds, params);
        if (StringUtils.isNotBlank(filter.getSearch())) {
            query.append(" and (companyInn like :search or lowerUTF8(companyName) like :search or lowerUTF8(branchName) like :search)");
            params.put("search", "%" + filter.getSearch().toLowerCase() + "%");
        }
    }

    private void setBranchCompanyDateTimeFilters(StringBuilder query, BaseFilter filter, Set<Long> branchIds, Map<String, Object> params) {
        setDateTimeFilters(query, filter, params);
        if (CollectionUtils.isNotEmpty(branchIds)) {
            query.append(" and branchId in (:branchIds)");
            params.put("branchIds", branchIds);
        } else if (filter.getCompanyId() != null) {
            query.append(" and companyId = :companyId");
            params.put("companyId", filter.getCompanyId());
        }
    }

    private void setDateTimeFilters(StringBuilder query, BaseFilter filter, Map<String, Object> params) {
        if (filter.getFrom() != null) {
            query.append(" and receiptDateTime >= :from");
            params.put("from", filter.getFrom().withNano(0));
        }
        if (filter.getTo() != null) {
            query.append(" and receiptDateTime <= :to");
            params.put("to", filter.getTo().withNano(0));
        }
    }
}
