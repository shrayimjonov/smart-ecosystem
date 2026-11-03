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
import uz.uzkassa.smartpos.dto.customerreceipts.CompanyReceiptsDTO;
import uz.uzkassa.smartpos.dto.customerreceipts.CompanyReceiptsStatDTO;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.CustomerReceiptsFilter;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.repository.clickhouse.ClickhouseUtils;
import uz.uzkassa.smartpos.repository.clickhouse.receipt.ClickhouseReceiptReportByCompany;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ClickhouseReceiptReportByCompanyImpl implements ClickhouseReceiptReportByCompany {
    private final JdbcClient jdbcClient;
    @Value("${spring.clickhouse.receipt}")
    private String RECEIPT;

    public ClickhouseReceiptReportByCompanyImpl(@Qualifier("clickhouseJdbcClient") JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Page<CompanyReceiptsDTO> getReceiptsAmountByCompany(CustomerReceiptsFilter filter) {
        StringBuilder query = new StringBuilder(" FROM ").append(this.RECEIPT).append(" WHERE 1=1 ");
        StringBuilder countQuery = new StringBuilder("SELECT count() FROM (SELECT 1 ");
        StringBuilder selectQuery = new StringBuilder("SELECT companyId AS id, " +
                "count() AS receipts, " +
                "sum(totalCost) AS revenue, " +
                "uniq(branchId) AS branchCount ");
        var params = new HashMap<String, Object>();
        setFiltersForStatistics(query, filter, params);
        query.append(" GROUP BY companyId ");
        countQuery.append(query).append(")");
        selectQuery.append(query);
        if (StringUtils.isNotBlank(filter.getOrderBy())) {
            SortTypeEnum sortOrder = SortTypeEnum.desc.equals(filter.getSortOrder()) ? SortTypeEnum.desc : SortTypeEnum.asc;
            if ("revenue".equalsIgnoreCase(filter.getOrderBy())) {
                selectQuery.append("ORDER BY revenue ");
                selectQuery.append(sortOrder.name());
            } else if ("branchCount".equalsIgnoreCase(filter.getOrderBy())) {
                selectQuery.append("ORDER BY branchCount ");
                selectQuery.append(sortOrder.name());
            } else if ("receipts".equalsIgnoreCase(filter.getOrderBy())) {
                selectQuery.append("ORDER BY receiptCount ");
                selectQuery.append(sortOrder.name());
            }
        }
        ClickhouseUtils.setLimits(selectQuery, filter.getPage(), filter.getSize());
        Long total = jdbcClient.sql(countQuery.toString()).params(params).query(Long.class).single();
        return new PageImpl<>(
                jdbcClient.sql(selectQuery.toString()).params(params).query(CompanyReceiptsDTO.class).list(),
                PageRequest.of(filter.getPage(), filter.getSize()),
                total
        );
    }

    @Override
    public CompanyReceiptsStatDTO getReceiptsAmountByCompanyStat(CustomerReceiptsFilter filter) {
        StringBuilder query = new StringBuilder("SELECT uniq(companyId) companyCount," +
                "uniq(branchId) branchCount," +
                "uniq(terminalId) terminalCount," +
                "sum(totalCost) revenueCount," +
                "count() receiptCount " +
                "FROM ").append(this.RECEIPT).append(" WHERE 1=1 ");
        var params = new HashMap<String, Object>();
        setFiltersForStatistics(query, filter, params);
        return jdbcClient.sql(query.toString()).params(params).query(CompanyReceiptsStatDTO.class).single();
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
            query.append(" and branchActivityTypeId = :activityTypeId ");
            params.put("activityTypeId", filter.getActivityTypeId());
        }
    }

    private void setNestedFilterForStatistics(StringBuilder query, CustomerReceiptsFilter filter, Map<String, Object> params) {
        if (filter.getRegionId() != null) {
            query.append(" and companyRegionId = :regionId");
            params.put("regionId", filter.getRegionId());
        }
        if (filter.getCityId() != null) {
            query.append(" and companyCityId = :cityId");
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
            query.append(" and (companyInn like :search or lowerUTF8(companyName) like :search)");
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
