package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.inventory.Product;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.ProductFilter;

@Repository
public interface ProductRepositoryCustom {

    ResultList<Product> getResultList(ProductFilter filter);
}
