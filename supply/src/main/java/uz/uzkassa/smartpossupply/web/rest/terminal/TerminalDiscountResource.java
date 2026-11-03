package uz.uzkassa.smartpossupply.web.rest.terminal;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.discount.DiscountCalculationDTO;
import uz.uzkassa.smartpossupply.dto.order.DiscountResultDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.DiscountRangeType;
import uz.uzkassa.smartpossupply.enums.DiscountType;
import uz.uzkassa.smartpossupply.enums.DiscountValueType;
import uz.uzkassa.smartpossupply.service.DiscountService;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 18.12.2024
 */
@RestController
@RequestMapping(ApiConstants.terminalDiscountRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TerminalDiscountResource {

    DiscountService discountService;

    @Operation(summary = ApiConstants.Statuses, operationId = "getDiscountStatuses")
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<CommonStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(CommonStatus.class));
    }

    @Operation(summary = "Discount types", operationId = "getDiscountTypes")
    @GetMapping("/discount-types")
    public ResponseEntity<EnumSet<DiscountType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(DiscountType.class));
    }

    @Operation(summary = "Discount range types", operationId = "getDiscountRangeTypes")
    @GetMapping("/range-types")
    public ResponseEntity<EnumSet<DiscountRangeType>> getRangeTypes() {
        return ResponseEntity.ok(EnumSet.allOf(DiscountRangeType.class));
    }

    @Operation(summary = "Discount value types", operationId = "getDiscountValueTypes")
    @GetMapping("/value-types")
    public ResponseEntity<EnumSet<DiscountValueType>> getValueTypes() {
        return ResponseEntity.ok(EnumSet.allOf(DiscountValueType.class));
    }

    @PostMapping("/calculate")
    public ResponseEntity<List<DiscountResultDTO>> calculate(@RequestBody DiscountCalculationDTO calculations) {
        return ResponseEntity.ok(discountService.calculate(calculations));
    }

    @PostMapping("/calculate-multiple")
    public ResponseEntity<Map<Long, List<DiscountResultDTO>>> calculate(@RequestBody List<DiscountCalculationDTO> calculations) {
        return ResponseEntity.ok(discountService.calculate(calculations));
    }
}
