package uz.uzkassa.smartpossupply.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.product.ProductDetailMobileDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductListForOfflineDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductListMobileDTO;
import uz.uzkassa.smartpossupply.filters.ProductFilter;
import uz.uzkassa.smartpossupply.service.ProductService;
import uz.uzkassa.smartpossupply.service.WarehouseProductService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.math.BigDecimal;
import java.util.ArrayList;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.mobileProductRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileProductResource {

    ProductService productService;
    WarehouseProductService warehouseProductService;

    @Operation(summary = ApiConstants.List, operationId = "getProductList")
    @GetMapping
    public ResponseEntity<Page<ProductListMobileDTO>> getList(@ParameterObject ProductFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(productService.getListForMobile(filter));
    }

    @GetMapping("/offline")
    public ResponseEntity<Page<ProductListForOfflineDTO>> getListForOffline(@ParameterObject ProductFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(productService.getListForMobileOffline(filter));
    }

    @Operation(summary = ApiConstants.Get, operationId = "getProduct")
    @GetMapping(ApiConstants.id)
    public ResponseEntity<ProductDetailMobileDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getForMobile(id));
    }

    @Operation(summary = "Warehouse Qty", operationId = "getProductQtyInWarehouse")
    @GetMapping("/warehouse-qty/{productId}")
    public ResponseEntity<BigDecimal> getWarehouseQty(@PathVariable Long productId) {
        return ResponseEntity.ok(warehouseProductService.getWarehouseQty(productId));
    }

    @Operation(summary = "Favorite product", operationId = "favoriteProduct")
    @PatchMapping("/favorite" + ApiConstants.id)
    public ResponseEntity<Void> favorite(@PathVariable Long id) {
        productService.favorite(id);
        return ResponseEntity.ok().build();
    }
}
