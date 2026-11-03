package uz.uzkassa.smartpossupply.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.dto.clickhouse.StockProductAggregationHistoryClickhouseDTO;
import uz.uzkassa.smartpossupply.dto.payload.ClickhouseSyncPayload;
import uz.uzkassa.smartpossupply.enums.CodeType;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.mappers.clickhouse.StockProductAggregationBlockCodeClickhouseMapper;
import uz.uzkassa.smartpossupply.mappers.clickhouse.StockProductAggregationCodeClickhouseMapper;
import uz.uzkassa.smartpossupply.mappers.clickhouse.StockProductMarkCodeClickhouseMapper;
import uz.uzkassa.smartpossupply.repository.StockProductAggregationBlockRepository;
import uz.uzkassa.smartpossupply.repository.StockProductAggregationRepository;
import uz.uzkassa.smartpossupply.repository.StockProductMarkRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;


/**
 * Every day at night, codes are synchronized to the Clickhouse database. Only completed stock operation codes are synchronized.
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Slf4j
public class ClickhouseAggregationCodeHistoryService extends BaseService {

    StockProductAggregationRepository stockProductAggregationRepository;
    StockProductAggregationBlockRepository stockProductAggregationBlockRepository;
    StockProductMarkRepository stockProductMarkRepository;
    StockProductAggregationCodeClickhouseMapper stockProductAggregationCodeClickhouseMapper;
    StockProductAggregationBlockCodeClickhouseMapper stockProductAggregationBlockCodeClickhouseMapper;
    StockProductMarkCodeClickhouseMapper stockProductMarkCodeClickhouseMapper;
    JdbcTemplate clickhouseJdbcTemplate;

    @Autowired
    public ClickhouseAggregationCodeHistoryService(StockProductAggregationRepository stockProductAggregationRepository, StockProductAggregationBlockRepository stockProductAggregationBlockRepository, StockProductMarkRepository stockProductMarkRepository, StockProductAggregationCodeClickhouseMapper stockProductAggregationCodeClickhouseMapper, StockProductAggregationBlockCodeClickhouseMapper stockProductAggregationBlockCodeClickhouseMapper, StockProductMarkCodeClickhouseMapper stockProductMarkCodeClickhouseMapper, @Qualifier("clickhouseJdbcTemplate") JdbcTemplate clickhouseJdbcTemplate) {
        this.stockProductAggregationRepository = stockProductAggregationRepository;
        this.stockProductAggregationBlockRepository = stockProductAggregationBlockRepository;
        this.stockProductMarkRepository = stockProductMarkRepository;
        this.stockProductAggregationCodeClickhouseMapper = stockProductAggregationCodeClickhouseMapper;
        this.stockProductAggregationBlockCodeClickhouseMapper = stockProductAggregationBlockCodeClickhouseMapper;
        this.stockProductMarkCodeClickhouseMapper = stockProductMarkCodeClickhouseMapper;
        this.clickhouseJdbcTemplate = clickhouseJdbcTemplate;
    }

    @Transactional
    public void sync(ClickhouseSyncPayload payload) {
        StringBuilder clickhouseQuery = new StringBuilder("INSERT INTO aggregation_history FORMAT JSONEachRow ");
        EntityTypeEnum entityType = EntityTypeEnum.valueOf(payload.getEntityType());
        Set<Long> errorIds = new HashSet<>();
        switch (entityType) {
            case STOCK_PRODUCT_AGGREGATION_HISTORY:
                stockProductAggregationRepository.findAllById(payload.getIds()).forEach(stockProductAggregation -> {
                    StockProductAggregationHistoryClickhouseDTO clickHouseStockProductAggregationCodeDTO = stockProductAggregationCodeClickhouseMapper.toDTO(stockProductAggregation);
                    clickHouseStockProductAggregationCodeDTO.setEntityId(stockProductAggregation.getStockProduct().getStockId());
                    clickHouseStockProductAggregationCodeDTO.setEntityType(stockProductAggregation.getStockProduct().getStock().getEntityType().getCode());
                    clickHouseStockProductAggregationCodeDTO.setEntityNumber(stockProductAggregation.getStockProduct().getStock().getStockNumber());
                    clickHouseStockProductAggregationCodeDTO.setTransferType(stockProductAggregation.getStockProduct().getStock().getTransferType().name());
                    clickHouseStockProductAggregationCodeDTO.setCodeType(CodeType.AGGREGATION.name());
                    try {
                        clickhouseQuery.append(objectMapper.writeValueAsString(clickHouseStockProductAggregationCodeDTO));
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage(), e);
                        errorIds.add(stockProductAggregation.getId());
                    }
                });
                try {
                    clickhouseJdbcTemplate.update(clickhouseQuery.toString());
                    if (!errorIds.isEmpty()) {
                        List<Long> ids = payload.getIds();
                        ids.removeAll(errorIds);
                        stockProductAggregationRepository.updateClickhouseHistorySync(ids);
                    } else {
                        stockProductAggregationRepository.updateClickhouseHistorySync(payload.getIds());
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                break;
            case STOCK_PRODUCT_AGGREGATION_BLOCK_HISTORY:
                stockProductAggregationBlockRepository.findAllById(payload.getIds()).forEach(stockProductAggregationBlock -> {
                    StockProductAggregationHistoryClickhouseDTO clickHouseStockProductAggregationCodeDTO = stockProductAggregationBlockCodeClickhouseMapper.toDTO(stockProductAggregationBlock);
                    clickHouseStockProductAggregationCodeDTO.setEntityId(stockProductAggregationBlock.getStockProduct().getStockId());
                    clickHouseStockProductAggregationCodeDTO.setEntityType(stockProductAggregationBlock.getStockProduct().getStock().getEntityType().getCode());
                    clickHouseStockProductAggregationCodeDTO.setEntityNumber(stockProductAggregationBlock.getStockProduct().getStock().getStockNumber());
                    clickHouseStockProductAggregationCodeDTO.setTransferType(stockProductAggregationBlock.getStockProduct().getStock().getTransferType().name());
                    clickHouseStockProductAggregationCodeDTO.setCodeType(CodeType.BLOCK.name());
                    try {
                        clickhouseQuery.append(objectMapper.writeValueAsString(clickHouseStockProductAggregationCodeDTO));
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage(), e);
                        errorIds.add(stockProductAggregationBlock.getId());
                    }
                });
                try {
                    clickhouseJdbcTemplate.update(clickhouseQuery.toString());
                    if (!errorIds.isEmpty()) {
                        List<Long> ids = payload.getIds();
                        ids.removeAll(errorIds);
                        stockProductAggregationBlockRepository.updateClickhouseHistorySync(ids);
                    } else {
                        stockProductAggregationBlockRepository.updateClickhouseHistorySync(payload.getIds());
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                break;
            case STOCK_PRODUCT_MARK_HISTORY:
                stockProductMarkRepository.findAllById(payload.getIds()).forEach(stockProductMark -> {
                    StockProductAggregationHistoryClickhouseDTO clickHouseStockProductAggregationCodeDTO = stockProductMarkCodeClickhouseMapper.toDTO(stockProductMark);
                    clickHouseStockProductAggregationCodeDTO.setEntityId(stockProductMark.getStockProduct().getStockId());
                    clickHouseStockProductAggregationCodeDTO.setEntityType(stockProductMark.getStockProduct().getStock().getEntityType().getCode());
                    clickHouseStockProductAggregationCodeDTO.setEntityNumber(stockProductMark.getStockProduct().getStock().getStockNumber());
                    clickHouseStockProductAggregationCodeDTO.setTransferType(stockProductMark.getStockProduct().getStock().getTransferType().name());
                    clickHouseStockProductAggregationCodeDTO.setCodeType(CodeType.MARK.name());
                    try {
                        clickhouseQuery.append(objectMapper.writeValueAsString(clickHouseStockProductAggregationCodeDTO));
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage(), e);
                        errorIds.add(stockProductMark.getId());
                    }
                });

                try {
                    clickhouseJdbcTemplate.update(clickhouseQuery.toString());
                    if (!errorIds.isEmpty()) {
                        List<Long> ids = payload.getIds();
                        ids.removeAll(errorIds);
                        stockProductMarkRepository.updateClickhouseHistorySync(ids);
                    } else {
                        stockProductMarkRepository.updateClickhouseHistorySync(payload.getIds());
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                break;
        }
    }
}
