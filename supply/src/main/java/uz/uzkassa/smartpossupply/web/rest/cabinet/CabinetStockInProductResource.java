package uz.uzkassa.smartpossupply.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.product.ProductCapacityDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductLookUpDTO;
import uz.uzkassa.smartpossupply.service.StockInProductService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetStockInProductRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetStockInProductResource {

    StockInProductService stockInProductService;

    @GetMapping
    public ResponseEntity<ProductCapacityDTO> getProduct(@RequestParam("branchId") Long branchId,
                                                         @RequestParam("warehouseId") Long warehouseId,
                                                         @RequestParam(value = "barcode", required = false) String barcode,
                                                         @RequestParam(value = "markCode", required = false) String markCode,
                                                         @RequestParam(value = "aggregationCode", required = false) String aggregationCode,
                                                         @RequestParam(value = "aggregationBlockCode", required = false) String aggregationBlockCode) {
        return ResponseEntity.ok(stockInProductService.getProduct(branchId, warehouseId, barcode, markCode, aggregationCode, aggregationBlockCode));
    }

    @GetMapping("/details")
    public ResponseEntity<StockProductLookUpDTO> getProductDetails(@RequestParam("branchId") Long branchId,
                                                                   @RequestParam("warehouseId") Long warehouseId,
                                                                   @RequestParam("productId") Long productId) {
        return ResponseEntity.ok(stockInProductService.getProductDetails(branchId, warehouseId, productId));
    }
}
