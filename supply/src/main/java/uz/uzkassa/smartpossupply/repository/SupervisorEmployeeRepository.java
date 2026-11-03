package uz.uzkassa.smartpossupply.repository;

import uz.uzkassa.smartpossupply.domain.supervisour.SupervisorEmployee;

import java.util.Set;

public interface SupervisorEmployeeRepository extends BaseRepository<SupervisorEmployee, Long>, SupervisorEmployeeRepositoryCustom {
    boolean existsBySupervisorIdIsNotAndEmployeeIdIsInAndDeletedIsFalse(Long supervisorId, Set<Long> employeeIds);

    void deleteAllBySupervisorId(Long supervisorId);
}
