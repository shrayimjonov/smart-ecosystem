package uz.uzkassa.smartpossupply.crons;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.dto.payload.ClickhouseSyncPayload;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqClickhouseSyncProducer;
import uz.uzkassa.smartpossupply.repository.StockProductAggregationBlockRepository;
import uz.uzkassa.smartpossupply.repository.StockProductAggregationRepository;
import uz.uzkassa.smartpossupply.repository.StockProductMarkRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@Component
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Profile("prod")
public class ClickhouseCronJob {

    StockProductAggregationRepository stockProductAggregationRepository;
    StockProductAggregationBlockRepository stockProductAggregationBlockRepository;
    StockProductMarkRepository stockProductMarkRepository;
    RabbitMqClickhouseSyncProducer rabbitMqClickhouseSyncProducer;

    //Fire at every day at 04:01 am
    @Scheduled(cron = "0 1 4 * * * ")
    public void syncAggregationCodeHistory() {
        List<Long> stockProductAggregationIds = stockProductAggregationRepository.getIdsToSyncClickhouseAggregationCodeHistory(StockStatus.COMPLETED);
        if (!CollectionUtils.isEmpty(stockProductAggregationIds)) {
            log.info("Sync aggregation codes in size:{}", stockProductAggregationIds.size());
            Lists.partition(stockProductAggregationIds, 1000)
                .forEach(ids -> rabbitMqClickhouseSyncProducer.sync(new ClickhouseSyncPayload(ids, EntityTypeEnum.STOCK_PRODUCT_AGGREGATION_HISTORY.getCode())));
        }
    }

    //Fire at every day at 04:30 am
    @Scheduled(cron = "0 30 4 * * * ")
    public void syncAggregationBlockCodeHistory() {
        List<Long> stockProductAggregationBlockIds = stockProductAggregationBlockRepository.getIdsToSyncClickhouseAggregationBlockCodeHistory(StockStatus.COMPLETED);
        if (!CollectionUtils.isEmpty(stockProductAggregationBlockIds)) {
            log.info("Sync aggregation block codes in size:{}", stockProductAggregationBlockIds.size());
            Lists.partition(stockProductAggregationBlockIds, 1000)
                .forEach(ids -> rabbitMqClickhouseSyncProducer.sync(new ClickhouseSyncPayload(ids, EntityTypeEnum.STOCK_PRODUCT_AGGREGATION_BLOCK_HISTORY.getCode())));
        }
    }

    //Fire at every day at 05:01 am
    @Scheduled(cron = "0 1 5 * * * ")
    public void syncMarkCodeHistory() {
        List<Long> stockProductMarkIds = stockProductMarkRepository.getIdsToSyncClickhouseMarkCodeHistory(StockStatus.COMPLETED);
        if (!CollectionUtils.isEmpty(stockProductMarkIds)) {
            log.info("Sync mark codes in size:{}", stockProductMarkIds.size());
            Lists.partition(stockProductMarkIds, 1000)
                .forEach(ids -> rabbitMqClickhouseSyncProducer.sync(new ClickhouseSyncPayload(ids, EntityTypeEnum.STOCK_PRODUCT_MARK_HISTORY.getCode())));
        }
    }

}
