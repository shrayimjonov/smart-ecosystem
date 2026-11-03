package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.stock.Stock;
import uz.uzkassa.cts.domain.stock.StockTransfer;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.stockout.StockOutDTO;
import uz.uzkassa.cts.dto.stockout.StockOutDetailDTO;
import uz.uzkassa.cts.dto.stockout.StockOutListDTO;
import uz.uzkassa.cts.enums.StockStatus;
import uz.uzkassa.cts.filter.StockFilter;

import java.util.List;

public interface StockOutService extends BaseService<Stock, Long> {

    Long create(StockOutDTO stockOutDTO);

    void create(StockTransfer stockTransfer);

    Long update(Long id, StockOutDTO stockOutDTO);

    StockOutDetailDTO get(Long id);

    Page<StockOutListDTO> getList(StockFilter filter);

    List<CommonDTO> lookUp(StockFilter filter);

    void updateStatus(Long id, StockStatus status);

    List<CommonDTO> getItemsLookUp(StockFilter filter);
}
