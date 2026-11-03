package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.Device;
import uz.uzkassa.cts.dto.device.*;
import uz.uzkassa.cts.dto.device.admin.DeviceDetailAdminDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceListAdminDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.sync.SyncDeviceListDTO;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.filter.*;

import java.util.List;

public interface DeviceService extends BaseService<Device, Long> {

    Long create(DeviceDTO deviceDTO);

    Page<DeviceListAdminDTO> getListForAdmin(BaseFilter filter);

    DeviceDetailAdminDTO getForAdmin(Long id);

    List<DeviceStatisticsAdminDTO> getStatisticsForAdmin(BaseFilter filter);

    void batchAdd(List<DeviceDTO> deviceDTOList);

    //DeviceDetailDTO update(DeviceDTO deviceDTO);

    //DeviceDetailDTO get(Long id);

    Page<DeviceDetailDTO> list(BaseFilter filter);

    List<Device> getSyncItems(SyncDeviceFilter syncDeviceFilter);

    Page<SerialNumberDTO> lookUp(DeviceLookUpFilter filter);

    Long importDevices(MultipartFile file);

    Long getDeviceTotalByModel(Long modelId);

    void updateStatus(Device device, DeviceStatus status);

    void updateStatus(Long id, String status, MultipartFile file);

    //void deleteById(Long id);

    Page<DeviceForTransferListDTO> getListForTransfer(CompanyDeviceFilter filter);

    Page<CompanyTerminalListDTO> listAll(CompanyBranchDeviceFilter filter);

    Page<RegistryListDTO> listRegister(CompanyDeviceListFilter filter);

    Long getListCountToSync();

    Page<SyncDeviceListDTO> getListToSync(BaseFilter filter);
}
