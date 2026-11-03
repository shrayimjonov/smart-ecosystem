package uz.uzkassa.smartpossupply.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.OfflineOrder;
import uz.uzkassa.smartpossupply.dto.offlineorder.OfflineOrderDetailDTO;
import uz.uzkassa.smartpossupply.dto.offlineorder.OfflineOrderListDTO;
import uz.uzkassa.smartpossupply.dto.offlineorder.OfflineOrderUpdateDTO;
import uz.uzkassa.smartpossupply.dto.offlineorder.OfflineOfflineOrderUpdateListDTO;
import uz.uzkassa.smartpossupply.dto.payload.Payload;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqOfflineOrderProducer;
import uz.uzkassa.smartpossupply.repository.OfflineOrderRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OfflineOrderService extends BaseService {

    OfflineOrderRepository offlineOrderRepository;
    RabbitMqOfflineOrderProducer rabbitMqOfflineOrderProducer;

    @Transactional
    public List<Long> create(OfflineOrderListDTO offlineOrderListDTO) {
        List<OfflineOrder> offlineOrders = offlineOrderListDTO.getOrders().stream().map(order -> {
            OfflineOrder offlineOrder = new OfflineOrder();
            offlineOrder.setMobileId(order.getMobileId());
            offlineOrder.setOrderJson(order);
            return offlineOrder;
        }).toList();
        List<Long> ids = offlineOrderRepository.saveAll(offlineOrders).stream().map(OfflineOrder::getId).toList();

        ids.forEach(id -> rabbitMqOfflineOrderProducer.createOrder(new Payload(id)));
        return ids;
    }

    @Transactional
    public List<Long> update(OfflineOfflineOrderUpdateListDTO offlineOrderUpdateListDTO) {
        List<OfflineOrder> offlineOrders = offlineOrderUpdateListDTO
                .getOrders()
                .stream()
                .map(orderUpdateDTO -> {
                    OfflineOrder offlineOrder = new OfflineOrder();
                    offlineOrder.setMobileId(orderUpdateDTO.getMobileId());
                    offlineOrder.setOrderId(orderUpdateDTO.getOrderId());
                    offlineOrder.setOrderJson(orderUpdateDTO);
                    return offlineOrder;
                }).toList();

        List<Long> ids = offlineOrderRepository.saveAll(offlineOrders)
                .stream()
                .map(OfflineOrder::getId)
                .toList();

        ids.forEach(id -> rabbitMqOfflineOrderProducer.updateOrder(new Payload(id)));
        return ids;
    }

    public List<OfflineOrderDetailDTO> getByMobileIds(List<String> mobileIds) {
        return offlineOrderRepository.findLastByMobileIds(mobileIds).stream().map(offlineOrder -> {
            OfflineOrderDetailDTO offlineOrderDetailDTO = new OfflineOrderDetailDTO();
            offlineOrderDetailDTO.setId(offlineOrder.getId());
            offlineOrderDetailDTO.setOrderId(offlineOrder.getOrderId());
            offlineOrderDetailDTO.setStatus(offlineOrder.getStatus());
            offlineOrderDetailDTO.setMobileId(offlineOrder.getMobileId());
            offlineOrderDetailDTO.setErrorMessage(offlineOrder.getErrorMessage());
            return offlineOrderDetailDTO;
        }).toList();
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void update(OfflineOrderUpdateDTO offlineOrderUpdateDTO) {
        offlineOrderRepository.findById(offlineOrderUpdateDTO.getId()).ifPresent(offlineOrder -> {
            offlineOrder.setOrderId(offlineOrderUpdateDTO.getOrderId());
            offlineOrder.setStatus(offlineOrderUpdateDTO.getStatus());
            offlineOrder.setErrorMessage(offlineOrderUpdateDTO.getErrorMessage());
            offlineOrderRepository.save(offlineOrder);
        });
    }
}

