package uz.uzkassa.smartpossupply.filters;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CompanyFilter extends BaseFilter {

    Long activityTypeId;

    Long parentActivityTypeId;

    String businessType;

    Long managerId;

    @JsonProperty("mine")
    Boolean mine;

    Boolean onlyHasManager;

    Boolean freeTrial;

    List<Long> managerIds;

    Long supervisorId;

    @JsonProperty("xFileEnabled")
    Boolean xFileEnabled;

    @JsonProperty("hasService")
    Boolean hasService;

    Long expeditorId;

    Long agentId;

    Boolean hasEquipment;

    LocalDateTime lastSyncDate;

    Boolean validateTin;
}
