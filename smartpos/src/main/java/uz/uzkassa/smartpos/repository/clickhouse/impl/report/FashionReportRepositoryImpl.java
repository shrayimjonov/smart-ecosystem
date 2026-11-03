package uz.uzkassa.smartpos.repository.clickhouse.impl.report;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.dto.fashion.FashionReportByCategoryStatisticsDto;
import uz.uzkassa.smartpos.dto.fashion.FashionReportByOneProductStatisticsDto;
import uz.uzkassa.smartpos.dto.fashion.FashionReportByProductStatisticsDto;
import uz.uzkassa.smartpos.dto.fashion.report.*;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.filter.FashionReportFilter;
import uz.uzkassa.smartpos.repository.clickhouse.report.FashionReportRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FashionReportRepositoryImpl implements FashionReportRepository {
    private final JdbcClient jdbcClient;

    public FashionReportRepositoryImpl(@Qualifier("clickhouseJdbcClient") JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Page<FashionReportByProductDto> reportByProduct(FashionReportFilter filter) {
        StringBuilder selectSql = new StringBuilder("""
                select fr.productId                              as productId,
                       any(fr.productName)                         as productName,
                       any(fr.categoryName)                         as categoryName,
                       avg(fr.salesPrice)                        as avgSalesPrice,
                       sum(fr.qty)                               as salesCount,
                       sum(fr.totalDiscount)                     as totalDiscount,
                       sum(fr.totalVat)                          as totalVat,
                       sum(fr.totalSumma)                        as totalSumma,
                       uniqIf(receiptId, fr.status = 'PAID')     as salesReceiptsCount,
                       uniqIf(receiptId, fr.status = 'RETURNED') as returnedReceiptsCount""");
        StringBuilder countSql = new StringBuilder("select uniq(fr.productId) as count");
        var params = new HashMap<String, Object>();
        StringBuilder sql = new StringBuilder(" from fashion_report fr where 1=1 ").append(whereSql(filter, params));
        countSql.append(sql);
        selectSql.append(sql);
        selectSql.append(" group by fr.productId ");
        if (StringUtils.isNotBlank(filter.getOrderBy())) {
            selectSql.append(" order by ").append(filter.getOrderBy());
            if (SortTypeEnum.desc.equals(filter.getSortOrder())) {
                selectSql.append(" desc");
            } else {
                selectSql.append(" asc");
            }
        }
        selectSql.append(" limit ").append(filter.getSize()).append(" offset ").append(filter.getOffset());
        return new PageImpl<>(jdbcClient.sql(selectSql.toString()).params(params).query(FashionReportByProductDto.class).list(),
                filter.getPageable(),
                jdbcClient.sql(countSql.toString()).params(params).query(Long.class).optional().orElse(0L));
    }

    @Override
    public List<FashionReportByColorGroupDto> reportByColorGroup(FashionReportFilter filter) {
        StringBuilder selectSql = new StringBuilder("""
                select fr.colorGroupId                           as colorGroupId,
                       any(fr.colorCode)                         as colorCode,
                       any(fr.colorName)                         as colorName,
                       any(fr.printName)                         as printName,
                       avg(fr.salesPrice)                        as avgSalesPrice,
                       sum(fr.qty)                               as salesCount,
                       sum(fr.totalDiscount)                     as totalDiscount,
                       sum(fr.totalVat)                          as totalVat,
                       sum(fr.totalSumma)                        as totalSumma,
                       uniqIf(receiptId, fr.status = 'PAID')     as salesReceiptsCount,
                       uniqIf(receiptId, fr.status = 'RETURNED') as returnedReceiptsCount
                from fashion_report fr\s""");
        var params = new HashMap<String, Object>();
        selectSql.append(" where 1=1 ").append(whereSql(filter, params));
        selectSql.append(" group by fr.colorGroupId ");
        if (StringUtils.isNotBlank(filter.getOrderBy())) {
            selectSql.append(" order by ").append(filter.getOrderBy());
            if (SortTypeEnum.desc.equals(filter.getSortOrder())) {
                selectSql.append(" desc");
            } else {
                selectSql.append(" asc");
            }
        }
        return jdbcClient.sql(selectSql.toString()).params(params).query(FashionReportByColorGroupDto.class).list();
    }


    @Override
    public List<FashionReportByOneProductDto> reportByOneProduct(FashionReportFilter filter) {
        StringBuilder selectSql = new StringBuilder("""
                select any(fr.colorGroupId)                      as colorGroupId,
                       any(fr.colorCode)                         as colorCode,
                       any(fr.colorName)                         as colorName,
                       any(fr.printName)                         as printName,
                       fr.itemId                                 as itemId,
                       any(fr.sizeCode)                          as sizeCode,
                       any(fr.sizeValue)                         as sizeValue,
                       avg(fr.salesPrice)                        as avgSalesPrice,
                       sum(fr.qty)                               as salesCount,
                       sum(fr.totalDiscount)                     as totalDiscount,
                       sum(fr.totalVat)                          as totalVat,
                       sum(fr.totalSumma)                        as totalSumma,
                       uniqIf(receiptId, fr.status = 'PAID')     as salesReceiptsCount,
                       uniqIf(receiptId, fr.status = 'RETURNED') as returnedReceiptsCount,
                       fr.branchId                               as branchId,
                       any(fr.branchName)                        as branchName
                from fashion_report fr\s""");
        var params = new HashMap<String, Object>();
        selectSql.append(" where 1=1 ").append(whereSql(filter, params));
        selectSql.append(" group by fr.itemId, fr.branchId ");
        if (StringUtils.isNotBlank(filter.getOrderBy())) {
            selectSql.append(" order by ").append(filter.getOrderBy());
            if (SortTypeEnum.desc.equals(filter.getSortOrder())) {
                selectSql.append(" desc");
            } else {
                selectSql.append(" asc");
            }
        }
        return jdbcClient.sql(selectSql.toString()).params(params).query(FashionReportByOneProductDto.class).list();
    }

    @Override
    public List<FashionReportByCategoryDto> reportByCategory(FashionReportFilter filter) {
        var params = new HashMap<String, Object>();
        String selectSql = "select fr.categoryId                             as categoryId,\n" +
                "       sum(fr.qty)                               as salesCount,\n" +
                "       sum(fr.totalDiscount)                     as totalDiscount,\n" +
                "       sum(fr.totalVat)                          as totalVat,\n" +
                "       sum(fr.totalSumma)                        as totalSumma,\n" +
                "       uniqIf(receiptId, fr.status = 'PAID')     as salesReceiptsCount,\n" +
                "       uniqIf(receiptId, fr.status = 'RETURNED') as returnedReceiptsCount\n" +
                "from fashion_report fr where 1=1 " + whereSql(filter, params)
                + " group by fr.categoryId ";
        return jdbcClient.sql(selectSql).params(params).query(FashionReportByCategoryDto.class).list();
    }

    @Override
    public Page<FashionReportByUserDto> reportByUser(FashionReportFilter filter) {
        StringBuilder selectSql = new StringBuilder("""
                select fr.userId            as userId,
                       any(fr.userFullName) as userFullName,
                       sum(fr.totalSumma)   as totalSumma,
                       sum(fr.totalDiscount)   as totalDiscount,
                       avg(fr.totalSumma)   as avgSumma,
                       uniq(receiptId)      as receiptsCount,
                       fr.branchId          as branchId,
                       any(fr.branchName)   as branchName""");
        StringBuilder countSql = new StringBuilder("select uniq(fr.userId, fr.branchId) as count");
        var params = new HashMap<String, Object>();
        StringBuilder sql = new StringBuilder(" from fashion_report fr where 1=1 ").append(whereSql(filter, params));
        countSql.append(sql);
        selectSql.append(sql).append(" group by fr.userId, fr.branchId ");
        if (StringUtils.isNotBlank(filter.getOrderBy())) {
            selectSql.append(" order by ").append(filter.getOrderBy());
            if (SortTypeEnum.desc.equals(filter.getSortOrder())) {
                selectSql.append(" desc");
            } else {
                selectSql.append(" asc");
            }
        }
        return new PageImpl<>(jdbcClient.sql(selectSql.toString()).params(params).query(FashionReportByUserDto.class).list(),
                filter.getPageable(),
                jdbcClient.sql(countSql.toString()).params(params).query(Long.class).optional().orElse(0L));
    }


    @Override
    public FashionReportByUserStatisticsDto reportByUserStatistics(FashionReportFilter filter) {
        var params = new HashMap<String, Object>();
        String selectSql = """
                select sum(fr.totalSumma) as totalSumma,
                       sum(fr.totalDiscount) as totalDiscount,
                       uniq(receiptId)    as receiptsCount  from fashion_report fr where 1=1 """ + whereSql(filter, params);
        return jdbcClient.sql(selectSql).params(params).query(FashionReportByUserStatisticsDto.class).optional().orElseGet(FashionReportByUserStatisticsDto::new);
    }

    @Override
    public FashionReportByProductStatisticsDto reportByProductStatistics(FashionReportFilter filter) {
        var params = new HashMap<String, Object>();
        String selectSql = "select uniq(productId)    as totalProductCount,\n" +
                "       sum(fr.totalDiscount) as totalDiscount,\n" +
                "       sum(fr.totalVat)      as totalNds,\n" +
                "       sum(fr.totalSumma)    as totalSum\n" +
                "from fashion_report fr where 1=1 " + whereSql(filter, params);
        return jdbcClient.sql(selectSql).params(params).query(FashionReportByProductStatisticsDto.class).optional().orElseGet(FashionReportByProductStatisticsDto::new);
    }

    @Override
    public FashionReportByCategoryStatisticsDto reportByCategoryStatistics(FashionReportFilter filter) {
        var params = new HashMap<String, Object>();
        String selectSql = "select uniq(productId)    as totalProductCount,\n" +
                "       sum(fr.totalDiscount) as totalDiscount,\n" +
                "       sum(fr.totalVat)      as totalNds,\n" +
                "       sum(fr.totalSumma)      as totalAmount,\n" +
                "       uniq(receiptId)    as totalReceiptCount\n" +
                "from fashion_report fr where 1=1 " + whereSql(filter, params);
        return jdbcClient.sql(selectSql).params(params).query(FashionReportByCategoryStatisticsDto.class).optional().orElseGet(FashionReportByCategoryStatisticsDto::new);
    }

    @Override
    public FashionReportByOneProductStatisticsDto reportByOneProductStatistics(FashionReportFilter filter) {
        var params = new HashMap<String, Object>();
        String selectSql = "select uniqIf(receiptId, fr.status = 'PAID') as totalPaidProductCount,\n" +
                "       sum(fr.totalDiscount)                 as totalDiscount,\n" +
                "       sum(fr.totalSumma)                    as totalSum,\n" +
                "       uniq(receiptId)                    as totalReceiptCount\n" +
                "from fashion_report fr where 1=1 " + whereSql(filter, params);
        return jdbcClient.sql(selectSql).params(params).query(FashionReportByOneProductStatisticsDto.class).optional().orElseGet(FashionReportByOneProductStatisticsDto::new);
    }

    private String whereSql(FashionReportFilter filter, Map<String, Object> params) {
        StringBuilder whereSql = new StringBuilder();
        if (filter.getCompanyId() != null) {
            whereSql.append(" and fr.companyId = :companyId");
            params.put("companyId", filter.getCompanyId());
        }
        if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
            whereSql.append(" and fr.branchId IN (:branchIds)");
            params.put("branchIds", filter.getBranchIds());
        }
        if (filter.getProductId() != null) {
            whereSql.append(" and fr.productId = :productId");
            params.put("productId", filter.getProductId());
        }
        if (filter.getColorGroupId() != null) {
            whereSql.append(" and fr.colorGroupId = :colorGroupId");
            params.put("colorGroupId", filter.getColorGroupId());
        }
        if (CollectionUtils.isNotEmpty(filter.getCategoryIds())) {
            whereSql.append(" and fr.categoryId in (:categoryIds)");
            params.put("categoryIds", filter.getCategoryIds());
        }
        if (filter.getUserId() != null) {
            whereSql.append(" and fr.userId = :userId");
            params.put("userId", filter.getUserId());
        }
        if (filter.getFrom() != null) {
            whereSql.append(" and fr.dateTime >= :from");
            params.put("from", filter.getFrom().withNano(0));
        }
        if (filter.getTo() != null) {
            whereSql.append(" and fr.dateTime <= :to");
            params.put("to", filter.getTo().withNano(0));
        }
        if (StringUtils.isNotBlank(filter.getSearch())) {
            whereSql.append(" and lowerUTF8(fr.productName) like :searchKey");
            params.put("searchKey", filter.getSearchForQuery());
        }
        return whereSql.toString();
    }
}
