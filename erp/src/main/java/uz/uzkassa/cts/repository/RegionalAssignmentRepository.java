package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.regionalassignment.RegionalAssignment;
import uz.uzkassa.cts.repository.custom.RegionalAssignmentRepositoryCustom;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionalAssignmentRepository extends BaseRepository<RegionalAssignment, Long>, RegionalAssignmentRepositoryCustom {

    @Query(value = "select rad.district_id from regional_assignment ra " +
                   "join regional_assignment_district rad on rad.regional_assignment_id = ra.id " +
                   "where ra.deleted <> true and ra.employee_id =:employeeId", nativeQuery = true)
    List<Long> getEmployeeDistrictIds(@Param("employeeId") Long employeeId);

    boolean existsByEmployeeId(Long employeeId);
}
