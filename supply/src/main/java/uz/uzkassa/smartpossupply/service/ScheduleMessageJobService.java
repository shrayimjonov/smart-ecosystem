package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Trigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.crons.quartz.JobScheduler;
import uz.uzkassa.smartpossupply.crons.quartz.ScheduleMessageJob;
import uz.uzkassa.smartpossupply.dto.job.JobDTO;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ScheduleMessageJobService {

    JobScheduler jobScheduler;
    private static final String JOB_GROUP_NAME = "SupplyScheduledMessageJobGroup";
    private static final String JOB_GROUP_DESCRIPTION = "SupplyScheduledMessageJobGroupDescription";
    private static final String TRIGGER_GROUP_NAME = "SupplyScheduledMessageTriggerGroup";
    private static final String TRIGGER_GROUP_DESCRIPTION = "SupplyScheduledMessageTriggerGroupDescription";

    public void createJob(String jobId, Long id, LocalDateTime startDate) {
        JobDTO jobDTO = JobDTO.builder()
                .id(id)
                .jobId(jobId)
                .startDate(startDate)
                .jobClass(ScheduleMessageJob.class)
                .jobGroup(JOB_GROUP_NAME)
                .jobGroupDescription(JOB_GROUP_DESCRIPTION)
                .triggerGroup(TRIGGER_GROUP_NAME)
                .triggerGroupDescription(TRIGGER_GROUP_DESCRIPTION)
                .build();
        jobScheduler.createJob(jobDTO);
    }

    public void updateJob(String jobId, LocalDateTime startDate) {
        jobScheduler.updateTrigger(jobId, TRIGGER_GROUP_NAME, startDate);
    }

    public void deleteJob(String jobId) {
        jobScheduler.deleteJob(jobId, JOB_GROUP_NAME);
    }

    public Trigger getJob(String jobId) {
        return jobScheduler.getTrigger(jobId, JOB_GROUP_NAME);
    }
}
