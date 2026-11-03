package uz.uzkassa.smartpossupply.web.rest.admin;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.company.CheckTinResultDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerDetailDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerListDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerWithLimitDTO;
import uz.uzkassa.smartpossupply.dto.importreport.CustomerImportReportDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.service.CompanyService;
import uz.uzkassa.smartpossupply.service.CustomerService;
import uz.uzkassa.smartpossupply.service.handlers.excel.importreport.CustomerImportReportExcelHandler;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminCustomerRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminCustomerResource {

    CustomerService customerService;
    CompanyService companyService;
    CustomerImportReportExcelHandler customerImportReportExcelHandler;

    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CustomerDTO customerDTO) {
        return ResponseEntity.ok(customerService.create(customerDTO).getId());
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<CustomerListDTO>> getList(@ParameterObject CompanyFilter filter) {
        return ResponseEntity.ok(customerService.getList(filter));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<CustomerDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.get(id));
    }

    @Operation(summary = ApiConstants.Check)
    @GetMapping(ApiConstants.check + "/{tin}")
    public ResponseEntity<CheckTinResultDTO> checkTin(@PathVariable(name = "tin") String tin) {
        return ResponseEntity.ok(companyService.checkTin(tin));
    }

    @Operation(summary = ApiConstants.LookUp)
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CustomerWithLimitDTO>> lookUp(@ParameterObject CompanyFilter filter) {
        return ResponseEntity.ok(customerService.lookUp(filter));
    }

    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CustomerDTO supplierDTO) {
        return ResponseEntity.ok(customerService.update(id, supplierDTO));
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Import)
    @PostMapping(value = ApiConstants.importStr, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> importCustomers(@RequestPart("file") MultipartFile file, @RequestPart(name = "importReportDTO") CustomerImportReportDTO importReportDTO) {
        customerImportReportExcelHandler.importReport(file, EntityTypeEnum.CUSTOMER, FileType.EXCEL, importReportDTO);
        return ResponseEntity.ok().build();
    }
}
