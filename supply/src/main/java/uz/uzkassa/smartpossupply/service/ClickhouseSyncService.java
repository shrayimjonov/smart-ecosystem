package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.dto.payload.ClickhouseSyncPayload;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ClickhouseSyncService extends BaseService {

    ClickhouseAggregationCodeHistoryService clickhouseAggregationCodeHistoryService;

    @Transactional
    public void sync(ClickhouseSyncPayload payload) {
        EntityTypeEnum entityType = EntityTypeEnum.valueOf(payload.getEntityType());
        switch (entityType) {
            case STOCK_PRODUCT_AGGREGATION_HISTORY, STOCK_PRODUCT_AGGREGATION_BLOCK_HISTORY, STOCK_PRODUCT_MARK_HISTORY:
                clickhouseAggregationCodeHistoryService.sync(payload);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + entityType);
        }
    }
}
