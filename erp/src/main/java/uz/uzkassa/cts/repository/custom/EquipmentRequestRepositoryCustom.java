package uz.uzkassa.cts.repository.custom;

import uz.uzkassa.cts.domain.hrms.EquipmentRequest;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.EquipmentRequestFilter;

public interface EquipmentRequestRepositoryCustom {

    ResultList<EquipmentRequest> getResultList(EquipmentRequestFilter filter);
}
