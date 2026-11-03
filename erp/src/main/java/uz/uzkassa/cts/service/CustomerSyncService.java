package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.dto.sync.customer.CustomerSyncDTO;
import uz.uzkassa.cts.enums.RelationType;
import uz.uzkassa.cts.filter.CustomerFilter;

public interface CustomerSyncService extends BaseService<Customer, Long> {

    Page<CustomerSyncDTO> getList(CustomerFilter filter);

    CustomerSyncDTO get(String tin);

    Long sync(CustomerSyncDTO customerSyncDTO);

    Long sync(Long id, RelationType relationType);
}
