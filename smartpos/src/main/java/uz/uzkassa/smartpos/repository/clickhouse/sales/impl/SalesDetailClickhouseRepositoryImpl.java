package uz.uzkassa.smartpos.repository.clickhouse.sales.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.dto.clickhouse.SalesDetailClickhouseInfoDto;
import uz.uzkassa.smartpos.dto.sales.SalesDetailDynamicsDto;
import uz.uzkassa.smartpos.dto.sales.SalesTopProductDto;
import uz.uzkassa.smartpos.filter.SalesFilter;
import uz.uzkassa.smartpos.repository.clickhouse.sales.SalesDetailClickhouseRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SalesDetailClickhouseRepositoryImpl implements SalesDetailClickhouseRepository {
    private final JdbcClient jdbcClient;
    private final ObjectMapper objectMapper;

    public SalesDetailClickhouseRepositoryImpl(@Qualifier("clickhouseJdbcClient") JdbcClient jdbcClient, ObjectMapper objectMapper) {
        this.jdbcClient = jdbcClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<SalesDetailClickhouseInfoDto> findAllBySalesId(Collection<Long> ids) {
        return jdbcClient.sql("select info from sales_detail where salesId in (:salesIds)")
                .params(Map.of("salesIds", ids))
                .query(String.class)
                .list()
                .stream().map(info -> {
                    try {
                        return objectMapper.readValue(info, SalesDetailClickhouseInfoDto.class);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
    }

    @Override
    public List<SalesTopProductDto> topProducts(SalesFilter filter) {
        StringBuilder sql = new StringBuilder("""
                select productId,
                       any(JSONExtractString(info, 'productName')) as name,
                       sum(amount)                                       as totalAmount,
                       sum(costAmount)                                   as totalCostAmount,
                       sum(qty)                                          as totalQty
                from sales_detail
                where 1 = 1""");
        var params = new HashMap<String, Object>();
        setParams(filter, sql, params);
        sql.append(" group by productId");
        sql.append(" order by totalAmount desc limit ").append(filter.getSize());
        return jdbcClient.sql(sql.toString())
                .params(params)
                .query(SalesTopProductDto.class)
                .list();
    }

    @Override
    public SalesDetailDynamicsDto salesDetailDynamics(SalesFilter filter) {
        StringBuilder sql = new StringBuilder("""
                select avgIf(discountPercent, discountPercent > 0) as averageDiscountPercent,
                       if(isNaN(avg(qty)), null, avg(qty))         as averageQty,
                       sumIf(qty, type = 'SALE')                   as count,
                       sumIf(qty, type = 'RETURNED')               as returnedCount
                from sales_detail
                where 1 = 1""");
        var params = new HashMap<String, Object>();
        setParams(filter, sql, params);
        return jdbcClient.sql(sql.toString())
                .params(params)
                .query(SalesDetailDynamicsDto.class)
                .optional().orElse(null);
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
        if (filter.getType() != null) {
            sql.append(" and type = :type");
            params.put("type", filter.getType().name());
        }
        if (filter.getStatus() != null) {
            sql.append(" and status = :status");
            params.put("status", filter.getStatus().name());
        }
    }
}
