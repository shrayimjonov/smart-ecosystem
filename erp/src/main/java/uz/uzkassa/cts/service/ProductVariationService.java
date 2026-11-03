package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.inventory.ProductVariation;
import uz.uzkassa.cts.dto.productvariation.ProductVariationDTO;
import uz.uzkassa.cts.dto.productvariation.admin.ProductVariationListByProductAdminDTO;

public interface ProductVariationService extends BaseService<ProductVariation, Long> {

    void create(ProductVariationDTO productVariationDTO);

    ProductVariationListByProductAdminDTO getListByProductId(Long productId);
}
