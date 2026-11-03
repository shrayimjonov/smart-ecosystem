package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.CustomerBranchFiscalModule;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.BaseDeviceFiscalModuleDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

@Repository
public interface CustomerBranchFiscalModuleRepositoryCustom {
    ResultList<SerialNumberDTO> getLookUp(BaseFilter filter);

    List<SerialNumberDTO> getItems(BaseFilter filter);

    ResultList<CustomerBranchFiscalModule> getList(BaseFilter filter);

    ResultList<BaseDeviceFiscalModuleDTO> getListByCustomerBranch(BaseFilter filter);

    ResultList<CustomerBranchFiscalModule> getResultList(BaseFilter filter);

    List<DeviceStatisticsAdminDTO> getStatistics(BaseFilter filter);
}
