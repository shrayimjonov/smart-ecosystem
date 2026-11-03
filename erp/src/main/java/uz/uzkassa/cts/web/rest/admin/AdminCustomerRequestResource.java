package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.customerRequest.CustomerRequestDTO;
import uz.uzkassa.cts.dto.customerRequest.admin.CustomerRequestDetailAdminDTO;
import uz.uzkassa.cts.dto.customerRequest.admin.CustomerRequestListAdminDTO;
import uz.uzkassa.cts.enums.CallStatus;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.CustomerRequestStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.CustomerRequestFilter;
import uz.uzkassa.cts.service.CustomerRequestService;
import uz.uzkassa.cts.service.ExportReportService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminCustomerRequestRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminCustomerRequestResource {

    CustomerRequestService customerRequestService;
    ExportReportService exportReportService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CustomerRequestDTO customerRequestDTO) {
        return ResponseEntity.ok(customerRequestService.create(customerRequestDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CustomerRequestDTO customerRequestDTO) {
        return ResponseEntity.ok(customerRequestService.update(id, customerRequestDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<CustomerRequestDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(customerRequestService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<CustomerRequestListAdminDTO>> getList(@ParameterObject CustomerRequestFilter filter) {
        return ResponseEntity.ok(customerRequestService.getList(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") CustomerRequestStatus status) {
        customerRequestService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/{id}/call-status")
    public ResponseEntity<Void> updateCallStatus(@PathVariable Long id, @RequestParam("status") CallStatus callStatus) {
        customerRequestService.updateCallStatus(id, callStatus);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject CustomerRequestFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.CUSTOMER_REQUEST, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCSV(@ParameterObject CustomerRequestFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.CUSTOMER_REQUEST, ContentType.CSV);
        return ResponseEntity.ok().build();
    }
}
