package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.stock.StockAdjustment;
import uz.uzkassa.cts.domain.stock.StockAdjustmentItem;
import uz.uzkassa.cts.domain.stock.StockItemVariationOption;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.stock.StockItemProductVariationDTO;
import uz.uzkassa.cts.dto.stockadjustment.*;
import uz.uzkassa.cts.enums.Prefix;
import uz.uzkassa.cts.enums.StockStatus;
import uz.uzkassa.cts.enums.StockTransferType;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.numbering.NumberingContext;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.StockAdjustmentService;
import uz.uzkassa.cts.service.WarehouseItemService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StockAdjustmentServiceImpl extends StockBaseServiceImpl<StockAdjustment, Long> implements StockAdjustmentService {

    StockAdjustmentItemRepository stockAdjustmentItemRepository;

    public StockAdjustmentServiceImpl(BaseRepository<StockAdjustment, Long> repository, StockRepository stockRepository, StockItemRepository stockItemRepository, StockAdjustmentRepository stockAdjustmentRepository, StockTransferRepository stockTransferRepository, StockItemVariationOptionRepository stockItemVariationOptionRepository, ProductRepository productRepository, DeviceRepository deviceRepository, FiscalModuleRepository fiscalModuleRepository, AccessoryRepository accessoryRepository, WarehouseItemService warehouseItemService, NumberingContext numberingContext, ValidationContext validationContext, ReferenceRepository referenceRepository, StockAdjustmentItemRepository stockAdjustmentItemRepository) {
        super(repository, stockRepository, stockItemRepository, stockAdjustmentRepository, stockTransferRepository, stockItemVariationOptionRepository, productRepository, deviceRepository, fiscalModuleRepository, accessoryRepository, warehouseItemService, numberingContext, validationContext, referenceRepository);
        this.stockAdjustmentItemRepository = stockAdjustmentItemRepository;
    }


    @Override
    @Transactional
    public Long create(StockAdjustmentDTO stockAdjustmentDTO) {
        validationContext.validate(stockAdjustmentDTO);
        StockAdjustment stockAdjustment = new StockAdjustment();
        if (StringUtils.isNotEmpty(stockAdjustmentDTO.getAdjustmentNumber())) {
            stockAdjustment.setAdjustmentNumber(stockAdjustmentDTO.getAdjustmentNumber());
        } else {
            Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.STOCK_ADJUSTMENT, stockAdjustmentDTO.getCompanyId());
            stockAdjustment.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
            stockAdjustment.setAdjustmentNumber(numberingMap.get(NUMBER));
        }
        stockAdjustment.setCompanyId(stockAdjustmentDTO.getCompanyId());
        stockAdjustment.setBranchId(stockAdjustmentDTO.getBranchId());
        stockAdjustment.setWarehouseId(stockAdjustmentDTO.getWarehouseId());
        stockAdjustment.setDescription(stockAdjustmentDTO.getDescription());
        stockAdjustment.setAdjustmentDate(stockAdjustmentDTO.getAdjustmentDate());
        stockAdjustmentRepository.save(stockAdjustment);

        BigDecimal grandTotalQty = BigDecimal.ZERO;

        for (StockAdjustmentItemDTO stockAdjustmentItemDTO : stockAdjustmentDTO.getItems()) {
            validationContext.validate(stockAdjustmentItemDTO);
            StockAdjustmentItem stockAdjustmentItem = new StockAdjustmentItem();
            stockAdjustmentItem.setItemId(stockAdjustmentItemDTO.getItemId());
            stockAdjustmentItem.setUnitId(stockAdjustmentItemDTO.getUnitId());
            stockAdjustmentItem.setQty(stockAdjustmentItemDTO.getQty());
            stockAdjustment.addItem(stockAdjustmentItem);
            stockAdjustmentItemRepository.save(stockAdjustmentItem);

            if (!CollectionUtils.isEmpty(stockAdjustmentItemDTO.getVariationOptionIds())) {
                List<StockItemVariationOption> stockItemVariationOptions = stockAdjustmentItemDTO.getVariationOptionIds()
                        .stream()
                        .map(variationOptionId -> {
                            StockItemVariationOption stockItemVariationOption = new StockItemVariationOption();
                            stockItemVariationOption.setEntityId(stockAdjustmentItem.getId());
                            stockItemVariationOption.setEntityType(stockAdjustment.getEntityType());
                            stockItemVariationOption.setVariationOptionId(variationOptionId);
                            return stockItemVariationOption;
                        }).toList();
                stockItemVariationOptionRepository.saveAll(stockItemVariationOptions);
            }
            grandTotalQty = grandTotalQty.add(stockAdjustmentItemDTO.getQty());
        }
        stockAdjustment.setTotalQty(grandTotalQty);
        return stockAdjustment.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, StockAdjustmentDTO stockAdjustmentDTO) {
        stockAdjustmentDTO.setId(id);
        validationContext.validate(stockAdjustmentDTO);
        return stockAdjustmentRepository.findById(id)
                .map(stockAdjustment -> {

                    if (StockStatus.DRAFT != stockAdjustment.getStatus()) {
                        throw validationService.statusChangeExceptionThrow(StockStatus.DRAFT.getName()).get();
                    }

                    stockAdjustment.setBranchId(stockAdjustmentDTO.getBranchId());
                    stockAdjustment.setWarehouseId(stockAdjustmentDTO.getWarehouseId());
                    stockAdjustment.setDescription(stockAdjustmentDTO.getDescription());
                    stockAdjustment.setAdjustmentDate(stockAdjustmentDTO.getAdjustmentDate());

                    Map<Long, StockAdjustmentItem> stockAdjustmentItemsMap = stockAdjustment
                            .getItems()
                            .stream()
                            .collect(Collectors.toMap(StockAdjustmentItem::getId, Function.identity()));

                    BigDecimal grandTotalQty = BigDecimal.ZERO;

                    for (StockAdjustmentItemDTO stockAdjustmentItemDTO : stockAdjustmentDTO.getItems()) {
                        validationContext.validate(stockAdjustmentItemDTO);
                        StockAdjustmentItem stockAdjustmentItem;
                        if (stockAdjustmentItemDTO.getId() != null && stockAdjustmentItemsMap.get(stockAdjustmentItemDTO.getId()) != null) {
                            stockAdjustmentItem = stockAdjustmentItemsMap.get(stockAdjustmentItemDTO.getId());
                            stockAdjustmentItemsMap.remove(stockAdjustmentItem.getId());
                        } else {
                            stockAdjustmentItem = new StockAdjustmentItem();
                        }

                        stockAdjustmentItem.setItemId(stockAdjustmentItemDTO.getItemId());
                        stockAdjustmentItem.setUnitId(stockAdjustmentItemDTO.getUnitId());
                        stockAdjustmentItem.setQty(stockAdjustmentItemDTO.getQty());
                        stockAdjustment.addItem(stockAdjustmentItem);

                        if (stockAdjustmentItem.getId() == null) {
                            stockAdjustmentItemRepository.save(stockAdjustmentItem);
                        }
                        if (!CollectionUtils.isEmpty(stockAdjustmentItemDTO.getVariationOptionIds())) {
                            stockItemVariationOptionRepository.deleteAllByEntityIdAndEntityType(stockAdjustmentItem.getId(), stockAdjustment.getEntityType());
                            List<StockItemVariationOption> stockItemVariationOptions = stockAdjustmentItemDTO.getVariationOptionIds()
                                    .stream()
                                    .map(variationOptionId -> {
                                        StockItemVariationOption stockItemVariationOption = new StockItemVariationOption();
                                        stockItemVariationOption.setEntityId(stockAdjustmentItem.getId());
                                        stockItemVariationOption.setEntityType(stockAdjustment.getEntityType());
                                        stockItemVariationOption.setVariationOptionId(variationOptionId);
                                        return stockItemVariationOption;
                                    }).toList();
                            stockItemVariationOptionRepository.saveAll(stockItemVariationOptions);
                        }
                        grandTotalQty = grandTotalQty.add(stockAdjustmentItemDTO.getQty());
                    }
                    stockAdjustment.setTotalQty(grandTotalQty);
                    if (!CollectionUtils.isEmpty(stockAdjustmentItemsMap.values())) {
                        stockAdjustmentItemRepository.deleteAllInBatch(stockAdjustmentItemsMap.values());
                    }
                    return id;
                }).orElseThrow(() -> validationService.entityNotFoundExceptionThrow(STOCK_ADJUSTMENT).get());
    }

    @Override
    public StockAdjustmentDetailDTO get(Long id) {
        return stockAdjustmentRepository.findById(id)
                .map(stockAdjustment -> {
                    StockAdjustmentDetailDTO stockAdjustmentDetailDTO = new StockAdjustmentDetailDTO();
                    stockAdjustmentDetailDTO.setId(stockAdjustment.getId());
                    stockAdjustmentDetailDTO.setAdjustmentNumber(stockAdjustment.getAdjustmentNumber());
                    stockAdjustmentDetailDTO.setAdjustmentDate(stockAdjustment.getAdjustmentDate());
                    stockAdjustmentDetailDTO.setStatus(stockAdjustment.getStatus());
                    stockAdjustmentDetailDTO.setDescription(stockAdjustment.getDescription());
                    stockAdjustmentDetailDTO.setCompany(stockAdjustment.getCompany().toShortDTO());
                    stockAdjustmentDetailDTO.setBranch(stockAdjustment.getBranch().toCommonDTO());
                    stockAdjustmentDetailDTO.setWarehouse(stockAdjustment.getWarehouse().toCommonDTO());
                    stockAdjustmentDetailDTO.setTotalQty(stockAdjustment.getTotalQty());
                    stockAdjustment.getItems()
                            .forEach(stockAdjustmentItem -> {
                                StockAdjustmentItemDetailDTO stockAdjustmentItemDetailDTO = new StockAdjustmentItemDetailDTO();
                                stockAdjustmentItemDetailDTO.setId(stockAdjustmentItem.getId());
                                stockAdjustmentItemDetailDTO.setItem(getItem(stockAdjustmentItem.getItemId(), stockAdjustmentItem.getItemType()));
                                stockAdjustmentItemDetailDTO.setUnit(stockAdjustmentItem.getUnit().toCommonDTO());
                                stockAdjustmentItemDetailDTO.setQty(stockAdjustmentItem.getQty());

                                stockItemVariationOptionRepository.findAllByEntityIdAndEntityTypeAndDeletedIsFalse(stockAdjustment.getId(), stockAdjustment.getEntityType()).forEach(stockAdjustmentVariationOption -> {
                                    StockItemProductVariationDTO stockItemProductVariationDTO = new StockItemProductVariationDTO();
                                    stockItemProductVariationDTO.setId(stockAdjustmentVariationOption.getVariationOptionId());
                                    stockItemProductVariationDTO.setName(stockAdjustmentVariationOption.getVariationOption().getName());
                                    stockItemProductVariationDTO.setVariationType(stockAdjustmentVariationOption.getVariationOption().getVariationType().toCommonDTO());
                                    stockAdjustmentItemDetailDTO.addProductVariation(stockItemProductVariationDTO);
                                });

                                stockAdjustmentDetailDTO.addItem(stockAdjustmentItemDetailDTO);
                            });
                    return stockAdjustmentDetailDTO;
                }).orElseThrow(() -> validationService.entityNotFoundExceptionThrow(STOCK_ADJUSTMENT).get());
    }

    @Override
    public Page<StockAdjustmentListDTO> getList(StockFilter filter) {
        ResultList<StockAdjustment> resultList = stockAdjustmentRepository.getResultList(filter);
        List<StockAdjustmentListDTO> result = resultList.getList()
                .stream()
                .map(stockAdjustment -> {
                    StockAdjustmentListDTO stockAdjustmentListDTO = new StockAdjustmentListDTO();
                    stockAdjustmentListDTO.setId(stockAdjustment.getId());
                    stockAdjustmentListDTO.setAdjustmentNumber(stockAdjustment.getAdjustmentNumber());
                    stockAdjustmentListDTO.setAdjustmentDate(stockAdjustment.getAdjustmentDate());
                    stockAdjustmentListDTO.setCompany(stockAdjustment.getCompany().toShortDTO());
                    stockAdjustmentListDTO.setBranch(stockAdjustment.getBranch().toCommonDTO());
                    stockAdjustmentListDTO.setWarehouse(stockAdjustment.getWarehouse().toCommonDTO());
                    stockAdjustmentListDTO.setStatus(stockAdjustment.getStatus());
                    stockAdjustmentListDTO.setTotalQty(stockAdjustment.getTotalQty());
                    return stockAdjustmentListDTO;
                })
                .toList();
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, StockStatus status) {
        stockAdjustmentRepository
                .findById(id)
                .ifPresentOrElse(stockAdjustment -> {
                    if (StockStatus.COMPLETED == status) {
                        if (StockStatus.DRAFT != stockAdjustment.getStatus()) {
                            throw validationService.statusChangeExceptionThrow(StockStatus.DRAFT.getName()).get();
                        }
                        warehouseItemService.out(stockAdjustment);
                    } else if (StockStatus.CANCELLED == status) {
                        if (StockStatus.COMPLETED != stockAdjustment.getStatus()) {
                            throw validationService.statusChangeExceptionThrow(StockStatus.COMPLETED.getName()).get();
                        }
                        warehouseItemService.in(stockAdjustment);
                    }
                    stockAdjustment.setStatus(status);
                    stockAdjustmentRepository.save(stockAdjustment);
                }, () -> validationService.entityNotFoundExceptionThrow(STOCK_ADJUSTMENT).get());
    }

    @Override
    public List<CommonDTO> getItemsLookUp(StockFilter filter) {
        filter.setTransferType(StockTransferType.IN);
        return stockItemRepository.lookUp(filter);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        stockAdjustmentRepository
                .findById(id)
                .ifPresentOrElse(stockAdjustment -> {
                    if (StockStatus.DRAFT == stockAdjustment.getStatus() || StockStatus.CANCELLED == stockAdjustment.getStatus()) {
                        stockAdjustmentItemRepository.deleteAllByStockAdjustmentId(id);
                        stockAdjustment.setDeleted(true);
                        stockAdjustmentRepository.save(stockAdjustment);
                    } else {
                        throw validationService.statusChangeExceptionThrow("черновик или отменен").get();
                    }
                }, () -> validationService.entityNotFoundExceptionThrow(STOCK_ADJUSTMENT).get());
    }
}

