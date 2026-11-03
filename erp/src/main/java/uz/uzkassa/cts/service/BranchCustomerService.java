package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.dto.customer.CustomerLookUpDTO;
import uz.uzkassa.cts.filter.BaseFilter;

public interface BranchCustomerService extends BaseService<Customer, Long> {

    Page<CustomerLookUpDTO> lookUp(BaseFilter filter);

}
