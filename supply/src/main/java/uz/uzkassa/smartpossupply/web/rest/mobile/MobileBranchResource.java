package uz.uzkassa.smartpossupply.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.branch.BranchLookUpDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.BranchService;

import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date:  03.02.2023 14:55
 */
@RestController
@RequestMapping(ApiConstants.mobileBranchRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileBranchResource {

    BranchService branchService;

    @Operation(summary = ApiConstants.LookUp, operationId ="branchLookUp")
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<BranchLookUpDTO>> lookUp(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(branchService.lookUp(filter));
    }
}
