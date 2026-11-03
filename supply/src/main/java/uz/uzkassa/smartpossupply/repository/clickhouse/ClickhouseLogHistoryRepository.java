package uz.uzkassa.smartpossupply.repository.clickhouse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.loghistory.LogHistoryListDTO;
import uz.uzkassa.smartpossupply.filters.ClickhouseFilter;
import uz.uzkassa.smartpossupply.mappers.clickhouse.rowmapper.ClickhouseLogHistoryRowMapper;
import uz.uzkassa.smartpossupply.utils.DateUtils;

import java.util.List;

/**
 * Powered by Shuxratjon Rayimjonov
 * Date: 16.06.2023
 */
@Component
public class ClickhouseLogHistoryRepository extends ClickhouseBaseRepository {

    public Page<LogHistoryListDTO> getList(ClickhouseFilter filter) {

        filter.setCompanyId(getCompanyId(filter, false));
        StringBuilder sql = new StringBuilder();
        sql.append(" select lh.* ");
        sql.append(" from log_history lh ");
        sql.append(" where 1=1");
        if (filter.getCompanyId() != null) {
            sql.append(" and lh.companyId=").append(filter.getCompanyId());
        }
        if (filter.getBranchId() != null) {
            sql.append(" and lh.branchId=").append(filter.getBranchId());
        }
        if (filter.getCreatorId() != null) {
            sql.append(" and lh.updaterId=").append(filter.getCreatorId());
        }
        if (filter.getEntityId() != null) {
            sql.append(" and lh.entityId=").append(filter.getEntityId());
        }
        if (filter.getTransferType() != null) {
            sql.append(" and lh.entityType='").append(filter.getTransferType()).append("'");
        }
        if (filter.getFrom() != null) {
            sql.append(" and lh.updatedDate>='").append(DateUtils.DATE_TIME_HYPHEN_FORMATTER.format(filter.getFrom())).append("'");
        }
        if (filter.getTo() != null) {
            sql.append(" and lh.updatedDate<='").append(DateUtils.DATE_TIME_HYPHEN_FORMATTER.format(filter.getTo())).append("'");
        }
        if (filter.isNotEmpty()) {
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
        List<LogHistoryListDTO> logHistoryList = jdbcTemplate.query(sql.toString(), new ClickhouseLogHistoryRowMapper.ClickhouseLogHistoryHeaderMapper());
        return new PageImpl<>(logHistoryList, filter.getPageable(), total);
    }

    @Override
    public String getTableName() {
        return "log_history";
    }
}
