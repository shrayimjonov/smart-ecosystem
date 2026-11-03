package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import uz.uzkassa.horeca.dto.stocktransfer.StockTransferItemDetailDTO;
import uz.uzkassa.horeca.repository.StockTransferItemRepository;

import java.util.List;

@Service

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class StockTransferItemsService extends BaseService {

    StockTransferItemRepository stockTransferItemRepository;

    public List<StockTransferItemDetailDTO> getList(Long stockTransferId) {
        return stockTransferItemRepository.findAllByStockTransferId(stockTransferId)
                .stream().map(stockTransferItem -> {
                    StockTransferItemDetailDTO stockTransferItemDetailDTO = new StockTransferItemDetailDTO();
                    stockTransferItemDetailDTO.setId(stockTransferItem.getId());
                    stockTransferItemDetailDTO.setFromProduct(toProductLookUpDTO(stockTransferItem.getFromProduct()));
                    stockTransferItemDetailDTO.setToProduct(toProductLookUpDTO(stockTransferItem.getToProduct()));
                    if (stockTransferItem.getFromWarehouseId() != null) {
                        stockTransferItemDetailDTO.setFromWarehouse(stockTransferItem.getFromWarehouse().toCommonDTO());
                    }
                    if (stockTransferItem.getToWarehouseId() != null) {
                        stockTransferItemDetailDTO.setToWarehouse(stockTransferItem.getToWarehouse().toCommonDTO());
                    }
                    stockTransferItemDetailDTO.setToProduct(toProductLookUpDTO(stockTransferItem.getToProduct()));
                    if (stockTransferItem.getUnit() != null) {
                        stockTransferItemDetailDTO.setUnit(stockTransferItem.getUnit().toCommonDTO());
                    }
                    stockTransferItemDetailDTO.setQty(stockTransferItem.getQty());
                    return stockTransferItemDetailDTO;
                }).toList();
    }

}
