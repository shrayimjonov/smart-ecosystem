package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.CustomerBranchFiscalModule;
import uz.uzkassa.cts.dto.device.BaseDeviceFiscalModuleDTO;
import uz.uzkassa.cts.dto.device.CustomerBranchDeviceListDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleAddDTO;
import uz.uzkassa.cts.dto.fm.admin.CustomerBranchFiscalModuleListAdminDTO;
import uz.uzkassa.cts.dto.fm.admin.FiscalModuleDetailAdminDTO;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface CustomerBranchFiscalModuleService extends BaseService<CustomerBranchFiscalModule, Long> {

    Page<CustomerBranchFiscalModuleListAdminDTO> getListForAdmin(BaseFilter filter);

    FiscalModuleDetailAdminDTO getForAdmin(Long id);

    List<DeviceStatisticsAdminDTO> getStatisticsForAdmin(BaseFilter filter);

    void add(FiscalModuleAddDTO fiscalModuleDTO);

    void addNew(FiscalModuleAddDTO fiscalModuleDTO);

    Page<SerialNumberDTO> lookUp(BaseFilter filter);

    List<SerialNumberDTO> getItems(BaseFilter filter);

    Page<CustomerBranchDeviceListDTO> list(BaseFilter filter);

    void deleteByFiscalModuleId(Long fiscalModuleId);

    void delete(Long id);

    Page<BaseDeviceFiscalModuleDTO> listByCustomerBranch(BaseFilter filter);
}
