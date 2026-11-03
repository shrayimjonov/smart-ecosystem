package uz.uzkassa.smartpos.repository.clickhouse.sales.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.dto.SalesDynamicsDto;
import uz.uzkassa.smartpos.dto.analytics.ReceiptStatDto;
import uz.uzkassa.smartpos.dto.clickhouse.SalesClickhouseInfoDto;
import uz.uzkassa.smartpos.dto.receipt.CreditPaymentDTO;
import uz.uzkassa.smartpos.dto.sales.SalesReportStatsDto;
import uz.uzkassa.smartpos.dto.sales.SalesTopBranchDto;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.filter.SalesFilter;
import uz.uzkassa.smartpos.repository.clickhouse.ClickhouseUtils;
import uz.uzkassa.smartpos.repository.clickhouse.sales.SalesClickhouseRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class SalesClickhouseRepositoryImpl implements SalesClickhouseRepository {
    private final JdbcClient jdbcClient;
    private final ObjectMapper objectMapper;

    public SalesClickhouseRepositoryImpl(@Qualifier("clickhouseJdbcClient") JdbcClient jdbcClient, ObjectMapper objectMapper) {
        this.jdbcClient = jdbcClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public ReceiptStatDto statistics(SalesFilter filter) {
        StringBuilder sql = new StringBuilder("""
                SELECT uniq(shiftNumber)                                  AS shifts,
                       countIf(id, type = 'SALE')                         AS count,
                       countIf(id, type = 'RETURNED')                     AS returnedCount,
                       sumIf(totalCost, type = 'SALE')                    AS total,
                       sumIf(totalCost, type = 'RETURNED')                AS returnedTotal,
                       sumIf(payments['CASH'], type = 'SALE')             AS cash,
                       sumIf(payments['CASH'], type = 'RETURNED')         AS returnedCash,
                       sumIf(payments['DISCOUNT'], type = 'SALE')         AS discount,
                       sumIf(payments['DISCOUNT'], type = 'RETURNED')     AS returnedDiscount,
                       sumIf(payments['CARD'], type = 'SALE')             AS card,
                       sumIf(payments['CARD'], type = 'RETURNED')         AS returnedCard,
                       sumIf(payments['UZCARD'], type = 'SALE')           AS uzcard,
                       sumIf(payments['UZCARD'], type = 'RETURNED')       AS returnedUzcard,
                       sumIf(payments['HUMO'], type = 'SALE')             AS humo,
                       sumIf(payments['HUMO'], type = 'RETURNED')         AS returnedHumo,
                       sumIf(payments['LOYALTY_CARD'], type = 'SALE')     AS loyalty,
                       sumIf(payments['LOYALTY_CARD'], type = 'RETURNED') AS returnedLoyalty,
                       sumIf(payments['OTHER'], type = 'SALE')            AS other,
                       sumIf(payments['OTHER'], type = 'RETURNED')        AS returnedOther,
                       sumIf(payments['NDS'], type = 'SALE')              AS vat,
                       sumIf(payments['NDS'], type = 'RETURNED')          AS returnedVat,
                       sumIf(length(salesDetailIds), type = 'SALE')       AS positions,
                       sumIf(length(salesDetailIds), type = 'RETURNED')   AS returnedPositions
                FROM sales
                where 1 = 1\s""");
        var params = new HashMap<String, Object>();
        setParams(filter, sql, params);
        return jdbcClient.sql(sql.toString()).params(params).query(ReceiptStatDto.class).optional().orElse(null);
    }

    @Override
    public Page<SalesClickhouseInfoDto> getSalesList(SalesFilter filter) {
        StringBuilder selectSql = new StringBuilder("select info ");
        StringBuilder countSql = new StringBuilder("select count() ");
        StringBuilder sql = new StringBuilder(" from sales where 1=1 ");

        var params = new HashMap<String, Object>();
        setParams(filter, sql, params);
        selectSql.append(sql);
        countSql.append(sql);
        if (StringUtils.isNotBlank(filter.getOrderBy())) {
            selectSql.append(" order by ").append(filter.getOrderBy());
            if (filter.getSortOrder() == SortTypeEnum.desc) {
                selectSql.append(" desc ");
            } else {
                selectSql.append(" asc ");
            }
        } else {
            selectSql.append(" order by createdDateTime desc ");
        }
        ClickhouseUtils.setLimits(selectSql, filter.getPage(), filter.getSize());
        List<SalesClickhouseInfoDto> infos = jdbcClient.sql(selectSql.toString()).params(params).query(String.class).list().stream().map(info -> {
            try {
                return objectMapper.readValue(info, SalesClickhouseInfoDto.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();
        return new PageImpl<>(infos, filter.getPageable(), jdbcClient.sql(countSql.toString()).params(params).query(Long.class).optional().orElse(0L));
    }

    @Override
    public Optional<SalesClickhouseInfoDto> findById(Long id) {
        return jdbcClient.sql("select info from sales where id = :id").params(Map.of("id", id)).query(String.class).optional().map(info -> {
            try {
                return objectMapper.readValue(info, SalesClickhouseInfoDto.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }


    @Override
    public Optional<SalesClickhouseInfoDto> findFirstOriginByUid(String originUid) {
        return jdbcClient.sql("select info from sales where originUid = :originUid order by id limit 1").params(Map.of("originUid", originUid)).query(String.class).optional().map(info -> {
            try {
                return objectMapper.readValue(info, SalesClickhouseInfoDto.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<SalesTopBranchDto> topBranches(SalesFilter filter) {
        StringBuilder selectSql = new StringBuilder("""
                select branchId                                                             as id,
                       any(branchName)                                                      as name,
                       count()                                                              as count,
                       sum(length(salesDetailIds))                                          as positions,
                       sum(costAmount)                                                      as totalCostAmount,
                       sum(totalCost)                                                       as totalSum
                from sales
                where 1 = 1""");
        var params = new HashMap<String, Object>();
        setParams(filter, selectSql, params);
        selectSql.append(" group by branchId ");
        if (StringUtils.isNotBlank(filter.getOrderBy())) {
            selectSql.append(" order by ").append(filter.getOrderBy());
            if (filter.getSortOrder() == SortTypeEnum.desc) {
                selectSql.append(" desc ");
            } else {
                selectSql.append(" asc ");
            }
        } else {
            selectSql.append(" order by totalSum desc ");
        }
        ClickhouseUtils.setLimits(selectSql, filter.getPage(), filter.getSize());
        List<SalesTopBranchDto> infos = jdbcClient.sql(selectSql.toString()).params(params).query(SalesTopBranchDto.class).list();
        return infos;
    }

    @Override
    public List<SalesDynamicsDto> salesDynamics(SalesFilter filter) {
        String groupBy = switch (filter.getTimeInterval()) {
            case HOUR, hour -> "toStartOfHour(createdDateTime) as date,";
            case DAY, day -> "toStartOfDay(createdDateTime) as date,";
            case WEEK, week -> "toStartOfWeek(createdDateTime) as date,";
            case MONTH, month -> "toStartOfMonth(createdDateTime) as date,";
        };
        String totalCost = "sumIf(totalCost, type = 'SALE') - sumIf(totalCost, type = 'RETURNED') as totalSales";
        if (filter.getType() != null) {
            totalCost = "sumIf(totalCost, type = '" + filter.getType().name() + "') as totalSales";
        }
        StringBuilder sql = new StringBuilder("select ").append(groupBy).append(totalCost).append(" from sales where 1 = 1");
        var params = new HashMap<String, Object>();
        setParams(filter, sql, params);
        sql.append(" group by 1 order by 1");
        return jdbcClient.sql(sql.toString()).params(params).query(SalesDynamicsDto.class).list();
    }

    @Override
    public SalesReportStatsDto getSalesReportStats(SalesFilter filter) {
        StringBuilder sql = new StringBuilder("""
                select sum(totalCost)                                        as totalSum,
                       sum(costAmount)                                       as totalCostAmount,
                       if(isNaN(avg(totalCost)), null, avg(totalCost)) as avgReceipt
                from sales
                where 1 = 1\s""");
        var params = new HashMap<String, Object>();
        setParams(filter, sql, params);
        return jdbcClient.sql(sql.toString()).params(params).query(SalesReportStatsDto.class).optional().orElse(null);
    }

    @Override
    public List<CreditPaymentDTO> findAllCreditPayments(String originUid) {
        return jdbcClient.sql("select info from sales where originUid = :originUid and type in ('CREDIT', 'ADVANCE')").params(Map.of("originUid", originUid)).query(String.class).list().stream().map(info -> {
            try {
                SalesClickhouseInfoDto infoDto = objectMapper.readValue(info, SalesClickhouseInfoDto.class);
                return new CreditPaymentDTO(infoDto.getTotalPaid(),
                        infoDto.getPayments().get(PaymentType.NDS),
                        infoDto.getPayments().get(PaymentType.DISCOUNT),
                        infoDto.getType().getReceiptStatus(),
                        infoDto.getCreatedDateTime(),
                        infoDto.getPayments());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    private void setParams(SalesFilter filter, StringBuilder sql, HashMap<String, Object> params) {
        if (filter.getCompanyId() != null) {
            sql.append(" and companyId = :companyId");
            params.put("companyId", filter.getCompanyId());
        }
        if (filter.getBranchId() != null) {
            sql.append(" and branchId = :branchId");
            params.put("branchId", filter.getBranchId());
        }
        if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
            sql.append(" and branchId in (:branchIds)");
            params.put("branchIds", filter.getBranchIds());
        }
        if (filter.getFrom() != null) {
            sql.append(" and createdDateTime >= :from");
            params.put("from", filter.getFrom().withNano(0));
        }
        if (filter.getTo() != null) {
            sql.append(" and createdDateTime <= :to");
            params.put("to", filter.getTo().withNano(0));
        }
        if (filter.getUserId() != null) {
            sql.append(" and createdById = :userId");
            params.put("userId", filter.getUserId());
        }
        if (filter.getSource() != null) {
            sql.append(" and source = :source");
            params.put("source", filter.getSource().name());
        }
        if (filter.getType() != null) {
            sql.append(" and type = :type");
            params.put("type", filter.getType().name());
        } else if (CollectionUtils.isNotEmpty(filter.getTypes())) {
            sql.append(" and type in (:types)");
            params.put("types", filter.getTypes().stream().map(Enum::name).toList());
        }
        if (filter.getStatus() != null) {
            sql.append(" and status = :status");
            params.put("status", filter.getStatus().name());
        }
        if (filter.getShiftNumber() != null) {
            sql.append(" and shiftNumber = :shiftNumber");
            params.put("shiftNumber", filter.getShiftNumber());
        }
        if (filter.getPaymentType() != null) {
            sql.append(" and mapContains(payments, :paymentType)");
            params.put("paymentType", filter.getPaymentType().name());
        }
        if (filter.getTotalCostFrom() != null) {
            sql.append(" and totalCost >= :totalCostFrom");
            params.put("totalCostFrom", filter.getTotalCostFrom());
        }
        if (filter.getTotalCostTo() != null) {
            sql.append(" and totalCost <= :totalCostTo");
            params.put("totalCostTo", filter.getTotalCostTo());
        }
    }
}
