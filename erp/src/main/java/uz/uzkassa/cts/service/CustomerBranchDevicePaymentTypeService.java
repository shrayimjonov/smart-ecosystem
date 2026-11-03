package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.CustomerBranchDevicePaymentType;
import uz.uzkassa.cts.dto.customerBranchDevicePaymentType.CustomerBranchDevicePaymentTypeDTO;

public interface CustomerBranchDevicePaymentTypeService extends BaseService<CustomerBranchDevicePaymentType, Long> {

    void create(Long customerBranchDeviceId);

    void update(CustomerBranchDevicePaymentTypeDTO customerBranchDevicePaymentTypeDTO);
}
