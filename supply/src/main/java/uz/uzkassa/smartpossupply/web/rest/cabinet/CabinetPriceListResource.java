package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.pricelist.PriceListDTO;
import uz.uzkassa.smartpossupply.dto.pricelist.PriceListDetailDTO;
import uz.uzkassa.smartpossupply.dto.pricelist.PriceListListDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.filters.PriceListFilter;
import uz.uzkassa.smartpossupply.service.PriceListService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.02.2023 13:50
 */
@RestController
@RequestMapping(ApiConstants.cabinetPriceListRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetPriceListResource {

    PriceListService priceListService;

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody PriceListDTO priceListDTO) {
        return ResponseEntity.ok(priceListService.create(priceListDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @Valid @RequestBody PriceListDTO priceListDTO) {
        return ResponseEntity.ok(priceListService.update(id, priceListDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<PriceListDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(priceListService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<PriceListListDTO>> getList(@ParameterObject PriceListFilter filter) {
        return ResponseEntity.ok(priceListService.getList(filter));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        priceListService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<CommonStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(CommonStatus.class));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") String status) {
        priceListService.updateStatus(id, CommonStatus.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject PriceListFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(priceListService.lookUp(filter));
    }
}
