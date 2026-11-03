package uz.uzkassa.smartpossupply.web.rest.mobile;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.product.ProductSortOfflineDTO;
import uz.uzkassa.smartpossupply.enums.SortOrderType;
import uz.uzkassa.smartpossupply.service.ProductSortService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.mobileProductSortRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileProductSortResource {

    ProductSortService productSortService;

    @GetMapping("/sort-order-types")
    public ResponseEntity<EnumSet<SortOrderType>> getSortOrderTypes() {
        return ResponseEntity.ok(EnumSet.allOf(SortOrderType.class));
    }

    @GetMapping(ApiConstants.offline)
    public ResponseEntity<List<ProductSortOfflineDTO>> getListForOffline(@RequestParam("sortOrderType") String sortOrderType) {
        if (SecurityUtils.getCurrentCompanyId() == null && SecurityUtils.getCurrentBranchId() == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(productSortService.getListForOffline(SecurityUtils.getCurrentCompanyId(), SecurityUtils.getCurrentBranchId(), sortOrderType));
    }
}