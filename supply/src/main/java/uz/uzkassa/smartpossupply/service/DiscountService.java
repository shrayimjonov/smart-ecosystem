package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.discount.Discount;
import uz.uzkassa.smartpossupply.domain.discount.DiscountItem;
import uz.uzkassa.smartpossupply.domain.discount.DiscountItemProduct;
import uz.uzkassa.smartpossupply.domain.discount.DiscountRange;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.discount.DiscountCalculationDTO;
import uz.uzkassa.smartpossupply.dto.discount.DiscountDTO;
import uz.uzkassa.smartpossupply.dto.discount.DiscountDetailByProductResponseDTO;
import uz.uzkassa.smartpossupply.dto.discount.DiscountDetailDTO;
import uz.uzkassa.smartpossupply.dto.discount.DiscountItemDTO;
import uz.uzkassa.smartpossupply.dto.discount.DiscountItemDetailDTO;
import uz.uzkassa.smartpossupply.dto.discount.DiscountItemProductDTO;
import uz.uzkassa.smartpossupply.dto.discount.DiscountItemProductDetailDTO;
import uz.uzkassa.smartpossupply.dto.discount.DiscountListByProductRequestDTO;
import uz.uzkassa.smartpossupply.dto.discount.DiscountListDTO;
import uz.uzkassa.smartpossupply.dto.discount.DiscountRangeDTO;
import uz.uzkassa.smartpossupply.dto.discount.DiscountRangeDetailDTO;
import uz.uzkassa.smartpossupply.dto.order.DiscountResultDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.DiscountRangeType;
import uz.uzkassa.smartpossupply.enums.DiscountType;
import uz.uzkassa.smartpossupply.enums.DiscountValueType;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.DiscountItemRepository;
import uz.uzkassa.smartpossupply.repository.DiscountRangeRepository;
import uz.uzkassa.smartpossupply.repository.DiscountRepository;
import uz.uzkassa.smartpossupply.repository.WarehouseProductRepository;
import uz.uzkassa.smartpossupply.validator.ValidationContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.12.2024
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class DiscountService extends BaseService {

    DiscountRepository discountRepository;
    DiscountItemRepository discountItemRepository;
    DiscountRangeRepository discountRangeRepository;
    WarehouseProductRepository warehouseProductRepository;
    OrderService orderService;
    ValidationContext validationContext;

    @Transactional
    public Long create(DiscountDTO discountDTO) {
        validationContext.validate(discountDTO);
        Discount discount = new Discount();
        discount.setName(discountDTO.getName());
        discount.setValidFrom(Optional.ofNullable(discountDTO.getValidFrom()).orElseGet(LocalDateTime::now));
        discount.setValidTo(discountDTO.getValidTo());
        discount.setBranchId(discountDTO.getBranchId());
        discountRepository.save(discount);
        for (DiscountItemDTO itemDTO : discountDTO.getItems()) {
            DiscountItem item = new DiscountItem();
            item.setDiscountType(DiscountType.valueOf(itemDTO.getDiscountType()));
            item.setRangeType(DiscountRangeType.valueOf(itemDTO.getRangeType()));
            item.setApplyAllCustomers(itemDTO.isApplyAllCustomers());
            item.setApplyAllProducts(itemDTO.isApplyAllProducts());
            item.setProductId(itemDTO.getProductId());
            item.setCustomerId(itemDTO.getCustomerId());
            item.setDiscountId(discount.getId());
            discount.addItem(item);
            discountItemRepository.save(item);
            for (DiscountRangeDTO discountRangeDTO : itemDTO.getRanges()) {
                DiscountRange discountRange = new DiscountRange();
                discountRange.setFixedAmount(discountRangeDTO.getFixedAmount());
                discountRange.setFromAmount(discountRangeDTO.getFromAmount());
                discountRange.setToAmount(discountRangeDTO.getToAmount());
                item.addRange(discountRange);
                discountRangeRepository.save(discountRange);
                for (DiscountItemProductDTO discountItemProductDTO : discountRangeDTO.getDiscountProducts()) {
                    DiscountItemProduct discountItemProduct = new DiscountItemProduct();
                    discountItemProduct.setProductId(discountItemProductDTO.getId());
                    discountItemProduct.setAmount(discountItemProductDTO.getAmount());
                    discountItemProduct.setRecommendedQty(discountItemProductDTO.getRecommendedQty());
                    discountItemProduct.setValueType(DiscountValueType.valueOf(discountItemProductDTO.getValueType()));
                    discountItemProduct.setDiscountLimit(discountItemProductDTO.getLimit());
                    discountRange.addItemProduct(discountItemProduct);
                }
            }
        }
        discountRepository.save(discount);
        return discount.getId();
    }

    @Transactional
    public Long update(Long id, DiscountDTO discountDTO) {
        return discountRepository.findById(id).map(discount -> {
            discountDTO.setId(discount.getId());
            validationContext.validate(discountDTO);
            discount.setName(discountDTO.getName());
            discount.setValidFrom(Optional.ofNullable(discountDTO.getValidFrom()).orElseGet(LocalDateTime::now));
            discount.setValidTo(discountDTO.getValidTo());
            discount.setBranchId(discountDTO.getBranchId());
            discountItemRepository.deleteAllByDiscountId(discount.getId());
            for (DiscountItemDTO itemDTO : discountDTO.getItems()) {
                DiscountItem item = new DiscountItem();
                item.setDiscountType(DiscountType.valueOf(itemDTO.getDiscountType()));
                item.setRangeType(DiscountRangeType.valueOf(itemDTO.getRangeType()));
                item.setApplyAllCustomers(itemDTO.isApplyAllCustomers());
                item.setApplyAllProducts(itemDTO.isApplyAllProducts());
                item.setProductId(itemDTO.getProductId());
                item.setCustomerId(itemDTO.getCustomerId());
                discount.addItem(item);
                discountItemRepository.save(item);
                for (DiscountRangeDTO discountRangeDTO : itemDTO.getRanges()) {
                    DiscountRange discountRange = new DiscountRange();
                    discountRange.setFixedAmount(discountRangeDTO.getFixedAmount());
                    discountRange.setFromAmount(discountRangeDTO.getFromAmount());
                    discountRange.setToAmount(discountRangeDTO.getToAmount());
                    item.addRange(discountRange);
                    discountRangeRepository.save(discountRange);
                    for (DiscountItemProductDTO discountItemProductDTO : discountRangeDTO.getDiscountProducts()) {
                        DiscountItemProduct discountItemProduct = new DiscountItemProduct();
                        discountItemProduct.setProductId(discountItemProductDTO.getId());
                        discountItemProduct.setAmount(discountItemProductDTO.getAmount());
                        discountItemProduct.setRecommendedQty(discountItemProductDTO.getRecommendedQty());
                        discountItemProduct.setValueType(DiscountValueType.valueOf(discountItemProductDTO.getValueType()));
                        discountItemProduct.setDiscountLimit(discountItemProductDTO.getLimit());
                        discountRange.addItemProduct(discountItemProduct);
                    }
                }
            }
            discountRepository.save(discount);
            return discount.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Discount.class.getSimpleName(), "id", id));
    }

    public DiscountDetailDTO get(Long id) {
        return discountRepository.getDetail(id).map(discount -> {
            DiscountDetailDTO discountDetailDTO = new DiscountDetailDTO();
            discountDetailDTO.setId(discount.getId());
            discountDetailDTO.setName(discount.getName());
            discountDetailDTO.setValidFrom(discount.getValidFrom());
            discountDetailDTO.setValidTo(discount.getValidTo());
            discountDetailDTO.setBranch(discount.getBranch().toCommonDTO());
            discountDetailDTO.setStatus(discount.getStatus());
            discountDetailDTO.setCreator(discount.getCreator().toCommonDTO());
            discountDetailDTO.setCreatedDate(discount.getCreatedDate());
            for (DiscountItem discountItem : discount.getItems()) {
                DiscountItemDetailDTO itemDetailDTO = new DiscountItemDetailDTO();
                itemDetailDTO.setDiscountItemId(discountItem.getId());
                itemDetailDTO.setDiscountType(discountItem.getDiscountType());
                itemDetailDTO.setRangeType(discountItem.getRangeType());
                itemDetailDTO.setApplyAllCustomers(discountItem.isApplyAllCustomers());
                itemDetailDTO.setApplyAllProducts(discountItem.isApplyAllProducts());
                if (discountItem.getProductId() != null) {
                    itemDetailDTO.setProduct(discountItem.getProduct().toProductCommonDTO());
                }
                if (discountItem.getCustomerId() != null) {
                    itemDetailDTO.setCustomer(discountItem.getCustomer().toCommonBaseDTO());
                }

                for (DiscountRange discountRange : discountItem.getDiscountRanges()) {
                    DiscountRangeDetailDTO rangeDetailDTO = new DiscountRangeDetailDTO();
                    rangeDetailDTO.setId(discountRange.getId());
                    rangeDetailDTO.setFixedAmount(discountRange.getFixedAmount());
                    rangeDetailDTO.setFromAmount(discountRange.getFromAmount());
                    rangeDetailDTO.setToAmount(discountRange.getToAmount());
                    for (DiscountItemProduct discountProduct : discountRange.getDiscountProducts()) {
                        DiscountItemProductDetailDTO discountItemProductDetailDTO = new DiscountItemProductDetailDTO();
                        discountItemProductDetailDTO.setDiscountItemProductId(discountProduct.getId());
                        discountItemProductDetailDTO.setProduct(discountProduct.getProduct().toProductCommonDTO());
                        discountItemProductDetailDTO.setProductPrice(discountProduct.getProduct().getPrice());
                        discountItemProductDetailDTO.setAmount(discountProduct.getAmount());
                        discountItemProductDetailDTO.setRecommendedQty(discountProduct.getRecommendedQty());
                        discountItemProductDetailDTO.setValueType(discountProduct.getValueType());
                        discountItemProductDetailDTO.setLimit(discountProduct.getDiscountLimit());
                        rangeDetailDTO.addItemProduct(discountItemProductDetailDTO);
                    }
                    itemDetailDTO.addRanges(rangeDetailDTO);
                }
                discountDetailDTO.add(itemDetailDTO);
            }
            return discountDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Discount.class.getSimpleName(), "id", id));
    }

    public Page<DiscountListDTO> getList(BaseFilter filter) {
        ResultList<Discount> resultList = discountRepository.getResultList(filter);
        List<DiscountListDTO> result = resultList.getList().stream().map(discount -> {
            DiscountListDTO discountListDTO = new DiscountListDTO();
            discountListDTO.setId(discount.getId());
            discountListDTO.setName(discount.getName());
            discountListDTO.setBranch(discount.getBranch().toCommonDTO());
            discountListDTO.setStatus(discount.getStatus());
            discountListDTO.setCreatedDate(discount.getCreatedDate());
            discountListDTO.setCreator(discount.getCreator().toCommonDTO());
            discountListDTO.setValidFrom(discount.getValidFrom());
            discountListDTO.setValidTo(discount.getValidTo());
            return discountListDTO;
        }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Transactional
    public void delete(Long id) {
        discountRepository.deleteById(id);
    }

    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        discountRepository.findById(id).ifPresentOrElse(discount -> {
            discount.setStatus(status);
            discountRepository.save(discount);
        }, () -> validationService.entityNotFoundExceptionThrow(Discount.class.getSimpleName(), "id", id));
    }

    public List<CommonDTO> lookUp(BaseFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return discountRepository.lookUp(filter).stream().map(Discount::toCommonDTO).toList();
    }

    public List<DiscountDetailByProductResponseDTO> getByProducts(DiscountListByProductRequestDTO requestDTO) {
        List<DiscountDetailByProductResponseDTO> result = new ArrayList<>();
        for (Long productId : requestDTO.getProductIds()) {
            discountItemRepository.findFirstByProductIdAndDiscountStatusAndDiscountValidToIsBefore(productId, CommonStatus.ACTIVE, LocalDateTime.now()).ifPresent(item -> {
                Discount discount = item.getDiscount();
                DiscountDetailByProductResponseDTO discountDetailDTO = new DiscountDetailByProductResponseDTO();
                discountDetailDTO.setId(discount.getId());
                discountDetailDTO.setName(discount.getName());
                for (DiscountItem discountItem : discount.getItems()) {
                    DiscountItemDetailDTO itemDetailDTO = new DiscountItemDetailDTO();
                    itemDetailDTO.setDiscountItemId(discountItem.getId());
                    itemDetailDTO.setDiscountType(discountItem.getDiscountType());
                    itemDetailDTO.setRangeType(discountItem.getRangeType());
                    itemDetailDTO.setApplyAllCustomers(discountItem.isApplyAllCustomers());
                    itemDetailDTO.setApplyAllProducts(discountItem.isApplyAllProducts());
                    if (discountItem.getProductId() != null) {
                        itemDetailDTO.setProduct(discountItem.getProduct().toProductCommonDTO());
                    }
                    if (discountItem.getCustomerId() != null) {
                        itemDetailDTO.setCustomer(discountItem.getCustomer().toCommonBaseDTO());
                    }
                    for (DiscountRange discountRange : discountItem.getDiscountRanges()) {
                        DiscountRangeDetailDTO rangeDetailDTO = new DiscountRangeDetailDTO();
                        rangeDetailDTO.setId(discountRange.getId());
                        rangeDetailDTO.setFixedAmount(discountRange.getFixedAmount());
                        rangeDetailDTO.setFromAmount(discountRange.getFromAmount());
                        rangeDetailDTO.setToAmount(discountRange.getToAmount());
                        for (DiscountItemProduct discountProduct : discountRange.getDiscountProducts()) {
                            DiscountItemProductDetailDTO discountItemProductDetailDTO = new DiscountItemProductDetailDTO();
                            discountItemProductDetailDTO.setDiscountItemProductId(discountProduct.getId());
                            discountItemProductDetailDTO.setProduct(discountProduct.getProduct().toProductCommonDTO());
                            discountItemProductDetailDTO.setProductPrice(discountProduct.getProduct().getPrice());
                            discountItemProductDetailDTO.setAmount(discountProduct.getAmount());
                            discountItemProductDetailDTO.setRecommendedQty(discountProduct.getRecommendedQty());
                            discountItemProductDetailDTO.setValueType(discountProduct.getValueType());
                            discountItemProductDetailDTO.setLimit(discountProduct.getDiscountLimit());
                            rangeDetailDTO.addItemProduct(discountItemProductDetailDTO);
                        }
                        itemDetailDTO.addRanges(rangeDetailDTO);
                    }
                    discountDetailDTO.add(itemDetailDTO);
                }
                result.add(discountDetailDTO);
            });
        }
        return result;
    }

    @Transactional
    public List<DiscountResultDTO> calculate(DiscountCalculationDTO calculationDTO) {
        if (calculationDTO.getQty() == null) {
            throw validationService.badRequestExceptionThrow("Qty is required").get();
        }
        if (calculationDTO.getTotalPrice() == null) {
            throw validationService.badRequestExceptionThrow("TotalPrice is required").get();
        }
        List<DiscountResultDTO> resultList = new ArrayList<>();
        DiscountItem discountItem = discountItemRepository.findFirstByProductIdAndDiscountStatusAndDiscountValidToIsBefore(calculationDTO.getProductId(), CommonStatus.ACTIVE, LocalDateTime.now()).orElse(null);
        if (discountItem != null) {
            if (DiscountType.SIMPLE == discountItem.getDiscountType()) {
                for (DiscountRange discountRange : discountItem.getDiscountRanges().reversed()) {
                    if (DiscountRangeType.BY_QTY == discountItem.getRangeType()) {
                        if (calculationDTO.getQty().compareTo(discountRange.getFixedAmount()) >= 0) {
                            for (DiscountItemProduct discountProduct : discountRange.getDiscountProducts()) {
                                DiscountResultDTO discountResultDTO = new DiscountResultDTO();
                                discountResultDTO.setValueType(discountProduct.getValueType());
                                discountResultDTO.setLimit(discountProduct.getDiscountLimit());
                                discountResultDTO.setDiscount(discountItem.getDiscount().toListDTO());
                                discountResultDTO.setDiscountItemProduct(discountProduct.toCommonDTO());
                                discountResultDTO.setProduct(discountProduct.getProduct().toProductCommonDTO());
                                discountResultDTO.setVat(discountProduct.getProduct().getVat());
                                discountResultDTO.setUnit(discountProduct.getProduct().getUnit().toDTO());
                                discountResultDTO.setCatalogPrice(discountProduct.getProduct().getPrice());
                                discountResultDTO.setHasMark(discountProduct.getProduct().isHasMark());
                                discountResultDTO.setProductType(discountProduct.getProduct().getProductType());
                                discountResultDTO.setQtyInPackage(discountProduct.getProduct().getQtyInPackage());
                                discountResultDTO.setQtyInBox(discountProduct.getProduct().getQtyInBox());
                                discountResultDTO.setQtyInPallet(discountProduct.getProduct().getQtyInPallet());
                                discountResultDTO.setPackageCode(discountProduct.getProduct().getPackageCode());
                                discountResultDTO.setPackageName(discountProduct.getProduct().getPackageName());
                                BigDecimal totalQty;
                                if (calculationDTO.getWarehouseId() != null) {
                                    discountResultDTO.setWarehouse(new CommonDTO(calculationDTO.getWarehouseId(), null));
                                    discountResultDTO.setQtyInWarehouse(warehouseProductRepository.getQty(calculationDTO.getWarehouseId(), discountProduct.getProductId(), getUnit().getId()));
                                    discountResultDTO.setReservationQty(warehouseProductRepository.getReservationQty(calculationDTO.getWarehouseId(), discountProduct.getProductId(), getUnit().getId()));
                                }
                                if (DiscountValueType.QTY == discountProduct.getValueType()) {
                                    totalQty = Optional.ofNullable(calculationDTO.getQty()).orElse(BigDecimal.ZERO).divide(Optional.ofNullable(discountRange.getFixedAmount()).orElse(BigDecimal.ZERO), RoundingMode.HALF_DOWN).multiply(discountProduct.getAmount());
                                    if (discountProduct.getDiscountLimit() != null && totalQty.compareTo(discountProduct.getDiscountLimit()) > 0) {
                                        totalQty = discountProduct.getDiscountLimit();
                                    }
                                    totalQty = BigDecimal.valueOf(totalQty.intValue());
                                    discountResultDTO.setDiscountPercent(BigDecimal.valueOf(100));
                                    discountResultDTO.setDiscountPrice(discountProduct.getProduct().getPrice());
                                } else if (DiscountValueType.PERCENTAGE == discountProduct.getValueType()) {
                                    totalQty = Optional.ofNullable(discountProduct.getRecommendedQty()).orElse(BigDecimal.ZERO);
                                    if (discountProduct.getDiscountLimit() != null && totalQty.compareTo(discountProduct.getDiscountLimit()) > 0) {
                                        totalQty = discountProduct.getDiscountLimit();
                                    }
                                    discountResultDTO.setDiscountPercent(discountProduct.getAmount());
                                    discountResultDTO.setDiscountPrice(Optional.ofNullable(discountProduct.getProduct().getPrice()).orElse(BigDecimal.ZERO).multiply(discountResultDTO.getDiscountPercent()).divide(BigDecimal.valueOf(100), RoundingMode.HALF_EVEN));
                                } else {
                                    totalQty = Optional.ofNullable(discountProduct.getRecommendedQty()).orElse(BigDecimal.ZERO);
                                    if (discountProduct.getDiscountLimit() != null && totalQty.compareTo(discountProduct.getDiscountLimit()) > 0) {
                                        totalQty = discountProduct.getDiscountLimit();
                                    }
                                    discountResultDTO.setDiscountPrice(discountProduct.getAmount());
                                    discountResultDTO.setDiscountPercent(Optional.ofNullable(discountProduct.getAmount()).orElse(BigDecimal.ZERO).multiply(BigDecimal.valueOf(100)).divide(discountProduct.getProduct().getPrice(), RoundingMode.HALF_EVEN));

                                }
                                discountResultDTO.setQty(totalQty);

                                BigDecimal withoutVatPrice = discountProduct.getProduct().getPrice().subtract(discountResultDTO.getDiscountPrice()).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100).add(Optional.ofNullable(discountProduct.getProduct().getVat().getAmount()).orElse(BigDecimal.ZERO)), RoundingMode.HALF_EVEN);
                                BigDecimal vatPrice = discountProduct.getProduct().getPrice().subtract(discountResultDTO.getDiscountPrice()).subtract(withoutVatPrice);
                                discountResultDTO.setVatRate(discountProduct.getProduct().getVat().getAmount());
                                discountResultDTO.setVatPrice(vatPrice);
                                discountResultDTO.setPriceWithoutVat(withoutVatPrice);
                                discountResultDTO.setExciseRate(calculationDTO.getExciseRate());
                                discountResultDTO.setExciseSum(calculationDTO.getExciseSum());

                                discountResultDTO.setTotalPrice(discountResultDTO.getCatalogPrice().multiply(totalQty));
                                discountResultDTO.setTotalDiscountPrice(discountResultDTO.getDiscountPrice().multiply(totalQty));
                                discountResultDTO.setTotalVatPrice(discountResultDTO.getVatPrice().multiply(totalQty));
                                discountResultDTO.setTotalPriceWithoutVat(discountResultDTO.getPriceWithoutVat().multiply(totalQty));
                                discountResultDTO.setBrutto(Optional.ofNullable(discountProduct.getProduct().getWeightBrutto()).orElse(BigDecimal.ZERO));
                                discountResultDTO.setNetto(Optional.ofNullable(discountProduct.getProduct().getWeightNetto()).orElse(BigDecimal.ZERO));
                                discountResultDTO.setTotalBrutto(Optional.ofNullable(discountResultDTO.getBrutto()).orElse(BigDecimal.ZERO).multiply(totalQty));
                                discountResultDTO.setTotalNetto(Optional.ofNullable(discountResultDTO.getNetto()).orElse(BigDecimal.ZERO).multiply(totalQty));
                                discountResultDTO.setQtyInOrders(orderService.checkQtyInOrders(discountProduct.getProductId(), null, discountResultDTO.getWarehouse().getId()));
                                resultList.add(discountResultDTO);
                            }
                            break;
                        }
                    } else {
                        if (calculationDTO.getTotalPrice().compareTo(discountRange.getFixedAmount()) >= 0) {
                            for (DiscountItemProduct discountProduct : discountRange.getDiscountProducts()) {
                                DiscountResultDTO discountResultDTO = new DiscountResultDTO();
                                discountResultDTO.setValueType(discountProduct.getValueType());
                                discountResultDTO.setLimit(discountProduct.getDiscountLimit());
                                discountResultDTO.setDiscount(discountItem.getDiscount().toListDTO());
                                discountResultDTO.setDiscountItemProduct(discountProduct.toCommonDTO());
                                discountResultDTO.setProduct(discountProduct.getProduct().toProductCommonDTO());
                                discountResultDTO.setVat(discountProduct.getProduct().getVat());
                                discountResultDTO.setUnit(discountProduct.getProduct().getUnit().toDTO());
                                discountResultDTO.setCatalogPrice(discountProduct.getProduct().getPrice());
                                discountResultDTO.setHasMark(discountProduct.getProduct().isHasMark());
                                discountResultDTO.setProductType(discountProduct.getProduct().getProductType());
                                discountResultDTO.setQtyInPackage(discountProduct.getProduct().getQtyInPackage());
                                discountResultDTO.setQtyInBox(discountProduct.getProduct().getQtyInBox());
                                discountResultDTO.setQtyInPallet(discountProduct.getProduct().getQtyInPallet());
                                discountResultDTO.setPackageCode(discountProduct.getProduct().getPackageCode());
                                discountResultDTO.setPackageName(discountProduct.getProduct().getPackageName());
                                BigDecimal totalQty;
                                if (calculationDTO.getWarehouseId() != null) {
                                    discountResultDTO.setWarehouse(new CommonDTO(calculationDTO.getWarehouseId(), null));
                                    discountResultDTO.setQtyInWarehouse(warehouseProductRepository.getQty(calculationDTO.getWarehouseId(), discountProduct.getProductId(), getUnit().getId()));
                                    discountResultDTO.setReservationQty(warehouseProductRepository.getReservationQty(calculationDTO.getWarehouseId(), discountProduct.getProductId(), getUnit().getId()));
                                }
                                if (DiscountValueType.QTY == discountProduct.getValueType()) {
                                    totalQty = Optional.ofNullable(calculationDTO.getTotalPrice()).orElse(BigDecimal.ZERO).divide(Optional.ofNullable(discountRange.getFixedAmount()).orElse(BigDecimal.ZERO), RoundingMode.HALF_DOWN).multiply(discountProduct.getAmount());
                                    if (discountProduct.getDiscountLimit() != null && totalQty.compareTo(discountProduct.getDiscountLimit()) > 0) {
                                        totalQty = discountProduct.getDiscountLimit();
                                    }
                                    totalQty = BigDecimal.valueOf(totalQty.intValue());
                                    discountResultDTO.setQty(totalQty);
                                    discountResultDTO.setDiscountPercent(BigDecimal.valueOf(100));
                                    discountResultDTO.setDiscountPrice(discountProduct.getProduct().getPrice());
                                } else if (DiscountValueType.PERCENTAGE == discountProduct.getValueType()) {
                                    totalQty = Optional.ofNullable(discountProduct.getRecommendedQty()).orElse(BigDecimal.ZERO);
                                    if (discountProduct.getDiscountLimit() != null && totalQty.compareTo(discountProduct.getDiscountLimit()) > 0) {
                                        totalQty = discountProduct.getDiscountLimit();
                                    }
                                    discountResultDTO.setDiscountPercent(discountProduct.getAmount());
                                    discountResultDTO.setDiscountPrice(Optional.ofNullable(discountProduct.getProduct().getPrice()).orElse(BigDecimal.ZERO).multiply(discountResultDTO.getDiscountPercent()).divide(BigDecimal.valueOf(100), RoundingMode.HALF_EVEN));
                                } else {
                                    totalQty = Optional.ofNullable(discountProduct.getRecommendedQty()).orElse(BigDecimal.ZERO);
                                    if (discountProduct.getDiscountLimit() != null && totalQty.compareTo(discountProduct.getDiscountLimit()) > 0) {
                                        totalQty = discountProduct.getDiscountLimit();
                                    }
                                    discountResultDTO.setDiscountPrice(discountProduct.getAmount());
                                    discountResultDTO.setDiscountPercent(Optional.ofNullable(discountProduct.getAmount()).orElse(BigDecimal.ZERO).multiply(BigDecimal.valueOf(100)).divide(discountProduct.getProduct().getPrice(), RoundingMode.HALF_EVEN));

                                }
                                discountResultDTO.setQty(totalQty);

                                BigDecimal withoutVatPrice = discountProduct.getProduct().getPrice().subtract(discountResultDTO.getDiscountPrice()).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100).add(Optional.ofNullable(discountProduct.getProduct().getVat().getAmount()).orElse(BigDecimal.ZERO)), RoundingMode.HALF_EVEN);
                                BigDecimal vatPrice = discountProduct.getProduct().getPrice().subtract(discountResultDTO.getDiscountPrice()).subtract(withoutVatPrice);
                                discountResultDTO.setVatRate(discountProduct.getProduct().getVat().getAmount());
                                discountResultDTO.setVatPrice(vatPrice);
                                discountResultDTO.setPriceWithoutVat(withoutVatPrice);
                                discountResultDTO.setExciseRate(calculationDTO.getExciseRate());
                                discountResultDTO.setExciseSum(calculationDTO.getExciseSum());

                                discountResultDTO.setTotalPrice(discountResultDTO.getCatalogPrice().multiply(totalQty));
                                discountResultDTO.setTotalDiscountPrice(discountResultDTO.getDiscountPrice().multiply(totalQty));
                                discountResultDTO.setTotalVatPrice(discountResultDTO.getVatPrice().multiply(totalQty));
                                discountResultDTO.setTotalPriceWithoutVat(discountResultDTO.getPriceWithoutVat().multiply(totalQty));
                                discountResultDTO.setBrutto(Optional.ofNullable(discountProduct.getProduct().getWeightBrutto()).orElse(BigDecimal.ZERO));
                                discountResultDTO.setNetto(Optional.ofNullable(discountProduct.getProduct().getWeightNetto()).orElse(BigDecimal.ZERO));
                                discountResultDTO.setTotalBrutto(Optional.ofNullable(discountResultDTO.getBrutto()).orElse(BigDecimal.ZERO).multiply(totalQty));
                                discountResultDTO.setTotalNetto(Optional.ofNullable(discountResultDTO.getNetto()).orElse(BigDecimal.ZERO).multiply(totalQty));
                                discountResultDTO.setQtyInOrders(orderService.checkQtyInOrders(discountProduct.getProductId(), null, discountResultDTO.getWarehouse().getId()));
                                resultList.add(discountResultDTO);
                            }
                            break;
                        }
                    }
                }
            } else {
                for (DiscountRange discountRange : discountItem.getDiscountRanges().reversed()) {
                    if (DiscountRangeType.BY_QTY == discountItem.getRangeType()) {
                        if (calculationDTO.getQty().compareTo(discountRange.getFromAmount()) >= 0) {
                            for (DiscountItemProduct discountProduct : discountRange.getDiscountProducts()) {
                                DiscountResultDTO discountResultDTO = new DiscountResultDTO();
                                discountResultDTO.setValueType(discountProduct.getValueType());
                                discountResultDTO.setLimit(discountProduct.getDiscountLimit());
                                discountResultDTO.setDiscount(discountItem.getDiscount().toListDTO());
                                discountResultDTO.setDiscountItemProduct(discountProduct.toCommonDTO());
                                discountResultDTO.setProduct(discountProduct.getProduct().toProductCommonDTO());
                                discountResultDTO.setVat(discountProduct.getProduct().getVat());
                                discountResultDTO.setUnit(discountProduct.getProduct().getUnit().toDTO());
                                discountResultDTO.setCatalogPrice(discountProduct.getProduct().getPrice());
                                discountResultDTO.setHasMark(discountProduct.getProduct().isHasMark());
                                discountResultDTO.setProductType(discountProduct.getProduct().getProductType());
                                discountResultDTO.setQtyInPackage(discountProduct.getProduct().getQtyInPackage());
                                discountResultDTO.setQtyInBox(discountProduct.getProduct().getQtyInBox());
                                discountResultDTO.setQtyInPallet(discountProduct.getProduct().getQtyInPallet());
                                discountResultDTO.setPackageCode(discountProduct.getProduct().getPackageCode());
                                discountResultDTO.setPackageName(discountProduct.getProduct().getPackageName());
                                BigDecimal totalQty;
                                if (calculationDTO.getWarehouseId() != null) {
                                    discountResultDTO.setWarehouse(new CommonDTO(calculationDTO.getWarehouseId(), null));
                                    discountResultDTO.setQtyInWarehouse(warehouseProductRepository.getQty(calculationDTO.getWarehouseId(), discountProduct.getProductId(), getUnit().getId()));
                                    discountResultDTO.setReservationQty(warehouseProductRepository.getReservationQty(calculationDTO.getWarehouseId(), discountProduct.getProductId(), getUnit().getId()));
                                }
                                if (DiscountValueType.QTY == discountProduct.getValueType()) {
                                    totalQty = Optional.ofNullable(discountProduct.getAmount()).orElse(BigDecimal.ZERO);
                                    if (discountProduct.getDiscountLimit() != null && totalQty.compareTo(discountProduct.getDiscountLimit()) > 0) {
                                        totalQty = discountProduct.getDiscountLimit();
                                    }
                                    discountResultDTO.setDiscountPercent(BigDecimal.valueOf(100));
                                    discountResultDTO.setDiscountPrice(discountProduct.getProduct().getPrice());
                                } else if (DiscountValueType.PERCENTAGE == discountProduct.getValueType()) {
                                    totalQty = Optional.ofNullable(discountProduct.getRecommendedQty()).orElse(BigDecimal.ZERO);
                                    if (discountProduct.getDiscountLimit() != null && totalQty.compareTo(discountProduct.getDiscountLimit()) > 0) {
                                        totalQty = discountProduct.getDiscountLimit();
                                    }
                                    discountResultDTO.setDiscountPercent(discountProduct.getAmount());
                                    discountResultDTO.setDiscountPrice(Optional.ofNullable(discountProduct.getProduct().getPrice()).orElse(BigDecimal.ZERO).multiply(discountResultDTO.getDiscountPercent()).divide(BigDecimal.valueOf(100), RoundingMode.HALF_EVEN));
                                } else {
                                    totalQty = Optional.ofNullable(discountProduct.getRecommendedQty()).orElse(BigDecimal.ZERO);
                                    if (discountProduct.getDiscountLimit() != null && totalQty.compareTo(discountProduct.getDiscountLimit()) > 0) {
                                        totalQty = discountProduct.getDiscountLimit();
                                    }
                                    discountResultDTO.setDiscountPrice(discountProduct.getAmount());
                                    discountResultDTO.setDiscountPercent(Optional.ofNullable(discountProduct.getAmount()).orElse(BigDecimal.ZERO).multiply(BigDecimal.valueOf(100)).divide(discountProduct.getProduct().getPrice(), RoundingMode.HALF_EVEN));

                                }

                                discountResultDTO.setQty(totalQty);

                                BigDecimal withoutVatPrice = discountProduct.getProduct().getPrice().subtract(discountResultDTO.getDiscountPrice()).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100).add(Optional.ofNullable(discountProduct.getProduct().getVat().getAmount()).orElse(BigDecimal.ZERO)), RoundingMode.HALF_EVEN);
                                BigDecimal vatPrice = discountProduct.getProduct().getPrice().subtract(discountResultDTO.getDiscountPrice()).subtract(withoutVatPrice);
                                discountResultDTO.setVatRate(discountProduct.getProduct().getVat().getAmount());
                                discountResultDTO.setVatPrice(vatPrice);
                                discountResultDTO.setPriceWithoutVat(withoutVatPrice);
                                discountResultDTO.setExciseRate(calculationDTO.getExciseRate());
                                discountResultDTO.setExciseSum(calculationDTO.getExciseSum());

                                discountResultDTO.setTotalPrice(discountResultDTO.getCatalogPrice().multiply(totalQty));
                                discountResultDTO.setTotalDiscountPrice(discountResultDTO.getDiscountPrice().multiply(totalQty));
                                discountResultDTO.setTotalVatPrice(discountResultDTO.getVatPrice().multiply(totalQty));
                                discountResultDTO.setTotalPriceWithoutVat(discountResultDTO.getPriceWithoutVat().multiply(totalQty));
                                discountResultDTO.setBrutto(Optional.ofNullable(discountProduct.getProduct().getWeightBrutto()).orElse(BigDecimal.ZERO));
                                discountResultDTO.setNetto(Optional.ofNullable(discountProduct.getProduct().getWeightNetto()).orElse(BigDecimal.ZERO));
                                discountResultDTO.setTotalBrutto(Optional.ofNullable(discountResultDTO.getBrutto()).orElse(BigDecimal.ZERO).multiply(totalQty));
                                discountResultDTO.setTotalNetto(Optional.ofNullable(discountResultDTO.getNetto()).orElse(BigDecimal.ZERO).multiply(totalQty));
                                discountResultDTO.setQtyInOrders(orderService.checkQtyInOrders(discountProduct.getProductId(), null, discountResultDTO.getWarehouse().getId()));
                                resultList.add(discountResultDTO);
                            }
                            break;
                        }
                    } else {
                        if (calculationDTO.getTotalPrice().compareTo(discountRange.getFromAmount()) >= 0) {
                            for (DiscountItemProduct discountProduct : discountRange.getDiscountProducts()) {
                                DiscountResultDTO discountResultDTO = new DiscountResultDTO();
                                discountResultDTO.setValueType(discountProduct.getValueType());
                                discountResultDTO.setLimit(discountProduct.getDiscountLimit());
                                discountResultDTO.setDiscount(discountItem.getDiscount().toListDTO());
                                discountResultDTO.setDiscountItemProduct(discountProduct.toCommonDTO());
                                discountResultDTO.setProduct(discountProduct.getProduct().toProductCommonDTO());
                                discountResultDTO.setVat(discountProduct.getProduct().getVat());
                                discountResultDTO.setUnit(discountProduct.getProduct().getUnit().toDTO());
                                discountResultDTO.setCatalogPrice(discountProduct.getProduct().getPrice());
                                discountResultDTO.setHasMark(discountProduct.getProduct().isHasMark());
                                discountResultDTO.setProductType(discountProduct.getProduct().getProductType());
                                discountResultDTO.setQtyInPackage(discountProduct.getProduct().getQtyInPackage());
                                discountResultDTO.setQtyInBox(discountProduct.getProduct().getQtyInBox());
                                discountResultDTO.setQtyInPallet(discountProduct.getProduct().getQtyInPallet());
                                discountResultDTO.setPackageCode(discountProduct.getProduct().getPackageCode());
                                discountResultDTO.setPackageName(discountProduct.getProduct().getPackageName());
                                BigDecimal totalQty;
                                if (calculationDTO.getWarehouseId() != null) {
                                    discountResultDTO.setWarehouse(new CommonDTO(calculationDTO.getWarehouseId(), null));
                                    discountResultDTO.setQtyInWarehouse(warehouseProductRepository.getQty(calculationDTO.getWarehouseId(), discountProduct.getProductId(), getUnit().getId()));
                                    discountResultDTO.setReservationQty(warehouseProductRepository.getReservationQty(calculationDTO.getWarehouseId(), discountProduct.getProductId(), getUnit().getId()));
                                }
                                if (DiscountValueType.QTY == discountProduct.getValueType()) {
                                    totalQty = Optional.ofNullable(discountProduct.getAmount()).orElse(BigDecimal.ZERO);
                                    if (discountProduct.getDiscountLimit() != null && totalQty.compareTo(discountProduct.getDiscountLimit()) > 0) {
                                        totalQty = discountProduct.getDiscountLimit();
                                    }
                                    discountResultDTO.setDiscountPercent(BigDecimal.valueOf(100));
                                    discountResultDTO.setDiscountPrice(discountProduct.getProduct().getPrice());
                                } else if (DiscountValueType.PERCENTAGE == discountProduct.getValueType()) {
                                    totalQty = discountProduct.getRecommendedQty();
                                    if (discountProduct.getDiscountLimit() != null && totalQty.compareTo(discountProduct.getDiscountLimit()) > 0) {
                                        totalQty = discountProduct.getDiscountLimit();
                                    }
                                    discountResultDTO.setQty(totalQty);
                                    discountResultDTO.setDiscountPercent(discountProduct.getAmount());
                                    discountResultDTO.setDiscountPrice(Optional.ofNullable(discountProduct.getProduct().getPrice()).orElse(BigDecimal.ZERO).multiply(discountResultDTO.getDiscountPercent()).divide(BigDecimal.valueOf(100), RoundingMode.HALF_EVEN));
                                } else {
                                    totalQty = Optional.ofNullable(discountProduct.getRecommendedQty()).orElse(BigDecimal.ZERO);
                                    if (discountProduct.getDiscountLimit() != null && totalQty.compareTo(discountProduct.getDiscountLimit()) > 0) {
                                        totalQty = discountProduct.getDiscountLimit();
                                    }
                                    discountResultDTO.setDiscountPrice(discountProduct.getAmount());
                                    discountResultDTO.setDiscountPercent(Optional.ofNullable(discountProduct.getAmount()).orElse(BigDecimal.ZERO).multiply(BigDecimal.valueOf(100)).divide(discountProduct.getProduct().getPrice(), RoundingMode.HALF_EVEN));
                                }

                                discountResultDTO.setQty(totalQty);

                                BigDecimal withoutVatPrice = discountProduct.getProduct().getPrice().subtract(discountResultDTO.getDiscountPrice()).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100).add(Optional.ofNullable(discountProduct.getProduct().getVat().getAmount()).orElse(BigDecimal.ZERO)), RoundingMode.HALF_EVEN);
                                BigDecimal vatPrice = discountProduct.getProduct().getPrice().subtract(discountResultDTO.getDiscountPrice()).subtract(withoutVatPrice);
                                discountResultDTO.setVatRate(discountProduct.getProduct().getVat().getAmount());
                                discountResultDTO.setVatPrice(vatPrice);
                                discountResultDTO.setPriceWithoutVat(withoutVatPrice);
                                discountResultDTO.setExciseRate(calculationDTO.getExciseRate());
                                discountResultDTO.setExciseSum(calculationDTO.getExciseSum());

                                discountResultDTO.setTotalPrice(discountResultDTO.getCatalogPrice().multiply(totalQty));
                                discountResultDTO.setTotalDiscountPrice(discountResultDTO.getDiscountPrice().multiply(totalQty));
                                discountResultDTO.setTotalVatPrice(discountResultDTO.getVatPrice().multiply(totalQty));
                                discountResultDTO.setTotalPriceWithoutVat(discountResultDTO.getPriceWithoutVat().multiply(totalQty));
                                discountResultDTO.setBrutto(Optional.ofNullable(discountProduct.getProduct().getWeightBrutto()).orElse(BigDecimal.ZERO));
                                discountResultDTO.setNetto(Optional.ofNullable(discountProduct.getProduct().getWeightNetto()).orElse(BigDecimal.ZERO));
                                discountResultDTO.setTotalBrutto(Optional.ofNullable(discountResultDTO.getBrutto()).orElse(BigDecimal.ZERO).multiply(totalQty));
                                discountResultDTO.setTotalNetto(Optional.ofNullable(discountResultDTO.getNetto()).orElse(BigDecimal.ZERO).multiply(totalQty));
                                discountResultDTO.setQtyInOrders(orderService.checkQtyInOrders(discountProduct.getProductId(), null, discountResultDTO.getWarehouse().getId()));
                                resultList.add(discountResultDTO);
                            }
                            break;
                        }
                    }
                }
            }
        }
        return resultList;
    }

    public Map<Long, List<DiscountResultDTO>> calculate(List<DiscountCalculationDTO> calculations) {
        Map<Long, List<DiscountResultDTO>> resultMap = new HashMap<>();
        for (DiscountCalculationDTO calculationDTO : calculations) {
            resultMap.put(calculationDTO.getProductId(), this.calculate(calculationDTO));
        }
        return resultMap;
    }

    public List<DiscountDetailDTO> getForOffline(Set<Long> branchIds) {
        List<Discount> discounts = discountRepository.getDetailForOffline(branchIds);

        if (discounts.isEmpty()) {
            return List.of();
        }

        List<DiscountDetailDTO> result = new LinkedList<>();
        for (Discount discount : discounts) {
            DiscountDetailDTO discountDetailDTO = new DiscountDetailDTO();
            discountDetailDTO.setId(discount.getId());
            discountDetailDTO.setName(discount.getName());
            discountDetailDTO.setValidFrom(discount.getValidFrom());
            discountDetailDTO.setValidTo(discount.getValidTo());
            discountDetailDTO.setBranch(discount.getBranch().toCommonDTO());
            discountDetailDTO.setStatus(discount.getStatus());
            discountDetailDTO.setCreator(discount.getCreator().toCommonDTO());
            discountDetailDTO.setCreatedDate(discount.getCreatedDate());

            for (DiscountItem discountItem : discount.getItems()) {
                DiscountItemDetailDTO itemDetailDTO = new DiscountItemDetailDTO();
                itemDetailDTO.setDiscountItemId(discountItem.getId());
                itemDetailDTO.setDiscountType(discountItem.getDiscountType());
                itemDetailDTO.setRangeType(discountItem.getRangeType());
                itemDetailDTO.setApplyAllCustomers(discountItem.isApplyAllCustomers());
                itemDetailDTO.setApplyAllProducts(discountItem.isApplyAllProducts());

                if (discountItem.getProductId() != null) {
                    itemDetailDTO.setProduct(discountItem.getProduct().toProductCommonDTO());
                }

                if (discountItem.getCustomerId() != null) {
                    itemDetailDTO.setCustomer(
                            discountItem.getCustomer().toCommonBaseDTO()
                    );
                }

                for (DiscountRange discountRange : discountItem.getDiscountRanges()) {

                    DiscountRangeDetailDTO rangeDetailDTO = new DiscountRangeDetailDTO();
                    rangeDetailDTO.setId(discountRange.getId());
                    rangeDetailDTO.setFixedAmount(discountRange.getFixedAmount());
                    rangeDetailDTO.setFromAmount(discountRange.getFromAmount());
                    rangeDetailDTO.setToAmount(discountRange.getToAmount());

                    for (DiscountItemProduct discountProduct : discountRange.getDiscountProducts()) {
                        DiscountItemProductDetailDTO discountItemProductDetailDTO = new DiscountItemProductDetailDTO();
                        discountItemProductDetailDTO.setDiscountItemProductId(discountProduct.getId());
                        discountItemProductDetailDTO.setProduct(discountProduct.getProduct().toProductCommonDTO());
                        discountItemProductDetailDTO.setProductPrice(discountProduct.getProduct().getPrice());
                        discountItemProductDetailDTO.setAmount(discountProduct.getAmount());
                        discountItemProductDetailDTO.setRecommendedQty(discountProduct.getRecommendedQty());
                        discountItemProductDetailDTO.setValueType(discountProduct.getValueType());
                        discountItemProductDetailDTO.setLimit(discountProduct.getDiscountLimit());
                        rangeDetailDTO.addItemProduct(discountItemProductDetailDTO);
                    }
                    itemDetailDTO.addRanges(rangeDetailDTO);
                }
                discountDetailDTO.add(itemDetailDTO);
            }
            result.add(discountDetailDTO);
        }
        return result;
    }

}