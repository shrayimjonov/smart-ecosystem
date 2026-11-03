package uz.uzkassa.smartpos.repository.postgres.fashion;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.smartpos.domain.postgres.fashion.FashionBalanceHistory;

import java.util.List;
import java.util.Optional;

public interface FashionBalanceHistoryRepository extends JpaRepository<FashionBalanceHistory, Long>, FashionBalanceHistoryRepositoryCustom {
    Optional<FashionBalanceHistory> findFirstByBranchIdAndProductIdOrderByIdDesc(Long branchId, Long productId);

    Optional<FashionBalanceHistory> findFirstByBranchIdAndProductItemIdOrderByIdDesc(Long branchId, Long productItemId);

    void deleteAllByBranchIdAndCityIdAndProductIdIn(@NotNull Long branchId, @NotNull Long cityId, List<Long> categoryIds);

    void deleteByProductIdAndBranchId(Long id, Long branchId);

    void deleteAllByProductItemId(Long productItemId);

    void deleteAllByProductItemColorGroupId(Long colorGroupId);

    void deleteAllByBranchId(@NotNull Long branchId);
}