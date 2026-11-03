package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.customer.CustomerRequest;
import uz.uzkassa.cts.dto.customerRequest.CustomerRequestDTO;
import uz.uzkassa.cts.dto.customerRequest.admin.CustomerRequestDetailAdminDTO;
import uz.uzkassa.cts.dto.customerRequest.admin.CustomerRequestListAdminDTO;
import uz.uzkassa.cts.enums.CallStatus;
import uz.uzkassa.cts.enums.CustomerRequestStatus;
import uz.uzkassa.cts.filter.CustomerRequestFilter;

public interface CustomerRequestService extends BaseService<CustomerRequest, Long> {

    Long create(CustomerRequestDTO customerRequestDTO);

    Long update(Long id, CustomerRequestDTO customerRequestDTO);

    Page<CustomerRequestListAdminDTO> getList(CustomerRequestFilter filter);

    CustomerRequestDetailAdminDTO get(Long id);

    void updateCallStatus(Long id, CallStatus callStatus);

    void updateStatus(Long id, CustomerRequestStatus status);
}
