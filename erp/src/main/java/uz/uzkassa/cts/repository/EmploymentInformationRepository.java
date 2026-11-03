package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.hrms.EmploymentInformation;

import java.util.Optional;

@Repository
public interface EmploymentInformationRepository extends BaseRepository<EmploymentInformation, Long> {

    @Query("SELECT coalesce(max(ei.incrementNumber), 0) FROM EmploymentInformation ei join ei.employee e WHERE e.companyId = :companyId")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId);

    Optional<EmploymentInformation> findFirstByEmployeeIdAndDeletedIsFalse(Long employeeId);


}
