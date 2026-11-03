package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.productvariation.ProductVariationDTO;
import uz.uzkassa.cts.dto.productvariation.admin.ProductVariationListByProductAdminDTO;
import uz.uzkassa.cts.service.ProductVariationService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminProductVariationRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminProductVariationResource {

    ProductVariationService productVariationService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody ProductVariationDTO productVariationDTO) {
        productVariationService.create(productVariationDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductVariationListByProductAdminDTO> getListByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(productVariationService.getListByProductId(productId));
    }
}
