package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.PartyUtilisationPayload;

@Component
public class RabbitMqPartyUtilisationProducer extends RabbitMqBaseProducer {

    public void sendParty(PartyUtilisationPayload payload) {
        sendToQueue(payload, PARTY_UITILISATION_QUEUE, PARTY_UITILISATION_PAYLOAD);
    }
}
