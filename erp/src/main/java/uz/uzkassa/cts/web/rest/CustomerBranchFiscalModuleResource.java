package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.domain.CustomerBranchFiscalModule;
import uz.uzkassa.cts.dto.device.BaseDeviceFiscalModuleDTO;
import uz.uzkassa.cts.dto.device.CustomerBranchDeviceListDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleAddDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.CustomerBranchFiscalModuleService;

import java.util.List;

/**
 * REST controller for managing {@link CustomerBranchFiscalModule}.
 */
@RestController
@RequestMapping("/api/customer/branch/fiscal/modules")
@Tag(name = "Customer Branch Fiscal Modules")
@Slf4j
@Deprecated
public class CustomerBranchFiscalModuleResource {

    private final CustomerBranchFiscalModuleService customerBranchFiscalModuleService;

    public CustomerBranchFiscalModuleResource(CustomerBranchFiscalModuleService customerBranchFiscalModuleService) {
        this.customerBranchFiscalModuleService = customerBranchFiscalModuleService;
    }

    @Operation(summary ="List")
    @GetMapping
    public ResponseEntity<Page<CustomerBranchDeviceListDTO>> list(@Valid BaseFilter filter) {
        log.debug("REST request to get customer branch fiscal modules list: {}", filter);
        return ResponseEntity.ok(customerBranchFiscalModuleService.list(filter));
    }

    @Operation(summary ="List By Customer Branch")
    @GetMapping("/by-customer-branch")
    public ResponseEntity<Page<BaseDeviceFiscalModuleDTO>> listByCustomerBranch(BaseFilter filter) {
        log.debug("REST request to get customer branch fiscal module list: {}", filter);
        return ResponseEntity.ok(customerBranchFiscalModuleService.listByCustomerBranch(filter));
    }


    @Operation(summary ="Add")
    @Deprecated
    @PostMapping
    public ResponseEntity<FiscalModuleAddDTO> add(@Valid @RequestBody FiscalModuleAddDTO fiscalModuleDTO) {
        log.debug("REST request to add new FiscalModuleDTO: {}", fiscalModuleDTO);
        customerBranchFiscalModuleService.add(fiscalModuleDTO);
        return ResponseEntity.ok(fiscalModuleDTO);
    }

    @Operation(summary ="Add New")
    @PostMapping("/new")
    public ResponseEntity<FiscalModuleAddDTO> addNew(@Valid @RequestBody FiscalModuleAddDTO fiscalModuleDTO) {
        log.debug("REST request to add new FiscalModuleDTO: {}", fiscalModuleDTO);
        customerBranchFiscalModuleService.addNew(fiscalModuleDTO);
        return ResponseEntity.ok(fiscalModuleDTO);
    }

    @Operation(summary ="Look Up")
    @GetMapping("/lookup")
    public ResponseEntity<Page<SerialNumberDTO>> lookUp(BaseFilter customerBranchFiscalModuleFilter) {
        log.debug("REST request to get fiscal module items: {}", customerBranchFiscalModuleFilter);
        return ResponseEntity.ok(customerBranchFiscalModuleService.lookUp(customerBranchFiscalModuleFilter));
    }

    @Operation(summary ="Items")
    @GetMapping("/items")
    public ResponseEntity<List<SerialNumberDTO>> getItems(BaseFilter filter) {
        log.debug("REST request to get customer branch fiscal module items: {}", filter);
        return ResponseEntity.ok(customerBranchFiscalModuleService.getItems(filter));
    }

}
