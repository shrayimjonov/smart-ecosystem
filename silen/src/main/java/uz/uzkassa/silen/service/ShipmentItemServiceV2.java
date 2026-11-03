package uz.uzkassa.silen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.*;
import uz.uzkassa.silen.domain.mongo.Block;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.domain.mongo.ShipmentMark;
import uz.uzkassa.silen.domain.redis.RedisShipmentItem;
import uz.uzkassa.silen.dto.warehouse.ShipmentCode;
import uz.uzkassa.silen.dto.warehouse.ShipmentItemCodeV2DTO;
import uz.uzkassa.silen.dto.warehouse.ShipmentItemDTO;
import uz.uzkassa.silen.dto.warehouse.ShipmentItemWithCodes;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.enumeration.ProductGroup;
import uz.uzkassa.silen.enumeration.ShipmentStatus;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.exceptions.ErrorConstants;
import uz.uzkassa.silen.integration.AsilBelgisiClient;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfo;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfoResponse;
import uz.uzkassa.silen.repository.mongo.BlockRepository;
import uz.uzkassa.silen.repository.mongo.MarkRepository;
import uz.uzkassa.silen.repository.mongo.ShipmentMarkRepository;
import uz.uzkassa.silen.repository.pg.AggregationRepository;
import uz.uzkassa.silen.repository.pg.ProductRepository;
import uz.uzkassa.silen.repository.pg.ShipmentItemRepository;
import uz.uzkassa.silen.repository.pg.ShipmentRepository;
import uz.uzkassa.silen.repository.redis.RedisShipmentCodeRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static uz.uzkassa.silen.utils.Utils.*;

