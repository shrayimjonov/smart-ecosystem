package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.CustomerDevice;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.sync.SyncCustomerDeviceListDTO;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

@Repository
public interface CustomerDeviceRepositoryCustom {

    ResultList<SerialNumberDTO> getLookUp(BaseFilter filter);

    ResultList<CustomerDevice> getList(BaseFilter filter);

    List<SyncCustomerDeviceListDTO> getListToSync(BaseFilter filter);

    ResultList<CustomerDevice> getResultList(BaseFilter filter);

    List<DeviceStatisticsAdminDTO> getStatistics(BaseFilter filter);
}
