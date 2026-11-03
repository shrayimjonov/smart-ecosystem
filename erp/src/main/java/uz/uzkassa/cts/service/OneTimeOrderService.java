package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.OneTimeOrder;
import uz.uzkassa.cts.dto.onetimeorder.OneTimeOrderListDTO;
import uz.uzkassa.cts.filter.OneTimeOrderFilter;

public interface OneTimeOrderService extends BaseService<OneTimeOrder, Long> {

    Page<OneTimeOrderListDTO> getList(OneTimeOrderFilter filter);
}