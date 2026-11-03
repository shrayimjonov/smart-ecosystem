package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Bonus;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.bonus.BonusListDTO;
import uz.uzkassa.cts.dto.bonus.BonusListDetailByEmployeeDTO;
import uz.uzkassa.cts.dto.bonus.BonusStatisticsDTO;
import uz.uzkassa.cts.dto.bonus.BonusSummaryExcelExportDTO;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;
import java.util.Optional;

@Repository
public interface BonusRepositoryCustom {

    ResultList<BonusListDTO> getResultList(BaseFilter filter);

    ResultList<BonusListDetailByEmployeeDTO> getResultListByEmployee(BaseFilter filter);

    Optional<BonusStatisticsDTO> getStatistic(BaseFilter filter);

    List<BonusSummaryExcelExportDTO> getBonusSummary(BaseFilter filter);

    List<String> getNotExistBillingIds(String billingIds);

    List<Bonus> findAll(BaseFilter filter);
}
