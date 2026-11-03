package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.hrms.LeaveAllowance;
import uz.uzkassa.cts.repository.custom.LeaveAllowanceRepositoryCustom;

import java.util.Optional;

@Repository
public interface LeaveAllowanceRepository extends BaseRepository<LeaveAllowance, Long>, LeaveAllowanceRepositoryCustom {

    Optional<LeaveAllowance> findFirstByEmployeeIdAndYearAndDeletedIsFalse(Long employeeId, Integer year);

    boolean existsByEmployeeIdAndYearAndDeletedIsFalse(Long employeeId, Integer year);

    boolean existsByEmployeeIdAndYearAndDeletedIsFalseAndIdNot(Long employeeId, Integer year, Long id);

}
