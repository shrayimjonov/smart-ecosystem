package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.device.CustomerBranchDeviceListDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.fm.CustomerFiscalModuleDTO;
import uz.uzkassa.cts.dto.fm.CustomerFiscalModuleRegisterDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.CustomerFiscalModuleService;

/**
 * REST controller for managing {@link uz.uzkassa.cts.domain.CustomerFiscalModule}.
 */
@RestController
@RequestMapping("/api/customer/fiscal/modules")
@Tag(name = "Customer Fiscal Modules")
@Slf4j
@Deprecated
public class CustomerFiscalModuleResource {

    private final CustomerFiscalModuleService customerFiscalModuleService;

    public CustomerFiscalModuleResource(CustomerFiscalModuleService customerFiscalModuleService) {
        this.customerFiscalModuleService = customerFiscalModuleService;
    }

    @Operation(summary ="List")
    @GetMapping
    public ResponseEntity<Page<CustomerBranchDeviceListDTO>> list(BaseFilter filter) {
        log.debug("REST request to get customer branch fiscal modules list: {}", filter);
        return ResponseEntity.ok(customerFiscalModuleService.list(filter));
    }

    @Operation(summary ="Add")
    @PostMapping
    public ResponseEntity<Void> add(@Valid @RequestBody CustomerFiscalModuleDTO customerFiscalModuleDTO) {
        log.debug("REST request to add fiscal module to Customer: {}", customerFiscalModuleDTO.getCustomerId());
        customerFiscalModuleService.add(customerFiscalModuleDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="Register")
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody CustomerFiscalModuleRegisterDTO customerFiscalModuleRegisterDTO) {
        log.debug("REST request to add fiscal module to Customer: {}", customerFiscalModuleRegisterDTO.getCustomerId());
        customerFiscalModuleService.register(customerFiscalModuleRegisterDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="Look Up")
    @GetMapping("/lookup")
    public ResponseEntity<Page<SerialNumberDTO>> lookUp(BaseFilter filter) {
        log.debug("REST request to get customer device items: {}", filter);
        return ResponseEntity.ok(customerFiscalModuleService.lookUp(filter));
    }

    @Operation(summary ="Delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete customer fiscal module: {}", id);
        customerFiscalModuleService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="Generate Template")
    @GetMapping("/{id}/generate-template")
    public ResponseEntity<String> getTemplateById(@PathVariable Long id) {
        return ResponseEntity.ok(customerFiscalModuleService.getHtmlTemplate(id));
    }
}
