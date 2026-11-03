package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.BranchFiscalModule;
import uz.uzkassa.cts.dto.device.BaseDeviceFiscalModuleListDTO;
import uz.uzkassa.cts.dto.device.CompanyTerminalListDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleForTransferListDTO;
import uz.uzkassa.cts.dto.fm.admin.BranchFiscalModuleListAdminDTO;
import uz.uzkassa.cts.dto.fm.admin.FiscalModuleDetailAdminDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CompanyBranchDeviceFilter;
import uz.uzkassa.cts.filter.CompanyDeviceFilter;

import java.util.List;

public interface BranchFiscalModuleService extends BaseService<BranchFiscalModule, Long> {

    Page<BranchFiscalModuleListAdminDTO> getListForAdmin(BaseFilter filter);

    FiscalModuleDetailAdminDTO getForAdmin(Long id);

    List<DeviceStatisticsAdminDTO> getStatisticsForAdmin(BaseFilter filter);

    Page<BaseDeviceFiscalModuleListDTO> list(BaseFilter filter);

    Page<BaseDeviceFiscalModuleListDTO> listByEmployee(BaseFilter filter);

    Page<FiscalModuleForTransferListDTO> getListForTransfer(CompanyDeviceFilter filter);

    Page<SerialNumberDTO> getListForTransfer2(CompanyDeviceFilter filter);

    Page<CompanyTerminalListDTO> listAll(CompanyBranchDeviceFilter filter);
}
