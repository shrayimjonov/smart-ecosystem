package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.inventory.ProductVariation;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.ProductVariationFilter;

@Repository
public interface ProductVariationRepositoryCustom {

    ResultList<ProductVariation> getResultList(ProductVariationFilter filter);
}
