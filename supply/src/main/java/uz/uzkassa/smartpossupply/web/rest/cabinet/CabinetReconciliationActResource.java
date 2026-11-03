package uz.uzkassa.smartpossupply.web.rest.cabinet;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.reconciliationact.CalculationDTO;
import uz.uzkassa.smartpossupply.dto.reconciliationact.FinancialOperationACTDTO;
import uz.uzkassa.smartpossupply.dto.reconciliationact.ReconciliationActDTO;
import uz.uzkassa.smartpossupply.dto.reconciliationact.StockProductACTDTO;
import uz.uzkassa.smartpossupply.filters.ReconciliationActFilter;
import uz.uzkassa.smartpossupply.service.ReconciliationActService;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@RestController
@RequestMapping(ApiConstants.reconciliationActRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor(access = PUBLIC)
public class CabinetReconciliationActResource {
    ReconciliationActService reconciliationActService;

    @GetMapping("/by-financial-operation")
    public ResponseEntity<Page<FinancialOperationACTDTO>> getReconciliationAct(@ParameterObject @Valid ReconciliationActFilter filter) {
        return ResponseEntity.ok(reconciliationActService.getReconciliationActByFinancialOperation(filter));
    }

    @GetMapping("/by-financial-operation/calculation")
    public ResponseEntity<ReconciliationActDTO> financialOperationCalculation(@ParameterObject @Valid ReconciliationActFilter filter) {
        return ResponseEntity.ok(reconciliationActService.financialOperationCalculation(filter));
    }

    @GetMapping("/by-product")
    public ResponseEntity<Page<StockProductACTDTO>> getReconciliationActByProduct(@ParameterObject @Valid ReconciliationActFilter filter) {
        return ResponseEntity.ok(reconciliationActService.getReconciliationActByProduct(filter));
    }

    @GetMapping("/by-product/calculation")
    public ResponseEntity<CalculationDTO> calculation(@ParameterObject @Valid ReconciliationActFilter filter) {
        return ResponseEntity.ok(reconciliationActService.calculation(filter));
    }
}
