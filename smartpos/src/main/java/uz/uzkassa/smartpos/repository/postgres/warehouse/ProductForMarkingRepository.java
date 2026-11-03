package uz.uzkassa.smartpos.repository.postgres.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.warehouse.ProductForMarking;
import uz.uzkassa.smartpos.enums.MarkingStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductForMarkingRepository extends JpaRepository<ProductForMarking, String>, JpaSpecificationExecutor<ProductForMarking> {
    Optional<ProductForMarking> findByMarkingAndBranchId(String marking, Long branchId);

    Optional<ProductForMarking> findFirstByMarkingInAndBranchId(Collection<String> markings, Long branchId);

    @Query("select p from ProductForMarking p where p.branchId = ?1 and p.productId = ?2 and (p.status = ?3 or p.status is null)")
    List<ProductForMarking> findByBranchIdAndProductIdAndStatusOrStatusNull(Long branchId, Long productId, MarkingStatus status);

    void deleteByIncomeOfProductId(Long incomeOfProductId);

    Set<ProductForMarking> findByMarkingInAndStatusNot(Collection<String> markings, MarkingStatus status);

    List<ProductForMarking> findAllByBranchIdAndMarkingIn(Long branchId, Collection<String> markings);

    List<ProductForMarking> findAllByParentIn(Collection<String> parents);

    void deleteAllByBranchIdAndStatusNotIn(Long branchId, Collection<MarkingStatus> statuses);

    void deleteAllByBranchIdAndStatusNotInAndProductIdIn(Long branchId, Collection<MarkingStatus> statuses, Collection<Long> productIds);
}
