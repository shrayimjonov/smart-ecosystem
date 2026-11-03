package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.OrderProduct;
import uz.uzkassa.silen.dto.filter.OrderFilter;

public interface OrderProductRepositoryCustom {
    Page<OrderProduct> findAllByFilter(OrderFilter filter);
}
