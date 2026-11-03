package uz.uzkassa.cts.service;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.CustomerBranchDevice;
import uz.uzkassa.cts.dto.applicationfile.GenerateApplicationFileDTO;
import uz.uzkassa.cts.dto.customerbranchdevice.CustomerBranchDeviceListAdminDTO;
import uz.uzkassa.cts.dto.customerbranchdevice.CustomerBranchDeviceRequestDTO;
import uz.uzkassa.cts.dto.customerbranchdevicechangetariff.*;
import uz.uzkassa.cts.dto.device.*;
import uz.uzkassa.cts.dto.device.admin.DeviceDetailAdminDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.sync.SyncCustomerDeviceListDTO;
import uz.uzkassa.cts.enums.DeviceType;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CustomerBranchDeviceListFilter;

import java.util.List;

public interface CustomerBranchDeviceService extends BaseService<CustomerBranchDevice, Long> {

    void add(CustomerBranchDeviceDTO customerBranchDeviceDTO);

    Page<SerialNumberDTO> lookUp(BaseFilter filter);

    List<SerialNumberDTO> getItems(BaseFilter filter);

    Page<CustomerBranchDeviceListDto> getList(CustomerBranchDeviceListFilter filter);

    Page<CustomerBranchDeviceListDTO> list(BaseFilter filter);

    List<CustomerDeviceListDTO> getFreeDevices(Long customerId);

    Resource downloadApplicationFile(GenerateApplicationFileDTO generateApplicationFileDTO);

    List<CustomerBranchDeviceListDto> getCustomerBranchDevicesByCustomerId(Long id);

    CustomerBranchDeviceListDto changeServiceAllowToTrue(Long customerBranchDeviceId);

    CustomerBranchDeviceListDto changeDevicePaymentType(CustomerBranchDeviceChangePaymentTypeDto paymentTypeDto);

    void delete(Long id);

    Page<BaseDeviceFiscalModuleDTO> listByCustomerBranch(BaseFilter filter);

    CustomerBranchDeviceListDto suspend(Long customerBranchDeviceId);

    CustomerBranchDeviceListDto activate(Long customerBranchDeviceId);

    Long getListCountToSync();

    Page<SyncCustomerDeviceListDTO> getListToSync(BaseFilter filter);

    void transferDeviceToOtherBranch(CustomerBranchDeviceTransferDTO customerBranchDeviceTransferDTO);

    CustomerBranchDeviceTariffDto getCustomerDeviceById(Long customerId, Long deviceId);

    String getTariffByCustomerAndDevice(Long customerId, String serialNumber);

    CustomerBranchDeviceListDto setFreePeriodDate(CustomerBranchDeviceFreePeriodDto dto);

    void setFreePeriodDateByCustomerId(CustomerBranchDeviceFreePeriodByCustomerIdDto dto);

    void importExcel(MultipartFile file, String paymentStatus, String typeTin);

    void changePaymentStatus(CustomerBranchDeviceRequestDTO dto);

    void updatePaymentStatus(CustomerBranchDeviceExcelDTO data);

    Long getTariffId(String tin, String serialNumber);

    void changeDeviceType(Long customerId, Long deviceId, DeviceType deviceType);

    Page<CustomerBranchDeviceListAdminDTO> getListForAdmin(BaseFilter filter);

    DeviceDetailAdminDTO getForAdmin(Long id);

    List<DeviceStatisticsAdminDTO> getStatisticsForAdmin(BaseFilter filter);
}
