package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.CustomerBranchApplication;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.promomanagardashboard.CountByDateAndRegionDTO;
import uz.uzkassa.cts.dto.promomanagardashboard.TopRegionsByCountDTO;
import uz.uzkassa.cts.filter.BaseFilter;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomerBranchApplicationRepositoryCustom {

    ResultList<CustomerBranchApplication> getResultList(BaseFilter filter);

    List<CountByDateAndRegionDTO> getCustomerApplicationCountByDate(LocalDateTime from, LocalDateTime to);

    List<TopRegionsByCountDTO> getTopRegionsByApplications(BaseFilter filter);

}
