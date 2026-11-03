package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.CompanyFiscalModule;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.BaseDeviceFiscalModuleListDTO;
import uz.uzkassa.cts.dto.device.CompanyTerminalListDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleForTransferListDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CompanyBranchDeviceFilter;
import uz.uzkassa.cts.filter.CompanyDeviceFilter;

import java.util.List;

@Repository
public interface CompanyFiscalModuleRepositoryCustom {

    ResultList<BaseDeviceFiscalModuleListDTO> getList(BaseFilter filter);

    ResultList<CompanyTerminalListDTO> getListAll(CompanyBranchDeviceFilter filter);

    ResultList<FiscalModuleForTransferListDTO> getListForTransfer(CompanyDeviceFilter filter);

    ResultList<CompanyFiscalModule> getResultList(BaseFilter filter);

    List<DeviceStatisticsAdminDTO> getStatistics(BaseFilter filter);
}
