package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.constants.Constants;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.domain.stock.StockItemVariationOption;
import uz.uzkassa.cts.domain.stock.StockTransfer;
import uz.uzkassa.cts.domain.stock.StockTransferItem;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.stock.StockItemProductVariationDTO;
import uz.uzkassa.cts.dto.stocktransfer.*;
import uz.uzkassa.cts.enums.Prefix;
import uz.uzkassa.cts.enums.StockStatus;
import uz.uzkassa.cts.enums.StockTransferStatus;
import uz.uzkassa.cts.enums.StockTransferType;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.filter.StockTransferFilter;
import uz.uzkassa.cts.numbering.NumberingContext;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.StockInService;
import uz.uzkassa.cts.service.StockOutService;
import uz.uzkassa.cts.service.StockTransferService;
import uz.uzkassa.cts.service.WarehouseItemService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StockTransferServiceImpl extends StockBaseServiceImpl<StockTransfer, Long> implements StockTransferService {

    StockTransferItemRepository stockTransferItemRepository;
    StockInService stockInService;
    StockOutService stockOutService;

    public StockTransferServiceImpl(BaseRepository<StockTransfer, Long> repository, StockRepository stockRepository,
                                    StockItemRepository stockItemRepository, StockAdjustmentRepository stockAdjustmentRepository,
                                    StockTransferRepository stockTransferRepository, StockItemVariationOptionRepository stockItemVariationOptionRepository,
                                    ProductRepository productRepository, DeviceRepository deviceRepository, FiscalModuleRepository fiscalModuleRepository,
                                    AccessoryRepository accessoryRepository, WarehouseItemService warehouseItemService, NumberingContext numberingContext,
                                    ValidationContext validationContext, StockTransferItemRepository stockTransferItemRepository, StockInService stockInService,
                                    StockOutService stockOutService, ReferenceRepository referenceRepository) {
        super(repository, stockRepository, stockItemRepository, stockAdjustmentRepository, stockTransferRepository, stockItemVariationOptionRepository,
                productRepository, deviceRepository, fiscalModuleRepository, accessoryRepository, warehouseItemService, numberingContext, validationContext, referenceRepository);
        this.stockTransferItemRepository = stockTransferItemRepository;
        this.stockInService = stockInService;
        this.stockOutService = stockOutService;
    }


    @Override
    @Transactional
    public Long create(StockTransferDTO stockTransferDTO) {
        validationContext.validate(stockTransferDTO);
        StockTransfer stockTransfer = new StockTransfer();
        if (StringUtils.isNotEmpty(stockTransferDTO.getTransferNumber())) {
            stockTransfer.setTransferNumber(stockTransferDTO.getTransferNumber());
        } else {
            Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.STOCK_TRANSFER, stockTransferDTO.getCompanyId());
            stockTransfer.setIncrementNumber(Long.valueOf(numberingMap.get(Constants.INCREMENT_NUMBER)));
            stockTransfer.setTransferNumber(numberingMap.get(Constants.NUMBER));
        }
        stockTransfer.setTransferDate(stockTransferDTO.getTransferDate());
        stockTransfer.setFromBranchId(stockTransferDTO.getFromBranchId());
        stockTransfer.setToBranchId(stockTransferDTO.getToBranchId());
        stockTransfer.setFromWarehouseId(stockTransferDTO.getFromWarehouseId());
        stockTransfer.setToWarehouseId(stockTransferDTO.getToWarehouseId());
        stockTransfer.setDescription(stockTransferDTO.getDescription());
        stockTransfer.setCompanyId(stockTransferDTO.getCompanyId());
        stockTransferRepository.save(stockTransfer);

        BigDecimal grandTotalQty = BigDecimal.ZERO;

        for (StockTransferItemDTO stockTransferItemDTO : stockTransferDTO.getItems()) {
            validationContext.validate(stockTransferItemDTO);

            StockTransferItem stockTransferItem = new StockTransferItem();
            stockTransferItem.setFromItemId(stockTransferItemDTO.getItemId());
            stockTransferItem.setToItemId(stockTransferItemDTO.getItemId());
            stockTransferItem.setItemType(stockTransferItemDTO.getItemType());
            stockTransferItem.setUnitId(stockTransferItemDTO.getUnitId());
            stockTransferItem.setQty(stockTransferItemDTO.getQty());
            stockTransfer.addItem(stockTransferItem);
            stockTransferItemRepository.save(stockTransferItem);

            if (!CollectionUtils.isEmpty(stockTransferItemDTO.getVariationOptionIds())) {
                List<StockItemVariationOption> stockItemVariationOptions = stockTransferItemDTO.getVariationOptionIds()
                        .stream()
                        .map(variationOptionId -> {
                            StockItemVariationOption stockItemVariationOption = new StockItemVariationOption();
                            stockItemVariationOption.setEntityId(stockTransferItem.getId());
                            stockItemVariationOption.setEntityType(stockTransfer.getEntityType());
                            stockItemVariationOption.setVariationOptionId(variationOptionId);
                            return stockItemVariationOption;
                        }).toList();
                stockItemVariationOptionRepository.saveAll(stockItemVariationOptions);
            }
            grandTotalQty = grandTotalQty.add(stockTransferItemDTO.getQty());
        }
        stockTransfer.setTotalQty(grandTotalQty);
        stockTransferRepository.save(stockTransfer);
        return stockTransfer.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, StockTransferDTO stockTransferDTO) {
        stockTransferDTO.setId(id);
        validationContext.validate(stockTransferDTO);
        return stockTransferRepository
                .findById(id)
                .map(stockTransfer -> {

                    if (StockTransferStatus.DRAFT != stockTransfer.getStatus()) {
                        throw validationService.statusChangeExceptionThrow(StockTransferStatus.DRAFT.getName()).get();
                    }
                    stockTransfer.setTransferDate(stockTransferDTO.getTransferDate());
                    stockTransfer.setFromBranchId(stockTransferDTO.getFromBranchId());
                    stockTransfer.setToBranchId(stockTransferDTO.getToBranchId());
                    stockTransfer.setFromWarehouseId(stockTransferDTO.getFromWarehouseId());
                    stockTransfer.setToWarehouseId(stockTransferDTO.getToWarehouseId());
                    stockTransfer.setDescription(stockTransferDTO.getDescription());

                    BigDecimal grandTotalQty = BigDecimal.ZERO;

                    HashMap<Long, StockTransferItem> stockTransferItemsMap = new HashMap<>();
                    stockTransfer
                            .getItems()
                            .forEach(stockTransferItem -> stockTransferItemsMap.put(stockTransferItem.getId(), stockTransferItem));


                    for (StockTransferItemDTO stockTransferItemDTO : stockTransferDTO.getItems()) {
                        validationContext.validate(stockTransferItemDTO);

                        StockTransferItem stockTransferItem;
                        if (stockTransferItemDTO.getStockTransferItemId() != null && stockTransferItemsMap.get(stockTransferItemDTO.getStockTransferItemId()) != null) {
                            stockTransferItem = stockTransferItemsMap.get(stockTransferItemDTO.getStockTransferItemId());
                            stockTransferItemsMap.remove(stockTransferItemDTO.getStockTransferItemId());
                        } else {
                            stockTransferItem = new StockTransferItem();
                        }
                        stockTransferItem.setFromItemId(stockTransferItemDTO.getItemId());
                        stockTransferItem.setToItemId(stockTransferItemDTO.getItemId());
                        stockTransferItem.setUnitId(stockTransferItemDTO.getUnitId());
                        stockTransferItem.setQty(stockTransferItemDTO.getQty());
                        stockTransferItem.setStockTransferId(stockTransfer.getId());
                        stockTransfer.addItem(stockTransferItem);
                        if (stockTransferItem.getId() == null) {
                            stockTransferItemRepository.save(stockTransferItem);
                        }
                        if (!CollectionUtils.isEmpty(stockTransferItemDTO.getVariationOptionIds())) {
                            stockItemVariationOptionRepository.deleteAllByEntityIdAndEntityType(stockTransferItem.getId(), stockTransfer.getEntityType());
                            List<StockItemVariationOption> stockItemVariationOptions = stockTransferItemDTO.getVariationOptionIds()
                                    .stream()
                                    .map(variationOptionId -> {
                                        StockItemVariationOption stockItemVariationOption = new StockItemVariationOption();
                                        stockItemVariationOption.setEntityId(stockTransferItem.getId());
                                        stockItemVariationOption.setEntityType(stockTransfer.getEntityType());
                                        stockItemVariationOption.setVariationOptionId(variationOptionId);
                                        return stockItemVariationOption;
                                    }).toList();
                            stockItemVariationOptionRepository.saveAll(stockItemVariationOptions);
                        }
                        grandTotalQty = grandTotalQty.add(stockTransferItemDTO.getQty());
                    }
                    stockTransfer.setTotalQty(grandTotalQty);
                    stockTransferRepository.save(stockTransfer);

                    if (!stockTransferItemsMap.isEmpty()) {
                        stockTransferItemRepository.deleteAllByIdInBatch(stockTransferItemsMap.keySet());
                    }
                    return stockTransfer.getId();
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(STOCK_TRANSFER));
    }

    @Override
    public Page<StockTransferListDTO> getList(StockTransferFilter filter) {
        ResultList<StockTransfer> resultList = stockTransferRepository.getResultList(filter);
        List<StockTransferListDTO> result = resultList
                .getList()
                .stream()
                .map(stockTransfer -> {
                    StockTransferListDTO stockTransferListDTO = new StockTransferListDTO();
                    stockTransferListDTO.setId(stockTransfer.getId());
                    stockTransferListDTO.setTransferDate(stockTransfer.getTransferDate());
                    stockTransferListDTO.setTransferNumber(stockTransfer.getTransferNumber());
                    stockTransferListDTO.setStatus(stockTransfer.getStatus());
                    stockTransferListDTO.setCompany(stockTransfer.getCompany().toShortDTO());
                    stockTransferListDTO.setFromBranch(stockTransfer.getFromBranch().toCommonDTO());
                    stockTransferListDTO.setToBranch(stockTransfer.getToBranch().toCommonDTO());
                    stockTransferListDTO.setFromWarehouse(stockTransfer.getFromWarehouse().toCommonDTO());
                    stockTransferListDTO.setToWarehouse(stockTransfer.getToWarehouse().toCommonDTO());
                    stockTransferListDTO.setTotalQty(stockTransfer.getTotalQty());
                    return stockTransferListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public StockTransferDetailDTO get(Long id) {
        return stockTransferRepository.findById(id).map(stockTransfer -> {
            StockTransferDetailDTO stockTransferDetailDTO = new StockTransferDetailDTO();
            stockTransferDetailDTO.setId(stockTransfer.getId());
            stockTransferDetailDTO.setTransferNumber(stockTransfer.getTransferNumber());
            stockTransferDetailDTO.setTransferDate(stockTransfer.getTransferDate());
            stockTransferDetailDTO.setStatus(stockTransfer.getStatus());
            stockTransferDetailDTO.setDescription(stockTransfer.getDescription());
            stockTransferDetailDTO.setFromBranch(stockTransfer.getFromBranch().toCommonDTO());
            stockTransferDetailDTO.setToBranch(stockTransfer.getToBranch().toCommonDTO());
            stockTransferDetailDTO.setFromWarehouse(stockTransfer.getFromWarehouse().toCommonDTO());
            stockTransferDetailDTO.setToWarehouse(stockTransfer.getToWarehouse().toCommonDTO());
            stockTransferDetailDTO.setTotalQty(stockTransfer.getTotalQty());
            stockTransfer.getItems().forEach(stockTransferItem -> {
                StockTransferItemDetailDTO stockTransferItemDetailDTO = new StockTransferItemDetailDTO();
                stockTransferItemDetailDTO.setId(stockTransferItem.getId());
                stockTransferItemDetailDTO.setItem(getItem(stockTransferItem.getFromItemId(), stockTransferItem.getItemType()));
                stockTransferItemDetailDTO.setUnit(stockTransferItem.getUnit().toCommonDTO());
                stockTransferItemDetailDTO.setQty(stockTransferItem.getQty());
                stockItemVariationOptionRepository.findAllByEntityIdAndEntityTypeAndDeletedIsFalse(stockTransferItem.getId(), stockTransfer.getEntityType()).forEach(stockItemVariationOption -> {
                    StockItemProductVariationDTO stockItemProductVariationDTO = new StockItemProductVariationDTO();
                    stockItemProductVariationDTO.setId(stockItemVariationOption.getVariationOption().getId());
                    stockItemProductVariationDTO.setName(stockItemVariationOption.getVariationOption().getName());
                    stockItemProductVariationDTO.setVariationType(stockItemVariationOption.getVariationOption().getVariationType().toCommonDTO());
                    stockTransferItemDetailDTO.addProductVariation(stockItemProductVariationDTO);
                });
                stockTransferDetailDTO.addItem(stockTransferItemDetailDTO);
            });
            return stockTransferDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(STOCK_TRANSFER));
    }

    @Override
    public List<CommonDTO> lookUp(StockTransferFilter filter) {
        return stockTransferRepository.getResultList(filter).getList().stream().map(SimpleEntity::toCommonDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, StockTransferStatus status) {
        stockTransferRepository
                .findById(id)
                .ifPresentOrElse(stockTransfer -> {
                    if (StockTransferStatus.APPROVED == status) {
                        if (StockTransferStatus.IN_TRANSIT != stockTransfer.getStatus()) {
                            throw validationService.statusChangeExceptionThrow(StockTransferStatus.IN_TRANSIT.getName()).get();
                        }
                        warehouseItemService.out(stockTransfer);
                    } else if (StockTransferStatus.REJECTED == status) {
                        if (StockTransferStatus.APPROVED != stockTransfer.getStatus()) {
                            throw validationService.statusChangeExceptionThrow(StockTransferStatus.APPROVED.getName()).get();
                        }
                        warehouseItemService.in(stockTransfer);
                    }
                    stockTransfer.setStatus(status);
                    stockTransferRepository.save(stockTransfer);
                    if (StockTransferStatus.APPROVED == status) {
                        stockInService.create(stockTransfer);
                        stockOutService.create(stockTransfer);
                    } else if (StockTransferStatus.REJECTED == status) {
                        stockInService.create(stockTransfer);
                        stockOutService.create(stockTransfer);
                    }
                }, () -> validationService.entityNotFoundExceptionThrow(STOCK_TRANSFER).get());
    }

    @Override
    public List<CommonDTO> getItemsLookUp(StockFilter filter) {
        filter.setTransferType(StockTransferType.IN);
        return stockItemRepository.lookUp(filter);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        stockTransferRepository.findById(id)
                .ifPresentOrElse(stockTransfer -> {
                    if (StockTransferStatus.DRAFT == stockTransfer.getStatus() || StockTransferStatus.REJECTED == stockTransfer.getStatus()) {
                        stockTransfer.setDeleted(true);
                        stockTransferRepository.save(stockTransfer);
                    } else {
                        throw validationService.statusChangeExceptionThrow(StockStatus.DRAFT.getName() + "," + StockTransferStatus.REJECTED.getName()).get();
                    }
                }, () -> validationService.entityNotFoundExceptionThrow(STOCK_TRANSFER).get());
    }
}
