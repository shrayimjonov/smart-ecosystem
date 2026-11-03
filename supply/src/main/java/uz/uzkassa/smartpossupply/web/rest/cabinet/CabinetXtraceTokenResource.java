package uz.uzkassa.smartpossupply.web.rest.cabinet;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.xtrace.XtraceTokenDTO;
import uz.uzkassa.smartpossupply.dto.xtrace.XtraceTokenDetailDTO;
import uz.uzkassa.smartpossupply.service.XtraceTokenService;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.cabinetXtraceTokenRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetXtraceTokenResource {

    XtraceTokenService xtraceTokenService;

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody XtraceTokenDTO xtraceTokenDTO) {
        return ResponseEntity.ok(xtraceTokenService.create(xtraceTokenDTO));
    }

    @GetMapping
    public ResponseEntity<XtraceTokenDetailDTO> get() {
        return ResponseEntity.ok(xtraceTokenService.get());
    }

    @DeleteMapping
    public void delete() {
        xtraceTokenService.deleteXTraceToken();
    }
}
