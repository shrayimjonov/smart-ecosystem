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
import uz.uzkassa.horeca.dto.stockout.*;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class StockOutService
        extends BaseService
        implements CrudAdvanced<Long, StockOutDTO, StockOutDetailDTO, StockOutListDTO, StockFilter, StockStatus, CommonDTO>, Numbering {

    StockRepository stockRepository;
    StockItemRepository stockItemRepository;
    WarehouseItemService warehouseItemService;

    @Override
    public HashMap<String, String> getNumberingMap(Long branchId) {
        Long incrementNumber = stockRepository.getMaxIncrementNumber(branchId, TransferType.OUT) + 1L;
        String stockNumber = Prefix.STOCK_OUT
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
    public void validate(StockOutDTO stockOutDTO) {
        getCurrentBranchId(stockOutDTO.getBranchId(), true);
        if (stockOutDTO.getStockDate() == null) {
            throw badRequestExceptionThrow("Stock date is required").get();
        }
        if (CollectionUtils.isEmpty(stockOutDTO.getItems())) {
            throw badRequestExceptionThrow("Items are required").get();
        }
    }

    private void validateItems(StockOutItemDTO stockOutItemDTO, StockOutDTO stockOutDTO) {
        if (stockOutItemDTO.getProductId() == null) {
            throw badRequestExceptionThrow("Product is required").get();
        }
        if (stockOutItemDTO.getUnitId() == null) {
            throw badRequestExceptionThrow("Unit is required").get();
        }
        if (stockOutItemDTO.getQty() == null) {
            throw badRequestExceptionThrow("Qty is required").get();
        }
        if (stockOutItemDTO.getTotalPrice() == null) {
            throw badRequestExceptionThrow("Price is required").get();
        }
        if (stockOutItemDTO.getWarehouseId() == null && stockOutDTO.getWarehouseId() == null) {
            throw badRequestExceptionThrow("Warehouse is required").get();
        }
    }

    @Transactional
    public Long create(StockOutDTO stockOutDTO) {
        this.validate(stockOutDTO);
        Long branchId = getCurrentBranchId(stockOutDTO.getBranchId(), true);
        HashMap<String, String> numberingMap = this.getNumberingMap(branchId);

        Stock stock = new Stock();
        stock.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        stock.setStockNumber(numberingMap.get(NUMBER));
        stock.setStockDate(stockOutDTO.getStockDate());
        stock.setTransferType(TransferType.OUT);
        stock.setStatus(StockStatus.DRAFT);
        stock.setDescription(stockOutDTO.getDescription());

        stock.setWarehouseId(stockOutDTO.getWarehouseId());
        stock.setBranchId(branchId);
        stock.setSupplierId(getCurrentCompanyId());
        stock.setCompanyId(getCurrentCompanyId());
        stock.setCustomerId(stockOutDTO.getCustomerId());
        stockRepository.save(stock);

        BigDecimal grandTotalPrice = BigDecimal.ZERO;
        BigDecimal grandTotalQty = BigDecimal.ZERO;
        BigDecimal grandTotalVatPrice = BigDecimal.ZERO;

        for (StockOutItemDTO stockOutItemDTO : stockOutDTO.getItems()) {
            this.validateItems(stockOutItemDTO, stockOutDTO);

            StockItem stockItem = new StockItem();
            stockItem.setProductId(stockOutItemDTO.getProductId());
            stockItem.setUnitId(stockOutItemDTO.getUnitId());
            stockItem.setWarehouseId(Optional.ofNullable(stockOutItemDTO.getWarehouseId()).orElse(stockOutDTO.getWarehouseId()));

            stockItem.setQty(stockOutItemDTO.getQty());
            stockItem.setPrice(stockOutItemDTO.getTotalPrice().divide(stockOutItemDTO.getQty(), MathContext.DECIMAL32));
            stockItem.setTotalPrice(stockOutItemDTO.getTotalPrice());

            if (stockOutItemDTO.getVatRate() != null) {
                stockItem.setVatRate(stockOutItemDTO.getVatRate());
                BigDecimal totalVatPrice = CalcUtils.calcVat(stockOutItemDTO.getVatRate(), stockOutItemDTO.getTotalPrice());
                stockItem.setVatPrice(totalVatPrice.divide(stockOutItemDTO.getQty(), MathContext.DECIMAL32));
                stockItem.setTotalVatPrice(totalVatPrice);
                grandTotalVatPrice = grandTotalVatPrice.add(totalVatPrice);
            }

            stockItem.setHasMarking(
                    !CollectionUtils.isEmpty(stockOutItemDTO.getMarkCodes()) ||
                            !CollectionUtils.isEmpty(stockOutItemDTO.getAggregationCodes()) ||
                            !CollectionUtils.isEmpty(stockOutItemDTO.getBlockCodes())
            );

            if (!CollectionUtils.isEmpty(stockOutItemDTO.getMarkCodes())) {
                stockItem.setMarkCodes(stockOutItemDTO.getMarkCodes());
            }
            if (!CollectionUtils.isEmpty(stockOutItemDTO.getAggregationCodes())) {
                stockItem.setAggregationCodes(stockOutItemDTO.getAggregationCodes());
            }
            if (!CollectionUtils.isEmpty(stockOutItemDTO.getBlockCodes())) {
                stockItem.setBlockCodes(stockOutItemDTO.getBlockCodes());
            }
            grandTotalPrice = grandTotalPrice.add(stockOutItemDTO.getTotalPrice());
            grandTotalQty = grandTotalQty.add(stockOutItemDTO.getQty());
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
    public Long update(Long id, StockOutDTO stockOutDTO) {
        return stockRepository
                .findById(id)
                .map(stock -> {
                    if (!StockStatus.DRAFT.equals(stock.getStatus())) {
                        throw badRequestExceptionThrow("Вы можете изменить только черновик").get();
                    }
                    stockOutDTO.setId(id);
                    this.validate(stockOutDTO);

                    stock.setStockDate(stockOutDTO.getStockDate());
                    stock.setCustomerId(stockOutDTO.getCustomerId());
                    stock.setDescription(stockOutDTO.getDescription());
                    stock.setStatus(StockStatus.DRAFT);

                    Map<Long, StockItem> stockItemsMap = stock
                            .getItems()
                            .stream()
                            .collect(Collectors.toMap(StockItem::getId, Function.identity()));

                    BigDecimal grandTotalPrice = BigDecimal.ZERO;
                    BigDecimal grandTotalQty = BigDecimal.ZERO;
                    BigDecimal grandTotalVatPrice = BigDecimal.ZERO;

                    for (StockOutItemDTO stockOutItemDTO : stockOutDTO.getItems()) {
                        this.validateItems(stockOutItemDTO, stockOutDTO);
                        StockItem stockItem;
                        if (stockOutItemDTO.getId() != null && stockItemsMap.get(stockOutItemDTO.getId()) != null) {
                            stockItem = stockItemsMap.get(stockOutItemDTO.getId());
                            stockItemsMap.remove(stockItem.getId());
                        } else {
                            stockItem = new StockItem();
                        }

                        stockItem.setProductId(stockOutItemDTO.getProductId());
                        stockItem.setUnitId(stockOutItemDTO.getUnitId());
                        stockItem.setWarehouseId(Optional.ofNullable(stockOutItemDTO.getWarehouseId()).orElse(stockOutDTO.getWarehouseId()));

                        stockItem.setQty(stockOutItemDTO.getQty());
                        stockItem.setPrice(stockOutItemDTO.getTotalPrice().divide(stockOutItemDTO.getQty(), MathContext.DECIMAL32));
                        stockItem.setTotalPrice(stockOutItemDTO.getTotalPrice());

                        if (stockOutItemDTO.getVatRate() != null) {
                            stockItem.setVatRate(stockOutItemDTO.getVatRate());
                            BigDecimal totalVatPrice = CalcUtils.calcVat(stockOutItemDTO.getVatRate(), stockOutItemDTO.getTotalPrice());
                            stockItem.setVatPrice(totalVatPrice.divide(stockOutItemDTO.getQty(), MathContext.DECIMAL32));
                            stockItem.setTotalVatPrice(totalVatPrice);
                            grandTotalVatPrice = grandTotalVatPrice.add(totalVatPrice);
                        }

                        stockItem.setHasMarking(
                                !CollectionUtils.isEmpty(stockOutItemDTO.getMarkCodes()) ||
                                        !CollectionUtils.isEmpty(stockOutItemDTO.getAggregationCodes()) ||
                                        !CollectionUtils.isEmpty(stockOutItemDTO.getBlockCodes())
                        );

                        if (!CollectionUtils.isEmpty(stockOutItemDTO.getMarkCodes())) {
                            stockItem.setMarkCodes(stockOutItemDTO.getMarkCodes());
                        }
                        if (!CollectionUtils.isEmpty(stockOutItemDTO.getAggregationCodes())) {
                            stockItem.setAggregationCodes(stockOutItemDTO.getAggregationCodes());
                        }
                        if (!CollectionUtils.isEmpty(stockOutItemDTO.getBlockCodes())) {
                            stockItem.setBlockCodes(stockOutItemDTO.getBlockCodes());
                        }
                        grandTotalPrice = grandTotalPrice.add(stockOutItemDTO.getTotalPrice());
                        grandTotalQty = grandTotalQty.add(stockOutItemDTO.getQty());
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
    public StockOutDetailDTO get(Long id) {
        return stockRepository
                .findById(id)
                .map(stock -> {
                    StockOutDetailDTO stockOutDetailDTO = new StockOutDetailDTO();
                    stockOutDetailDTO.setId(stock.getId());
                    stockOutDetailDTO.setStockDate(stock.getStockDate());
                    stockOutDetailDTO.setStockNumber(stock.getStockNumber());
                    stockOutDetailDTO.setBranch(stock.getBranch().toCommonDTO());
                    if (stock.getCustomerId() != null) {
                        stockOutDetailDTO.setCustomer(toCompanyBaseDTO(stock.getCustomer()));
                    }
                    if (stock.getWarehouseId() != null) {
                        stockOutDetailDTO.setWarehouse(stock.getWarehouse().toCommonDTO());
                    }
                    stockOutDetailDTO.setDescription(stock.getDescription());
                    stockOutDetailDTO.setStatus(stock.getStatus());
                    stockOutDetailDTO.setTotalPrice(stock.getTotalPrice());
                    stockOutDetailDTO.setTotalQty(stock.getTotalQty());
                    stockOutDetailDTO.setTotalVatPrice(stock.getTotalVatPrice());

                    stock
                            .getItems()
                            .forEach(stockItem -> {
                                StockOutItemDetailDTO stockOutItemDetailDTO = new StockOutItemDetailDTO();
                                stockOutItemDetailDTO.setId(stockItem.getId());
                                stockOutItemDetailDTO.setProduct(stockItem.getProduct().toCommonDTO());
                                stockOutItemDetailDTO.setUnit(stockItem.getUnit().toCommonDTO());
                                stockOutItemDetailDTO.setHasMarking(stockItem.isHasMarking());
                                if (stockItem.getWarehouseId() != null) {
                                    stockOutItemDetailDTO.setWarehouse(stockItem.getWarehouse().toCommonDTO());
                                }

                                stockOutItemDetailDTO.setQty(stockItem.getQty());
                                stockOutItemDetailDTO.setTotalPrice(stockItem.getTotalPrice());
                                stockOutItemDetailDTO.setVatRate(stockItem.getVatRate());
                                stockOutItemDetailDTO.setTotalVatPrice(stockItem.getTotalVatPrice());
                                stockOutDetailDTO.addItem(stockOutItemDetailDTO);
                            });
                    return stockOutDetailDTO;
                })
                .orElseThrow(notFoundExceptionThrow(Stock.class.getSimpleName(), "id", id));
    }

    @Override
    public Page<StockOutListDTO> getList(StockFilter filter) {
        filter.setTransferType(TransferType.OUT.name());
        ResultList<Stock> resultList = stockRepository.getResultList(filter);
        List<StockOutListDTO> result = resultList
                .getList()
                .stream()
                .map(stock -> {
                    StockOutListDTO stockOutListDTO = new StockOutListDTO();
                    stockOutListDTO.setId(stock.getId());
                    stockOutListDTO.setStockNumber(stock.getStockNumber());
                    stockOutListDTO.setStockDate(stock.getStockDate());
                    if (stock.getCustomerId() != null) {
                        stockOutListDTO.setCustomer(stock.getCustomer().toCommonDTO());
                    }
                    if (stock.getWarehouseId() != null) {
                        stockOutListDTO.setWarehouse(stock.getWarehouse().toCommonDTO());
                    }
                    stockOutListDTO.setTotalPrice(stock.getTotalPrice());
                    stockOutListDTO.setTotalQty(stock.getTotalQty());
                    stockOutListDTO.setTotalVatPrice(stock.getTotalVatPrice());
                    stockOutListDTO.setStatus(stock.getStatus());
                    return stockOutListDTO;
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
        filter.setTransferType(TransferType.OUT.name());
        return stockRepository
                .getResultList(filter)
                .getList()
                .stream()
                .map(Stock::toCommonDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, StockStatus status) {
        stockRepository
                .findById(id)
                .map(stock -> {
                    if (StockStatus.COMPLETED == status) {
                        if (StockStatus.DRAFT != stock.getStatus()) {
                            throw badRequestExceptionThrow("Вы можете заполнить только черновик").get();
                        }
                        warehouseItemService.out(stock);
                    } else if (StockStatus.CANCELLED == status) {
                        if (StockStatus.COMPLETED != stock.getStatus()) {
                            throw badRequestExceptionThrow("Вы можете отклонять только завершен").get();
                        }
                        warehouseItemService.in(stock);
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
