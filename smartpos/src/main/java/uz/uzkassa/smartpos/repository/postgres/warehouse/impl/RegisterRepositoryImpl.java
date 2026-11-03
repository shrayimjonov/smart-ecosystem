package uz.uzkassa.smartpos.repository.postgres.warehouse.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.simple.JdbcClient;
import uz.uzkassa.smartpos.dto.ReportDTO;
import uz.uzkassa.smartpos.dto.analytics.RegisterStatsDTO;
import uz.uzkassa.smartpos.dto.filter.RegisterFilter;
import uz.uzkassa.smartpos.dto.warehouse.RegisterDto;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.repository.postgres.warehouse.RegisterRepositoryCustom;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class RegisterRepositoryImpl implements RegisterRepositoryCustom {

    private final JdbcClient jdbcClient;

    @Override
    public Page<RegisterDto> findAllByFilter(RegisterFilter filter) {
        StringBuilder selectQuery = new StringBuilder("""
                select min(product_id) productId,
                       min(product_name) productName,
                       mxik,
                       null branchId,
                       null branchName,
                       unit_id unitId,
                       min(u.name) unitName,
                       sum(balance) qty,
                       sum(marking_count) markingCount,
                       avg(coalesce(cost_price, 0)) costPrice,
                       avg(coalesce(sales_price, 0)) salesPrice,
                       false hasMark,
                       sum(coalesce(cost_price, 0) * coalesce(balance, 0)) totalCostPrice,
                       sum(coalesce(sales_price, 0) * coalesce(balance, 0)) totalSalesPrice
                """);
        String countQuery = "select count(distinct (mxik, unit_id))";
        StringBuilder query = new StringBuilder("\nfrom register r inner join unit u on r.unit_id = u.id where ");
        var params = new HashMap<String, Object>();
        query.append("company_id = :companyId");
        params.put("companyId", filter.getCompanyId());
        if (CollectionUtils.isNotEmpty(filter.getBranchIdsWithBranchId())) {
            query.append(" and branch_id in (:branchIds)");
            params.put("branchIds", filter.getBranchIdsWithBranchId());
        }
        if (filter.getProductId() != null) {
            query.append(" and product_id = :productId");
            params.put("productId", filter.getProductId());
        }
        if (filter.getUnitId() != null) {
            query.append(" and unit_id = :unitId");
            params.put("unitId", filter.getUnitId());
        }
        if (CollectionUtils.isNotEmpty(filter.getCategoryIds())) {
            query.append(" and category_id in (:categoryIds)");
            params.put("categoryIds", filter.getCategoryIds());
        }
        if (StringUtils.isNotEmpty(filter.getMxik())) {
            query.append(" and mxik = :mxik");
            params.put("mxik", filter.getMxik());
        }
        if (StringUtils.isNotBlank(filter.getSearch())) {
            query.append(" and lower(product_name) like :search");
            params.put("search", filter.getSearchForQuery());
        }
        selectQuery.append(query).append(" group by mxik, unit_id");
        if (StringUtils.isNotBlank(filter.getOrderBy()) && !"id".equals(filter.getOrderBy())) {
            selectQuery.append(" order by ").append(filter.getOrderBy());
            if (SortTypeEnum.desc.equals(filter.getSortOrder())) {
                selectQuery.append(" desc ");
            } else {
                selectQuery.append(" asc ");
            }
        }
        selectQuery.append(" limit :limit offset :offset");
        params.put("limit", filter.getSize());
        params.put("offset", filter.getOffset());
        return new PageImpl<>(jdbcClient.sql(selectQuery.toString())
                .params(params)
                .query(RegisterDto.class)
                .list(),
                filter.getPageable(),
                jdbcClient.sql(countQuery + query)
                        .params(params)
                        .query(Long.class)
                        .optional().orElse(0L));
    }

    @Override
    public List<RegisterDto> findByProduct(RegisterFilter filter) {
        StringBuilder selectQuery = new StringBuilder("""
                select product_id productId,
                       min(product_name) productName,
                       min(mxik) as mxik,
                       b.id branchId,
                       b.name branchName,
                       u.id unitId,
                       u.name unitName,
                       sum(balance) qty,
                       sum(marking_count) markingCount,
                       avg(coalesce(cost_price, 0)) costPrice,
                       avg(coalesce(sales_price, 0)) salesPrice,
                       bool_and(has_mark) hasMark,
                       sum(coalesce(cost_price, 0) * coalesce(balance, 0)) totalCostPrice,
                       sum(coalesce(sales_price, 0) * coalesce(balance, 0)) totalSalesPrice
                """);
        StringBuilder query = new StringBuilder("\nfrom register r inner join unit u on r.unit_id = u.id inner join branch b on b.id = r.branch_id where ");
        var params = new HashMap<String, Object>();
        query.append("r.company_id = :companyId");
        params.put("companyId", filter.getCompanyId());
        if (CollectionUtils.isNotEmpty(filter.getBranchIdsWithBranchId())) {
            query.append(" and branch_id in (:branchIds)");
            params.put("branchIds", filter.getBranchIdsWithBranchId());
        }
        if (StringUtils.isNotEmpty(filter.getMxik())) {
            query.append(" and ");
            query.append("mxik = :mxik");
            params.put("mxik", filter.getMxik());
        }
        if (filter.getUnitId() != null) {
            query.append(" and ");
            query.append("unit_id = :unitId");
            params.put("unitId", filter.getUnitId());
        }
        String fullQuery = selectQuery.append(query)
                .append(" group by product_id, u.id, b.id")
                .toString();
        return jdbcClient.sql(fullQuery)
                .params(params)
                .query(RegisterDto.class)
                .list();
    }

    @Override
    public RegisterStatsDTO getStats(RegisterFilter filter) {
        StringBuilder query = new StringBuilder("""
                select count(distinct (mxik, unit_id))                      productCount,
                       sum(coalesce(cost_price, 0) * coalesce(balance, 0))  totalCostPrice,
                       sum(coalesce(sales_price, 0) * coalesce(balance, 0)) totalSalesPrice
                from register where company_id = :companyId
                """);
        var params = new HashMap<String, Object>();
        params.put("companyId", filter.getCompanyId());
        if (CollectionUtils.isNotEmpty(filter.getBranchIdsWithBranchId())) {
            query.append(" and branch_id in (:branchIds)");
            params.put("branchIds", filter.getBranchIdsWithBranchId());
        }
        if (filter.getProductId() != null) {
            query.append(" and ");
            query.append("product_id = :productId");
            params.put("productId", filter.getProductId());
        }
        if (filter.getUnitId() != null) {
            query.append(" and ");
            query.append("unit_id = :unitId");
            params.put("unitId", filter.getUnitId());
        }
        if (CollectionUtils.isNotEmpty(filter.getCategoryIds())) {
            query.append(" and ");
            query.append("category_id in (:categoryIds)");
            params.put("categoryIds", filter.getCategoryIds());
        }
        if (StringUtils.isNotEmpty(filter.getMxik())) {
            query.append(" and ");
            query.append("mxik = :mxik");
            params.put("mxik", filter.getMxik());
        }
        if (StringUtils.isNotBlank(filter.getSearchForQuery())) {
            query.append(" and ");
            query.append("lower(product_name) like :search");
            params.put("search", filter.getSearchForQuery());
        }
        return jdbcClient.sql(query.toString()).params(params).query(RegisterStatsDTO.class).single();
    }

    @Override
    public List<ReportDTO> findAllByBranchIdAndCityId(Long branchId, Long cityId) {
        String sql = """
                select r.id           as registerId,
                       c.name         as categoryName,
                       pb.name        as productName,
                       pb.barcode     as barcode,
                       pb.vat_barcode as vatBarcode,
                       u.name         as unitName,
                       pb.sales_price as salesPrice,
                       r.balance      as balance
                from register r
                         inner join unit u on r.unit_id = u.id
                         inner join product_branch pb on r.branch_id = pb.branch_id and r.product_id = pb.product_id and pb.deleted is false
                         inner join category_branch c on pb.category_id = c.category_id and pb.branch_id = c.branch_id
                where r.branch_id = :branchId and pb.city_id = :cityId""";

        return jdbcClient.sql(sql)
                .param("branchId", branchId)
                .param("cityId", cityId)
                .query(ReportDTO.class).list();

    }
}
