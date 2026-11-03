package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.CustomerFiscalModule;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

@Repository
public interface CustomerFiscalModuleRepositoryCustom {

    ResultList<CustomerFiscalModule> getList(BaseFilter filter);

    ResultList<SerialNumberDTO> getLookUp(BaseFilter filter);

    ResultList<CustomerFiscalModule> getResultList(BaseFilter filter);

    List<DeviceStatisticsAdminDTO> getStatistics(BaseFilter filter);
}
