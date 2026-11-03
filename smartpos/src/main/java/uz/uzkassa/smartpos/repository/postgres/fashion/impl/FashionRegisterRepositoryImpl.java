package uz.uzkassa.smartpos.repository.postgres.fashion.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import uz.uzkassa.smartpos.domain.postgres.fashion.FashionRegister;
import uz.uzkassa.smartpos.domain.postgres.fashion.FashionRegister_;
import uz.uzkassa.smartpos.dto.analytics.RegisterStatsDTO;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.fashion.FashionBranchColorGroupRegisterDto;
import uz.uzkassa.smartpos.dto.product.ProductItemBranchStockDTO;
import uz.uzkassa.smartpos.dto.warehouse.fashion.FashionColorGroupRegisterDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.FashionRegisterDto;
import uz.uzkassa.smartpos.filter.FashionRegisterFilter;
import uz.uzkassa.smartpos.repository.postgres.fashion.FashionRegisterRepositoryCustom;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class FashionRegisterRepositoryImpl implements FashionRegisterRepositoryCustom {
    private final JdbcClient jdbcClient;
    private final EntityManager entityManager;
    private final FashionRegisterMapper registerMapper = new FashionRegisterMapper();
    private final FashionColorGroupRegisterMapper colorGroupRegisterMapper = new FashionColorGroupRegisterMapper();
    private final FashionBranchColorGroupRegisterMapper branchColorGroupRegisterMapper = new FashionBranchColorGroupRegisterMapper();

    @Override
    public RegisterStatsDTO getStats(FashionRegisterFilter filter) {
        StringBuilder sql = new StringBuilder("""
                select count(distinct fr.product_id) as productCount,
                       sum(fr.total_cost_price)      as totalCostPrice,
                       sum(fr.total_sales_price)     as totalSalesPrice
                from fashion_register fr\s""");
        if (StringUtils.isNotBlank(filter.getSearch()) || CollectionUtils.isNotEmpty(filter.getCategoryIds())) {
            sql.append(" inner join product_branch pb on pb.branch_id = fr.branch_id and pb.city_id = fr.city_id and pb.product_id = fr.product_id ");
        }
        sql.append(" where fr.company_id = :companyId ");
        Map<String, Object> params = getParams(filter, sql);
        return jdbcClient.sql(sql.toString()).params(params).query(RegisterStatsDTO.class).optional().orElseGet(RegisterStatsDTO::new);
    }

    @Override
    public Page<FashionRegisterDto> findAllFashionRegister(FashionRegisterFilter filter) {
        StringBuilder selectSql = new StringBuilder("""
                select fr.product_id                                                                     as productId,
                       min(pb.name)                                                                      as productName,
                       min(fr.unit_id)                                                                   as "unit.id",
                       min(u.name)                                                                       as "unit.name",
                       sum(qty)                                                                          as qty,
                       max(fr.cost_price)                                                                as costPrice,
                       max(fr.sales_price)                                                               as salesPrice,
                       sum(fr.total_cost_price)                                                          as totalCostPrice,
                       sum(fr.total_sales_price)                                                         as totalSalesPrice\s""");
        String countSql = "select count(*) as count";
        StringBuilder sql = new StringBuilder("""
                 from fashion_register fr
                         inner join product_branch pb
                                   on pb.branch_id = fr.branch_id and pb.city_id = fr.city_id and pb.product_id = fr.product_id
                         inner join unit u on fr.unit_id = u.id where fr.company_id = :companyId \
                """);
        var params = getParams(filter, sql);
        sql.append(" group by fr.product_id ");
        selectSql.append(sql);
        selectSql.append(" limit ").append(filter.getSize()).append(" offset ").append(filter.getOffset());
        String count = "select count(*) from (" + countSql + ") as t";
        return new PageImpl<>(
                jdbcClient.sql(selectSql.toString()).params(params).query(registerMapper).list(),
                filter.getPageable(),
                jdbcClient.sql(count).params(params).query(Long.class).optional().orElse(0L));
    }

    @Override
    public List<FashionColorGroupRegisterDto> colorGroupRegisters(FashionRegisterFilter filter) {
        StringBuilder sql = new StringBuilder("""
                select pcg.generic_id            as id,
                       min(pcg.color_code)            as colorCode,
                       min(pcg.color_name)            as colorName,
                       min(pcg.print_name)            as printName,
                       min(u.id)                 as "unit.id",
                       min(u.name)               as "unit.name",
                       sum(fr.qty)               as qty,
                       max(fr.cost_price)        as costPrice,
                       max(fr.sales_price)       as salesPrice,
                       sum(fr.total_cost_price)  as totalCostPrice,
                       sum(fr.total_sales_price) as totalSalesPrice
                from fashion_register fr
                         inner join product_item pi on fr.product_item_id = pi.generic_id and fr.branch_id = pi.branch_id
                         inner join product_color_group pcg on pcg.generic_id = pi.color_group_id and pcg.branch_id = pi.branch_id
                         inner join unit u on fr.unit_id = u.id
                where fr.company_id = :companyId and fr.product_id = :productId\s""");
        var params = new HashMap<String, Object>();
        params.put("companyId", filter.getCompanyId());
        params.put("productId", filter.getProductId());
        if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
            sql.append(" and fr.branch_id in (:branchIds)");
            params.put("branchIds", filter.getBranchIds());
        }
        sql.append(" group by pcg.generic_id ");
        return jdbcClient.sql(sql.toString()).params(params).query(colorGroupRegisterMapper).list();
    }

    @Override
    public Page<FashionBranchColorGroupRegisterDto> branchColorGroupRegisters(FashionRegisterFilter filter) {
        StringBuilder selectSql = new StringBuilder("""
                select pi.size_value             as sizeValue,
                       b.id                      as "branch.id",
                       b.name                    as "branch.name",
                       min(u.id)                 as "unit.id",
                       min(u.name)               as "unit.name",
                       sum(qty)                  as qty,
                       max(fr.cost_price)        as costPrice,
                       max(fr.sales_price)       as salesPrice,
                       sum(fr.total_cost_price)  as totalCostPrice,
                       sum(fr.total_sales_price) as totalSalesPrice
                """);
        StringBuilder countSql = new StringBuilder("select count(*) as count\n");
        StringBuilder sql = new StringBuilder("""
                from fashion_register fr
                         inner join product_item pi on fr.product_item_id = pi.generic_id and fr.branch_id = pi.branch_id
                         inner join unit u on fr.unit_id = u.id
                         inner join branch b on fr.branch_id = b.id
                where fr.company_id = :companyId
                  and fr.product_id = :productId
                  and pi.color_group_id = :colorGroupId
                """);
        var params = new HashMap<String, Object>();
        params.put("companyId", filter.getCompanyId());
        params.put("productId", filter.getProductId());
        params.put("colorGroupId", filter.getColorGroupId());
        if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
            sql.append(" and fr.branch_id in (:branchIds)");
            params.put("branchIds", filter.getBranchIds());
        }
        sql.append(" group by pi.size_value, b.id ");
        selectSql.append(sql).append(" limit ").append(filter.getSize()).append(" offset ").append(filter.getOffset());
        countSql.append(sql);
        String count = "select count(*) from (" + countSql + ") as t";
        return new PageImpl<>(
                jdbcClient.sql(selectSql.toString()).params(params).query(branchColorGroupRegisterMapper).list(),
                filter.getPageable(),
                jdbcClient.sql(count).params(params).query(Long.class).optional().orElse(0L)
        );
    }

    @Override
    public List<ProductItemBranchStockDTO> productItemBranchStock(Long productItemId) {
        String sql = """
                select b.id                      as branchId,
                       b.name                    as branchName,
                       sum(fr.qty)               as count
                from fashion_register fr
                         inner join branch b on fr.branch_id = b.id
                where fr.product_item_id = :productItemId group by b.id \s""";
        var params = new HashMap<String, Object>();
        params.put("productItemId", productItemId);
        return jdbcClient.sql(sql).params(params).query(ProductItemBranchStockDTO.class).list();
    }

    @Override
    public List<FashionRegister> colorGroupItemsRegisters(FashionRegisterFilter filter) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<FashionRegister> query = builder.createQuery(FashionRegister.class);
        Root<FashionRegister> root = query.from(FashionRegister.class);
        var predicates = new ArrayList<Predicate>();
        predicates.add(builder.equal(root.get(FashionRegister_.companyId), filter.getCompanyId()));
        predicates.add(builder.equal(root.get(FashionRegister_.productId), filter.getProductId()));
        if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
            predicates.add(root.get(FashionRegister_.branchId).in(filter.getBranchIds()));
        }
        query.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public Map<Long, BigDecimal> getProductsStockAmount(Long branchId, List<Long> productIds) {
        return jdbcClient.sql("""
                        select product_id, sum(qty) as qty
                        from fashion_register
                        where branch_id = :branchId and product_id in (:productIds)
                        group by product_id
                        """)
                .params(Map.of("branchId", branchId, "productIds", productIds))
                .query((rs, rowNum) -> Pair.of(rs.getLong("product_id"), rs.getBigDecimal("qty")))
                .list()
                .stream().collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }


    private Map<String, Object> getParams(FashionRegisterFilter filter, StringBuilder sql) {
        Map<String, Object> params = new HashMap<>();
        params.put("companyId", filter.getCompanyId());
        if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
            sql.append(" and fr.branch_id in (:branchIds)");
            params.put("branchIds", filter.getBranchIds());
        }
        if (filter.getProductId() != null) {
            sql.append(" and fr.product_id = :productId");
            params.put("productId", filter.getProductId());
        }
        if (StringUtils.isNotBlank(filter.getSearch())) {
            sql.append(" and pb.name ilike :search");
            params.put("search", filter.getSearchForQuery());
        }
        if (CollectionUtils.isNotEmpty(filter.getCategoryIds())) {
            sql.append(" and pb.category_id in (:categoryIds)");
            params.put("categoryIds", filter.getCategoryIds());
        }
        return params;
    }

    public final static class FashionRegisterMapper implements RowMapper<FashionRegisterDto> {

        @Override
        public FashionRegisterDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            FashionRegisterDto dto = new FashionRegisterDto();
            dto.setProductId(rs.getLong("productId"));
            dto.setProductName(rs.getString("productName"));
            dto.setUnit(new CommonDTO(rs.getLong("unit.id"), rs.getString("unit.name")));
            dto.setQty(rs.getBigDecimal("qty"));
            dto.setCostPrice(rs.getBigDecimal("costPrice"));
            dto.setSalesPrice(rs.getBigDecimal("salesPrice"));
            dto.setTotalCostPrice(rs.getBigDecimal("totalCostPrice"));
            dto.setTotalSalesPrice(rs.getBigDecimal("totalSalesPrice"));
            return dto;
        }
    }

    public final static class FashionBranchColorGroupRegisterMapper implements RowMapper<FashionBranchColorGroupRegisterDto> {

        @Override
        public FashionBranchColorGroupRegisterDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            FashionBranchColorGroupRegisterDto dto = new FashionBranchColorGroupRegisterDto();
            dto.setSizeValue(rs.getString("sizeValue"));
            dto.setBranch(new CommonDTO(rs.getLong("branch.id"), rs.getString("branch.name")));
            dto.setUnit(new CommonDTO(rs.getLong("unit.id"), rs.getString("unit.name")));
            dto.setQty(rs.getBigDecimal("qty"));
            dto.setCostPrice(rs.getBigDecimal("costPrice"));
            dto.setSalesPrice(rs.getBigDecimal("salesPrice"));
            dto.setTotalCostPrice(rs.getBigDecimal("totalCostPrice"));
            dto.setTotalSalesPrice(rs.getBigDecimal("totalSalesPrice"));
            return dto;
        }

    }


    public final static class FashionColorGroupRegisterMapper implements RowMapper<FashionColorGroupRegisterDto> {

        @Override
        public FashionColorGroupRegisterDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            FashionColorGroupRegisterDto dto = new FashionColorGroupRegisterDto();
            dto.setId(rs.getLong("id"));
            dto.setColorCode(rs.getString("colorCode"));
            dto.setColorName(rs.getString("colorName"));
            dto.setPrintName(rs.getString("printName"));
            dto.setUnit(new CommonDTO(rs.getLong("unit.id"), rs.getString("unit.name")));
            dto.setQty(rs.getBigDecimal("qty"));
            dto.setCostPrice(rs.getBigDecimal("costPrice"));
            dto.setSalesPrice(rs.getBigDecimal("salesPrice"));
            dto.setTotalCostPrice(rs.getBigDecimal("totalCostPrice"));
            dto.setTotalSalesPrice(rs.getBigDecimal("totalSalesPrice"));
            return dto;
        }

    }
}
