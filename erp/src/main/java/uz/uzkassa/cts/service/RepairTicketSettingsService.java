package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.RepairTicketSettings;
import uz.uzkassa.cts.dto.repairticket.RepairTicketSettingsCommonDTO;
import uz.uzkassa.cts.dto.repairticket.RepairTicketSettingsDTO;
import uz.uzkassa.cts.dto.repairticket.RepairTicketSettingsDetailDTO;
import uz.uzkassa.cts.dto.repairticket.RepairTicketSettingsListDTO;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface RepairTicketSettingsService extends BaseService<RepairTicketSettings, Long> {

    Long create(RepairTicketSettingsDTO repairTicketSettingsDTO);

    RepairTicketSettingsDetailDTO get(Long id);

    Page<RepairTicketSettingsListDTO> getList(BaseFilter baseFilter);

    List<RepairTicketSettingsCommonDTO> lookUp(BaseFilter filter);
}
