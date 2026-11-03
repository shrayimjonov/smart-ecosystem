package uz.uzkassa.smartpossupply.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.company.CheckTinResultDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerDetailDTO;
import uz.uzkassa.smartpossupply.dto.customer.mobile.CustomerListMobileDTO;
import uz.uzkassa.smartpossupply.dto.customer.mobile.CustomerDetailMobileDTO;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.service.CompanyService;
import uz.uzkassa.smartpossupply.service.CustomerService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.mobileCustomerRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileCustomerResource {

    CustomerService customerService;
    CompanyService companyService;

    @Operation(summary = ApiConstants.Create, operationId ="createCustomer")
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CustomerDTO customerDTO) {
        return ResponseEntity.ok(customerService.create(customerDTO).getId());
    }

    @Operation(summary = ApiConstants.Get, operationId ="getCustomer")
    @GetMapping(ApiConstants.id)
    public ResponseEntity<CustomerDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.get(id));
    }

    @Operation(summary = ApiConstants.Update, operationId ="updateCustomer")
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CustomerDTO supplierDTO) {
        return ResponseEntity.ok(customerService.update(id, supplierDTO));
    }

    @Operation(summary = ApiConstants.List, operationId ="getCustomerList")
    @GetMapping
    public ResponseEntity<Page<CustomerListMobileDTO>> getList(@ParameterObject CompanyFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(customerService.getListForMobile(filter));
    }

    @Operation(summary = ApiConstants.List, operationId = "getCustomerListForOffline")
    @GetMapping("/offline")
    public ResponseEntity<Page<CustomerDetailMobileDTO>> getListForOffline(@ParameterObject CompanyFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(customerService.getListForOffline(filter));
    }

    @Operation(summary = ApiConstants.Check, operationId ="checkTin")
    @GetMapping(ApiConstants.check + "/{tin}")
    public ResponseEntity<CheckTinResultDTO> checkTin(@PathVariable(name = "tin") String tin) {
        return ResponseEntity.ok(companyService.checkTin(tin));
    }

    @Operation(summary = ApiConstants.LookUp, operationId ="customerLookUp")
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CompanyBaseDTO>> lookUp(@ParameterObject CompanyFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(customerService.lookUpForMobile(filter));
    }
}
