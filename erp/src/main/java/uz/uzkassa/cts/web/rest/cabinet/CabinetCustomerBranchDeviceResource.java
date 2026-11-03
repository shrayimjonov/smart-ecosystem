package uz.uzkassa.cts.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.service.SmartposService;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.cabinetCustomerBranchDeviceRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCustomerBranchDeviceResource {

    SmartposService smartposService;

    @PutMapping(value = "/{id}/sync/smartpos")
    public ResponseEntity<Void> updateSmartpos(@PathVariable Long id) {
        smartposService.updateCustomerByType(id);
        return ResponseEntity.ok().build();
    }
}
