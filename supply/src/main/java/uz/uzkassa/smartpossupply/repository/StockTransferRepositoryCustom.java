package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.stocktransfer.StockTransfer;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.StockTransferFilter;

import java.util.List;

@Repository
public interface StockTransferRepositoryCustom {
    ResultList<StockTransfer> getResultList(StockTransferFilter filter);

    List<StockTransfer> lookUp(StockTransferFilter filter);
}
