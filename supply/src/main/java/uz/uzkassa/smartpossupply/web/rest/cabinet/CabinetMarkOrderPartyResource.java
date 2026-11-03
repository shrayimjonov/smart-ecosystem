package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.markorder.party.MarkOrderPartyDTO;
import uz.uzkassa.smartpossupply.dto.markorder.party.MarkOrderPartyListDTO;
import uz.uzkassa.smartpossupply.filters.MarkOrderPartyFilter;
import uz.uzkassa.smartpossupply.service.MarkOrderPartyService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetMarkOrderPartyRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetMarkOrderPartyResource {

    MarkOrderPartyService markOrderPartyService;

    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody MarkOrderPartyDTO markOrderPartyDTO) {
        markOrderPartyService.create(markOrderPartyDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<MarkOrderPartyListDTO>> getList(@ParameterObject MarkOrderPartyFilter filter) {
        return ResponseEntity.ok(markOrderPartyService.getList(filter));
    }

    @GetMapping("/retry/{id}")
    public ResponseEntity<Void> retry(@PathVariable Long id) {
        markOrderPartyService.retry(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Нанесение маркировок")
    @PutMapping("/apply/{partyId}")
    public ResponseEntity<Long> apply(@PathVariable("partyId") Long partyId) {
        return ResponseEntity.ok(markOrderPartyService.apply(partyId));
    }

}
