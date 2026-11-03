package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.Order;
import uz.uzkassa.silen.dto.filter.Filter;

public interface OrderRepositoryCustom {
    Page<Order> findAllByFilter(Filter filter);
}
