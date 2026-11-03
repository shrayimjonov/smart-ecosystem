package uz.uzkassa.smartpos.repository.postgres.fashion;

import org.springframework.data.util.Pair;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface FashionIncomeOfProductRepositoryCustom {
    Map<Pair<Long, Long>, BigDecimal> getAvgCostPrices(List<Pair<Long, Long>> productIdAndBranchIds);
}
