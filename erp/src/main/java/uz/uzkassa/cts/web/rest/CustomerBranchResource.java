package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.apay.ApayRegisterCustomerBranchesDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.changelog.ChangeLogListDTO;
import uz.uzkassa.cts.dto.customer.*;
import uz.uzkassa.cts.dto.sync.SyncCustomerBranchListDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CompanyCustomerBranchFilter;
import uz.uzkassa.cts.filter.CustomerBranchFilter;
import uz.uzkassa.cts.service.APayCustomerBranchService;
import uz.uzkassa.cts.service.CustomerBranchOldService;

import java.util.List;

/**
 * REST controller for managing {@link uz.uzkassa.cts.domain.CustomerBranch}.
 */
@RestController
@RequestMapping("/api/customer/branches")
@Tag(name = "Customer Branch (TT)")
@Slf4j
@RequiredArgsConstructor
@Deprecated
public class CustomerBranchResource {

    private final CustomerBranchOldService customerBranchOldService;
    private final APayCustomerBranchService aPayCustomerBranchService;

    @Operation(summary = "Add")
    @PostMapping
    public ResponseEntity<CustomerBranchOldDTO> create(@Valid @RequestBody CustomerBranchOldDTO customerBranch) {
        log.debug("REST request to create new CustomerBranchDTO: {}", customerBranch);
        return ResponseEntity.ok(customerBranchOldService.create(customerBranch));
    }

    @Operation(summary = "Update")
    @PutMapping
    public ResponseEntity<CustomerBranchUpdateDTO> update(@Valid @RequestBody CustomerBranchUpdateDTO customerBranchUpdateDTO) {
        log.debug("REST request to update CustomerBranchUpdateDTO : {}", customerBranchUpdateDTO);
        return ResponseEntity.ok(customerBranchOldService.update(customerBranchUpdateDTO));
    }

    @Operation(summary = "Confirm Doc")
    @PutMapping("/confirm/doc")
    public ResponseEntity<CustomerBranchConfirmDocDTO> confirmDoc(@Valid @RequestBody CustomerBranchConfirmDocDTO customerBranchConfirmDocDTO) {
        log.debug("REST request to confirm doc CustomerBranchConfirmDocDTO : {}", customerBranchConfirmDocDTO);
        return ResponseEntity.ok(customerBranchOldService.confirmDoc(customerBranchConfirmDocDTO));
    }

    @Operation(summary = "List")
    @GetMapping
    public ResponseEntity<Page<CustomerBranchListDTO>> list(CustomerBranchFilter filter) {
        log.debug("REST request to get all customer branches : {}", filter.getCustomerId());
        return ResponseEntity.ok(customerBranchOldService.list(filter));
    }

    @Operation(summary = "List By Employee")
    @GetMapping("/by-employee")
    public ResponseEntity<Page<CustomerBranchShortListDTO>> listByEmployee(BaseFilter filter) {
        log.debug("REST request to get all CustomerShortDTO : {}", filter);
        return ResponseEntity.ok(customerBranchOldService.listByEmployee(filter));
    }

    @Operation(summary = "List By Branch")
    @GetMapping("/by-branch")
    public ResponseEntity<Page<CustomerBranchShortListDTO>> listByBranch(BaseFilter filter) {
        log.debug("REST request to get all CustomerShortDTO : {}", filter);
        return ResponseEntity.ok(customerBranchOldService.listByEmployee(filter));
    }

    @Operation(summary = "Histories")
    @GetMapping("/histories")
    public ResponseEntity<Page<ChangeLogListDTO>> getHistories(CustomerBranchFilter filter) {
        log.debug("REST request to get all customer branch histories : {}", filter.getCustomerId());
        return ResponseEntity.ok(customerBranchOldService.getHistories(filter));
    }

    @Operation(summary = "Get")
    @GetMapping("/{id}")
    public ResponseEntity<CustomerBranchDetailDTO> getById(@PathVariable(value = "id") Long id) {
        log.debug("REST request to get customer branch : {}", id);
        return ResponseEntity.ok(customerBranchOldService.get(id));
    }

    @Operation(summary = "All List", description = "New API: /api/admin/v1/customer-branches")
    @GetMapping("/all")
    @Deprecated
    public ResponseEntity<Page<AdminCustomerBranchListDTO>> listAll(CompanyCustomerBranchFilter filter) {
        log.debug("REST request to get all customer branches : {}", filter);
        return ResponseEntity.ok(customerBranchOldService.getAll(filter));
    }

    @Operation(summary = "Get Branches With Fiscal Devices")
    @GetMapping("/all-by-fiscal-devices")
    public ResponseEntity<Page<AdminCustomerBranchListByFiscalDevicesDTO>> getAllByFiscalDevices(CompanyCustomerBranchFilter filter) {
        log.debug("REST request to get branches by fiscal devices : {}", filter);
        return ResponseEntity.ok(customerBranchOldService.getAllByFiscalDevices(filter));
    }

