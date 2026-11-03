package uz.uzkassa.smartpossupply.web.rest.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.branch.BranchAddressLookupDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.filters.CustomerBranchFilter;
import uz.uzkassa.smartpossupply.service.CustomerBranchService;

import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.01.2023 13:43
 */
@RestController
@RequestMapping(ApiConstants.adminCustomerBranchRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminCustomerBranchResource {

    CustomerBranchService customerBranchService;

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<BranchAddressLookupDTO>> lookUp(@ParameterObject CustomerBranchFilter filter) {
        if (filter.getCustomerId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return ResponseEntity.ok(customerBranchService.lookUpWithAddress(filter));
    }

    @PostMapping("/create-default")
    public ResponseEntity<Void> createDefault() {
        customerBranchService.createDefaultCustomerBranch();
        return ResponseEntity.ok().build();
    }
}
