package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.CustomerFiscalModule;
import uz.uzkassa.cts.dto.device.CustomerBranchDeviceListDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.fm.CustomerFiscalModuleDTO;
import uz.uzkassa.cts.dto.fm.CustomerFiscalModuleRegisterDTO;
import uz.uzkassa.cts.dto.fm.admin.CustomerFiscalModuleListAdminDTO;
import uz.uzkassa.cts.dto.fm.admin.FiscalModuleDetailAdminDTO;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface CustomerFiscalModuleService extends BaseService<CustomerFiscalModule, Long> {

    Page<CustomerFiscalModuleListAdminDTO> getListForAdmin(BaseFilter filter);

    FiscalModuleDetailAdminDTO getForAdmin(Long id);

    List<DeviceStatisticsAdminDTO> getStatisticsForAdmin(BaseFilter filter);

    Page<CustomerBranchDeviceListDTO> list(BaseFilter filter);

    Page<SerialNumberDTO> lookUp(BaseFilter filter);

    void delete(Long id);

    void deleteByFiscalModuleId(Long fiscalModuleId);

    void register(CustomerFiscalModuleRegisterDTO customerFiscalModuleRegisterDTO);

    void add(CustomerFiscalModuleDTO customerFiscalModuleDTO);

    String getHtmlTemplate(Long id);
}
