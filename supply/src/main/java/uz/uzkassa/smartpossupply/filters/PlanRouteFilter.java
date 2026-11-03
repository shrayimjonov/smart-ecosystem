package uz.uzkassa.smartpossupply.filters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlanRouteFilter extends BaseFilter {

    Long agentId;

    Long templateId;

    Long customerBranchId;

    Long planRouteId;

    Long customerId;

    List<Long> customerBranchIds;

    List<Long> customerIds;

    List<Long> agentIds;

    List<Long> planRouteIds;

    List<Long> planRouteTemplateIds;

    Long categoryId;

    List<Long> categoryIds;

    boolean withExpeditor;

    @JsonIgnore
    boolean forStatistics;

    String entityType;

    Long entityId;

    String paymentType;
}
