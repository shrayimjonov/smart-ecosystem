package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.BranchDevice;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.BaseDeviceFiscalModuleListDTO;
import uz.uzkassa.cts.dto.device.CompanyTerminalListDTO;
import uz.uzkassa.cts.dto.device.DeviceForTransferListDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.sync.SyncBranchDeviceListDTO;
import uz.uzkassa.cts.filter.*;

import java.util.List;

@Repository
public interface BranchDeviceRepositoryCustom {

    ResultList<SerialNumberDTO> getLookUp(BaseFilter filter);

    ResultList<BaseDeviceFiscalModuleListDTO> getList(BaseFilter filter);

    ResultList<DeviceForTransferListDTO> getListForTransfer(CompanyDeviceFilter filter);

    List<SerialNumberDTO> getListForTransfer2(CompanyDeviceFilter filter);

    List<BranchDevice> freeDevices(Long id, List<String> serialNumbers);

    ResultList<CompanyTerminalListDTO> getListAll(CompanyBranchDeviceFilter filter);

    ResultList<BaseDeviceFiscalModuleListDTO> getListByEmployee(BaseFilter filter);

    List<SyncBranchDeviceListDTO> getListToSync(BaseFilter filter);

    ResultList<BranchDevice> getResultList(BaseFilter filter);

    List<DeviceStatisticsAdminDTO> getStatistics(BaseFilter filter);
}
