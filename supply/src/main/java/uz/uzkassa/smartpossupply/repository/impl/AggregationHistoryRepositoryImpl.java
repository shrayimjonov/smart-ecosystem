package uz.uzkassa.smartpossupply.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationHistoryListDTO;
import uz.uzkassa.smartpossupply.enums.CodeType;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.filters.ClickhouseFilter;
import uz.uzkassa.smartpossupply.mappers.clickhouse.rowmapper.ClickhouseAggregationHistoryRowMapper;
import uz.uzkassa.smartpossupply.repository.AggregationHistoryRepositoryCustom;

public class AggregationHistoryRepositoryImpl extends BaseRepositoryImpl implements AggregationHistoryRepositoryCustom {

    private final JdbcTemplate jdbcTemplate;

    public AggregationHistoryRepositoryImpl(@Qualifier("clickhouseJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Page<AggregationHistoryListDTO> getResultList(ClickhouseFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        StringBuilder sql = new StringBuilder();
        sql.append(" select ah.* ");
        sql.append(" from aggregation_history ah ");
        sql.append(" where ah.companyId=").append(filter.getCompanyId());
        if (StringUtils.isNotEmpty(filter.getType())) {
            sql.append(" and ah.codeType ='").append(CodeType.valueOf(filter.getType())).append("'");
        }
        if (StringUtils.isNotEmpty(filter.getEntityType())) {
            sql.append(" and ah.entityType ='").append(EntityTypeEnum.valueOf(filter.getEntityType())).append("'");
        }
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lowerUTF8(ah.code) ").append(filter.getSearchValueClickhouse());
            sql.append(" or lowerUTF8(ah.entityNumber) ").append(filter.getSearchValueClickhouse());
            sql.append(") ");
        }

        String countSql = sql.toString().replace("select ah.* ", "select count(ah.id)");

        sql.append(" order by ah.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());
        sql.append(" LIMIT ").append(filter.getStart()).append(", ").append(filter.getSize());
        Long total = jdbcTemplate.queryForObject(countSql, Long.class);
        return new PageImpl<>(jdbcTemplate.query(sql.toString(), new ClickhouseAggregationHistoryRowMapper.ClickhouseAggregationHistoryHeaderMapper()), filter.getSortedPageable(), total);
    }
}
