package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.dto.customer.CustomerDTO;
import uz.uzkassa.cts.dto.customer.admin.CustomerDetailAdminDTO;
import uz.uzkassa.cts.dto.customer.admin.CustomerListAdminDTO;
import uz.uzkassa.cts.dto.customer.admin.CustomerStatisticsAdminDTO;
import uz.uzkassa.cts.dto.user.PhoneDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.CustomerStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.CustomerFilter;
import uz.uzkassa.cts.service.CustomerService;
import uz.uzkassa.cts.service.ExportReportService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminCustomerRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminCustomerResource {

    CustomerService customerService;
    ExportReportService exportReportService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CustomerDTO customerDTO) {
        return ResponseEntity.ok(customerService.create(customerDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CustomerDTO customerDTO) {
        return ResponseEntity.ok(customerService.update(id, customerDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<CustomerDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getForAdmin(id));
    }

    @GetMapping
    public ResponseEntity<Page<CustomerListAdminDTO>> getList(@ParameterObject CustomerFilter filter) {
        return ResponseEntity.ok(customerService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CompanyCommonDTO>> lookUp(@ParameterObject CustomerFilter filter) {
        return ResponseEntity.ok(customerService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") CustomerStatus status) {
        customerService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/phone")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody PhoneDTO phoneDTO) {
        customerService.updatePhone(id, phoneDTO);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/white-list")
    public ResponseEntity<Void> whiteList(@PathVariable Long id, @RequestParam("whiteList") boolean whiteList) {
        customerService.whiteList(id, whiteList);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/customer-info-from-nic/{tin}")
    public ResponseEntity<String> getCustomerInfoFromNic(@PathVariable String tin) {
        return ResponseEntity.ok(customerService.getCustomerByTinFromNic(tin));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject CustomerFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.CUSTOMER, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCSV(@ParameterObject CustomerFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.CUSTOMER, ContentType.CSV);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.idStatistics)
    public ResponseEntity<CustomerStatisticsAdminDTO> getStatistics(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getStatistics(id));
    }
}
