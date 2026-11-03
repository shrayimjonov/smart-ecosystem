package uz.uzkassa.smartpossupply.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.discount.DiscountCalculationDTO;
import uz.uzkassa.smartpossupply.dto.discount.DiscountDetailByProductResponseDTO;
import uz.uzkassa.smartpossupply.dto.discount.DiscountDetailDTO;
import uz.uzkassa.smartpossupply.dto.discount.DiscountListByProductRequestDTO;
import uz.uzkassa.smartpossupply.dto.order.DiscountResultDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.DiscountRangeType;
import uz.uzkassa.smartpossupply.enums.DiscountType;
import uz.uzkassa.smartpossupply.enums.DiscountValueType;
import uz.uzkassa.smartpossupply.service.DiscountService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 18.12.2024
 */
@RestController
@RequestMapping(ApiConstants.mobileDiscountRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileDiscountResource {

    DiscountService discountService;

    @Operation(operationId = "getDiscountsByProducts")
    @GetMapping("/products")
    public ResponseEntity<List<DiscountDetailByProductResponseDTO>> getByProducts(@RequestBody DiscountListByProductRequestDTO requestDTO) {
        return ResponseEntity.ok(discountService.getByProducts(requestDTO));
    }

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

    @Operation(summary = ApiConstants.Statuses, operationId = "getDiscountRangeTypes")
    @GetMapping("/range-types")
    public ResponseEntity<EnumSet<DiscountRangeType>> getRangeTypes() {
        return ResponseEntity.ok(EnumSet.allOf(DiscountRangeType.class));
    }

    @Operation(summary = ApiConstants.Statuses, operationId = "getDiscountValueTypes")
    @GetMapping("/value-types")
    public ResponseEntity<EnumSet<DiscountValueType>> getValueTypes() {
        return ResponseEntity.ok(EnumSet.allOf(DiscountValueType.class));
    }

    @PostMapping("/calculate")
    public ResponseEntity<Map<Long, List<DiscountResultDTO>>> calculate(@RequestBody List<DiscountCalculationDTO> calculations) {
        return ResponseEntity.ok(discountService.calculate(calculations));
    }

    @GetMapping(ApiConstants.offline)
    public ResponseEntity<List<DiscountDetailDTO>> getForOffline() {
        if (CollectionUtils.isEmpty(SecurityUtils.getCurrentBranchIds())) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok(discountService.getForOffline(SecurityUtils.getCurrentBranchIds()));
    }
}
