package uz.uzkassa.smartpossupply.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.lgota.LgotaPrefixDTO;
import uz.uzkassa.smartpossupply.service.LgotaPrefixService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetLgotaPrefixRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetLgotaPrefixResource {

    LgotaPrefixService lgotaPrefixService;

    @GetMapping(ApiConstants.items)
    public ResponseEntity<List<LgotaPrefixDTO>> getItems() {
        return ResponseEntity.ok(lgotaPrefixService.getItems());
    }
}
