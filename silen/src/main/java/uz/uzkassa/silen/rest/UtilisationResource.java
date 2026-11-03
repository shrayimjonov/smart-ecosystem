package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.silen.dto.UtilisationDTO;
import uz.uzkassa.silen.integration.turon.UtilisationResponse;
import uz.uzkassa.silen.service.UtilisationService;

@RestController
@RequestMapping("/api/utilise")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UtilisationResource {
    UtilisationService utilisationService;

    @PostMapping
    public ResponseEntity<UtilisationResponse> create(@RequestBody @Valid UtilisationDTO utilisationDTO) {
        return ResponseEntity.ok(utilisationService.create(utilisationDTO));
    }

}
