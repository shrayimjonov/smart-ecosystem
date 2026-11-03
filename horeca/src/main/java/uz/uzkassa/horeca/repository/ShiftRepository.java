package uz.uzkassa.horeca.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.horeca.domain.shift.Shift;
import uz.uzkassa.horeca.enums.ShiftStatus;

import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.11.2024
 */

@Repository
public interface ShiftRepository extends BaseRepository<Shift, Long>, ShiftRepositoryCustom {

    Optional<Shift> findFirstByBranchIdAndStatusOrderByCreatedDateDesc(Long branchId, ShiftStatus status);

    Optional<Shift> findFirstByUserIdAndStatusOrderByCreatedDateDesc(Long userId, ShiftStatus status);

    boolean existsByUserIdAndStatus(Long userId, ShiftStatus status);

    @Query(value = "select count(sh.id) from Shift sh where sh.branchId=:branchId")
    Long getMaxIncrementNumber(@Param("branchId") Long branchId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")})
    @Query("select s from Shift s where s.id=:id")
    Optional<Shift> get(@Param("id") Long id);
}
