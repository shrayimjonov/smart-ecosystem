package uz.uzkassa.smartpossupply.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.dto.clickhouse.LogHistoryClickhouseDTO;
import uz.uzkassa.smartpossupply.dto.loghistory.LogHistoryListDTO;
import uz.uzkassa.smartpossupply.dto.payload.ClickhouseLogHistoryPayload;
import uz.uzkassa.smartpossupply.filters.ClickhouseFilter;
import uz.uzkassa.smartpossupply.repository.clickhouse.ClickhouseLogHistoryRepository;
import uz.uzkassa.smartpossupply.utils.DateUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.07.2023
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ClickhouseLogHistoryService extends BaseService {

    ClickhouseLogHistoryRepository clickhouseLogHistoryRepository;
    JdbcTemplate clickhouseJdbcTemplate;

    public ClickhouseLogHistoryService(ClickhouseLogHistoryRepository clickhouseLogHistoryRepository, @Qualifier("clickhouseJdbcTemplate") JdbcTemplate clickhouseJdbcTemplate) {
        this.clickhouseLogHistoryRepository = clickhouseLogHistoryRepository;
        this.clickhouseJdbcTemplate = clickhouseJdbcTemplate;
    }


    @Transactional
    public void addLogHistory(ClickhouseLogHistoryPayload payload) {
        LogHistoryClickhouseDTO logHistory = new LogHistoryClickhouseDTO();
        logHistory.setId(UUID.randomUUID().toString());
        logHistory.setField(payload.getField());
        logHistory.setEntityType(payload.getEntityType());
        logHistory.setEntityId(payload.getEntityId());
        logHistory.setUpdaterId(payload.getUpdaterId());
        logHistory.setCompanyId(payload.getCompanyId());
        logHistory.setBranchId(payload.getBranchId());
        if (StringUtils.isNotEmpty(payload.getUpdaterLogin())) {
            userRepository.findFirstByLogin(payload.getUpdaterLogin())
                .ifPresent(updater -> {
                    logHistory.setUpdaterName(updater.getFullName());
                    logHistory.setUpdaterId(updater.getId());
                    if (updater.getCompany() != null) {
                        logHistory.setCompanyId(updater.getCompany().getId());
                        logHistory.setCompanyName(updater.getCompany().getName());
                        logHistory.setCompanyTin(updater.getCompany().getTin());
                    }
                });
        }
        logHistory.setUpdatedDate(LocalDateTime.now().format(DateUtils.DATE_TIME_HYPHEN_FORMATTER));
        if (payload.getOldValue() instanceof String || payload.getOldValue() instanceof Double) {
            logHistory.setFromStringValue(String.valueOf(payload.getOldValue()));
        } else if (payload.getOldValue() instanceof Number) {
            if (payload.getOldValue() instanceof Integer) {
                logHistory.setFromNumberValue(BigDecimal.valueOf((Integer) payload.getOldValue()));
            } else {
                logHistory.setFromNumberValue((BigDecimal) payload.getOldValue());
            }
        } else if (payload.getOldValue() instanceof LocalDateTime) {
            logHistory.setFromDateValue(((LocalDateTime) payload.getOldValue()).format(DateUtils.DATE_TIME_HYPHEN_FORMATTER));
        } else if (payload.getOldValue() instanceof LocalDate) {
            logHistory.setFromDateValue(((LocalDate) payload.getOldValue()).format(DateUtils.YEAR_MONTH_DAY_HYPHEN_FORMATTER));
        } else if (payload.getOldValue() instanceof Boolean) {
            logHistory.setFromStringValue((Boolean) payload.getOldValue() ? "Yes" : "No");
        }
        if (payload.getNewValue() instanceof String || payload.getNewValue() instanceof Double) {
            logHistory.setToStringValue(String.valueOf(payload.getNewValue()));
        } else if (payload.getNewValue() instanceof Number) {
            if (payload.getNewValue() instanceof Integer) {
                logHistory.setToNumberValue(BigDecimal.valueOf((Integer) payload.getNewValue()));
            } else {
                logHistory.setToNumberValue((BigDecimal) payload.getNewValue());
            }
        } else if (payload.getNewValue() instanceof LocalDateTime) {
            logHistory.setToDateValue(((LocalDateTime) payload.getNewValue()).format(DateUtils.DATE_TIME_HYPHEN_FORMATTER));
        } else if (payload.getNewValue() instanceof LocalDate) {
            logHistory.setToDateValue(((LocalDate) payload.getNewValue()).format(DateUtils.YEAR_MONTH_DAY_HYPHEN_FORMATTER));
        } else if (payload.getNewValue() instanceof Boolean) {
            logHistory.setToStringValue((Boolean) payload.getNewValue() ? "Yes" : "No");
        }
        StringBuilder query = new StringBuilder("INSERT INTO log_history FORMAT JSONEachRow ");
        try {
            query.append(objectMapper.writeValueAsString(logHistory));
        } catch (JsonProcessingException e) {
            log.error("Clickhouse Log History mapping error: {} ", logHistory);
            log.error(e.getMessage(), e);
        }
        try {
            clickhouseJdbcTemplate.update(query.toString());
        } catch (DataAccessException e) {
            log.error("Clickhouse Log History insert database error: {}", query);
            log.error(e.getMessage(), e);
        }
    }

    public Page<LogHistoryListDTO> getList(ClickhouseFilter filter) {
        return clickhouseLogHistoryRepository.getList(filter);
    }
}
