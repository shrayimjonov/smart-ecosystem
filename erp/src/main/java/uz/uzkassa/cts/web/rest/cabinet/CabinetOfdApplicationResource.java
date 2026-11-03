package uz.uzkassa.cts.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.ofdapplication.cabinet.OfdApplicationListCabinetDTO;
import uz.uzkassa.cts.filter.OfdApplicationFilter;
import uz.uzkassa.cts.service.OfdApplicationService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetOfdApplicationRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetOfdApplicationResource {

    OfdApplicationService ofdApplicationService;

    @GetMapping
    public ResponseEntity<Page<OfdApplicationListCabinetDTO>> getList(@ParameterObject OfdApplicationFilter filter) {
        return ResponseEntity.ok(ofdApplicationService.getListForCabinet(filter));
    }

    @GetMapping(ApiConstants.sync + "/{customerId}")
    public ResponseEntity<Void> sync(@PathVariable Long customerId) {
        ofdApplicationService.sync(customerId);
        return ResponseEntity.ok().build();
    }
}
