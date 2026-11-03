package uz.uzkassa.smartpos.repository.postgres.warehouse;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.warehouse.Register;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegisterRepository extends JpaRepository<Register, Long>, RegisterRepositoryCustom {

    void deleteByBranchIdAndProductId(Long branchId, Long productId);

    Optional<Register> findOneByBranchIdAndProductId(Long branchId, Long productId);

    List<Register> findAllByBranchIdAndProductIdIn(Long branchId, List<Long> productIds);

    @Modifying
    @Query(value = "update Register r set r.balance = 0, r.markingCount = 0 where r.branchId = :branchId")
    void resetWarehouse(@Param("branchId") Long branchId);

    @Modifying
    @Query("delete from Register where (branchId, cityId) = (select id, cityKeyId from Branch where id = :branchId)")
    void deleteByBranchId(@Param("branchId") Long branchId);

    void deleteAllByBranchIdAndCityIdAndProductIdIn(@NotNull Long branchId, Long cityId, Collection<Long> categoryIds);

    @Modifying
    @Query(value = "delete from register r using product_branch pb where r.product_id = pb.product_id and r.branch_id = pb.branch_id and pb.city_id = :cityId and pb.deleted and r.city_id = :cityId", nativeQuery = true)
    void cleanup(@Param("cityId") Long cityId);
}
