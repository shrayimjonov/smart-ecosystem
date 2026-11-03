package uz.uzkassa.smartpos.repository.postgres.fashion.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.fashion.balance.FashionBalanceHistoryDto;
import uz.uzkassa.smartpos.dto.fashion.balance.FashionBalanceHistoryStatisticsDto;
import uz.uzkassa.smartpos.dto.fashion.balance.FashionColorGroupHistoryDto;
import uz.uzkassa.smartpos.filter.FashionBalanceHistoryFilter;
import uz.uzkassa.smartpos.repository.postgres.fashion.FashionBalanceHistoryRepositoryCustom;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class FashionBalanceHistoryRepositoryImpl implements FashionBalanceHistoryRepositoryCustom {
    private final JdbcClient jdbcClient;
    private final BalanceHistoryRowMapper rowMapper = new BalanceHistoryRowMapper();

    @Override
    public Page<FashionBalanceHistoryDto> findAllByFilter(FashionBalanceHistoryFilter filter) {
        StringBuilder selectSql = new StringBuilder("""
                select fbh.product_id            as "product.id",
                       min(pb.name)              as "product.name",
                       fbh.balance_date          as balanceDate,
                       sum(fbh.balance_start)    as balanceStart,
                       sum(fbh.balance_end)      as balanceEnd,
                       sum(fbh.income)           as income,
                       sum(fbh.sale)             as sale,
                       sum(fbh.back)             as back,
                       sum(fbh.transfer_in)      as transferIn,
                       sum(fbh.transfer_out)     as transferOut,
                       sum(fbh.stock_adjustment) as stockAdjustment
                """);
        StringBuilder countSql = new StringBuilder("select count(distinct (fbh.product_id, fbh.balance_date)) as count ");
        StringBuilder sql = new StringBuilder("""
                from fashion_balance_history fbh
                         left join product_branch pb
                                   on fbh.product_id = pb.product_id and fbh.branch_id = pb.branch_id and fbh.city_id = pb.city_id where 1=1\s""");
        Map<String, Object> params = new HashMap<>();
        sql.append(whereSql(filter, params));
        selectSql.append(sql).append(" group by fbh.product_id, fbh.balance_date ");
        if (StringUtils.isNotBlank(filter.getOrderBy())) {
            selectSql.append(" order by ").append(filter.getOrderBy()).append(" ").append(filter.getSortOrder().name());
        } else {
            selectSql.append(" order by fbh.balance_date desc ");
        }
        countSql.append(sql);
        return new PageImpl<>(jdbcClient.sql(selectSql.toString()).params(params).query(rowMapper).list(),
                filter.getPageable(),
                jdbcClient.sql(countSql.toString()).params(params).query(Long.class).optional().orElse(0L));
    }

    @Override
    public List<FashionColorGroupHistoryDto> findAllColorGroupByFilter(FashionBalanceHistoryFilter filter) {
        HashMap<String, Object> params = new HashMap<>();
        String selectSql = """
                select pcg.generic_id            as colorGroupId,
                       min(pcg.color_code)       as colorGroupCode,
                       min(pcg.color_name)       as colorGroupName,
                       min(pcg.print_name)       as printName,
                       fbh.balance_date          as balanceDate,
                       sum(fbh.balance_start)    as balanceStart,
                       sum(fbh.balance_end)      as balanceEnd,
                       sum(fbh.income)           as income,
                       sum(fbh.sale)             as sale,
                       sum(fbh.back)             as back,
                       sum(fbh.transfer_in)      as transferIn,
                       sum(fbh.transfer_out)     as transferOut,
                       sum(fbh.stock_adjustment) as stockAdjustment
                from fashion_balance_history fbh
                         left join product_item pi
                                   on fbh.product_item_id = pi.generic_id and fbh.branch_id = pi.branch_id
                         left join product_color_group pcg on pi.color_group_id = pcg.generic_id and pi.branch_id = pcg.branch_id where 1=1 
                """ + whereSql(filter, params) +
                " group by pcg.generic_id, fbh.balance_date";
        return jdbcClient.sql(selectSql).params(params).query(FashionColorGroupHistoryDto.class).list();
    }


    @Override
    public FashionBalanceHistoryStatisticsDto getStatisticsByFilter(FashionBalanceHistoryFilter filter) {
        final LocalDate from = filter.getFrom();
        final LocalDate to = filter.getTo();
        final LocalDate balanceDate = filter.getBalanceDate();

        FashionBalanceHistoryStatisticsDto dto = new FashionBalanceHistoryStatisticsDto();

        HashMap<String, Object> productCountParams = new HashMap<>();
        String productCountSql = "select count(distinct fbh.product_id) as productCount " +
                " from fashion_balance_history fbh where 1=1 " + whereSql(filter, productCountParams);
        Long productCount = jdbcClient.sql(productCountSql).params(productCountParams).query(Long.class).optional().orElse(0L);

        filter.setFrom(null);
        filter.setTo(Optional.ofNullable(from).orElse(LocalDate.now()).minusDays(1));
        filter.setBalanceDate(null);
        HashMap<String, Object> balanceStartParams = new HashMap<>();
        String balanceStartSql = "select sum(fbh.balance_end - fbh.balance_start) as balanceStart " +
                " from fashion_balance_history fbh where 1=1 " + whereSql(filter, balanceStartParams);
        BigDecimal balanceStart = jdbcClient.sql(balanceStartSql).params(balanceStartParams).query(BigDecimal.class).optional().orElse(BigDecimal.ZERO);

        filter.setFrom(null);
        filter.setTo(Optional.ofNullable(to).orElse(LocalDate.now()));
        filter.setBalanceDate(null);
        HashMap<String, Object> balanceEndParams = new HashMap<>();
        String balanceEndSql = "select sum(fbh.balance_end - fbh.balance_start) as balanceEnd " +
                " from fashion_balance_history fbh where 1=1 " + whereSql(filter, balanceEndParams);
        BigDecimal balanceEnd = jdbcClient.sql(balanceEndSql).params(balanceEndParams).query(BigDecimal.class).optional().orElse(BigDecimal.ZERO);

        dto.setProductCount(productCount);
        dto.setBalanceStart(balanceStart);
        dto.setBalanceEnd(balanceEnd);
        return dto;
    }

    private String whereSql(FashionBalanceHistoryFilter filter, Map<String, Object> params) {
        StringBuilder whereSql = new StringBuilder();
        whereSql.append(" and fbh.company_id = :companyId ");
        params.put("companyId", filter.getCompanyId());
        if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
            whereSql.append(" and fbh.branch_id in (:branchIds) ");
            params.put("branchIds", filter.getBranchIds());
        }
        if (filter.getProductId() != null) {
            whereSql.append(" and fbh.product_id = :productId ");
            params.put("productId", filter.getProductId());
        }
        if (CollectionUtils.isNotEmpty(filter.getCategoryIds())) {
            whereSql.append(" and pb.category_id in (:categoryIds) ");
            params.put("categoryIds", filter.getCategoryIds());
        }
        if (filter.getBalanceDate() != null) {
            whereSql.append(" and fbh.balance_date = :balanceDate ");
            params.put("balanceDate", filter.getBalanceDate());
        }
        if (filter.getFrom() != null) {
            whereSql.append(" and fbh.balance_date >= :from ");
            params.put("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            whereSql.append(" and fbh.balance_date <= :to ");
            params.put("to", filter.getTo());
        }
        return whereSql.toString();
    }

    private static final class BalanceHistoryRowMapper implements RowMapper<FashionBalanceHistoryDto> {

        @Override
        public FashionBalanceHistoryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            FashionBalanceHistoryDto dto = new FashionBalanceHistoryDto();
            dto.setProduct(new CommonDTO(rs.getLong("product.id"), rs.getString("product.name")));
            dto.setBalanceDate(rs.getObject("balanceDate", LocalDate.class));
            dto.setBalanceStart(rs.getBigDecimal("balanceStart"));
            dto.setBalanceEnd(rs.getBigDecimal("balanceEnd"));
            dto.setIncome(rs.getBigDecimal("income"));
            dto.setSale(rs.getBigDecimal("sale"));
            dto.setBack(rs.getBigDecimal("back"));
            dto.setTransferIn(rs.getBigDecimal("transferIn"));
            dto.setTransferOut(rs.getBigDecimal("transferOut"));
            dto.setStockAdjustment(rs.getBigDecimal("stockAdjustment"));
            return dto;
        }
    }
}
