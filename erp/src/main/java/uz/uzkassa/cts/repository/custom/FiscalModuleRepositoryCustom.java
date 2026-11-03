package uz.uzkassa.cts.repository.custom;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.FiscalModule;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.CompanyTerminalListDTO;
import uz.uzkassa.cts.dto.device.RegistryListDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleForTransferListDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleListDTO;
import uz.uzkassa.cts.filter.*;

import java.util.List;

@Repository
public interface FiscalModuleRepositoryCustom {
    Page<RegistryListDTO> listRegister(CompanyDeviceListFilter filter);

    ResultList<SerialNumberDTO> getLookUp(FiscalModuleLookUpFilter filter);

    ResultList<FiscalModuleListDTO> getList(BaseFilter filter);

    ResultList<FiscalModuleForTransferListDTO> getListForTransfer(CompanyDeviceFilter filter);

    ResultList<CompanyTerminalListDTO> getListAll(CompanyBranchDeviceFilter filter);

    ResultList<FiscalModule> getResultList(BaseFilter filter);

    List<DeviceStatisticsAdminDTO> getStatistics(BaseFilter filter);
}
