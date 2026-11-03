package uz.uzkassa.smartpossupply.repository;

import uz.uzkassa.smartpossupply.domain.equipment.Equipment;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.EquipmentFilter;

public interface EquipmentRepositoryCustom {
    ResultList<Equipment> getResultList(EquipmentFilter filter);
}
