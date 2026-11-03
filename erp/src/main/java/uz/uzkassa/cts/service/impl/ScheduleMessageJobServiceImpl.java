package uz.uzkassa.cts.service.impl;

import lombok.RequiredArgsConstructor;
import org.quartz.Trigger;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.dto.job.JobDTO;
import uz.uzkassa.cts.jobs.quartz.JobScheduler;
import uz.uzkassa.cts.jobs.quartz.ScheduleMessageJob;
import uz.uzkassa.cts.service.ScheduleMessageJobService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ScheduleMessageJobServiceImpl implements ScheduleMessageJobService {

    private final JobScheduler jobScheduler;
    private static final String JOB_GROUP_NAME = "ScheduledMessageJobGroup";
    private static final String JOB_GROUP_DESCRIPTION = "ScheduledMessageJobGroupDescription";
    private static final String TRIGGER_GROUP_NAME = "ScheduledMessageTriggerGroup";
    private static final String TRIGGER_GROUP_DESCRIPTION = "ScheduledMessageTriggerGroupDescription";


    @Override
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

    @Override
    public void updateJob(String jobId, LocalDateTime startDate) {
        jobScheduler.updateTrigger(jobId, TRIGGER_GROUP_NAME, startDate);
    }

    @Override
    public void deleteJob(String jobId) {
        jobScheduler.deleteJob(jobId, JOB_GROUP_NAME);
    }

    @Override
    public Trigger getJob(String jobId) {
        return jobScheduler.getTrigger(jobId, JOB_GROUP_NAME);
    }
}
