package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.inventory.Product;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.product.ProductDTO;
import uz.uzkassa.cts.dto.product.admin.ProductDetailAdminDTO;
import uz.uzkassa.cts.dto.product.admin.ProductListAdminDTO;
import uz.uzkassa.cts.filter.ProductFilter;

import java.util.List;

public interface ProductService extends BaseService<Product, Long> {

    Long create(ProductDTO productDTO);

    Long update(Long id, ProductDTO productDTO);

    ProductDetailAdminDTO getForAdmin(Long id);

    Page<ProductListAdminDTO> getListForAdmin(ProductFilter filter);

    List<CommonDTO> lookUp(ProductFilter filter);
}
