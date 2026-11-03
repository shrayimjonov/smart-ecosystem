package uz.uzkassa.silen.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.Aggregation;
import uz.uzkassa.silen.domain.ShipmentItem;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.domain.mongo.ShipmentMark;
import uz.uzkassa.silen.dto.mq.WarehousePayload;
import uz.uzkassa.silen.dto.warehouse.ShipmentCode;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.enumeration.WarehouseOperation;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.kafka.KafkaConstants;
import uz.uzkassa.silen.kafka.producer.KafkaProducer;
import uz.uzkassa.silen.repository.mongo.MarkRepository;
import uz.uzkassa.silen.repository.mongo.ShipmentMarkRepository;
import uz.uzkassa.silen.repository.pg.AggregationRepository;
import uz.uzkassa.silen.repository.pg.ShipmentItemRepository;
import uz.uzkassa.silen.utils.Utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RefundService extends BaseService {
    MarkRepository markRepository;
    AggregationRepository aggregationRepository;
    ShipmentMarkRepository shipmentMarkRepository;
    ShipmentItemRepository shipmentItemRepository;
    KafkaProducer<WarehousePayload> kafkaProducer;

    @Transactional
    public String save(String printCode) {
        if (!Utils.validatePrintCode(printCode)) {
            throw new BadRequestException("Код не соответствует стандартному формату", printCode);
        }

        final String code = Utils.clearPrintCode(printCode);
        final String code18 = Utils.clearAggregationCode(printCode);
        ShipmentMark shipmentMark = shipmentMarkRepository.findFirstByCodeOrCode(code, code18).orElseThrow(badRequest("Данный код не отгружен"));
        ShipmentItem shipmentItem = shipmentItemRepository.findById(shipmentMark.getShipmentItemId()).orElseThrow(() -> new BadRequestException("Отгрузга ещё не завершён"));

        if (Utils.isAggregation(printCode)) {
            refundByAggregation(code, shipmentItem, shipmentMark);
        } else {
            refundByMark(code, shipmentItem, shipmentMark);
        }
        return code;
    }

    @Transactional
    public void refundByAggregation(String code, ShipmentItem shipmentItem, ShipmentMark shipmentMark) {
        final String code18 = Utils.clearAggregationCode(code);
        final String code20 = Utils.clearPrintCode(code);

        Optional<Aggregation> firstByCodeOrCode = aggregationRepository.findFirstByCodeOrCode(code18, code20);
        if (firstByCodeOrCode.isPresent()) {
            Aggregation aggregation = firstByCodeOrCode.get();
            // create returned history
            aggregation.setStatus(AggregationStatus.RETURNED);
            aggregationRepository.saveAndFlush(aggregation);


            List<Aggregation> allByParentCode = aggregationRepository.findAllByParentCode(aggregation.getCode());
            for (Aggregation child : allByParentCode) {
                // create returned history
                child.setStatus(AggregationStatus.RETURNED);
                aggregationRepository.saveAndFlush(child);

                // add to warehouse like returned
                WarehousePayload warehousePayload = new WarehousePayload();
                warehousePayload.setOperation(WarehouseOperation.Returned);
                warehousePayload.setNote("Возвращен");
                warehousePayload.setUnit(PackageType.fromCode(aggregation.getUnit()));
                warehousePayload.setAlcoholCount(BigDecimal.valueOf(child.getQuantity()));
                warehousePayload.setOperationDate(LocalDateTime.now());
                warehousePayload.setOrganizationId(child.getOrganizationId());
                warehousePayload.setProductId(child.getProductId());
                warehousePayload.setCode(child.getCode());
                kafkaProducer.sendMessage(KafkaConstants.WAREHOUSE_TOPIC, warehousePayload);

            }
            //remove from shipmentItem
            for (ShipmentCode shipmentCode : shipmentItem.getTypes()) {
                if (shipmentCode.getUnit() == PackageType.fromCode(shipmentMark.getUnit())) {
                    shipmentCode.setCount(shipmentCode.getCount() - 1);
                    shipmentItem.setQty(shipmentItem.getQty().subtract(BigDecimal.valueOf(shipmentCode.getCapacity())));
                }
            }
            shipmentItemRepository.save(shipmentItem);
            shipmentMarkRepository.deleteByCode(shipmentMark.getCode());

            // add to warehouse like returned
            WarehousePayload payload = new WarehousePayload();
            payload.setOperation(WarehouseOperation.Returned);
            payload.setNote("Возвращен");
            payload.setUnit(PackageType.fromCode(aggregation.getUnit()));
            payload.setAlcoholCount(BigDecimal.valueOf(aggregation.getQuantity()));
            payload.setOperationDate(LocalDateTime.now());
            payload.setOrganizationId(aggregation.getOrganizationId());
            payload.setProductId(aggregation.getProductId());
            payload.setCode(aggregation.getCode());
            kafkaProducer.sendMessage(KafkaConstants.WAREHOUSE_TOPIC, payload);
        }
    }

    @Transactional
    public void refundByMark(String code, ShipmentItem shipmentItem, ShipmentMark shipmentMark) {
        Optional<Mark> block = markRepository.findFirstByCode(code);
        if (block.isPresent()) {
            Mark mark = block.get();
            if (mark.getParentCode() != null) {
                throw new BadRequestException("КМ состоит в агрегации. Для возврата отсканируйте его код агрегации " + block.get().getParentCode());
            }
            mark.setUsed(false);
            mark.setStatus(AggregationStatus.RETURNED);
            markRepository.save(mark);

            // remove from shipmentItem
            for (ShipmentCode shipmentCode : shipmentItem.getTypes()) {
                if (shipmentCode.getUnit() == null) {
                    shipmentCode.setCount(shipmentCode.getCount() - 1);
                    shipmentItem.setQty(shipmentItem.getQty().subtract(BigDecimal.valueOf(shipmentCode.getCapacity())));
                    log.debug("ShipmentItem {} successfully changed to: {} qty: {}", shipmentItem.getId(), shipmentCode.getCount(), shipmentItem.getQty());
                }
            }
            shipmentItemRepository.save(shipmentItem);
            // remove from shipMark
            shipmentMarkRepository.delete(shipmentMark);

            // add to warehouse like returned
            WarehousePayload payload = new WarehousePayload();
            payload.setOrganizationId(shipmentItem.getShipment().getOrganizationId());
            payload.setOperationDate(LocalDateTime.now());
            payload.setOperation(WarehouseOperation.Returned);
            payload.setNote("Возвращен");
            payload.setUnit(mark.getUnit());
            payload.setAlcoholCount(BigDecimal.valueOf(mark.getQuantity()));
            payload.setProductId(shipmentItem.getProductId());
            payload.setCode(code);
            kafkaProducer.sendMessage(KafkaConstants.WAREHOUSE_TOPIC, payload);
        }


    }
}
