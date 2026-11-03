package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.dto.payload.StockOptimizationPayload;
import uz.uzkassa.smartpossupply.enums.CodeType;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqStockOptimizationProducer;
import uz.uzkassa.smartpossupply.repository.StockProductAggregationBlockRepository;
import uz.uzkassa.smartpossupply.repository.StockProductAggregationRepository;
import uz.uzkassa.smartpossupply.repository.StockProductMarkRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
//@Transactional
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class StockOptimizationService extends BaseService {


    StockProductAggregationRepository stockProductAggregationRepository;
    StockProductAggregationBlockRepository stockProductAggregationBlockRepository;
    StockProductMarkRepository stockProductMarkRepository;
    RabbitMqStockOptimizationProducer rabbitMqStockOptimizationProducer;

    public void deleteOldRows() {
        stockProductMarkRepository.findFirstByDeletedIsTrueOrderByCreatedDateAsc().ifPresent(stockProductMark -> rabbitMqStockOptimizationProducer.delete(new StockOptimizationPayload(CodeType.MARK, stockProductMark.getCreatedDate(), LocalDateTime.now().minusDays(DURATION_OF_DELETED_CODES_SAVING_MEASURED_IN_DAYS))));
        stockProductAggregationBlockRepository.findFirstByDeletedIsTrueOrderByCreatedDateAsc().ifPresent(stockProductAggregationBlock -> rabbitMqStockOptimizationProducer.delete(new StockOptimizationPayload(CodeType.BLOCK, stockProductAggregationBlock.getCreatedDate(), LocalDateTime.now().minusDays(DURATION_OF_DELETED_CODES_SAVING_MEASURED_IN_DAYS))));
        stockProductAggregationRepository.findFirstByDeletedIsTrueOrderByCreatedDateAsc().ifPresent(stockProductAggregation -> rabbitMqStockOptimizationProducer.delete(new StockOptimizationPayload(CodeType.AGGREGATION, stockProductAggregation.getCreatedDate(), LocalDateTime.now().minusDays(DURATION_OF_DELETED_CODES_SAVING_MEASURED_IN_DAYS))));
    }

    public void deleteCodes(StockOptimizationPayload payload) {
        switch (payload.getCodeType()) {
            case MARK -> deleteStockProductMark(payload);
            case BLOCK -> deleteStockProductAggregationBlock(payload);
            case AGGREGATION -> deleteStockProductAggregation(payload);
        }
    }


    public void deleteStockProductMark(StockOptimizationPayload payload) {
        List<LocalDateTime[]> dateRanges = this.getMonthlyRanges(payload.getFromDate(), payload.getToDate());
        for (LocalDateTime[] dateRange : dateRanges) {
            rabbitMqStockOptimizationProducer.deleteItems(new StockOptimizationPayload(CodeType.MARK, dateRange[0], dateRange[1]));
        }
    }

    public void deleteMarks(StockOptimizationPayload payload) {
        stockProductMarkRepository.deleteOldRows(payload.getFromDate(), payload.getToDate(), MAX_ROW_COUNT_TO_DELETE_IN_PER_QUERY);
        stockProductAggregationRepository.flush();
    }


    public void deleteStockProductAggregationBlock(StockOptimizationPayload payload) {
        List<LocalDateTime[]> dateRanges = this.getMonthlyRanges(payload.getFromDate(), payload.getToDate());
        for (LocalDateTime[] dateRange : dateRanges) {
            rabbitMqStockOptimizationProducer.deleteItems(new StockOptimizationPayload(CodeType.BLOCK, dateRange[0], dateRange[1]));
        }
    }

    public void deleteBlocks(StockOptimizationPayload payload) {
        log.info("Calling stored procedure : fromDate={}, toDate={}, batchSize={}", payload.getFromDate(), payload.getToDate(), MAX_ROW_COUNT_TO_DELETE_IN_PER_QUERY);
        stockProductAggregationBlockRepository.deleteOldRows(payload.getFromDate(), payload.getToDate(), MAX_ROW_COUNT_TO_DELETE_IN_PER_QUERY);
        stockProductAggregationRepository.flush();
    }


    public void deleteStockProductAggregation(StockOptimizationPayload payload) {
        List<LocalDateTime[]> dateRanges = this.getMonthlyRanges(payload.getFromDate(), payload.getToDate());
        for (LocalDateTime[] dateRange : dateRanges) {
            rabbitMqStockOptimizationProducer.deleteItems(new StockOptimizationPayload(CodeType.AGGREGATION, dateRange[0], dateRange[1]));
        }
    }

    public void deleteAggregations(StockOptimizationPayload payload) {
        log.info("Calling stored procedure : fromDate={}, toDate={}, batchSize={}", payload.getFromDate(), payload.getToDate(), MAX_ROW_COUNT_TO_DELETE_IN_PER_QUERY);
        stockProductAggregationRepository.deleteOldRows(payload.getFromDate(), payload.getToDate(), MAX_ROW_COUNT_TO_DELETE_IN_PER_QUERY);
        stockProductAggregationRepository.flush();
    }


    public void deleteItems(StockOptimizationPayload payload) {
        switch (payload.getCodeType()) {
            case MARK -> deleteMarks(payload);
            case BLOCK -> deleteBlocks(payload);
            case AGGREGATION -> deleteAggregations(payload);
            default -> {
            }
        }
    }

    public List<LocalDateTime[]> getMonthlyRanges(LocalDateTime fromDate, LocalDateTime toDate) {
        List<LocalDateTime[]> ranges = new ArrayList<>();
        if (fromDate.isAfter(toDate)) {
            LocalDateTime temp = fromDate;
            fromDate = toDate;
            toDate = temp;
        }
        LocalDateTime currentFrom = fromDate;
        while (!currentFrom.isAfter(toDate)) {
            LocalDateTime currentTo = currentFrom.plusDays(1);
            if (currentTo.isAfter(toDate)) {
                currentTo = toDate.plusSeconds(1);
            }
            ranges.add(new LocalDateTime[]{currentFrom, currentTo});
            currentFrom = currentTo;
        }
        return ranges;
    }
}