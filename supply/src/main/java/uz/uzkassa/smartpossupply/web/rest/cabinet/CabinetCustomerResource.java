package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.company.CheckTinResultDTO;
import uz.uzkassa.smartpossupply.dto.customer.*;
import uz.uzkassa.smartpossupply.dto.importreport.CustomerImportReportDTO;
import uz.uzkassa.smartpossupply.enums.CompanyStatus;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.service.CompanyService;
import uz.uzkassa.smartpossupply.service.CustomerService;
import uz.uzkassa.smartpossupply.service.handlers.excel.CustomerListStrategy;
import uz.uzkassa.smartpossupply.service.handlers.excel.importreport.CustomerImportReportExcelHandler;
import uz.uzkassa.smartpossupply.service.handlers.excel.template.CustomerImportTemplateExcelHandler;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetCustomerRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCustomerResource {

    CustomerService customerService;
    CompanyService companyService;
    CustomerListStrategy customerListStrategy;
    CustomerImportTemplateExcelHandler customerImportTemplateExcelHandler;
    CustomerImportReportExcelHandler customerImportReportExcelHandler;

    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CustomerDTO customerDTO) {
        return ResponseEntity.ok(customerService.create(customerDTO).getId());
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<CustomerDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.get(id));
    }

    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CustomerDTO supplierDTO) {
        return ResponseEntity.ok(customerService.update(id, supplierDTO));
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<CustomerListDTO>> getList(@ParameterObject CompanyFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(customerService.getList(filter));
    }

    @Operation(summary = ApiConstants.ExportCSV)
    @GetMapping(ApiConstants.exportCsv)
    public ResponseEntity<Void> exportCsv(@ParameterObject CompanyFilter filter) {
        customerListStrategy.generate(filter, FileType.CSV);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.ExportExcel)
    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportExcel(@ParameterObject CompanyFilter filter) {
        customerListStrategy.generate(filter, FileType.EXCEL);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = ApiConstants.Statistics)
    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<CustomerStatisticDTO> getStatistics(@ParameterObject BaseFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new CustomerStatisticDTO());
        }
        return ResponseEntity.ok(customerService.getStatistics(filter));
    }

    @Operation(summary = "Statistics by Customer")
    @GetMapping(ApiConstants.id + ApiConstants.statistics)
    public ResponseEntity<StatisticsByCustomerDTO> getStatisticsByCustomer(@PathVariable Long id) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new StatisticsByCustomerDTO());
        }
        return ResponseEntity.ok(customerService.getStatisticsByCustomer(id));
    }

    @Operation(summary = ApiConstants.LookUp)
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CustomerWithLimitDTO>> lookUp(@ParameterObject CompanyFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(customerService.lookUp(filter));
    }

    @Operation(summary = ApiConstants.Check)
    @GetMapping(ApiConstants.check + "/{tin}")
    public ResponseEntity<CheckTinResultDTO> checkTin(@PathVariable(name = "tin") String tin) {
        return ResponseEntity.ok(companyService.checkTin(tin));
    }

    @Operation(summary = "Financial Discount")
    @PutMapping(ApiConstants.id + "/financial-discount")
    public ResponseEntity<Void> financialDiscount(
            @PathVariable Long id,
            @RequestParam("hasFinancialDiscount") boolean hasFinancialDiscount
    ) {
        customerService.financialDiscount(id, hasFinancialDiscount);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") String status) {
        customerService.updateStatus(id, CompanyStatus.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<CompanyStatus>> getStatus() {
        return ResponseEntity.ok(EnumSet.of(CompanyStatus.ACTIVE, CompanyStatus.IN_ACTIVE));
    }

    @Operation(summary = ApiConstants.Template)
    @GetMapping("/download-template")
    public ResponseEntity<Resource> getTemplate() {
        return customerImportTemplateExcelHandler.download();
    }

    @Operation(summary = ApiConstants.Import)
    @PostMapping(value = ApiConstants.importStr, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> importCustomers(@RequestPart("file") MultipartFile file, @RequestPart(name = "importReportDTO", required = false) CustomerImportReportDTO importReportDTO) {
        customerImportReportExcelHandler.importReport(file, EntityTypeEnum.CUSTOMER, FileType.EXCEL, importReportDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/limits")
    public ResponseEntity<Void> createOrUpdate(@RequestBody CustomerLimitDTO customerLimitDTO) {
        customerService.createOrUpdateLimit(customerLimitDTO);
        return ResponseEntity.ok().build();
    }
}
