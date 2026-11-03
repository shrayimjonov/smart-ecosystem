package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.ofd.ConfirmCustomerBranchDTO;
import uz.uzkassa.cts.filter.CustomerBranchFilter;
import uz.uzkassa.cts.service.OfdCustomerBranchService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminOfdCustomerBranchRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminOfdCustomerBranchResource {

    OfdCustomerBranchService ofdCustomerBranchService;

    @PostMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestBody ConfirmCustomerBranchDTO confirmCustomerBranchDTO) {
        return ResponseEntity.ok(ofdCustomerBranchService.confirm(confirmCustomerBranchDTO));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject CustomerBranchFilter filter) {
        return ResponseEntity.ok(ofdCustomerBranchService.lookUp(filter));
    }
}
