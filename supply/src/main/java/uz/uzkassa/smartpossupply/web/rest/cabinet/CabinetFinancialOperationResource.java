package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.customer.AccountReceivableReportDTO;
import uz.uzkassa.smartpossupply.dto.financialoperation.FinancialOperationDTO;
import uz.uzkassa.smartpossupply.dto.financialoperation.FinancialOperationDetailDTO;
import uz.uzkassa.smartpossupply.dto.financialoperation.FinancialOperationListDTO;
import uz.uzkassa.smartpossupply.dto.financialoperation.FinancialOperationStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.enums.FinancialOperationType;
import uz.uzkassa.smartpossupply.filters.FinancialOperationFilter;
import uz.uzkassa.smartpossupply.filters.OrderFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.interfaces.CrudRest;
import uz.uzkassa.smartpossupply.service.FinancialOperationService;
import uz.uzkassa.smartpossupply.service.handlers.excel.AccountReceivableStrategy;
import uz.uzkassa.smartpossupply.service.handlers.excel.FinancialOperationListStrategy;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

/**
 * Created by: Shuxratjon Rayimjonov
 * Date: 09.01.2024
 */
@RestController
@RequestMapping(ApiConstants.cabinetFinancialOperationRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor(access = PUBLIC)
public class CabinetFinancialOperationResource implements CrudRest<Long, FinancialOperationDTO, FinancialOperationDetailDTO, FinancialOperationListDTO, FinancialOperationFilter> {

    FinancialOperationService financialOperationService;
    AccountReceivableStrategy accountReceivableStrategy;
    FinancialOperationListStrategy financialOperationListStrategy;

    @Override
    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody FinancialOperationDTO financialOperationDTO) {
        financialOperationDTO.setOperationType(FinancialOperationType.DEBIT);
        return ResponseEntity.ok(financialOperationService.create(financialOperationDTO));
    }

    @Override
    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable("id") Long id, @RequestBody FinancialOperationDTO financialOperationDTO) {
        return ResponseEntity.ok(financialOperationService.update(id, financialOperationDTO));
    }

    @Override
    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<FinancialOperationDetailDTO> get(@PathVariable("id") Long id) {
        return ResponseEntity.ok(financialOperationService.get(id));
    }

    @Override
    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<FinancialOperationListDTO>> getList(@ParameterObject FinancialOperationFilter filter) {
        return ResponseEntity.ok(financialOperationService.getList(filter));
    }


    @Operation(summary = ApiConstants.Statistics)
    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<FinancialOperationStatisticsDTO> getStatistics(@ParameterObject FinancialOperationFilter filter) {
        return ResponseEntity.ok(financialOperationService.getStatistics(filter));
    }

    @Operation(summary = ApiConstants.Types)
    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<FinancialOperationType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(FinancialOperationType.class));
    }

    @Override
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        financialOperationService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.ExportExcel)
    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Resource> exportExcel(@ParameterObject OrderFilter filter) {
        financialOperationListStrategy.generate(filter, FileType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCsv)
    public ResponseEntity<Resource> exportCsv(@ParameterObject OrderFilter filter) {
        financialOperationListStrategy.generate(filter, FileType.CSV);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/account-receivable/export-excel")
    public ResponseEntity<Resource> exportAccountReceivableReportExcel(@ParameterObject StockFilter filter) {
        accountReceivableStrategy.generate(filter, FileType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/account-receivable/export-csv")
    public ResponseEntity<Resource> exportAccountReceivableReportCsv(@ParameterObject StockFilter filter) {
        accountReceivableStrategy.generate(filter, FileType.CSV);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Account receivable statistics")
    @GetMapping("/account-receivable/report")
    public ResponseEntity<Page<AccountReceivableReportDTO>> getAccountReceivableReport(@ParameterObject StockFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(financialOperationService.getAccountReceivableReport(filter));
    }
}
