package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.payload.AttendancePayload;
import uz.uzkassa.cts.dto.payload.Payload;

@Component
public class RabbitMqAttendanceProducer extends RabbitMqBaseProducer {

    public void create(AttendancePayload payload) {
        sendToQueue(payload, ATTENDANCE_QUEUE, ATTENDANCE_PAYLOAD, 5);
    }

    public void updateItem(Payload payload) {
        sendToQueue(payload, ATTENDANCE_ITEM_QUEUE, ATTENDANCE_ITEM_PAYLOAD, 5);
    }
}
