package uz.uzkassa.smartpos.repository.postgres.warehouse;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public interface IncomeOfProductDetailRepositoryCustom {
    Map<Pair<Long, Long>, BigDecimal> getAvgCostPrices(List<Pair<Long, Long>> productIdAndBranchIds);
}
