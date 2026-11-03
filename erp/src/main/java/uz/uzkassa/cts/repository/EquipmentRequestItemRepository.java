package uz.uzkassa.cts.repository;

import uz.uzkassa.cts.domain.hrms.EquipmentRequestItem;

public interface EquipmentRequestItemRepository extends BaseRepository<EquipmentRequestItem, Long> {

    void deleteAllByEquipmentRequestId(Long equipmentRequestId);
}
