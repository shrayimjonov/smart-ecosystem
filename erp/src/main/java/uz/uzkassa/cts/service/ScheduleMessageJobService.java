package uz.uzkassa.cts.service;

import org.quartz.Trigger;

import java.time.LocalDateTime;

public interface ScheduleMessageJobService {

    void createJob(String jobId, Long id, LocalDateTime startTime);
    void updateJob(String jobId, LocalDateTime startTime);
    void deleteJob(String jobId);
    Trigger getJob(String jobId);
}
