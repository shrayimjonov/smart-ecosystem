package uz.uzkassa.horeca.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import uz.uzkassa.horeca.dto.loghistory.LogHistoryListDTO;
import uz.uzkassa.horeca.filters.ClickhouseFilter;
import uz.uzkassa.horeca.mappers.clickhouse.ClickhouseLogHistoryRowMapper;
import uz.uzkassa.horeca.utils.DateUtils;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.util.List;
import java.util.Optional;

@Component
public class LogHistoryRepository {

    @Qualifier("clickhouseJdbcTemplate")
    JdbcTemplate jdbcTemplate;

    public LogHistoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Page<LogHistoryListDTO> getList(ClickhouseFilter filter) {
        filter.setCompanyId(Optional.ofNullable(filter.getCompanyId()).orElseGet(HeaderUtils::getCurrentCompanyId));
        StringBuilder sql = new StringBuilder();
        sql.append(" select lh.* ");
        sql.append(" from log_history lh ");
        sql.append(" where 1=1");
        if (filter.getCompanyId() != null) {
            sql.append(" and lh.companyId=").append(filter.getCompanyId());
        }
        if (filter.getEntityId() != null) {
            sql.append(" and lh.entityId=").append(filter.getEntityId());
        }
        if (filter.getEntityType() != null) {
            sql.append(" and lh.entityType='").append(filter.getEntityType()).append("'");
        }
        if (filter.getFrom() != null) {
            sql.append(" and lh.updatedDate>='").append(DateUtils.dateTimeHyphenFormatter.format(filter.getFrom())).append("'");
        }
        if (filter.getTo() != null) {
            sql.append(" and lh.updatedDate<='").append(DateUtils.dateTimeHyphenFormatter.format(filter.getTo())).append("'");
        }
        if (filter.hasSearch()) {
            sql.append(" and (");
            sql.append(" lowerUTF8(lh.field) ").append(filter.getSearchValueClickhouse());
            sql.append(" or lowerUTF8(lh.updaterName) ").append(filter.getSearchValueClickhouse());
            sql.append(") ");
        }
        String countSql = sql.toString().replace(" select lh.* ", "select count()");
        Long total = jdbcTemplate.queryForObject(countSql, Long.class);

        sql.append(" order by lh.updatedDate");
        sql.append(" ").append(filter.getSortOrder().getName());
        sql.append(" LIMIT ").append(filter.getStart()).append(", ").append(filter.getSize());
        List<LogHistoryListDTO> logHistoryList = jdbcTemplate.query(
            sql.toString(),
            new ClickhouseLogHistoryRowMapper.ClickhouseLogHistoryHeaderMapper()
        );
        return new PageImpl<>(logHistoryList, filter.getPageable(), total);
    }
}
