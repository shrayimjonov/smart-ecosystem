package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.stock.Stock;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.stockreturn.StockReturnDTO;
import uz.uzkassa.cts.dto.stockreturn.StockReturnDetailDTO;
import uz.uzkassa.cts.dto.stockreturn.StockReturnListDTO;
import uz.uzkassa.cts.enums.StockStatus;
import uz.uzkassa.cts.filter.StockFilter;

import java.util.List;

public interface StockReturnService extends BaseService<Stock, Long> {

    Long create(StockReturnDTO stockReturnDTO);

    Long update(Long id, StockReturnDTO stockReturnDTO);

    StockReturnDetailDTO get(Long id);

    Page<StockReturnListDTO> getList(StockFilter filter);

    List<CommonDTO> lookUp(StockFilter filter);

    void updateStatus(Long id, StockStatus status);

    List<CommonDTO> getItemsLookUp(StockFilter filter);
}
