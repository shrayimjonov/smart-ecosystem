package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.customerbranchapplication.admin.CustomerBranchApplicationDetailAdminDTO;
import uz.uzkassa.cts.dto.customerbranchapplication.admin.CustomerBranchApplicationListAdminDTO;
import uz.uzkassa.cts.dto.customerbranchapplication.base.CustomerBranchApplicationDTO;
import uz.uzkassa.cts.dto.customerbranchapplication.base.CustomerBranchApplicationUpdateDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.CustomerBranchApplicationService;
import uz.uzkassa.cts.service.ExportReportService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminCustomerBranchApplicationRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminCustomerBranchApplicationResource {

    CustomerBranchApplicationService customerBranchApplicationService;
    ExportReportService exportReportService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CustomerBranchApplicationDTO customerBranchApplicationDTO) {
        return ResponseEntity.ok(customerBranchApplicationService.create(customerBranchApplicationDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CustomerBranchApplicationDTO customerBranchApplicationDTO) {
        return ResponseEntity.ok(customerBranchApplicationService.update(id, customerBranchApplicationDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<CustomerBranchApplicationDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(customerBranchApplicationService.getForAdmin(id));
    }

    @GetMapping
    public ResponseEntity<Page<CustomerBranchApplicationListAdminDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(customerBranchApplicationService.getListForAdmin(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody CustomerBranchApplicationUpdateDTO dto) {
        customerBranchApplicationService.updateStatus(id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerBranchApplicationService.deleteById(id, true);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.CUSTOMER_BRANCH_APPLICATION, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCSV(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.CUSTOMER_BRANCH_APPLICATION, ContentType.CSV);
        return ResponseEntity.ok().build();
    }
}
