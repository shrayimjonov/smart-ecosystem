package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.hrms.EquipmentRequest;
import uz.uzkassa.cts.dto.equipmentrequest.EquipmentRequestDTO;
import uz.uzkassa.cts.dto.equipmentrequest.admin.EquipmentRequestDetailAdminDTO;
import uz.uzkassa.cts.dto.equipmentrequest.admin.EquipmentRequestListAdminDTO;
import uz.uzkassa.cts.dto.equipmentrequest.cabinet.EquipmentRequestDetailCabinetDTO;
import uz.uzkassa.cts.dto.equipmentrequest.cabinet.EquipmentRequestListCabinetDTO;
import uz.uzkassa.cts.enums.ApproverCommonStatus;
import uz.uzkassa.cts.filter.EquipmentRequestFilter;

public interface EquipmentRequestService extends BaseService<EquipmentRequest, Long> {
    Long create(EquipmentRequestDTO equipmentRequestDTO);

    Long update(Long id, EquipmentRequestDTO equipmentRequestDTO);

    EquipmentRequestDetailAdminDTO getForAdmin(Long id);

    Page<EquipmentRequestListAdminDTO> getListForAdmin(EquipmentRequestFilter filter);

    EquipmentRequestDetailCabinetDTO getForCabinet(Long id);

    Page<EquipmentRequestListCabinetDTO> getListForCabinet(EquipmentRequestFilter filter);

    void updateStatus(Long id, ApproverCommonStatus status);
}
