package uz.uzkassa.smartpossupply.dto.job;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.quartz.Job;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@ToString
@Builder
public class JobDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 24563457254L;

    private Long id;
    private String jobId;
    private Map<String, Object> dataMap;
    private LocalDateTime startDate;
    private String jobGroup;
    private String jobGroupDescription;
    private Class<? extends Job> jobClass;
    private String triggerGroup;
    private String triggerGroupDescription;
}
