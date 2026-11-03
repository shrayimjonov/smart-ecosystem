package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.inventory.Warehouse;
import uz.uzkassa.cts.repository.custom.WarehouseRepositoryCustom;

@Repository
public interface WarehouseRepository extends BaseRepository<Warehouse, Long>, WarehouseRepositoryCustom {

   boolean existsByNameIsIgnoreCaseAndBranchIdAndDeletedIsFalse(String name, Long branchId);

   boolean existsByNameIsIgnoreCaseAndBranchIdAndDeletedIsFalseAndIdNot(String name, Long branchId, Long id);
}
