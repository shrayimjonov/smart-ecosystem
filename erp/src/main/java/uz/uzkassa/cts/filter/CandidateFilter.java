package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.CandidateStatus;
import uz.uzkassa.cts.enums.InterviewStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class CandidateFilter extends SimpleFilter {
    private CandidateStatus status;
    private InterviewStatus interviewStatus;
    private Long vacancyId;
    private Long regionId;
    private LocalDateTime from;
    private LocalDateTime to;
}
