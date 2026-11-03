package uz.uzkassa.smartpossupply.filters;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.dto.order.PlanRouteExpeditorDTO;

import java.util.List;

@Getter
@Setter
public class OrderFilter extends BaseFilter {

    @JsonProperty("used")
    Boolean used;

    String orderType;

    boolean fetchDeleted;

    Long customerBranchId;

    String source;

    String paymentType;

    Long truckId;

    Long planRouteId;

    List<Long> planRouteIds;

    Long agentId;

    List<Long> expeditors;

    Long expeditorId;

    List<Long> planRouteTemplateIds;

    Double longitude;

    Double latitude;

    Long planRouteCustomerBranchId;

    Long categoryId;

    Long tripId;

    @Schema
    List<PlanRouteExpeditorDTO> planRouteExpeditors;

    String token;

    Long telegramUserId;

    String botType;
}
