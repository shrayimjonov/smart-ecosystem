package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.stock.Stock;
import uz.uzkassa.cts.domain.stock.StockTransfer;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.stockin.StockInDTO;
import uz.uzkassa.cts.dto.stockin.StockInDetailDTO;
import uz.uzkassa.cts.dto.stockin.StockInItemLookUpDTO;
import uz.uzkassa.cts.dto.stockin.StockInListDTO;
import uz.uzkassa.cts.enums.StockStatus;
import uz.uzkassa.cts.filter.ProductFilter;
import uz.uzkassa.cts.filter.StockFilter;

import java.util.List;

public interface StockInService extends BaseService<Stock, Long> {

    Long create(StockInDTO stockInDTO);

    void create(StockTransfer stockTransfer);

    Long update(Long id, StockInDTO stockInDTO);

    StockInDetailDTO get(Long id);

    Page<StockInListDTO> getList(StockFilter filter);

    List<CommonDTO> lookUp(StockFilter filter);

    void updateStatus(Long id, StockStatus status);

    List<StockInItemLookUpDTO> getItemsLookUp(ProductFilter filter);
}
