package uz.uzkassa.silen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.Aggregation;
import uz.uzkassa.silen.domain.ShipmentItem;
import uz.uzkassa.silen.domain.StoreInItem;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.domain.mongo.ShipmentMark;
import uz.uzkassa.silen.domain.mongo.StoreInMark;
import uz.uzkassa.silen.dto.StoreInMarkDTO;
import uz.uzkassa.silen.dto.mq.Payload;
import uz.uzkassa.silen.dto.mq.WarehousePayload;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.enumeration.WarehouseOperation;
import uz.uzkassa.silen.kafka.KafkaConstants;
import uz.uzkassa.silen.kafka.producer.KafkaProducer;
import uz.uzkassa.silen.repository.mongo.MarkRepository;
import uz.uzkassa.silen.repository.mongo.ShipmentMarkRepository;
import uz.uzkassa.silen.repository.mongo.StoreInMarkRepository;
import uz.uzkassa.silen.repository.pg.AggregationRepository;
import uz.uzkassa.silen.repository.pg.ShipmentItemRepository;
import uz.uzkassa.silen.repository.pg.StoreInItemRepository;
import uz.uzkassa.silen.utils.Utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreInItemMarkService extends BaseService {
    private final StoreInItemRepository storeInItemRepository;
    private final StoreInMarkRepository storeInMarkRepository;
    private final MarkRepository markRepository;
    private final AggregationRepository aggregationRepository;
    private final ShipmentMarkRepository shipmentMarkRepository;
    private final ShipmentItemRepository shipmentItemRepository;
    private final KafkaProducer<WarehousePayload> kafkaProducer;

    private ShipmentMark checkCodeOrParentIsShipped(String codeOrParent, String parentCode) {
        Optional<ShipmentMark> shipmentMarkOptional = shipmentMarkRepository.findFirstByCode(codeOrParent);
        if (shipmentMarkOptional.isPresent()) {
            return shipmentMarkOptional.get();
        } else {
            if (!StringUtils.isEmpty(parentCode)) {
                return checkCodeOrParentIsShipped(parentCode, null);
            } else {
                throw badRequest("Код не отгружен").get();
            }
        }
    }

    public String create(StoreInMarkDTO storeInMarkDTO) {
        final String code = Utils.clearPrintCode(storeInMarkDTO.getPrintCode());
        if (Utils.isAggregation(storeInMarkDTO.getPrintCode())) {
            final String code18 = Utils.clearAggregationCode(storeInMarkDTO.getPrintCode());
            final Aggregation aggregation = aggregationRepository.findFirstByCodeOrCode(code18, code).orElseThrow(notFound("КА не найден"));
            final ShipmentMark shipmentMark = checkCodeOrParentIsShipped(aggregation.getCode(), aggregation.getParentCode());
            final ShipmentItem shipmentItem = shipmentItemRepository.findById(shipmentMark.getShipmentItemId()).orElseThrow(notFound("Продукт не найден"));

            StoreInItem storeInItem = findOrElseGet(storeInMarkDTO.getStoreInId(), shipmentItem.getProductId(), shipmentItem.getSerialId());
            if (storeInItem.getId() != null) {
                // check code is already added
                Set<String> codes = new HashSet<>();
                codes.add(aggregation.getCode());
                if (aggregation.getParentCode() != null) codes.add(aggregation.getParentCode());

                List<Mark> childrenMarks = markRepository.findAllByParentCode(aggregation.getCode());
                if (!CollectionUtils.isEmpty(childrenMarks)) {
                    childrenMarks.forEach(child -> codes.add(child.getCode()));
                }
                /*List<Aggregation> childrenAggregations = aggregationRepository.findAllByParentCode(aggregation.getCode());
                if (!CollectionUtils.isEmpty(childrenAggregations)) {
                    childrenAggregations.forEach(child -> codes.add(child.getCode()));
                }*/
                List<StoreInMark> itemIdAndCode = storeInMarkRepository.findAllByItemIdAndCodeIn(storeInItem.getId(), new HashSet<>(codes));

                if (!CollectionUtils.isEmpty(itemIdAndCode)) {
                    List<String> addedCodes = itemIdAndCode.stream().map(StoreInMark::getCode).collect(Collectors.toList());
                    if (addedCodes.contains(code)) {
                        throw badRequest("Код уже добавлен " + code).get();
                    }
                    if (aggregation.getParentCode() != null && addedCodes.contains(aggregation.getParentCode())) {
                        throw badRequest("Верхних упаковка уже деобавлено " + aggregation.getParentCode()).get();
                    }
                    if (!CollectionUtils.isEmpty(childrenMarks) && !CollectionUtils.isEmpty(addedCodes)) {
                        StringBuilder stringBuilder = new StringBuilder("Коды уже добавлен [\n");
                        for (String addedCode : addedCodes) {
                            stringBuilder.append(addedCode).append("\n");
                        }
                        stringBuilder.append("]");
                        throw badRequest(stringBuilder.toString()).get();
                    }
                }
            }
            if (PackageType.PALLET.equals(aggregation.getPackageType())) {
                Long amount = aggregationRepository.countByParentCode(aggregation.getCode());
                storeInItem.setQty(storeInItem.getQty() + amount.intValue());
            } else {
                storeInItem.setQty(storeInItem.getQty() + aggregation.getQuantity());
            }
            storeInItemRepository.save(storeInItem);

            StoreInMark storeInMark = new StoreInMark();
            storeInMark.setItemId(storeInItem.getId());
            storeInMark.setCode(aggregation.getCode());
            storeInMark.setPrintCode(aggregation.getPrintCode());
            storeInMark.setUnit(aggregation.getUnit());
            storeInMarkRepository.save(storeInMark);
            return storeInMark.getId();
        } else {
            final Mark mark = markRepository.findFirstByCode(code).orElseThrow(notFound("КM не найден"));
            final ShipmentMark shipmentMark = checkCodeOrParentIsShipped(code, mark.getParentCode());
            final ShipmentItem shipmentItem = shipmentItemRepository.findById(shipmentMark.getShipmentItemId()).orElseThrow(notFound("Продукт не найден"));

            StoreInItem storeInItem = findOrElseGet(storeInMarkDTO.getStoreInId(), shipmentItem.getProductId(), shipmentItem.getSerialId());
            if (storeInItem.getId() != null) {
                // check code is already added
                Set<String> codes = new HashSet<>();
                codes.add(code);
                if (mark.getParentCode() != null) codes.add(mark.getParentCode());
                List<Mark> childrenMarks = new ArrayList<>();
                if (mark.isBlock()) {
                    childrenMarks.addAll(markRepository.findAllByParentCode(mark.getCode()));
                    if (!CollectionUtils.isEmpty(childrenMarks)) {
                        childrenMarks.forEach(child -> codes.add(child.getCode()));
                    }
                }

                List<StoreInMark> itemIdAndCode = storeInMarkRepository.findAllByItemIdAndCodeIn(storeInItem.getId(), new HashSet<>(codes));
                if (!CollectionUtils.isEmpty(itemIdAndCode)) {
                    List<String> addedCodes = itemIdAndCode.stream().map(StoreInMark::getCode).collect(Collectors.toList());
                    if (addedCodes.contains(code)) {
                        throw badRequest("Код уже добавлен " + code).get();
                    }
                    if (mark.getParentCode() != null && addedCodes.contains(mark.getParentCode())) {
                        throw badRequest("Верхних упаковка уже деобавлено " + mark.getParentCode()).get();
                    }
                    if (!CollectionUtils.isEmpty(childrenMarks) && !CollectionUtils.isEmpty(addedCodes)) {
                        StringBuilder stringBuilder = new StringBuilder("Коды уже добавлен [\n");
                        for (String addedCode : addedCodes) {
                            stringBuilder.append(addedCode).append("\n");
                        }
                        stringBuilder.append("]");
                        throw badRequest(stringBuilder.toString()).get();
                    }
                }
            }
            if (mark.isBlock()) {
                List<Mark> children = markRepository.findAllByParentCode(mark.getCode());
                storeInItem.setQty(storeInItem.getQty() + children.size());
            } else {
                storeInItem.setQty(storeInItem.getQty() + 1/*mark.getQuantity()*/);
            }
            storeInItemRepository.save(storeInItem);

            StoreInMark storeInMark = new StoreInMark();
            storeInMark.setItemId(storeInItem.getId());
            storeInMark.setCode(mark.getCode());
            storeInMark.setPrintCode(mark.getPrintCode());
            storeInMark.setUnit(mark.getPackageType().getCode());
            storeInMarkRepository.save(storeInMark);
            return storeInMark.getId();
        }
    }

    public void delete(String id) {
        StoreInMark storeInMark = storeInMarkRepository.findById(id).orElseThrow(notFound("Код не найден"));
        StoreInItem storeInItem = storeInItemRepository.findById(storeInMark.getItemId()).orElseThrow(notFound("Продукт не найден"));
        if (storeInMark.getPackageType().isMark()) {
            Optional<Mark> firstByCode = markRepository.findFirstByCode(storeInMark.getCode());
            firstByCode.ifPresent(mark -> storeInItem.setQty(storeInItem.getQty() - mark.getQuantity()));
        } else {
            Optional<Aggregation> aggregationOptional = aggregationRepository.findFirstByCode(storeInMark.getCode());
            aggregationOptional.ifPresent(aggregation -> {
                if (PackageType.PALLET.equals(aggregation.getPackageType())) {
                    Long amount = aggregationRepository.countByParentCode(aggregation.getCode());
                    storeInItem.setQty(storeInItem.getQty() - amount.intValue());
                } else {
                    storeInItem.setQty(storeInItem.getQty() - aggregation.getQuantity());
                }
            });
        }
        storeInMarkRepository.deleteById(id);
        if (storeInItem.getQty() == 0) {
            storeInItemRepository.delete(storeInItem);
        } else {
            storeInItemRepository.save(storeInItem);
        }
    }

    @Transactional
    public void marksToStore(Payload payload) {
        final String organizationId = payload.getExtraId();
        List<StoreInItem> allByStoreInId = storeInItemRepository.findAllByStoreInId(payload.getId());
        for (StoreInItem storeInItem : allByStoreInId) {
            Set<StoreInMark> allByItemId = storeInMarkRepository.findAllByItemId(storeInItem.getId());

            // collect marks collection
            Set<String> markCodes = new HashSet<>();
            // collect aggregations collection
            Set<String> aggregationCodes = new HashSet<>();

            for (StoreInMark storeInMark : allByItemId) {
                if (PackageType.fromCode(storeInMark.getUnit()).isAggregation()) {
                    aggregationCodes.addAll(Utils.clearAggregationPrintCode(storeInMark.getCode()));
                } else {
                    markCodes.add(storeInMark.getCode());
                }
            }
            final LocalDateTime localDateTime = LocalDateTime.now();
            if (CollectionUtils.isNotEmpty(markCodes)) {
                List<Mark> markList = markRepository.findAllByCodeIn(markCodes);
                for (Mark mark : markList) {
                    if (!StringUtils.isEmpty(mark.getParentCode())) {
                        aggregationRepository.decrementQuantity(mark.getParentCode(), mark.getQuantity());
                    }
                    mark.setUsed(false);
                    mark.setParentCode(null);
                    mark.setStatus(AggregationStatus.RETURNED);
                    markRepository.save(mark);
                    // remove from shipment
                    shipmentMarkRepository.deleteByCode(mark.getCode());
                    // send to warehouse
                    WarehousePayload warehousePayload = new WarehousePayload();
                    warehousePayload.setOrganizationId(organizationId);
                    warehousePayload.setOperation(WarehouseOperation.Returned);
                    warehousePayload.setOperationDate(localDateTime);
                    warehousePayload.setNote(WarehouseOperation.Returned.getNameRu());
                    warehousePayload.setUnit(mark.getPackageType());
                    warehousePayload.setAlcoholCount(BigDecimal.ONE);
                    warehousePayload.setProductId(storeInItem.getProductId());
                    warehousePayload.setLogin(storeInItem.getCreatedBy());
                    warehousePayload.setCode(mark.getCode());
                    kafkaProducer.sendMessage(KafkaConstants.WAREHOUSE_TOPIC,warehousePayload);
                }
            }

            if (CollectionUtils.isNotEmpty(aggregationCodes)) {
                List<Aggregation> aggregationList = aggregationRepository.findAllByCodeIn(aggregationCodes);
                for (Aggregation aggregation : aggregationList) {
                    if (!StringUtils.isEmpty(aggregation.getParentCode())) {
                        aggregationRepository.decrementQuantity(aggregation.getParentCode(), aggregation.getQuantity());
                    }
                    aggregation.setStatus(AggregationStatus.RETURNED);
                    aggregation.setParentCode(null);
                    aggregationRepository.save(aggregation);
                    // remove from shipment
                    shipmentMarkRepository.deleteByCode(aggregation.getCode());
                    // send to warehouse
                    WarehousePayload warehousePayload = new WarehousePayload();
                    warehousePayload.setOrganizationId(organizationId);
                    warehousePayload.setOperation(WarehouseOperation.Returned);
                    warehousePayload.setOperationDate(localDateTime);
                    warehousePayload.setNote(WarehouseOperation.Returned.getNameRu());
                    warehousePayload.setUnit(aggregation.getPackageType());
                    warehousePayload.setAlcoholCount(BigDecimal.valueOf(aggregation.getQuantity()));
                    warehousePayload.setProductId(storeInItem.getProductId());
                    warehousePayload.setLogin(storeInItem.getCreatedBy());
                    warehousePayload.setCode(aggregation.getCode());
                    kafkaProducer.sendMessage(KafkaConstants.WAREHOUSE_TOPIC, warehousePayload);
                }
            }
        }

    }

    private StoreInItem findOrElseGet(String storeInId, String productId, String serialId) {
        return storeInItemRepository.findFirstByStoreInIdAndProductIdAndSerialId(storeInId, productId, serialId).orElseGet(() -> {
            // create store in item
            StoreInItem item = new StoreInItem();
            item.setStoreInId(storeInId);
            item.setProductId(productId);
            item.setSerialId(serialId);
            item.setQty(0);
            return item;
        });
    }
}
