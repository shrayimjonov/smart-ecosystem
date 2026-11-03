package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.FiscalModule;
import uz.uzkassa.cts.dto.device.CompanyTerminalListDTO;
import uz.uzkassa.cts.dto.device.RegistryListDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleForTransferListDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleListDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleRegisterDTO;
import uz.uzkassa.cts.dto.fm.admin.FiscalModuleDetailAdminDTO;
import uz.uzkassa.cts.dto.fm.admin.FiscalModuleListAdminDTO;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.filter.*;

import java.util.List;

public interface FiscalModuleService extends BaseService<FiscalModule, Long> {

    Page<SerialNumberDTO> lookUp(FiscalModuleLookUpFilter filter);

    FiscalModuleRegisterDTO add(FiscalModuleRegisterDTO fiscalModuleRegisterDTO);

    void batchAdd(List<SerialNumberDTO> fiscalModules);

    Page<FiscalModuleListDTO> list(BaseFilter filter);

    Long importFiscalModule(MultipartFile file);

    void updateStatusAndClear(FiscalModule fiscalModule, DeviceStatus status);

    void updateStatus(FiscalModule fiscalModule, DeviceStatus status);

    Page<FiscalModuleForTransferListDTO> getListForTransfer(CompanyDeviceFilter filter);

    Page<CompanyTerminalListDTO> listAll(CompanyBranchDeviceFilter filter);

    Page<RegistryListDTO> listRegister(CompanyDeviceListFilter filter);

    Long create(FiscalModuleDTO fiscalModuleDTO);

    Page<FiscalModuleListAdminDTO> getListForAdmin(BaseFilter filter);

    FiscalModuleDetailAdminDTO getForAdmin(Long id);

    List<DeviceStatisticsAdminDTO> getStatisticsForAdmin(BaseFilter filter);
}
