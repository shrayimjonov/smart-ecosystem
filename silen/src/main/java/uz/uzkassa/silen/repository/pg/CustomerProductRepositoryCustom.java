package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.CustomerProduct;
import uz.uzkassa.silen.dto.filter.CustomerFilter;

public interface CustomerProductRepositoryCustom {
    Page<CustomerProduct> findAllByFilter(CustomerFilter filter);
}
