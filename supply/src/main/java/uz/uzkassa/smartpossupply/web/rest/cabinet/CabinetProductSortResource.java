package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.product.ProductSortDTO;
import uz.uzkassa.smartpossupply.enums.SortOrderType;
import uz.uzkassa.smartpossupply.service.ProductSortService;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetProductSortRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetProductSortResource {

    ProductSortService productSortService;

    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody ProductSortDTO productSortDTO) {
        productSortService.create(productSortDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sort-order-types")
    public ResponseEntity<EnumSet<SortOrderType>> getSortOrderTypes() {
        return ResponseEntity.ok(EnumSet.allOf(SortOrderType.class));
    }
}
