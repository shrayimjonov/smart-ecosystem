package uz.uzkassa.cts.repository.clickhouse;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.changelog.ChangeLogClickhouseDTO;
import uz.uzkassa.cts.dto.changelog.ChangeLogListDTO;
import uz.uzkassa.cts.dto.payload.ChangeLogPayload;
import uz.uzkassa.cts.filter.ClickhouseFilter;
import uz.uzkassa.cts.mappings.clickhouse.rowmapper.ClickhouseChangeLogRowMapper;
import uz.uzkassa.cts.repository.UserRepository;
import uz.uzkassa.cts.utils.DateUtil;
import uz.uzkassa.cts.utils.QueryUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ClickhouseChangeLogRepository extends ClickhouseBaseRepository {

    UserRepository userRepository;

    @Override
    public String getTableName() {
        return "change_log";
    }

    public Page<ChangeLogListDTO> getList(ClickhouseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select chl.* from ").append(getTableName()).append(" chl ");
        sql.append(" where 1=1 ");
        if (filter.getEntityId() != null) {
            sql.append(" and chl.entityId =").append(filter.getEntityId());
        } else if (CollectionUtils.isNotEmpty(filter.getEntityList())) {
            sql.append(" and chl.entityId in (").append(QueryUtils.getSqlLongFormat(filter.getEntityList())).append(")\n");
        }
        if (filter.getEntityType() != null) {
            sql.append(" and chl.entityType ='").append(filter.getEntityType().name()).append("'");
        }
        if (filter.getFrom() != null) {
            sql.append(" and chl.updatedDate >='").append(DateUtil.LONG_FORMATTER.format(filter.getFromStartOfDay())).append("'");
        }
        if (filter.getTo() != null) {
            sql.append(" and chl.updatedDate <='").append(DateUtil.LONG_FORMATTER.format(filter.getToEndOfDay())).append("'");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lowerUTF8(chl.field) ").append(filter.getSearchValueClickhouse());
            sql.append(" or lowerUTF8(chl.updaterName) ").append(filter.getSearchValueClickhouse());
            sql.append(") ");
        }
        String countSql = sql.toString().replace(" select chl.* ", "select count()");
        Long total = jdbcTemplate.queryForObject(countSql, Long.class);

        sql.append(" order by chl.updatedDate ");
        sql.append(" ").append(filter.getSortOrder().getName());
        sql.append(" LIMIT ").append(filter.getLimit());
        List<ChangeLogListDTO> logHistoryList = jdbcTemplate.query(sql.toString(), new ClickhouseChangeLogRowMapper.ClickhouseChangeLogMapper());
        return new PageImpl<>(logHistoryList, filter.getPageable(), total);
    }

    public void create(ChangeLogPayload payload) {
        ChangeLogClickhouseDTO changeHistory = new ChangeLogClickhouseDTO();
        changeHistory.setId(UUID.randomUUID());
        changeHistory.setEntityId(payload.getEntityId());
        changeHistory.setEntityName(payload.getEntityName());
        changeHistory.setEntityType(payload.getEntityType().name());
        changeHistory.setField(payload.getField());
        changeHistory.setOldValue(payload.getOldValue());
        changeHistory.setNewValue(payload.getNewValue());
        changeHistory.setUpdaterId(payload.getUpdaterId());
        if (payload.getUpdatedDate() != null) {
            changeHistory.setUpdatedDate(payload.getUpdatedDate().format(DateUtil.LONG_FORMATTER));
        }
        if (payload.getUpdaterId() != null && StringUtils.isEmpty(payload.getUpdaterName())) {
            userRepository.findById(payload.getUpdaterId()).ifPresent(user -> changeHistory.setUpdaterName(user.getFullName()));
        } else {
            changeHistory.setUpdaterName(Optional.ofNullable(payload.getUpdaterName()).orElse("System"));
        }
        try {
            this.insert(objectMapper.writeValueAsString(changeHistory));
        } catch (JsonProcessingException | DataAccessException e) {
            log.error(e.getMessage(), e);
        }
    }
}
