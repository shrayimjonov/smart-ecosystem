package uz.uzkassa.smartpossupply.web.rest.admin;

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
import uz.uzkassa.smartpossupply.dto.contract.ContractDetailDTO;
import uz.uzkassa.smartpossupply.dto.contract.ContractLookUpDTO;
import uz.uzkassa.smartpossupply.enums.ContractStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.ContractService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 30.10.2023
 */
@RestController
@RequestMapping(ApiConstants.adminContractRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminContractResource {

    ContractService contractService;

    @Operation(summary = ApiConstants.LookUp)
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<ContractLookUpDTO>> lookUp(@ParameterObject BaseFilter filter) {
        filter.setStatus(ContractStatus.ACCEPTED.getCode());
        return ResponseEntity.ok(contractService.lookUp(filter));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<ContractDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.get(id));
    }
}
