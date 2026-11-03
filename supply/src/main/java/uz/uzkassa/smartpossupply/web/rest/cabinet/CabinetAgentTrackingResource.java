package uz.uzkassa.smartpossupply.web.rest.cabinet;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.planroute.AgentTrackingDTO;
import uz.uzkassa.smartpossupply.dto.planroute.AgentTrackingListDTO;
import uz.uzkassa.smartpossupply.filters.PlanRouteFilter;
import uz.uzkassa.smartpossupply.service.AgentTrackingService;

import java.util.ArrayList;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 31.01.2023 15:41
 */
@RestController
@RequestMapping(ApiConstants.cabinetAgentTrackingRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetAgentTrackingResource {

    AgentTrackingService agentTrackingService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody AgentTrackingDTO agentTrackingDTO) {
        return ResponseEntity.ok(agentTrackingService.create(agentTrackingDTO));
    }

    @GetMapping
    public ResponseEntity<Page<AgentTrackingListDTO>> getList(@ParameterObject PlanRouteFilter filter) {
        if (filter.getAgentId() == null && (filter.getEntityType() == null || filter.getEntityId() == null)) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getSortedPageable(), 0));
        }
        return ResponseEntity.ok(agentTrackingService.getList(filter));
    }
}
