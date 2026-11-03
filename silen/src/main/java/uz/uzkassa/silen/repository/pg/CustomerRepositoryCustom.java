package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.Customer;
import uz.uzkassa.silen.dto.filter.CustomerFilter;

public interface CustomerRepositoryCustom {

    Page<Customer> findAllByFilter(CustomerFilter filter);

}
