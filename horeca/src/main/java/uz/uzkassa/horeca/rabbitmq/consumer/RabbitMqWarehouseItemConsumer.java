package uz.uzkassa.horeca.rabbitmq.consumer;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.horeca.constants.MqConstants;
import uz.uzkassa.horeca.dto.payload.WarehouseItemPayload;
import uz.uzkassa.horeca.repository.OrderRepository;
import uz.uzkassa.horeca.service.WarehouseItemService;

import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RabbitMqWarehouseItemConsumer implements MqConstants {

    WarehouseItemService warehouseItemService;
    OrderRepository orderRepository;

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(durable = "false", value = WAREHOUSE_ITEM_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = WAREHOUSE_ITEM_QUEUE
        ),
        containerFactory = "rabbitListenerContainerFactoryMin"
    )
    public void stockOut(WarehouseItemPayload warehouseItemPayload) {
        try {
            //orderRepository.findById(warehouseItemPayload.getOrderId()).ifPresent(order -> warehouseItemService.out(order));
        } catch (Exception e) {
            log.error("Stock out by order queue error", e);
            throw new AmqpRejectAndDontRequeueException("Stock out by order queue error:" + e.getMessage());
        }
    }
}
