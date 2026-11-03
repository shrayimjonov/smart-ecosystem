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
import uz.uzkassa.smartpossupply.dto.base.CommonCodeDTO;
import uz.uzkassa.smartpossupply.dto.customerbranch.CustomerBranchDTO;
import uz.uzkassa.smartpossupply.dto.customerbranch.MobileCustomerBranchDetailDTO;
import uz.uzkassa.smartpossupply.dto.customerbranch.MobileCustomerBranchListDTO;
import uz.uzkassa.smartpossupply.filters.CustomerBranchFilter;
import uz.uzkassa.smartpossupply.service.CustomerBranchService;

import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date:  03.02.2023 14:55
 */
@RestController
@RequestMapping(ApiConstants.mobileCustomerBranchRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileCustomerBranchResource {

    CustomerBranchService customerBranchService;

    @Operation(summary = ApiConstants.Create, operationId ="createCustomerBranch")
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CustomerBranchDTO customerBranchDTO) {
        return ResponseEntity.ok(customerBranchService.create(customerBranchDTO));
    }

    @Operation(summary = ApiConstants.Update, operationId ="updateCustomerBranch")
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CustomerBranchDTO customerBranchDTO) {
        return ResponseEntity.ok(customerBranchService.update(id, customerBranchDTO));
    }

    @Operation(summary = ApiConstants.LookUp, operationId ="customerBranchLookUp")
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonCodeDTO>> lookUp(@ParameterObject CustomerBranchFilter filter) {
        return ResponseEntity.ok(customerBranchService.lookUp(filter));
    }

    @Operation(summary = ApiConstants.Get, operationId ="getCustomerBranch")
    @GetMapping(ApiConstants.id)
    public ResponseEntity<MobileCustomerBranchDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(customerBranchService.getForMobile(id));
    }

    @Operation(summary = ApiConstants.List, operationId ="getCustomerBranchList")
    @GetMapping
    public ResponseEntity<Page<MobileCustomerBranchListDTO>> getList(@ParameterObject CustomerBranchFilter filter) {
        if (filter.getCustomerId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(customerBranchService.getListForMobile(filter));
    }

    @Operation(summary = ApiConstants.List, operationId ="getCustomerBranchListForOffline")
    @GetMapping("/offline")
    public ResponseEntity<Page<MobileCustomerBranchDetailDTO>> getListForOffline(@ParameterObject CustomerBranchFilter filter) {
        return ResponseEntity.ok(customerBranchService.getListForOffline(filter));
    }
}
