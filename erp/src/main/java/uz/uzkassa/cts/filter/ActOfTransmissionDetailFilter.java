package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.ActDocFlowStatus;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActOfTransmissionDetailFilter extends ActOfTransmissionFilter {
    Long creatorId;
    LocalDate acceptFrom;
    LocalDate acceptTo;
    ActDocFlowStatus actDocFlowStatus;
}
