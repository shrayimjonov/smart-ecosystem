package uz.uzkassa.smartpossupply.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.user.supervisor.*;
import uz.uzkassa.smartpossupply.filters.*;
import uz.uzkassa.smartpossupply.service.SupervisorService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping(ApiConstants.mobileSupervisorRootApi)
public class MobileSupervisorResource {
    SupervisorService supervisorService;

    @Operation(summary = ApiConstants.Get)
    @GetMapping("/employees")
    public ResponseEntity<Page<EmployeeAssignmentDTO>> getAllEmployees(@ParameterObject SupervisorFilter filter) {
        return ResponseEntity.ok(supervisorService.getAllEmployee(filter));
    }

    @PostMapping("/assign")
    public ResponseEntity<Void> assignAgentsAndExpeditorsToSupervisor(@RequestBody SupervisorEmployeeAssignDTO dto) {
        supervisorService.assignAgent(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping("/plan-route-templates")
    public ResponseEntity<Page<SupervisorPlanRouteDTO>> getPlanRouteTemplate(@ParameterObject SupervisorFilter filter) {
        return ResponseEntity.ok(supervisorService.getPlanRouteTemplateForMobile(filter));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping("/plan-routes/{templateId}/statistics")
    public ResponseEntity<List<SupervisorPlanRouteStatisticDTO>> getPlanRouteStatistics(@PathVariable Long templateId, @RequestParam LocalDate from, @RequestParam LocalDate to) {
        return ResponseEntity.ok(supervisorService.getPlanRouteStatistics(templateId, from, to));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping("/orders/{templateId}")
    public ResponseEntity<Long> getCountOfOrders(@PathVariable Long templateId, @RequestParam LocalDateTime from, @RequestParam LocalDateTime to) {
        return ResponseEntity.ok(supervisorService.getCountOfOrders(templateId, from, to));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping("/customers")
    public ResponseEntity<Page<SupervisorCustomerDTO>> getCustomers(@ParameterObject CompanyFilter filter) {
        return ResponseEntity.ok(supervisorService.getCustomers(filter));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping("/assigned-agents")
    public ResponseEntity<Page<AgentInfoDTO>> getAssignedAgents(@ParameterObject SupervisorFilter filter) {
        return ResponseEntity.ok(supervisorService.getAssignedAgents(filter));
    }

    @GetMapping("/agent-statistics")
    public ResponseEntity<List<AgentStatisticsDTO>> getAgentStatistics(@RequestParam(required = false) List<Long> agentIds, @ParameterObject SimpleFilter filter) {
        return ResponseEntity.ok(supervisorService.getAgentStatistics(agentIds, filter));
    }

    @GetMapping("/agent-statistics-by-product")
    public ResponseEntity<List<AgentStatisticsByProductDTO>> getAgentStatisticsByProducts(@RequestParam(required = false) List<Long> agentIds, @ParameterObject SimpleFilter filter) {
        return ResponseEntity.ok(supervisorService.getAgentStatisticsByProducts(agentIds, filter));
    }

    @GetMapping("/plan-routes/{agentId}")
    public ResponseEntity<List<PlanRouteInfoDTO>> getPlanRoutes(@PathVariable Long agentId, @ParameterObject SimpleFilter filter) {
        return ResponseEntity.ok(supervisorService.getPlanRoutes(agentId, filter));
    }

    @GetMapping("/orders")
    public ResponseEntity<Page<OrderInfoDTO>> getOrders(@ParameterObject SupervisorOrderFilter filter) {
        return ResponseEntity.ok(supervisorService.getOrders(filter));
    }

    @GetMapping("/assigned-agents-plan-routes")
    public ResponseEntity<Page<AgentPlanRouteInfoDTO>> getAgentsPlanRoutes(@ParameterObject SupervisorFilter filter) {
        if (SecurityUtils.getCurrentUserId() == null) {
            return ResponseEntity.ok(new PageImpl<>(List.of(), filter.getSortedPageable(), 0));
        }
        return ResponseEntity.ok(supervisorService.getAgentsWithPlanRoute(filter));
    }

    @GetMapping("/statistics")
    public ResponseEntity<CommonStatisticsDTO> getCommonStatistics(@ParameterObject SupervisorFilter filter) {
        if (SecurityUtils.getCurrentUserId() == null) {
            return ResponseEntity.ok(new CommonStatisticsDTO());
        }
        return ResponseEntity.ok(supervisorService.getCommonStatistics(filter));
    }

    @GetMapping("/stock-expeditor-statistics")
    public ResponseEntity<ExpeditorStatisticsDTO> getExpeditorCommonStatistics(@ParameterObject SupervisorFilter filter) {
        return ResponseEntity.ok(supervisorService.getExpeditorCommonStatistics(filter));
    }

    @GetMapping("/stock/{expeditorId}/statistics")
    public ResponseEntity<ExpeditorCancelledStockOutDTO> getExpeditorStatistics(@PathVariable Long expeditorId, @ParameterObject SupervisorExpeditorFilter filter) {
        filter.setExpeditorId(expeditorId);
        return ResponseEntity.ok(supervisorService.getExpeditorStatistics(filter));
    }

    @GetMapping("/order-expeditor-statistics")
    public ResponseEntity<ExpeditorOrderDTO> getExpeditorOrderStatistics(@ParameterObject SupervisorOrderFilter filter) {
        return ResponseEntity.ok(supervisorService.getExpeditorOrderStatistics(filter));
    }

    @GetMapping("/{expeditorId}/stocks")
    public ResponseEntity<Page<ExpeditorStockDTO>> getExpeditorStocks(@PathVariable Long expeditorId, @ParameterObject SupervisorExpeditorFilter filter) {
        filter.setExpeditorId(expeditorId);
        return ResponseEntity.ok(supervisorService.getExpeditorStockStatistics(filter));
    }

}
