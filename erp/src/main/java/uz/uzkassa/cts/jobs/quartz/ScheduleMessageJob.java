package uz.uzkassa.cts.jobs.quartz;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.ListUtils;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.dto.payload.ScheduleMessagePayload;
import uz.uzkassa.cts.enums.MailingListItemStatus;
import uz.uzkassa.cts.enums.ScheduleMessageStatus;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqScheduledMessageProducer;
import uz.uzkassa.cts.repository.MailingListItemRepository;
import uz.uzkassa.cts.repository.ScheduleMessageRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ScheduleMessageJob extends QuartzJobBean {

    private final ScheduleMessageRepository scheduleMessageRepository;
    private final RabbitMqScheduledMessageProducer scheduledMessageProducer;
    private final MailingListItemRepository mailingListItemRepository;

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        Long scheduledMessageId = (Long) jobDataMap.get("id");
        scheduleMessageRepository.findByIdAndStatus(scheduledMessageId, ScheduleMessageStatus.ACTIVE).ifPresent(scheduledMessage -> {
            ScheduleMessagePayload payload = new ScheduleMessagePayload();
            payload.setSubject(scheduledMessage.getSubject());
            payload.setMessage(scheduledMessage.getMessage());

            List<Long> mailingListItemIds = mailingListItemRepository.getIdsByMailingListIdExcludingStatus(scheduledMessage.getMailingListId(), MailingListItemStatus.SENT);
            List<List<Long>> partitionedIds = ListUtils.partition(mailingListItemIds, 100);
            partitionedIds.forEach(ids -> {
                payload.setMailingListItemIds(new ArrayList<>(ids));
                scheduledMessageProducer.sendScheduledMessage(payload);
            });
            scheduledMessage.setStatus(ScheduleMessageStatus.SENT);
            scheduleMessageRepository.save(scheduledMessage);
        });
    }
}

