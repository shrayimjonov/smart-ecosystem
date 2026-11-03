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
import uz.uzkassa.smartpos.dto.analytics.*;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.ReceiptFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.SalesDynamicsFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.SalesStatsFilter;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.enums.TimeInterval;
import uz.uzkassa.smartpos.repository.clickhouse.ClickhouseUtils;
import uz.uzkassa.smartpos.repository.clickhouse.receipt.ClickhouseReceiptSalesStatsRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ClickhouseReceiptSalesStatsRepositoryImpl implements ClickhouseReceiptSalesStatsRepository {
    private final JdbcClient jdbcClient;
    @Value("${spring.clickhouse.receipt}")
    private String RECEIPT;

    public ClickhouseReceiptSalesStatsRepositoryImpl(@Qualifier("clickhouseJdbcClient") JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public EmployeeSalesStatDTO getEmployeeSalesStat(SalesStatsFilter filter) {
        StringBuilder query = new StringBuilder("SELECT sum(totalCost) totalRevenue," +
                                                "avgOrDefault(totalCost) averageCheckAmount," +
                                                "count() totalSales FROM ").append(this.RECEIPT).append(" WHERE 1=1 ");
        var params = new HashMap<String, Object>();
        setFiltersForEmployeeStatistics(query, filter, params);
        return jdbcClient.sql(query.toString()).params(params).query(EmployeeSalesStatDTO.class).single();
    }

    @Override
    public Page<UserSalesStatsDTO> getUserSalesStats(SalesStatsFilter filter, boolean forExcel) {
        StringBuilder countQuery = new StringBuilder("SELECT count() FROM (SELECT 1 ");
        StringBuilder query = new StringBuilder(" FROM ").append(this.RECEIPT).append(" WHERE 1=1 ");
        var params = new HashMap<String, Object>();
        setFiltersForEmployeeStatistics(query, filter, params);
        StringBuilder selectQuery = new StringBuilder("SELECT concat(any(createdByLastname), ' ', any(createdByFirstname)) AS name," +
                                                      "any(branchName) AS branchName," +
                                                      "sum(totalCost) AS salesTotal," +
                                                      "avg(totalCost) AS averageReceiptCost," +
                                                      "count() AS salesCount");
        query.append(" GROUP BY createdById, branchId ");
        selectQuery.append(query);
        countQuery.append(query).append(")");
        Long total = jdbcClient.sql(countQuery.toString()).params(params).query(Long.class).single();
        if (StringUtils.isNotBlank(filter.getOrderBy()) && !"id".equals(filter.getOrderBy())) {
            SortTypeEnum sortOrder = SortTypeEnum.desc.equals(filter.getSortOrder()) ? SortTypeEnum.desc : SortTypeEnum.asc;
            selectQuery.append(" ORDER BY ").append(filter.getOrderBy()).append(" ").append(sortOrder.name());
        }
        ClickhouseUtils.setLimits(selectQuery, filter.getPage(), filter.getSize());
        return new PageImpl<>(
                jdbcClient.sql(selectQuery.toString()).params(params).query(UserSalesStatsDTO.class).list(),
                PageRequest.of(filter.getPage(), filter.getSize()),
                total
        );
    }

    @Override
    public List<OverallSalesStatsDTO> getSalesDynamic(SalesDynamicsFilter filter) {
        StringBuilder query = new StringBuilder("SELECT ");
        String groupBy;
        String select = "sumIf(totalCost, status = 'PAID') AS salesTotal," +
                        "count() AS salesCount," +
                        "avgOrDefaultIf(totalCost, status = 'PAID') AS averageReceiptCost," +
                        "sumIf(greatest(totalCash, payments['CASH']), status = 'PAID') AS salesCash," +
                        "sumIf(greatest(totalCard, payments['CARD']), status = 'PAID') AS salesCard," +
                        "sumIf(greatest(totalDiscount, payments['DISCOUNT']), status = 'PAID') AS discount," +
                        "sumIf(greatest(totalNds, payments['NDS']), status = 'PAID') AS nds," +
                        "sumIf(totalExcise, status = 'PAID') AS excise," +
                        "sumIf(totalPaid, status = 'RETURNED') AS returned " +
                        "FROM " +
                        this.RECEIPT + " WHERE 1=1 ";
        switch (filter.getGranularity()) {
            case MONTH:
            case month:
                filter.setGranularity(TimeInterval.MONTH);
                query.append("toStartOfMonth(receiptDateTime) AS dateTime, ");
                groupBy = " GROUP BY toStartOfMonth(receiptDateTime)";
                break;
            case WEEK:
            case week:
                filter.setGranularity(TimeInterval.WEEK);
                query.append("toMonday(receiptDateTime) AS dateTime, ");
                groupBy = " GROUP BY toMonday(receiptDateTime)";
                break;
            case DAY:
            case day:
                filter.setGranularity(TimeInterval.DAY);
                query.append("toDate(receiptDateTime) AS dateTime, ");
                groupBy = " GROUP BY toDate(receiptDateTime)";
                break;
            default:
                filter.setGranularity(TimeInterval.HOUR);
                query.append("toStartOfHour(receiptDateTime) as dateTime, ");
                groupBy = " GROUP BY toStartOfHour(receiptDateTime)";
        }
        query.append(select);
        var params = new HashMap<String, Object>();
        setBranchCompanyDateTimeFilters(query, filter, filter.getBranchIdsWithBranchId(), params);
        query.append(groupBy);
        return jdbcClient.sql(query.toString()).params(params).query(OverallSalesStatsDTO.class).list();
    }

    @Override
    public OverallSalesStatsDTO getSalesStats(SalesStatsFilter filter) {
        StringBuilder query = new StringBuilder("""
                SELECT count()                                                               AS salesCount,
                       avgOrDefaultIf(totalCost, status = 'PAID')                            AS averageReceiptCost,
                       sumIf(greatest(totalCash, payments['CASH']), status = 'PAID')         AS salesCash,
                       sumIf(greatest(totalCard, payments['CARD']), status = 'PAID')         AS salesCard,
                       sumIf(greatest(totalDiscount, payments['DISCOUNT']), status = 'PAID') AS discount,
                       sumIf(greatest(totalNds, payments['NDS']), status = 'PAID')           AS nds,
                       sumIf(totalExcise, status = 'PAID')                                   AS excise,
                       sumIf(totalPaid, status = 'RETURNED')                                 AS returned,
                       sumIf(totalCost, status = 'PAID') - discount                          AS salesTotal
                FROM\s""").append(this.RECEIPT).append(" WHERE 1=1 ");
        var params = new HashMap<String, Object>();
        setBranchCompanyDateTimeFilters(query, filter, filter.getBranchIdsWithBranchId(), params);
        return jdbcClient.sql(query.toString()).params(params).query(OverallSalesStatsDTO.class).single();
    }


    @Override
    public ReceiptStatDto getReceiptStat(ReceiptFilter filter) {
        StringBuilder query = new StringBuilder("""
                SELECT uniq(shiftNumber)                                                                AS shifts,
                       countIf(id, status = 'PAID')                                                     AS count,
                       countIf(id, status = 'RETURNED')                                                 AS returnedCount,
                       sumIf(totalCost, status = 'PAID')                                                AS total,
                       sumIf(totalCost, status = 'RETURNED')                                            AS returnedTotal,
                       sumIf(greatest(totalCash, payments['CASH']), status = 'PAID')                    AS cash,
                       sumIf(greatest(totalCash, payments['CASH']), status = 'RETURNED')                AS returnedCash,
                       sumIf(greatest(totalDiscount, payments['DISCOUNT']), status = 'PAID')            AS discount,
                       sumIf(greatest(totalDiscount, payments['DISCOUNT']), status = 'RETURNED')        AS returnedDiscount,
                       sumIf(greatest(totalCard, payments['CARD']), status = 'PAID')                    AS card,
                       sumIf(greatest(totalCard, payments['CARD']), status = 'RETURNED')                AS returnedCard,
                       sumIf(payments['UZCARD'], status = 'PAID')                                       AS uzcard,
                       sumIf(payments['UZCARD'], status = 'RETURNED')                                   AS returnedUzcard,
                       sumIf(payments['HUMO'], status = 'PAID')                                         AS humo,
                       sumIf(payments['HUMO'], status = 'RETURNED')                                     AS returnedHumo,
                       sumIf(greatest(totalLoyaltyCard, payments['LOYALTY_CARD']), status = 'PAID')     AS loyalty,
                       sumIf(greatest(totalLoyaltyCard, payments['LOYALTY_CARD']), status = 'RETURNED') AS returnedLoyalty,
                       sumIf(payments['OTHER'], status = 'PAID')                                        AS other,
                       sumIf(payments['OTHER'], status = 'RETURNED')                                    AS returnedOther,
                       sumIf(greatest(totalNds, payments['NDS']), status = 'PAID')                      AS vat,
                       sumIf(greatest(totalNds, payments['NDS']), status = 'RETURNED')                  AS returnedVat,
                       sumIf(length(receiptDetailIds), status = 'PAID')                                 AS positions,
                       sumIf(length(receiptDetailIds), status = 'RETURNED')                             AS returnedPositions
                FROM\s""").append(this.RECEIPT).append(" WHERE 1=1 ");
        var params = new HashMap<String, Object>();
        setFiltersTwo(filter, query, params);
        return jdbcClient.sql(query.toString()).params(params).query(ReceiptStatDto.class).single();
    }

    @Override
    public ReceiptStatisticByStatusesDTO getReceiptStatisticByStatuses(ReceiptFilter filter) {
        StringBuilder query = new StringBuilder("""
                SELECT sum(totalCash) FILTER (WHERE status = 'PAID') AS paidCash,
                sumIf(totalCard, status = 'PAID') AS paidCard,
                sumIf(totalCash, status = 'RETURNED') AS returnedCash,
                sumIf(totalCard, status = 'RETURNED') AS returnedCard,\s
                sumIf(totalCost, status = 'INCOME') AS income,
                sumIf(totalCost, status = 'RETURN_FLOW') AS returnFlow,
                sumIf(totalCost, status = 'WITHDRAW') AS withdraw,
                sumIf(totalCost, status = 'FLOW') AS flow,
                sumIf(totalCost, status = 'INCASSATION') AS incassation
                FROM\s""").append(this.RECEIPT).append(" WHERE 1=1");
        var params = new HashMap<String, Object>();
        setBranchCompanyDateTimeFilters(query, filter, filter.getBranchIdsWithBranchId(), params);
        return jdbcClient.sql(query.toString()).params(params).query(ReceiptStatisticByStatusesDTO.class).single();
    }

    private void setFiltersForEmployeeStatistics(StringBuilder query, SalesStatsFilter filter, Map<String, Object> params) {
        setBaseFiltersForStatistics(query, filter, filter.getBranchIdsWithBranchId(), params);
        if (filter.getUserId() != null) {
            query.append(" and createdById = :userId");
            params.put("userId", filter.getUserId());
        }
    }

    private void setBaseFiltersForStatistics(StringBuilder query, BaseFilter filter, Set<Long> branchIds, Map<String, Object> params) {
        setBranchCompanyDateTimeFilters(query, filter, branchIds, params);
        if (StringUtils.isNotBlank(filter.getSearch())) {
            query.append(" and (companyInn like :search or lowerUTF8(companyName) like :search)");
            params.put("search", "%" + filter.getSearch().toLowerCase() + "%");
        }
    }


    private void setFiltersTwo(ReceiptFilter filter, StringBuilder query, Map<String, Object> params) {
        setFilters(query, filter, params);
        if (CollectionUtils.isEmpty(filter.getBranchIdsWithBranchId()) && filter.getCompanyId() != null) {
            query.append(" and companyId = :companyId");
            params.put("companyId", filter.getCompanyId());
        }
        if (filter.getPaymentTypes() != null) {
            filter.getPaymentTypes().forEach(
                    paymentType -> {
                        query.append(" and ");
                        query.append("payments['").append(paymentType.name()).append("'] > 0 ");
                    }
            );
        }
        if (filter.getIsFiscal() != null) {
            if (filter.getIsFiscal()) {
                query.append(" and isFiscal = 1");
            } else {
                query.append(" and isFiscal = 0");
            }
        }
        if (filter.getSource() != null) {
            query.append(" and source = :source");
            params.put("source", filter.getSource().name());
        }
        if (filter.getPosition() != null) {
            query.append(" and length(receiptDetailIds) = :position");
            params.put("position", filter.getPosition());
        }

        if (filter.getActivityTypeId() != null) {
            query.append(" and branchActivityTypeId = :activityTypeId");
            params.put("activityTypeId", filter.getActivityTypeId());
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

    private void setFilters(StringBuilder query, ReceiptFilter filter, Map<String, Object> params) {
        setDateTimeFilters(query, filter, params);
        if (filter.getStatus() != null) {
            query.append(" and status = :status");
            params.put("status", filter.getStatus().name());
        }
        if (filter.getShiftNumber() != null) {
            query.append(" and shiftNumber = :shiftNumber");
            params.put("shiftNumber", filter.getShiftNumber());
        }
        if (CollectionUtils.isNotEmpty(filter.getStatuses())) {
            query.append(" and status in (:statuses)");
            params.put("statuses", filter.getStatuses().stream().map(Enum::name).toList());
        }
        if (CollectionUtils.isNotEmpty(filter.getBranchIdsWithBranchId())) {
            query.append(" and branchId in (:branchIds)");
            params.put("branchIds", filter.getBranchIdsWithBranchId());
        }
        if (filter.getCustomerId() != null) {
            query.append(" and customerId = :customerId");
            params.put("customerId", filter.getCustomerId());
        }
        if (filter.getUserId() != null) {
            query.append(" and createdById = :userId");
            params.put("userId", filter.getUserId());
        }
        if (filter.getTerminalId() != null) {
            query.append(" and terminalId = :terminalId");
            params.put("terminalId", filter.getTerminalId());
        }
        if (filter.getTotalCostFrom() != null) {
            query.append(" and totalCost >= :totalCostFrom");
            params.put("totalCostFrom", filter.getTotalCostFrom());
        }
        if (filter.getTotalCostTo() != null) {
            query.append(" and totalCost <= :totalCostTo");
            params.put("totalCostTo", filter.getTotalCostTo());
        }
        if (CollectionUtils.isNotEmpty(filter.getUidList())) {
            query.append(" and uid in (:uids)");
            params.put("uids", filter.getUidList());
        }
        if (StringUtils.isNotBlank(filter.getSearch())) {
            query.append(" and (uid like :search or originUid like :search or fiscalUrl like :search or lowerUTF8(terminalSerialNumber) like :search)");
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
}
