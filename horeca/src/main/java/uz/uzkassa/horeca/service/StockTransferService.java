package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.Category;
import uz.uzkassa.horeca.domain.product.Product;
import uz.uzkassa.horeca.domain.stocktransfer.StockTransfer;
import uz.uzkassa.horeca.domain.stocktransfer.StockTransferItem;
import uz.uzkassa.horeca.domain.warehouse.WarehouseItem;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.stocktransfer.*;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.enums.Prefix;
import uz.uzkassa.horeca.enums.StockTransferStatus;
import uz.uzkassa.horeca.enums.StockTransferType;
import uz.uzkassa.horeca.filters.StockTransferFilter;
import uz.uzkassa.horeca.interfaces.CrudAdvanced;
import uz.uzkassa.horeca.interfaces.Numbering;
import uz.uzkassa.horeca.repository.*;
import uz.uzkassa.horeca.utils.DateUtils;
import uz.uzkassa.horeca.utils.ProductUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class StockTransferService
        extends BaseService
        implements
        CrudAdvanced<Long, StockTransferDTO, StockTransferDetailDTO, StockTransferListDTO, StockTransferFilter, StockTransferUpdateStatusDTO, CommonDTO>,
        Numbering {

    StockTransferRepository stockTransferRepository;
    StockTransferItemRepository stockTransferItemRepository;
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    WarehouseItemRepository warehouseItemRepository;

    @Override
    public HashMap<String, String> getNumberingMap(Long branchId) {
        Long incrementNumber = stockTransferRepository.getMaxIncrementNumber(branchId) + 1L;
        String stockNumber = Prefix.STOCK_TRANSFER
                .getName()
                .concat(DateUtils.formatYearLastTwoDigit(LocalDate.now()))
                .concat("-")
                .concat(COMMON_NUMBERING_FORMAT.format(incrementNumber));

        HashMap<String, String> numberMap = new HashMap<>();
        numberMap.put(INCREMENT_NUMBER, String.valueOf(incrementNumber));
        numberMap.put(NUMBER, stockNumber);
        return numberMap;
    }

    @Override
    public void validate(StockTransferDTO stockTransferDTO) {
        if (stockTransferDTO.getFromBranchId() == null) {
            throw badRequestExceptionThrow("From branch is required").get();
        }
        if (stockTransferDTO.getToBranchId() == null) {
            throw badRequestExceptionThrow("To branch is required").get();
        }
        if (stockTransferDTO.getStockTransferType() == null) {
            throw badRequestExceptionThrow("Stock transfer type is required").get();
        }
        if (stockTransferDTO.getTransferDate() == null) {
            throw badRequestExceptionThrow("Transfer date is required").get();
        }
        if (CollectionUtils.isEmpty(stockTransferDTO.getItems())) {
            throw badRequestExceptionThrow("Items are required").get();
        }
    }

    private void validateItems(StockTransferItemDTO stockTransferItemDTO, StockTransferDTO stockTransferDTO) {
        if (stockTransferDTO.getFromWarehouseId() == null && stockTransferItemDTO.getFromWarehouseId() == null) {
            throw badRequestExceptionThrow("From warehouse is required").get();
        }
        if (stockTransferDTO.getToWarehouseId() == null && stockTransferItemDTO.getToWarehouseId() == null) {
            throw badRequestExceptionThrow("To warehouse is required").get();
        }
        if (stockTransferItemDTO.getQty() == null || stockTransferItemDTO.getQty().compareTo(BigDecimal.ZERO) == 0) {
            throw badRequestExceptionThrow("Qty is required").get();
        }
        if (stockTransferItemDTO.getUnitId() == null) {
            throw badRequestExceptionThrow("Unit is required").get();
        }
        if (stockTransferItemDTO.getProductId() == null) {
            throw badRequestExceptionThrow("Product is required").get();
        }
        if (stockTransferItemDTO.getCreatedDate() == null) {
            throw badRequestExceptionThrow("CreatedDate is required").get();
        }
    }

    @Transactional
    public Long create(StockTransferDTO stockTransferDTO) {
        this.validate(stockTransferDTO);
        HashMap<String, String> numberingMap = this.getNumberingMap(stockTransferDTO.getFromBranchId());

        StockTransfer stockTransfer = new StockTransfer();
        stockTransfer.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        stockTransfer.setTransferNumber(numberingMap.get(NUMBER));
        stockTransfer.setTransferDate(stockTransferDTO.getTransferDate());
        stockTransfer.setTransferType(stockTransferDTO.getStockTransferType());
        stockTransfer.setStatus(StockTransferStatus.DRAFT);
        stockTransfer.setFromBranchId(stockTransferDTO.getFromBranchId());
        stockTransfer.setToBranchId(stockTransferDTO.getToBranchId());
        stockTransfer.setFromWarehouseId(stockTransferDTO.getFromWarehouseId());
        stockTransfer.setToWarehouseId(stockTransferDTO.getToWarehouseId());
        stockTransfer.setDescription(stockTransferDTO.getDescription());
        stockTransferRepository.save(stockTransfer);

        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;
        final boolean isTransferInBranch = StockTransferType.BETWEEN_BRANCHES.equals(stockTransferDTO.getStockTransferType());
        for (StockTransferItemDTO stockTransferItemDTO : stockTransferDTO.getItems()) {
            this.validateItems(stockTransferItemDTO, stockTransferDTO);

            final Product fromProduct = productRepository
                    .findById(stockTransferItemDTO.getProductId())
                    .orElseThrow(notFoundExceptionSupplier(Product.class, "id", stockTransferItemDTO.getId()));

            Product toProduct = null;
            if (isTransferInBranch) {
                if (StringUtils.isNotEmpty(fromProduct.getBarcode())) {
                    toProduct =
                            productRepository
                                    .findFirstByBranchIdAndBarcodeAndDeletedIsFalse(stockTransferDTO.getToBranchId(), fromProduct.getBarcode())
                                    .orElse(null);
                }

                if (toProduct == null && StringUtils.isNotEmpty(fromProduct.getCatalogCode())) {
                    toProduct =
                            productRepository
                                    .findFirstByBranchIdAndCatalogCodeAndDeletedIsFalse(stockTransferDTO.getToBranchId(), fromProduct.getCatalogCode())
                                    .orElse(null);
                }
                if (toProduct != null && (toProduct.getPrice() == null)) {
                    throw badRequestExceptionThrow("Необходимо установить цену на продукт :" + toProduct.getName()).get();
                }

                if (toProduct == null) {
                    Category toCategory = null;
                    if (StringUtils.isNotEmpty(fromProduct.getCatalogCode())) {
                        toCategory =
                                categoryRepository
                                        .findFirstByBranchIdAndCatalogCodeAndDeletedIsFalse(
                                                stockTransferDTO.getToBranchId(),
                                                ProductUtils.clearCatalogCode(fromProduct.getCatalogCode())
                                        )
                                        .orElse(null);
                    }
                    if (toCategory == null) {
                        toCategory =
                                categoryRepository
                                        .findFirstByBranchIdAndCategoryTypeAndDeletedIsFalse(stockTransferDTO.getToBranchId(), fromProduct.getCategory().getCategoryType())
                                        .orElseThrow(
                                                notFoundExceptionThrow(
                                                        Category.class.getSimpleName(),
                                                        fromProduct.getCategory().getCategoryType().getName(),
                                                        fromProduct.getCategory().getCategoryType().getName()
                                                )
                                        );
                    }

                    toProduct = new Product();
                    toProduct.setTasnifId(fromProduct.getTasnifId());
                    toProduct.setName(fromProduct.getName());
                    toProduct.setBrand(fromProduct.getBrand());
                    toProduct.setBarcode(fromProduct.getBarcode());
                    toProduct.setCatalogCode(fromProduct.getCatalogCode());
                    toProduct.setCatalogName(fromProduct.getCatalogName());
                    toProduct.setPackageName(fromProduct.getPackageName());
                    toProduct.setPackageCode(fromProduct.getPackageCode());
                    toProduct.setDescription(fromProduct.getDescription());
                    toProduct.setStatus(CommonStatus.ACTIVE);
                    toProduct.setUnitId(fromProduct.getUnitId());
                    toProduct.setVat(fromProduct.getVat());
                    toProduct.setPrice(fromProduct.getPrice());
                    toProduct.setWeight(fromProduct.getWeight());
                    toProduct.setCategoryId(toCategory.getId());
                    toProduct.setBranchId(stockTransferDTO.getToBranchId());
                    productRepository.save(toProduct);
                }
            }

            StockTransferItem stockTransferItem = new StockTransferItem();
            stockTransferItem.setFromProductId(fromProduct.getId());
            stockTransferItem.setToProductId(isTransferInBranch ? toProduct.getId() : fromProduct.getId());
            stockTransferItem.setUnitId(stockTransferItemDTO.getUnitId());
            stockTransferItem.setQty(stockTransferItemDTO.getQty());
            stockTransferItem.setCreatedDate(stockTransferItemDTO.getCreatedDate());
            stockTransferItem.setInKg(stockTransferItemDTO.getInKg());
            stockTransferItem.setPrice(stockTransferItemDTO.getPrice());
            stockTransferItem.setFromWarehouseId(stockTransferItemDTO.getFromWarehouseId());
            stockTransferItem.setToWarehouseId(stockTransferItemDTO.getToWarehouseId());
            stockTransfer.addItem(stockTransferItem);
            totalQty = totalQty.add(stockTransferItemDTO.getQty());
            totalPrice = totalPrice.add(stockTransferItemDTO.getPrice());
        }
        stockTransfer.setTotalQty(totalQty);
        stockTransfer.setTotalPrice(totalPrice);
        return stockTransfer.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, StockTransferDTO stockTransferDTO) {
        return stockTransferRepository
                .findById(id)
                .map(stockTransfer -> {
                    if (!StockTransferStatus.DRAFT.equals(stockTransfer.getStatus())) {
                        throw badRequestExceptionThrow("Вы можете изменить только черновик").get();
                    }
                    stockTransferDTO.setId(id);
                    this.validate(stockTransferDTO);
                    stockTransfer.setTransferDate(stockTransferDTO.getTransferDate());
                    stockTransfer.setTransferType(stockTransferDTO.getStockTransferType());
                    stockTransfer.setFromBranchId(stockTransferDTO.getFromBranchId());
                    stockTransfer.setToBranchId(stockTransferDTO.getToBranchId());
                    stockTransfer.setFromWarehouseId(stockTransferDTO.getFromWarehouseId());
                    stockTransfer.setToWarehouseId(stockTransferDTO.getToWarehouseId());
                    stockTransfer.setDescription(stockTransferDTO.getDescription());

                    Map<Long, StockTransferItem> stockTransferItemsMap = stockTransfer
                            .getItems()
                            .stream()
                            .collect(Collectors.toMap(StockTransferItem::getId, Function.identity()));

                    BigDecimal totalQty = BigDecimal.ZERO;
                    BigDecimal totalPrice = BigDecimal.ZERO;

                    for (StockTransferItemDTO stockTransferItemDTO : stockTransferDTO.getItems()) {
                        this.validateItems(stockTransferItemDTO, stockTransferDTO);
                        StockTransferItem stockTransferItem;
                        if (stockTransferItemDTO.getId() != null) {
                            stockTransferItem = stockTransferItemsMap.get(stockTransferItemDTO.getId());
                            stockTransferItemsMap.remove(stockTransferItemDTO.getId());
                        } else {
                            stockTransferItem = new StockTransferItem();
                        }

                        Product fromProduct = productRepository
                                .findById(stockTransferItemDTO.getProductId())
                                .orElseThrow(notFoundExceptionThrow(Product.class.getSimpleName(), "id", stockTransferItemDTO.getId()));

                        Product toProduct = null;
                        if (StringUtils.isNotEmpty(fromProduct.getBarcode())) {
                            toProduct =
                                    productRepository
                                            .findFirstByBranchIdAndBarcodeAndDeletedIsFalse(stockTransferDTO.getToBranchId(), fromProduct.getBarcode())
                                            .orElse(null);
                        }

                        if (toProduct == null && StringUtils.isNotEmpty(fromProduct.getCatalogCode())) {
                            toProduct =
                                    productRepository
                                            .findFirstByBranchIdAndCatalogCodeAndDeletedIsFalse(
                                                    stockTransferDTO.getToBranchId(),
                                                    fromProduct.getCatalogCode()
                                            )
                                            .orElse(null);
                        }
                        if (toProduct != null && (toProduct.getPrice() == null)) {
                            throw badRequestExceptionThrow("Необходимо установить цену на продукт :" + toProduct.getName()).get();
                        }

                        if (toProduct == null) {
                            Category toCategory = null;
                            if (StringUtils.isNotEmpty(fromProduct.getCatalogCode())) {
                                toCategory =
                                        categoryRepository
                                                .findFirstByBranchIdAndCatalogCodeAndDeletedIsFalse(
                                                        stockTransferDTO.getToBranchId(),
                                                        ProductUtils.clearCatalogCode(fromProduct.getCatalogCode())
                                                )
                                                .orElse(null);
                            }
                            if (toCategory == null) {
                                toCategory =
                                        categoryRepository
                                                .findFirstByBranchIdAndCategoryTypeAndDeletedIsFalse(
                                                        stockTransfer.getFromBranchId(),
                                                        fromProduct.getCategory().getCategoryType()
                                                )
                                                .orElseThrow(
                                                        notFoundExceptionThrow(
                                                                Category.class.getSimpleName(),
                                                                fromProduct.getCategory().getCategoryType().getName(),
                                                                fromProduct.getCategory().getCategoryType().getName()
                                                        )
                                                );
                            }

                            toProduct = new Product();
                            toProduct.setTasnifId(fromProduct.getTasnifId());
                            toProduct.setName(fromProduct.getName());
                            toProduct.setBrand(fromProduct.getBrand());
                            toProduct.setBarcode(fromProduct.getBarcode());
                            toProduct.setCatalogCode(fromProduct.getCatalogCode());
                            toProduct.setCatalogName(fromProduct.getCatalogName());
                            toProduct.setPackageName(fromProduct.getPackageName());
                            toProduct.setPackageCode(fromProduct.getPackageCode());
                            toProduct.setDescription(fromProduct.getDescription());
                            toProduct.setStatus(CommonStatus.ACTIVE);
                            toProduct.setUnitId(fromProduct.getUnitId());
                            toProduct.setVat(fromProduct.getVat());
                            toProduct.setPrice(fromProduct.getPrice());
                            toProduct.setWeight(fromProduct.getWeight());
                            toProduct.setCategoryId(toCategory.getId());
                            toProduct.setBranchId(stockTransferDTO.getToBranchId());
                            productRepository.save(toProduct);
                        }

                        stockTransferItem.setFromProductId(fromProduct.getId());
                        stockTransferItem.setToProductId(toProduct.getId());
                        stockTransferItem.setUnitId(stockTransferItemDTO.getUnitId());
                        stockTransferItem.setQty(stockTransferItemDTO.getQty());
                        stockTransferItem.setCreatedDate(stockTransferItemDTO.getCreatedDate());
                        stockTransferItem.setInKg(stockTransferItemDTO.getInKg());
                        stockTransferItem.setPrice(stockTransferItemDTO.getPrice());
                        stockTransferItem.setFromWarehouseId(stockTransferItemDTO.getFromWarehouseId());
                        stockTransferItem.setToWarehouseId(stockTransferItemDTO.getToWarehouseId());
                        stockTransfer.addItem(stockTransferItem);
                        totalQty = totalQty.add(stockTransferItemDTO.getQty());
                        totalPrice = totalPrice.add(toProduct.getPrice());
                    }

                    stockTransfer.setTotalQty(totalQty);
                    stockTransfer.setTotalPrice(totalPrice);

                    if (!CollectionUtils.isEmpty(stockTransferItemsMap.values())) {
                        stockTransferItemRepository.deleteAllInBatch(stockTransferItemsMap.values());
                    }
                    stockTransferRepository.save(stockTransfer);
                    return stockTransfer.getId();
                })
                .orElseThrow(notFoundExceptionThrow(StockTransfer.class.getSimpleName(), "id", id));
    }

    @Override
    public StockTransferDetailDTO get(Long id) {
        return stockTransferRepository
                .findById(id)
                .map(stockTransfer -> {
                    StockTransferDetailDTO stockTransferDetailDTO = new StockTransferDetailDTO();
                    stockTransferDetailDTO.setId(stockTransfer.getId());
                    stockTransferDetailDTO.setTransferNumber(stockTransfer.getTransferNumber());
                    stockTransferDetailDTO.setTransferDate(stockTransfer.getTransferDate());
                    stockTransferDetailDTO.setTransferType(stockTransfer.getTransferType());
                    stockTransferDetailDTO.setStatus(stockTransfer.getStatus());
                    stockTransferDetailDTO.setDescription(stockTransfer.getDescription());
                    stockTransferDetailDTO.setFromBranch(stockTransfer.getFromBranch().toCommonDTO());
                    stockTransferDetailDTO.setToBranch(stockTransfer.getToBranch().toCommonDTO());
                    stockTransferDetailDTO.setRejectReason(stockTransfer.getRejectReason());
                    if (stockTransfer.getFromWarehouseId() != null) {
                        stockTransferDetailDTO.setFromWarehouse(stockTransfer.getFromWarehouse().toCommonDTO());
                    }
                    if (stockTransfer.getToWarehouseId() != null) {
                        stockTransferDetailDTO.setToWarehouse(stockTransfer.getToWarehouse().toCommonDTO());
                    }
                    stockTransferDetailDTO.setTotalPrice(stockTransfer.getTotalPrice());
                    stockTransferDetailDTO.setTotalQty(stockTransfer.getTotalQty());
                    stockTransfer
                            .getItems()
                            .forEach(stockTransferItem -> {
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
                                stockTransferDetailDTO.addItem(stockTransferItemDetailDTO);
                            });
                    return stockTransferDetailDTO;
                })
                .orElseThrow(notFoundExceptionThrow(StockTransfer.class.getSimpleName(), "id", id));
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
                    stockTransferListDTO.setTransferType(stockTransfer.getTransferType());
                    stockTransferListDTO.setStatus(stockTransfer.getStatus());
                    stockTransferListDTO.setDescription(stockTransfer.getDescription());
                    stockTransferListDTO.setFromBranch(stockTransfer.getFromBranch().toCommonDTO());
                    stockTransferListDTO.setToBranch(stockTransfer.getToBranch().toCommonDTO());
                    if (stockTransfer.getFromWarehouseId() != null) {
                        stockTransferListDTO.setFromWarehouse(stockTransfer.getFromWarehouse().toCommonDTO());
                    }
                    if (stockTransfer.getToWarehouseId() != null) {
                        stockTransferListDTO.setToWarehouse(stockTransfer.getToWarehouse().toCommonDTO());
                    }
                    stockTransferListDTO.setTotalPrice(stockTransfer.getTotalPrice());
                    stockTransferListDTO.setTotalQty(stockTransfer.getTotalQty());
                    stockTransferListDTO.setCreatedBy(stockTransfer.getCreator().toCommonDTO());
                    stockTransferListDTO.setComment(stockTransfer.getDescription());
                    return stockTransferListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        stockTransferRepository.findById(id)
                .map(stockTransfer -> {
                    if (StockTransferStatus.DRAFT.equals(stockTransfer.getStatus()) || StockTransferStatus.REJECTED.equals(stockTransfer.getStatus())) {
                        stockTransfer.setDeleted(true);
                        stockTransferRepository.save(stockTransfer);
                        return stockTransfer;
                    } else {
                        throw badRequestExceptionThrow("Вы можете удалить только черновик или отменен").get();
                    }
                }).orElseThrow(notFoundExceptionThrow(StockTransfer.class.getSimpleName(), "id", id));
    }

    @Override
    public List<CommonDTO> lookUp(StockTransferFilter filter) {
        return stockTransferRepository
                .getResultList(filter)
                .getList()
                .stream()
                .map(StockTransfer::toCommonDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, StockTransferUpdateStatusDTO updateStatusDTO) {
        StockTransfer stockTransfer = stockTransferRepository
                .findById(id)
                .orElseThrow(notFoundExceptionThrow(StockTransfer.class.getSimpleName(), "id", id));
        final StockTransferStatus status = updateStatusDTO.getStatus();
        if (StockTransferStatus.APPROVED.equals(status)) {
            if (!StockTransferStatus.IN_TRANSIT.equals(stockTransfer.getStatus())) {
                throw badRequestExceptionThrow("Вы можете одобрить передачу со статусом транзить").get();
            }
            for (StockTransferItem stockTransferItem : stockTransfer.getItems()) {
                final Long fromWarehouseId = Optional.ofNullable(stockTransfer.getFromWarehouseId()).orElse(stockTransferItem.getFromWarehouseId());
                final Long toWarehouseId = Optional.ofNullable(stockTransfer.getFromWarehouseId()).orElse(stockTransferItem.getFromWarehouseId());

                WarehouseItem fromWarehouseItem = warehouseItemRepository
                        .findByWarehouseIdAndProductIdAndUnitIdAndCreatedDateAndPriceAndQtyGreaterThanEqual(
                                fromWarehouseId,
                                stockTransferItem.getFromProductId(),
                                stockTransferItem.getUnitId(),
                                stockTransferItem.getCreatedDate(),
                                stockTransferItem.getPrice(),
                                stockTransferItem.getQty()
                        )
                        .orElseThrow(badRequestExceptionThrow(
                                localize("validate.stock.transfer", stockTransferItem.getFromProduct().getName())
                        ));

                fromWarehouseItem.subtractQty(stockTransferItem.getQty());
                fromWarehouseItem.subtractInKg(stockTransferItem.getInKg());
                warehouseItemRepository.save(fromWarehouseItem);

                WarehouseItem toWarehouseItem = warehouseItemRepository
                        .findByWarehouseIdAndProductIdAndUnitIdAndCreatedDateAndPrice(
                                toWarehouseId,
                                stockTransferItem.getToProductId(),
                                stockTransferItem.getUnitId(),
                                stockTransferItem.getCreatedDate(),
                                stockTransferItem.getPrice()
                        ).orElseGet(() -> {
                            WarehouseItem warehouseItem = new WarehouseItem();
                            warehouseItem.setWarehouseId(toWarehouseId);
                            warehouseItem.setProductId(stockTransferItem.getToProductId());
                            warehouseItem.setUnitId(stockTransferItem.getUnitId());
                            warehouseItem.setPrice(stockTransferItem.getPrice());
                            warehouseItem.setQty(BigDecimal.ZERO);
                            warehouseItem.setInKg(BigDecimal.ZERO);
                            return warehouseItem;
                        });

                toWarehouseItem.addQty(stockTransferItem.getQty());
                toWarehouseItem.addInKg(Optional.ofNullable(stockTransferItem.getInKg()).orElse(BigDecimal.ZERO));
                warehouseItemRepository.save(toWarehouseItem);
            }
        } else if (StockTransferStatus.REJECTED.equals(status)) {
            if (!EnumSet.of(StockTransferStatus.APPROVED, StockTransferStatus.IN_TRANSIT).contains(stockTransfer.getStatus())) {
                throw badRequestExceptionThrow("Вы можете отклонять передачу со статусом одобрено или отправлено").get();
            }

            if (StockTransferStatus.APPROVED.equals(stockTransfer.getStatus())) {
                for (StockTransferItem stockTransferItem : stockTransfer.getItems()) {
                    WarehouseItem fromWarehouseItem = warehouseItemRepository
                            .findByWarehouseIdAndProductIdAndUnitId(
                                    Optional.ofNullable(stockTransfer.getFromWarehouseId()).orElse(stockTransferItem.getFromWarehouseId()),
                                    stockTransferItem.getFromProductId(),
                                    stockTransferItem.getUnitId()
                            )
                            .orElse(new WarehouseItem());

                    if (fromWarehouseItem.getId() == null) {
                        fromWarehouseItem.setProductId(stockTransferItem.getFromProductId());
                        fromWarehouseItem.setWarehouseId(Optional.ofNullable(stockTransfer.getFromWarehouseId()).orElse(stockTransferItem.getFromWarehouseId()));
                        fromWarehouseItem.setUnitId(stockTransferItem.getUnitId());
                    }
                    fromWarehouseItem.addQty(stockTransferItem.getQty());
                    warehouseItemRepository.save(fromWarehouseItem);

                    WarehouseItem toWarehouseItem = warehouseItemRepository
                            .findByWarehouseIdAndProductIdAndUnitId(
                                   Optional.ofNullable(stockTransfer.getToWarehouseId()).orElse(stockTransferItem.getToWarehouseId()),
                                    stockTransferItem.getToProductId(),
                                    stockTransferItem.getUnitId()
                            )
                            .orElseThrow(
                                    notFoundExceptionThrow(WarehouseItem.class.getSimpleName(), "ProductId", stockTransferItem.getFromProductId())
                            );

                    toWarehouseItem.subtractQty(stockTransferItem.getQty());
                    warehouseItemRepository.save(toWarehouseItem);
                }
            }
            stockTransfer.setRejectReason(updateStatusDTO.getRejectReason());
        }
        stockTransfer.setStatus(status);
        stockTransferRepository.save(stockTransfer);
    }
}
