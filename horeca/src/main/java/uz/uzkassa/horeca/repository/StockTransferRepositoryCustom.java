package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.stocktransfer.StockTransfer;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.StockTransferFilter;

public interface StockTransferRepositoryCustom {
    ResultList<StockTransfer> getResultList(StockTransferFilter filter);
}
