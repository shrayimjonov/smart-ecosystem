package uz.uzkassa.smartpos.web.rest;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.filter.receipt.SalesStatsFilter;
import uz.uzkassa.smartpos.dto.warehouse.BalanceHistoryDTO;
import uz.uzkassa.smartpos.report.RegisterImportTemplateXLs;
import uz.uzkassa.smartpos.service.RegisterService;
import uz.uzkassa.smartpos.utils.ServerUtils;

import java.io.IOException;

@RestController
@RequestMapping("/api/warehouse/register")
@RequiredArgsConstructor
public class RegisterResource {
    private final RegisterService registerService;
    private final RegisterImportTemplateXLs registerImportTemplateXLs;

    @GetMapping("/history")
    public Page<BalanceHistoryDTO> getHistory(@ParameterObject SalesStatsFilter filter) {
        return registerService.getHistory(filter);
    }

    @GetMapping("/materialized/history")
    public Page<BalanceHistoryDTO> getMaterializedHistory(@ParameterObject SalesStatsFilter filter) {
        return registerService.getMaterializedHistory(filter);
    }


    @GetMapping(value = "/materialized/history/excel")
    public ResponseEntity<Resource> download(@ParameterObject SalesStatsFilter filter) {
        return ServerUtils.getResponseEntity(registerService.balanceHistoryDownload(filter), "balance-history");
    }


    @GetMapping(value = "/export")
    public ResponseEntity<Resource> exportToExcel(@RequestParam Long branchId) throws IOException {
        return ServerUtils.getResponseEntity(registerImportTemplateXLs.exportRegisters(branchId), "registers");
    }

}
