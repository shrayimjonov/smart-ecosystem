package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.Product;
import uz.uzkassa.silen.dto.ProductSelectCountItem;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.ProductFilter;

import java.util.List;

public interface ProductRepositoryCustom {
    Page<Product> findAllByFilter(ProductFilter filter);

    List<SelectItem> getItems(ProductFilter filter);

    List<SelectItem> getChildren(String productId, String searchKey);

    List<ProductSelectCountItem> getCounts(ProductFilter filter);

    Page<SelectItem> selectCatalogCodeItems(ProductFilter filter);

    List<SelectItem> selectCatalogCodeItemsList(ProductFilter filter);
}
