package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.CustomerBranch;
import uz.uzkassa.cts.domain.Device;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.dto.payload.SmartPosRemoveDevicePayload;
import uz.uzkassa.cts.dto.smartpos.SmartposFiscalizationDetailDTO;
import uz.uzkassa.cts.dto.smartpos.company.CustomerCtoDTO;
import uz.uzkassa.cts.dto.sms.ReceiptSmsRequestDTO;
import uz.uzkassa.cts.filter.DeviceModelFilter;

import java.util.List;

public interface SmartposService extends BaseService<Device, Long> {

    void fiscalSendSms(ReceiptSmsRequestDTO receiptSmsRequestDTO);

    SmartposFiscalizationDetailDTO getFiscalizationDetail(DeviceModelFilter deviceModelFilter);

    SmartposFiscalizationDetailDTO update(Customer customer, CustomerBranch customerBranch);

    SmartposFiscalizationDetailDTO updateCustomer(Long customerId, Long customerBranchId);

    void updateCustomer(Long fiscalizationId);

    void updateCustomerByType(Long customerBranchDeviceId);

    CustomerCtoDTO getCustomerDetails(Long customerId);

    boolean getDeviceStatusBySerialNumber(String serialNumber);

    void removeDevice(SmartPosRemoveDevicePayload removeKKMPayload);

    void syncCustomerBranch(Long customerBranchId);

    void attachManager(List<String> tins, User user);
}
