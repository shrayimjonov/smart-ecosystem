package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.ofd.ConfirmCustomerBranchDTO;
import uz.uzkassa.cts.service.OfdCustomerBranchService;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminOfdCustomerRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminOfdCustomerResource {

    OfdCustomerBranchService ofdCustomerBranchService;

    @PostMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestBody ConfirmCustomerBranchDTO confirmCustomerBranchDTO) {
        return ResponseEntity.ok(ofdCustomerBranchService.confirm(confirmCustomerBranchDTO));
    }
}