    @Operation(summary = "List for Map")
    @GetMapping("/map")
    public ResponseEntity<List<CustomerBranchListMapDTO>> listForMap(CompanyCustomerBranchFilter filter) {
        log.debug("REST request to get all customer branches for Map: {}", filter);
        return ResponseEntity.ok(customerBranchOldService.listForMap(filter));
    }

    @Operation(summary = "Empty Customer Branches. KKM is not registered")
    @GetMapping("/empties/{customerId}")
    public ResponseEntity<List<CommonDTO>> getEmptyCustomerBranches(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerBranchOldService.getEmptyCustomerBranches(customerId));
    }

    @Operation(summary = "Customer Branch Items")
    @GetMapping("/lookup")
    public ResponseEntity<List<CommonDTO>> lookUp(BaseFilter filter) {
        log.debug("REST request to get all customer branch items : {}", filter);
        return ResponseEntity.ok(customerBranchOldService.lookUp(filter));
    }

    @Operation(summary = "Customer Branches By Customer Tin")
    @GetMapping("/items")
    public ResponseEntity<CustomerBranchLookUpDTO> getItems(@Valid BaseFilter filter) {
        log.debug("REST request to get all customer branch items : {}", filter);
        return ResponseEntity.ok(customerBranchOldService.getItems(filter));
    }

    @Operation(summary = "Activate TT")
    @PutMapping("/{id}/{documentType}/activate")
    public ResponseEntity<Void> activate(@PathVariable(value = "id") Long id,
                                         @PathVariable(value = "documentType") String documentType,
                                         @RequestParam("file") MultipartFile file) {
        log.debug("REST request to activate customer branch: {} ", id);
        customerBranchOldService.activate(id, documentType, file);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Upload Document")
    @PutMapping("/{id}/{documentType}")
    public ResponseEntity<Void> uploadDocument(@PathVariable(value = "id") Long id,
                                               @PathVariable(value = "documentType") String documentType,
                                               @RequestParam("file") MultipartFile file) {
        log.debug("REST request to upload documents: {} ", id);
        customerBranchOldService.uploadDocument(id, documentType, file);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete Document")
    @DeleteMapping("/{id}/{fileId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable(value = "id") Long id,
                                               @PathVariable(value = "fileId") Long fileId) {
        log.debug("REST request to delete documents: {} ", id);
        customerBranchOldService.deleteDocument(id, fileId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get Count List to Sync")
    @GetMapping("/sync/count")
    public ResponseEntity<Long> getListCountToSync() {
        log.debug("REST request to get all customer branches count");
        return ResponseEntity.ok(customerBranchOldService.getListCountToSync());
    }

    @Operation(summary = "Get List to Sync")
    @GetMapping("/sync/list")
    public ResponseEntity<Page<SyncCustomerBranchListDTO>> getListToSync(BaseFilter filter) {
        log.debug("REST request to get all customer branches : {}", filter);
        return ResponseEntity.ok(customerBranchOldService.getListToSync(filter));
    }

    @Operation(summary = "Sync with Smartpos")
    @PostMapping("/sync/smartpos/{id}")
    public ResponseEntity<Void> syncWithSmartPos(@PathVariable(value = "id") Long id) {
        customerBranchOldService.syncWithSmartPos(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Sync batch with Smartpos")
    @PostMapping("/sync/smartpos/batch")
    public ResponseEntity<Void> batchWithSmartPos() {
        customerBranchService.batchWithSmartPos();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Sync with A-Pay")
    @PostMapping("/sync/a-pay")
    public ResponseEntity<Void> syncWithAPay() {
        customerBranchOldService.syncCustomerBranchListAPay();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Scanner")
    @PutMapping(value = "/scanner/{customerBranchId}/{scannerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> scanner(@RequestParam(value = "file", required = false) MultipartFile file,
                                        @PathVariable("customerBranchId") Long customerBranchId,
                                        @PathVariable("scannerId") Long scannerId) {
        log.debug("REST request to CB scanner : {}", file);
        return ResponseEntity.ok(customerBranchOldService.connectScanner(customerBranchId, scannerId, file));
    }

    @Operation(summary = "Scanner remove")
    @PutMapping("/remove-scanner/{id}")
    public ResponseEntity<Long> removeScanner(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(customerBranchOldService.removeScanner(id));
    }

    @Operation(summary = "Apay register")
    @PutMapping("/apay/register")
    public void apayRegister(@Valid @RequestBody ApayRegisterCustomerBranchesDTO dto) {
        log.debug("REST request to apayRegister dto: {}", dto);
        aPayCustomerBranchService.quickRegister(dto);
    }
}
