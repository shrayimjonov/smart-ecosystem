package uz.uzkassa.cts.repository.custom;

import uz.uzkassa.cts.domain.RepairTicketSettings;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.repairticket.RepairTicketSettingsListDTO;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface RepairTicketSettingsRepositoryCustom {

    ResultList<RepairTicketSettings> getResultList(BaseFilter filter);
}
