package uz.uzkassa.cts.jobs.quartz;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.hrms.Event;
import uz.uzkassa.cts.dto.payload.ReminderPayload;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqReminderProducer;
import uz.uzkassa.cts.repository.EventRepository;
import uz.uzkassa.cts.repository.EventShareRepository;
import uz.uzkassa.cts.repository.ReminderRepository;

@Component
@Transactional
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class EventReminderJob extends QuartzJobBean {

    private final EventRepository eventRepository;
    private final ReminderRepository reminderRepository;
    private final EventShareRepository eventShareRepository;
    private final RabbitMqReminderProducer rabbitMqReminderProducer;

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        Long eventId = (Long) jobDataMap.get("id");
        String jobId = (String) jobDataMap.get("jobId");
        reminderRepository.findByEntityIdAndEntityTypeAndJobId(eventId, EntityTypeEnum.EVENT, jobId).ifPresent(reminder -> {
            Event event = eventRepository.getReferenceById(eventId);
            ReminderPayload payload = new ReminderPayload();
            payload.setSubject(event.getSubject());
            payload.setMessage(event.getDescription());
            payload.setChannelType(reminder.getType());
            payload.setParticipantIds(eventShareRepository.getParticipantIds(eventId));
            payload.setCompanyId(event.getCompanyId());
            payload.setEntityId(eventId);
            payload.setEntityType(event.getEntityType());
            rabbitMqReminderProducer.sendReminder(payload);
        });
    }
}

