package uz.uzkassa.smartpossupply.filters;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SupervisorExpeditorFilter extends SimpleFilter {
    Long supervisorId;
    Long expeditorId;
    LocalDate visitPlanDate;
}
