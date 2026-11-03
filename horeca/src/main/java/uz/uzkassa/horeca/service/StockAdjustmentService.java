package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.stockadjustment.StockAdjustment;
import uz.uzkassa.horeca.domain.stockadjustment.StockAdjustmentItem;
import uz.uzkassa.horeca.domain.unit.Unit;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.stockadjustment.*;
import uz.uzkassa.horeca.enums.Prefix;
import uz.uzkassa.horeca.enums.StockAdjustmentStatus;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.interfaces.CrudAdvanced;
import uz.uzkassa.horeca.interfaces.Numbering;
import uz.uzkassa.horeca.repository.StockAdjustmentItemRepository;
import uz.uzkassa.horeca.repository.StockAdjustmentRepository;
import uz.uzkassa.horeca.utils.DateUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class StockAdjustmentService extends BaseService
        implements CrudAdvanced<Long, StockAdjustmentDTO, StockAdjustmentDetailDTO, StockAdjustmentListDTO, BaseFilter, StockAdjustmentStatus, CommonDTO>, Numbering {

    StockAdjustmentRepository stockAdjustmentRepository;
    StockAdjustmentItemRepository stockAdjustmentItemRepository;
    WarehouseItemService warehouseItemService;

    @Override
    public void validate(StockAdjustmentDTO stockAdjustmentDTO) {
        getCurrentBranchId(stockAdjustmentDTO.getBranchId(), true);
        if (stockAdjustmentDTO.getAdjustmentDate() == null) {
            throw badRequestExceptionThrow("Adjustment date is required").get();
        }
        if (CollectionUtils.isEmpty(stockAdjustmentDTO.getItems())) {
            throw badRequestExceptionThrow("Items are required").get();
        }
    }

    private void validateItems(StockAdjustmentItemDTO stockAdjustmentItemDTO, StockAdjustmentDTO stockAdjustmentDTO) {
        if (stockAdjustmentItemDTO.getWarehouseId() == null && stockAdjustmentDTO.getWarehouseId() == null) {
            throw badRequestExceptionThrow("Warehouse is required").get();
        }
        if (stockAdjustmentItemDTO.getProductId() == null) {
            throw badRequestExceptionThrow("Product is required").get();
        }
        if (stockAdjustmentItemDTO.getQty() == null) {
            throw badRequestExceptionThrow("Qty is required").get();
        }
    }

    @Override
    public HashMap<String, String> getNumberingMap(Long branchId) {
        Long incrementNumber = stockAdjustmentRepository.getMaxIncrementNumber(branchId) + 1L;
        String stockNumber = Prefix.STOCK_ADJUSTMENT.getName().concat(DateUtils.formatYearLastTwoDigit(LocalDate.now())).concat("-").concat(COMMON_NUMBERING_FORMAT.format(incrementNumber));

        HashMap<String, String> numberMap = new HashMap<>();
        numberMap.put(INCREMENT_NUMBER, String.valueOf(incrementNumber));
        numberMap.put(NUMBER, stockNumber);
        return numberMap;
    }

    @Override
    @Transactional
    public Long create(StockAdjustmentDTO stockAdjustmentDTO) {
        this.validate(stockAdjustmentDTO);
        Long branchId = getCurrentBranchId(stockAdjustmentDTO.getBranchId(), true);
        HashMap<String, String> numberingMap = this.getNumberingMap(branchId);
        Unit unit = getUnit();
        StockAdjustment stockAdjustment = new StockAdjustment();
        stockAdjustment.setAdjustmentNumber(numberingMap.get(NUMBER));
        stockAdjustment.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        stockAdjustment.setCompanyId(getCurrentCompanyId());
        stockAdjustment.setAdjustmentDate(stockAdjustmentDTO.getAdjustmentDate());
        stockAdjustment.setDescription(stockAdjustmentDTO.getDescription());
        stockAdjustment.setBranchId(branchId);
        stockAdjustment.setWarehouseId(stockAdjustmentDTO.getWarehouseId());
        stockAdjustmentRepository.save(stockAdjustment);

        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (StockAdjustmentItemDTO stockAdjustmentItemDTO : stockAdjustmentDTO.getItems()) {
            this.validateItems(stockAdjustmentItemDTO, stockAdjustmentDTO);
            StockAdjustmentItem stockAdjustmentItem = new StockAdjustmentItem();
            stockAdjustmentItem.setProductId(stockAdjustmentItemDTO.getProductId());
            stockAdjustmentItem.setQty(stockAdjustmentItemDTO.getQty());
            stockAdjustmentItem.setUnitId(unit.getId());
            stockAdjustmentItem.setPrice(stockAdjustmentItemDTO.getPrice());
            stockAdjustmentItem.setWarehouseId(Optional.ofNullable(stockAdjustmentDTO.getWarehouseId()).orElseGet(stockAdjustmentItemDTO::getWarehouseId));
            if (!CollectionUtils.isEmpty(stockAdjustmentItemDTO.getAggregationCodes())) {
                stockAdjustmentItem.setAggregationCodes(stockAdjustmentItemDTO.getAggregationCodes());
            }
            if (!CollectionUtils.isEmpty(stockAdjustmentItemDTO.getMarkCodes())) {
                stockAdjustmentItem.setMarkCodes(stockAdjustmentItemDTO.getMarkCodes());
            }
            if (!CollectionUtils.isEmpty(stockAdjustmentItemDTO.getBlockCodes())) {
                stockAdjustmentItem.setBlockCodes(stockAdjustmentItemDTO.getBlockCodes());
            }
            stockAdjustment.addItem(stockAdjustmentItem);
            totalQty = totalQty.add(stockAdjustmentItemDTO.getQty());
            if (stockAdjustmentItemDTO.getPrice() != null) {
                totalPrice = totalPrice.add(stockAdjustmentItemDTO.getPrice());
            }
        }
        stockAdjustment.setTotalQty(totalQty);
        stockAdjustment.setTotalPrice(totalPrice);
        stockAdjustmentRepository.save(stockAdjustment);
        return stockAdjustment.getId();
    }

    @Transactional
    @Override
    public Long update(Long id, StockAdjustmentDTO stockAdjustmentDTO) {
        StockAdjustment stockAdjustment = stockAdjustmentRepository
                .findById(id)
                .orElseThrow(notFoundExceptionThrow(StockAdjustment.class.getSimpleName(), "id", id));

        if (!StockAdjustmentStatus.DRAFT.equals(stockAdjustment.getStatus())) {
            throw badRequestExceptionThrow(
                    localizationService.localize("you.can.update.only", new Object[]{StockAdjustmentStatus.DRAFT.getName()})).get();
        }

        stockAdjustment.setAdjustmentDate(stockAdjustmentDTO.getAdjustmentDate());
        stockAdjustment.setDescription(stockAdjustmentDTO.getDescription());
        stockAdjustment.setWarehouseId(stockAdjustmentDTO.getWarehouseId());

        Map<Long, StockAdjustmentItem> stockAdjustmentItemsMap = stockAdjustment
                .getItems()
                .stream()
                .collect(Collectors.toMap(StockAdjustmentItem::getId, Function.identity()));

        Unit unit = getUnit();
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (StockAdjustmentItemDTO stockAdjustmentItemDTO : stockAdjustmentDTO.getItems()) {
            StockAdjustmentItem stockAdjustmentItem;
            if (
                    stockAdjustmentItemDTO.getId() != null &&
                            stockAdjustmentItemsMap.get(stockAdjustmentItemDTO.getId()) != null
            ) {
                stockAdjustmentItem = stockAdjustmentItemsMap.get(stockAdjustmentItemDTO.getId());
                stockAdjustmentItemsMap.remove(stockAdjustmentItem.getId());
            } else {
                stockAdjustmentItem = new StockAdjustmentItem();
            }

            stockAdjustmentItem.setProductId(stockAdjustmentItemDTO.getProductId());
            stockAdjustmentItem.setUnitId(unit.getId());
            stockAdjustmentItem.setQty(stockAdjustmentItemDTO.getQty());
            stockAdjustmentItem.setPrice(stockAdjustmentItemDTO.getPrice());
            stockAdjustmentItem.setWarehouseId(Optional.ofNullable(stockAdjustmentItemDTO.getWarehouseId()).orElse(stockAdjustmentDTO.getWarehouseId()));
            stockAdjustment.addItem(stockAdjustmentItem);

            totalQty = totalQty.add(stockAdjustmentItemDTO.getQty());
            if (stockAdjustmentItemDTO.getPrice() != null) {
                totalPrice = totalPrice.add(stockAdjustmentItemDTO.getPrice());
            }
        }
        stockAdjustment.setTotalQty(totalQty);
        stockAdjustment.setTotalPrice(totalPrice);
        if (!stockAdjustmentItemsMap.isEmpty()) {
            stockAdjustmentItemRepository.deleteAllByIdInBatch(stockAdjustmentItemsMap.keySet());
        }
        stockAdjustmentRepository.save(stockAdjustment);
        return stockAdjustment.getId();
    }

    @Override
    public StockAdjustmentDetailDTO get(Long id) {
        return stockAdjustmentRepository
                .findById(id).map(stockAdjustment -> {
                    StockAdjustmentDetailDTO stockAdjustmentDetailDTO = new StockAdjustmentDetailDTO();
                    stockAdjustmentDetailDTO.setId(stockAdjustment.getId());
                    stockAdjustmentDetailDTO.setAdjustmentDate(stockAdjustment.getAdjustmentDate());
                    stockAdjustmentDetailDTO.setAdjustmentNumber(stockAdjustment.getAdjustmentNumber());
                    stockAdjustmentDetailDTO.setStatus(stockAdjustment.getStatus());
                    stockAdjustmentDetailDTO.setBranch(stockAdjustment.getBranch().toCommonDTO());
                    if (stockAdjustment.getWarehouseId() != null) {
                        stockAdjustmentDetailDTO.setWarehouse(stockAdjustment.getWarehouse().toCommonDTO());
                    }
                    stockAdjustmentDetailDTO.setDescription(stockAdjustment.getDescription());
                    stockAdjustmentDetailDTO.setTotalQty(stockAdjustment.getTotalQty());

                    stockAdjustment
                            .getItems()
                            .forEach(stockAdjustmentItem -> {
                                StockAdjustmentItemDetailDTO stockAdjustmentItemDetailDTO = new StockAdjustmentItemDetailDTO();
                                stockAdjustmentItemDetailDTO.setId(stockAdjustmentItem.getProductId());
                                stockAdjustmentItemDetailDTO.setProduct(stockAdjustmentItem.getProduct().toCommonDTO());
                                stockAdjustmentItemDetailDTO.setQty(stockAdjustmentItem.getQty());
                                stockAdjustmentItemDetailDTO.setPrice(stockAdjustmentItem.getPrice());
                                if (stockAdjustmentItem.getWarehouseId() != null) {
                                    stockAdjustmentItemDetailDTO.setWarehouse(stockAdjustmentItem.getWarehouse().toCommonDTO());
                                }
                                stockAdjustmentItemDetailDTO.setAggregationCodes(stockAdjustmentItem.getAggregationCodes());
                                stockAdjustmentItemDetailDTO.setMarkCodes(stockAdjustmentItem.getMarkCodes());
                                stockAdjustmentItemDetailDTO.setBlockCodes(stockAdjustmentItem.getBlockCodes());
                                stockAdjustmentDetailDTO.addItem(stockAdjustmentItemDetailDTO);
                            });
                    return stockAdjustmentDetailDTO;
                }).orElseThrow(notFoundExceptionThrow(StockAdjustment.class.getSimpleName(), "id", id));
    }

    @Override
    public Page<StockAdjustmentListDTO> getList(BaseFilter filter) {
        ResultList<StockAdjustment> resultList = stockAdjustmentRepository.getResultList(filter);
        List<StockAdjustmentListDTO> result = resultList
                .getList()
                .stream()
                .map(stockAdjustment -> {
                    StockAdjustmentListDTO stockAdjustmentListDTO = new StockAdjustmentListDTO();
                    stockAdjustmentListDTO.setId(stockAdjustment.getId());
                    stockAdjustmentListDTO.setAdjustmentNumber(stockAdjustment.getAdjustmentNumber());
                    stockAdjustmentListDTO.setAdjustmentDate(stockAdjustment.getAdjustmentDate());
                    stockAdjustmentListDTO.setBranch(stockAdjustment.getBranch().toCommonDTO());
                    if (stockAdjustment.getWarehouseId() != null) {
                        stockAdjustmentListDTO.setWarehouse(stockAdjustment.getWarehouse().toCommonDTO());
                    }
                    stockAdjustmentListDTO.setStatus(stockAdjustment.getStatus());
                    stockAdjustmentListDTO.setTotalQty(stockAdjustment.getTotalQty());
                    stockAdjustmentListDTO.setTotalPrice(stockAdjustment.getTotalPrice());
                    return stockAdjustmentListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        stockAdjustmentRepository
                .findById(id)
                .map(stockAdjustment -> {
                    if (!StockAdjustmentStatus.DRAFT.equals(stockAdjustment.getStatus())) {
                        throw badRequestExceptionThrow(
                                localizationService.localize("you.can.delete.only", new Object[]{StockAdjustmentStatus.DRAFT.getName()})
                        ).get();
                    }
                    stockAdjustment.setDeleted(true);
                    stockAdjustmentRepository.save(stockAdjustment);
                    return stockAdjustment;
                })
                .orElseThrow(notFoundExceptionThrow(StockAdjustment.class.getSimpleName(), "id", id));
    }

    @Override
    public List<CommonDTO> lookUp(BaseFilter filter) {
        return stockAdjustmentRepository.getResultList(filter)
                .getList()
                .stream()
                .map(StockAdjustment::toCommonDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, StockAdjustmentStatus status) {
        stockAdjustmentRepository
                .findById(id)
                .map(stockAdjustment -> {
                    if (StockAdjustmentStatus.COMPLETED.equals(status)) {
                        if (!StockAdjustmentStatus.DRAFT.equals(stockAdjustment.getStatus())) {
                            throw badRequestExceptionThrow("Только черновой статус корректировки может быть завершен").get();
                        }
                        warehouseItemService.out(stockAdjustment);
                    } else if (StockAdjustmentStatus.REJECTED.equals(status)) {
                        if (!StockAdjustmentStatus.COMPLETED.equals(stockAdjustment.getStatus())) {
                            throw badRequestExceptionThrow("Только завершенный статус корректировки может быть отклонен").get();
                        }
                        warehouseItemService.in(stockAdjustment);
                    }
                    stockAdjustment.setStatus(status);
                    stockAdjustmentRepository.save(stockAdjustment);
                    return stockAdjustment;
                })
                .orElseThrow(notFoundExceptionThrow(StockAdjustment.class.getSimpleName(), "id", id));
    }
}
