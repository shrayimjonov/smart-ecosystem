package uz.uzkassa.cts.service;

import java.time.LocalDateTime;
import java.util.List;

public interface EventReminderJobService {

    void createJob(String jobId, Long id, LocalDateTime startTime);

    void deleteJobs(List<String> jobIds);
}
