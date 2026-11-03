package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.product.ProductContract;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyStatisticsDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;

import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.07.2023
 */
@Repository
public interface ProductContractRepositoryCustom {
    ResultList<ProductContract> getResultList(BaseFilter filter);

    List<ProductContract> lookUp(BaseFilter filter);

    StockProductQtyStatisticsDTO getQtyStatistics(StockFilter filter);

    Page<StockProductQtyDetailListDTO> getQtyDetails(StockFilter filter);
}