/**
 * Service Implementation for managing {@link ShipmentItem}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShipmentItemServiceV2 extends BaseService {
    private final MarkRepository markRepository;
    private final ShipmentRepository shipmentRepository;
    private final AggregationRepository aggregationRepository;
    private final ShipmentMarkRepository shipmentMarkRepository;
    private final RedisShipmentCodeRepository shipmentCodeRepository;
    private final AsilBelgisiClient asilBelgisiClient;
    private final ShipmentItemRepository shipmentItemRepository;
    private final BlockRepository blockRepository;
    private final ProductRepository productRepository;

    @Transactional
    public ShipmentItemDTO addMark(final ShipmentItemCodeV2DTO dto) {
        final Shipment shipment = shipmentRepository.findById(dto.getShipmentId()).orElseThrow(notFound());
        if (ShipmentStatus.CLOSED.equals(shipment.getStatus())) {
            throw badRequest("Отгрузка завершена").get();
        }
        validateCountLimit(dto.getShipmentId());
        final ShipmentItemWithCodes result = validateCode(dto);
        final RedisShipmentItem item = result.getShipmentItem();

        final Set<String> codes = result.getCodes();
        if (CollectionUtils.isNotEmpty(codes)) {
            saveMarks(codes, dto.getCode());
        }
        updateQuantity(item, result, true);
        log.debug("Before save shipment item: {}", item);
        shipmentCodeRepository.save(item);
        saveShipmentMark(dto.getCode(), item.getId(), result.getUnit());

        return item.toDto();
    }

    /**
     * checking limit of codes for once shipment
     *
     * @param shipmentId default = 5000
     */
    private void validateCountLimit(final String shipmentId) {
        BigDecimal existsCodesCount = BigDecimal.ONE;
        final List<RedisShipmentItem> list = shipmentCodeRepository.findAllByShipmentId(shipmentId);
        for (final RedisShipmentItem redisShipmentItem : list) {
            for (final ShipmentCode shipmentCode : redisShipmentItem.getTypes()) {
                existsCodesCount = existsCodesCount.add(BigDecimal.valueOf(shipmentCode.getCount())); //Todo remove nested loop
            }
        }
        log.debug("Added to shipment codes count is {}", existsCodesCount);
        if (existsCodesCount.compareTo(BigDecimal.valueOf(5000)) == 0) {
            throw new BadRequestException("В одном отгрузке не должно быть больше 5000 кодов");
        }
    }

    public ShipmentItemWithCodes validateCode(final ShipmentItemCodeV2DTO dto) {
        final String code = dto.getCode();
        final boolean isMark = isMark(code);
        final boolean isAggregation = isAggregation(code);
        if (!(isMark || isAggregation)) {
            throw badRequest("Код не соответствует стандартному формату").get();
        }
        final String cleanCode = clearPrintCode(code);
        if (shipmentMarkRepository.existsShipmentMarkByCode(cleanCode)) {
            throw new BadRequestException(isMark ? "Код маркировки уже отгружен" : "Код аггрегации уже отгружен", isMark ? ErrorConstants.MARK_USED_ERROR_CODE : ErrorConstants.AGGREGATION_USED_ERROR_CODE, cleanCode);
        }

        final Organization organization = getCurrentOrganization();
        final ProductGroup pg = organization.getProductGroup();
        final ShipmentItemWithCodes result = new ShipmentItemWithCodes();

        if (!isMark) {
            final Aggregation aggregation = aggregationRepository.findFirstByCode(cleanCode).orElseThrow(notFound("Код аггрегации не найден"));
            if (aggregation.getStatus().checkStatusContainNotAbleToShip()) {
                throw new BadRequestException("Код не может быть отгружен. \nКод статус " + aggregation.getStatus().getText());
            }
            if (aggregation.getParentCode() != null) {
                if (Arrays.asList(ProductGroup.Beer, ProductGroup.Tobacco, ProductGroup.Appliances, ProductGroup.Pharma).contains(pg)) {
                    if (shipmentMarkRepository.existsShipmentMarkByCode(aggregation.getParentCode())) {
                        throw new BadRequestException("Код аггрегации уже отгружен", ErrorConstants.AGGREGATION_USED_ERROR_CODE, cleanCode);
                    } else {
                        aggregationRepository.findFirstByCode(aggregation.getParentCode()).ifPresent(parent -> {
                            if (parent.getStatus().checkStatusContainNotAbleToShip()) {
                                throw new BadRequestException("Код не может быть отгружен. \nКод статус " + aggregation.getStatus().getText());
                            }
                        });
                    }
                } else {
                    throw new BadRequestException("Ошибка! Код состоит в агрегации. КА: " + aggregation.getParentCode());
                }
            }
            // children count
            int quantityInUnit = getQuantityInUnit(aggregation.getCode(), aggregation.getPackageType());
            log.debug("Scanned code is KA {}", cleanCode);
            final String productId = aggregation.getProductId();
            final RedisShipmentItem shipmentItem = shipmentCodeRepository.findFirstByShipmentIdAndProductId(dto.getShipmentId(), productId).orElseGet(RedisShipmentItem::new);
            log.debug("Found shipment item KA {}", shipmentItem);
            if (shipmentItem.getId() == null) {
                final Product product = aggregation.getProduct();
                final String productName = product.getVisibleName()!=null ? product.getVisibleName() : product.getName();
                shipmentItem.setId(shipmentItemRepository.getNextSequenceValue());
                shipmentItem.setShipmentId(dto.getShipmentId());
                shipmentItem.setProductId(productId);
                shipmentItem.setProductName(productName);
                shipmentItem.setBarcode(product.getBarcode());
                shipmentItem.setParentBarcode(product.getParentBarcode());
                shipmentItem.setQty(BigDecimal.ZERO);
                if (aggregation.getSerialNum() != null) {
                    shipmentItem.setSerialId(aggregation.getSerialId());
                    shipmentItem.setSerialId(aggregation.getSerialNum().getSerialNumber());
                }
            }
            result.setUnit(aggregation.getPackageType());
            result.setShipmentItem(shipmentItem);
            result.setQuantity(quantityInUnit);
            result.setCapacity(aggregation.getQuantity());
        } else {
            final Block mark = blockRepository.findFirstByCode(cleanCode).orElseThrow(notFound("КМ не найден"));
            if (mark.getStatus().checkStatusContainNotAbleToShip()) {
                throw new BadRequestException("Код не может быть отгружен. \nКод статус " + mark.getStatus().getText());
            } else if (mark.getParentCode() != null) {
                if (EnumSet.of(ProductGroup.Beer, ProductGroup.Pharma).contains(pg)) {
                    if (shipmentMarkRepository.existsShipmentMarkByCode(mark.getParentCode())) {
                        throw new BadRequestException("Код аггрегации уже отгружен", ErrorConstants.AGGREGATION_USED_ERROR_CODE, cleanCode);
                    } else {
                        aggregationRepository.findFirstByCode(mark.getParentCode()).ifPresent(parent -> {
                            if (parent.getStatus().checkStatusContainNotAbleToShip()) {
                                throw new BadRequestException("Код не может быть отгружен. \nКод статус " + parent.getStatus().getText());
                            }
                        });
                        // checkin parent block
                        markRepository.findFirstByCode(mark.getParentCode()).ifPresent(parent -> {
                            if (parent.getStatus().checkStatusContainNotAbleToShip()) {
                                throw new BadRequestException("Код не может быть отгружен. \nКод статус " + parent.getStatus().getText());
                            }
                        });
                    }
                } else {
                    throw new BadRequestException("Ошибка! Код состоит в агрегации. КА: " + mark.getParentCode());
                }
            }
            log.debug("Scanned code is mark {}", cleanCode);
            // children count
            int quantityInUnit = getQuantityInUnit(mark.getCode(), mark.getPackageType());
            final String productId = mark.getProductId();
            final RedisShipmentItem shipmentItem = shipmentCodeRepository.findFirstByShipmentIdAndProductId(dto.getShipmentId(), productId).orElseGet(RedisShipmentItem::new);
            log.debug("Found shipment item mark {}", shipmentItem);
            if (shipmentItem.getId() == null) {
                final Product product = productRepository.findById(productId).orElse(null);
                final String productName = product != null && product.getVisibleName() != null ? product.getVisibleName() : mark.getProductName();
                final String parentBarcode = product != null && product.getParentBarcode() != null ? product.getParentBarcode() : mark.getBarcode();
                shipmentItem.setId(shipmentItemRepository.getNextSequenceValue());
                shipmentItem.setShipmentId(dto.getShipmentId());
                shipmentItem.setProductId(productId);
                shipmentItem.setProductName(productName);
                shipmentItem.setBarcode(mark.getBarcode());
                shipmentItem.setParentBarcode(parentBarcode);
                shipmentItem.setQty(BigDecimal.ZERO);
                shipmentItem.setSerialId(mark.getSerialId());
                shipmentItem.setSerialNumber(mark.getSerialNumber());
            }
            result.setUnit(mark.getPackageType());
            result.setShipmentItem(shipmentItem);
            result.setQuantity(quantityInUnit);
            result.setCapacity(mark.isBlock() ? mark.getQuantity() : 1);
        }
        if (dto.isTrueCheck()) {
            String trueToken = getCurrentOrganizationAsilBelgiToken(organization.getTin());
            if (StringUtils.isEmpty(trueToken)) {
                throw badRequest("Отсутствует токен организации").get();
            }
            final Set<String> codes = checkCodeIsCompatibleForShip(trueToken, pg, cleanCode, isMark);
            result.setCodes(codes);
        }
        return result;
    }

    public Set<String> checkCodeIsCompatibleForShip(final String trueToken, final ProductGroup pg, final String code, final boolean isMark) {
        final CisInfoResponse[] result = asilBelgisiClient.cisesInfo(trueToken, Collections.singleton(code));
        if (result != null && result.length > 0) {
            final CisInfo cisInfo = result[0].getCisInfo();
            if (pg.checkIsCanShipCodeByStatus(cisInfo.getStatus())) {
                if (isMark) {
                    return cisInfo.getChild();
                }
                return Optional.ofNullable(cisInfo.getChild()).orElseThrow(badRequest("Код аггрегации пустой!"));
            } else {
                final String errorMessage = cisInfo.getStatus() != null ? "Ошибка аггрегации! Статус КМ : " + cisInfo.getStatus().getText() : "Ошибка аггрегации! Статус КМ : NULL";
                throw new BadRequestException(errorMessage);
            }
        }
        return null;
    }

    private void saveShipmentMark(final String code, final Long shipmentItemId, final PackageType packageType) {
        final String cleanCode = clearPrintCode(code);

        final ShipmentMark mark = new ShipmentMark();
        mark.setPrintCode(code);
        mark.setCode(cleanCode);
        if (packageType != null) {
            mark.setUnit(packageType.getCode());
        } else {
            mark.setUnit(null);
        }
        mark.setShipmentItemId(shipmentItemId);
        shipmentMarkRepository.save(mark);
    }

    private void updateQuantity(final RedisShipmentItem item, final ShipmentItemWithCodes result, final boolean add) {
        final PackageType unit = result.getUnit();
        int qty = result.getQuantity();
        int capacity = result.getCapacity();

        final Map<PackageType, ShipmentCode> types = item.getTypes().stream().collect(Collectors.toMap(ShipmentCode::getUnit, Function.identity()));
        if (!types.containsKey(unit)) {
            final String unitName = unit != null ? unit.getNameRu() : PackageType.BOTTLE.getNameRu();
            ShipmentCode built = ShipmentCode.builder().count(1).capacity(capacity).unit(unit).unitName(unitName).build();
            types.put(unit, built);
        }

        final ShipmentCode type = types.get(unit);
        type.setCount(add ? type.getCount() + 1 : type.getCount() - 1);
        types.put(unit, type);
        item.setTypes(new LinkedHashSet<>(types.values()));

        if (add) {
            item.setQty(item.getQty().add(new BigDecimal(qty)));
        } else {
            item.setQty(item.getQty().subtract(new BigDecimal(qty)));
        }
    }

    private void saveMarks(final Set<String> codes, final String aggregationCode) {
        final List<Mark> markList = markRepository.findAllByCodeIn(codes);
        for (final Mark markExists : markList) {
            if (markExists.getParentCode() != null) {
                markExists.setParentCode(clearPrintCode(aggregationCode));
                markExists.setUsed(true);
                markExists.setStatus(AggregationStatus.SHIPPED);
            }
            markRepository.save(markExists);
            codes.remove(markExists.getCode());
        }
        if (CollectionUtils.isNotEmpty(codes)) {
            for (final String code : codes) {
                final Mark mark = new Mark();
                mark.setCode(code);
                mark.setPrintCode(code);
                mark.setCreatedDate(LocalDateTime.now());
                mark.setOrganizationId(getCurrentOrganizationId());
                mark.setParentCode(clearPrintCode(aggregationCode));
                mark.setUsed(true);
                mark.setStatus(AggregationStatus.SHIPPED);
                markRepository.save(mark);
            }
        }

    }

    public int getQuantityInUnit(String code, PackageType packageType) {
        int quantityInUnit = 0;
        if (PackageType.PALLET == packageType) {
            List<Aggregation> allByParentCodeBox = aggregationRepository.findAllByParentCode(code);
            if (!allByParentCodeBox.isEmpty()) {
                Set<String> boxCodes = allByParentCodeBox.stream().map(Aggregation::getCode).collect(Collectors.toSet());

                List<Mark> allByParentCodeIn = markRepository.findAllByParentCodeIn(boxCodes);
                Set<String> blockParentCodes = new HashSet<>();
                for (Mark mark : allByParentCodeIn) {
                    if (mark.isBlock()) {
                        blockParentCodes.add(mark.getCode());
                    } else {
                        quantityInUnit++;
                    }
                }
                if (!blockParentCodes.isEmpty()) {
                    quantityInUnit += getQuantityInMarkByParentCode(blockParentCodes);
                }
            } else {
                // check blocks count
                List<Mark> allByParentCodeBlock = markRepository.findAllByParentCode(code);
                Set<String> blockParentCodes = new HashSet<>();
                for (Mark mark : allByParentCodeBlock) {
                    if (mark.isBlock()) {
                        blockParentCodes.add(mark.getCode());
                    } else {
                        quantityInUnit++;
                    }
                }
                if (!allByParentCodeBlock.isEmpty()) {
                    quantityInUnit += getQuantityInMarkByParentCode(blockParentCodes);
                }
            }
            return quantityInUnit;
        }
        if (PackageType.BOX == packageType) {
            // check blocks count
            List<Mark> allByParentCodeBlock = markRepository.findAllByParentCode(code);
            Set<String> blockParentCodes = new HashSet<>();
            for (Mark mark : allByParentCodeBlock) {
                if (mark.isBlock()) {
                    blockParentCodes.add(mark.getCode());
                } else {
                    quantityInUnit++;
                }
            }
            if (!allByParentCodeBlock.isEmpty()) {
                quantityInUnit += getQuantityInMarkByParentCode(blockParentCodes);
            }

            return quantityInUnit;
        }
        if (PackageType.BLOCK == packageType) {
            // check blocks count
            List<Mark> allByParentCodeBlock = markRepository.findAllByParentCode(code);
            return allByParentCodeBlock.size();
        }
        return quantityInUnit;
    }

    public int getQuantityInMarkByParentCode(Collection<String> parentCodes) {
        List<Mark> allByParentCodeIn = markRepository.findAllByParentCodeIn(parentCodes);
        if (!allByParentCodeIn.isEmpty()) {
            return allByParentCodeIn.size();
        }
        return 0;
    }
}
