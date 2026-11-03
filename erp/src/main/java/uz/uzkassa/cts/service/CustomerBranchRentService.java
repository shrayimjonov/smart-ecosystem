package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.CustomerBranchRent;
import uz.uzkassa.cts.dto.customerbranch.rent.CustomerBranchRentDTO;
import uz.uzkassa.cts.enums.CommonStatus;

public interface CustomerBranchRentService extends BaseService<CustomerBranchRent, Long> {

    CustomerBranchRentDTO getByCustomerBranchId(Long customerBranchId);

    Long create(CustomerBranchRentDTO customerBranchRentDTO);

    Long update(Long id, CustomerBranchRentDTO customerBranchRentDTO);

    Long createOrUpdate(CustomerBranchRentDTO customerBranchRentDTO);

    void updateStatus(CommonStatus status, Long customerBranchId);
}
