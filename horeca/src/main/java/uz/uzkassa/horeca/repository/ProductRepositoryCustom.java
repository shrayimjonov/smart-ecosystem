package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.product.Product;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.ProductFilter;

public interface ProductRepositoryCustom {
    ResultList<Product> getResultList(ProductFilter filter);
}
