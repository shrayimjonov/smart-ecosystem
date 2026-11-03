package uz.uzkassa.horeca.web.rest.mobile;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.eps.EpsServiceDTO;
import uz.uzkassa.horeca.service.EpsService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.mobileEpsServiceRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileEpsServiceResource {

    EpsService epsService;

    @GetMapping
    public ResponseEntity<List<EpsServiceDTO>> getList() {
        return ResponseEntity.ok(epsService.getList());
    }
}