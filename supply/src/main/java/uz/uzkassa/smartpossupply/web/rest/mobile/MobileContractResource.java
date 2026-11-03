package uz.uzkassa.smartpossupply.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.contract.ContractLookUpDTO;
import uz.uzkassa.smartpossupply.dto.contract.ContractLookUpOfflineDTO;
import uz.uzkassa.smartpossupply.enums.ContractStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.ContractService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.mobileContractRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileContractResource {

    ContractService contractService;

    @Operation(summary = ApiConstants.LookUp, operationId ="contractLookUp")
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<ContractLookUpDTO>> lookUp(@ParameterObject BaseFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        filter.setStatus(ContractStatus.ACCEPTED.getCode());
        return ResponseEntity.ok(contractService.lookUp(filter));
    }

    @GetMapping(ApiConstants.lookUp + "/offline")
    public ResponseEntity<List<ContractLookUpOfflineDTO>> lookUpForOffline(@ParameterObject BaseFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        return ResponseEntity.ok(contractService.lookUpForOffline(filter));
    }

    @GetMapping(ApiConstants.offline + "/{customerId}")
    public ResponseEntity<List<ContractLookUpDTO>> getListForOfflineByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(contractService.getListForOfflineByCustomerId(customerId));
    }
}
