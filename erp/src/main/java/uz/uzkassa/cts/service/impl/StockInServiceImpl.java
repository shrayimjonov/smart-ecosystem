package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.domain.stock.Stock;
import uz.uzkassa.cts.domain.stock.StockItem;
import uz.uzkassa.cts.domain.stock.StockItemVariationOption;
import uz.uzkassa.cts.domain.stock.StockTransfer;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.stock.StockItemProductVariationDTO;
import uz.uzkassa.cts.dto.stockin.*;
import uz.uzkassa.cts.enums.Prefix;
import uz.uzkassa.cts.enums.StockStatus;
import uz.uzkassa.cts.enums.StockTransferStatus;
import uz.uzkassa.cts.enums.StockTransferType;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.numbering.NumberingContext;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.StockInService;
import uz.uzkassa.cts.service.WarehouseItemService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StockInServiceImpl extends StockBaseServiceImpl<Stock, Long> implements StockInService {

    public StockInServiceImpl(BaseRepository<Stock, Long> repository, StockRepository stockRepository, StockItemRepository stockItemRepository, StockAdjustmentRepository stockAdjustmentRepository, StockTransferRepository stockTransferRepository, StockItemVariationOptionRepository stockItemVariationOptionRepository, ProductRepository productRepository, DeviceRepository deviceRepository, FiscalModuleRepository fiscalModuleRepository, AccessoryRepository accessoryRepository, WarehouseItemService warehouseItemService, NumberingContext numberingContext, ValidationContext validationContext, ReferenceRepository referenceRepository) {
        super(repository, stockRepository, stockItemRepository, stockAdjustmentRepository, stockTransferRepository, stockItemVariationOptionRepository, productRepository, deviceRepository, fiscalModuleRepository, accessoryRepository, warehouseItemService, numberingContext, validationContext, referenceRepository);
    }

    @Override
    @Transactional
    public Long create(StockInDTO stockInDTO) {
        validationContext.validate(stockInDTO);
        Stock stock = new Stock();
        if (StringUtils.isNotEmpty(stockInDTO.getStockNumber())) {
            stock.setStockNumber(stockInDTO.getStockNumber());
        } else {
            Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.STOCK_IN, stockInDTO.getCompanyId());
            stock.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
            stock.setStockNumber(numberingMap.get(NUMBER));
        }
        stock.setStockDate(stockInDTO.getStockDate());
        stock.setTransferType(StockTransferType.IN);
        stock.setDescription(stockInDTO.getDescription());
        stock.setCompanyId(stockInDTO.getCompanyId());
        stock.setBranchId(stockInDTO.getBranchId());
        stock.setDepartmentId(stockInDTO.getDepartmentId());
        stock.setWarehouseId(stockInDTO.getWarehouseId());
        stock.setStockTransferId(stockInDTO.getStockTransferId());
        if (stockInDTO.getStockTransferId() != null) {
            stock.setStatus(StockStatus.COMPLETED);
        }
        stockRepository.save(stock);
        for (StockInItemDTO stockInItemDTO : stockInDTO.getItems()) {
            validationContext.validate(stockInItemDTO);

            StockItem stockItem = new StockItem();
            stockItem.setItemId(stockInItemDTO.getItemId());
            stockItem.setItemType(stockInItemDTO.getItemType());
            stockItem.setUnitId(stockInItemDTO.getUnitId());
            stockItem.setQty(stockInItemDTO.getQty());
            stock.addItem(stockItem);
            stockItemRepository.save(stockItem);

            if (!CollectionUtils.isEmpty(stockInItemDTO.getVariationOptionIds())) {
                List<StockItemVariationOption> stockItemVariationOptions = stockInItemDTO.getVariationOptionIds()
                        .stream()
                        .map(variationOptionId -> {
                            StockItemVariationOption stockItemVariationOption = new StockItemVariationOption();
                            stockItemVariationOption.setEntityId(stockItem.getId());
                            stockItemVariationOption.setEntityType(stock.getEntityType());
                            stockItemVariationOption.setVariationOptionId(variationOptionId);
                            return stockItemVariationOption;
                        }).toList();
                stockItemVariationOptionRepository.saveAll(stockItemVariationOptions);
            }
        }
        return stock.getId();
    }

    @Override
    @Transactional
    public void create(StockTransfer stockTransfer) {
        StockInDTO stockInDTO = new StockInDTO();
        stockInDTO.setCompanyId(stockTransfer.getCompanyId());
        stockInDTO.setDescription(stockTransfer.getDescription());
        stockInDTO.setStockDate(stockTransfer.getTransferDate());
        stockInDTO.setStockTransferId(stockTransfer.getId());
        if (StockTransferStatus.APPROVED == stockTransfer.getStatus()) {
            stockInDTO.setBranchId(stockTransfer.getToBranchId());
            stockInDTO.setWarehouseId(stockTransfer.getToWarehouseId());
        } else {
            stockInDTO.setBranchId(stockTransfer.getFromBranchId());
            stockInDTO.setWarehouseId(stockTransfer.getFromWarehouseId());
        }
        stockTransfer.getItems().forEach(stockTransferItem -> {
            StockInItemDTO stockInItemDTO = new StockInItemDTO();
            stockInItemDTO.setItemId(stockTransferItem.getFromItemId());
            List<Long> variationOptionIds = stockItemVariationOptionRepository.findAllVariationOptionIdsByEntityIdAndEntityTypeAndDeletedIsFalse(stockTransferItem.getId(), stockTransfer.getEntityType());
            stockInItemDTO.setVariationOptionIds(variationOptionIds);
            stockInItemDTO.setUnitId(stockTransferItem.getUnitId());
            stockInItemDTO.setQty(stockTransferItem.getQty());
            stockInDTO.addItem(stockInItemDTO);
        });
        this.create(stockInDTO);
    }

    @Override
    @Transactional
    public Long update(Long id, StockInDTO stockInDTO) {
        stockInDTO.setId(id);
        validationContext.validate(stockInDTO);
        return stockRepository
                .findById(id)
                .map(stock -> {
                    if (StockStatus.DRAFT != stock.getStatus()) {
                        throw validationService.statusChangeExceptionThrow(StockStatus.DRAFT.getName()).get();
                    }

                    stock.setStockDate(stockInDTO.getStockDate());
                    stock.setStatus(StockStatus.DRAFT);
                    stock.setDescription(stockInDTO.getDescription());
                    stock.setCompanyId(stockInDTO.getCompanyId());
                    stock.setBranchId(stockInDTO.getBranchId());
                    stock.setDepartmentId(stockInDTO.getDepartmentId());
                    stock.setWarehouseId(stockInDTO.getWarehouseId());

                    Map<Long, StockItem> stockItemsMap = stock
                            .getItems()
                            .stream()
                            .collect(Collectors.toMap(StockItem::getId, Function.identity()));

                    for (StockInItemDTO stockInItemDTO : stockInDTO.getItems()) {
                        validationContext.validate(stockInItemDTO);

                        StockItem stockItem;
                        if (stockInItemDTO.getId() != null && stockItemsMap.get(stockInItemDTO.getId()) != null) {
                            stockItem = stockItemsMap.get(stockInItemDTO.getId());
                            stockItemsMap.remove(stockItem.getId());
                        } else {
                            stockItem = new StockItem();
                        }
                        stockItem.setItemId(stockInItemDTO.getItemId());
                        stockItem.setItemType(stockInItemDTO.getItemType());
                        stockItem.setUnitId(stockInItemDTO.getUnitId());
                        stockItem.setQty(stockInItemDTO.getQty());
                        stock.addItem(stockItem);
                        if (stockItem.getId() == null) {
                            stockItemRepository.save(stockItem);
                        }
                        if (!CollectionUtils.isEmpty(stockInItemDTO.getVariationOptionIds())) {
                            stockItemVariationOptionRepository.deleteAllByEntityIdAndEntityType(stockItem.getId(), stock.getEntityType());
                            List<StockItemVariationOption> stockItemVariationOptions = stockInItemDTO.getVariationOptionIds()
                                    .stream()
                                    .map(variationOptionId -> {
                                        StockItemVariationOption stockItemVariationOption = new StockItemVariationOption();
                                        stockItemVariationOption.setEntityId(stockItem.getId());
                                        stockItemVariationOption.setEntityType(stock.getEntityType());
                                        stockItemVariationOption.setVariationOptionId(variationOptionId);
                                        return stockItemVariationOption;
                                    }).toList();
                            stockItemVariationOptionRepository.saveAll(stockItemVariationOptions);
                        }
                    }
                    if (!CollectionUtils.isEmpty(stockItemsMap.values())) {
                        stockItemRepository.deleteAllInBatch(stockItemsMap.values());
                    }
                    return id;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(STOCK));
    }

    @Override
    public Page<StockInListDTO> getList(StockFilter filter) {
        filter.setTransferType(StockTransferType.IN);
        ResultList<Stock> resultList = stockRepository.getResultList(filter);
        List<StockInListDTO> result = resultList
                .getList()
                .stream()
                .map(stock -> {
                    StockInListDTO stockInListDTO = new StockInListDTO();
                    stockInListDTO.setId(stock.getId());
                    stockInListDTO.setStockNumber(stock.getStockNumber());
                    stockInListDTO.setStockDate(stock.getStockDate());
                    stockInListDTO.setBranch(stock.getBranch().toCommonDTO());
                    stockInListDTO.setCompany(stock.getCompany().toShortDTO());
                    if (stock.getDepartmentId() != null) {
                        stockInListDTO.setDepartment(stock.getDepartment().toCommonDTO());
                    }
                    stockInListDTO.setWarehouse(stock.getWarehouse().toCommonDTO());
                    stockInListDTO.setStatus(stock.getStatus());
                    return stockInListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public StockInDetailDTO get(Long id) {
        return stockRepository.findById(id).map(stock -> {
            StockInDetailDTO stockInDetailDTO = new StockInDetailDTO();
            stockInDetailDTO.setId(stock.getId());
            stockInDetailDTO.setStockNumber(stock.getStockNumber());
            stockInDetailDTO.setStockDate(stock.getStockDate());
            stockInDetailDTO.setStatus(stock.getStatus());
            stockInDetailDTO.setDescription(stock.getDescription());
            stockInDetailDTO.setBranch(stock.getBranch().toCommonDTO());
            stockInDetailDTO.setCompany(stock.getCompany().toCommonDTO());
            if (stock.getDepartmentId() != null) {
                stockInDetailDTO.setDepartment(stock.getDepartment().toCommonDTO());
            }
            stockInDetailDTO.setWarehouse(stock.getWarehouse().toCommonDTO());

            stock.getItems().forEach(stockItem -> {
                StockInItemDetailDTO stockInItemDetailDTO = new StockInItemDetailDTO();
                stockInItemDetailDTO.setId(stockItem.getId());
                stockInItemDetailDTO.setItem(getItem(stockItem.getItemId(), stockItem.getItemType()));
                stockInItemDetailDTO.setUnit(stockItem.getUnit().toCommonDTO());
                stockInItemDetailDTO.setQty(stockItem.getQty());
                stockItemVariationOptionRepository.findAllByEntityIdAndEntityTypeAndDeletedIsFalse(stockItem.getId(), stock.getEntityType()).forEach(stockVariationOption -> {
                    StockItemProductVariationDTO stockItemProductVariationDTO = new StockItemProductVariationDTO();
                    stockItemProductVariationDTO.setId(stockVariationOption.getVariationOption().getId());
                    stockItemProductVariationDTO.setName(stockVariationOption.getVariationOption().getName());
                    stockItemProductVariationDTO.setVariationType(stockVariationOption.getVariationOption().getVariationType().toCommonDTO());
                    stockInItemDetailDTO.addProductVariation(stockItemProductVariationDTO);
                });
                stockInDetailDTO.addItem(stockInItemDetailDTO);
            });
            return stockInDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(STOCK));
    }

    @Override
    public List<CommonDTO> lookUp(StockFilter filter) {
        filter.setTransferType(StockTransferType.IN);
        return stockRepository.getResultList(filter).getList().stream().map(SimpleEntity::toCommonDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, StockStatus status) {
        stockRepository
                .findById(id)
                .ifPresentOrElse(stock -> {
                    if (StockStatus.COMPLETED == status) {
                        if (StockStatus.DRAFT != stock.getStatus()) {
                            throw validationService.statusChangeExceptionThrow(StockStatus.DRAFT.getName()).get();
                        }
                        warehouseItemService.in(stock);
                    } else if (StockStatus.CANCELLED == status) {
                        if (StockStatus.COMPLETED != stock.getStatus()) {
                            throw validationService.statusChangeExceptionThrow(StockStatus.COMPLETED.getName()).get();
                        }
                        warehouseItemService.out(stock);
                    }
                    stock.setStatus(status);
                    stockRepository.save(stock);
                }, () -> validationService.entityNotFoundExceptionThrow(STOCK).get());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        stockRepository.findById(id)
                .ifPresentOrElse(stock -> {
                    if (StockStatus.DRAFT == stock.getStatus() || StockStatus.CANCELLED == stock.getStatus()) {
                        stock.setDeleted(true);
                        stockRepository.save(stock);
                    } else {
                        throw validationService.statusChangeExceptionThrow("черновик или отменен").get();
                    }
                }, () -> validationService.entityNotFoundExceptionThrow(STOCK).get());
    }
}
