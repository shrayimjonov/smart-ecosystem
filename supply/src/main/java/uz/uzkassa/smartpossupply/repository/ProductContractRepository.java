package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.product.ProductContract;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.07.2023
 */
@Repository
public interface ProductContractRepository extends BaseRepository<ProductContract, Long>, ProductContractRepositoryCustom {

    Optional<ProductContract> findFirstByNameAndBasePriceAndProfitRateAndBranchIdAndDeletedIsFalse(String name, BigDecimal basePrice, BigDecimal profitRate, Long branchId);

    boolean existsByNameAndBasePriceAndProfitRateAndBranchIdAndDeletedIsFalse(String name, BigDecimal basePrice, BigDecimal profitRate, Long branchId);

    boolean existsByNameAndBasePriceAndProfitRateAndBranchIdAndDeletedIsFalseAndIdNot(String name, BigDecimal basePrice, BigDecimal profitRate, Long branchId, Long id);
}
