package uz.uzkassa.smartpossupply.crons.quartz;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.ListUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.dto.payload.ScheduleMessagePayload;
import uz.uzkassa.smartpossupply.enums.MailingListItemStatus;
import uz.uzkassa.smartpossupply.enums.ScheduleMessageStatus;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqScheduledMessageProducer;
import uz.uzkassa.smartpossupply.repository.MailingListItemRepository;
import uz.uzkassa.smartpossupply.repository.ScheduleMessageRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ScheduleMessageJob extends QuartzJobBean {

    ScheduleMessageRepository scheduleMessageRepository;
    RabbitMqScheduledMessageProducer scheduledMessageProducer;
    MailingListItemRepository mailingListItemRepository;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        Long scheduledMessageId = (Long) jobDataMap.get("id");
        scheduleMessageRepository.findByIdAndStatus(scheduledMessageId, ScheduleMessageStatus.ACTIVE).ifPresent(scheduledMessage -> {
            ScheduleMessagePayload payload = new ScheduleMessagePayload();
            String subject;
            String message;
            switch (SecurityUtils.getCurrentRequestLanguageEnum()) {
                case cyrillic -> {
                    subject = scheduledMessage.getSubjectCyrillic();
                    message = scheduledMessage.getMessageCyrillic();
                }
                case ru -> {
                    subject = scheduledMessage.getSubjectRu();
                    message = scheduledMessage.getMessageRu();
                }
                default -> {
                    subject = scheduledMessage.getSubjectUz();
                    message = scheduledMessage.getMessageUz();
                }
            }
            payload.setSubject(subject);
            payload.setMessage(message);

            List<Long> mailingListItemIds = mailingListItemRepository.getIdsByMailingListIdExcludingStatus(scheduledMessage.getMailingList().getId(), MailingListItemStatus.SENT);
            List<List<Long>> partitionedIds = ListUtils.partition(mailingListItemIds, 100);
            partitionedIds.forEach(ids -> {
                payload.setMailingListItemIds(new ArrayList<>(ids));
                scheduledMessageProducer.send(payload);
            });
            scheduledMessage.setStatus(ScheduleMessageStatus.SENT);
            scheduleMessageRepository.save(scheduledMessage);
        });
    }
}

