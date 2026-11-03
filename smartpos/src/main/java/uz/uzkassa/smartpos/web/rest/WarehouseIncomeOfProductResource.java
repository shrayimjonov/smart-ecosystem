package uz.uzkassa.smartpos.web.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.warehouse.InvoiceValidationDTO;
import uz.uzkassa.smartpos.security.AuthoritiesConstants;
import uz.uzkassa.smartpos.service.IncomeOfProductServiceV2;
import uz.uzkassa.smartpos.service.MarkingService;

@RestController
@RequestMapping("/api/warehouse/income-of-product")
@Slf4j
@Secured({AuthoritiesConstants.SUPPORT, AuthoritiesConstants.SALES_MANAGER, AuthoritiesConstants.OWNER, AuthoritiesConstants.BRANCH_ADMIN, AuthoritiesConstants.SMARTPOS_ADMIN})
@RequiredArgsConstructor
public class WarehouseIncomeOfProductResource {
    private final MarkingService markingService;
    private final IncomeOfProductServiceV2 incomeOfProductService;

    @DeleteMapping("/{id}")
    public void deleteIncomeOfProduct(@PathVariable Long id) {
        incomeOfProductService.deleteIncomeOfProduct(id);
    }

    @PutMapping("/{id}/fix-markings")
    public void fixMarkings(@PathVariable Long id) {
        log.debug("Request to fix income of product markings by id : {}", id);
        markingService.saveIncomeOfProductMarkings(id);
    }

    @GetMapping("/invoice/check")
    public InvoiceValidationDTO validateInvoice(@RequestParam Long invoiceId, @RequestParam Long branchId) {
        return markingService.invoiceValidation(invoiceId, branchId);
    }
}
