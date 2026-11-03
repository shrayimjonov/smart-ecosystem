package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.product.ProductContractWarehouse;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;

import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.12.2023
 */
@Repository
public interface ProductContractWarehouseRepositoryCustom {

    ResultList<ProductContractWarehouse> getResultList(BalanceReportFilter filter);

    BigDecimal getReportTotalQty(BalanceReportFilter filter);

}
