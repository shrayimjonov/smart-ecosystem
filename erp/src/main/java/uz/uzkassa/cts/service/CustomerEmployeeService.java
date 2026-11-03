package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.CustomerEmployee;
import uz.uzkassa.cts.dto.customeremployee.CustomerEmployeeDTO;
import uz.uzkassa.cts.dto.customeremployee.CustomerEmployeeListDTO;

import java.util.List;

public interface CustomerEmployeeService extends BaseService<CustomerEmployee, Long> {
    Long create(CustomerEmployeeDTO customerEmployeeDTO);

    Long update(Long id, CustomerEmployeeDTO customerEmployeeDTO);

    List<CustomerEmployeeListDTO> getList(Long customerId);
}
