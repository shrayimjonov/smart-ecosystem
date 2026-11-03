package uz.uzkassa.silen.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
import uz.uzkassa.silen.dto.ListResult;
import uz.uzkassa.silen.dto.mq.Payload;
import uz.uzkassa.silen.dto.warehouse.CodeWithCountDTO;
import uz.uzkassa.silen.dto.warehouse.ShipmentCode;
import uz.uzkassa.silen.dto.warehouse.ShipmentItemCodeDTO;
import uz.uzkassa.silen.dto.warehouse.ShipmentItemDTO;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.enumeration.ShipmentStatus;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.exceptions.ErrorConstants;
import uz.uzkassa.silen.integration.AsilBelgisiClient;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfo;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfoResponse;
import uz.uzkassa.silen.repository.mongo.BlockRepository;
import uz.uzkassa.silen.repository.mongo.MarkRepository;
import uz.uzkassa.silen.repository.mongo.ShipmentMarkRepository;
import uz.uzkassa.silen.repository.pg.*;
import uz.uzkassa.silen.repository.redis.RedisShipmentCodeRepository;
import uz.uzkassa.silen.utils.Utils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static uz.uzkassa.silen.utils.Utils.checkIsEqualBarcodes;
import static uz.uzkassa.silen.utils.Utils.clearPrintCode;

