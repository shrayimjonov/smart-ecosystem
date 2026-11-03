package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.cases.CaseDTO;
import uz.uzkassa.cts.dto.cases.CaseDetailDTO;
import uz.uzkassa.cts.dto.cases.CaseUpdateAssigneeDTO;
import uz.uzkassa.cts.dto.cases.CaseUpdateStatusDTO;
import uz.uzkassa.cts.dto.cases.request.CaseAcceptDto;
import uz.uzkassa.cts.dto.customer.CustomerLookUpDTO;
import uz.uzkassa.cts.dto.device.CustomerBranchDeviceListDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.enums.CaseType;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CaseFilter;
import uz.uzkassa.cts.service.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
@Tag(name = "Cases")
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Deprecated
public class CaseResource {

    CaseService caseService;
    BranchCustomerService customerService;
    CustomerBranchOldService customerBranchOldService;
    CustomerBranchFiscalModuleService customerBranchFiscalModuleService;
    CustomerDeviceService customerDeviceService;
    ExportReportService exportReportService;

    @Operation(summary = "Add")
    @PostMapping
    public ResponseEntity<CaseDTO> create(@Valid @RequestBody CaseDTO caseDTO) {
        log.debug("REST request to create new CaseDTO: {}", caseDTO);
        return ResponseEntity.ok(caseService.create(caseDTO));
    }

    @Operation(summary = "Add")
    @PutMapping("/accept")
    public ResponseEntity<CaseDetailDTO> accept(@Valid @RequestBody CaseAcceptDto acceptDto) {
        return ResponseEntity.ok(caseService.accept(acceptDto));
    }

    @Operation(summary = "Add")
    @GetMapping("/{id}/generate-template")
    public ResponseEntity<List<String>> accept(@PathVariable Long id) {
        return ResponseEntity.ok(caseService.generateTemplate(id));
    }

    @Operation(summary = "get data fields by case type")
    @GetMapping("/get-fields-by-type/{type}")
    public ResponseEntity<Object> getFields(@PathVariable CaseType type) {
        return ResponseEntity.ok(caseService.getFieldsByType(type));
    }

    @Operation(summary = "Get")
    @GetMapping("/{id}")
    public ResponseEntity<CaseDetailDTO> getById(@PathVariable Long id) {
        log.debug("REST request to get CaseDetailDTO : {}", id);
        return ResponseEntity.ok(caseService.get(id));
    }

    @Operation(summary = "List")
    @GetMapping
    public ResponseEntity<Page<CaseDetailDTO>> list(CaseFilter filter) {
        log.debug("REST request to get all CaseListDTO: {}", filter);
        return ResponseEntity.ok(caseService.list(filter));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject CaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.CASES, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCsv(@ParameterObject CaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.CASES, ContentType.CSV);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "get info")
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(CaseFilter filter) {
        log.debug("card payment request filter: {}", filter);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"case.xlsx\"")
                .body(caseService.download(filter));
    }

    @Operation(summary = "Update")
    @PutMapping
    public ResponseEntity<CaseDTO> update(@Valid @RequestBody CaseDTO caseDTO) {
        log.debug("REST request to update CaseUpdateDTO : {}", caseDTO);
        return ResponseEntity.ok(caseService.update(caseDTO));
    }

    @Operation(summary = "Update Status")
    @PutMapping("/status")
    public ResponseEntity<Void> updateStatus(@Valid @RequestBody CaseUpdateStatusDTO caseUpdateStatusDTO) {
        caseService.updateStatus(caseUpdateStatusDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Assignee Look Up")
    @GetMapping("/assignee/lookup")
    public ResponseEntity<Page<CommonDTO>> assigneeLookUp(BaseFilter filter) {
        log.debug("REST request to get case assignee lookup: {}", filter);
        return ResponseEntity.ok(caseService.getAssigneeLookUp(filter));
    }

    @Operation(summary = "Assign")
    @PutMapping("/assign")
    public ResponseEntity<Void> assign(@Valid @RequestBody CaseUpdateAssigneeDTO caseUpdateAssigneeDTO) {
        caseService.assign(caseUpdateAssigneeDTO);
        return ResponseEntity.ok().build();
    }


    //// DATA TO FILL TEMPLATES

    @Operation(summary = "get customers")
    @GetMapping("/customer/lookup")
    public ResponseEntity<Page<CustomerLookUpDTO>> lookUp(BaseFilter filter) {
        return ResponseEntity.ok(customerService.lookUp(filter));
    }

    @Operation(summary = "Customer Branch Items")
    @GetMapping("/branch/lookup")
    public ResponseEntity<List<CommonDTO>> branchLookUp(BaseFilter filter) {
        return ResponseEntity.ok(customerBranchOldService.lookUp(filter));
    }

    @Operation(summary = "get all device in branch")
    @GetMapping("/branch/devices/lookup")
    public ResponseEntity<Page<SerialNumberDTO>> branchDeviceLookUp(BaseFilter filter) {
        return ResponseEntity.ok(customerBranchFiscalModuleService.lookUp(filter));
    }

    @Operation(summary = "get all devices of customer")
    @GetMapping("/customer/devices/list")
    public ResponseEntity<Page<CustomerBranchDeviceListDTO>> list(BaseFilter filter) {
        return ResponseEntity.ok(customerDeviceService.list(filter));
    }

    @Operation(summary = "get Director name")
    @GetMapping("/customer/{customerId}/director")
    public ResponseEntity<String> getDirectorName(@PathVariable Long customerId) {
        return ResponseEntity.ok(caseService.getDirectorFullName(customerId));
    }

    @Operation(summary = "check phone existence")
    @GetMapping("/check-phone-existence/{phoneNumber}")
    public ResponseEntity<String> checkPhoneExistence(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(caseService.checkPhoneExistence(phoneNumber));
    }

    @Operation(summary = "get customer by phone")
    @GetMapping("/customer/{customerId}/phoneNumber")
    public ResponseEntity<String> getCustomerByPhone(@PathVariable Long customerId) {
        return ResponseEntity.ok(caseService.getCustomerByPhone(customerId));
    }

    @Operation(summary = "check login exists in smartpos and CTS")
    @GetMapping("/check-login-exists/{phoneNumber}")
    public ResponseEntity<Boolean> checkLoginByPhone(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(caseService.checkPhoneExists(phoneNumber));
    }
}
