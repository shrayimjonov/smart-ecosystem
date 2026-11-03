package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.Department;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepartmentRepository extends BaseRepository<Department, Long>, DepartmentRepositoryCustom {
    boolean existsByBranchIdAndDeletedIsFalseAndNameIgnoreCase(Long branchId, String name);

    Optional<Department> findFirstByBranchIdAndDeletedIsFalseAndNameIgnoreCase(Long branchId, String name);

    boolean existsByBranchIdAndDeletedIsFalseAndNameIgnoreCaseAndIdNot(Long branchId, String name, Long id);

    List<Department> findAllByBranchIdAndDeletedIsFalse(Long branchId);

    Optional<Department> findFirstByUniqueIdAndBranchIdAndDeletedIsFalse(UUID uniqueId, Long branchId);
}
