package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.Bonus;
import uz.uzkassa.cts.dto.bonus.BillingBonusWebhookDTO;
import uz.uzkassa.cts.dto.bonus.BonusListDTO;
import uz.uzkassa.cts.dto.bonus.BonusListDetailByEmployeeDTO;
import uz.uzkassa.cts.dto.bonus.BonusStatisticsDTO;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface BonusService extends BaseService<Bonus, Long> {

    Page<BonusListDTO> getList(BaseFilter filter);

    Page<BonusListDetailByEmployeeDTO> getListByEmployee(Long employeeId, BaseFilter filter);

    BonusStatisticsDTO getStatistic(BaseFilter filter);

    void createBatch(List<BillingBonusWebhookDTO> bonusDTOs);
}
