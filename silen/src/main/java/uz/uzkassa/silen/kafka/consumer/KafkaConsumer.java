package uz.uzkassa.silen.kafka.consumer;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import uz.uzkassa.silen.dto.mq.WarehousePayload;
import uz.uzkassa.silen.kafka.KafkaConstants;
import uz.uzkassa.silen.service.AggregationHistoryService;
import uz.uzkassa.silen.service.WarehouseService;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaConsumer {
    WarehouseService warehouseService;
    AggregationHistoryService aggregationHistoryService;

    @KafkaListener(id = KafkaConstants.WAREHOUSE_QUEUE, topics = KafkaConstants.WAREHOUSE_TOPIC)
    public void warehouseQueue(WarehousePayload payload) {
        try {
            log.debug(payload.toString());
            warehouseService.calculate(payload);
        } catch (Exception e) {
            log.error("Warehouse queue error payload {}", payload.toString(), e);
        }
    }

    @KafkaListener(id = KafkaConstants.WAREHOUSE_AGGREGATION_HISTORY_QUEUE, topics = KafkaConstants.WAREHOUSE_TOPIC)
    public void createHistoryFromWarehousePayload(WarehousePayload payload) {
        try {
            log.debug(payload.toString());
            aggregationHistoryService.create(payload);
        } catch (Exception e) {
            log.error("Create AggregationHistory from Warehouse route error {}", payload.toString(), e);
        }
    }

}
