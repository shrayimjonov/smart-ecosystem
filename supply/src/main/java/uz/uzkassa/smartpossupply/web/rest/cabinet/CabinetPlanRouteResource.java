package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.customerbranch.CustomerBranchLookUpDTO;
import uz.uzkassa.smartpossupply.dto.planroute.*;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.CustomerBranchFilter;
import uz.uzkassa.smartpossupply.filters.PlanRouteFilter;
import uz.uzkassa.smartpossupply.interfaces.CrudRest;
import uz.uzkassa.smartpossupply.service.CustomerBranchService;
import uz.uzkassa.smartpossupply.service.PlanRouteService;
import uz.uzkassa.smartpossupply.service.handlers.excel.SalesReportByAgentStrategy;
import uz.uzkassa.smartpossupply.service.handlers.excel.SalesReportStrategy;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.01:2023 10:40
 */
@RestController
@RequestMapping(ApiConstants.cabinetPlanRouteRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetPlanRouteResource implements CrudRest<Long, PlanRouteDTO, PlanRouteDetailDTO, PlanRouteListDTO, PlanRouteFilter> {

    PlanRouteService planRouteService;
    CustomerBranchService customerBranchService;
    SalesReportByAgentStrategy salesReportByAgentStrategy;
    SalesReportStrategy salesReportStrategy;

    @Override
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody PlanRouteDTO planRouteDTO) {
        return ResponseEntity.ok(planRouteService.create(planRouteDTO));
    }

    @PostMapping("/{templateId}")
    public ResponseEntity<Long> createByTemplateId(@PathVariable Long templateId) {
        return ResponseEntity.ok(planRouteService.createByTemplateId(templateId));
    }

    @PatchMapping("/start/{planRouteCustomerBranchId}")
    public ResponseEntity<Long> start(@PathVariable Long planRouteCustomerBranchId) {
        planRouteService.start(planRouteCustomerBranchId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/finish")
    public ResponseEntity<Long> finish(@RequestBody PlanRouteFinishDTO planRouteFinishDTO) {
        return ResponseEntity.ok(planRouteService.finish(planRouteFinishDTO));
    }

    @PutMapping(ApiConstants.id)
    @Override
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody PlanRouteDTO planRouteDTO) {
        return ResponseEntity.ok(planRouteService.update(id, planRouteDTO));
    }

    @GetMapping(ApiConstants.id)
    @Override
    public ResponseEntity<PlanRouteDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(planRouteService.get(id));
    }

    @GetMapping("/template/{templateId}")
    public ResponseEntity<PlanRouteDetailDTO> getByTemplateId(@PathVariable Long templateId) {
        return ResponseEntity.ok(planRouteService.getByTemplateId(templateId));
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    @Override
    public ResponseEntity<Page<PlanRouteListDTO>> getList(@ParameterObject PlanRouteFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(planRouteService.getList(filter));
    }

    @Operation(summary = ApiConstants.LookUp)
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<PlanRouteLookUpDTO>> lookUp(@ParameterObject PlanRouteFilter filter) {
        return ResponseEntity.ok(planRouteService.lookUp(filter));
    }

    @Override
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        planRouteService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/customer-branches/lookup")
    public ResponseEntity<List<CustomerBranchLookUpDTO>> lookUp(@ParameterObject CustomerBranchFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return ResponseEntity.ok(customerBranchService.lookUpForPlanRoute(filter));
    }

    @GetMapping("/expeditors/lookup")
    public ResponseEntity<List<CommonDTO>> lookUpForExpeditor(@ParameterObject BaseFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(planRouteService.lookUpForExpeditor(filter));
    }

    @PatchMapping("/{id}/agent/{agentId}")
    public ResponseEntity<Void> changeAgent(@PathVariable Long id, @PathVariable Long agentId) {
        planRouteService.changeAgent(id, agentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/customer-statistics")
    public ResponseEntity<PlanRouteCustomerStatisticsDTO> getStatistics(@ParameterObject PlanRouteFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PlanRouteCustomerStatisticsDTO());
        }
        return ResponseEntity.ok(planRouteService.getCustomerStatistics(filter));
    }

    @GetMapping("/report")
    public ResponseEntity<Page<PlanRouteReportDTO>> report(@ParameterObject PlanRouteFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(planRouteService.report(filter));
    }

    @GetMapping("/agent/report")
    public ResponseEntity<Page<PlanRouteReportDTO>> agentReport(@ParameterObject PlanRouteFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(planRouteService.reportByAgent(filter));
    }

    @GetMapping("/report-total-qty")
    public ResponseEntity<PlanRouteReportTotalQtyDTO> reportTotalQty(@ParameterObject PlanRouteFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PlanRouteReportTotalQtyDTO());
        }
        return ResponseEntity.ok(planRouteService.reportTotalQty(filter));
    }

    @GetMapping("/agent/report-total-qty")
    public ResponseEntity<PlanRouteReportTotalQtyDTO> reportByAgentTotalQty(@ParameterObject PlanRouteFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PlanRouteReportTotalQtyDTO());
        }
        return ResponseEntity.ok(planRouteService.reportByAgentTotalQty(filter));
    }

    @Operation(summary = ApiConstants.ExportCSV)
    @GetMapping(ApiConstants.exportCsv)
    public ResponseEntity<Void> exportCsv(@ParameterObject PlanRouteFilter filter) {
        salesReportStrategy.generate(filter, FileType.CSV);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.ExportExcel)
    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Resource> exportExcel(@ParameterObject PlanRouteFilter filter) {
        salesReportStrategy.generate(filter, FileType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.ExportCSV)
    @GetMapping("/agent" + ApiConstants.exportCsv)
    public ResponseEntity<Void> agentReportExportCsv(@ParameterObject PlanRouteFilter filter) {
        salesReportByAgentStrategy.generate(filter, FileType.CSV);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.ExportExcel)
    @GetMapping("/agent" + ApiConstants.exportExcel)
    public ResponseEntity<Resource> agentReportExportExcel(@ParameterObject PlanRouteFilter filter) {
        salesReportByAgentStrategy.generate(filter, FileType.EXCEL);
        return ResponseEntity.ok().build();
    }
}
