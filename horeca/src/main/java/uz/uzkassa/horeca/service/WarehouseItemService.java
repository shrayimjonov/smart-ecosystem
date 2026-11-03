package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.horeca.domain.order.Order;
import uz.uzkassa.horeca.domain.stock.Stock;
import uz.uzkassa.horeca.domain.stockadjustment.StockAdjustment;
import uz.uzkassa.horeca.domain.stockadjustment.StockAdjustmentItem;
import uz.uzkassa.horeca.domain.warehouse.WarehouseItem;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.stock.StockBalanceReportDTO;
import uz.uzkassa.horeca.dto.warehouse.WarehouseCheckDTO;
import uz.uzkassa.horeca.dto.warehouse.WarehouseCheckResponseDTO;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.repository.TechCardRepository;
import uz.uzkassa.horeca.repository.WarehouseItemRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class WarehouseItemService extends BaseService {

    WarehouseItemRepository warehouseItemRepository;
    TechCardRepository techCardRepository;

    public Page<StockBalanceReportDTO> getReport(BaseFilter filter) {
        ResultList<WarehouseItem> resultList = warehouseItemRepository.getReport(filter);
        List<StockBalanceReportDTO> result = resultList.getList().stream().map(warehouseProduct -> {
            StockBalanceReportDTO stockBalanceReportDTO = new StockBalanceReportDTO();
            stockBalanceReportDTO.setId(warehouseProduct.getId());
            stockBalanceReportDTO.setProduct(warehouseProduct.getProduct().toCommonDTO());
            stockBalanceReportDTO.setQty(warehouseProduct.getQty());
            stockBalanceReportDTO.setWarehouse(warehouseProduct.getWarehouse().toCommonDTO());
            return stockBalanceReportDTO;
        }).collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public BigDecimal getReportTotalQty(BaseFilter filter) {
        return warehouseItemRepository.getReportTotalQty(filter);
    }

    @Transactional
    public void out(Order order) {
        order.getItems().forEach(orderItem -> techCardRepository.findFirstByProductIdAndDeletedIsFalse(orderItem.getMenuItem().getProductId()).map(techCard -> {
            techCard.getItems().forEach(techCardItem -> {
                WarehouseItem warehouseItem = warehouseItemRepository.findByWarehouseIdAndProductIdAndUnitId(1L/*techCardItem.getIngredient().getWarehouseId()*/, techCardItem.getIngredientId(), orderItem.getMultiUnit().getTechCardId() != null ? orderItem.getMultiUnit().getTechCard().getUnitId() : orderItem.getMultiUnit().getMenuItem().getProduct().getUnitId()).orElse(null);
                if (warehouseItem != null) {
                    warehouseItem.subtractQty(orderItem.getQty());
                    if (warehouseItem.getQty().compareTo(BigDecimal.ZERO) < 0) {
                        throw badRequestExceptionThrow("Количество на складе недостаточно").get();
                    }
                    warehouseItemRepository.save(warehouseItem);
                }
            });
            return orderItem;
        }).orElseThrow(notFoundExceptionThrow(Order.class.getSimpleName(), "productId", orderItem.getMenuItem().getProductId())));
    }

    @Transactional
    public void out(Stock stock) {
        stock.getItems().forEach(stockItem -> {
            WarehouseItem warehouseItem = warehouseItemRepository.findByWarehouseIdAndProductIdAndUnitId(Optional.ofNullable(stockItem.getWarehouseId()).orElseGet(stock::getWarehouseId), stockItem.getProductId(), stockItem.getUnitId()).orElse(null);
            if (warehouseItem != null) {
                warehouseItem.subtractQty(stockItem.getQty());
                if (warehouseItem.getQty().compareTo(BigDecimal.ZERO) < 0) {
                    throw badRequestExceptionThrow("Количество на складе недостаточно").get();
                }
                warehouseItemRepository.save(warehouseItem);
            }
        });
    }

    @Transactional
    public void in(Stock stock) {
        stock.getItems().forEach(stockItem -> {
            Long warehouseId = Optional.ofNullable(stockItem.getWarehouseId())
                    .orElseGet(stock::getWarehouseId);
            WarehouseItem warehouseItem = warehouseItemRepository.findByWarehouseIdAndProductIdAndUnitIdAndCreatedDateAndPrice(warehouseId, stockItem.getProductId(), stockItem.getUnitId(), stock.getCreatedDate().toLocalDate(), stockItem.getPrice())
                    .orElseGet(() -> {
                        WarehouseItem item = new WarehouseItem();
                        item.setWarehouseId(warehouseId);
                        item.setProductId(stockItem.getProductId());
                        item.setUnitId(stockItem.getUnitId());
                        item.setQty(stockItem.getQty());
                        item.setInKg(stockItem.getInKg());
                        item.setPrice(stockItem.getPrice());
                        item.setCreatedDate(stock.getCreatedDate().toLocalDate());
                        return item;
                    });
            if (warehouseItem.getId() != null) {
                warehouseItem.addQty(stockItem.getQty());
                warehouseItem.addInKg(stockItem.getInKg());
            }
            warehouseItemRepository.save(warehouseItem);
        });
    }

    public void out(StockAdjustment stockAdjustment) {
        for (StockAdjustmentItem stockAdjustmentItem : stockAdjustment.getItems()) {
            Long warehouseId = Optional.ofNullable(stockAdjustment.getWarehouseId()).orElseGet(stockAdjustmentItem::getWarehouseId);
            WarehouseItem warehouseItem = warehouseItemRepository.findByWarehouseIdAndProductIdAndUnitId(warehouseId, stockAdjustmentItem.getProductId(), stockAdjustmentItem.getUnitId()).orElseThrow(notFoundExceptionThrow(WarehouseItem.class.getSimpleName(), "Warehouse product", warehouseId));

            warehouseItem.subtractQty(stockAdjustmentItem.getQty());
            if (warehouseItem.getQty().compareTo(BigDecimal.ZERO) < 0) {
                throw badRequestExceptionThrow(localizationService.localize("validate.stock.adjustment", new Object[]{stockAdjustmentItem.getProduct().getName()})).get();
            }
            warehouseItemRepository.save(warehouseItem);
        }
    }

    public void in(StockAdjustment stockAdjustment) {
        for (StockAdjustmentItem stockAdjustmentItem : stockAdjustment.getItems()) {
            Long warehouseId = Optional.ofNullable(stockAdjustment.getWarehouseId()).orElseGet(stockAdjustmentItem::getWarehouseId);
            WarehouseItem warehouseItem = warehouseItemRepository.findByWarehouseIdAndProductIdAndUnitId(warehouseId, stockAdjustmentItem.getProductId(), stockAdjustmentItem.getUnitId()).orElseThrow(notFoundExceptionThrow(WarehouseItem.class.getSimpleName(), "Warehouse product", warehouseId));

            warehouseItem.addQty(stockAdjustmentItem.getQty());
            warehouseItemRepository.save(warehouseItem);
        }
    }

    public List<WarehouseCheckResponseDTO> checkExistingCount(WarehouseCheckDTO warehouseCheckDTO) {
        return warehouseItemRepository.findAllByWarehouseIdAndProductIdAndUnitIdAndQtyGreaterThan(
                        warehouseCheckDTO.getWarehouseId(), warehouseCheckDTO.getProductId(),
                        warehouseCheckDTO.getUnitId(), BigDecimal.ZERO)
                .stream().map(warehouseItem -> {
                    WarehouseCheckResponseDTO warehouseCheckResponseDTO = new WarehouseCheckResponseDTO();
                    warehouseCheckResponseDTO.setQty(warehouseItem.getQty());
                    warehouseCheckResponseDTO.setInKg(warehouseItem.getInKg());
                    warehouseCheckResponseDTO.setPrice(warehouseItem.getPrice());
                    warehouseCheckResponseDTO.setCreatedDate(warehouseItem.getCreatedDate());
                    return warehouseCheckResponseDTO;

                }).toList();
    }
}
