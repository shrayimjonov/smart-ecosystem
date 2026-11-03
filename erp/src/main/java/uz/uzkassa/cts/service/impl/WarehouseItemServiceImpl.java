package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.inventory.WarehouseItem;
import uz.uzkassa.cts.domain.inventory.WarehouseItemVariationOption;
import uz.uzkassa.cts.domain.stock.Stock;
import uz.uzkassa.cts.domain.stock.StockAdjustment;
import uz.uzkassa.cts.domain.stock.StockItemVariationOption;
import uz.uzkassa.cts.domain.stock.StockTransfer;
import uz.uzkassa.cts.dto.stock.balancereport.StockBalanceReportDetailListDTO;
import uz.uzkassa.cts.dto.stock.balancereport.StockBalanceReportListDTO;
import uz.uzkassa.cts.dto.stock.balancereport.StockBalanceReportStatisticsDTO;
import uz.uzkassa.cts.enums.StockTransferType;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.WarehouseItemService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class WarehouseItemServiceImpl extends BaseServiceImpl<WarehouseItem, Long> implements WarehouseItemService {

    private final WarehouseItemRepository warehouseItemRepository;
    private final WarehouseItemVariationRepository warehouseItemVariationRepository;
    private final StockRepository stockRepository;
    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final StockTransferRepository stockTransferRepository;
    private final StockItemVariationOptionRepository stockItemVariationOptionRepository;

    public WarehouseItemServiceImpl(WarehouseItemRepository warehouseItemRepository, WarehouseItemVariationRepository warehouseItemVariationRepository, StockRepository stockRepository, StockAdjustmentRepository stockAdjustmentRepository, StockTransferRepository stockTransferRepository, StockItemVariationOptionRepository stockItemVariationOptionRepository) {
        super(warehouseItemRepository);
        this.warehouseItemRepository = warehouseItemRepository;
        this.warehouseItemVariationRepository = warehouseItemVariationRepository;
        this.stockRepository = stockRepository;
        this.stockAdjustmentRepository = stockAdjustmentRepository;
        this.stockTransferRepository = stockTransferRepository;
        this.stockItemVariationOptionRepository = stockItemVariationOptionRepository;
    }

    @Override
    public Page<StockBalanceReportListDTO> getBalanceReport(StockFilter filter) {
        return warehouseItemRepository.getReport(filter);
    }

    @Override
    public BigDecimal getBalanceReportTotalQty(StockFilter filter) {
        return warehouseItemRepository.getReportTotalQty(filter);
    }

    @Transactional
    @Override
    public void out(Stock stock) {
        stock.getItems().forEach(stockItem -> {
            List<StockItemVariationOption> stockItemVariationOptions = stockItemVariationOptionRepository.findAllByEntityIdAndEntityTypeAndDeletedIsFalse(stockItem.getId(), stock.getEntityType());

            if (!CollectionUtils.isEmpty(stockItemVariationOptions)) {
                long[] variationOptions = stockItemVariationOptions.stream().mapToLong(StockItemVariationOption::getVariationOptionId).toArray();

                WarehouseItem warehouseItem = warehouseItemRepository.findFirstByWarehouseIdAndItemIdAndUnitIdAndOptions(stock.getWarehouseId(), stockItem.getItemId(), stockItem.getUnitId(), variationOptions);
                if (warehouseItem != null) {
                    warehouseItem.setVariationOptions(variationOptions);
                    warehouseItem.subtractQty(stockItem.getQty());
                    if (warehouseItem.getQty().compareTo(BigDecimal.ZERO) < 0) {
                        throw validationService.badRequestExceptionThrow(WAREHOUSE_QUANTITY_SHORTAGE).get();
                    }
                    warehouseItemRepository.save(warehouseItem);

                    List<WarehouseItemVariationOption> warehouseItemVariationOptions = stockItemVariationOptions.stream().map(stockItemVariationOption -> {
                        WarehouseItemVariationOption warehouseItemVariationOption = new WarehouseItemVariationOption();
                        warehouseItemVariationOption.setWarehouseItemId(warehouseItem.getId());
                        warehouseItemVariationOption.setStockItemVariationOptionId(stockItemVariationOption.getId());
                        warehouseItemVariationOption.setTransferType(stock.getTransferType());
                        return warehouseItemVariationOption;
                    }).collect(Collectors.toList());
                    warehouseItemVariationRepository.saveAll(warehouseItemVariationOptions);
                }
            } else {
                WarehouseItem warehouseItem = warehouseItemRepository.findByWarehouseIdAndItemIdAndUnitId(stock.getWarehouseId(), stockItem.getItemId(), stockItem.getUnitId()).orElse(null);
                if (warehouseItem != null) {
                    warehouseItem.subtractQty(stockItem.getQty());
                    if (warehouseItem.getQty().compareTo(BigDecimal.ZERO) < 0) {
                        throw validationService.badRequestExceptionThrow(WAREHOUSE_QUANTITY_SHORTAGE).get();
                    }
                    warehouseItemRepository.save(warehouseItem);
                }
            }
        });
    }

    @Transactional
    @Override
    public void in(Stock stock) {
        stock.getItems().forEach(stockItem -> {
            List<StockItemVariationOption> stockItemVariationOptions = stockItemVariationOptionRepository.findAllByEntityIdAndEntityTypeAndDeletedIsFalse(stockItem.getId(), stock.getEntityType());
            if (!CollectionUtils.isEmpty(stockItemVariationOptions)) {
                long[] variationOptions = stockItemVariationOptions.stream().mapToLong(StockItemVariationOption::getVariationOptionId).toArray();

                WarehouseItem warehouseItem = warehouseItemRepository.findFirstByWarehouseIdAndItemIdAndUnitIdAndOptions(stock.getWarehouseId(), stockItem.getItemId(), stockItem.getUnitId(), variationOptions);
                if (warehouseItem == null) {
                    warehouseItem = new WarehouseItem();
                    warehouseItem.setWarehouseId(stock.getWarehouseId());
                    warehouseItem.setItemId(stockItem.getItemId());
                    warehouseItem.setUnitId(stockItem.getUnitId());
                    warehouseItem.setVariationOptions(variationOptions);
                }
                warehouseItem.addQty(stockItem.getQty());
                warehouseItemRepository.save(warehouseItem);

                WarehouseItem finalWarehouseItem = warehouseItem;
                List<WarehouseItemVariationOption> warehouseItemVariationOptions = stockItemVariationOptions.stream().map(stockItemVariationOption -> {
                    WarehouseItemVariationOption warehouseItemVariationOption = new WarehouseItemVariationOption();
                    warehouseItemVariationOption.setWarehouseItemId(finalWarehouseItem.getId());
                    warehouseItemVariationOption.setStockItemVariationOptionId(stockItemVariationOption.getId());
                    warehouseItemVariationOption.setTransferType(stock.getTransferType());
                    return warehouseItemVariationOption;
                }).collect(Collectors.toList());
                warehouseItemVariationRepository.saveAll(warehouseItemVariationOptions);
            } else {
                WarehouseItem warehouseItem = warehouseItemRepository.findByWarehouseIdAndItemIdAndUnitId(stock.getWarehouseId(), stockItem.getItemId(), stockItem.getUnitId()).orElse(new WarehouseItem());
                if (warehouseItem.getId() == null) {
                    warehouseItem.setWarehouseId(stock.getWarehouseId());
                    warehouseItem.setItemId(stockItem.getItemId());
                    warehouseItem.setUnitId(stockItem.getUnitId());
                }
                warehouseItem.addQty(stockItem.getQty());
                warehouseItemRepository.save(warehouseItem);
            }
        });
    }

    @Override
    @Transactional
    public void in(StockAdjustment stockAdjustment) {
        stockAdjustment.getItems().forEach(stockAdjustmentItem -> {
            List<StockItemVariationOption> stockItemVariationOptions = stockItemVariationOptionRepository.findAllByEntityIdAndEntityTypeAndDeletedIsFalse(stockAdjustmentItem.getId(), stockAdjustment.getEntityType());

            if (!CollectionUtils.isEmpty(stockItemVariationOptions)) {
                long[] variationOptions = stockItemVariationOptions.stream().mapToLong(StockItemVariationOption::getVariationOptionId).toArray();

                WarehouseItem warehouseItem = warehouseItemRepository.findFirstByWarehouseIdAndItemIdAndUnitIdAndOptions(stockAdjustment.getWarehouseId(), stockAdjustmentItem.getItemId(), stockAdjustmentItem.getUnitId(), variationOptions);
                if (warehouseItem == null) {
                    warehouseItem = new WarehouseItem();
                    warehouseItem.setWarehouseId(stockAdjustment.getWarehouseId());
                    warehouseItem.setItemId(stockAdjustmentItem.getItemId());
                    warehouseItem.setUnitId(stockAdjustmentItem.getUnitId());
                    warehouseItem.setVariationOptions(variationOptions);
                }
                warehouseItem.addQty(stockAdjustmentItem.getQty());
                warehouseItemRepository.save(warehouseItem);

                WarehouseItem finalWarehouseItem = warehouseItem;
                List<WarehouseItemVariationOption> warehouseItemVariationOptions = stockItemVariationOptions.stream().map(stockItemVariationOption -> {
                    WarehouseItemVariationOption warehouseItemVariationOption = new WarehouseItemVariationOption();
                    warehouseItemVariationOption.setWarehouseItemId(finalWarehouseItem.getId());
                    warehouseItemVariationOption.setStockItemVariationOptionId(stockItemVariationOption.getId());
                    warehouseItemVariationOption.setTransferType(StockTransferType.ADJUSTMENT);
                    return warehouseItemVariationOption;
                }).collect(Collectors.toList());
                warehouseItemVariationRepository.saveAll(warehouseItemVariationOptions);
            } else {
                WarehouseItem warehouseItem = warehouseItemRepository.findByWarehouseIdAndItemIdAndUnitId(stockAdjustment.getWarehouseId(), stockAdjustmentItem.getItemId(), stockAdjustmentItem.getUnitId()).orElse(new WarehouseItem());
                if (warehouseItem.getId() == null) {
                    warehouseItem.setWarehouseId(stockAdjustment.getWarehouseId());
                    warehouseItem.setItemId(stockAdjustmentItem.getItemId());
                    warehouseItem.setUnitId(stockAdjustmentItem.getUnitId());
                }
                warehouseItem.addQty(stockAdjustmentItem.getQty());
                warehouseItemRepository.save(warehouseItem);
            }
        });
    }

    @Override
    @Transactional
    public void out(StockAdjustment stockAdjustment) {
        stockAdjustment.getItems().forEach(stockAdjustmentItem -> {
            List<StockItemVariationOption> stockItemVariationOptions = stockItemVariationOptionRepository.findAllByEntityIdAndEntityTypeAndDeletedIsFalse(stockAdjustmentItem.getId(), stockAdjustment.getEntityType());
            if (!CollectionUtils.isEmpty(stockItemVariationOptions)) {
                long[] variationOptions = stockItemVariationOptions.stream().mapToLong(StockItemVariationOption::getVariationOptionId).toArray();

                WarehouseItem warehouseItem = warehouseItemRepository.findFirstByWarehouseIdAndItemIdAndUnitIdAndOptions(stockAdjustment.getWarehouseId(), stockAdjustmentItem.getItemId(), stockAdjustmentItem.getUnitId(), variationOptions);
                if (warehouseItem != null) {
                    warehouseItem.setVariationOptions(variationOptions);
                    warehouseItem.subtractQty(stockAdjustmentItem.getQty());
                    if (warehouseItem.getQty().compareTo(BigDecimal.ZERO) < 0) {
                        throw validationService.badRequestExceptionThrow(WAREHOUSE_QUANTITY_SHORTAGE).get();
                    }
                    warehouseItemRepository.save(warehouseItem);

                    List<WarehouseItemVariationOption> warehouseItemVariationOptions = stockItemVariationOptions.stream().map(stockAdjustmentItemVariationOptionId -> {
                        WarehouseItemVariationOption warehouseItemVariationOption = new WarehouseItemVariationOption();
                        warehouseItemVariationOption.setWarehouseItemId(warehouseItem.getId());
                        warehouseItemVariationOption.setStockItemVariationOptionId(stockAdjustmentItemVariationOptionId.getId());
                        warehouseItemVariationOption.setTransferType(StockTransferType.ADJUSTMENT);
                        return warehouseItemVariationOption;
                    }).collect(Collectors.toList());
                    warehouseItemVariationRepository.saveAll(warehouseItemVariationOptions);
                }
            } else {
                WarehouseItem warehouseItem = warehouseItemRepository.findByWarehouseIdAndItemIdAndUnitId(stockAdjustment.getWarehouseId(), stockAdjustmentItem.getItemId(), stockAdjustmentItem.getUnitId()).orElse(null);
                if (warehouseItem != null) {
                    warehouseItem.subtractQty(stockAdjustmentItem.getQty());
                    if (warehouseItem.getQty().compareTo(BigDecimal.ZERO) < 0) {
                        throw validationService.badRequestExceptionThrow(WAREHOUSE_QUANTITY_SHORTAGE).get();
                    }
                    warehouseItemRepository.save(warehouseItem);
                }
            }
        });
    }

    @Transactional
    @Override
    public void in(StockTransfer stockTransfer) {
        stockTransfer.getItems().forEach(stockTransferItem -> {
            List<StockItemVariationOption> stockItemVariationOptions = stockItemVariationOptionRepository.findAllByEntityIdAndEntityTypeAndDeletedIsFalse(stockTransferItem.getId(), stockTransfer.getEntityType());
            //FROM WAREHOUSE IN
            {
                if (!CollectionUtils.isEmpty(stockItemVariationOptions)) {
                    long[] variationOptions = stockItemVariationOptions.stream().mapToLong(StockItemVariationOption::getVariationOptionId).toArray();

                    WarehouseItem warehouseItem = warehouseItemRepository.findFirstByWarehouseIdAndItemIdAndUnitIdAndOptions(stockTransfer.getFromWarehouseId(), stockTransferItem.getFromItemId(), stockTransferItem.getUnitId(), variationOptions);
                    if (warehouseItem == null) {
                        warehouseItem = new WarehouseItem();
                        warehouseItem.setWarehouseId(stockTransfer.getFromWarehouseId());
                        warehouseItem.setItemId(stockTransferItem.getFromItemId());
                        warehouseItem.setUnitId(stockTransferItem.getUnitId());
                        warehouseItem.setVariationOptions(variationOptions);
                    }
                    warehouseItem.addQty(stockTransferItem.getQty());
                    warehouseItemRepository.save(warehouseItem);

                    WarehouseItem finalWarehouseItem = warehouseItem;
                    List<WarehouseItemVariationOption> warehouseItemVariationOptions = stockItemVariationOptions.stream().map(stockTransferItemVariationOption -> {
                        WarehouseItemVariationOption warehouseItemVariationOption = new WarehouseItemVariationOption();
                        warehouseItemVariationOption.setWarehouseItemId(finalWarehouseItem.getId());
                        warehouseItemVariationOption.setStockItemVariationOptionId(stockTransferItemVariationOption.getId());
                        warehouseItemVariationOption.setTransferType(StockTransferType.TRANSFER_TO);
                        return warehouseItemVariationOption;
                    }).collect(Collectors.toList());
                    warehouseItemVariationRepository.saveAll(warehouseItemVariationOptions);
                } else {
                    WarehouseItem warehouseItem = warehouseItemRepository.findByWarehouseIdAndItemIdAndUnitId(stockTransfer.getFromWarehouseId(), stockTransferItem.getFromItemId(), stockTransferItem.getUnitId()).orElseGet(WarehouseItem::new);
                    warehouseItem.setWarehouseId(stockTransfer.getFromWarehouseId());
                    warehouseItem.setItemId(stockTransferItem.getFromItemId());
                    warehouseItem.setUnitId(stockTransferItem.getUnitId());
                    warehouseItem.addQty(stockTransferItem.getQty());
                    warehouseItemRepository.save(warehouseItem);
                }
            }
            //FROM WAREHOUSE OUT
            {
                if (!CollectionUtils.isEmpty(stockItemVariationOptions)) {
                    long[] variationOptions = stockItemVariationOptions.stream().mapToLong(StockItemVariationOption::getVariationOptionId).toArray();

                    WarehouseItem warehouseItem = warehouseItemRepository.findFirstByWarehouseIdAndItemIdAndUnitIdAndOptions(stockTransfer.getToWarehouseId(), stockTransferItem.getToItemId(), stockTransferItem.getUnitId(), variationOptions);
                    if (warehouseItem != null) {
                        warehouseItem.subtractQty(stockTransferItem.getQty());
                        if (warehouseItem.getQty().compareTo(BigDecimal.ZERO) < 0) {
                            throw validationService.badRequestExceptionThrow(WAREHOUSE_QUANTITY_SHORTAGE).get();
                        }
                        warehouseItemRepository.save(warehouseItem);

                        List<WarehouseItemVariationOption> warehouseItemVariationOptions = stockItemVariationOptions.stream().map(stockTransferItemVariationOption -> {
                            WarehouseItemVariationOption warehouseItemVariationOption = new WarehouseItemVariationOption();
                            warehouseItemVariationOption.setWarehouseItemId(warehouseItem.getId());
                            warehouseItemVariationOption.setStockItemVariationOptionId(stockTransferItemVariationOption.getId());
                            warehouseItemVariationOption.setTransferType(StockTransferType.TRANSFER_FROM);
                            return warehouseItemVariationOption;
                        }).collect(Collectors.toList());
                        warehouseItemVariationRepository.saveAll(warehouseItemVariationOptions);
                    }

                } else {
                    WarehouseItem warehouseItem = warehouseItemRepository.findByWarehouseIdAndItemIdAndUnitId(stockTransfer.getToWarehouseId(), stockTransferItem.getToItemId(), stockTransferItem.getUnitId()).orElse(null);
                    if (warehouseItem != null) {
                        warehouseItem.subtractQty(stockTransferItem.getQty());
                        if (warehouseItem.getQty().compareTo(BigDecimal.ZERO) < 0) {
                            throw validationService.badRequestExceptionThrow(WAREHOUSE_QUANTITY_SHORTAGE).get();
                        }
                        warehouseItemRepository.save(warehouseItem);
                    }
                }
            }
        });
    }

    @Transactional
    @Override
    public void out(StockTransfer stockTransfer) {

        stockTransfer.getItems().forEach(stockTransferItem -> {
            List<StockItemVariationOption> stockItemVariationOptions = stockItemVariationOptionRepository.findAllByEntityIdAndEntityTypeAndDeletedIsFalse(stockTransferItem.getId(), stockTransfer.getEntityType());

            //FROM WAREHOUSE OUT
            {
                if (!CollectionUtils.isEmpty(stockItemVariationOptions)) {
                    long[] variationOptions = stockItemVariationOptions.stream().mapToLong(StockItemVariationOption::getVariationOptionId).toArray();

                    WarehouseItem warehouseItem = warehouseItemRepository.findFirstByWarehouseIdAndItemIdAndUnitIdAndOptions(stockTransfer.getFromWarehouseId(), stockTransferItem.getFromItemId(), stockTransferItem.getUnitId(), variationOptions);
                    if (warehouseItem != null) {
                        warehouseItem.setVariationOptions(variationOptions);
                        warehouseItem.subtractQty(stockTransferItem.getQty());
                        if (warehouseItem.getQty().compareTo(BigDecimal.ZERO) < 0) {
                            throw validationService.badRequestExceptionThrow(WAREHOUSE_QUANTITY_SHORTAGE).get();
                        }
                        warehouseItemRepository.save(warehouseItem);

                        List<WarehouseItemVariationOption> warehouseItemVariationOptions = stockItemVariationOptions.stream().map(stockTransferItemVariationOption -> {
                            WarehouseItemVariationOption warehouseItemVariationOption = new WarehouseItemVariationOption();
                            warehouseItemVariationOption.setWarehouseItemId(warehouseItem.getId());
                            warehouseItemVariationOption.setStockItemVariationOptionId(stockTransferItemVariationOption.getId());
                            warehouseItemVariationOption.setTransferType(StockTransferType.TRANSFER_FROM);
                            return warehouseItemVariationOption;
                        }).collect(Collectors.toList());
                        warehouseItemVariationRepository.saveAll(warehouseItemVariationOptions);
                    }
                } else {
                    WarehouseItem warehouseItem = warehouseItemRepository.findByWarehouseIdAndItemIdAndUnitId(stockTransfer.getFromWarehouseId(), stockTransferItem.getFromItemId(), stockTransferItem.getUnitId()).orElse(null);
                    if (warehouseItem != null) {
                        warehouseItem.subtractQty(stockTransferItem.getQty());
                        if (warehouseItem.getQty().compareTo(BigDecimal.ZERO) < 0) {
                            throw validationService.badRequestExceptionThrow(WAREHOUSE_QUANTITY_SHORTAGE).get();
                        }
                        warehouseItemRepository.save(warehouseItem);
                    }
                }
            }
            //FROM WAREHOUSE IN
            {
                if (!CollectionUtils.isEmpty(stockItemVariationOptions)) {
                    long[] variationOptions = stockItemVariationOptions.stream().mapToLong(StockItemVariationOption::getVariationOptionId).toArray();

                    WarehouseItem warehouseItem = warehouseItemRepository.findFirstByWarehouseIdAndItemIdAndUnitIdAndOptions(stockTransfer.getToWarehouseId(), stockTransferItem.getToItemId(), stockTransferItem.getUnitId(), variationOptions);
                    if (warehouseItem == null) {
                        warehouseItem = new WarehouseItem();
                        warehouseItem.setWarehouseId(stockTransfer.getToWarehouseId());
                        warehouseItem.setItemId(stockTransferItem.getToItemId());
                        warehouseItem.setUnitId(stockTransferItem.getUnitId());
                        warehouseItem.setVariationOptions(variationOptions);
                    }
                    warehouseItem.addQty(stockTransferItem.getQty());
                    warehouseItemRepository.save(warehouseItem);

                    WarehouseItem finalWarehouseItem = warehouseItem;
                    List<WarehouseItemVariationOption> warehouseItemVariationOptions = stockItemVariationOptions.stream().map(stockTransferItemVariationOption -> {
                        WarehouseItemVariationOption warehouseItemVariationOption = new WarehouseItemVariationOption();
                        warehouseItemVariationOption.setWarehouseItemId(finalWarehouseItem.getId());
                        warehouseItemVariationOption.setStockItemVariationOptionId(stockTransferItemVariationOption.getId());
                        warehouseItemVariationOption.setTransferType(StockTransferType.TRANSFER_TO);
                        return warehouseItemVariationOption;
                    }).collect(Collectors.toList());
                    warehouseItemVariationRepository.saveAll(warehouseItemVariationOptions);

                } else {
                    WarehouseItem warehouseItem = warehouseItemRepository.findByWarehouseIdAndItemIdAndUnitId(stockTransfer.getToWarehouseId(), stockTransferItem.getToItemId(), stockTransferItem.getUnitId()).orElseGet(WarehouseItem::new);
                    if (warehouseItem.getId() == null) {
                        warehouseItem.setWarehouseId(stockTransfer.getToWarehouseId());
                        warehouseItem.setItemId(stockTransferItem.getToItemId());
                        warehouseItem.setUnitId(stockTransferItem.getUnitId());
                    }
                    warehouseItem.addQty(stockTransferItem.getQty());
                    warehouseItemRepository.save(warehouseItem);
                }
            }
        });
    }

    @Override
    public BigDecimal getVariationsQty(StockFilter filter) {
        return warehouseItemRepository.getVariationsQty(filter);
    }

    @Override
    public StockBalanceReportStatisticsDTO getBalanceReportStatistics(StockFilter filter) {
        return stockRepository.getBalanceReportStatistics(filter);
    }

    @Override
    public Page<StockBalanceReportDetailListDTO> getBalanceReportDetails(StockFilter filter) {
        if (filter.getTransferType() == null) {
            throw validationService.requiredExceptionThrow(TRANSFER_TYPE).get();
        }
        return switch (filter.getTransferType()) {
            case IN,
                 OUT,
                 RETURN ->
                    stockRepository.getBalanceReportDetails(filter);
            case ADJUSTMENT ->
                    stockAdjustmentRepository.getBalanceReportDetails(filter);
            case TRANSFER_FROM,
                 TRANSFER_TO ->
                    stockTransferRepository.getBalanceReportDetails(filter);
        };
    }

}
