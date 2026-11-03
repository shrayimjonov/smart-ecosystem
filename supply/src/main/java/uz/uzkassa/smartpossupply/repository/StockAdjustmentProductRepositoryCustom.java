package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.filters.StockFilter;

@Repository
public interface StockAdjustmentProductRepositoryCustom {

    Page<StockProductQtyDetailListDTO> getQtyDetails(StockFilter filter);
}
