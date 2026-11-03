package uz.uzkassa.smartpos.web.rest.fashion;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.product.ProductItemBranchStockDTO;
import uz.uzkassa.smartpos.dto.product.ProductItemInfoDTO;
import uz.uzkassa.smartpos.dto.product.ProductItemSearchDTO;
import uz.uzkassa.smartpos.service.ProductItemService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fashion/cashbox")
@RequiredArgsConstructor
public class FashionCashboxResource {
    private final ProductItemService productItemService;

    @GetMapping("/product-search/{barcode}")
    public ProductItemSearchDTO productSearch(@PathVariable(name = "barcode") final String barcode, @RequestParam(value = "branchId", required = false) final Long branchId) {
        return productItemService.productSearch(barcode, branchId);
    }

    @GetMapping("/product-lookup/{barcode}")
    public List<ProductItemSearchDTO> productLookup(@PathVariable(name = "barcode") final String barcode, @RequestParam(value = "branchId", required = false) final Long branchId) {
        return productItemService.productLookup(barcode, branchId);
    }

    @GetMapping("/product-info/{productId}")
    public ProductItemInfoDTO productInfo(@PathVariable(name = "productId") final Long productId, @RequestParam(value = "branchId", required = false) final Long branchId) {
        return productItemService.productInfo(productId, branchId);
    }

    @GetMapping("/product-item-branch-stock/{productItemId}")
    public List<ProductItemBranchStockDTO> productItemBranchStock(@PathVariable(name = "productItemId") final Long productItemId) {
        return productItemService.productItemBranchStock(productItemId);
    }
}
