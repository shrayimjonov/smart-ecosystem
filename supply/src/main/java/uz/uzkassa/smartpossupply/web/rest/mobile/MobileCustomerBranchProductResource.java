package uz.uzkassa.smartpossupply.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.customerbranch.CustomerBranchProductDTO;
import uz.uzkassa.smartpossupply.dto.customerbranch.CustomerBranchProductListDTO;
import uz.uzkassa.smartpossupply.filters.CustomerBranchFilter;
import uz.uzkassa.smartpossupply.service.CustomerBranchProductService;

import java.util.ArrayList;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date:  30.11.2023
 */
@RestController
@RequestMapping(ApiConstants.mobileCustomerBranchProductRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileCustomerBranchProductResource {

    CustomerBranchProductService customerBranchProductService;

    @Operation(summary = ApiConstants.Create, operationId = "createCustomerBranchProduct")
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CustomerBranchProductDTO customerBranchProductDTO) {
        return ResponseEntity.ok(customerBranchProductService.create(customerBranchProductDTO));
    }

    @Operation(summary = ApiConstants.List, operationId = "getCustomerBranchProductList")
    @GetMapping
    public ResponseEntity<Page<CustomerBranchProductListDTO>> getList(@ParameterObject CustomerBranchFilter filter) {
        if (filter.getId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>()));
        }
        return ResponseEntity.ok(customerBranchProductService.getList(filter));
    }
}
