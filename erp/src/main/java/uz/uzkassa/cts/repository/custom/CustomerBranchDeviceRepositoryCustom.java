package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.CustomerBranchDevice;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.BaseDeviceFiscalModuleDTO;
import uz.uzkassa.cts.dto.device.CustomerDeviceListDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.sync.SyncCustomerDeviceListDTO;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

@Repository
public interface CustomerBranchDeviceRepositoryCustom {
    ResultList<CustomerBranchDevice> getList(BaseFilter filter);

    ResultList<SerialNumberDTO> getLookUp(BaseFilter filter);

    List<SerialNumberDTO> getItems(BaseFilter filter);

    List<CustomerDeviceListDTO> getFreeDevices(Long customerId);

    ResultList<BaseDeviceFiscalModuleDTO> getListByCustomerBranch(BaseFilter filter);

    List<SyncCustomerDeviceListDTO> getListToSync(BaseFilter filter);

    ResultList<CustomerBranchDevice> getResultList(BaseFilter filter);

    List<DeviceStatisticsAdminDTO> getStatistics(BaseFilter filter);
}
