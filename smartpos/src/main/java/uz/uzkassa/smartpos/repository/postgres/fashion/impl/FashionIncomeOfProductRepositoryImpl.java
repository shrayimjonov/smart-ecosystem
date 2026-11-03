package uz.uzkassa.smartpos.repository.postgres.fashion.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.repository.postgres.fashion.FashionIncomeOfProductRepositoryCustom;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FashionIncomeOfProductRepositoryImpl implements FashionIncomeOfProductRepositoryCustom {
    private final JdbcClient jdbcClient;

    @Override
    public Map<Pair<Long, Long>, BigDecimal> getAvgCostPrices(List<Pair<Long, Long>> productIdAndBranchIds) {
        String suffix = productIdAndBranchIds.stream()
                .map(pair -> "(" + pair.getFirst() + "," + pair.getSecond() + ")")
                .reduce((a, b) -> a + "," + b).orElse("");
        if (!suffix.isEmpty()) {
            String query = "select product_id, branch_id, avg(cost_price) from fashion_income_of_product_detail where cost_price > 0 and (product_id, branch_id) in (" + suffix + ") group by product_id, branch_id";
            return jdbcClient.sql(query)
                    .query((rs, rowNum) -> Map.entry(Pair.of(rs.getLong(1), rs.getLong(2)), rs.getBigDecimal(3)))
                    .list()
                    .stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        return Map.of();
    }
}
