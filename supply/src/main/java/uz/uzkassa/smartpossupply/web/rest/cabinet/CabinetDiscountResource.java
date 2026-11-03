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
import uz.uzkassa.smartpossupply.dto.discount.*;
import uz.uzkassa.smartpossupply.dto.order.DiscountResultDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.DiscountRangeType;
import uz.uzkassa.smartpossupply.enums.DiscountType;
import uz.uzkassa.smartpossupply.enums.DiscountValueType;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.DiscountService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 18.12.2024
 */
@RestController
@RequestMapping(ApiConstants.cabinetDiscountRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetDiscountResource {

    DiscountService discountService;

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody DiscountDTO discountDTO) {
        return ResponseEntity.ok(discountService.create(discountDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @Valid @RequestBody DiscountDTO discountDTO) {
        return ResponseEntity.ok(discountService.update(id, discountDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<DiscountDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(discountService.get(id));
    }

    @GetMapping("/products")
    public ResponseEntity<List<DiscountDetailByProductResponseDTO>> getByProducts(@RequestBody DiscountListByProductRequestDTO requestDTO) {
        return ResponseEntity.ok(discountService.getByProducts(requestDTO));
    }

    @GetMapping
    public ResponseEntity<Page<DiscountListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(discountService.getList(filter));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        discountService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<CommonStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(CommonStatus.class));
    }

    @GetMapping("/discount-types")
    public ResponseEntity<EnumSet<DiscountType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(DiscountType.class));
    }

    @GetMapping("/range-types")
    public ResponseEntity<EnumSet<DiscountRangeType>> getRangeTypes() {
        return ResponseEntity.ok(EnumSet.allOf(DiscountRangeType.class));
    }

    @GetMapping("/value-types")
    public ResponseEntity<EnumSet<DiscountValueType>> getValueTypes() {
        return ResponseEntity.ok(EnumSet.allOf(DiscountValueType.class));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") String status) {
        discountService.updateStatus(id, CommonStatus.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject BaseFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(discountService.lookUp(filter));
    }

    @PostMapping("/calculate")
    public ResponseEntity<List<DiscountResultDTO>> calculate(@RequestBody DiscountCalculationDTO calculationDTO) {
        return ResponseEntity.ok(discountService.calculate(calculationDTO));
    }
}
