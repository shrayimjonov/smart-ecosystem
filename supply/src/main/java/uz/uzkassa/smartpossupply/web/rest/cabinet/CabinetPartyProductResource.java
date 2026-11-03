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
import uz.uzkassa.smartpossupply.dto.party.*;
import uz.uzkassa.smartpossupply.filters.AicProductsFilter;
import uz.uzkassa.smartpossupply.service.PartyProductService;

import java.util.List;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetPartyProductRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetPartyProductResource {

    PartyProductService partyProductService;

    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid PartyDTO partyDTO) {
        partyProductService.create(partyDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<PartyProductDetailsDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(partyProductService.get(id));
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping("/party/{id}")
    public ResponseEntity<List<PartyProductListDTO>> getList(@PathVariable("id") Long partyId) {
        return ResponseEntity.ok(partyProductService.getAll(partyId));
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping("/marks")
    public ResponseEntity<Set<String>> getCodes(@ParameterObject PartyProductCodesFilterDTO filter) {
        return ResponseEntity.ok(partyProductService.getAllCodes(filter));
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping("/by-product")
    public ResponseEntity<Page<PartyProductLookUpForAicDTO>> listProductsByFilter(@ParameterObject AicProductsFilter filter) {
        return ResponseEntity.ok(partyProductService.listProductsByFilter(filter));
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        partyProductService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
