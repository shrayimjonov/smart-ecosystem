package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.dto.tariff.TariffTotalDTO;
import uz.uzkassa.cts.filter.EmployeeReportFilter;

import java.util.List;

@Repository
public interface TariffRepositoryCustom {

    List<TariffTotalDTO> listByEmployee(EmployeeReportFilter filter);
}
