package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.stock.Stock;
import uz.uzkassa.horeca.domain.stock.StockItem;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.stockin.*;
import uz.uzkassa.horeca.enums.CodeType;
import uz.uzkassa.horeca.enums.Prefix;
import uz.uzkassa.horeca.enums.StockStatus;
import uz.uzkassa.horeca.enums.TransferType;
import uz.uzkassa.horeca.filters.StockFilter;
import uz.uzkassa.horeca.interfaces.CrudAdvanced;
import uz.uzkassa.horeca.interfaces.Numbering;
import uz.uzkassa.horeca.repository.StockItemRepository;
import uz.uzkassa.horeca.repository.StockRepository;
import uz.uzkassa.horeca.utils.CalcUtils;
import uz.uzkassa.horeca.utils.DateUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class StockInService
        extends BaseService
        implements CrudAdvanced<Long, StockInDTO, StockInDetailDTO, StockInListDTO, StockFilter, StockStatus, CommonDTO>, Numbering {

    StockRepository stockRepository;
    StockItemRepository stockItemRepository;
    WarehouseItemService warehouseItemService;

    @Override
    public HashMap<String, String> getNumberingMap(Long branchId) {
        Long incrementNumber = stockRepository.getMaxIncrementNumber(branchId, TransferType.IN) + 1L;
        String stockNumber = Prefix.STOCK_IN
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
    public void validate(StockInDTO stockInDTO) {
        getCurrentBranchId(stockInDTO.getBranchId(), true);
        if (stockInDTO.getStockDate() == null) {
            throw badRequestExceptionThrow("Stock date is required").get();
        }
        if (CollectionUtils.isEmpty(stockInDTO.getItems())) {
            throw badRequestExceptionThrow("Items are required").get();
        }
    }

    private void validateItems(StockInItemDTO stockInItemDTO, StockInDTO stockInDTO) {
        if (stockInItemDTO.getProductId() == null) {
            throw badRequestExceptionThrow("Product is required").get();
        }
        if (stockInItemDTO.getUnitId() == null) {
            throw badRequestExceptionThrow("Unit is required").get();
        }
        if (stockInItemDTO.getQty() == null) {
            throw badRequestExceptionThrow("Qty is required").get();
        }
        if (stockInItemDTO.getTotalPrice() == null) {
            throw badRequestExceptionThrow("Total price is required").get();
        }
        if (stockInDTO.getWarehouseId() == null && stockInItemDTO.getWarehouseId() == null) {
            throw badRequestExceptionThrow("Warehouse is required").get();
        }
    }

    @Transactional
    public Long create(StockInDTO stockInDTO) {
        this.validate(stockInDTO);
        Long branchId = getCurrentBranchId(stockInDTO.getBranchId(), true);
        HashMap<String, String> numberingMap = this.getNumberingMap(branchId);

        Stock stock = new Stock();
        stock.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        stock.setStockNumber(numberingMap.get(NUMBER));
        stock.setStockDate(stockInDTO.getStockDate());
        stock.setTransferType(TransferType.IN);
        stock.setStatus(StockStatus.DRAFT);
        stock.setDescription(stockInDTO.getDescription());

        stock.setBranchId(branchId);
        stock.setWarehouseId(stockInDTO.getWarehouseId());
        stock.setSupplierId(stockInDTO.getSupplierId());
        stock.setCustomerId(getCurrentCompanyId());
        stock.setCompanyId(getCurrentCompanyId());
        stockRepository.save(stock);

        BigDecimal grandTotalPrice = BigDecimal.ZERO;
        BigDecimal grandTotalQty = BigDecimal.ZERO;
        BigDecimal grandTotalVatPrice = BigDecimal.ZERO;

        for (StockInItemDTO stockInItemDTO : stockInDTO.getItems()) {
            this.validateItems(stockInItemDTO, stockInDTO);

            StockItem stockItem = new StockItem();
            stockItem.setProductId(stockInItemDTO.getProductId());
            stockItem.setPrice(stockInItemDTO.getPrice());
            stockItem.setUnitId(stockInItemDTO.getUnitId());
            stockItem.setWarehouseId(Optional.ofNullable(stockInItemDTO.getWarehouseId()).orElse(stockInDTO.getWarehouseId()));
            stockItem.setQty(stockInItemDTO.getQty());
            stockItem.setTotalPrice(stockInItemDTO.getTotalPrice());
            stockItem.setInKg(stockInItemDTO.getInKg());

            if (stockInItemDTO.getVatRate() != null) {
                stockItem.setVatRate(stockInItemDTO.getVatRate());
                BigDecimal totalVatPrice = CalcUtils.calcVat(stockInItemDTO.getTotalPrice(), stockInItemDTO.getVatRate());
                stockItem.setVatPrice(totalVatPrice.divide(stockInItemDTO.getQty(), MathContext.DECIMAL32));
                stockItem.setTotalVatPrice(totalVatPrice);
                grandTotalVatPrice = grandTotalVatPrice.add(totalVatPrice);
            }

            stockItem.setHasMarking(
                    !CollectionUtils.isEmpty(stockInItemDTO.getMarkCodes()) ||
                            !CollectionUtils.isEmpty(stockInItemDTO.getAggregationCodes()) ||
                            !CollectionUtils.isEmpty(stockInItemDTO.getBlockCodes())
            );

            if (!CollectionUtils.isEmpty(stockInItemDTO.getMarkCodes())) {
                stockItem.setMarkCodes(stockInItemDTO.getMarkCodes());
            }
            if (!CollectionUtils.isEmpty(stockInItemDTO.getAggregationCodes())) {
                stockItem.setAggregationCodes(stockInItemDTO.getAggregationCodes());
            }
            if (CollectionUtils.isEmpty(stockInItemDTO.getBlockCodes())) {
                stockItem.setBlockCodes(stockInItemDTO.getBlockCodes());
            }

            grandTotalPrice = grandTotalPrice.add(stockInItemDTO.getTotalPrice());
            grandTotalQty = grandTotalQty.add(stockInItemDTO.getQty());
            stock.addItem(stockItem);
        }
        stock.setTotalPrice(grandTotalPrice);
        stock.setTotalQty(grandTotalQty);
        stock.setTotalVatPrice(grandTotalVatPrice);
        stockRepository.save(stock);
        return stock.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, StockInDTO stockInDTO) {
        return stockRepository
                .findById(id)
                .map(stock -> {
                    stockInDTO.setId(id);
                    this.validate(stockInDTO);
                    if (!StockStatus.DRAFT.equals(stock.getStatus())) {
                        throw badRequestExceptionThrow("Вы можете изменить только черновик").get();
                    }

                    stock.setStockDate(stockInDTO.getStockDate());
                    stock.setSupplierId(stockInDTO.getSupplierId());
                    stock.setDescription(stockInDTO.getDescription());
                    stock.setStatus(StockStatus.DRAFT);

                    Map<Long, StockItem> stockItemsMap = stock
                            .getItems()
                            .stream()
                            .collect(Collectors.toMap(StockItem::getId, Function.identity()));

                    BigDecimal grandTotalPrice = BigDecimal.ZERO;
                    BigDecimal grandTotalQty = BigDecimal.ZERO;
                    BigDecimal grandTotalVatPrice = BigDecimal.ZERO;

                    for (StockInItemDTO stockInItemDTO : stockInDTO.getItems()) {
                        this.validateItems(stockInItemDTO, stockInDTO);

                        StockItem stockItem;
                        if (stockInItemDTO.getId() != null && stockItemsMap.get(stockInItemDTO.getId()) != null) {
                            stockItem = stockItemsMap.get(stockInItemDTO.getId());
                            stockItemsMap.remove(stockItem.getId());
                        } else {
                            stockItem = new StockItem();
                        }
                        stockItem.setProductId(stockInItemDTO.getProductId());
                        stockItem.setPrice(stockInItemDTO.getPrice());
                        stockItem.setInKg(stockInItemDTO.getInKg());
                        stockItem.setUnitId(stockInItemDTO.getUnitId());
                        stockItem.setWarehouseId(Optional.ofNullable(stockInItemDTO.getWarehouseId()).orElse(stockInDTO.getWarehouseId()));
                        stockItem.setQty(stockInItemDTO.getQty());
                        stockItem.setTotalPrice(stockInItemDTO.getTotalPrice());

                        if (stockInItemDTO.getVatRate() != null) {
                            stockItem.setVatRate(stockInItemDTO.getVatRate());
                            BigDecimal totalVatPrice = CalcUtils.calcVat(stockInItemDTO.getVatRate(), stockInItemDTO.getTotalPrice());
                            stockItem.setVatPrice(totalVatPrice.divide(stockInItemDTO.getQty(), MathContext.DECIMAL32));
                            stockItem.setTotalVatPrice(totalVatPrice);
                            grandTotalVatPrice = grandTotalVatPrice.add(totalVatPrice);
                        }

                        stockItem.setHasMarking(
                                !CollectionUtils.isEmpty(stockInItemDTO.getMarkCodes()) ||
                                        !CollectionUtils.isEmpty(stockInItemDTO.getAggregationCodes()) ||
                                        !CollectionUtils.isEmpty(stockInItemDTO.getBlockCodes())
                        );

                        if (!CollectionUtils.isEmpty(stockInItemDTO.getMarkCodes())) {
                            stockItem.setMarkCodes(stockInItemDTO.getMarkCodes());
                        }
                        if (!CollectionUtils.isEmpty(stockInItemDTO.getAggregationCodes())) {
                            stockItem.setAggregationCodes(stockInItemDTO.getAggregationCodes());
                        }
                        if (!CollectionUtils.isEmpty(stockInItemDTO.getBlockCodes())) {
                            stockItem.setBlockCodes(stockInItemDTO.getBlockCodes());
                        }

                        grandTotalPrice = grandTotalPrice.add(stockInItemDTO.getTotalPrice());
                        grandTotalQty = grandTotalQty.add(stockInItemDTO.getQty());
                        stock.addItem(stockItem);
                    }
                    stock.setTotalPrice(grandTotalPrice);
                    stock.setTotalQty(grandTotalQty);
                    stock.setTotalVatPrice(grandTotalVatPrice);
                    stockRepository.save(stock);
                    if (!CollectionUtils.isEmpty(stockItemsMap.values())) {
                        stockItemRepository.deleteAllInBatch(stockItemsMap.values());
                    }
                    return id;
                })
                .orElseThrow(notFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));
    }

    @Override
    public StockInDetailDTO get(Long id) {
        return stockRepository
                .findById(id)
                .map(stock -> {
                    StockInDetailDTO stockInDetailDTO = new StockInDetailDTO();
                    stockInDetailDTO.setId(stock.getId());
                    stockInDetailDTO.setStockDate(stock.getStockDate());
                    stockInDetailDTO.setStockNumber(stock.getStockNumber());
                    stockInDetailDTO.setBranch(stock.getBranch().toCommonDTO());
                    if (stock.getSupplierId() != null) {
                        stockInDetailDTO.setSupplier(toCompanyBaseDTO(stock.getSupplier()));
                    }
                    if (stock.getWarehouseId() != null) {
                        stockInDetailDTO.setWarehouse(stock.getWarehouse().toCommonDTO());
                    }
                    stockInDetailDTO.setDescription(stock.getDescription());
                    stockInDetailDTO.setStatus(stock.getStatus());

                    stockInDetailDTO.setTotalPrice(stock.getTotalPrice());
                    stockInDetailDTO.setTotalQty(stock.getTotalQty());
                    stockInDetailDTO.setTotalVatPrice(stock.getTotalVatPrice());

                    stock
                            .getItems()
                            .forEach(stockItem -> {
                                StockInItemDetailDTO stockInItemDetailDTO = new StockInItemDetailDTO();
                                stockInItemDetailDTO.setId(stockItem.getId());
                                stockInItemDetailDTO.setProduct(stockItem.getProduct().toCommonDTO());
                                stockInItemDetailDTO.setPrice(stockItem.getPrice());
                                stockInItemDetailDTO.setUnit(stockItem.getUnit().toCommonDTO());
                                stockInItemDetailDTO.setInKg(stockItem.getInKg());
                                stockInItemDetailDTO.setHasMarking(stockItem.isHasMarking());
                                if (stockItem.getWarehouseId() != null) {
                                    stockInItemDetailDTO.setWarehouse(stockItem.getWarehouse().toCommonDTO());
                                }
                                stockInItemDetailDTO.setQty(stockItem.getQty());
                                stockInItemDetailDTO.setTotalPrice(stockItem.getTotalPrice());
                                stockInItemDetailDTO.setVatRate(stockItem.getVatRate());
                                stockInItemDetailDTO.setTotalVatPrice(stockItem.getTotalVatPrice());
                                stockInDetailDTO.addItem(stockInItemDetailDTO);
                            });
                    return stockInDetailDTO;
                })
                .orElseThrow(notFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));
    }

    @Override
    public Page<StockInListDTO> getList(StockFilter filter) {
        filter.setTransferType(TransferType.IN.name());
        ResultList<Stock> resultList = stockRepository.getResultList(filter);
        List<StockInListDTO> result = resultList
                .getList()
                .stream()
                .map(stock -> {
                    StockInListDTO stockInListDTO = new StockInListDTO();
                    stockInListDTO.setId(stock.getId());
                    stockInListDTO.setStockNumber(stock.getStockNumber());
                    stockInListDTO.setStockDate(stock.getStockDate());
                    stockInListDTO.setStatus(stock.getStatus());
                    if (stock.getSupplierId() != null) {
                        stockInListDTO.setSupplier(stock.getSupplier().toCommonDTO());
                    }
                    if (stock.getWarehouseId() != null) {
                        stockInListDTO.setWarehouse(stock.getWarehouse().toCommonDTO());
                    }
                    stockInListDTO.setTotalPrice(stock.getTotalPrice());
                    stockInListDTO.setTotalQty(stock.getTotalQty());
                    stockInListDTO.setTotalVatPrice(stock.getTotalVatPrice());
                    return stockInListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        stockRepository.findById(id)
                .map(stock -> {
                    if (StockStatus.DRAFT.equals(stock.getStatus()) || StockStatus.CANCELLED.equals(stock.getStatus())) {
                        stock.setDeleted(true);
                        stockRepository.save(stock);
                        return stock;
                    } else {
                        throw badRequestExceptionThrow("Вы можете удалить только черновик или отменен").get();
                    }
                }).orElseThrow(notFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));
    }

    @Override
    public List<CommonDTO> lookUp(StockFilter filter) {
        filter.setTransferType(TransferType.IN.name());
        return stockRepository
                .getResultList(filter)
                .getList()
                .stream().map(Stock::toCommonDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, StockStatus status) {
        stockRepository
                .findById(id)
                .map(stock -> {
                    if (StockStatus.COMPLETED.equals(status)) {
                        if (!StockStatus.DRAFT.equals(stock.getStatus())) {
                            throw badRequestExceptionThrow("Вы можете заполнить только черновик").get();
                        }
                        warehouseItemService.in(stock);
                    } else if (StockStatus.CANCELLED.equals(status)) {
                        if (!StockStatus.COMPLETED.equals(stock.getStatus())) {
                            throw badRequestExceptionThrow("Вы можете отклонять только завершен").get();
                        }
                        warehouseItemService.out(stock);
                    }
                    stock.setStatus(status);
                    stockRepository.save(stock);
                    return stock;
                })
                .orElseThrow(notFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));
    }

    public Map<CodeType, List<String>> getCodes(Long stockItemId) {
        return stockItemRepository
                .findById(stockItemId)
                .map(stockItem -> {
                    Map<CodeType, List<String>> result = new HashMap<>();
                    result.put(CodeType.MARK, Optional.ofNullable(stockItem.getMarkCodes()).orElse(new ArrayList<>()));
                    result.put(CodeType.AGGREGATION, Optional.ofNullable(stockItem.getAggregationCodes()).orElse(new ArrayList<>()));
                    result.put(CodeType.BLOCK, Optional.ofNullable(stockItem.getBlockCodes()).orElse(new ArrayList<>()));
                    return result;
                }).orElseThrow(notFoundExceptionThrow(StockItem.class.getSimpleName(), "id", stockItemId));
    }
}
