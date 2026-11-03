package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.productserial.ProductSerial;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyStatisticsDTO;
import uz.uzkassa.smartpossupply.filters.ProductSerialFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 11.05.2023
 */
@Repository
public interface ProductSerialRepositoryCustom {

    Page<ProductSerial> getResultList(ProductSerialFilter filter);

    StockProductQtyStatisticsDTO getQtyStatistics(StockFilter filter);

    Page<StockProductQtyDetailListDTO> getQtyDetails(StockFilter filter);
}
