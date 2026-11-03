package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.equipment.Equipment;

@Repository
public interface EquipmentRepository extends BaseRepository<Equipment, Long>, EquipmentRepositoryCustom {

    void deleteAllByCustomerBranchId(Long customerBranchId);
    boolean existsByCustomerBranchIdAndDeletedIsFalse(Long customerBranchId);
}
