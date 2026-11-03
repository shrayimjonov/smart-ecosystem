package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.dto.product.ProductCapacityDTO;

@Repository
public interface StockInProductRepositoryCustom {

    ProductCapacityDTO getProduct(Long branchId, Long warehouseId, String barcode);

}
