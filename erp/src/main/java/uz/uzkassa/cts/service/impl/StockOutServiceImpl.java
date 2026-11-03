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
import uz.uzkassa.cts.dto.stockout.*;
import uz.uzkassa.cts.enums.Prefix;
import uz.uzkassa.cts.enums.StockStatus;
import uz.uzkassa.cts.enums.StockTransferStatus;
import uz.uzkassa.cts.enums.StockTransferType;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.numbering.NumberingContext;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.StockOutService;
import uz.uzkassa.cts.service.WarehouseItemService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StockOutServiceImpl extends StockBaseServiceImpl<Stock, Long> implements StockOutService {

    public StockOutServiceImpl(BaseRepository<Stock, Long> repository, StockRepository stockRepository, StockItemRepository stockItemRepository, StockAdjustmentRepository stockAdjustmentRepository, StockTransferRepository stockTransferRepository, StockItemVariationOptionRepository stockItemVariationOptionRepository, ProductRepository productRepository, DeviceRepository deviceRepository, FiscalModuleRepository fiscalModuleRepository, AccessoryRepository accessoryRepository, WarehouseItemService warehouseItemService, NumberingContext numberingContext, ValidationContext validationContext, ReferenceRepository referenceRepository) {
        super(repository, stockRepository, stockItemRepository, stockAdjustmentRepository, stockTransferRepository, stockItemVariationOptionRepository, productRepository, deviceRepository, fiscalModuleRepository, accessoryRepository, warehouseItemService, numberingContext, validationContext, referenceRepository);
    }

    @Override
    @Transactional
    public Long create(StockOutDTO stockOutDTO) {
        validationContext.validate(stockOutDTO);
        Stock stock = new Stock();
        if (StringUtils.isNotEmpty(stockOutDTO.getStockNumber())) {
            stock.setStockNumber(stockOutDTO.getStockNumber());
        } else {
            Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.STOCK_OUT, stockOutDTO.getCompanyId());
            stock.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
            stock.setStockNumber(numberingMap.get(NUMBER));
        }
        stock.setStockDate(stockOutDTO.getStockDate());
        stock.setTransferType(StockTransferType.OUT);
        stock.setDescription(stockOutDTO.getDescription());
        stock.setCompanyId(stockOutDTO.getCompanyId());
        stock.setBranchId(stockOutDTO.getBranchId());
        stock.setDepartmentId(stockOutDTO.getDepartmentId());
        stock.setWarehouseId(stockOutDTO.getWarehouseId());
        stock.setStockTransferId(stockOutDTO.getStockTransferId());
        if (stockOutDTO.getStockTransferId() != null) {
            stock.setStatus(StockStatus.COMPLETED);
        }
        stockRepository.save(stock);

        for (StockOutItemDTO stockOutItemDTO : stockOutDTO.getItems()) {
            validationContext.validate(stockOutItemDTO);

            StockItem stockItem = new StockItem();
            stockItem.setItemId(stockOutItemDTO.getItemId());
            stockItem.setUnitId(stockOutItemDTO.getUnitId());
            stockItem.setQty(stockOutItemDTO.getQty());
            stock.addItem(stockItem);
            stockItemRepository.save(stockItem);

            if (!CollectionUtils.isEmpty(stockOutItemDTO.getVariationOptionIds())) {
                List<StockItemVariationOption> stockItemVariationOptions = stockOutItemDTO.getVariationOptionIds().stream().map(variationOptionId -> {
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
        StockOutDTO stockOutDTO = new StockOutDTO();
        stockOutDTO.setCompanyId(stockTransfer.getCompanyId());
        stockOutDTO.setStockDate(stockTransfer.getTransferDate());
        stockOutDTO.setDescription(stockTransfer.getDescription());
        stockOutDTO.setStockTransferId(stockTransfer.getId());
        if (StockTransferStatus.APPROVED == stockTransfer.getStatus()) {
            stockOutDTO.setBranchId(stockTransfer.getFromBranchId());
            stockOutDTO.setWarehouseId(stockTransfer.getFromWarehouseId());
        } else {
            stockOutDTO.setBranchId(stockTransfer.getToBranchId());
            stockOutDTO.setWarehouseId(stockTransfer.getToWarehouseId());
        }
        stockTransfer.getItems().forEach(stockTransferItem -> {
            StockOutItemDTO stockInItemDTO = new StockOutItemDTO();
            stockInItemDTO.setItemId(stockTransferItem.getFromItemId());
            List<Long> variationOptionIds = stockItemVariationOptionRepository.findAllVariationOptionIdsByEntityIdAndEntityTypeAndDeletedIsFalse(stockTransferItem.getId(), stockTransfer.getEntityType());
            stockInItemDTO.setVariationOptionIds(variationOptionIds);
            stockInItemDTO.setUnitId(stockTransferItem.getUnitId());
            stockInItemDTO.setQty(stockTransferItem.getQty());
            stockOutDTO.addItem(stockInItemDTO);
        });
        this.create(stockOutDTO);
    }

    @Override
    @Transactional
    public Long update(Long id, StockOutDTO stockOutDTO) {
        stockOutDTO.setId(id);
        validationContext.validate(stockOutDTO);
        return stockRepository
                .findById(id)
                .map(stock -> {
                    if (StockStatus.DRAFT != stock.getStatus()) {
                        throw validationService.statusChangeExceptionThrow(StockStatus.DRAFT.getName()).get();
                    }

                    stock.setStockDate(stockOutDTO.getStockDate());
                    stock.setStatus(StockStatus.DRAFT);
                    stock.setDescription(stockOutDTO.getDescription());
                    stock.setCompanyId(stockOutDTO.getCompanyId());
                    stock.setBranchId(stockOutDTO.getBranchId());
                    stock.setDepartmentId(stockOutDTO.getDepartmentId());
                    stock.setWarehouseId(stockOutDTO.getWarehouseId());

                    Map<Long, StockItem> stockItemsMap = stock
                            .getItems()
                            .stream()
                            .collect(Collectors.toMap(StockItem::getId, Function.identity()));

                    for (StockOutItemDTO stockOutItemDTO : stockOutDTO.getItems()) {
                        validationContext.validate(stockOutItemDTO);

                        StockItem stockItem;
                        if (stockOutItemDTO.getId() != null && stockItemsMap.get(stockOutItemDTO.getId()) != null) {
                            stockItem = stockItemsMap.get(stockOutItemDTO.getId());
                            stockItemsMap.remove(stockItem.getId());
                        } else {
                            stockItem = new StockItem();
                        }
                        stockItem.setItemId(stockOutItemDTO.getItemId());
                        stockItem.setUnitId(stockOutItemDTO.getUnitId());
                        stockItem.setQty(stockOutItemDTO.getQty());
                        stock.addItem(stockItem);
                        if (stockItem.getId() == null) {
                            stockItemRepository.save(stockItem);
                        }
                        if (!CollectionUtils.isEmpty(stockOutItemDTO.getVariationOptionIds())) {
                            List<StockItemVariationOption> stockItemVariationOptions = stockOutItemDTO.getVariationOptionIds()
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
    public Page<StockOutListDTO> getList(StockFilter filter) {
        filter.setTransferType(StockTransferType.OUT);
        ResultList<Stock> resultList = stockRepository.getResultList(filter);
        List<StockOutListDTO> result = resultList
                .getList()
                .stream()
                .map(stock -> {
                    StockOutListDTO stockOutListDTO = new StockOutListDTO();
                    stockOutListDTO.setId(stock.getId());
                    stockOutListDTO.setStockNumber(stock.getStockNumber());
                    stockOutListDTO.setStockDate(stock.getStockDate());
                    stockOutListDTO.setBranch(stock.getBranch().toCommonDTO());
                    stockOutListDTO.setCompany(stock.getCompany().toShortDTO());
                    if (stock.getDepartmentId() != null) {
                        stockOutListDTO.setDepartment(stock.getDepartment().toCommonDTO());
                    }
                    stockOutListDTO.setWarehouse(stock.getWarehouse().toCommonDTO());
                    stockOutListDTO.setStatus(stock.getStatus());
                    return stockOutListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public StockOutDetailDTO get(Long id) {
        return stockRepository.findById(id).map(stock -> {
            StockOutDetailDTO stockOutDetailDTO = new StockOutDetailDTO();
            stockOutDetailDTO.setId(stock.getId());
            stockOutDetailDTO.setStockNumber(stock.getStockNumber());
            stockOutDetailDTO.setStockDate(stock.getStockDate());
            stockOutDetailDTO.setStatus(stock.getStatus());
            stockOutDetailDTO.setDescription(stock.getDescription());
            stockOutDetailDTO.setBranch(stock.getBranch().toCommonDTO());
            stockOutDetailDTO.setCompany(stock.getCompany().toCommonDTO());
            if (stock.getDepartmentId() != null) {
                stockOutDetailDTO.setDepartment(stock.getDepartment().toCommonDTO());
            }
            stockOutDetailDTO.setWarehouse(stock.getWarehouse().toCommonDTO());
            stock.getItems().forEach(stockItem -> {
                StockOutItemDetailDTO stockOutItemDetailDTO = new StockOutItemDetailDTO();
                stockOutItemDetailDTO.setId(stockItem.getId());
                stockOutItemDetailDTO.setItem(getItem(stockItem.getItemId(), stockItem.getItemType()));
                stockOutItemDetailDTO.setUnit(stockItem.getUnit().toCommonDTO());
                stockOutItemDetailDTO.setQty(stockItem.getQty());
                stockItemVariationOptionRepository.findAllByEntityIdAndEntityTypeAndDeletedIsFalse(stockItem.getId(), stock.getEntityType()).forEach(stockVariationOption -> {
                    StockItemProductVariationDTO stockItemProductVariationDTO = new StockItemProductVariationDTO();
                    stockItemProductVariationDTO.setId(stockVariationOption.getVariationOptionId());
                    stockItemProductVariationDTO.setName(stockVariationOption.getVariationOption().getName());
                    stockItemProductVariationDTO.setVariationType(stockVariationOption.getVariationOption().getVariationType().toCommonDTO());
                    stockOutItemDetailDTO.addProductVariation(stockItemProductVariationDTO);
                });
                stockOutDetailDTO.addItem(stockOutItemDetailDTO);
            });
            return stockOutDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(STOCK));
    }

    @Override
    public List<CommonDTO> lookUp(StockFilter filter) {
        filter.setTransferType(StockTransferType.OUT);
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
                        warehouseItemService.out(stock);
                    } else if (StockStatus.CANCELLED == status) {
                        if (StockStatus.COMPLETED != stock.getStatus()) {
                            throw validationService.statusChangeExceptionThrow(StockStatus.COMPLETED.getName()).get();
                        }
                        warehouseItemService.in(stock);
                    }
                    stock.setStatus(status);
                    stockRepository.save(stock);
                }, () -> validationService.entityNotFoundExceptionThrow(STOCK).get());
    }

    @Override
    public List<CommonDTO> getItemsLookUp(StockFilter filter) {
        filter.setTransferType(StockTransferType.IN);
        return stockItemRepository.lookUp(filter);
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
                        throw validationService.statusChangeExceptionThrow(StockStatus.DRAFT.getName() + "," + StockStatus.CANCELLED.getName()).get();
                    }
                }, () -> validationService.entityNotFoundExceptionThrow(STOCK).get());
    }
}
