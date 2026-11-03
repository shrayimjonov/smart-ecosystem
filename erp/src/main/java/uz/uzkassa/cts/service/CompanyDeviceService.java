package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.CompanyDevice;
import uz.uzkassa.cts.dto.companydevice.admin.CompanyDeviceListAdminDTO;
import uz.uzkassa.cts.dto.device.CompanyDeviceListNewDTO;
import uz.uzkassa.cts.dto.device.CompanyTerminalListDTO;
import uz.uzkassa.cts.dto.device.DeviceForTransferListDTO;
import uz.uzkassa.cts.dto.device.DeviceTransferDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceDetailAdminDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.filter.SyncDeviceFilter;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CompanyBranchDeviceFilter;
import uz.uzkassa.cts.filter.CompanyDeviceFilter;
import uz.uzkassa.cts.filter.CompanyDeviceListFilter;

import java.util.List;

public interface CompanyDeviceService extends BaseService<CompanyDevice, Long> {

    Page<DeviceForTransferListDTO> listForTransfer(CompanyDeviceFilter filter);

    void sync(SyncDeviceFilter syncDeviceFilter);

    void transfer(DeviceTransferDTO deviceTransferDTO);

    Page<CompanyDeviceListNewDTO> list(CompanyDeviceListFilter filter);

    void deleteByDeviceId(Long deviceId);

    Page<CompanyTerminalListDTO> listAll(CompanyBranchDeviceFilter filter);

    Page<CompanyDeviceListAdminDTO> getListForAdmin(BaseFilter filter);

    DeviceDetailAdminDTO getForAdmin(Long id);

    List<DeviceStatisticsAdminDTO> getStatisticsForAdmin(BaseFilter filter);
}
