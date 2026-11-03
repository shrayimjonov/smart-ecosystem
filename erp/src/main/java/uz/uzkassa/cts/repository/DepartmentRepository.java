package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Branch;
import uz.uzkassa.cts.domain.hrms.Department;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.repository.custom.DepartmentRepositoryCustom;

import java.util.List;

@Repository
public interface DepartmentRepository extends BaseRepository<Department, Long>, DepartmentRepositoryCustom {

    List<Department> findAllByBranchIdInAndParentIdIsNullAndStatus(List<Long> branches, CommonStatus status);

    List<Department> findAllByBranchIdAndParentIdIsNullAndStatus(Long branchId, CommonStatus status);

    List<Department> findAllByBranchIdAndStatusAndDeletedIsFalse(Long branchId, CommonStatus status);
}
