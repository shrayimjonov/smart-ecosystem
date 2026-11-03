package uz.uzkassa.smartpossupply.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.agent.performancereport.AgentPerformanceReportDTO;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.AgentPerformanceReportFilter;
import uz.uzkassa.smartpossupply.service.AgentService;
import uz.uzkassa.smartpossupply.service.handlers.excel.AgentPerformanceReportStrategy;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetAgentRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetAgentResource {

    AgentService agentService;
    AgentPerformanceReportStrategy agentPerformanceReportStrategy;

    @GetMapping("/performance-report")
    public ResponseEntity<List<AgentPerformanceReportDTO>> getPerformanceReport(@ParameterObject AgentPerformanceReportFilter filter) {
        return ResponseEntity.ok(agentService.getPerformanceReport(filter));
    }

    @GetMapping("/performance-report" + ApiConstants.exportExcel)
    public ResponseEntity<Resource> getPerformanceReportExcel(@ParameterObject AgentPerformanceReportFilter filter) {
        filter.setCompanyId(SecurityUtils.getCompanyId());
        agentPerformanceReportStrategy.generate(filter, FileType.EXCEL);
        return ResponseEntity.ok().build();
    }
}
