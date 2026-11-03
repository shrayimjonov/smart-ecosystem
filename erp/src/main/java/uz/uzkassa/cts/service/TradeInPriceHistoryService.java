package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.TradeInPriceHistory;
import uz.uzkassa.cts.dto.tradeinpricehistory.request.TradeInPriceHistoryRequestDTO;
import uz.uzkassa.cts.dto.tradeinpricehistory.response.TradeInPriceHistoryResponseDTO;
import uz.uzkassa.cts.filter.TradeInPriceHistoryListFilter;

import java.util.List;

public interface TradeInPriceHistoryService extends BaseService<TradeInPriceHistory, Long> {

    TradeInPriceHistoryResponseDTO create(TradeInPriceHistoryRequestDTO tradeInPriceHistoryDTO);

    TradeInPriceHistoryResponseDTO paymentCreate(TradeInPriceHistoryRequestDTO tradeInPriceHistoryDTO);

    TradeInPriceHistoryResponseDTO update(TradeInPriceHistoryRequestDTO tradeInPriceHistoryDTO);

    TradeInPriceHistoryResponseDTO getById(Long id);

    TradeInPriceHistoryResponseDTO getActiveByDeviceModelId(Long id);

    Page<TradeInPriceHistoryResponseDTO> list(TradeInPriceHistoryListFilter filter);

    List<TradeInPriceHistoryResponseDTO> lookUp(TradeInPriceHistoryListFilter filter);
}
