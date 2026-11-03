package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.customer.CustomerRequest;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.CustomerRequestFilter;

@Repository
public interface CustomerRequestRepositoryCustom {

    ResultList<CustomerRequest> getResultList(CustomerRequestFilter filter);
}
