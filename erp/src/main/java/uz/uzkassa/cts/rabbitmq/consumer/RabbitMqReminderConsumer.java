package uz.uzkassa.cts.rabbitmq.consumer;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.config.ApplicationProperties;
import uz.uzkassa.cts.constants.MqConstants;
import uz.uzkassa.cts.domain.Notification;
import uz.uzkassa.cts.dto.email.EmailDTO;
import uz.uzkassa.cts.dto.payload.ReminderPayload;
import uz.uzkassa.cts.dto.sms.EtcRequestDTO;
import uz.uzkassa.cts.repository.EmployeeRepository;
import uz.uzkassa.cts.repository.NotificationRepository;
import uz.uzkassa.cts.service.EmailService;
import uz.uzkassa.cts.service.PushKassaNotificationService;
import uz.uzkassa.cts.service.SmsService;

import static uz.uzkassa.cts.constants.Constants.PUSH_NOTIFICATION;
import static uz.uzkassa.cts.constants.Constants.PUSH_SERVICE_TAG;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class RabbitMqReminderConsumer implements MqConstants {

    EmployeeRepository employeeRepository;
    ApplicationProperties applicationProperties;
    SmsService smsService;
    EmailService emailService;
    PushKassaNotificationService pushKassaNotificationService;
    NotificationRepository notificationRepository;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false",
            value = SEND_REMINDER_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = SEND_REMINDER_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMax")
    public void sendReminder(ReminderPayload payload) {
        try {
            switch (payload.getChannelType()) {
                case SMS -> employeeRepository.getPhones(payload.getParticipantIds()).forEach(phone -> {
                    EtcRequestDTO etcRequestDTO = new EtcRequestDTO();
                    etcRequestDTO.setText(payload.getMessage());
                    etcRequestDTO.setPhone(phone);
                    smsService.send(etcRequestDTO);
                });

                case EMAIL -> employeeRepository.getEmails(payload.getParticipantIds()).forEach(email ->
                        emailService.send(EmailDTO.builder()
                                .subject(payload.getSubject())
                                .message(payload.getMessage())
                                .from(applicationProperties.getEmailProperty().getFrom())
                                .to(email)
                                .build())
                );

                case NOTIFICATION ->
                        notificationRepository.saveAll(payload.getParticipantIds().stream().map(employeeId -> {
                            Notification notification = new Notification();
                            notification.setMessageRu(payload.getMessage());
                            notification.setMessageUzLatin(payload.getMessage());
                            notification.setMessageUzCyrillic(payload.getMessage());
                            notification.setEntityId(payload.getEntityId());
                            notification.setEntityType(payload.getEntityType());
                            notification.setCompanyId(payload.getCompanyId());
                            notification.setReceiverId(employeeId);
                            return notification;
                        }).toList());

                case PUSH ->
                        payload.getParticipantIds().forEach(employeeId -> pushKassaNotificationService.sendNotificationToSession(payload.getMessage(), PUSH_SERVICE_TAG, PUSH_NOTIFICATION, employeeId.toString()));
            }
        } catch (Exception e) {
            log.error("Send reminder queue error: ", e);
            throw new AmqpRejectAndDontRequeueException("Send reminder queue error: " + e.getMessage());
        }
    }
}
