package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.party.PartyDTO;
import uz.uzkassa.smartpossupply.dto.party.PartyDetailsDTO;
import uz.uzkassa.smartpossupply.dto.party.PartyDetailsForStockInDTO;
import uz.uzkassa.smartpossupply.dto.party.PartyListDTO;
import uz.uzkassa.smartpossupply.enums.PartyStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.PartyService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetPartyRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetPartyResource {

    PartyService partyService;

    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody @Valid PartyDTO partyDTO) {
        return ResponseEntity.ok(partyService.create(partyDTO));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<PartyDetailsDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(partyService.get(id));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping("/{partyId}/{branchId}")
    public ResponseEntity<PartyDetailsForStockInDTO> getByBranch(@PathVariable("partyId") Long partyId, @PathVariable("branchId") Long branchId) {
        return ResponseEntity.ok(partyService.getByBranchNewRelease(partyId, branchId));
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<PartyListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(partyService.getList(filter));
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<PartyStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(PartyStatus.class));
    }

    @Operation(summary = ApiConstants.LookUp)
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(partyService.getLookUp(filter));
    }

    @Operation(summary = ApiConstants.Update)
    @PutMapping("/send/{id}")
    public ResponseEntity<String> sendToTuron(@PathVariable("id") Long id) {
        return ResponseEntity.ok(partyService.sendToTuron(id));
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        partyService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
