package uz.uzkassa.cts.repository.custom;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Device;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.*;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.sync.SyncDeviceListDTO;
import uz.uzkassa.cts.filter.*;

import java.util.List;

@Repository
public interface DeviceRepositoryCustom {

    ResultList<Device> getResultList(BaseFilter filter);

    List<DeviceStatisticsAdminDTO> getStatistics(BaseFilter filter);

    Page<RegistryListDTO> listRegister(CompanyDeviceListFilter filter);

    Page<DeviceForTransferListDTO> getListForTransfer(CompanyDeviceFilter filter);

    Page<CompanyTerminalListDTO> listAll(CompanyBranchDeviceFilter filter);

    Page<DeviceDetailDTO> list(BaseFilter filter);

    List<Device> getSyncItems(SyncDeviceFilter syncDeviceFilter);

    Long getDeviceTotalByModel(Long modelId);

    Page<SerialNumberDTO> resultLookUp(DeviceLookUpFilter filter);

    List<SyncDeviceListDTO> getListToSync(BaseFilter filter);
}
