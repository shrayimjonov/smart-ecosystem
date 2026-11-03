package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.CompanyFiscalModule;
import uz.uzkassa.cts.dto.device.BaseDeviceFiscalModuleListDTO;
import uz.uzkassa.cts.dto.device.CompanyTerminalListDTO;
import uz.uzkassa.cts.dto.device.DeviceTransferDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleForTransferListDTO;
import uz.uzkassa.cts.dto.fm.admin.CompanyFiscalModuleListAdminDTO;
import uz.uzkassa.cts.dto.fm.admin.FiscalModuleDetailAdminDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CompanyBranchDeviceFilter;
import uz.uzkassa.cts.filter.CompanyDeviceFilter;

import java.util.List;

public interface CompanyFiscalModuleService extends BaseService<CompanyFiscalModule, Long> {

    Page<BaseDeviceFiscalModuleListDTO> list(BaseFilter filter);

    Page<CompanyTerminalListDTO> listAll(CompanyBranchDeviceFilter filter);

    void transfer(DeviceTransferDTO deviceTransferDTO);

    Page<FiscalModuleForTransferListDTO> listForTransfer(CompanyDeviceFilter filter);

    Page<CompanyFiscalModuleListAdminDTO> getListForAdmin(BaseFilter filter);

    FiscalModuleDetailAdminDTO getForAdmin(Long id);

    List<DeviceStatisticsAdminDTO> getStatisticsForAdmin(BaseFilter filter);
}
