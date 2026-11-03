package uz.uzkassa.smartpossupply.crons.quartz;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.job.JobDTO;
import uz.uzkassa.smartpossupply.web.rest.errors.JobSchedulingException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class JobScheduler {

    Scheduler scheduler;

    public void createJob(JobDTO jobDTO) {
        try {
            JobDetail jobDetail = buildJobDetail(jobDTO);
            Trigger trigger = buildJobTrigger(jobDetail, jobDTO);
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException ex) {
            log.error(ex.getMessage(), ex);
            throw new JobSchedulingException("Error scheduling job", ex);
        }
    }

    public void updateTrigger(String triggerName, String triggerGroup, LocalDateTime startDate) {
        try {
            TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);
            Trigger oldTrigger = scheduler.getTrigger(triggerKey);

            Trigger newTrigger = oldTrigger.getTriggerBuilder()
                    .startAt(Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant()))
                    .build();

            scheduler.rescheduleJob(triggerKey, newTrigger);
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
            throw new JobSchedulingException("Error updating trigger", e);
        }
    }

    public Trigger getTrigger(String triggerName, String triggerGroup) {
        try {
            TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);
            return scheduler.getTrigger(triggerKey);
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
            throw new JobSchedulingException("Error getting trigger", e);
        }
    }

    public void deleteJob(String jobId, String groupName) {
        try {
            JobKey jobKey = new JobKey(jobId, groupName);
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
            throw new JobSchedulingException("Error deleting job", e);
        }
    }

    public void deleteJobs(List<String> jobIds, String groupName) {
        try {
            List<JobKey> jobKeys = jobIds.stream().map(jobId -> new JobKey(jobId, groupName)).toList();
            scheduler.deleteJobs(jobKeys);
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
            throw new JobSchedulingException("Error deleting jobs", e);
        }
    }

    private JobDetail buildJobDetail(JobDTO jobDTO) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("id", jobDTO.getId());
        jobDataMap.put("jobId", jobDTO.getJobId());
        if (jobDTO.getDataMap() != null && !jobDTO.getDataMap().isEmpty()) {
            jobDataMap.putAll(jobDTO.getDataMap());
        }
        return JobBuilder.newJob(jobDTO.getJobClass())
                .withIdentity(jobDTO.getJobId(), jobDTO.getJobGroup())
                .usingJobData(jobDataMap)
                .withDescription(jobDTO.getJobGroupDescription())
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, JobDTO jobDTO) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDTO.getJobId(), jobDTO.getTriggerGroup())
                .withDescription(jobDTO.getTriggerGroupDescription())
                .startAt(Date.from(jobDTO.getStartDate().atZone(ZoneId.systemDefault()).toInstant()))
                .build();
    }
}
