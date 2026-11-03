package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.CtsServiceOrder;
import uz.uzkassa.cts.dto.order.request.ServiceOrderDTO;
import uz.uzkassa.cts.dto.serviceorder.response.ServiceOrderListResponseDto;
import uz.uzkassa.cts.dto.serviceorder.response.ServiceOrderResponseDto;
import uz.uzkassa.cts.filter.ServiceOrderFilter;

public interface ServiceOrderService extends BaseService<CtsServiceOrder, Long> {

    ServiceOrderDTO create(ServiceOrderDTO serviceOrderDTO);

    ServiceOrderDTO update(ServiceOrderDTO dto);

    ServiceOrderResponseDto getById(Long id);

    Page<ServiceOrderListResponseDto> list(ServiceOrderFilter filter, Long customerId);
}
