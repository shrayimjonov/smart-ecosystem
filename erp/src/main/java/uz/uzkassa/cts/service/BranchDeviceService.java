package uz.uzkassa.cts.service;


import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.BranchDevice;
import uz.uzkassa.cts.dto.branchdevice.BranchDeviceListAdminDTO;
import uz.uzkassa.cts.dto.device.*;
import uz.uzkassa.cts.dto.device.admin.DeviceDetailAdminDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.sync.SyncBranchDeviceListDTO;
import uz.uzkassa.cts.filter.*;

import java.util.List;

public interface BranchDeviceService extends BaseService<BranchDevice, Long> {

    void add(BranchDeviceDTO branchDeviceDTO);

    Page<SerialNumberDTO> lookUp(BaseFilter filter);

    Page<BaseDeviceFiscalModuleListDTO> list(BaseFilter filter);

    Page<DeviceForTransferListDTO> getListForTransfer(CompanyDeviceFilter filter);

    Page<SerialNumberDTO> getListForTransfer2(CompanyDeviceFilter filter);

    List<BranchDevice> freeDevices(Long id, List<String> serialNumbers);

    void deleteByDeviceId(Long deviceId);

    Page<CompanyTerminalListDTO> listAll(CompanyBranchDeviceFilter filter);

    Page<BaseDeviceFiscalModuleListDTO> listByEmployee(BaseFilter filter);

    Long getListCountToSync();

    Page<SyncBranchDeviceListDTO> getListToSync(BaseFilter filter);

    DeviceDetailAdminDTO getForAdmin(Long id);

    Page<BranchDeviceListAdminDTO> getListForAdmin(BaseFilter filter);

    List<DeviceStatisticsAdminDTO> getStatisticsForAdmin(BaseFilter filter);
}