/**
 * Service Implementation for managing {@link ShipmentItem}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShipmentItemService extends BaseService {
    AsilBelgisiClient asilBelgisiClient;
    MarkRepository markRepository;
    ProductRepository productRepository;
    SerialNumberRepository serialNumberRepository;
    ShipmentRepository shipmentRepository;
    AggregationRepository aggregationRepository;
    ShipmentItemRepository shipmentItemRepository;
    ShipmentMarkRepository shipmentMarkRepository;
    RedisShipmentCodeRepository shipmentCodeRepository;
    private final BlockRepository blockRepository;

    @Transactional
    public ShipmentItemDTO save(final ShipmentItemDTO dto) {
        final Shipment shipment = shipmentRepository.findById(dto.getShipmentId()).orElseThrow(notFound());
        if (ShipmentStatus.CLOSED.equals(shipment.getStatus())) {
            throw badRequest("Отгрузка завершена").get();
        }
        final Product product = productRepository.findById(dto.getProductId()).orElseThrow(notFound("Продукт не найден"));

        RedisShipmentItem redisShipmentItem = new RedisShipmentItem();
        redisShipmentItem.setShipmentId(dto.getShipmentId());
        redisShipmentItem.setTypes(dto.getTypes());
        redisShipmentItem.setProductId(dto.getProductId());
        redisShipmentItem.setOrderQty(dto.getOrderQty());
        redisShipmentItem.setProductId(product.getId());
        redisShipmentItem.setProductName(product.getVisibleName());
        redisShipmentItem.setBarcode(product.getBarcode());
        redisShipmentItem.setParentBarcode(product.getParentBarcode());
        redisShipmentItem.setQty(BigDecimal.ZERO);
        if (StringUtils.isNotEmpty(dto.getSerialId())) {
            SerialNumber serialNumber = serialNumberRepository.findById(dto.getSerialId()).orElseThrow(notFound());
            redisShipmentItem.setSerialId(serialNumber.getId());
            redisShipmentItem.setSerialNumber(serialNumber.getNameWithDate());
        }
        redisShipmentItem.setId(shipmentItemRepository.getNextSequenceValue());
        redisShipmentItem = shipmentCodeRepository.save(redisShipmentItem);

        shipment.setStatus(ShipmentStatus.IN_PROGRESS);
        shipmentRepository.save(shipment);

        return redisShipmentItem.toDto();
    }

    @Transactional
    public ShipmentItemDTO update(ShipmentItemDTO dto) {
        RedisShipmentItem redisShipmentItem = shipmentCodeRepository.findById(dto.getId()).orElseThrow(notFound());
        Shipment shipment = shipmentRepository.findById(dto.getShipmentId()).orElseThrow(notFound());
        if (ShipmentStatus.CLOSED.equals(shipment.getStatus())) {
            throw badRequest("Отгрузка завершена").get();
        }
        if (redisShipmentItem.getQty().compareTo(BigDecimal.ZERO) != 0 && !redisShipmentItem.getProductId().equals(dto.getProductId())) {
            throw badRequest("Продукция не может быть изменена").get();
        }
        Product product = productRepository.findById(dto.getProductId()).orElseThrow(notFound("Продукт не найден"));

        redisShipmentItem.setProductId(dto.getProductId());
        redisShipmentItem.setProductName(product.getVisibleName());
        redisShipmentItem.setBarcode(product.getBarcode());
        redisShipmentItem.setParentBarcode(product.getParentBarcode());
        redisShipmentItem.setOrderQty(dto.getOrderQty());
        redisShipmentItem.setQty(reCalculateQty2(dto.getTypes()));
        redisShipmentItem.setTypes(dto.getTypes());
        if (StringUtils.isNotEmpty(dto.getSerialId())) {
            SerialNumber serialNumber = serialNumberRepository.findById(dto.getSerialId()).orElseThrow(notFound("Серия не найден"));
            redisShipmentItem.setSerialId(serialNumber.getId());
            redisShipmentItem.setSerialNumber(serialNumber.getNameWithDate());
        } else {
            redisShipmentItem.setSerialId(null);
            redisShipmentItem.setSerialNumber(null);
        }
        redisShipmentItem = shipmentCodeRepository.save(redisShipmentItem);

        return redisShipmentItem.toDto();
    }

    /**
     * This endpoint retrieves a list of shipment items by the specified shipment ID. The response will contain details of the shipment items including their IDs, shipment numbers, status, product details, package type, quantity, order quantity, types, codes, barcode, and serial information.
     * The response will be in JSON format and will include an array of shipment item objects, each containing the mentioned details.
     * Example response:
     * <pre>
     *     [
     *     {
     *         "id": 0,
     *         "shipmentId": "",
     *         "shipmentNumber": "",
     *         "shipmentStatus": "",
     *         "shipmentStatusName": "",
     *         "productId": "",
     *         "productName": "",
     *         "packageType": "",
     *         "qty": 0,
     *         "orderQty": null,
     *         "types": [
     *             {
     *                 "unit": null,
     *                 "unitName": null,
     *                 "capacity": 0,
     *                 "count": 0
     *             }
     *         ],
     *         "codes": null,
     *         "barcode": "",
     *         "serialId": "",
     *         "serialNumber": ""
     *     }
     * ]
     * </pre>
     */
    public List<ShipmentItemDTO> findAllByShipmentId(String shipmentId) {
        return shipmentItemRepository.findAllByShipmentIdAndReturnIdIsNull(shipmentId)
                .stream().map(shipmentItem -> shipmentItem.toDto(false))
                .collect(Collectors.toList());
    }

    public BigDecimal reCalculateQty(LinkedHashSet<ShipmentCode> dtoTypes) {
        Integer qty = 0;
        Map<PackageType, ShipmentCode> types = new LinkedHashMap<>();
        for (ShipmentCode code : dtoTypes) {
            types.put(code.getUnit(), code);
        }
        if (types.get(null) != null) {
            qty += types.get(null).getCount();
        }
        if (types.get(PackageType.BLOCK) != null) {
            qty += types.get(PackageType.BLOCK).getCapacity()
                    * types.get(PackageType.BLOCK).getCount();
        }
        if (types.get(PackageType.BOX) != null) {
            Integer boxCapacity = types.get(PackageType.BOX).getCapacity();
            qty += boxCapacity * types.get(PackageType.BOX).getCount();
        }
        if (types.get(PackageType.PALLET) != null) {
            qty += types.get(PackageType.BOX).getCapacity()
                    * types.get(PackageType.PALLET).getCapacity()
                    * types.get(PackageType.PALLET).getCount();
        }
        return BigDecimal.valueOf(qty);
    }

    public BigDecimal reCalculateQty2(LinkedHashSet<ShipmentCode> dtoTypes) {
        Integer qty = 0;
        Map<PackageType, ShipmentCode> types = dtoTypes.stream().collect(Collectors.toMap(ShipmentCode::getUnit, Function.identity()));
        if (types.containsKey(null)) {
            qty += types.get(null).getCount();
        }
        if (types.containsKey(PackageType.BLOCK)) {
            qty += types.get(PackageType.BLOCK).getCapacity()
                    * types.get(PackageType.BLOCK).getCount();
        }
        if (types.containsKey(PackageType.BOX)) {
            qty += types.get(PackageType.BOX).getCapacity()
                    * types.get(PackageType.BOX).getCount();
        }
        if (types.get(PackageType.PALLET) != null) {
            Integer boxCapacity = types.containsKey(PackageType.BOX) ? types.get(PackageType.BOX).getCapacity() : 1;
            int palletCapacity = boxCapacity * types.get(PackageType.PALLET).getCapacity();
            qty += palletCapacity * types.get(PackageType.PALLET).getCount();
        }
        return BigDecimal.valueOf(qty);
    }

    //@Transactional
    public CodeWithCountDTO addMark(ShipmentItemCodeDTO dto) {
        RedisShipmentItem item = shipmentCodeRepository.findById(dto.getId()).orElseThrow(notFound());
//        validateCountLimit(item.getShipmentId());
        if (shipmentMarkRepository.findFirstByCode(dto.getCode()).isPresent()) {
            throw badRequest("Данный код уже добавлен").get();
        }

        final Organization organization = getCurrentOrganization();
        final String code = clearPrintCode(dto.getCode());
        final String aggregationCode = validateCode(dto, organization);

        if (dto.isTrueCheck()) {
            Set<String> codes = checkCodeIsCompatibleForShip(organization, aggregationCode != null ? aggregationCode : code, dto.getUnit(), dto.getBarcode());
            if (codes != null) {
                saveMarks(codes, aggregationCode != null ? aggregationCode : code, dto.getUnit());
            }
        }
        int quantityInUnit = getQuantityInUnit(code, dto.getUnit(), item.getTypes());
        saveShipmentMark(dto, dto.getUnit(), aggregationCode != null ? aggregationCode : code, quantityInUnit);

//        updateQuantity(quantityInUnit, item, dto.getUnit(), true);
//        shipmentCodeRepository.save(item);

        return new CodeWithCountDTO(dto.getCode(), quantityInUnit);
    }

    /**
     * checking limit of codes for once shipment
     *
     * @param shipmentId default = 5000
     */
    private void validateCountLimit(String shipmentId) {
        BigDecimal existsCodesCount = BigDecimal.ONE;
        List<RedisShipmentItem> list = shipmentCodeRepository.findAllByShipmentId(shipmentId);
        for (RedisShipmentItem redisShipmentItem : list) {
            for (ShipmentCode shipmentCode : redisShipmentItem.getTypes()) {
                existsCodesCount = existsCodesCount.add(BigDecimal.valueOf(shipmentCode.getCount()));
            }
        }
        log.debug("Added to shipment codes count is {}", existsCodesCount);
        if (existsCodesCount.compareTo(BigDecimal.valueOf(5000)) == 0) {
            throw new BadRequestException("В одном отгрузке не должно быть больше 5000 кодов");
        }
    }

    //    @Transactional
    public CodeWithCountDTO removeMark(ShipmentItemCodeDTO dto) {
        final String code = clearPrintCode(dto.getCode());
        final String code18 = Utils.clearAggregationCode(dto.getCode());
        ShipmentMark mark = shipmentMarkRepository.findFirstByCodeOrCode(code18, code).orElseThrow(badRequest("Этот код не отгружен"));
        if (mark.getShipmentItemId() != null && !mark.getShipmentItemId().equals(dto.getId())) {
            throw badRequest("Код не принадлежит к этой отгрузке").get();
        }

//        RedisShipmentItem item = shipmentCodeRepository.findById(dto.getId()).orElseThrow(notFound());
//        PackageType packageType = mark.getUnit() != null ? PackageType.fromCode(mark.getUnit()) : null;
//        int quantityInUnit = getQuantityInUnit(mark.getCode(), packageType);
//        log.debug("Removed shipment codes count is {}, packageType: {}, item: {}", quantityInUnit, packageType, item);
//        updateQuantity(quantityInUnit, item, packageType, false);
//        shipmentCodeRepository.save(item);
        shipmentMarkRepository.delete(mark);
        return new CodeWithCountDTO(dto.getCode(), mark.getQty());
    }

    private void updateQuantity(Integer qty, RedisShipmentItem item, PackageType unit, boolean add) {
        Map<PackageType, ShipmentCode> types = item.getTypes().stream().collect(Collectors.toMap(ShipmentCode::getUnit, Function.identity()));
        ShipmentCode type = types.get(unit);
        type.setCount(add ? type.getCount() + 1 : type.getCount() - 1);
        types.put(unit, type);
        item.setTypes(new LinkedHashSet<>(types.values()));

        if (add) {
            item.setQty(item.getQty().add(new BigDecimal(qty)));
        } else {
            item.setQty(item.getQty().subtract(new BigDecimal(qty)));
        }
    }

    public int getQuantityInUnit(final String code, final PackageType packageType, final LinkedHashSet<ShipmentCode> types) {
        log.info("Package type quantity {}", packageType);
        if (packageType == null || PackageType.BOTTLE == packageType) {
            return 1;
        }
        int quantityInUnit = 0;
        if (PackageType.PALLET == packageType) {
            List<Aggregation> allByParentCodeBox = aggregationRepository.findAllByParentCode(code);
            if (!allByParentCodeBox.isEmpty()) {
                Set<String> boxCodes = allByParentCodeBox.stream().map(Aggregation::getCode).collect(Collectors.toSet());
                List<Mark> allByParentCodeIn = markRepository.findAllByParentCodeIn(boxCodes);
                for (Mark mark : allByParentCodeIn) {
                    quantityInUnit += getQuantityInUnit(types, mark);
                }
            } else {
                // check blocks count
                List<Mark> allByParentCodeBlock = markRepository.findAllByParentCode(code);
                for (Mark mark : allByParentCodeBlock) {
                    quantityInUnit += getQuantityInUnit(types, mark);
                }
            }
            return quantityInUnit;
        }
        if (PackageType.BOX == packageType) {
            // check blocks count
            List<Mark> allByParentCodeBlock = markRepository.findAllByParentCode(code);
            for (Mark mark : allByParentCodeBlock) {
                quantityInUnit += getQuantityInUnit(types, mark);
            }
            return quantityInUnit;
        }
        if (PackageType.BLOCK == packageType) {
            // check blocks count
            Optional<Mark> firstByCode = markRepository.findFirstByCode(code);
            if (firstByCode.isPresent()) {
                quantityInUnit += getQuantityInUnit(types, firstByCode.get());
            } else {
                final Map<PackageType, Integer> packageTypeCapacityMap = types.stream()
                        .collect(Collectors.toMap(ShipmentCode::getUnit, ShipmentCode::getCapacity));
                quantityInUnit += packageTypeCapacityMap.getOrDefault(PackageType.BLOCK, 1);
            }
        }
        return quantityInUnit;
    }

    private static int getQuantityInUnit(final LinkedHashSet<ShipmentCode> types, final Mark mark) {
        if (mark.isBlock()) {
            if (mark.getQuantity() > 0) {
                return mark.getQuantity();
            } else {
                final Map<PackageType, Integer> packageTypeCapacityMap = types.stream()
                        .collect(Collectors.toMap(ShipmentCode::getUnit, ShipmentCode::getCapacity));
                return packageTypeCapacityMap.getOrDefault(PackageType.BLOCK, 1);
            }
        } else {
            return 1;
        }
    }

    public int getQuantityInMarkByParentCode(Collection<String> parentCodes) {
        List<Mark> allByParentCodeIn = markRepository.findAllByParentCodeIn(parentCodes);
        if (!allByParentCodeIn.isEmpty()) {
            return allByParentCodeIn.size();
        }
        return 0;
    }

    public String validateCode(final ShipmentItemCodeDTO dto, final Organization organization) {
        if (Arrays.asList("204559521", "306351564").contains(organization.getTin())) {
            if (!PackageType.PALLET.equals(dto.getUnit()) && !Utils.validateCode(dto.getCode(), dto.getUnit())) {
                throw badRequest("Код не соответствует стандартному формату").get();
            }
        } else if (!Utils.validateCode(dto.getCode(), dto.getUnit())) {
            throw badRequest("Код не соответствует стандартному формату").get();
        }

        final String code = clearPrintCode(dto.getCode());
        final String code18 = Utils.clearAggregationCode(dto.getCode());
        shipmentMarkRepository.findFirstByCodeOrCode(code, code18).ifPresent(mark -> {
            throw new BadRequestException(dto.getUnit() == null ? "Код маркировки уже отгружен" : "Код аггрегации уже отгружен", dto.getUnit() == null ? ErrorConstants.MARK_USED_ERROR_CODE : ErrorConstants.AGGREGATION_USED_ERROR_CODE, code);
        });

        if (dto.getUnit() != null) {
            if (dto.getUnit().isAggregation()) {
                final Optional<Aggregation> aggregation1 = aggregationRepository.findFirstByCodeOrCode(code18, code);
                if (aggregation1.isPresent()) {
                    final Aggregation aggregation = aggregation1.get();
                    if (!aggregation.getPackageType().equals(dto.getUnit())) {
                        throw badRequest(aggregation.getPackageType().getNameRu() + " не можете добавить как " + dto.getUnit().getNameRu()).get();
                    }
                    if (aggregation.getParentCode() != null) {
//                        if (EnumSet.of(ProductGroup.Beer, ProductGroup.Tobacco, ProductGroup.Appliances, ProductGroup.Pharma).contains(organization.getProductGroup())) {
                        Optional<ShipmentMark> firstByCode = shipmentMarkRepository.findFirstByCode(aggregation.getParentCode());
                        if (firstByCode.isPresent()) {
                                throw new BadRequestException(dto.getUnit() == null ? "Код маркировки уже отгружен" : "Код аггрегации уже отгружен", dto.getUnit() == null ? ErrorConstants.MARK_USED_ERROR_CODE : ErrorConstants.AGGREGATION_USED_ERROR_CODE, code);
                            } else {
                                aggregationRepository.findFirstByCode(aggregation.getParentCode()).ifPresent(parent -> {
                                    if (parent.getStatus().checkStatusContainNotAbleToShip()) {
                                        throw new BadRequestException("Код не может быть отгружен. \nКод статус " + aggregation.getStatus().getText());
                                    }
                                });
                            }
                        /*} else {
                            throw new BadRequestException("Ошибка! Код состоит в агрегации. КА: " + aggregation.getParentCode());
                        }*/
                    }
                    if (aggregation.getStatus().checkStatusContainNotAbleToShip()) {
                        throw new BadRequestException("Код не может быть отгружен. \nКод статус " + aggregation.getStatus().getText());
                    }
                    if (aggregation.getProduct() != null && checkIsEqualBarcodes(dto.getBarcode(), aggregation.getProduct().getBarcode())) {
                        throw new BadRequestException("Ошибка! Несоответствие штрихкода: \nКод: " + dto.getCode() + "\nПродукция: " + aggregation.getProduct().getName());
                    }

                    return aggregation.getCode();
                } else if (organization.isAllowForeignCodes()) {
                    return code18;
                }
            } else {
                final Optional<Block> aggregation1 = blockRepository.findFirstByCode(code);
                if (aggregation1.isPresent()) {
                    final Block aggregation = aggregation1.get();
                    if (!aggregation.getPackageType().equals(dto.getUnit())) {
                        throw badRequest(aggregation.getPackageType().getNameRu() + " не можете добавить как " + dto.getUnit().getNameRu()).get();
                    }
                    if (aggregation.getParentCode() != null) {
//                        if (EnumSet.of(ProductGroup.Beer, ProductGroup.Tobacco, ProductGroup.Appliances, ProductGroup.Pharma).contains(organization.getProductGroup())) {
                        Optional<ShipmentMark> firstByCode = shipmentMarkRepository.findFirstByCode(aggregation.getParentCode());
                        if (firstByCode.isPresent()) {
                                throw new BadRequestException(dto.getUnit() == null ? "Код маркировки уже отгружен" : "Код аггрегации уже отгружен", dto.getUnit() == null ? ErrorConstants.MARK_USED_ERROR_CODE : ErrorConstants.AGGREGATION_USED_ERROR_CODE, code);
                            } else {
                                aggregationRepository.findFirstByCode(aggregation.getParentCode()).ifPresent(parent -> {
                                    if (parent.getStatus().checkStatusContainNotAbleToShip()) {
                                        throw new BadRequestException("Код не может быть отгружен. \nКод статус " + aggregation.getStatus().getText());
                                    }
                                });
                            }
                        /*} else {
                            throw new BadRequestException("Ошибка! Код состоит в агрегации. КА: " + aggregation.getParentCode());
                        }*/
                    }
                    if (aggregation.getStatus().checkStatusContainNotAbleToShip()) {
                        throw new BadRequestException("Код не может быть отгружен. \nКод статус " + aggregation.getStatus().getText());
                    }
                    productRepository.findById(aggregation.getProductId()).ifPresent(product -> {
                        if (checkIsEqualBarcodes(product.getParentBarcode(), dto.getCode())) {
                            throw new BadRequestException("Ошибка! Несоответствие штрихкода: \nКод: " + dto.getCode() + "\nПродукция: " + aggregation.getProductName());
                        }
                    });


                    return aggregation.getCode();
                } else if (organization.isAllowForeignCodes()) {
                    return code;
                }
            }

        } else {
            if (Utils.checkIsEqualBarcodes(dto.getBarcode(), dto.getCode())) {
                throw new BadRequestException("Ошибка! Несоответствие штрихкода: \nKод:" + dto.getCode());
            }

            markRepository.findFirstByCode(code)
                    .ifPresent(markOrBlock -> {
                        if (markOrBlock.getParentCode() != null) {
//                        if (EnumSet.of(ProductGroup.Beer, ProductGroup.Pharma).contains(organization.getProductGroup())) {
                            Optional<ShipmentMark> firstByCode = shipmentMarkRepository.findFirstByCode(markOrBlock.getParentCode());
                            if (firstByCode.isPresent()) {
                                throw new BadRequestException("Код аггрегации уже отгружен", ErrorConstants.AGGREGATION_USED_ERROR_CODE, code);
                            } else {
                                if (markOrBlock.isBlock()) {
                                    aggregationRepository.findFirstByCode(markOrBlock.getParentCode()).ifPresent(parent -> {
                                        if (parent.getStatus().checkStatusContainNotAbleToShip()) {
                                            throw new BadRequestException("Код не может быть отгружен. \nКод статус " + parent.getStatus().getText());
                                        }
                                    });
                                } else {// checkin parent block
                                    markRepository.findFirstByCode(markOrBlock.getParentCode()).ifPresent(parent -> {
                                        if (parent.getStatus().checkStatusContainNotAbleToShip()) {
                                            throw new BadRequestException("Код не может быть отгружен. \nКод статус " + parent.getStatus().getText());
                                        }
                                    });
                                }
                            }
//                        } else {
//                            throw new BadRequestException("Ошибка! Код состоит в агрегации. КА: " + markOrBlock.getParentCode());
//                        }

                        } else if (markOrBlock.getStatus().checkStatusContainNotAbleToShip()) {
                            throw new BadRequestException("Код не может быть отгружен. \nКод статус " + markOrBlock.getStatus().getText());
                        }
                    });
        }
        return null;
    }

    public Set<String> checkCodeIsCompatibleForShip(final Organization organization, final String code, final PackageType unit, final String barcode) {
        final CisInfoResponse[] result = asilBelgisiClient.cisesInfo(getCurrentOrganizationAsilBelgiToken(organization.getTin()), Collections.singleton(code));
        if (result != null && result.length > 0) {
            final CisInfoResponse cisInfoResponse = result[0];
            if (cisInfoResponse.getCisInfo() == null) {
                throw new BadRequestException(cisInfoResponse.getErrorMessage());
            }
            final CisInfo cisInfo = cisInfoResponse.getCisInfo();
            if (cisInfo.getGtin() != null) {
                if (Utils.checkIsEqualBarcodes(barcode, cisInfo.getGtin())) {
                    throw new BadRequestException("Ошибка! Несоответствие штрихкода: \nKод:" + cisInfo.getRequestedCis());
                }
            }
            if (organization.getProductGroup().checkIsCanShipCodeByStatus(cisInfo.getStatus())) {
                if (unit == null) {
                    return cisInfo.getChild();
                }
                return Optional.ofNullable(cisInfo.getChild()).orElseThrow(badRequest("Код аггрегации пустой!"));
            } else {
                final String errorMessage = Optional.ofNullable(cisInfo.getStatus()).map(status -> "Ошибка аггрегации! Статус КМ : " + status.getText()).orElse(cisInfoResponse.getErrorMessage());
                throw new BadRequestException(errorMessage);
            }
        }
        throw new BadRequestException("Код не принадлежит данной организации");
    }

    private void saveShipmentMark(ShipmentItemCodeDTO dto, PackageType packageType, String code, int quantityInUnit) {
        ShipmentMark mark = new ShipmentMark();
        mark.setPrintCode(dto.getCode());
        mark.setCode(code);
        mark.setQty(quantityInUnit);
        if (packageType != null) {
            mark.setUnit(packageType.getCode());
        } else {
            mark.setUnit(null);
        }
        mark.setShipmentItemId(dto.getId());
        shipmentMarkRepository.save(mark);
    }

    private void saveMarks(Set<String> codes, String parentCode, PackageType unit) {
        if (CollectionUtils.isNotEmpty(codes)) {
            for (String code : codes) {
                if (markRepository.findFirstByCode(code).isEmpty()) {
                    Mark mark = new Mark();
                    mark.setCode(code);
                    mark.setPrintCode(code);
                    mark.setOrganizationId(getCurrentOrganizationId());
                    mark.setParentCode(clearPrintCode(parentCode));
                    mark.setUnit(unit);
                    mark.setQuantity(unit != null ? 1 : 0);
                    markRepository.save(mark);
                }
            }
        }
    }

    public ShipmentItemDTO findOne(Long id, PackageType unit) {
        return shipmentCodeRepository.findById(id).map(shipmentItem -> {
            ShipmentItemDTO dto = shipmentItem.toDto();
            List<ShipmentMark> codes;
            if (unit == null) {
                codes = shipmentMarkRepository.findAllByShipmentItemIdAndUnitIsNull(id);
            } else {
                codes = shipmentMarkRepository.findAllByShipmentItemIdAndUnit(id, unit.getCode());
            }
            dto.setCodes(codes.stream().map(ShipmentMark::getPrintCode).collect(Collectors.toSet()));
            shipmentRepository.findById(shipmentItem.getShipmentId()).ifPresent(shipment -> {
                dto.setShipmentNumber(shipment.getNumber());
                dto.setNote(shipment.getNote());
            });
            return dto;
        }).orElseThrow(badRequest("Отгрузка завершена"));
    }

    public ShipmentItemDTO getByCode(String printCode) {
        final String code = clearPrintCode(printCode);
        ShipmentMark mark = shipmentMarkRepository.findFirstByCodeAndShipmentItemIdIsNotNull(code).orElseThrow(notFound());
        return shipmentItemRepository.findById(mark.getShipmentItemId()).map(shipmentItem -> {
            ShipmentItemDTO dto = shipmentItem.toDto(false);
            dto.setCodes(shipmentMarkRepository.findAllByShipmentItemId(mark.getShipmentItemId()).stream().map(ShipmentMark::getPrintCode).collect(Collectors.toSet()));
            return dto;
        }).orElseThrow(notFound());
    }

    public ListResult<ShipmentMark> getCodes(Long id) {
        List<ShipmentMark> list = shipmentMarkRepository.findAllByShipmentItemId(id);
        return new ListResult<>(list, (long) list.size());
    }

    @Transactional
    public void delete(Long id) {
        RedisShipmentItem item = shipmentCodeRepository.findById(id)
                .orElseThrow(badRequest("Отгрузка завершена"));
        /*Shipment shipment = shipmentRepository.findById(item.getShipmentId()).orElseThrow(notFound());
        if (ShipmentStatus.CLOSED.equals(shipment.getStatus())) {
            throw badRequest("Отгрузка завершена").get();
        }*/
        List<ShipmentMark> shipmentMarkList = shipmentMarkRepository.findAllByShipmentItemIdAndUnitIsNull(id);
        shipmentMarkList.forEach(shipmentMark -> markRepository.findFirstByCode(shipmentMark.getCode())
                .ifPresent(mark -> {
                    if (mark.getParentCode() == null) {
                        mark.setStatus(AggregationStatus.DRAFT);
                        mark.setUsed(false);
                    } else {
                        mark.setStatus(AggregationStatus.CREATED);
                    }
                    markRepository.save(mark);
                }));
        shipmentMarkRepository.deleteAllByShipmentItemId(id);
        shipmentCodeRepository.delete(item);
    }


    /**
     * Get serial number's aggregations and add they are to shipment item by unit type
     */
    public void fillShipmentItem(Long id) {
        RedisShipmentItem redisShipmentItem = shipmentCodeRepository.findById(id).orElseThrow(notFound(""));
        Payload payload = new Payload(redisShipmentItem.getId());
        rabbitMqProducer.sendFillShipmentItem(payload);
    }

    public void fillWithBox(Long id) {
        RedisShipmentItem redisShipmentItem = shipmentCodeRepository.findById(id).orElseThrow(notFound());
        if (redisShipmentItem.getSerialId() == null) return;

        Map<PackageType, ShipmentCode> types = redisShipmentItem.getTypes().stream()
                .collect(Collectors.toMap(ShipmentCode::getUnit, Function.identity()));
        // collect only aggregations like BOX, PALLET
        final List<Aggregation> aggregationsBySerial = aggregationRepository.findAllBySerialIdAndStatusAndDeletedIsFalse(redisShipmentItem.getSerialId(), AggregationStatus.CREATED);
        if (CollectionUtils.isEmpty(aggregationsBySerial)) return;

        List<ShipmentMark> batchInsert = new LinkedList<>();
        for (Aggregation aggregation : aggregationsBySerial) {
            PackageType packageType = aggregation.getPackageType();
            ShipmentMark shipmentMark = new ShipmentMark();
            shipmentMark.setPrintCode(aggregation.getPrintCode());
            shipmentMark.setCode(aggregation.getCode());
            shipmentMark.setUnit(aggregation.getUnit());
            shipmentMark.setShipmentItemId(id);

            if (types.containsKey(packageType)) {
                Integer count = types.get(packageType).getCount();
                types.get(packageType).setCount(count + 1);
            } else {
                ShipmentCode code = new ShipmentCode();
                code.setCount(1);
                code.setUnit(packageType);
                code.setCapacity(aggregation.getQuantity());
                code.setUnitName(packageType.getNameRu());
                types.put(packageType, code);
            }
            batchInsert.add(shipmentMark);
            if (batchInsert.size() > 50) {
                shipmentMarkRepository.saveAll(batchInsert);
                batchInsert.clear();
            }
        }
        if (CollectionUtils.isNotEmpty(batchInsert)) {
            shipmentMarkRepository.saveAll(batchInsert);
            batchInsert.clear();
        }
        if (types.containsKey(PackageType.BLOCK)) {
            int qty = types.get(PackageType.BLOCK).getCount() * types.get(PackageType.BLOCK).getCapacity();
            BigDecimal add = redisShipmentItem.getQty().add(BigDecimal.valueOf(qty));
            redisShipmentItem.setQty(add);
        }
        if (types.containsKey(PackageType.BOX)) {
            int qty = types.get(PackageType.BOX).getCount() * types.get(PackageType.BOX).getCapacity();
            BigDecimal add = redisShipmentItem.getQty().add(BigDecimal.valueOf(qty));
            redisShipmentItem.setQty(add);
        }
        if (types.containsKey(PackageType.PALLET)) {
            int childCapacity = 1;
            if (types.containsKey(PackageType.BOX)) {
                childCapacity = types.containsKey(PackageType.BOX) ? types.get(PackageType.BOX).getCapacity() : 1;
            } else if (types.containsKey(PackageType.BLOCK)) {
                childCapacity = types.containsKey(PackageType.BLOCK) ? types.get(PackageType.BLOCK).getCapacity() : 1;
            }
            int qty = types.get(PackageType.PALLET).getCount() * types.get(PackageType.PALLET).getCapacity() * childCapacity;
            BigDecimal add = redisShipmentItem.getQty().add(BigDecimal.valueOf(qty));
            redisShipmentItem.setQty(add);
        }
        shipmentCodeRepository.save(redisShipmentItem);
    }
}
