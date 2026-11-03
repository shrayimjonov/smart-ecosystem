package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.CustomerDevice;
import uz.uzkassa.cts.dto.customer.admin.CustomerDevicesByDeviceTypeDTO;
import uz.uzkassa.cts.dto.customerdevice.CustomerDeviceListAdminDTO;
import uz.uzkassa.cts.dto.device.CustomerBranchDeviceListDTO;
import uz.uzkassa.cts.dto.device.CustomerDeviceCheckDTO;
import uz.uzkassa.cts.dto.device.CustomerDeviceDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceDetailAdminDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.sync.SyncCustomerDeviceListDTO;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface CustomerDeviceService extends BaseService<CustomerDevice, Long> {

    String register(CustomerDeviceDTO customerDeviceDTO);

    CustomerDeviceCheckDTO getPaidDeviceCount(String tin);

    Page<SerialNumberDTO> lookUp(BaseFilter filter);

    Page<CustomerBranchDeviceListDTO> list(BaseFilter filter);

    void delete(Long id);

    void deleteByDeviceId(Long deviceId);

    Long getListCountToSync();

    Page<SyncCustomerDeviceListDTO> getListToSync(BaseFilter filter);

    Page<CustomerDeviceListAdminDTO> getListForAdmin(BaseFilter filter);

    DeviceDetailAdminDTO getForAdmin(Long id);

    List<DeviceStatisticsAdminDTO> getStatisticsForAdmin(BaseFilter filter);

    List<CustomerDevicesByDeviceTypeDTO> getActiveDeviceCountByTin(String tin, String pinfl);
}
