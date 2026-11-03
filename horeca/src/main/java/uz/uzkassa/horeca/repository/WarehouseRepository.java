package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.warehouse.Warehouse;


public interface WarehouseRepository extends BaseRepository<Warehouse, Long>, WarehouseRepositoryCustom {
    boolean existsByBranchIdAndDeletedIsFalseAndNameIgnoreCase(Long branchId, String name);

    boolean existsByBranchIdAndDeletedIsFalseAndNameIgnoreCaseAndIdNot(Long branchId, String name, Long id);

    boolean existsByBranchIdAndDepartmentIdAndDeletedIsFalse(Long branchId, Long departmentId);

    boolean existsByBranchIdAndDepartmentIdIsNullAndDeletedIsFalse(Long branchId);

    boolean existsByBranchIdAndDepartmentIdAndIdNotAndDeletedIsFalse(Long branchId, Long departmentId, Long id);
}
