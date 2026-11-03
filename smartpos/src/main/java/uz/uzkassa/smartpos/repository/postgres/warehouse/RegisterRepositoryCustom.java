package uz.uzkassa.smartpos.repository.postgres.warehouse;

import org.springframework.data.domain.Page;
import uz.uzkassa.smartpos.dto.ReportDTO;
import uz.uzkassa.smartpos.dto.analytics.RegisterStatsDTO;
import uz.uzkassa.smartpos.dto.filter.RegisterFilter;
import uz.uzkassa.smartpos.dto.warehouse.RegisterDto;

import java.util.List;

public interface RegisterRepositoryCustom {
    Page<RegisterDto> findAllByFilter(RegisterFilter filter);

    List<RegisterDto> findByProduct(RegisterFilter filter);

    RegisterStatsDTO getStats(RegisterFilter filter);

    List<ReportDTO> findAllByBranchIdAndCityId(Long branchId, Long cityId);
}
