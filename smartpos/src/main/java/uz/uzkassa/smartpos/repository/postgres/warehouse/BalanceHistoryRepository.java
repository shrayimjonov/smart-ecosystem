package uz.uzkassa.smartpos.repository.postgres.warehouse;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.warehouse.BalanceHistory;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface BalanceHistoryRepository extends JpaRepository<BalanceHistory, Long>, JpaSpecificationExecutor<BalanceHistory> {
    void deleteByProductIdAndBranchId(Long productId, Long branchId);

    void deleteAllByBranchIdAndCityIdAndProductIdIn(@NotNull Long branchId, @NotNull Long cityId, Collection<Long> categoryIds);

    Optional<BalanceHistory> findOneByBranchIdAndProductIdAndBalanceDate(Long branchId, Long productId, LocalDate balanceDate);

    Optional<BalanceHistory> findFirstByBranchIdAndProductIdOrderByBalanceDateDesc(Long branchId, Long productId);

    @Modifying
    @Query(value = "update balance_history set balance_end=0, stock_adjustment = balance_end where id in (select max(id) from balance_history where branch_id=:branchId group by branch_id, product_id)", nativeQuery = true)
    void resetHistory(@Param("branchId") Long branchId);

    @Modifying
    @Query(value = "delete from balance_history bh using product_branch pb where bh.product_id = pb.product_id and bh.branch_id = pb.branch_id and pb.city_id = :cityId and pb.deleted and bh.city_id = :cityId", nativeQuery = true)
    void cleanup(@Param("cityId") Long cityId);

    void deleteAllByBranchId(@NotNull Long branchId);
}
