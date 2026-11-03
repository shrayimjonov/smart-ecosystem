package uz.uzkassa.smartpossupply.web.rest.terminal;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.base.CommonCodeDTO;
import uz.uzkassa.smartpossupply.dto.customerbranch.MobileCustomerBranchListDTO;
import uz.uzkassa.smartpossupply.filters.CustomerBranchFilter;
import uz.uzkassa.smartpossupply.service.CustomerBranchService;

import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date:  17.10.2024
 */
@RestController
@RequestMapping(ApiConstants.terminalCustomerBranchRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TerminalCustomerBranchResource {

    CustomerBranchService customerBranchService;

    @Operation(summary = ApiConstants.LookUp, operationId = "customerBranchLookUp")
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonCodeDTO>> lookUp(@ParameterObject CustomerBranchFilter filter) {
        return ResponseEntity.ok(customerBranchService.lookUp(filter));
    }

    @Operation(summary = ApiConstants.List, operationId ="getCustomerBranchList")
    @GetMapping
    public ResponseEntity<Page<MobileCustomerBranchListDTO>> getList(@ParameterObject CustomerBranchFilter filter) {
        if (filter.getCustomerId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(customerBranchService.getListForMobile(filter));
    }
}
