package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.stock.StockAdjustment;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.stockadjustment.StockAdjustmentDTO;
import uz.uzkassa.cts.dto.stockadjustment.StockAdjustmentDetailDTO;
import uz.uzkassa.cts.dto.stockadjustment.StockAdjustmentListDTO;
import uz.uzkassa.cts.enums.StockStatus;
import uz.uzkassa.cts.filter.StockFilter;

import java.util.List;

public interface StockAdjustmentService extends BaseService<StockAdjustment, Long> {

    Long create(StockAdjustmentDTO stockAdjustmentDTO);

    Long update(Long id, StockAdjustmentDTO stockAdjustmentDTO);

    StockAdjustmentDetailDTO get(Long id);

    Page<StockAdjustmentListDTO> getList(StockFilter filter);

    void updateStatus(Long id, StockStatus status);

    List<CommonDTO> getItemsLookUp(StockFilter filter);
}
