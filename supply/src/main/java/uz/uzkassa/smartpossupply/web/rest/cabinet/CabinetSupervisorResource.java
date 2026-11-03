package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.user.supervisor.SupervisorDTO;
import uz.uzkassa.smartpossupply.dto.user.supervisor.SupervisorPlanRouteDTO;
import uz.uzkassa.smartpossupply.filters.SupervisorFilter;
import uz.uzkassa.smartpossupply.service.SupervisorService;

@RestController
@RequestMapping(ApiConstants.cabinetSupervisorRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetSupervisorResource {
    SupervisorService supervisorService;

    @Operation(summary = ApiConstants.Get)
    @GetMapping
    public ResponseEntity<Page<SupervisorDTO>> getAll(@ParameterObject SupervisorFilter filter) {
        return ResponseEntity.ok(supervisorService.getAll(filter));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping("/{supervisorId}")
    public ResponseEntity<SupervisorDTO> getById(@PathVariable Long supervisorId) {
        return ResponseEntity.ok(supervisorService.getBySupervisorId(supervisorId));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping("/plan-route-templates")
    public ResponseEntity<Page<SupervisorPlanRouteDTO>> getPlanRouteTemplates(@ParameterObject SupervisorFilter filter) {
        return ResponseEntity.ok(supervisorService.getPlanRouteTemplate(filter));
    }
}
