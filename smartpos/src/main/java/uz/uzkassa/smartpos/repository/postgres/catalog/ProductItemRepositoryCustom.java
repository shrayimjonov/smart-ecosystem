package uz.uzkassa.smartpos.repository.postgres.catalog;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductItem;
import uz.uzkassa.smartpos.dto.filter.ProductFilter;

@Repository
public interface ProductItemRepositoryCustom {
    Page<ProductItem> findAllByFilter(ProductFilter filter);
}
