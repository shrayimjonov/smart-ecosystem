package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.aggregation.InvalidAggregation;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.domain.stock.StockProduct;
import uz.uzkassa.smartpossupply.domain.stock.StockProductAggregation;
import uz.uzkassa.smartpossupply.domain.stock.StockProductAggregationBlock;
import uz.uzkassa.smartpossupply.domain.stock.StockProductMark;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfo;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponse;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponseResult;
import uz.uzkassa.smartpossupply.dto.stock.CheckAslBelgiDTO;
import uz.uzkassa.smartpossupply.enums.PackageType;
import uz.uzkassa.smartpossupply.enums.ProductType;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.enums.TuronMarkStatus;
import uz.uzkassa.smartpossupply.integration.AslBelgiClient;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqAslBelgiProducer;
import uz.uzkassa.smartpossupply.repository.InvalidAggregationRepository;
import uz.uzkassa.smartpossupply.repository.StockInRepository;
import uz.uzkassa.smartpossupply.repository.StockProductAggregationBlockRepository;
import uz.uzkassa.smartpossupply.repository.StockProductAggregationRepository;
import uz.uzkassa.smartpossupply.repository.StockProductMarkRepository;
import uz.uzkassa.smartpossupply.repository.StockProductRepository;
import uz.uzkassa.smartpossupply.utils.MarkUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
/**
 * This class is responsible for checking codes during stock in.
 * We should be aware of code structures before updating the code.
 * Sample code structure:
 * Aggregation->AggregationBlock->Mark.
 * Aggregation->Mark
 */
public class InvalidAggregationService extends BaseService {

    AslBelgiClient aslBelgiClient;
    InvalidAggregationRepository invalidAggregationRepository;
    StockInRepository stockInRepository;
    RabbitMqAslBelgiProducer rabbitMqAslBelgiProducer;
    StockProductAggregationRepository stockProductAggregationRepository;
    StockProductMarkRepository stockProductMarkRepository;
    StockProductAggregationBlockRepository stockProductAggregationBlockRepository;
    StockProductRepository stockProductRepository;

    @Transactional
    public void check(CheckAslBelgiDTO checkAslBelgiDTO) {
        rabbitMqAslBelgiProducer.check(checkAslBelgiDTO);
    }

    @Transactional
    public void checkQueue(CheckAslBelgiDTO checkAslBelgiDTO) {
        List<Long> stockProductIds = stockProductRepository.getIdsByStockId(checkAslBelgiDTO.getStockId());
        stockProductMarkRepository.deleteAllByStockProductIdsAndParentIsFalse(stockProductIds);
        stockProductAggregationBlockRepository.deleteAllByStockProductIdsAndParentIsFalse(stockProductIds);
        stockProductAggregationRepository.deleteAllByStockProductIdsAndParentIsFalse(stockProductIds);

        for (Long stockProductId : stockProductIds) {
            checkAslBelgiDTO.setStockProductId(stockProductId);
            rabbitMqAslBelgiProducer.checkChild(checkAslBelgiDTO);
        }
    }

