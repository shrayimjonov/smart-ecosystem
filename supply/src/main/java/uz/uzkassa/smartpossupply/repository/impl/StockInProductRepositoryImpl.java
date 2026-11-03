package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.dto.product.ProductCapacityDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.repository.StockInProductRepositoryCustom;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.List;

public class StockInProductRepositoryImpl extends BaseRepositoryImpl implements StockInProductRepositoryCustom {

    @Override
    public ProductCapacityDTO getProduct(Long branchId, Long warehouseId, String barcode) {
        StringBuilder sql = new StringBuilder()
            .append("SELECT p.id, p.name, p.capacity ")
            .append("FROM product p ")
            .append("WHERE p.deleted = false ")
            .append("  AND p.status = :status ")
            .append("  AND p.branch_id = :branchId ")
            .append("  AND p.barcode = :barcode ")
            .append("  AND EXISTS ( ")
            .append("      SELECT 1 FROM stock_product sp ")
            .append("      JOIN stock s ON s.id = sp.stock_id ")
            .append("      WHERE sp.product_id = p.id ")
            .append("        AND sp.deleted = false ")
            .append("        AND s.deleted = false ")
            .append("        AND s.transfer_type = :transferType ")
            .append("        AND s.company_id = :companyId ")
            .append("  ) ")
            .append("  AND EXISTS ( ")
            .append("      SELECT 1 FROM warehouse_product wp ")
            .append("      WHERE wp.product_id = p.id ")
            .append("        AND wp.warehouse_id = :warehouseId ")
            .append("        AND wp.deleted = false ")
            .append("  )");

        Query query = entityManager.createNativeQuery(sql.toString(), "ProductCapacityDTOMapper")
            .setParameter("status", CommonStatus.ACTIVE.getCode())
            .setParameter("companyId", SecurityUtils.getCompanyId())
            .setParameter("branchId", branchId)
            .setParameter("warehouseId", warehouseId)
            .setParameter("barcode", barcode)
            .setParameter("transferType", TransferType.IN.name());
        query.setMaxResults(1);

        List<ProductCapacityDTO> result = query.getResultList();
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        return result.getFirst();
    }
}
