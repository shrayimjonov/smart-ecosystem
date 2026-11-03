package uz.uzkassa.smartpos.repository.postgres.catalog;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.dto.product.ProductColorGroupLookupDTO;

import java.util.List;

@Repository
public interface ProductColorGroupRepositoryCustom {
    List<ProductColorGroupLookupDTO> lookup(Long productId, Long branchId);
}
