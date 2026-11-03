package uz.uzkassa.smartpos.repository.postgres.warehouse;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import uz.uzkassa.smartpos.domain.postgres.warehouse.TransferDetail;

import java.util.Collection;

public interface TransferDetailRepository extends JpaRepository<TransferDetail, Long> {
    @Query(value = "select exists(select td.id from transfer_detail td inner join transfer t on t.id = td.transfer_id where t.status != ?1 and t.from_branch_id = ?2 and jsonb_exists(td.markings, ?3)) ", nativeQuery = true)
    boolean existsByTransferStatusNotAndBranchIdAndMarkingsContaining(String status, Long branchId, String code);

    void deleteAllByProductIdAndBranchId(Long id, Long branchId);

    void deleteAllByBranchIdAndCityIdAndProductIdIn(@NotNull Long branchId, @NotNull Long cityId, Collection<Long> categoryIds);

    @Modifying
    @Query(value = "delete from transfer_detail td using product_branch pb where td.product_id = pb.product_id and td.branch_id = pb.branch_id and td.city_id = pb.city_id and pb.deleted", nativeQuery = true)
    void cleanup();
}
