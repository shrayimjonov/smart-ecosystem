package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.stock.StockTransfer;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.stocktransfer.StockTransferDTO;
import uz.uzkassa.cts.dto.stocktransfer.StockTransferDetailDTO;
import uz.uzkassa.cts.dto.stocktransfer.StockTransferListDTO;
import uz.uzkassa.cts.enums.StockTransferStatus;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.filter.StockTransferFilter;

import java.util.List;

public interface StockTransferService extends BaseService<StockTransfer, Long> {

    Long create(StockTransferDTO stockTransferDTO);

    Long update(Long id, StockTransferDTO stockTransferDTO);

    StockTransferDetailDTO get(Long id);

    Page<StockTransferListDTO> getList(StockTransferFilter filter);

    List<CommonDTO> lookUp(StockTransferFilter filter);

    void updateStatus(Long id, StockTransferStatus status);

    List<CommonDTO> getItemsLookUp(StockFilter filter);
}
