package uz.uzkassa.smartpossupply.rabbitmq.consumer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.dto.payload.PartyUtilisationPayload;
import uz.uzkassa.smartpossupply.enums.CodeType;
import uz.uzkassa.smartpossupply.service.AggregationBlockService;
import uz.uzkassa.smartpossupply.service.MarkService;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class RabbitMqPartyUtilisationConsumer implements MqConstants {

    MarkService markService;
    AggregationBlockService aggregationBlockService;

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(durable = "false", value = PARTY_UITILISATION_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = PARTY_UITILISATION_QUEUE
        ),
        containerFactory = "rabbitListenerContainerFactoryMax"
    )
    public void createActProductListQueue(PartyUtilisationPayload payload) {
        try {
            if (CodeType.BLOCK.equals(payload.getCodeType())) {
                aggregationBlockService.setAppliedByParty(payload);
            } else if (CodeType.MARK.equals(payload.getCodeType())) {
                markService.setAppliedByParty(payload);
            }
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Creating act product list error: " + e.getMessage());
        }
    }
}
