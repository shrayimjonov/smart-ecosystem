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
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.stock.StockItemProductVariationDTO;
import uz.uzkassa.cts.dto.stockreturn.*;
import uz.uzkassa.cts.enums.Prefix;
import uz.uzkassa.cts.enums.StockStatus;
import uz.uzkassa.cts.enums.StockTransferType;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.numbering.NumberingContext;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.StockReturnService;
import uz.uzkassa.cts.service.WarehouseItemService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StockReturnServiceImpl extends StockBaseServiceImpl<Stock, Long> implements StockReturnService {


    public StockReturnServiceImpl(BaseRepository<Stock, Long> repository, StockRepository stockRepository, StockItemRepository stockItemRepository, StockAdjustmentRepository stockAdjustmentRepository, StockTransferRepository stockTransferRepository, StockItemVariationOptionRepository stockItemVariationOptionRepository, ProductRepository productRepository, DeviceRepository deviceRepository, FiscalModuleRepository fiscalModuleRepository, AccessoryRepository accessoryRepository, WarehouseItemService warehouseItemService, NumberingContext numberingContext, ValidationContext validationContext, ReferenceRepository referenceRepository) {
        super(repository, stockRepository, stockItemRepository, stockAdjustmentRepository, stockTransferRepository, stockItemVariationOptionRepository, productRepository, deviceRepository, fiscalModuleRepository, accessoryRepository, warehouseItemService, numberingContext, validationContext, referenceRepository);
    }

    @Override
    @Transactional
    public Long create(StockReturnDTO stockReturnDTO) {
        validationContext.validate(stockReturnDTO);
        Stock stock = new Stock();
        if (StringUtils.isNotEmpty(stockReturnDTO.getStockNumber())) {
            stock.setStockNumber(stockReturnDTO.getStockNumber());
        } else {
            Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.STOCK_RETURN, stockReturnDTO.getCompanyId());
            stock.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
            stock.setStockNumber(numberingMap.get(NUMBER));
        }
        stock.setStockDate(stockReturnDTO.getStockDate());
        stock.setTransferType(StockTransferType.RETURN);
        stock.setStatus(StockStatus.DRAFT);
        stock.setDescription(stockReturnDTO.getDescription());
        stock.setCompanyId(stockReturnDTO.getCompanyId());
        stock.setBranchId(stockReturnDTO.getBranchId());
        stock.setDepartmentId(stockReturnDTO.getDepartmentId());
        stock.setWarehouseId(stockReturnDTO.getWarehouseId());
        stockRepository.save(stock);

        for (StockReturnItemDTO stockReturnItemDTO : stockReturnDTO.getItems()) {
            validationContext.validate(stockReturnItemDTO);

            StockItem stockItem = new StockItem();
            stockItem.setItemId(stockReturnItemDTO.getItemId());
            stockItem.setUnitId(stockReturnItemDTO.getUnitId());
            stockItem.setQty(stockReturnItemDTO.getQty());
            stock.addItem(stockItem);
            stockItemRepository.save(stockItem);

            if (!CollectionUtils.isEmpty(stockReturnItemDTO.getVariationOptionIds())) {
                List<StockItemVariationOption> stockItemVariationOptions = stockReturnItemDTO.getVariationOptionIds().stream().map(variationOptionId -> {
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
    public Long update(Long id, StockReturnDTO stockOutDTO) {
        stockOutDTO.setId(id);
        validationContext.validate(stockOutDTO);
        return stockRepository
                .findById(id)
                .map(stock -> {
                    if (StockStatus.DRAFT != stock.getStatus()) {
                        throw validationService.statusChangeExceptionThrow(StockStatus.DRAFT.getName()).get();
                    }

                    stock.setStockDate(stockOutDTO.getStockDate());
                    stock.setDescription(stockOutDTO.getDescription());
                    stock.setCompanyId(stockOutDTO.getCompanyId());
                    stock.setBranchId(stockOutDTO.getBranchId());
                    stock.setDepartmentId(stockOutDTO.getDepartmentId());
                    stock.setWarehouseId(stockOutDTO.getWarehouseId());

                    Map<Long, StockItem> stockItemsMap = stock
                            .getItems()
                            .stream()
                            .collect(Collectors.toMap(StockItem::getId, Function.identity()));

                    for (StockReturnItemDTO stockReturnItemDTO : stockOutDTO.getItems()) {
                        validationContext.validate(stockReturnItemDTO);

                        StockItem stockItem;
                        if (stockReturnItemDTO.getId() != null && stockItemsMap.get(stockReturnItemDTO.getId()) != null) {
                            stockItem = stockItemsMap.get(stockReturnItemDTO.getId());
                            stockItemsMap.remove(stockItem.getId());
                        } else {
                            stockItem = new StockItem();
                        }
                        stockItem.setItemId(stockReturnItemDTO.getItemId());
                        stockItem.setUnitId(stockReturnItemDTO.getUnitId());
                        stockItem.setQty(stockReturnItemDTO.getQty());
                        stock.addItem(stockItem);
                        if (stockItem.getId() == null) {
                            stockItemRepository.save(stockItem);
                        }
                        if (!CollectionUtils.isEmpty(stockReturnItemDTO.getVariationOptionIds())) {
                            stockItemVariationOptionRepository.deleteAllByEntityIdAndEntityType(stockItem.getId(), stock.getEntityType());
                            List<StockItemVariationOption> stockItemVariationOptions = stockReturnItemDTO.getVariationOptionIds().stream().map(variationOptionId -> {
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
    public Page<StockReturnListDTO> getList(StockFilter filter) {
        filter.setTransferType(StockTransferType.RETURN);
        ResultList<Stock> resultList = stockRepository.getResultList(filter);
        List<StockReturnListDTO> result = resultList
                .getList()
                .stream()
                .map(stock -> {
                    StockReturnListDTO stockReturnListDTO = new StockReturnListDTO();
                    stockReturnListDTO.setId(stock.getId());
                    stockReturnListDTO.setStockNumber(stock.getStockNumber());
                    stockReturnListDTO.setStockDate(stock.getStockDate());
                    stockReturnListDTO.setCompany(stock.getCompany().toShortDTO());
                    stockReturnListDTO.setBranch(stock.getBranch().toCommonDTO());
                    if (stock.getDepartmentId() != null) {
                        stockReturnListDTO.setDepartment(stock.getDepartment().toCommonDTO());
                    }
                    stockReturnListDTO.setWarehouse(stock.getWarehouse().toCommonDTO());
                    stockReturnListDTO.setStatus(stock.getStatus());
                    return stockReturnListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public StockReturnDetailDTO get(Long id) {
        return stockRepository.findById(id).map(stock -> {
            StockReturnDetailDTO stockReturnDetailDTO = new StockReturnDetailDTO();
            stockReturnDetailDTO.setId(stock.getId());
            stockReturnDetailDTO.setStockNumber(stock.getStockNumber());
            stockReturnDetailDTO.setStockDate(stock.getStockDate());
            stockReturnDetailDTO.setStatus(stock.getStatus());
            stockReturnDetailDTO.setDescription(stock.getDescription());
            stockReturnDetailDTO.setCompany(stock.getCompany().toCommonDTO());
            stockReturnDetailDTO.setBranch(stock.getBranch().toCommonDTO());
            if (stock.getDepartmentId() != null) {
                stockReturnDetailDTO.setDepartment(stock.getDepartment().toCommonDTO());
            }
            stockReturnDetailDTO.setWarehouse(stock.getWarehouse().toCommonDTO());

            stock.getItems().forEach(stockItem -> {
                StockReturnItemDetailDTO stockReturnItemDetailDTO = new StockReturnItemDetailDTO();
                stockReturnItemDetailDTO.setId(stockItem.getId());
                stockReturnItemDetailDTO.setItem(getItem(stockItem.getItemId(), stockItem.getItemType()));
                stockReturnItemDetailDTO.setUnit(stockItem.getUnit().toCommonDTO());
                stockReturnItemDetailDTO.setQty(stockItem.getQty());
                stockItemVariationOptionRepository.findAllByEntityIdAndEntityTypeAndDeletedIsFalse(stockItem.getId(), stock.getEntityType())
                        .forEach(stockVariationOption -> {
                            StockItemProductVariationDTO stockItemProductVariationDTO = new StockItemProductVariationDTO();
                            stockItemProductVariationDTO.setId(stockVariationOption.getVariationOptionId());
                            stockItemProductVariationDTO.setName(stockVariationOption.getVariationOption().getName());
                            stockItemProductVariationDTO.setVariationType(stockVariationOption.getVariationOption().getVariationType().toCommonDTO());
                            stockReturnItemDetailDTO.addProductVariation(stockItemProductVariationDTO);
                        });
                stockReturnDetailDTO.addItem(stockReturnItemDetailDTO);
            });
            return stockReturnDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(STOCK));
    }

    @Override
    public List<CommonDTO> lookUp(StockFilter filter) {
        filter.setTransferType(StockTransferType.RETURN);
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
