package uz.uzkassa.smartpossupply.filters;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChecklistFilter extends BaseFilter {

    Long agentId;

    Long supervisorId;
}
