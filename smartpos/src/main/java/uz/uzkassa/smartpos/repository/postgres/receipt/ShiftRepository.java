package uz.uzkassa.smartpos.repository.postgres.receipt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.receipt.Shift;
import uz.uzkassa.smartpos.enums.ShiftStatus;

import java.util.Optional;

@SuppressWarnings("unused")
@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {

    Optional<Shift> findFirstByBranchIdAndStatusOrderByStartDateTimeDesc(Long branchId, ShiftStatus status);

    Optional<Shift> findFirstByTerminalIdAndStatusOrderByStartDateTimeDesc(Long terminalId, ShiftStatus status);

    Optional<Shift> findFirstByUserIdAndStatusAndTerminalIdIsNotNullOrderByStartDateTimeDesc(Long userId, ShiftStatus status);

    @Query(nativeQuery = true, value = "select t.serial_number from shift sh inner join device t on sh.terminal_id = t.id where sh.user_id = :userId and sh.status = 'OPEN' order by sh.start_date_time desc limit 1")
    String findFirstOpenShiftTerminalSerialNumber(@Param("userId") Long userId);

    Optional<Shift> findFirstByBranchIdOrderByNumberDesc(Long branchId);
}