    @Transactional
    public void checkItem(CheckAslBelgiDTO payload) {
        stockProductRepository.findById(payload.getStockProductId()).ifPresentOrElse(stockProduct -> {
            log.info("============================== CHECK CODES STARTED. STOCK NUMBER: {}, STOCK_PRODUCT_ID: {}, BARCODE: {} ==============================", stockProduct.getStock().getStockNumber(), stockProduct.getId(), Optional.ofNullable(stockProduct.getProduct().getBarcode()).orElse(""));
            List<InvalidAggregation> invalidAggregations = new ArrayList<>();
            Stock stock = stockProduct.getStock();
            Set<StockProductAggregation> stockProductAggregationSet = new HashSet<>();
            List<String> aggregationCodes = new ArrayList<>();
            Map<String, StockProductAggregation> stringStockProductAggregationMap = new HashMap<>();
            for (StockProductAggregation stockProductAggregation : stockProduct.getStockProductAggregations()) {
                if (stockProductAggregation.isParent()) {
                    aggregationCodes.add(stockProductAggregation.getCode());
                    stringStockProductAggregationMap.put(stockProductAggregation.getOriginalCode(), stockProductAggregation);
                }
            }

            List<String> markCodes = new ArrayList<>(stockProduct
                    .getStockProductMarks()
                    .stream()
                    .filter(StockProductMark::isParent)
                    .map(StockProductMark::getCode)
                    .toList());

            List<String> aggregationBlockCodes = new ArrayList<>(stockProduct
                    .getStockProductAggregationBlocks()
                    .stream()
                    .filter(StockProductAggregationBlock::isParent)
                    .map(StockProductAggregationBlock::getCode)
                    .toList());

            if (!CollectionUtils.isEmpty(aggregationCodes)) {
                List<StockProductAggregation> stockProductAggregations = stockProductAggregationRepository.getByTransferTypeAndCodesAndCompanyIdAndStatusNotAndStockProductIdNot(
                        TransferType.IN,
                        aggregationCodes,
                        stock.getCompanyId(),
                        StockStatus.CANCELLED,
                        stockProduct.getId()
                );
                if (!CollectionUtils.isEmpty(stockProductAggregations)) {
                    for (StockProductAggregation stockProductAggregation : stockProductAggregations) {
                        InvalidAggregation invalidAggregation = new InvalidAggregation();
                        invalidAggregation.setOriginalCode(stockProductAggregation.getOriginalCode());
                        invalidAggregation.setCode(stockProductAggregation.getCode());
                        invalidAggregation.setErrorMessage(
                                "Код уже использован в приходе " + stockProductAggregation.getStockProduct().getStock().getStockNumber()
                        );
                        invalidAggregation.setStockProductId(stockProduct.getId());
                        invalidAggregation.setStockId(stock.getId());
                        invalidAggregations.add(invalidAggregation);
                        aggregationCodes.remove(stockProductAggregation.getCode());
                    }
                }
            }

            if (!CollectionUtils.isEmpty(markCodes)) {
                List<StockProductMark> stockProductMarks = stockProductMarkRepository.getByTransferTypeAndCodesAndCompanyIdAndStatusNotAndStockProductIdNot(
                        TransferType.IN,
                        markCodes,
                        stock.getCompanyId(),
                        StockStatus.CANCELLED,
                        stockProduct.getId()
                );
                if (!CollectionUtils.isEmpty(stockProductMarks)) {
                    for (StockProductMark stockProductMark : stockProductMarks) {
                        InvalidAggregation invalidAggregation = new InvalidAggregation();
                        invalidAggregation.setOriginalCode(stockProductMark.getOriginalCode());
                        invalidAggregation.setCode(stockProductMark.getCode());
                        invalidAggregation.setErrorMessage(
                                "Код уже использован в приходе " + stockProductMark.getStockProduct().getStock().getStockNumber()
                        );
                        invalidAggregation.setStockProductId(stockProduct.getId());
                        invalidAggregation.setStockId(stock.getId());
                        invalidAggregations.add(invalidAggregation);
                        markCodes.remove(stockProductMark.getCode());
                    }
                }
            }

            if (!CollectionUtils.isEmpty(aggregationBlockCodes)) {
                List<StockProductAggregationBlock> stockProductAggregationBlocks = stockProductAggregationBlockRepository.getByTransferTypeAndCodesAndCompanyIdAndStatusNotAndStockProductIdNot(
                        TransferType.IN,
                        aggregationBlockCodes,
                        stock.getCompanyId(),
                        StockStatus.CANCELLED,
                        stockProduct.getId()
                );
                if (!CollectionUtils.isEmpty(stockProductAggregationBlocks)) {
                    for (StockProductAggregationBlock stockProductAggregationBlock : stockProductAggregationBlocks) {
                        InvalidAggregation invalidAggregation = new InvalidAggregation();
                        invalidAggregation.setOriginalCode(stockProductAggregationBlock.getOriginalCode());
                        invalidAggregation.setCode(stockProductAggregationBlock.getCode());
                        invalidAggregation.setErrorMessage(
                                "Код уже использован в приходе " + stockProductAggregationBlock.getStockProduct().getStock().getStockNumber()
                        );
                        invalidAggregation.setStockProductId(stockProduct.getId());
                        invalidAggregation.setStockId(stock.getId());
                        invalidAggregations.add(invalidAggregation);
                        aggregationBlockCodes.remove(stockProductAggregationBlock.getCode());
                    }
                }
            }
            boolean hasWarning = false;
            boolean isChecked = true;
            if (!CollectionUtils.isEmpty(aggregationCodes) || !CollectionUtils.isEmpty(markCodes) || !CollectionUtils.isEmpty(aggregationBlockCodes)) {

                if (payload.isXtraceEnabled()) {
                    try {
                        CisInfoResponseResult cisInfoResponseResult;
                        if (payload.isFromFactura()) {
                            cisInfoResponseResult = aslBelgiClient.checkPrivateInfoForStockInFactura(
                                    payload.getToken(),
                                    aggregationCodes,
                                    markCodes,
                                    aggregationBlockCodes,
                                    stockProduct.getProduct().getProductType()
                            );
                        } else {
                            cisInfoResponseResult = aslBelgiClient.checkPrivateInfo(
                                    payload.getToken(),
                                    aggregationCodes,
                                    markCodes,
                                    aggregationBlockCodes,
                                    stockProduct.getProduct().getProductType()
                            );
                        }

                        if (cisInfoResponseResult != null && !CollectionUtils.isEmpty(cisInfoResponseResult.getCisInfoResponses())) {
                            for (CisInfoResponse cisInfoResponse : cisInfoResponseResult.getCisInfoResponses()) {
                                CisInfo cisInfo = cisInfoResponse.getCisInfo();
                                StockProductAggregation stockProductAggregation = stringStockProductAggregationMap.get(cisInfo.getOriginalCode());
                                if (stockProductAggregation != null) {
                                    stockProductAggregation.setCode(cisInfo.getCode());
                                    stockProductAggregationSet.add(stockProductAggregation);
                                }

                                if (
                                        StringUtils.isNotEmpty(cisInfo.getErrorMessage()) ||
                                                !(
                                                        TuronMarkStatus.APPLIED.equals(cisInfo.getStatus()) ||
                                                                TuronMarkStatus.INTRODUCED.equals(cisInfo.getStatus())
                                                )
                                ) {
                                    InvalidAggregation invalidAggregation = new InvalidAggregation();
                                    invalidAggregation.setOriginalCode(cisInfo.getOriginalCode());
                                    invalidAggregation.setCode(cisInfo.getRequestedCis());
                                    invalidAggregation.setErrorMessage(cisInfo.getErrorMessage());
                                    invalidAggregation.setStockProductId(stockProduct.getId());
                                    invalidAggregation.setStockId(stock.getId());
                                    invalidAggregations.add(invalidAggregation);
                                    isChecked = false;
                                } else if (StringUtils.isNotEmpty(cisInfo.getOwnerInn()) && !(stock.getCompany().getTin().equals(cisInfo.getOwnerInn()) || stock.getSupplier().getTin().equals(cisInfo.getOwnerInn()))) {
                                    hasWarning = true;
                                }
                            }
                            this.writeCodes(payload.getToken(), cisInfoResponseResult, stockProduct, aggregationCodes, aggregationBlockCodes, markCodes, payload.isXtraceEnabled());
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        stock.setStatus(StockStatus.CHECKING_ERROR);
                        isChecked = false;
                    }
                }
            }
            if (!CollectionUtils.isEmpty(stockProductAggregationSet)) {
                stockProductAggregationRepository.saveAll(stockProductAggregationSet);
            }
            if (!CollectionUtils.isEmpty(invalidAggregations)) {
                invalidAggregationRepository.saveAll(invalidAggregations);
                if (StockStatus.CHECKING_ERROR != stock.getStatus()) {
                    stock.setStatus(StockStatus.CHECKING_ERROR);
                    stockInRepository.save(stock);
                }
            } else {
                BigDecimal qty = BigDecimal.ZERO;
                qty = qty.add(stockProductAggregationRepository.getCount(stockProduct.getId()));
                qty = qty.add(stockProductAggregationBlockRepository.getCount(stockProduct.getId()));
                qty = qty.add(stockProductMarkRepository.getCount(stockProduct.getId()));
                stockProduct.setTuronQty(qty);
                stockProduct.setChecked(isChecked);
                stockProduct.setHasWarning(hasWarning);
                stockProductRepository.save(stockProduct);
            }
        }, () -> {
            throw validationService.entityNotFoundExceptionThrow(StockProduct.class.getSimpleName(), "id", payload.getStockProductId()).get();
        });
    }

    private List<String> getCisInfoCodes(CisInfo cisInfo) {
        if (cisInfo.getCodes() != null && !cisInfo.getCodes().isEmpty()) {
            return new ArrayList<>(cisInfo.getCodes().keySet());
        } else if (!CollectionUtils.isEmpty(cisInfo.getChild())) {
            return cisInfo.getChild();
        }
        return null;
    }

    private CisInfoResponseResult checkCodes(String token, List<String> codes, ProductType productType, PackageType packageType, boolean isXtraceEnabled) {
        if (isXtraceEnabled) {
            try {
                CisInfoResponseResult cisInfoResponseResult = null;
                switch (packageType) {
                    case PALLET, LEVEL3, BOX, LEVEL2, BOX_LV_1, BOX_LV_2 ->
                            cisInfoResponseResult = aslBelgiClient.checkPrivateInfo(token, codes, null, null, productType);
                    case BLOCK, LEVEL1, GROUP ->
                            cisInfoResponseResult = aslBelgiClient.checkPrivateInfo(token, null, null, codes, productType);
                    case UNIT, BOTTLE ->
                            cisInfoResponseResult = aslBelgiClient.checkPrivateInfo(token, null, codes, null, productType);
                }
                return cisInfoResponseResult;
            } catch (Exception e) {
                log.info("Xtrace private info error: {}", e.getMessage(), e);
            }
        } else {
            try {
                CisInfoResponseResult cisInfoResponseResult = null;
                switch (packageType) {
                    case PALLET:
                    case LEVEL3:
                    case BOX:
                    case LEVEL2:
                        cisInfoResponseResult = aslBelgiClient.checkCisesInfo(token, codes, null, null, productType);
                        break;
                    case BLOCK:
                    case LEVEL1:
                        cisInfoResponseResult = aslBelgiClient.checkCisesInfo(token, null, null, codes, productType);
                        break;
                    case UNIT:
                    case BOTTLE:
                        cisInfoResponseResult = aslBelgiClient.checkCisesInfo(token, null, codes, null, productType);
                    default:
                }
                if (cisInfoResponseResult != null && !CollectionUtils.isEmpty(cisInfoResponseResult.getCisInfoResponses())) {
                    for (CisInfoResponse cisInfoResponse : cisInfoResponseResult.getCisInfoResponses()) {
                        if (cisInfoResponse.getCisInfo() != null) {
                            codes.remove(cisInfoResponse.getCisInfo().getOriginalCode());
                        }
                    }
                    return cisInfoResponseResult;
                }
            } catch (Exception e) {
                log.error("CisInfo error: {}", e.getMessage());
            }
            if (!codes.isEmpty()) {
                try {
                    CisInfoResponseResult cisInfoResponseResult = null;
                    switch (packageType) {
                        case PALLET:
                        case LEVEL3:
                        case BOX:
                        case LEVEL2:
                            cisInfoResponseResult = aslBelgiClient.checkAggregatedPublicList(token, codes, null, null, productType);
                            break;
                        case BLOCK:
                        case LEVEL1:
                            cisInfoResponseResult = aslBelgiClient.checkAggregatedPublicList(token, null, null, codes, productType);
                            break;
                        case UNIT:
                        case BOTTLE:
                            cisInfoResponseResult = aslBelgiClient.checkAggregatedPublicList(token, null, codes, null, productType);
                        default:
                    }
                    return cisInfoResponseResult;
                } catch (Exception e) {
                    log.info("Public info error: {}", e.getMessage(), e);
                }
            }
        }
        return null;
    }

    private void writeCodes(String token, CisInfoResponseResult cisInfoResponseResult, StockProduct stockProduct, List<String> aggregationCodes, List<String> aggregationBlockCodes, List<String> markCodes, boolean isXtraceEnabled) {
        ProductType productType = stockProduct.getProduct().getProductType();
        Long productId = stockProduct.getProductId();
        if (cisInfoResponseResult != null && !CollectionUtils.isEmpty(cisInfoResponseResult.getCisInfoResponses())) {
            List<StockProductMark> marks = new ArrayList<>();
            List<StockProductAggregationBlock> blocks = new ArrayList<>();
            List<StockProductAggregation> boxes = new ArrayList<>();
            List<StockProductAggregation> pallets = new ArrayList<>();
            boolean isUpdated = false;
            for (CisInfoResponse cisInfoResponse : cisInfoResponseResult.getCisInfoResponses()) {
                CisInfo cisInfo = cisInfoResponse.getCisInfo();
                if (cisInfo != null && (TuronMarkStatus.APPLIED.equals(cisInfo.getStatus()) || TuronMarkStatus.INTRODUCED.equals(cisInfo.getStatus()))) {
                    isUpdated = true;
                    PackageType packageType = MarkUtils.findPackageType(cisInfo);
                    switch (packageType) {
                        case PALLET, LEVEL3, BOX_LV_2 -> {
                            int palletQty = 0;
                            StockProductAggregation pallet = stockProductAggregationRepository.findFirstByCodeAndStockProductIdAndDeletedIsFalse(Optional.ofNullable(cisInfo.getRequestedCis()).orElse(cisInfo.getOriginalCode()), stockProduct.getId()).orElse(null);
                            if (pallet == null) {
                                pallet = new StockProductAggregation();
                                pallet.setProductId(productId);
                                pallet.setCode(cisInfo.getCode());
                                pallet.setOriginalCode(cisInfo.getOriginalCode());
                                pallet.setStockProductId(stockProduct.getId());
                                pallet.setTransferType(TransferType.IN);
                                pallet.setParent(false);
                            }
                            pallet.setOwnerName(cisInfo.getOwnerName());
                            pallet.setOwnerTin(cisInfo.getOwnerInn());
                            pallet.setUsed(false);
                            pallet.setStatus(cisInfo.getStatus());
                            pallet.setPackageType(PackageType.PALLET);
                            List<String> childCodes = getCisInfoCodes(cisInfo);
                            if (CollectionUtils.isNotEmpty(childCodes)) {
                                PackageType childPackageType = null;
                                String code = childCodes.getFirst();
                                if (StringUtils.isNotEmpty(code)) {
                                    childPackageType = MarkUtils.findPackageTypeByCode(code, productType);
                                }
                                if (!PackageType.UNIT.equals(childPackageType)) {
                                    CisInfoResponseResult childCisInfoResponseResult = checkCodes(token, childCodes, productType, childPackageType, isXtraceEnabled);
                                    if (childCisInfoResponseResult != null && CollectionUtils.isNotEmpty(childCisInfoResponseResult.getCisInfoResponses())) {
                                        if (childCisInfoResponseResult.getCisInfoResponses().getFirst() != null && childCisInfoResponseResult.getCisInfoResponses().getFirst().getCisInfo() != null && TuronMarkStatus.isSuccess(childCisInfoResponseResult.getCisInfoResponses().getFirst().getCisInfo().getStatus())) {
                                            childPackageType = MarkUtils.findPackageType(childCisInfoResponseResult.getCisInfoResponses().getFirst().getCisInfo());
                                            switch (childPackageType) {
                                                case BOX, LEVEL2, BOX_LV_1 -> {
                                                    for (CisInfoResponse childCisInfoResponse : childCisInfoResponseResult.getCisInfoResponses()) {
                                                        CisInfo childCisInfo = childCisInfoResponse.getCisInfo();
                                                        if (childCisInfo != null && TuronMarkStatus.isSuccess(childCisInfo.getStatus())) {
                                                            int boxQty = 0;
                                                            StockProductAggregation box = new StockProductAggregation();
                                                            box.setProductId(productId);
                                                            box.setCode(childCisInfo.getCode());
                                                            box.setOriginalCode(Optional.ofNullable(childCisInfo.getOriginalCode()).orElse(childCisInfo.getRequestedCis()));
                                                            box.setStockProductId(stockProduct.getId());
                                                            box.setTransferType(TransferType.IN);
                                                            box.setParent(false);
                                                            box.setPallet(pallet);
                                                            box.setOwnerName(childCisInfo.getOwnerName());
                                                            box.setOwnerTin(childCisInfo.getOwnerInn());
                                                            box.setUsed(false);
                                                            box.setStatus(childCisInfo.getStatus());
                                                            box.setPackageType(PackageType.BOX);
                                                            List<String> boxChildCodes = getCisInfoCodes(childCisInfo);
                                                            if (CollectionUtils.isNotEmpty(boxChildCodes)) {
                                                                String childCode = boxChildCodes.getFirst();
                                                                if (StringUtils.isNotEmpty(childCode) && PackageType.BLOCK.equals(MarkUtils.findPackageTypeByCode(code, productType))) {
                                                                    CisInfoResponseResult boxChildCisInfoResponseResult = checkCodes(token, boxChildCodes, productType, PackageType.BLOCK, isXtraceEnabled);
                                                                    if (boxChildCisInfoResponseResult.getCisInfoResponses().getFirst() != null && boxChildCisInfoResponseResult.getCisInfoResponses().getFirst().getCisInfo() != null && TuronMarkStatus.isSuccess(boxChildCisInfoResponseResult.getCisInfoResponses().getFirst().getCisInfo().getStatus())) {
                                                                        if (boxChildCisInfoResponseResult != null && CollectionUtils.isNotEmpty(boxChildCisInfoResponseResult.getCisInfoResponses())) {
                                                                            for (CisInfoResponse boxChildCisInfoResponse : boxChildCisInfoResponseResult.getCisInfoResponses()) {
                                                                                int blockQty = 0;
                                                                                CisInfo boxChildCisInfo = boxChildCisInfoResponse.getCisInfo();
                                                                                StockProductAggregationBlock block = new StockProductAggregationBlock();
                                                                                block.setProductId(productId);
                                                                                block.setCode(boxChildCisInfo.getCode());
                                                                                block.setOriginalCode(boxChildCisInfo.getOriginalCode());
                                                                                block.setStockProductId(stockProduct.getId());
                                                                                block.setTransferType(TransferType.IN);
                                                                                block.setParent(false);
                                                                                block.setBox(box);
                                                                                block.setOwnerName(boxChildCisInfo.getOwnerName());
                                                                                block.setOwnerTin(boxChildCisInfo.getOwnerInn());
                                                                                block.setUsed(false);
                                                                                block.setStatus(boxChildCisInfo.getStatus());
                                                                                block.setPackageType(PackageType.BLOCK);
                                                                                List<String> blockChildCodes = getCisInfoCodes(boxChildCisInfo);
                                                                                if (CollectionUtils.isNotEmpty(blockChildCodes)) {
                                                                                    blockQty += blockChildCodes.size();
                                                                                    for (String blockChildCode : blockChildCodes) {
                                                                                        StockProductMark unit = new StockProductMark();
                                                                                        unit.setProductId(productId);
                                                                                        unit.setCode(blockChildCode);
                                                                                        unit.setOriginalCode(blockChildCode);
                                                                                        unit.setStockProductId(stockProduct.getId());
                                                                                        unit.setTransferType(TransferType.IN);
                                                                                        unit.setParent(false);
                                                                                        unit.setBlock(block);
                                                                                        unit.setOwnerName(cisInfo.getOwnerName());
                                                                                        unit.setOwnerTin(cisInfo.getOwnerInn());
                                                                                        unit.setUsed(false);
                                                                                        unit.setStatus(cisInfo.getStatus());
                                                                                        unit.setPackageType(PackageType.UNIT);
                                                                                        unit.setQty(BigDecimal.ONE);
                                                                                        marks.add(unit);
                                                                                    }
                                                                                }
                                                                                block.setQty(BigDecimal.valueOf(blockQty));
                                                                                blocks.add(block);
                                                                                boxQty += blockQty;
                                                                            }
                                                                        }
                                                                    }
                                                                } else {
                                                                    for (String boxChild : boxChildCodes) {
                                                                        StockProductMark unit = new StockProductMark();
                                                                        unit.setProductId(productId);
                                                                        unit.setCode(boxChild);
                                                                        unit.setOriginalCode(boxChild);
                                                                        unit.setStockProductId(stockProduct.getId());
                                                                        unit.setTransferType(TransferType.IN);
                                                                        unit.setParent(false);
                                                                        unit.setBox(box);
                                                                        unit.setOwnerName(childCisInfo.getOwnerName());
                                                                        unit.setOwnerTin(childCisInfo.getOwnerInn());
                                                                        unit.setUsed(false);
                                                                        unit.setStatus(childCisInfo.getStatus());
                                                                        unit.setPackageType(PackageType.UNIT);
                                                                        unit.setQty(BigDecimal.ONE);
                                                                        marks.add(unit);
                                                                        boxQty += 1;
                                                                    }
                                                                }
                                                            }
                                                            box.setQty(BigDecimal.valueOf(boxQty));
                                                            boxes.add(box);
                                                            palletQty += boxQty;
                                                        }
                                                    }
                                                }
                                                case BLOCK, LEVEL1, GROUP -> {
                                                    for (CisInfoResponse childCisInfoResponse : childCisInfoResponseResult.getCisInfoResponses()) {
                                                        CisInfo childCisInfo = childCisInfoResponse.getCisInfo();
                                                        if (childCisInfo != null) {
                                                            int blockQty = 0;
                                                            StockProductAggregationBlock block = new StockProductAggregationBlock();
                                                            block.setProductId(productId);
                                                            block.setCode(childCisInfo.getCode());
                                                            block.setOriginalCode(childCisInfo.getOriginalCode());
                                                            block.setStockProductId(stockProduct.getId());
                                                            block.setTransferType(TransferType.IN);
                                                            block.setParent(false);
                                                            block.setPallet(pallet);
                                                            block.setOwnerName(childCisInfo.getOwnerName());
                                                            block.setOwnerTin(childCisInfo.getOwnerInn());
                                                            block.setUsed(false);
                                                            block.setStatus(childCisInfo.getStatus());
                                                            block.setPackageType(PackageType.BLOCK);
                                                            List<String> blockChildCodes = getCisInfoCodes(childCisInfo);
                                                            if (CollectionUtils.isNotEmpty(blockChildCodes)) {
                                                                blockQty += blockChildCodes.size();
                                                                for (String blockChildCode : blockChildCodes) {
                                                                    StockProductMark unit = new StockProductMark();
                                                                    unit.setProductId(productId);
                                                                    unit.setCode(blockChildCode);
                                                                    unit.setOriginalCode(blockChildCode);
                                                                    unit.setStockProductId(stockProduct.getId());
                                                                    unit.setTransferType(TransferType.IN);
                                                                    unit.setParent(false);
                                                                    unit.setBlock(block);
                                                                    unit.setOwnerName(cisInfo.getOwnerName());
                                                                    unit.setOwnerTin(cisInfo.getOwnerInn());
                                                                    unit.setUsed(false);
                                                                    unit.setStatus(cisInfo.getStatus());
                                                                    unit.setPackageType(PackageType.UNIT);
                                                                    unit.setQty(BigDecimal.ONE);
                                                                    marks.add(unit);
                                                                }
                                                            }
                                                            block.setQty(BigDecimal.valueOf(blockQty));
                                                            blocks.add(block);
                                                            palletQty += blockQty;
                                                        }
                                                    }
                                                }
                                                case UNIT, BOTTLE -> {
                                                    for (CisInfoResponse unitCisInfoResponse : childCisInfoResponseResult.getCisInfoResponses()) {
                                                        if (unitCisInfoResponse != null && unitCisInfoResponse.getCisInfo() != null) {
                                                            CisInfo childCisInfo = unitCisInfoResponse.getCisInfo();
                                                            StockProductMark unit = new StockProductMark();
                                                            unit.setProductId(productId);
                                                            unit.setCode(childCisInfo.getCode());
                                                            unit.setOriginalCode(childCisInfo.getOriginalCode());
                                                            unit.setStockProductId(stockProduct.getId());
                                                            unit.setTransferType(TransferType.IN);
                                                            unit.setParent(false);
                                                            unit.setPallet(pallet);
                                                            unit.setOwnerName(cisInfo.getOwnerName());
                                                            unit.setOwnerTin(cisInfo.getOwnerInn());
                                                            unit.setUsed(false);
                                                            unit.setStatus(cisInfo.getStatus());
                                                            unit.setPackageType(PackageType.UNIT);
                                                            unit.setQty(BigDecimal.ONE);
                                                            marks.add(unit);
                                                            palletQty += 1;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    for (String palletChild : childCodes) {
                                        StockProductMark unit = new StockProductMark();
                                        unit.setProductId(productId);
                                        unit.setCode(palletChild);
                                        unit.setOriginalCode(palletChild);
                                        unit.setStockProductId(stockProduct.getId());
                                        unit.setTransferType(TransferType.IN);
                                        unit.setParent(false);
                                        unit.setPallet(pallet);
                                        unit.setOwnerName(cisInfo.getOwnerName());
                                        unit.setOwnerTin(cisInfo.getOwnerInn());
                                        unit.setUsed(false);
                                        unit.setStatus(cisInfo.getStatus());
                                        unit.setPackageType(PackageType.UNIT);
                                        unit.setQty(BigDecimal.ONE);
                                        marks.add(unit);
                                        palletQty += 1;
                                    }
                                }
                                pallet.setQty(BigDecimal.valueOf(palletQty));
                                pallets.add(pallet);
                            }
                        }
                        case BOX, LEVEL2, BOX_LV_1 -> {
                            StockProductAggregation box = stockProductAggregationRepository.findFirstByCodeAndStockProductIdAndDeletedIsFalse(Optional.ofNullable(cisInfo.getRequestedCis()).orElse(cisInfo.getOriginalCode()), stockProduct.getId()).orElseGet(StockProductAggregation::new);
                            int boxQty = 0;
                            if (box.getId() == null) {
                                box = new StockProductAggregation();
                                box.setProductId(productId);
                                box.setCode(cisInfo.getCode());
                                box.setOriginalCode(Optional.ofNullable(cisInfo.getOriginalCode()).orElse(cisInfo.getRequestedCis()));
                                box.setStockProductId(stockProduct.getId());
                                box.setTransferType(TransferType.IN);
                                box.setParent(false);
                            }
                            box.setOwnerName(cisInfo.getOwnerName());
                            box.setOwnerTin(cisInfo.getOwnerInn());
                            box.setUsed(false);
                            box.setStatus(cisInfo.getStatus());
                            box.setPackageType(PackageType.BOX);
                            List<String> boxChildCodes = getCisInfoCodes(cisInfo);
                            if (CollectionUtils.isNotEmpty(boxChildCodes)) {
                                String code = boxChildCodes.getFirst();
                                if (StringUtils.isNotEmpty(code) && PackageType.isBlock(MarkUtils.findPackageTypeByCode(code, productType))) {
                                    CisInfoResponseResult boxChildCisInfoResponseResult = checkCodes(token, boxChildCodes, productType, PackageType.BLOCK, isXtraceEnabled);
                                    if (boxChildCisInfoResponseResult != null) {
                                        for (CisInfoResponse boxChildCisInfoResponse : boxChildCisInfoResponseResult.getCisInfoResponses()) {
                                            if (boxChildCisInfoResponse != null && boxChildCisInfoResponse.getCisInfo() != null) {
                                                int blockQty = 0;
                                                CisInfo boxChildCisInfo = boxChildCisInfoResponse.getCisInfo();
                                                StockProductAggregationBlock block = new StockProductAggregationBlock();
                                                block.setProductId(productId);
                                                block.setCode(boxChildCisInfo.getCode());
                                                block.setOriginalCode(boxChildCisInfo.getOriginalCode());
                                                block.setStockProductId(stockProduct.getId());
                                                block.setTransferType(TransferType.IN);
                                                block.setParent(false);
                                                block.setBox(box);
                                                block.setOwnerName(boxChildCisInfo.getOwnerName());
                                                block.setOwnerTin(boxChildCisInfo.getOwnerInn());
                                                block.setUsed(false);
                                                block.setStatus(boxChildCisInfo.getStatus());
                                                block.setPackageType(PackageType.BLOCK);
                                                List<String> blockChildCodes = getCisInfoCodes(boxChildCisInfo);
                                                if (CollectionUtils.isNotEmpty(blockChildCodes)) {
                                                    blockQty += blockChildCodes.size();
                                                    for (String blockChildCode : blockChildCodes) {
                                                        StockProductMark unit = new StockProductMark();
                                                        unit.setProductId(productId);
                                                        unit.setCode(blockChildCode);
                                                        unit.setOriginalCode(blockChildCode);
                                                        unit.setStockProductId(stockProduct.getId());
                                                        unit.setTransferType(TransferType.IN);
                                                        unit.setParent(false);
                                                        unit.setBlock(block);
                                                        unit.setOwnerName(cisInfo.getOwnerName());
                                                        unit.setOwnerTin(cisInfo.getOwnerInn());
                                                        unit.setUsed(false);
                                                        unit.setStatus(cisInfo.getStatus());
                                                        unit.setPackageType(PackageType.UNIT);
                                                        unit.setQty(BigDecimal.ONE);
                                                        marks.add(unit);
                                                    }
                                                }
                                                block.setQty(BigDecimal.valueOf(blockQty));
                                                blocks.add(block);
                                                boxQty += blockQty;
                                            }
                                        }
                                    }
                                } else {
                                    for (String boxChildCode : boxChildCodes) {
                                        StockProductMark unit = new StockProductMark();
                                        unit.setProductId(productId);
                                        unit.setCode(boxChildCode);
                                        unit.setOriginalCode(boxChildCode);
                                        unit.setStockProductId(stockProduct.getId());
                                        unit.setTransferType(TransferType.IN);
                                        unit.setParent(false);
                                        unit.setBox(box);
                                        unit.setOwnerName(cisInfo.getOwnerName());
                                        unit.setOwnerTin(cisInfo.getOwnerInn());
                                        unit.setUsed(false);
                                        unit.setStatus(cisInfo.getStatus());
                                        unit.setPackageType(PackageType.UNIT);
                                        unit.setQty(BigDecimal.ONE);
                                        marks.add(unit);
                                        boxQty += 1;

                                    }
                                }
                                box.setQty(BigDecimal.valueOf(boxQty));
                                boxes.add(box);
                            }
                        }
                        case BLOCK, LEVEL1, GROUP -> {
                            StockProductAggregationBlock block = stockProductAggregationBlockRepository.findFirstByCodeAndStockProductIdAndDeletedIsFalse(Optional.ofNullable(cisInfo.getRequestedCis()).orElse(cisInfo.getOriginalCode()), stockProduct.getId()).orElse(null);
                            int blockQty = 0;
                            if (block == null) {
                                block = new StockProductAggregationBlock();
                                block.setProductId(productId);
                                block.setOriginalCode(cisInfo.getOriginalCode());
                                block.setStockProductId(stockProduct.getId());
                                block.setTransferType(TransferType.IN);
                                block.setParent(false);
                            }
                            block.setCode(cisInfo.getCode());
                            block.setOwnerName(cisInfo.getOwnerName());
                            block.setOwnerTin(cisInfo.getOwnerInn());
                            block.setUsed(false);
                            block.setStatus(cisInfo.getStatus());
                            block.setPackageType(PackageType.BLOCK);
                            List<String> blockChildCodes = getCisInfoCodes(cisInfo);
                            if (!CollectionUtils.isEmpty(blockChildCodes)) {
                                blockQty += blockChildCodes.size();
                                for (String boxChildCode : blockChildCodes) {
                                    StockProductMark unit = new StockProductMark();
                                    unit.setProductId(productId);
                                    unit.setCode(boxChildCode);
                                    unit.setOriginalCode(boxChildCode);
                                    unit.setStockProductId(stockProduct.getId());
                                    unit.setTransferType(TransferType.IN);
                                    unit.setParent(false);
                                    unit.setBlock(block);
                                    unit.setOwnerName(cisInfo.getOwnerName());
                                    unit.setOwnerTin(cisInfo.getOwnerInn());
                                    unit.setUsed(false);
                                    unit.setStatus(cisInfo.getStatus());
                                    unit.setPackageType(PackageType.UNIT);
                                    unit.setQty(BigDecimal.ONE);
                                    marks.add(unit);
                                }
                                block.setQty(BigDecimal.valueOf(blockQty));
                                blocks.add(block);
                            }
                        }
                        case UNIT, BOTTLE -> {
                            StockProductMark unit = stockProductMarkRepository.findFirstByCodeAndStockProductIdAndDeletedIsFalse(Optional.ofNullable(cisInfo.getRequestedCis()).orElse(cisInfo.getOriginalCode()), stockProduct.getId()).orElse(null);
                            if (unit == null) {
                                unit = new StockProductMark();
                                unit.setProductId(productId);
                                unit.setCode(cisInfo.getCode());
                                unit.setOriginalCode(cisInfo.getOriginalCode());
                                unit.setStockProductId(stockProduct.getId());
                                unit.setTransferType(TransferType.IN);
                                unit.setParent(false);
                            }
                            unit.setOwnerName(cisInfo.getOwnerName());
                            unit.setOwnerTin(cisInfo.getOwnerInn());
                            unit.setUsed(false);
                            unit.setStatus(cisInfo.getStatus());
                            unit.setPackageType(PackageType.UNIT);
                            unit.setQty(BigDecimal.ONE);
                            marks.add(unit);
                        }
                        default -> {
                            return;
                        }
                    }
                    if (!CollectionUtils.isEmpty(aggregationCodes)) {
                        aggregationCodes.remove(cisInfo.getOriginalCode());
                    }
                    if (!CollectionUtils.isEmpty(aggregationBlockCodes)) {
                        aggregationBlockCodes.remove(cisInfo.getOriginalCode());
                    }
                    if (!CollectionUtils.isEmpty(markCodes)) {
                        markCodes.remove(cisInfo.getOriginalCode());
                    }
                }
            }
            if (isUpdated) {
                if (CollectionUtils.isNotEmpty(pallets)) {
                    stockProductAggregationRepository.saveAllAndFlush(pallets);
                }
                if (CollectionUtils.isNotEmpty(boxes)) {
                    stockProductAggregationRepository.saveAllAndFlush(boxes);
                }
                if (CollectionUtils.isNotEmpty(blocks)) {
                    stockProductAggregationBlockRepository.saveAllAndFlush(blocks);
                }
                if (CollectionUtils.isNotEmpty(marks)) {
                    stockProductMarkRepository.saveAllAndFlush(marks);
                }
            }
        }
    }
}
