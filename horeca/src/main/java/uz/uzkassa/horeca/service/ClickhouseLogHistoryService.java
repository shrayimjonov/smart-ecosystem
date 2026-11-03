package uz.uzkassa.horeca.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uz.uzkassa.horeca.dto.clickhouse.LogHistoryClickhouseDTO;
import uz.uzkassa.horeca.dto.loghistory.LogHistoryListDTO;
import uz.uzkassa.horeca.dto.payload.ClickhouseLogHistoryPayload;
import uz.uzkassa.horeca.filters.ClickhouseFilter;
import uz.uzkassa.horeca.repository.LogHistoryRepository;
import uz.uzkassa.horeca.utils.DateUtils;

@Service
@Slf4j

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ClickhouseLogHistoryService extends BaseService {

    LogHistoryRepository logHistoryRepository;

    @Autowired
    @Qualifier(value = "clickhouseJdbcTemplate")
    JdbcTemplate jdbcTemplate;

    public void addLogHistory(ClickhouseLogHistoryPayload payload) {
        log.info(
                "Writing log history started. \nfieldName: {} \nold Value: {}\nnew value: {}",
                payload.getField(),
                payload.getOldValue(),
                payload.getNewValue()
        );
        LogHistoryClickhouseDTO logHistory = new LogHistoryClickhouseDTO();
        logHistory.setId(UUID.randomUUID().toString());
        logHistory.setField(localizationService.localize(payload.getField(), payload.getField()));
        logHistory.setEntityType(payload.getEntityType());
        logHistory.setEntityId(payload.getEntityId());
        logHistory.setUpdaterId(payload.getUpdaterId());
        logHistory.setCompanyId(payload.getCompanyId());
        if (StringUtils.isNotEmpty(payload.getUpdaterLogin())) {
            userRepository
                    .findFirstByLoginAndDeletedIsFalse(payload.getUpdaterLogin())
                    .ifPresent(updater -> {
                        logHistory.setUpdaterName(updater.getName());
                        logHistory.setUpdaterId(updater.getId());
                        if (updater.getCompanyId() != null) {
                            logHistory.setCompanyId(updater.getCompany().getId());
                            logHistory.setCompanyName(updater.getCompany().getName());
                            logHistory.setCompanyTin(updater.getCompany().getTin());
                        }
                    });
        }
        logHistory.setUpdatedDate(LocalDateTime.now().format(DateUtils.dateTimeHyphenFormatter));
        if (payload.getOldValue() instanceof String || payload.getOldValue() instanceof Double) {
            logHistory.setFromStringValue(String.valueOf(payload.getOldValue()));
        } else if (payload.getOldValue() instanceof Number) {
            logHistory.setFromNumberValue((BigDecimal) payload.getOldValue());
        } else if (payload.getOldValue() instanceof LocalDateTime) {
            logHistory.setFromDateValue(((LocalDateTime) payload.getOldValue()).format(DateUtils.dateTimeHyphenFormatter));
        } else if (payload.getOldValue() instanceof LocalDate) {
            logHistory.setFromDateValue(((LocalDate) payload.getOldValue()).format(DateUtils.yearMonthDayHyphenFormatter));
        } else if (payload.getOldValue() instanceof Boolean) {
            logHistory.setFromStringValue((Boolean) payload.getOldValue() ? "Yes" : "No");
        }
        if (payload.getNewValue() instanceof String || payload.getNewValue() instanceof Double) {
            logHistory.setToStringValue(String.valueOf(payload.getNewValue()));
        } else if (payload.getNewValue() instanceof Number) {
            logHistory.setToNumberValue((BigDecimal) payload.getNewValue());
        } else if (payload.getNewValue() instanceof LocalDateTime) {
            logHistory.setToDateValue(((LocalDateTime) payload.getNewValue()).format(DateUtils.dateTimeHyphenFormatter));
        } else if (payload.getNewValue() instanceof LocalDate) {
            logHistory.setToDateValue(((LocalDate) payload.getNewValue()).format(DateUtils.yearMonthDayHyphenFormatter));
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
            log.info("insert to clickhouse: {}", logHistory);
            jdbcTemplate.update(query.toString());
        } catch (DataAccessException e) {
            log.error("Clickhouse Log History insert database error: {}", query);
            log.error(e.getMessage(), e);
        }
    }


    public Page<LogHistoryListDTO> getList(ClickhouseFilter filter) {
        return logHistoryRepository.getList(filter);
    }
}
