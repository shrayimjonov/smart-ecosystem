package uz.uzkassa.cts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.dto.job.JobDTO;
import uz.uzkassa.cts.jobs.quartz.EventReminderJob;
import uz.uzkassa.cts.jobs.quartz.JobScheduler;
import uz.uzkassa.cts.service.EventReminderJobService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventReminderJobServiceImpl implements EventReminderJobService {

    private final JobScheduler jobScheduler;
    private static final String JOB_GROUP_NAME = "EventReminderJobGroup";
    private static final String JOB_GROUP_DESCRIPTION = "EventReminderJobGroupDescription";
    private static final String TRIGGER_GROUP_NAME = "EventReminderTriggerGroup";
    private static final String TRIGGER_GROUP_DESCRIPTION = "EventReminderTriggerGroupDescription";

    @Override
    public void createJob(String jobId, Long id, LocalDateTime startDate) {
        JobDTO jobDTO = JobDTO.builder()
                .id(id)
                .jobId(jobId)
                .startDate(startDate)
                .jobClass(EventReminderJob.class)
                .jobGroup(JOB_GROUP_NAME)
                .jobGroupDescription(JOB_GROUP_DESCRIPTION)
                .triggerGroup(TRIGGER_GROUP_NAME)
                .triggerGroupDescription(TRIGGER_GROUP_DESCRIPTION)
                .build();
        jobScheduler.createJob(jobDTO);
    }

    @Override
    public void deleteJobs(List<String> jobIds) {
        jobScheduler.deleteJobs(jobIds, JOB_GROUP_NAME);
    }
}
