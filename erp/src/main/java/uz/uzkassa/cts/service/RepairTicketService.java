package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.RepairTicket;
import uz.uzkassa.cts.dto.repairticket.RepairTicketDTO;
import uz.uzkassa.cts.dto.repairticket.RepairTicketDetailDTO;
import uz.uzkassa.cts.dto.repairticket.RepairTicketItemDTO;
import uz.uzkassa.cts.dto.repairticket.RepairTicketListDTO;
import uz.uzkassa.cts.enums.RepairTicketStatus;
import uz.uzkassa.cts.enums.RepairType;
import uz.uzkassa.cts.filter.RepairTicketFilter;

import java.util.List;

public interface RepairTicketService extends BaseService<RepairTicket, Long> {

    Long create(RepairTicketDTO repairTicketDTO);

    RepairTicketDetailDTO get(Long id);

    Page<RepairTicketListDTO> getList(RepairTicketFilter filter);

    void updateStatus(Long id, RepairTicketStatus status);

    void assignMaster(Long id, Long masterId);

    void transfer(Long id, Long toBranchId);

    void updateType(Long id, Long typeId);

    void updateRepairType(Long id, RepairType repairType);

    void updateEquipments(Long id, List<Long> equipmentIds);

    void updateItems(Long id, List<RepairTicketItemDTO> items);

    void accept(Long id);

    void updateRepairStatus(Long id, Long repairStatusId);

    String getRepairTicketAcceptActHtmlTemplate(Long id);

    String getRepairTicketDefectActHtmlTemplate(Long id);
}
