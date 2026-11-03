package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.shift.Shift;
import uz.uzkassa.smartpossupply.enums.ShiftStatus;

import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 21.12.2022 17:28
 */

@Repository
public interface ShiftRepository extends BaseRepository<Shift, Long> {
    Optional<Shift> findFirstByBranchIdAndStatusOrderByCreatedDateDesc(Long branchId, ShiftStatus status);

    @Query(value = "select count(sh.id) from Shift sh where sh.branchId=:branchId")
    Long getMaxIncrementNumber(@Param("branchId") Long branchId);
}
